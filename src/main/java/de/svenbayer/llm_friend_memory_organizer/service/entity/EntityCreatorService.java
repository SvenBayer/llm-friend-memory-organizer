package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.category.CategoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.person.PersonEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.*;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.CategoriesExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.InformationExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TagsExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.UsersExtractedLine;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EntityCreatorService {
//private final Neo4jVectorStore vectorStore;
    private final MemoryEntityService memoryService;
    private final PersonEntityService personEntityService;
    private final TopicEntityService topicEntityService;
    private final EntityTimeService entityTimeService;

    public EntityCreatorService(MemoryEntityService memoryService, PersonEntityService personEntityService, TopicEntityService topicEntityService, EntityTimeService entityTimeService) {
        this.memoryService = memoryService;
        this.personEntityService = personEntityService;
        this.topicEntityService = topicEntityService;
        this.entityTimeService = entityTimeService;
    }

    public void createEntities(EnrichedMessage enrichedMessage) {
        personEntityService.createPeopleEntities(enrichedMessage);
        createMemoriesAndTopics(enrichedMessage);
        topicEntityService.createTopTopics(enrichedMessage);
        entityTimeService.setTimeForEntities(enrichedMessage);

        personEntityService.persistData();
        topicEntityService.persistData();
    }

    private void createMemoriesAndTopics(EnrichedMessage enrichedMessage) {
        List<InformationExtractedLine> informationExtractedLines = enrichedMessage.getInformationExtractedLines();
        Map<InformationExtractedLine, List<CategoriesExtractedLine>> categoriesExtractedLines = enrichedMessage.getCategoriesExtractedLines();
        Map<InformationExtractedLine, List<UsersExtractedLine>> usersLines = enrichedMessage.getUsersLines();
        Map<InformationExtractedLine, List<TagsExtractedLine>> tagsExtractedLine = enrichedMessage.getTagsExtractedLine();

        if (!informationExtractedLines.isEmpty()) {
            for (InformationExtractedLine memoryKey : informationExtractedLines) {
                if (memoryKey == null || memoryKey.getLine().isEmpty()) {
                    continue;
                }
                MemoryEntity memoryEntity = memoryService.createMemory(memoryKey.getLine());

                memoryService.getMemories().add(memoryEntity);

                List<UsersExtractedLine> aliases = usersLines.get(memoryKey);
                if (aliases == null) {
                    aliases = new ArrayList<>();
                }
                Set<PersonEntity> personEntities = personEntityService.getPersonEntitiesForSection(aliases);
                if (personEntities != null) {
                    for (PersonEntity personEntity : personEntities) {
                        List<CategoriesExtractedLine> assignedCategories = categoriesExtractedLines.get(memoryKey);
                        List<CategoryEntity> categoryEntities = personEntity.getCategoriesForText(assignedCategories);
                        for (CategoryEntity categoryEntity : categoryEntities) {
                            categoryEntity.addMemory(memoryEntity);
                        }
                    }
                }

                List<TagsExtractedLine> topics = tagsExtractedLine.get(memoryKey);
                if (topics != null) {
                    for (TagsExtractedLine topic : topics) {
                        TopicEntity topicEntity = topicEntityService.getTopicEntityForName(topic);
                        if (topicEntity != null) {
                            topicEntity.addMemory(memoryEntity);
                            if (personEntities != null) {
                                for (PersonEntity personEntity : personEntities) {
                                    personEntity.addTopic(topicEntity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
