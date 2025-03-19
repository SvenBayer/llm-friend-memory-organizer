package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Interaction")
@EqualsAndHashCode(callSuper = true)
public class InteractionEntity extends CategoryEntity {

    public InteractionEntity() {
        super("Interaction");
    }
}
