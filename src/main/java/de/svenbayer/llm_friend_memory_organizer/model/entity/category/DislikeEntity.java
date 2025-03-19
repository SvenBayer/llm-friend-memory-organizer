package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Dislike")
@EqualsAndHashCode(callSuper = true)
public class DislikeEntity extends CategoryEntity {

    public DislikeEntity() {
        super("Dislike");
    }
}
