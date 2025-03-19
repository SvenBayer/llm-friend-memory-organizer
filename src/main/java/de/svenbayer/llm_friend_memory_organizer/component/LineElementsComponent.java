package de.svenbayer.llm_friend_memory_organizer.component;

import de.svenbayer.llm_friend_memory_organizer.model.LineElements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class LineElementsComponent {

    public List<LineElements> parseText(String text) {
        List<LineElements> result = new ArrayList<>();

        String[] lines = text.split("\n");
        for (String line : lines) {
            // Skip empty lines
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }

            // Remove number prefix if present
            if (trimmedLine.matches("^\\d{1,2}\\..*")) {
                trimmedLine = trimmedLine.substring(trimmedLine.indexOf('.') + 1).trim();
            }

            // Parse sections and elements
            LineElements lineElements = new LineElements();
            String[] sections = trimmedLine.split(";");
            for (String section : sections) {
                lineElements.addSection(section);
            }
            result.add(lineElements);
        }
        return result;
    }


    public String appendEnumerationList(String firstList, String secondList) {
        if (firstList == null || firstList.isEmpty()) {
            return secondList;
        }
        if (secondList == null || secondList.isEmpty()) {
            return firstList;
        }

        // Find the last number in the first list
        String[] firstLines = firstList.split("\n");
        String lastLine = firstLines[firstLines.length - 1].trim();
        int lastNumber = Integer.parseInt(lastLine.split("\\.")[0]);

        // Update numbers in the second list
        String[] secondLines = secondList.split("\n");
        StringBuilder result = new StringBuilder(firstList);

        for (int i = 0; i < secondLines.length; i++) {
            String line = secondLines[i].trim();
            if (line.isEmpty()) continue;

            String content = line.substring(line.indexOf(".") + 1).trim();
            result.append("\n").append(lastNumber + i + 1).append(". ").append(content);
        }

        return result.toString();
    }

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