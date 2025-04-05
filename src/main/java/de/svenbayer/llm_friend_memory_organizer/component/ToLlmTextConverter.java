package de.svenbayer.llm_friend_memory_organizer.component;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ToLlmTextConverter {

    public <T> String getNumberedListForList(List<T> list, Function<T, String> converter) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            String line = converter.apply(element);
            result.append(i + 1).append(". ").append(line);
            if (i < list.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    public <K, V> String getPipedListWithKeyLines(Map<K, List<V>> mapOfList, Function<V, String> converter) {
        return mapOfList.values().stream()
                .flatMap(List::stream)
                .map(line -> String.join("|", converter.apply(line)))
                .collect(Collectors.joining("\n"));
    }
}
