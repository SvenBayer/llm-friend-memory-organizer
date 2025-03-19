package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.model.DocumentEntity;
import de.svenbayer.llm_friend_memory_organizer.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StartupService {

    private static final Logger logger = LoggerFactory.getLogger(StartupService.class);

    private final DocumentRepository documentRepository;
    private final EmbeddingModel embeddingModel;

    @Autowired
    public StartupService(DocumentRepository documentRepository, EmbeddingModel embeddingModel) {
        this.documentRepository = documentRepository;
        this.embeddingModel = embeddingModel;
    }

    //@PostConstruct
    public void onStartup() {
        // 1) Create index if not present
        documentRepository.createIndexIfNotExists();
        logger.info("Embedding index created (if not exists).");

        // 2) Create sample documents
        DocumentEntity doc1 = createDocument("doc1", "Hello World");
        DocumentEntity doc2 = createDocument("doc2", "Spring Data Neo4j is cool");
        DocumentEntity doc3 = createDocument("doc3", "This is a test document");

        // 3) Link doc1 -> doc2
        doc1.getRelatedDocs().add(doc2);
        documentRepository.save(doc1);
        logger.info("Linked doc1 -> doc2.");

        // 4) Search by embedding
        float[] embedding = embeddingModel.embed("Hello");
        List<DocumentEntity> similarToHello = documentRepository.findSimilarDocuments(5, embedding);
        logger.info("Searching for docs similar to 'Hello'...");
        similarToHello.forEach(d -> logger.info("Found similar doc: {}", d.getId()));

        // 5) Search by relationship
        List<DocumentEntity> doc1Related = documentRepository.findDirectlyRelatedDocuments("doc1");
        logger.info("Docs directly related to doc1:");
        doc1Related.forEach(d -> logger.info("doc1 -> {}", d.getId()));
    }

    private DocumentEntity createDocument(String id, String text) {
        DocumentEntity doc = new DocumentEntity();
        doc.setId(id);
        doc.setText(text);
        doc.setEmbedding(embeddingModel.embed(text));

        // Persist in Neo4j
        return documentRepository.save(doc);
    }
}

