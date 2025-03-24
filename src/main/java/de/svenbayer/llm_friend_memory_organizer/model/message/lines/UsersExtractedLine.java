package de.svenbayer.llm_friend_memory_organizer.model.message.lines;

import lombok.Data;

import java.util.List;

@Data
public class UsersExtractedLine {

    private final List<String> users;
}
