package de.svenbayer.llm_friend_memory_organizer.model.entity.category;

import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Challenge")
@EqualsAndHashCode(callSuper = true)
public class ChallengeEntity extends CategoryEntity {

    public ChallengeEntity() {
        super("Challenge");
    }
}
