package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopTopicRepository extends Neo4jRepository<TopTopicEntity, String> {

    @Query("SHOW INDEXES YIELD name WHERE name = 'toptopic_index'")
    List<String> findIndex();

    default void createIndexIfNotExists() {
        if (findIndex().isEmpty()) {
            createTopTopicEmbeddingIndexIfNotExists();
        }
    }

    @Query("""
    CREATE VECTOR INDEX toptopic_index IF NOT EXISTS
    FOR (n:TopTopic)
    ON (n.embedding)
    OPTIONS {
        indexConfig: {
            `vector.dimensions`: 768,
            `vector.similarity_function`: 'cosine'
        }
    }
    """)
    void createTopTopicEmbeddingIndexIfNotExists();

    /**
     * Example: custom query to call the vector similarity procedure.
     * We pass the embedding (double[]) and how many "topK" matches we want.
     */
    @Query("""
    CALL db.index.vector.queryNodes(
        'toptopic_index',
        toInteger($topK),
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<TopTopicEntity> findSimilarTopTopics(Integer topK, float[] embedding);

    @Query("""
    CALL db.index.vector.queryNodes(
        'toptopic_index',
        1,
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    WHERE score > 0.90
    RETURN node
    ORDER BY score ASC
    LIMIT 1
    """)
    TopTopicEntity findSameTopTopic(float[] embedding);
}

