package de.svenbayer.llm_friend_memory_organizer.model.entity.topic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("TopTopic")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TopTopicEntity extends TopicEntity {

    @Relationship(type = "HAS_TOPIC", direction = Relationship.Direction.OUTGOING)
    private Set<TopicEntity> topics = new HashSet<>();

    public void addTopic(TopicEntity topicEntity) {
        this.topics.add(topicEntity);
    }
}



