package de.svenbayer.llm_friend_memory_organizer.service.llm;

import de.svenbayer.llm_friend_memory_organizer.config.ConfigProperties;
import de.svenbayer.llm_friend_memory_organizer.model.message.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DeepMemorySystemPromptService {

    private final ConfigProperties configProperties;

    @Value("classpath:templates/extract-information-prompt.st")
    private Resource extractInformationPromptTemplate;

    @Value("classpath:templates/relationship-conclusion-prompt.st")
    private Resource relationshipConclusionPromptTemplate;

    @Value("classpath:templates/categorize-information-prompt.st")
    private Resource categorizeInformationPromptTemplate;

    @Value("classpath:templates/extract-user-prompt.st")
    private Resource extractUserPromptTemplate;

    @Value("classpath:templates/tag-information-prompt.st")
    private Resource tagInformationPromptTemplate;

    @Value("classpath:templates/extract-time-prompt.st")
    private Resource extractTimePromptTemplate;

    @Value("classpath:templates/top-topics-prompt.st")
    private Resource topTopicsPromptTemplate;

    public DeepMemorySystemPromptService(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public Prompt getExtractInformationPrompt(UserMessage userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage.getMessage());
        return createPromptFromTemplate(this.extractInformationPromptTemplate, params);
    }

    public Prompt getRelationshipConclusionPrompt(UserMessage userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage.getMessage());
        return createPromptFromTemplate(this.relationshipConclusionPromptTemplate, params);
    }

    public Prompt getCategorizeInformationPrompt(String message) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", message);
        return createPromptFromTemplate(this.categorizeInformationPromptTemplate, params);
    }

    public Prompt getExtractUserPromptTemplate(String message) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", message);
        return createPromptFromTemplate(this.extractUserPromptTemplate, params);
    }

    public Prompt getTagInformationPrompt(String message) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", message);
        return createPromptFromTemplate(this.tagInformationPromptTemplate, params);
    }

    public Prompt getExtractTimePrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.extractTimePromptTemplate, params);
    }

    public Prompt getTopTopicsPrompt(String tagsAsLines) {
        Map<String, Object> params = new HashMap<>();
        params.put("topicList", tagsAsLines);
        return createPromptFromTemplate(this.topTopicsPromptTemplate, params);
    }

    private Prompt createPromptFromTemplate(Resource templateResource, Map<String, Object> params) {
        PromptTemplate promptTemplate = new PromptTemplate(templateResource);
        return promptTemplate.create(params);
    }
}
