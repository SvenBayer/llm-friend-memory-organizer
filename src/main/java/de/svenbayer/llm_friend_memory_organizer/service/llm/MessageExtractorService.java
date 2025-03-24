package de.svenbayer.llm_friend_memory_organizer.service.llm;

import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
import de.svenbayer.llm_friend_memory_organizer.model.LineElements;
import de.svenbayer.llm_friend_memory_organizer.model.message.*;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
public class MessageExtractorService {

    private final DeepMemorySystemPromptService promptService;
    private final LlmMessageProcessor llmMessageProcessor;
    private final LineElementsComponent lineElementsComponent;

    public MessageExtractorService(DeepMemorySystemPromptService promptService, LlmMessageProcessor llmMessageProcessor, LineElementsComponent lineElementsComponent) {
        this.promptService = promptService;
        this.llmMessageProcessor = llmMessageProcessor;
        this.lineElementsComponent = lineElementsComponent;
    }

    public List<InformationExtractedLine> extractInformationLines(UserMessage userMessage) {
        Prompt informationRelevantPrompt = promptService.getExtractInformationPrompt(userMessage);
        String answer = llmMessageProcessor.processMessage(informationRelevantPrompt);

        System.out.println("Extracted Information:\n" + answer);
        return getInformationExtractedLines(answer);
    }

    public List<TagsExtractedLine> extractTaggedMessage(EnrichedMessage enrichedMessage) {
        String enumerationWithRelationshipsMessage = enrichedMessage.getInformationExtractedLinesWithNumbering();
        Prompt taggedInformationPrompt = promptService.getTagInformationPrompt(enumerationWithRelationshipsMessage);
        String answer = llmMessageProcessor.processMessage(taggedInformationPrompt);

        System.out.println("Tagged Information:\n" + answer);
        return extractLinesWithSections(answer, TagsExtractedLine::new);
    }

    public List<CategoriesExtractedLine> extractCategorizedMessage(EnrichedMessage enrichedMessage) {
        String enumerationWithRelationshipsMessage = enrichedMessage.getInformationExtractedLinesWithNumbering();
        Prompt categorizeInformationPrompt = promptService.getCategorizeInformationPrompt(enumerationWithRelationshipsMessage);
        String answer = llmMessageProcessor.processMessage(categorizeInformationPrompt);
        System.out.println("Categorized Information:\n" + answer);

        return extractLinesWithSections(answer, CategoriesExtractedLine::new);
    }

    public List<InformationExtractedLine> extractRelationshipMessage(UserMessage userMessage) {
        Prompt relationshipConclusionPrompt = promptService.getRelationshipConclusionPrompt(userMessage);
        String answer = llmMessageProcessor.processMessage(relationshipConclusionPrompt);

        System.out.println("Extracted Relationships:\n" + answer);
        return getInformationExtractedLines(answer);
    }

    public List<UsersExtractedLine> extractCategorizedWithUserMessage(EnrichedMessage enrichedMessage) {
        String informationExtractedLinesWithNumbering = enrichedMessage.getInformationExtractedLinesWithNumbering();
        Prompt categorizedWithUserPrompt = promptService.getExtractUserPromptTemplate(informationExtractedLinesWithNumbering);
        String answer = llmMessageProcessor.processMessage(categorizedWithUserPrompt);

        System.out.println("Extracted Categorized with users:\n" + answer);
        return extractLinesWithSections(answer, UsersExtractedLine::new);
    }

    public TimeExtractedIndexes extractTimeIndexesMessage(EnrichedMessage enrichedMessage) {
        String informationExtractedLinesWithNumbering = enrichedMessage.getInformationExtractedLinesWithNumbering();
        Prompt extractTimePrompt = promptService.getExtractTimePrompt(informationExtractedLinesWithNumbering);
        String answer = llmMessageProcessor.processMessage(extractTimePrompt);
        System.out.println("Times:\n" + answer);
        return getTimeExtractedIndexes(answer);
    }

    public TopTopicsExtractedWithTags extractTopicMessage(EnrichedMessage enrichedMessage) {
        String tagsAsLines = enrichedMessage.getTagsAsLines();
        Prompt extractTopTopicsPrompt = promptService.getTopTopicsPrompt(tagsAsLines);
        String answer = llmMessageProcessor.processMessage(extractTopTopicsPrompt);

        System.out.println("Top Topics:\n" + answer);
        return extractTopTopicsWithTags(answer);
    }

    private List<InformationExtractedLine> getInformationExtractedLines(String answer) {
        List<String> lineElements = lineElementsComponent.parseToLines(answer);

        return lineElements.stream()
                .map(InformationExtractedLine::new)
                .toList();
    }

    private <T> List<T> extractLinesWithSections(String answer, Function<List<String>, T> lineConstructor) {
        List<LineElements> lineElements = lineElementsComponent.parseText(answer);
        List<T> result = new ArrayList<>();
        for (LineElements elements : lineElements) {
            List<List<String>> sections = elements.getSections();
            if (!sections.isEmpty() && sections.size() > 1) {
                List<String> outputDataSection = sections.get(1);
                if (sections.size() > 2) {
                    outputDataSection.addAll(sections.get(2));
                }
                result.add(lineConstructor.apply(outputDataSection));
            }
        }
        return result;
    }

    private TopTopicsExtractedWithTags extractTopTopicsWithTags(String answer) {
        TopTopicsExtractedWithTags topTopicsWithTags = new TopTopicsExtractedWithTags();
        List<LineElements> topicsWithTopTopic = lineElementsComponent.parseText(answer);
        for (LineElements elements : topicsWithTopTopic) {
            List<List<String>> sections = elements.getSections();
            if (!sections.isEmpty() && sections.size() > 1) {
                String topTopicName = sections.get(1).getFirst();
                List<String> first = sections.getFirst();
                Set<String> tags = new HashSet<>(first);
                topTopicsWithTags.getTopTopicsWithTags().put(topTopicName, tags);
            }
        }
        return topTopicsWithTags;
    }

    private TimeExtractedIndexes getTimeExtractedIndexes(String answer) {
        List<LineElements> lineElements = lineElementsComponent.parseText(answer);
        TimeExtractedIndexes result = new TimeExtractedIndexes();
        for (LineElements elements : lineElements) {
            List<List<String>> sections = elements.getSections();
            if (!sections.isEmpty() && sections.size() > 1) {
                String timeIndicatorName = sections.getFirst().getFirst();
                List<String> lines = sections.get(1);
                List<Integer> linesResult = new ArrayList<>();
                for (String line : lines) {
                    if (isNumeric(line)) {
                        Integer index = Integer.valueOf(line);
                        linesResult.add(index);
                    }
                }
                result.getTimeIndexes().put(timeIndicatorName, linesResult);
            }
        }
        return result;
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
}
