package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryEntityService {

    private final EmbeddingModel embeddingModel;

    @Getter
    private final List<MemoryEntity> memories = new ArrayList<>();

    public MemoryEntityService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    protected MemoryEntity createMemory(String text) {
        float[] emb = embeddingModel.embed(text);
        MemoryEntity memoryEntity = new MemoryEntity();
        memoryEntity.setEmbeddingText(text);
        memoryEntity.setEmbedding(emb);
        return memoryEntity;
    }
}

