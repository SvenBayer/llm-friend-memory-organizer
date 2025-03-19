package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryRepository extends Neo4jRepository<MemoryEntity, String> {

    @Query("SHOW INDEXES YIELD name WHERE name = 'memory-index'")
    List<String> findIndex();

    default void createIndexIfNotExists() {
        if (findIndex().isEmpty()) {
            createMemoryEmbeddingIndexIfNotExists();
        }
    }

    @Query("""
    CREATE VECTOR INDEX memory_index IF NOT EXISTS
    FOR (n:MemoryEntity)
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
    RETURN node
    ORDER BY score ASC
    LIMIT toInteger($topK)
    """)
    List<MemoryEntity> findSimilarMemories(Integer topK, float[] embedding);

    /**
     * Example: find all memories directly related to a given memory.
     */
    @Query("""
        MATCH (d:Document {id: $id})-[:RELATED_TO]->(related:Memory)
        RETURN related
        """)
    List<MemoryEntity> findDirectlyRelatedMemories(String id);
}

