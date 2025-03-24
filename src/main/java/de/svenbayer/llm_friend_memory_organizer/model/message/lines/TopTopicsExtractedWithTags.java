package de.svenbayer.llm_friend_memory_organizer.model.message.lines;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class TopTopicsExtractedWithTags {

    private Map<String, Set<String>> topTopicsWithTags = new HashMap<>();
}
