package de.svenbayer.llm_friend_memory_organizer.service.llm;

import de.svenbayer.llm_friend_memory_organizer.component.ToLlmTextConverter;
import de.svenbayer.llm_friend_memory_organizer.config.ConfigProperties;
import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.AssumptionEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.SuggestionEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.*;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.InformationExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TagsExtractedLine;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DeepMemorySystemPromptService {

    private final ConfigProperties configProperties;
    private final ToLlmTextConverter toLlmTextConverter;

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

    @Value("classpath:templates/find-relevant-memories-prompt.st")
    private Resource findRelevantMemories;

    @Value("classpath:templates/suggestions-prompt.st")
    private Resource suggestionsPromptTemplate;

    @Value("classpath:templates/assumptions-prompt.st")
    private Resource assumptionsPromptTemplate;

    @Value("classpath:templates/find-relevant-suggestions-prompt.st")
    private Resource relevantSuggestionsForMemories;

    @Value("classpath:templates/find-relevant-assumptions-prompt.st")
    private Resource relevantAssumptionsForMemories;

    public DeepMemorySystemPromptService(ConfigProperties configProperties, ToLlmTextConverter toLlmTextConverter) {
        this.configProperties = configProperties;
        this.toLlmTextConverter = toLlmTextConverter;
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

    public Prompt getCategorizeInformationPrompt(List<InformationExtractedLine> informationExtractedLines) {
        String numberedList = toLlmTextConverter.getNumberedListForList(informationExtractedLines, InformationExtractedLine::getLine);
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", numberedList);
        return createPromptFromTemplate(this.categorizeInformationPromptTemplate, params);
    }

    public Prompt getExtractUserPromptTemplate(List<InformationExtractedLine> informationExtractedLines) {
        String numberedList = toLlmTextConverter.getNumberedListForList(informationExtractedLines, InformationExtractedLine::getLine);
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", numberedList);
        return createPromptFromTemplate(this.extractUserPromptTemplate, params);
    }

    public Prompt getTagInformationPrompt(List<InformationExtractedLine> informationExtractedLines) {
        String numberedList = toLlmTextConverter.getNumberedListForList(informationExtractedLines, InformationExtractedLine::getLine);
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", numberedList);
        return createPromptFromTemplate(this.tagInformationPromptTemplate, params);
    }

    public Prompt getExtractTimePrompt(List<InformationExtractedLine> informationExtractedLines) {
        String numberedList = toLlmTextConverter.getNumberedListForList(informationExtractedLines, InformationExtractedLine::getLine);
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", numberedList);
        return createPromptFromTemplate(this.extractTimePromptTemplate, params);
    }

    public Prompt getTopTopicsPrompt(Map<InformationExtractedLine, List<TagsExtractedLine>> tagsAsLines) {
        String pipedListElements = toLlmTextConverter.getPipedListWithKeyLines(tagsAsLines, TagsExtractedLine::getTag);
        Map<String, Object> params = new HashMap<>();
        params.put("topicList", pipedListElements);
        return createPromptFromTemplate(this.topTopicsPromptTemplate, params);
    }

    public Prompt getRelevantMemoriesForLinesPrompt(UserMessage userMessage, List<String> indirectRelatedMemories, int maxMemories) {
        String numberedList = toLlmTextConverter.getNumberedListForList(indirectRelatedMemories, String::toString);
        return createRelevantMemoriesPrompt(userMessage, maxMemories, numberedList);
    }

    public Prompt getRelevantMemoriesPrompt(UserMessage userMessage, List<MemoryEntity> indirectRelatedMemories, int maxMemories) {
        String numberedList = toLlmTextConverter.getNumberedListForList(indirectRelatedMemories, MemoryEntity::getEmbeddingText);
        return createRelevantMemoriesPrompt(userMessage, maxMemories, numberedList);
    }

    public Prompt getSuggestionPromptForMemories(List<MemoryEntity> memoriesLinkedToTopTopic, int maxSuggestions) {
        String numberedList = toLlmTextConverter.getNumberedListForList(memoriesLinkedToTopTopic, MemoryEntity::getEmbeddingText);
        Map<String, Object> params = new HashMap<>();
        params.put("memories", numberedList);
        params.put("maxSuggestions", maxSuggestions);
        return createPromptFromTemplate(this.suggestionsPromptTemplate, params);
    }

    public Prompt getAssumptionPromptForMemories(List<MemoryEntity> memoriesLinkedToTopTopic, int maxAssumptions) {
        String numberedList = toLlmTextConverter.getNumberedListForList(memoriesLinkedToTopTopic, MemoryEntity::getEmbeddingText);
        Map<String, Object> params = new HashMap<>();
        params.put("memories", numberedList);
        params.put("maxAssumptions", maxAssumptions);
        return createPromptFromTemplate(this.assumptionsPromptTemplate, params);
    }

    public Prompt getSuggestionsForMemories(List<SuggestionEntity> suggestions, Set<String> memoryIds, int maxSuggestions) {
        String suggestionNumberedList = toLlmTextConverter.getNumberedListForList(suggestions, SuggestionEntity::getEmbeddingText);
        Map<String, Object> params = new HashMap<>();
        params.put("memories", memoryIds);
        params.put("suggestions", suggestionNumberedList);
        params.put("maxSuggestions", maxSuggestions);
        return createPromptFromTemplate(this.relevantSuggestionsForMemories, params);
    }

    public Prompt getAssumptionsForMemories(List<AssumptionEntity> assumptions, Set<String> memoryIds, int maxAssumptions) {
        String suggestionNumberedList = toLlmTextConverter.getNumberedListForList(assumptions, AssumptionEntity::getEmbeddingText);
        Map<String, Object> params = new HashMap<>();
        params.put("memories", memoryIds);
        params.put("assumptions", suggestionNumberedList);
        params.put("maxAssumptions", maxAssumptions);
        return createPromptFromTemplate(this.relevantAssumptionsForMemories, params);
    }

    private Prompt createRelevantMemoriesPrompt(UserMessage userMessage, int maxMemories, String numberedList) {
        Map<String, Object> params = new HashMap<>();
        params.put("userMessage", userMessage.getMessage());
        params.put("memories", numberedList);
        params.put("maxMemories", maxMemories);
        return createPromptFromTemplate(this.findRelevantMemories, params);
    }

    private Prompt createPromptFromTemplate(Resource templateResource, Map<String, Object> params) {
        PromptTemplate promptTemplate = new PromptTemplate(templateResource);
        return promptTemplate.create(params);
    }
}
