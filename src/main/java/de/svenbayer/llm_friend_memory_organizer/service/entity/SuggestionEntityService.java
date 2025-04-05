package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.SuggestionEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.repository.MemoryRepository;
import de.svenbayer.llm_friend_memory_organizer.repository.SuggestionRepository;
import de.svenbayer.llm_friend_memory_organizer.service.llm.DeepMemorySystemPromptService;
import de.svenbayer.llm_friend_memory_organizer.service.llm.LlmMessageProcessor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SuggestionEntityService implements IEntityPersistingService {

    public static final int MAX_MEMORY_CHANGES = 6;
    public static final int MAX_SUGGESTIONS = 5;

    private final EmbeddingModel embeddingModel;
    private final SuggestionRepository suggestionRepository;
    private final DeepMemorySystemPromptService deepMemorySystemPromptService;
    private final LlmMessageProcessor llmMessageProcessor;
    private final LineElementsComponent lineElementsComponent;
    private final MemoryRepository memoryRepository;

    private final Set<SuggestionEntity> suggestions = new HashSet<>();

    public SuggestionEntityService(EmbeddingModel embeddingModel, SuggestionRepository suggestionRepository, MemoryRepository memoryRepository, DeepMemorySystemPromptService deepMemorySystemPromptService, LlmMessageProcessor llmMessageProcessor, LineElementsComponent lineElementsComponent) {
        this.embeddingModel = embeddingModel;
        this.suggestionRepository = suggestionRepository;
        this.memoryRepository = memoryRepository;
        suggestionRepository.createIndexIfNotExists();
        this.deepMemorySystemPromptService = deepMemorySystemPromptService;
        this.llmMessageProcessor = llmMessageProcessor;
        this.lineElementsComponent = lineElementsComponent;
    }

    public void createSuggestions(Set<TopTopicEntity> topTopics) {
        removeOutdatedSuggestions(topTopics);
        for (TopTopicEntity topTopic : topTopics) {
            List<MemoryEntity> memoriesLinkedToTopTopic = memoryRepository.findMemoriesThatHaveTopTopic(topTopic.getTopicName());
            Prompt prompt = deepMemorySystemPromptService.getSuggestionPromptForMemories(memoriesLinkedToTopTopic, MAX_SUGGESTIONS);
            String message = llmMessageProcessor.processMessage(prompt);
            List<String> suggestionLines = lineElementsComponent.parseToLines(message);
            for (String suggestionLine : suggestionLines) {
                SuggestionEntity suggestionEntity = createSuggestionEntity(suggestionLine, topTopic);
                if (suggestionEntity != null) {
                    this.suggestions.add(suggestionEntity);
                }
            }
        }
    }

    protected SuggestionEntity createSuggestionEntity(String text, TopTopicEntity topTopic) {
        SuggestionEntity suggestionEntity = new SuggestionEntity();
        suggestionEntity.addTopTopic(topTopic);
        suggestionEntity.setEmbeddingText(text);
        float[] emb = embeddingModel.embed(text);
        suggestionEntity.setEmbedding(emb);

        if (this.suggestions.contains(suggestionEntity)) {
            return suggestionEntity;
        }

        SuggestionEntity sameSuggestion = suggestionRepository.findSameSuggestion(emb);
        if (sameSuggestion != null) {
            return sameSuggestion;
        }

        return suggestionEntity;
    }

    @Override
    public void completeTransaction() {
        this.suggestionRepository.saveAll(suggestions);
        this.suggestions.clear();
    }

    private void removeOutdatedSuggestions(Set<TopTopicEntity> topTopics) {
        Set<String> topTopicNames = topTopics.stream()
                .map(TopTopicEntity::getTopicName)
                .collect(Collectors.toSet());
        List<SuggestionEntity> suggestions = suggestionRepository.findSuggestionsThatHaveTopTopics(topTopicNames);
        Set<SuggestionEntity> suggestionsToRemove = new HashSet<>();
        for (SuggestionEntity suggestion : suggestions) {
            if (suggestion.getMemoryChanges() >= MAX_MEMORY_CHANGES) {
                suggestionsToRemove.add(suggestion);
            } else {
                suggestion.setMemoryChanges(suggestion.getMemoryChanges() + 1);
            }
        }
        suggestionRepository.deleteAll(suggestionsToRemove);
    }
}
