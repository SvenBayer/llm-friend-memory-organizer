package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Activity")
@EqualsAndHashCode(callSuper = true)
public class ActivityEntity extends CategoryEntity {

    public ActivityEntity() {
        super("Activity");
    }
}
