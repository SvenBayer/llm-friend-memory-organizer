package de.svenbayer.llm_friend_memory_organizer.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LineElements {

    private static final String SECTION_SEPARATOR = "\\|";

    private List<List<String>> sections = new ArrayList<>();

    public void addSection(String section) {
        List<String> elements = Arrays.stream(section.split(SECTION_SEPARATOR))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        sections.add(elements);
    }
}
