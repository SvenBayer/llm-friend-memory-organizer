package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("PersonalInformation")
@EqualsAndHashCode(callSuper = true)
public class PersonalInformationEntity extends CategoryEntity {

    public PersonalInformationEntity() {
        super("Personal Information");
    }
}
