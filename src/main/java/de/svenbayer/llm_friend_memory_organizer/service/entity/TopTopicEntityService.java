package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.EnrichedMessage;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TagsExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TopTopicsExtractedWithTags;
import de.svenbayer.llm_friend_memory_organizer.repository.TopTopicRepository;
import de.svenbayer.llm_friend_memory_organizer.repository.TopicRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class TopTopicEntityService implements IEntityPersistingService {

    private final TopTopicRepository topTopicRepository;
    private final EmbeddingModel embeddingModel;

    private final Set<TopTopicEntity> topTopics = new HashSet<>();
    private TopicEntityService topicEntitiyService;

    public TopTopicEntityService(TopTopicRepository topTopicRepository, EmbeddingModel embeddingModel, TopicEntityService topicEntitiyService) {
        this.topTopicRepository = topTopicRepository;
        this.topicEntitiyService = topicEntitiyService;
        this.topTopicRepository.createIndexIfNotExists();
        this.embeddingModel = embeddingModel;
    }

    protected Set<TopTopicEntity> createTopTopics(EnrichedMessage enrichedMessage) {
        TopTopicsExtractedWithTags tagsWithTopTopics = enrichedMessage.getTagsWithTopTopics();
        Map<String, Set<String>> topTopicsWithTags = tagsWithTopTopics.getTopTopicsWithTags();

        for (Map.Entry<String, Set<String>> entry : topTopicsWithTags.entrySet()) {
            TopTopicEntity topTopic = getTopTopicEntityForName(entry.getKey());
            Set<String> topicsToLink = entry.getValue();
            for (String top : topicsToLink) {
                TopicEntity topicEntity = topicEntitiyService.createTopicEntity(top);
                if (topicEntity.getTopicName().equals(top)) {
                    topTopic.addTopic(topicEntity);
                }
            }
        }
        return Set.copyOf(this.topTopics);
    }

    private TopTopicEntity getTopTopicEntityForName(String topTopic) {
        Optional<TopTopicEntity> firstFound = this.topTopics.stream()
                .filter(topicEntity -> topicEntity.getTopicName().equals(topTopic))
                .findFirst();
        if (firstFound.isPresent()) {
            return firstFound.get();
        } else {
            TopTopicEntity topTopicEntity = createTopTopicEntity(topTopic);
            this.topTopics.add(topTopicEntity);
            return topTopicEntity;
        }
    }

    private TopTopicEntity createTopTopicEntity(String text) {
        TopTopicEntity topTopicEntity = new TopTopicEntity();
        topTopicEntity.setTopicName(text);
        float[] emb = embeddingModel.embed(text);
        topTopicEntity.setEmbedding(emb);

        if (this.topTopics.contains(topTopicEntity)) {
            return topTopicEntity;
        }

        TopTopicEntity sameTopTopic = topTopicRepository.findSameTopTopic(emb);
        if (sameTopTopic != null) {
            return sameTopTopic;
        }

        return topTopicEntity;
    }

    @Override
    public void completeTransaction() {
        this.topTopicRepository.saveAll(this.topTopics);
        this.topTopics.clear();
    }
}
