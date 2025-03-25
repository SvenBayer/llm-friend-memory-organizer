package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.EnrichedMessage;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TopTopicsExtractedWithTags;
import de.svenbayer.llm_friend_memory_organizer.repository.TopTopicRepository;
import de.svenbayer.llm_friend_memory_organizer.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicEntityService implements IEntityPersistingService {

    private final TopicRepository topicRepository;
    private final TopTopicRepository topTopicRepository;

    private final Set<TopicEntity> topics;
    private final Set<TopTopicEntity> topTopics;

    public TopicEntityService(TopicRepository topicRepository, TopTopicRepository topTopicRepository) {
        this.topicRepository = topicRepository;
        this.topTopicRepository = topTopicRepository;
        this.topics = new HashSet<>(topicRepository.findAll());
        this.topTopics = new HashSet<>(topTopicRepository.findAll());
    }

    protected void createTopTopics(EnrichedMessage enrichedMessage) {
        TopTopicsExtractedWithTags tagsWithTopTopics = enrichedMessage.getTagsWithTopTopics();
        Map<String, Set<String>> topTopicsWithTags = tagsWithTopTopics.getTopTopicsWithTags();

        for (Map.Entry<String, Set<String>> entry : topTopicsWithTags.entrySet()) {
            TopTopicEntity topTopic = getTopTopicEntityForName(entry.getKey());
            Set<String> topicsToLink = entry.getValue();
            for (TopicEntity topicEntity : this.topics) {
                for (String top : topicsToLink) {
                    if (topicEntity.getTopicName().equals(top)) {
                        topTopic.addTopic(topicEntity);
                    }
                }
            }
        }
    }

    private TopTopicEntity getTopTopicEntityForName(String topTopic) {
        Optional<TopTopicEntity> firstFound = this.topTopics.stream()
                .filter(topicEntity -> topicEntity.getTopicName().equals(topTopic))
                .findFirst();
        if (firstFound.isPresent()) {
            return firstFound.get();
        } else {
            TopTopicEntity topTopicEntity = new TopTopicEntity();
            topTopicEntity.setTopicName(topTopic);
            this.topTopics.add(topTopicEntity);
            return topTopicEntity;
        }
    }

    public TopicEntity getTopicEntityForName(String topic) {
        Optional<TopicEntity> firstFound = this.topics.stream()
                .filter(topicEntity -> topicEntity.getTopicName().equals(topic))
                .findFirst();
        if (firstFound.isPresent()) {
            return firstFound.get();
        } else {
            TopicEntity topicEntity = new TopicEntity();
            topicEntity.setTopicName(topic);
            this.topics.add(topicEntity);
            return topicEntity;
        }
    }

    @Override
    public void persistData() {
        this.topicRepository.saveAll(this.topics);
        this.topTopicRepository.saveAll(this.topTopics);
    }
}
