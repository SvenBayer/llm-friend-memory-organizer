package de.svenbayer.llm_friend_memory_organizer.service.entity;

import de.svenbayer.llm_friend_memory_organizer.model.LineElements;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.EnrichedMessage;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TopTopicsExtractedWithTags;
import de.svenbayer.llm_friend_memory_organizer.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicEntityService implements IEntityPersistingService {

    private final TopicRepository topicRepository;

    private final Set<TopicEntity> topics;
    private final Set<TopTopicEntity> topTopics;

    public TopicEntityService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
        this.topics = new HashSet<>(topicRepository.findAllTopicsWithoutTopTopics());
        this.topTopics = new HashSet<>(topicRepository.findAllTopTopics());
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
        this.topicRepository.saveAll(this.topTopics);
    }
}
