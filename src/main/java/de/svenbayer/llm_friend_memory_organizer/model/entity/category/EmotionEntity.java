package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Emotion")
@EqualsAndHashCode(callSuper = true)
public class EmotionEntity extends CategoryEntity {

    public EmotionEntity() {
        super("Emotion");
    }
}
