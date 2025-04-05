package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TagsExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.repository.TopicRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicEntityService implements IEntityPersistingService {

    private final TopicRepository topicRepository;
    private final EmbeddingModel embeddingModel;

    private final Set<TopicEntity> topics = new HashSet<>();

    public TopicEntityService(TopicRepository topicRepository, EmbeddingModel embeddingModel) {
        this.topicRepository = topicRepository;
        this.topicRepository.createIndexIfNotExists();
        this.embeddingModel = embeddingModel;
    }

    public TopicEntity getTopicEntityForName(TagsExtractedLine topic) {
        Optional<TopicEntity> firstFound = this.topics.stream()
                .filter(topicEntity -> topicEntity.getTopicName().equals(topic.getTag()))
                .findFirst();
        if (firstFound.isPresent()) {
            return firstFound.get();
        } else {
            TopicEntity topicEntity = createTopicEntity(topic.getTag());
            this.topics.add(topicEntity);
            return topicEntity;
        }
    }

    public TopicEntity createTopicEntity(String text) {
        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setTopicName(text);
        float[] emb = embeddingModel.embed(text);
        topicEntity.setEmbedding(emb);

        if (this.topics.contains(topicEntity)) {
            return topicEntity;
        }

        TopicEntity sameTopic = topicRepository.findSameTopic(emb);
        if (sameTopic != null) {
            return sameTopic;
        }

        return topicEntity;
    }

    @Override
    public void completeTransaction() {
        this.topicRepository.saveAll(this.topics);
        this.topics.clear();
    }
}
