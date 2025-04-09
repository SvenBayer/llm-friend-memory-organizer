package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.repository.MemoryRepository;
import de.svenbayer.llm_friend_memory_organizer.service.extractor.MemoryFormatterService;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimpleMemoryFinderService {

    private final MemoryRepository memoryRepository;
    private final EmbeddingModel embeddingModel;
    private final MemoryFormatterService memoryFormatterService;

    public SimpleMemoryFinderService(MemoryRepository memoryRepository, EmbeddingModel embeddingModel, MemoryFormatterService memoryFormatterService) {
        this.memoryRepository = memoryRepository;
        this.embeddingModel = embeddingModel;
        this.memoryFormatterService = memoryFormatterService;
    }

    public String findRelevantMemories(String description) {
        float[] embedded = embeddingModel.embed(description);
        List<MemoryEntity> similarMemories = memoryRepository.findSimilarMemories(10, embedded);
        if (similarMemories.isEmpty()) {
            return "";
        }
        return memoryFormatterService.getAddReadibleTimeToMemories(similarMemories);
    }
}
