package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Place")
@EqualsAndHashCode(callSuper = true)
public class PlaceEntity extends CategoryEntity {

    public PlaceEntity() {
        super("Place");
    }
}
