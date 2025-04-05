package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.SuggestionEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SuggestionRepository extends Neo4jRepository<SuggestionEntity, String> {

    @Query("SHOW INDEXES YIELD name WHERE name = 'suggestion_index'")
    List<String> findIndex();

    default void createIndexIfNotExists() {
        if (findIndex().isEmpty()) {
            createSuggestionEmbeddingIndexIfNotExists();
        }
    }

    @Query("""
    CREATE VECTOR INDEX suggestion_index IF NOT EXISTS
    FOR (n:Suggestion)
    ON (n.embedding)
    OPTIONS {
        indexConfig: {
            `vector.dimensions`: 768,
            `vector.similarity_function`: 'cosine'
        }
    }
    """)
    void createSuggestionEmbeddingIndexIfNotExists();

    /**
     * Example: custom query to call the vector similarity procedure.
     * We pass the embedding (double[]) and how many "topK" matches we want.
     */
    @Query("""
    CALL db.index.vector.queryNodes(
        'suggestion_index',
        toInteger($topK),
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<SuggestionEntity> findSimilarSuggestions(Integer topK, float[] embedding);

    @Query("""
    CALL db.index.vector.queryNodes(
        'suggestion_index',
        1,
        [x IN $embedding | toFloat(x)]
    )
    YIELD node, score
    WHERE score > 0.90
    RETURN node
    ORDER BY score ASC
    LIMIT 1
    """)
    SuggestionEntity findSameSuggestion(float[] embedding);

    @Query("""
    MATCH (s:Suggestion)-[]->(tt:TopTopic)
    WHERE tt.topicName IN $topTopicNames
    RETURN DISTINCT s
    """)
    List<SuggestionEntity> findSuggestionsThatHaveTopTopics(Set<String> topTopicNames);

    @Query("""
    MATCH (s:Suggestion)-[]->(tt:TopTopic)-[]->(t:Topic)-[]->(m:Memory)
    WHERE m.embeddingText IN $memoryIds
    RETURN DISTINCT s
    """)
    List<SuggestionEntity> findSuggestionsThatHaveMemories(Set<String> memoryIds);
}

