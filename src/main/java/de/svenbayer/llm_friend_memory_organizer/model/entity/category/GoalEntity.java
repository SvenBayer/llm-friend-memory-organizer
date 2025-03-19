package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Goal")
@EqualsAndHashCode(callSuper = true)
public class GoalEntity extends CategoryEntity {

    public GoalEntity() {
        super("Goal");
    }
}
