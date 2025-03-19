package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Relationship")
@EqualsAndHashCode(callSuper = true)
public class RelationshipEntity extends CategoryEntity {

    public RelationshipEntity() {
        super("Relationship");
    }
}
