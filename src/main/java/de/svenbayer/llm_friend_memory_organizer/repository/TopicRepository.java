package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends Neo4jRepository<TopicEntity, String> {

    @Query("SHOW INDEXES YIELD name WHERE name = 'topic_index'")
    List<String> findIndex();

    default void createIndexIfNotExists() {
        if (findIndex().isEmpty()) {
            createTopicEmbeddingIndexIfNotExists();
        }
    }

    @Query("""
    CREATE VECTOR INDEX topic_index IF NOT EXISTS
    FOR (n:Topic)
    ON (n.embedding)
    OPTIONS {
        indexConfig: {
            `vector.dimensions`: 768,
            `vector.similarity_function`: 'cosine'
        }
    }
    """)
    void createTopicEmbeddingIndexIfNotExists();

    /**
     * Example: custom query to call the vector similarity procedure.
     * We pass the embedding (double[]) and how many "topK" matches we want.
     */
    @Query("""
    CALL db.index.vector.queryNodes(
        'topic_index',
        toInteger($topK),
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<TopicEntity> findSimilarTopics(Integer topK, float[] embedding);

    @Query("""
    CALL db.index.vector.queryNodes(
        'topic_index',
        1,
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    WHERE score > 0.90
    RETURN node
    ORDER BY score ASC
    LIMIT 1
    """)
    TopicEntity findSameTopic(float[] embedding);
}

