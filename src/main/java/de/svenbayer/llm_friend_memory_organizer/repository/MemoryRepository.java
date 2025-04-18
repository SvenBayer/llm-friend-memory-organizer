package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MemoryRepository extends Neo4jRepository<MemoryEntity, String> {

    @Query("SHOW INDEXES YIELD name WHERE name = 'memory_index'")
    List<String> findIndex();

    default void createIndexIfNotExists() {
        if (findIndex().isEmpty()) {
            createMemoryEmbeddingIndexIfNotExists();
        }
    }

    @Query("""
    CREATE VECTOR INDEX memory_index IF NOT EXISTS
    FOR (n:Memory)
    ON (n.embedding)
    OPTIONS {
        indexConfig: {
            `vector.dimensions`: 768,
            `vector.similarity_function`: 'cosine'
        }
    }
    """)
    void createMemoryEmbeddingIndexIfNotExists();

    /**
     * Example: custom query to call the vector similarity procedure.
     * We pass the embedding (double[]) and how many "topK" matches we want.
     */
    @Query("""
    CALL db.index.vector.queryNodes(
        'memory_index',
        toInteger($topK),
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    WHERE 0.99 > score > 0.75
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<MemoryEntity> findSimilarMemories(Integer topK, float[] embedding);

    @Query("""
    CALL db.index.vector.queryNodes(
        'memory_index',
        1,
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    WHERE score > 0.98
    RETURN node
    ORDER BY score ASC
    LIMIT 1
    """)
    MemoryEntity findSameMemory(float[] embedding);

    @Query("""
    MATCH (m1:Memory)<-[]-()-[]->(m2:Memory)
    WHERE m1.embeddingText = $embeddingText
    RETURN DISTINCT(m2)
    """)
    List<MemoryEntity> findIndirectRelatedMemories(String embeddingText);

    @Query("""
    MATCH (tt:TopTopic)-[]->(t:Topic)-[]->(m:Memory)
    WHERE tt.topicName = $topTopicName
    RETURN DISTINCT(m)
    """)
    List<MemoryEntity> findMemoriesThatHaveTopTopic(String topTopicName);
}

