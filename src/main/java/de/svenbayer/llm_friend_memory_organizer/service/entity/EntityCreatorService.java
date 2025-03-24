package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
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
        List<CategoriesExtractedLine> categoriesExtractedLines = enrichedMessage.getCategoriesExtractedLines();
        List<UsersExtractedLine> usersLines = enrichedMessage.getUsersLines();
        List<TagsExtractedLine> tagsExtractedLine = enrichedMessage.getTagsExtractedLine();

        if (!informationExtractedLines.isEmpty()) {
            int size = informationExtractedLines.size();
            boolean isCategoriesEmpty = isEmptyAndNotHasSizeOf(categoriesExtractedLines, size);
            boolean isUsersEmpty = isEmptyAndNotHasSizeOf(usersLines, size);
            boolean isTagsEmpty = isEmptyAndNotHasSizeOf(tagsExtractedLine, size);
            if (isCategoriesEmpty && isUsersEmpty && isTagsEmpty) {
                System.out.println("Not all necessary information is available to create memories.");
                return;
            }

            for (int i = 0; i < size; i++) {
                MemoryEntity memoryEntity = memoryService.createMemory(informationExtractedLines.get(i).getLine());

                memoryService.getMemories().add(memoryEntity);

                Set<PersonEntity> personEntities = personEntityService.getPersonEntitiesForSection(usersLines.get(i).getUsers());
                for (PersonEntity personEntity : personEntities) {
                    List<CategoryEntity> categoryEntities = personEntity.getCategoriesForText(categoriesExtractedLines.get(i).getCategories());
                    for (CategoryEntity categoryEntity : categoryEntities) {
                        categoryEntity.addMemory(memoryEntity);
                    }
                }

                List<String> topics = tagsExtractedLine.get(i).getTags();
                for (String topic : topics) {
                    TopicEntity topicEntity = topicEntityService.getTopicEntityForName(topic);
                    topicEntity.addMemory(memoryEntity);
                    for (PersonEntity personEntity : personEntities) {
                        personEntity.addTopic(topicEntity);
                    }
                }
            }
        }
    }

    private boolean isEmptyAndNotHasSizeOf(List lines, int size) {
        return lines.isEmpty() || lines.size() < size;
    }
}
