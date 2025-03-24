package de.svenbayer.llm_friend_memory_organizer.model.message.lines;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TimeExtractedIndexes {

    private final Map<String, List<Integer>> timeIndexes = new HashMap<>();
}
