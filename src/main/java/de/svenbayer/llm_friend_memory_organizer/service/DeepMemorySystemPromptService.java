package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.config.ConfigProperties;
import de.svenbayer.llm_friend_memory_organizer.model.MemoryCategory;
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

    @Value("classpath:templates/information-relevant-prompt.st")
    private Resource informationRelevantPromptTemplate;

    @Value("classpath:templates/key-points-prompt.st")
    private Resource keyPointsPromptTemplate;

    public DeepMemorySystemPromptService(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public Prompt getInformationRelevantPrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.informationRelevantPromptTemplate, params);
    }

    public Prompt getKeyPointsPrompt(String userMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("maxLength", configProperties.maxSummarizationLength);
        params.put("userMessage", userMessage);
        return createPromptFromTemplate(this.informationRelevantPromptTemplate, params);
    }

    private Prompt createPromptFromTemplate(Resource templateResource, Map<String, Object> params) {
        PromptTemplate promptTemplate = new PromptTemplate(templateResource);
        return promptTemplate.create(params);
    }
}
