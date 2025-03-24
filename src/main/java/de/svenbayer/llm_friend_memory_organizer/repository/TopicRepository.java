package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends Neo4jRepository<TopicEntity, String> {

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
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<TopicEntity> findSimilarTopics(Integer topK, float[] embedding);

    /**
     * Example: find all memories directly related to a given memory.
     */
    @Query("""
        MATCH (d:Topic {id: $id})-[:RELATED_TO]->(related:Memory)
        RETURN related
        """)
    List<TopicEntity> findDirectlyRelatedTopics(String id);

    @Query("MATCH (t:Topic) WHERE NOT t:TopTopic RETURN t")
    List<TopicEntity> findAllTopicsWithoutTopTopics();

    @Query("MATCH (t:TopTopic) RETURN t")
    List<TopTopicEntity> findAllTopTopics();
}

