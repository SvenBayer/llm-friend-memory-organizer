package de.svenbayer.llm_friend_memory_organizer.model.entity.inferred;

import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.service.neo4j.FloatArrayConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.convert.ConvertWith;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Suggestion")
@Data
public class SuggestionEntity {

    @Id
    private String embeddingText = "";

    @EqualsAndHashCode.Exclude
    @ConvertWith(converter = FloatArrayConverter.class)
    private float[] embedding = new float[0];

    @EqualsAndHashCode.Exclude
    private boolean isAlreadySuggested = false;

    @EqualsAndHashCode.Exclude
    private int memoryChanges = 0;

    @EqualsAndHashCode.Exclude
    @Relationship(type = "HAS_TOPTOPIC", direction = Relationship.Direction.OUTGOING)
    private Set<TopTopicEntity> topTopics = new HashSet<>();

    public void addTopTopic(TopTopicEntity topTopicEntity) {
        this.topTopics.add(topTopicEntity);
    }
}
