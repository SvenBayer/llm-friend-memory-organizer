package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Preference")
@EqualsAndHashCode(callSuper = true)
public class PreferenceEntity extends CategoryEntity {

    public PreferenceEntity() {
        super("Preference");
    }
}
