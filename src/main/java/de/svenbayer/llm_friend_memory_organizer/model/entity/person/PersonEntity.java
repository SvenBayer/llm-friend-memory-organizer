package de.svenbayer.llm_friend_memory_organizer.model.entity.person;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.category.*;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node("Person")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonEntity extends MemoryEntity {

    private List<String> aliases = new ArrayList<>();
    private boolean isUser = false;
    private boolean isAssistant = false;

    @Relationship(type = "HAS_CATEGORY", direction = Relationship.Direction.OUTGOING)
    private Set<CategoryEntity> categories = new HashSet<>();

    @Relationship(type = "HAS_TOPIC", direction = Relationship.Direction.OUTGOING)
    private Set<TopicEntity> topics = new HashSet<>();

    public PersonEntity() {
        categories.add(new ActivityEntity());
        categories.add(new AssetEntity());
        categories.add(new ChallengeEntity());
        categories.add(new DislikeEntity());
        categories.add(new EmotionEntity());
        categories.add(new GoalEntity());
        categories.add(new InteractionEntity());
        categories.add(new PersonalInformationEntity());
        categories.add(new PlaceEntity());
        categories.add(new PreferenceEntity());
        categories.add(new RelationshipEntity());
    }

    public void addTopic(TopicEntity topicEntity) {
        topics.add(topicEntity);
    }

    public List<CategoryEntity> getCategoriesForText(List<String> assignedCategories) {
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        for (String assignedCategory : assignedCategories) {
            Optional<CategoryEntity> potentialCategoryFound = categories.stream()
                    .filter(category -> assignedCategory.contains(category.getName()))
                    .findFirst();
            potentialCategoryFound.ifPresent(categoryEntities::add);
        }
        return categoryEntities;
    }

    public void addAliases(List<String> alternativeAliases) {
        this.aliases.addAll(alternativeAliases);
    }
}
