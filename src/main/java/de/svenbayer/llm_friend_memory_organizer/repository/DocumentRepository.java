package de.svenbayer.llm_friend_memory_organizer.repository;

import de.svenbayer.llm_friend_memory_organizer.model.DocumentEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends Neo4jRepository<DocumentEntity, String> {

    @Query("SHOW INDEXES YIELD name WHERE name = 'memory-index'")
    List<String> findIndex();

    default void createIndexIfNotExists() {
        if (findIndex().isEmpty()) {
            createDocumentEmbeddingIndexIfNotExists();
        }
    }

    @Query("""
    CREATE VECTOR INDEX memory_index IF NOT EXISTS
    FOR (n:Document)
    ON (n.embedding)
    OPTIONS {
        indexConfig: {
            `vector.dimensions`: 768,
            `vector.similarity_function`: 'cosine'
        }
    }
    """)
    void createDocumentEmbeddingIndexIfNotExists();

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
    List<DocumentEntity> findSimilarDocuments(Integer topK, float[] embedding);

    /**
     * Example: find all documents directly related to a given document.
     */
    @Query("""
        MATCH (d:Document {id: $docId})-[:RELATED_TO]->(related:Document)
        RETURN related
        """)
    List<DocumentEntity> findDirectlyRelatedDocuments(String docId);
}

