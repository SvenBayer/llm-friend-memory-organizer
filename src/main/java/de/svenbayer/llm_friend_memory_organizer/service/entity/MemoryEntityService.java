package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.repository.MemoryRepository;
import lombok.Getter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MemoryEntityService {

    private final EmbeddingModel embeddingModel;
    private final MemoryRepository memoryRepository;

    @Getter
    private final Set<MemoryEntity> memories = new HashSet<>();

    public MemoryEntityService(EmbeddingModel embeddingModel, MemoryRepository memoryRepository) {
        this.embeddingModel = embeddingModel;
        this.memoryRepository = memoryRepository;
        memoryRepository.createIndexIfNotExists();
    }

    protected MemoryEntity createMemory(String text) {
        MemoryEntity memoryEntity = new MemoryEntity();
        memoryEntity.setEmbeddingText(text);
        float[] emb = embeddingModel.embed(text);
        memoryEntity.setEmbedding(emb);

        if (this.memories.contains(memoryEntity)) {
            return memoryEntity;
        }

        MemoryEntity sameMemory = memoryRepository.findSameMemory(emb);
        if (sameMemory != null) {
            return sameMemory;
        }

        return memoryEntity;
    }
}
