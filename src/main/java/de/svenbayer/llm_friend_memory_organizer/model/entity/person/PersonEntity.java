package de.svenbayer.llm_friend_memory_organizer.model.entity.person;

import de.svenbayer.llm_friend_memory_organizer.model.entity.category.*;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.CategoriesExtractedLine;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Node("Person")
@Data
public class PersonEntity {

    @Id
    private String id = UUID.randomUUID().toString();

    private Set<String> aliases = new HashSet<>();

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

    public List<CategoryEntity> getCategoriesForText(List<CategoriesExtractedLine> assignedCategories) {
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        for (CategoriesExtractedLine assignedCategory : assignedCategories) {
            Optional<CategoryEntity> potentialCategoryFound = categories.stream()
                    .filter(category -> assignedCategory.getCategory().contains(category.getName()))
                    .findFirst();
            potentialCategoryFound.ifPresent(categoryEntities::add);
        }
        return categoryEntities;
    }

    public void addAliases(List<String> alternativeAliases) {
        this.aliases.addAll(alternativeAliases);
    }
}
