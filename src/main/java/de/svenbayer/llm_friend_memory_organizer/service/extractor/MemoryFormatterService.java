package de.svenbayer.llm_friend_memory_organizer.service.extractor;

import de.svenbayer.llm_friend_memory_organizer.component.HumanReadibleDateFormatter;
import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemoryFormatterService {

    private final HumanReadibleDateFormatter humanReadibleDateFormatter;

    public MemoryFormatterService(HumanReadibleDateFormatter humanReadibleDateFormatter) {
        this.humanReadibleDateFormatter = humanReadibleDateFormatter;
    }

    public String getAddReadibleTimeToMemories(List<MemoryEntity> memoryEntities) {
        StringBuilder memBuilder = new StringBuilder();
        for (int i = 0; i < memoryEntities.size(); i++) {
            MemoryEntity memoryEntity = memoryEntities.get(i);
            String pre = humanReadibleDateFormatter.formatDateToHumanReadible(memoryEntity.getStartTime().toLocalDate());
            memBuilder.append(i + 1).append(". ").append(pre).append(memoryEntity.getEmbeddingText()).append("\n");
        }
        return memBuilder.toString();
    }
}
