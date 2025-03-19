package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.model.DocumentEntity;
import de.svenbayer.llm_friend_memory_organizer.repository.DocumentRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EmbeddingModel embeddingModel;  // Some service that calls Ollama/Nomic to get embeddings

    @Autowired
    public DocumentService(DocumentRepository documentRepository, EmbeddingModel embeddingModel) {
        this.documentRepository = documentRepository;
        this.embeddingModel = embeddingModel;
    }

    /**
     * Create (or update) a Document in Neo4j with embedded text & vector.
     */
    public DocumentEntity addDocument(String docId, String text) {
        float[] embedding = embeddingModel.embed(text);

        // If doc already exists, we'll just update it; if not, create a new one
        DocumentEntity doc = documentRepository.findById(docId)
                .orElse(new DocumentEntity());

        doc.setId(docId);
        doc.setText(text);
        doc.setEmbedding(embedding);

        return documentRepository.save(doc);
    }

    /**
     * Find top K similar documents by embedding, using the custom vector query.
     */
    public List<DocumentEntity> findSimilar(String queryText, int k) {
        // Get embedding for the query
        float[] queryEmbedding = embeddingModel.embed(queryText);

        // Use custom query to call db.index.vector.query
        return documentRepository.findSimilarDocuments(k, queryEmbedding);
    }

    /**
     * Create a relationship in Neo4j between two existing documents.
     *
     * (Option 1) The quick Cypher approach:
     */
    public void relateDocuments(String fromId, String toId) {
        // Load them from DB
        DocumentEntity fromDoc = documentRepository.findById(fromId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + fromId));
        DocumentEntity toDoc = documentRepository.findById(toId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + toId));

        fromDoc.getRelatedDocs().add(toDoc);
        documentRepository.save(fromDoc);
        // This creates the :RELATED_TO relationship automatically,
        // because fromDoc has a relationship set toDoc in `relatedDocs`.
    }

    /**
     * (Option 2) If you prefer direct @Query or driver usage, you can define
     * a custom query in the repository or just do something like:
     *
     *   MATCH (a:Document {id: $fromId}), (b:Document {id: $toId})
     *   CREATE (a)-[:RELATED_TO]->(b)
     *
     * But often the Spring Data approach above is simpler.
     */
}

