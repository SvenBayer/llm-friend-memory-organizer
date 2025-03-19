package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.config.ConfigProperties;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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

    @Value("classpath:templates/tag-information-prompt.st")
    private Resource tagInformationPromptTemplate;

    @Value("classpath:templates/add-people-prompt.st")
    private Resource addPeoplePromptTemplate;

    @Value("classpath:templates/extract-time-prompt.st")
    private Resource extractTimePromptTemplate;

    @Value("classpath:templates/top-topics-prompt.st")
    private Resource topTopicsPromptTemplate;

    public DeepMemorySystemPromptService(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public Prompt getExtractInformationPrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.extractInformationPromptTemplate, params);
    }

    public Prompt getRelationshipConclusionPrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.relationshipConclusionPromptTemplate, params);
    }

    public Prompt getCategorizeInformationPrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.categorizeInformationPromptTemplate, params);
    }

    public Prompt getTagInformationPrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.tagInformationPromptTemplate, params);
    }

    public Prompt getAddPeoplePrompt(String userMessage, List<String> people) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        params.put("people", String.join(", ", people));
        return createPromptFromTemplate(this.addPeoplePromptTemplate, params);
    }

    public Prompt getExtractTimePrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.extractTimePromptTemplate, params);
    }

    public Prompt getTopTopicsPrompt(List<String> topics) {
        String topicList = String.join(", ", topics);
        Map<String, Object> params = new HashMap<>();
        params.put("topicList", topicList);
        return createPromptFromTemplate(this.topTopicsPromptTemplate, params);
    }

    private Prompt createPromptFromTemplate(Resource templateResource, Map<String, Object> params) {
        PromptTemplate promptTemplate = new PromptTemplate(templateResource);
        return promptTemplate.create(params);
    }
}
