package de.svenbayer.llm_friend_memory_organizer.model.entity.topic;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Node("Topic")
@Data
@NoArgsConstructor
public class TopicEntity {

    @Id
    private String topicName;

    private float[] embedding = new float[0];;

    @Relationship(type = "HAS_MEMORY", direction = Relationship.Direction.OUTGOING)
    private Set<MemoryEntity> memories = new HashSet<>();

    public void addMemory(MemoryEntity memory) {
        memories.add(memory);
    }
}
