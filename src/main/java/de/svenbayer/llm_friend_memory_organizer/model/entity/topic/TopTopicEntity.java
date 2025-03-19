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

@Node("TopTopic")
@Data
@NoArgsConstructor
public class TopTopicEntity {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private String topicName;

    private float[] embedding;

    @Relationship(type = "HAS_TOPIC", direction = Relationship.Direction.OUTGOING)
    private Set<TopicEntity> topics = new HashSet<>();

    public void addTopic(TopicEntity topicEntity) {
        this.topics.add(topicEntity);
    }
}
