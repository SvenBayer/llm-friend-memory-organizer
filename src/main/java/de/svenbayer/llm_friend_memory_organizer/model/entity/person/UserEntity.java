package de.svenbayer.llm_friend_memory_organizer.model.entity.person;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("User")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends PersonEntity {

    public UserEntity() {
        setId("user");
    }
}
