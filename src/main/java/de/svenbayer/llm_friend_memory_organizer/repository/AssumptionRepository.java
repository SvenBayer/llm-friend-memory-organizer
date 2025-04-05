package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.AssumptionEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AssumptionRepository extends Neo4jRepository<AssumptionEntity, String> {

    @Query("SHOW INDEXES YIELD name WHERE name = 'assumption_index'")
    List<String> findIndex();

    default void createIndexIfNotExists() {
        if (findIndex().isEmpty()) {
            createAssumptionEmbeddingIndexIfNotExists();
        }
    }

    @Query("""
    CREATE VECTOR INDEX assumption_index IF NOT EXISTS
    FOR (n:Assumption)
    ON (n.embedding)
    OPTIONS {
        indexConfig: {
            `vector.dimensions`: 768,
            `vector.similarity_function`: 'cosine'
        }
    }
    """)
    void createAssumptionEmbeddingIndexIfNotExists();

    /**
     * Example: custom query to call the vector similarity procedure.
     * We pass the embedding (double[]) and how many "topK" matches we want.
     */
    @Query("""
    CALL db.index.vector.queryNodes(
        'assumption_index',
        toInteger($topK),
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<AssumptionEntity> findSimilarAssumptions(Integer topK, float[] embedding);

    @Query("""
    CALL db.index.vector.queryNodes(
        'assumption_index',
        1,
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    WHERE score > 0.90
    RETURN node
    ORDER BY score ASC
    LIMIT 1
    """)
    AssumptionEntity findSameAssumption(float[] embedding);

    @Query("""
    MATCH (a:Assumption)-[]->(tt:TopTopic)
    WHERE tt.topicName IN $topTopicNames
    RETURN DISTINCT a
    """)
    List<AssumptionEntity> findAssumptionsThatHaveTopTopics(Set<String> topTopicNames);

    @Query("""
    MATCH (a:Assumption)-[]->(tt:TopTopic)-[]->(t:Topic)-[]->(m:Memory)
    WHERE m.embeddingText IN $memoryIds
    RETURN DISTINCT a
    """)
    List<AssumptionEntity> findAssumptionsThatHaveMemories(Set<String> memoryIds);
}

