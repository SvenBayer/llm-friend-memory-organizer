package de.svenbayer.llm_friend_memory_organizer.controller;

import de.svenbayer.llm_friend_memory_organizer.model.message.RelevantMemories;
import de.svenbayer.llm_friend_memory_organizer.service.DeepMemoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeepMemoryController {

    private final DeepMemoryService deepMemoryService;

    public DeepMemoryController(DeepMemoryService deepMemoryService) {
        this.deepMemoryService = deepMemoryService;
    }

    @PostMapping("/memorizeMessage")
    public RelevantMemories memorizeMessage(@RequestBody String userMessage) {
        return deepMemoryService.memorizeMessage(userMessage);
    }
}
