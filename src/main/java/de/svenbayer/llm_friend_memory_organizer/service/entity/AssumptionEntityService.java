package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.inferred.AssumptionEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.repository.AssumptionRepository;
import de.svenbayer.llm_friend_memory_organizer.repository.MemoryRepository;
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
public class AssumptionEntityService implements IEntityPersistingService {

    public static final int MAX_MEMORY_CHANGES = 6;
    public static final int MAX_ASSUMPTIONS = 5;

    private final EmbeddingModel embeddingModel;
    private final AssumptionRepository assumptionRepository;
    private final MemoryRepository memoryRepository;
    private final DeepMemorySystemPromptService deepMemorySystemPromptService;
    private final LlmMessageProcessor llmMessageProcessor;
    private final LineElementsComponent lineElementsComponent;

    private final Set<AssumptionEntity> assumptions = new HashSet<>();

    public AssumptionEntityService(EmbeddingModel embeddingModel, AssumptionRepository assumptionRepository, MemoryRepository memoryRepository, DeepMemorySystemPromptService deepMemorySystemPromptService, LlmMessageProcessor llmMessageProcessor, LineElementsComponent lineElementsComponent) {
        this.embeddingModel = embeddingModel;
        this.assumptionRepository = assumptionRepository;
        this.memoryRepository = memoryRepository;
        this.deepMemorySystemPromptService = deepMemorySystemPromptService;
        this.llmMessageProcessor = llmMessageProcessor;
        this.lineElementsComponent = lineElementsComponent;
        assumptionRepository.createIndexIfNotExists();
    }

    public void createAssumptions(Set<TopTopicEntity> topTopics) {
        removeOutdatedAssumptions(topTopics);
        for (TopTopicEntity topTopic : topTopics) {
            List<MemoryEntity> memoriesLinkedToTopTopic = memoryRepository.findMemoriesThatHaveTopTopic(topTopic.getTopicName());
            Prompt prompt = deepMemorySystemPromptService.getAssumptionPromptForMemories(memoriesLinkedToTopTopic, MAX_ASSUMPTIONS);
            String message = llmMessageProcessor.processMessage(prompt);
            List<String> assumptionLines = lineElementsComponent.parseToLines(message);
            for (String assumptionLine : assumptionLines) {
                AssumptionEntity assumptionEntity = createAssumptionEntity(assumptionLine, topTopic);
                if (assumptionEntity != null) {
                    this.assumptions.add(assumptionEntity);
                }
            }
        }
    }

    protected AssumptionEntity createAssumptionEntity(String text, TopTopicEntity topTopic) {
        AssumptionEntity assumptionEntity = new AssumptionEntity();
        assumptionEntity.addTopTopic(topTopic);
        assumptionEntity.setEmbeddingText(text);
        float[] emb = embeddingModel.embed(text);
        assumptionEntity.setEmbedding(emb);

        if (this.assumptions.contains(assumptionEntity)) {
            return assumptionEntity;
        }

        AssumptionEntity sameAssumption = assumptionRepository.findSameAssumption(emb);
        if (sameAssumption != null) {
            return sameAssumption;
        }

        return assumptionEntity;
    }

    @Override
    public void completeTransaction() {
        this.assumptionRepository.saveAll(assumptions);
        this.assumptions.clear();
    }

    private void removeOutdatedAssumptions(Set<TopTopicEntity> topTopics) {
        Set<String> topTopicNames = topTopics.stream()
                .map(TopTopicEntity::getTopicName)
                .collect(Collectors.toSet());
        List<AssumptionEntity> assumptions = assumptionRepository.findAssumptionsThatHaveTopTopics(topTopicNames);
        Set<AssumptionEntity> assumptionsToRemove = new HashSet<>();
        for (AssumptionEntity assumption : assumptions) {
            if (assumption.getMemoryChanges() >= MAX_MEMORY_CHANGES) {
                assumptionsToRemove.add(assumption);
            } else {
                assumption.setMemoryChanges(assumption.getMemoryChanges() + 1);
            }
        }
        assumptionRepository.deleteAll(assumptionsToRemove);
    }
}
