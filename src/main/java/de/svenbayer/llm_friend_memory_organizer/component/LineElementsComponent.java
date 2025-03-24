package de.svenbayer.llm_friend_memory_organizer.component;

import de.svenbayer.llm_friend_memory_organizer.model.LineElements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LineElementsComponent {

    public List<String> parseToLines(String text) {
        return Stream.of(text.split("\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(line -> {
                    if (line.matches("^\\d{1,2}\\.\\s+.*")) {
                        return line.replaceFirst("^\\d{1,2}\\.\\s+", "");
                    }
                    return line;
                })
                .collect(Collectors.toList());
    }

    public List<LineElements> parseText(String text) {
        List<LineElements> result = new ArrayList<>();

        List<String> lines = parseToLines(text);

        for (String line : lines) {
            // Parse sections and elements
            LineElements lineElements = new LineElements();
            String[] sections = line.split(";");
            for (String section : sections) {
                lineElements.addSection(section);
            }
            result.add(lineElements);
        }
        return result;
    }

    @Deprecated
    public List<String> extractSections(List<Integer> sectionIndexes, List<LineElements> lineElements) {
        return lineElements.stream()
                .map(LineElements::getSections)
                .filter(section -> !section.isEmpty())
                .map(section -> {
                    List<String> result = new ArrayList<>();
                    for (int index: sectionIndexes) {
                        if (section.size() > index) {
                            result.addAll(section.get(index));
                        }
                    }
                    return result;
                })
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }
}