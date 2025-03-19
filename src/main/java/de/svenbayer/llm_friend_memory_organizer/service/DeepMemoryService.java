package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
import de.svenbayer.llm_friend_memory_organizer.service.entity.EntityCreatorService;
import de.svenbayer.llm_friend_memory_organizer.service.process.OllamaProcessService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeepMemoryService {

    private final DeepMemorySystemPromptService promptService;
    private final ChatClient chatClient;
    private final OllamaProcessService ollamaProcessService;
    private final Neo4jVectorStore vectorStore;
    private final EntityCreatorService entityCreatorService;
    private final LineElementsComponent lineElementsComponent;

    public DeepMemoryService(DeepMemorySystemPromptService promptService, ChatClient.Builder chatClientBuilder, OllamaProcessService ollamaProcessService, Neo4jVectorStore vectorStore, EntityCreatorService entityCreatorService, LineElementsComponent lineElementsComponent) {
        this.promptService = promptService;
        this.chatClient = chatClientBuilder.build();
        this.ollamaProcessService = ollamaProcessService;
        this.vectorStore = vectorStore;
        this.entityCreatorService = entityCreatorService;
        this.lineElementsComponent = lineElementsComponent;
    }

    public void memorizeMessage(String userMessage) {
        processMessageToGraphInformation(userMessage);
        ollamaProcessService.stopOllamaContainer();
    }

    private boolean processMessageToGraphInformation(String userMessage) {
        System.out.println("User message:\n" + userMessage);

        String informationExtractedMessage = processToInformationMessage(userMessage);
        String relationshipMessage = processToRelationshipMessage(userMessage);
        String informationCategorizedMessage = processToCategorizedMessage(informationExtractedMessage, relationshipMessage);
        String taggedInformationMessage = processToTaggedMessage(informationCategorizedMessage);

        entityCreatorService.createPeopleEntities(informationCategorizedMessage);
        entityCreatorService.createCategoriesAndTopics(taggedInformationMessage);
        createTopTopics(taggedInformationMessage);
        createTimeEntities(informationExtractedMessage);

        return true;
    }

    private void createTimeEntities(String informationExtractedMessage) {
        Prompt extractTimePrompt = promptService.getExtractTimePrompt(informationExtractedMessage);
        String timeMessage = processMessage(extractTimePrompt);
        System.out.println("Times:\n" + timeMessage);
        entityCreatorService.createTimeEntities(timeMessage);
    }

    private void createTopTopics(String taggedInformationMessage) {
        List<String> topics = entityCreatorService.extractTopics(taggedInformationMessage);
        Prompt topTopicsPrompt = promptService.getTopTopicsPrompt(topics);
        String topTopics = processMessage(topTopicsPrompt);
        System.out.println("Top Topics:\n" + topTopics);
        entityCreatorService.createTopTopics(topTopics);
    }

    private String processToTaggedMessage(String informationCategorizedMessage) {
        Prompt taggedInformationPrompt = promptService.getTagInformationPrompt(informationCategorizedMessage);
        String taggedInformationMessage = processMessage(taggedInformationPrompt);
        System.out.println("Tagged Information:\n" + taggedInformationMessage);
        return taggedInformationMessage;
    }

    private String processToCategorizedMessage(String informationExtractedMessage, String relationshipMessage) {
        String enumerationWithRelationshipsMessage = lineElementsComponent.appendEnumerationList(informationExtractedMessage, relationshipMessage);
        Prompt categorizeInformationPrompt = promptService.getCategorizeInformationPrompt(enumerationWithRelationshipsMessage);
        String informationCategorizedMessage = processMessage(categorizeInformationPrompt);
        System.out.println("Categorized Information:\n" + informationCategorizedMessage);
        return informationCategorizedMessage;
    }

    private String processToRelationshipMessage(String userMessage) {
        Prompt relationshipConclusionPrompt = promptService.getRelationshipConclusionPrompt(userMessage);
        String relationshipMessage = processMessage(relationshipConclusionPrompt);
        System.out.println("Extracted Relationships\n" + relationshipMessage);
        return relationshipMessage;
    }

    private String processToInformationMessage(String userMessage) {
        Prompt informationRelevantPrompt = promptService.getExtractInformationPrompt(userMessage);
        String informationExtractedMessage = processMessage(informationRelevantPrompt);
        System.out.println("Extracted Information:\n" + informationExtractedMessage);
        return informationExtractedMessage;
    }

    private String processMessage(Prompt informationRelevantPrompt) {
        String storeMessage = chatClient.prompt()
                .user(informationRelevantPrompt.getContents())
                .call()
                .content();

        String decisionMessage;
        if (storeMessage.contains("</think>")) {
            decisionMessage = storeMessage.substring(storeMessage.lastIndexOf("</think>") + 8);
        } else {
            decisionMessage = storeMessage;
        }
        return decisionMessage;
    }
}
