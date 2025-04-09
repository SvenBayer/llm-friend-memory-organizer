package de.svenbayer.llm_friend_memory_organizer.service.extractor;

import de.svenbayer.llm_friend_memory_organizer.component.HumanReadibleDateFormatter;
import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.AssumptionEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.SuggestionEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.RelevantMemories;
import de.svenbayer.llm_friend_memory_organizer.model.message.UserMessage;
import de.svenbayer.llm_friend_memory_organizer.repository.AssumptionRepository;
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
public class MemoryExtractorService {

    private final MemoryRepository memoryRepository;
    private final EmbeddingModel embeddingModel;
    private final LlmMessageProcessor llmMessageProcessor;
    private final DeepMemorySystemPromptService promptService;
    private final LineElementsComponent lineElementsComponent;
    private final SuggestionRepository suggestionRepository;
    private final AssumptionRepository assumptionRepository;
    private final MemoryFormatterService memoryFormatterService;

    public MemoryExtractorService(MemoryRepository memoryRepository, EmbeddingModel embeddingModel, LlmMessageProcessor llmMessageProcessor, DeepMemorySystemPromptService promptService, LineElementsComponent lineElementsComponent, SuggestionRepository suggestionRepository, AssumptionRepository assumptionRepository, MemoryFormatterService memoryFormatterService) {
        this.memoryRepository = memoryRepository;
        this.embeddingModel = embeddingModel;
        this.llmMessageProcessor = llmMessageProcessor;
        this.promptService = promptService;
        this.lineElementsComponent = lineElementsComponent;
        this.suggestionRepository = suggestionRepository;
        this.assumptionRepository = assumptionRepository;
        this.memoryFormatterService = memoryFormatterService;
    }

    public RelevantMemories extractRelevantInformationForUserMessage(UserMessage userMessage) {
        String relevantMemories = extractRelevantMemories(userMessage);
        if (relevantMemories != null && !relevantMemories.isEmpty()) {
            Set<String> relevantMemoryIds = parseRelevantMemoryEntities(relevantMemories);
            if (!relevantMemoryIds.isEmpty()) {
                String relevantAssumptions = extractRelevantAssumptions(relevantMemoryIds);
                String relevantSuggestions = extractRelevantSuggestions(relevantMemoryIds);
                List<MemoryEntity> memoryEntities = memoryRepository.findAllById(relevantMemoryIds);

                String relevantMemoriesText = memoryFormatterService.getAddReadibleTimeToMemories(memoryEntities);
                return new RelevantMemories(relevantMemoriesText, relevantAssumptions, relevantSuggestions);
            }
        }
        return null;
    }

    private Set<String> parseRelevantMemoryEntities(String relevantMemories) {
        return new HashSet<>(lineElementsComponent.parseToLines(relevantMemories));
    }

    private String extractRelevantMemories(UserMessage userMessage) {
        float[] embedded = embeddingModel.embed(userMessage.getMessage());
        List<MemoryEntity> similarMemories = memoryRepository.findSimilarMemories(10, embedded);

        Set<String> possibleRelatedMemories = new HashSet<>();
        for (MemoryEntity memoryEntity : similarMemories) {
            System.out.println("Found memory via RAG:\n" + memoryEntity.getEmbeddingText());
            List<MemoryEntity> indirectRelatedMemories = memoryRepository.findIndirectRelatedMemories(memoryEntity.getEmbeddingText());
            if (indirectRelatedMemories != null && !indirectRelatedMemories.isEmpty()) {
                Prompt prompt = promptService.getRelevantMemoriesPrompt(userMessage, indirectRelatedMemories, 3);
                String answer = llmMessageProcessor.processMessage(prompt);
                List<String> lines = lineElementsComponent.parseToLines(answer);
                possibleRelatedMemories.addAll(lines);
                System.out.println("Answer from LLM with memories found via Graph:\n" + answer);
            }
        }
        Set<String> similarMemoriesTexts = similarMemories.stream()
                .map(MemoryEntity::getEmbeddingText)
                .collect(Collectors.toSet());
        possibleRelatedMemories.addAll(similarMemoriesTexts);

        if (!possibleRelatedMemories.isEmpty()) {
            Prompt relevantMemoriesPrompt = promptService.getRelevantMemoriesForLinesPrompt(userMessage, possibleRelatedMemories.stream().toList(), 10);
            String answerWithRelevantMemories = llmMessageProcessor.processMessage(relevantMemoriesPrompt);
            System.out.println("The final answer:\n" + answerWithRelevantMemories);
            return lineElementsComponent.extractNumberedAnswers(answerWithRelevantMemories);
        } else {
            return null;
        }
    }

    private String extractRelevantSuggestions(Set<String> memoryIds) {
        List<SuggestionEntity> suggestions = suggestionRepository.findSuggestionsThatHaveMemories(memoryIds);
        if (suggestions != null && !suggestions.isEmpty()) {
            Prompt prompt = promptService.getSuggestionsForMemories(suggestions, memoryIds, 3);
            String answer = llmMessageProcessor.processMessage(prompt);
            return lineElementsComponent.extractNumberedAnswers(answer);
        } else {
            return null;
        }
    }

    private String extractRelevantAssumptions(Set<String> memoryIds) {
        List<AssumptionEntity> assumptions = assumptionRepository.findAssumptionsThatHaveMemories(memoryIds);
        if (assumptions != null && !assumptions.isEmpty()) {
            Prompt prompt = promptService.getAssumptionsForMemories(assumptions, memoryIds, 3);
            String answer = llmMessageProcessor.processMessage(prompt);
            return lineElementsComponent.extractNumberedAnswers(answer);
        } else {
            return null;
        }
    }
}
