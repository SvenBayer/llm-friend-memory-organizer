package de.svenbayer.llm_friend_memory_organizer.service.llm;

import de.svenbayer.llm_friend_memory_organizer.component.LineElementsComponent;
import de.svenbayer.llm_friend_memory_organizer.model.LineElements;
import de.svenbayer.llm_friend_memory_organizer.model.message.*;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public Map<InformationExtractedLine, List<TagsExtractedLine>> extractTaggedMessage(EnrichedMessage enrichedMessage) {
        Prompt taggedInformationPrompt = promptService.getTagInformationPrompt(enrichedMessage.getInformationExtractedLines());
        String answer = llmMessageProcessor.processMessage(taggedInformationPrompt);

        System.out.println("Tagged Information:\n" + answer);
        return extractLinesWithMapping(answer, InformationExtractedLine::new, TagsExtractedLine::new);
    }

    public Map<InformationExtractedLine, List<CategoriesExtractedLine>> extractCategorizedMessage(EnrichedMessage enrichedMessage) {
        Prompt categorizeInformationPrompt = promptService.getCategorizeInformationPrompt(enrichedMessage.getInformationExtractedLines());
        String answer = llmMessageProcessor.processMessage(categorizeInformationPrompt);
        System.out.println("Categorized Information:\n" + answer);

        return extractLinesWithMapping(answer, InformationExtractedLine::new, CategoriesExtractedLine::new);
    }

    public List<InformationExtractedLine> extractRelationshipMessage(UserMessage userMessage) {
        Prompt relationshipConclusionPrompt = promptService.getRelationshipConclusionPrompt(userMessage);
        String answer = llmMessageProcessor.processMessage(relationshipConclusionPrompt);

        System.out.println("Extracted Relationships:\n" + answer);
        return getInformationExtractedLines(answer);
    }

    public Map<InformationExtractedLine, List<UsersExtractedLine>> extractCategorizedWithUserMessage(EnrichedMessage enrichedMessage) {
        Prompt categorizedWithUserPrompt = promptService.getExtractUserPromptTemplate(enrichedMessage.getInformationExtractedLines());
        String answer = llmMessageProcessor.processMessage(categorizedWithUserPrompt);

        System.out.println("Extracted Categorized with users:\n" + answer);
        return extractLinesWithMapping(answer, InformationExtractedLine::new, UsersExtractedLine::new);
    }

    public TimeExtractedIndexes extractTimeIndexesMessage(EnrichedMessage enrichedMessage) {
        Prompt extractTimePrompt = promptService.getExtractTimePrompt(enrichedMessage.getInformationExtractedLines());
        String answer = llmMessageProcessor.processMessage(extractTimePrompt);
        System.out.println("Times:\n" + answer);
        return getTimeExtractedIndexes(answer);
    }

    public TopTopicsExtractedWithTags extractTopicMessage(EnrichedMessage enrichedMessage) {
        Prompt extractTopTopicsPrompt = promptService.getTopTopicsPrompt(enrichedMessage.getTagsExtractedLine());
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

    private <K, T> Map<K, List<T>> extractLinesWithMapping(String answer, Function<String, K> keyConstructor, Function<String, T> valueConstructor) {
        Map<K, List<T>> result = new HashMap<>();

        List<LineElements> lineElements = lineElementsComponent.parseText(answer);
        for (LineElements elements : lineElements) {
            List<T> values = new ArrayList<>();
            List<List<String>> sections = elements.getSections();
            if (!sections.isEmpty() && sections.size() > 1) {
                List<String> outputDataSection = sections.get(1);
                if (sections.size() > 2) {
                    outputDataSection.addAll(sections.get(2));
                }
                for (String entry : outputDataSection) {
                    values.add(valueConstructor.apply(entry));
                }
                K key = keyConstructor.apply(sections.getFirst().getFirst());
                result.put(key, values);
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
