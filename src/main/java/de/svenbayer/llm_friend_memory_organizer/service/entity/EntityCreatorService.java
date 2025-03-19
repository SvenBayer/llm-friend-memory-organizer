package de.svenbayer.llm_friend_memory_organizer.service.entity;

import com.zoho.hawking.HawkingTimeParser;
import com.zoho.hawking.datetimeparser.configuration.HawkingConfiguration;
import com.zoho.hawking.language.english.model.DateRange;
import com.zoho.hawking.language.english.model.DatesFound;
import com.zoho.hawking.language.english.model.ParserOutput;
import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
import de.svenbayer.llm_friend_memory_organizer.model.LineElements;
import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.category.CategoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.person.PersonEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopTopicEntity;
import de.svenbayer.llm_friend_memory_organizer.model.entity.topic.TopicEntity;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityCreatorService {

    private final LineElementsComponent lineElementsComponent;

    // TODO fill up the list with existing entities from the database before initialising them
    private final List<PersonEntity> people = new ArrayList<>();
    private final List<TopicEntity> topics = new ArrayList<>();
    private final List<TopTopicEntity> topTopics = new ArrayList<>();
    private final List<MemoryEntity> memories = new ArrayList<>();

    public EntityCreatorService(LineElementsComponent lineElementsComponent) {
        this.lineElementsComponent = lineElementsComponent;
    }

    public List<String> extractTopics(String taggedInformationMessage) {
        List<LineElements> lineElements = lineElementsComponent.parseText(taggedInformationMessage);
        return lineElementsComponent.extractSections(List.of(3, 4), lineElements);
    }

    public void createCategoriesAndTopics(String taggedInformationMessage) {
        DateTime startOfDay = new DateTime().withTimeAtStartOfDay();
        DateTime endOfDay = new DateTime().withTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59);

        List<LineElements> lineElements = lineElementsComponent.parseText(taggedInformationMessage);
        for (LineElements lineElement : lineElements) {
            List<List<String>> sections = lineElement.getSections();
            if (!sections.isEmpty() && sections.size() > 3) {
                // The user works as a GenAI Expert.; Activity, Personal Information; the user (Sven); work, GenAI Expert; career, profession;
                MemoryEntity memoryEntity = new MemoryEntity();
                memoryEntity.addDescription(sections.getFirst().getFirst());

                memoryEntity.setStartTime(startOfDay);
                memoryEntity.setEndTime(endOfDay);
                this.memories.add(memoryEntity);

                Set<PersonEntity> personEntities = getPersonEntitiesForSection(sections.get(2));
                for (PersonEntity personEntity : personEntities) {
                    List<CategoryEntity> categoryEntities = personEntity.getCategoriesForText(sections.get(1));
                    for (CategoryEntity categoryEntity : categoryEntities) {
                        categoryEntity.addMemory(memoryEntity);
                    }
                }

                List<String> topics = sections.get(3);
                if (sections.size() > 4) {
                    topics.addAll(sections.get(4));
                }
                for (String topic : topics) {
                    TopicEntity topicEntity = getTopicEntityForName(topic);
                    topicEntity.addMemory(memoryEntity);
                    for (PersonEntity personEntity : personEntities) {
                        personEntity.addTopic(topicEntity);
                    }
                }
            }
        }
    }

    public void createTopTopics(String topTopics) {
        List<LineElements> topicsWithTopTopic = lineElementsComponent.parseText(topTopics);
        for (LineElements elements : topicsWithTopTopic) {
            List<List<String>> sections = elements.getSections();
            if (!sections.isEmpty() && sections.size() > 1) {
                TopTopicEntity topTopic = getTopTopicEntityForName(sections.getFirst().getFirst());
                List<String> topicsToLink = sections.get(0);
                for (TopicEntity topicEntity : this.topics) {
                    for (String top : topicsToLink) {
                        if (topicEntity.getTopicName().equals(top)) {
                            topTopic.addTopic(topicEntity);
                        }
                    }
                }
            }
        }
    }

    public void createTimeEntities(String timeMessage) {
        List<LineElements> timeLines = lineElementsComponent.parseText(timeMessage);
        for (LineElements timeLine : timeLines) {
            List<List<String>> sections = timeLine.getSections();
            if (!sections.isEmpty() && sections.size() > 2) {
                String timeDescription = sections.getFirst().getFirst();
                System.out.println("Parsing time: " + timeDescription);

                List<String> lineOccurrences = sections.get(2);
                List<DateTime> timeRangeForTimeDescription = getTimeRangeForTimeDescription(timeDescription);
                if (timeRangeForTimeDescription != null && timeRangeForTimeDescription.size() > 1) {
                    for (String lineNumber : lineOccurrences) {
                        if (isNumeric(lineNumber)) {
                            int number = Integer.parseInt(lineNumber) + 1;
                            if (this.memories.size() > number) {
                                MemoryEntity memoryEntity = this.memories.get(number);
                                memoryEntity.setStartTime(timeRangeForTimeDescription.get(0));
                                memoryEntity.setEndTime(timeRangeForTimeDescription.get(1));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    private List<DateTime> getTimeRangeForTimeDescription(String timeDescription) {
        HawkingTimeParser parser = new HawkingTimeParser();
        DatesFound datesFound = parser.parse(timeDescription, new Date(), new HawkingConfiguration(), "eng");
        List<ParserOutput> parserOutputs = datesFound.getParserOutputs();
        System.out.println(datesFound);

        if (!parserOutputs.isEmpty()) {
            List<DateTime> timeRange = new ArrayList<>();
            DateRange dateRange = parserOutputs.getFirst().getDateRange();

            DateTime startDateTime = dateRange.getStart();
            timeRange.add(startDateTime);

            DateTime endDateTime = dateRange.getEnd();
            timeRange.add(endDateTime);

            return timeRange;
        } else {
            return null;
        }
    }

    public void createPeopleEntities(String peopleMessage) {
        List<LineElements> lineElements = lineElementsComponent.parseText(peopleMessage);
        List<String> aliases = lineElementsComponent.extractSections(List.of(2), lineElements);
        List<List<String>> groupedAliases = groupSimilarAliases(aliases);

        for (List<String> aliasGroup : groupedAliases) {
            // Try to find existing person with any of these aliases
            Optional<PersonEntity> existingPerson = people.stream()
                    .filter(person -> person.getAliases().stream()
                            .anyMatch(existingAlias -> aliasGroup.stream()
                                    .anyMatch(newAlias ->
                                            existingAlias.contains(newAlias) ||
                                                    newAlias.contains(existingAlias))))
                    .findFirst();

            if (existingPerson.isPresent()) {
                // Add new aliases to existing person
                existingPerson.get().addAliases(aliasGroup);
            } else {
                // Create new person
                PersonEntity newPerson = new PersonEntity();
                newPerson.setAliases(new ArrayList<>(aliasGroup));
                this.people.add(newPerson);
            }
        }
    }

    private List<List<String>> groupSimilarAliases(List<String> aliases) {
        List<List<String>> groups = new ArrayList<>();
        Set<String> processedAliases = new HashSet<>();

        for (String alias : aliases) {
            if (processedAliases.contains(alias)) {
                continue;
            }

            List<String> group = new ArrayList<>();
            group.add(alias);
            processedAliases.add(alias);

            for (String otherAlias : aliases) {
                if (!processedAliases.contains(otherAlias) &&
                        (alias.contains(otherAlias) || otherAlias.contains(alias))) {
                    group.add(otherAlias);
                    processedAliases.add(otherAlias);
                }
            }
            groups.add(group);
        }
        return groups;
    }

    private Set<PersonEntity> getPersonEntitiesForSection(List<String> aliases) {
        return this.people.stream()
                .filter(personEntity -> aliases.stream()
                        .anyMatch(foundAlias -> personEntity.getAliases().stream()
                                .anyMatch(foundAlias::contains)))
                .collect(Collectors.toSet());
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

    private TopicEntity getTopicEntityForName(String topic) {
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
}
