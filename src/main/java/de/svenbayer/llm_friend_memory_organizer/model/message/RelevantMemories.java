package de.svenbayer.llm_friend_memory_organizer.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
public class RelevantMemories {

    private String memoryText;
    private String assumptionText;
    private String suggestionText;
}
