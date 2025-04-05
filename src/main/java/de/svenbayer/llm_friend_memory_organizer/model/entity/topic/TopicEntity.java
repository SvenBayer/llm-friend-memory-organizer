package de.svenbayer.llm_friend_memory_organizer.model.entity.topic;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.service.neo4j.FloatArrayConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Topic")
@Data
@NoArgsConstructor
public class TopicEntity {

    @Id
    private String topicName;

    @EqualsAndHashCode.Exclude
    @ConvertWith(converter = FloatArrayConverter.class)
    private float[] embedding = new float[0];

    @EqualsAndHashCode.Exclude
    @Relationship(type = "HAS_MEMORY", direction = Relationship.Direction.OUTGOING)
    private Set<MemoryEntity> memories = new HashSet<>();

    public void addMemory(MemoryEntity memory) {
        memories.add(memory);
    }
}
