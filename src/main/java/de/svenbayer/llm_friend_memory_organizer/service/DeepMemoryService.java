package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.model.message.*;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.*;
import de.svenbayer.llm_friend_memory_organizer.service.entity.EntityCreatorService;
import de.svenbayer.llm_friend_memory_organizer.service.extractor.MemoryExtractorService;
import de.svenbayer.llm_friend_memory_organizer.service.llm.MessageExtractorService;
import de.svenbayer.llm_friend_memory_organizer.service.process.OllamaProcessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DeepMemoryService {

    private final OllamaProcessService ollamaProcessService;
    private final EntityCreatorService entityCreatorService;
    private final MessageExtractorService messageExtractorService;
    private final MemoryExtractorService memoryExtractorService;

    public DeepMemoryService(OllamaProcessService ollamaProcessService, EntityCreatorService entityCreatorService, MessageExtractorService messageExtractorService, MemoryExtractorService memoryExtractorService) {
        this.ollamaProcessService = ollamaProcessService;
        this.entityCreatorService = entityCreatorService;
        this.messageExtractorService = messageExtractorService;
        this.memoryExtractorService = memoryExtractorService;
    }

    public RelevantMemories memorizeMessage(String message) {
        UserMessage userMessage = new UserMessage(message);
        processMessageToGraphInformation(userMessage);
        RelevantMemories relevantMemories = memoryExtractorService.extractRelevantInformationForUserMessage(userMessage);
        ollamaProcessService.stopOllamaContainer();
        return relevantMemories;
    }

    private void processMessageToGraphInformation(UserMessage userMessage) {
        System.out.println("User message:\n" + userMessage);

        EnrichedMessage enrichedMessage = new EnrichedMessage();

        List<InformationExtractedLine> informationExtractedLines = messageExtractorService.extractInformationLines(userMessage);
        enrichedMessage.setInformationExtractedLines(informationExtractedLines);

        List<InformationExtractedLine> relationshipLines = messageExtractorService.extractRelationshipMessage(userMessage);
        enrichedMessage.addInformationExtractedLines(relationshipLines);

        Map<InformationExtractedLine, List<CategoriesExtractedLine>> categoriesExtractedLines = messageExtractorService.extractCategorizedMessage(enrichedMessage);
        enrichedMessage.setCategoriesExtractedLines(categoriesExtractedLines);

        Map<InformationExtractedLine, List<UsersExtractedLine>> usersLines =  messageExtractorService.extractCategorizedWithUserMessage(enrichedMessage);
        enrichedMessage.setUsersLines(usersLines);

        Map<InformationExtractedLine, List<TagsExtractedLine>> tagsExtractedLine = messageExtractorService.extractTaggedMessage(enrichedMessage);
        enrichedMessage.setTagsExtractedLine(tagsExtractedLine);

        TimeExtractedIndexes timeExtractedIndexes = messageExtractorService.extractTimeIndexesMessage(enrichedMessage);
        enrichedMessage.setTimeExtractedIndexes(timeExtractedIndexes);

        TopTopicsExtractedWithTags topTopicsExtractedWithTags = messageExtractorService.extractTopicMessage(enrichedMessage);
        enrichedMessage.setTagsWithTopTopics(topTopicsExtractedWithTags);

        entityCreatorService.createEntities(enrichedMessage);
    }
}
