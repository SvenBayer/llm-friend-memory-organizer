package de.svenbayer.llm_friend_memory_organizer.controller;

import de.svenbayer.llm_friend_memory_organizer.model.message.RelevantMemories;
import de.svenbayer.llm_friend_memory_organizer.service.DeepMemoryService;
import de.svenbayer.llm_friend_memory_organizer.service.SimpleMemoryFinderService;
import de.svenbayer.llm_friend_memory_organizer.service.extractor.MemoryExtractorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeepMemoryController {

    private final DeepMemoryService deepMemoryService;
    private final SimpleMemoryFinderService simpleMemoryFinderService;

    public DeepMemoryController(DeepMemoryService deepMemoryService, SimpleMemoryFinderService simpleMemoryFinderService) {
        this.deepMemoryService = deepMemoryService;
        this.simpleMemoryFinderService = simpleMemoryFinderService;
    }

    @PostMapping("/memorizeMessage")
    public RelevantMemories memorizeMessage(@RequestBody String userMessage) {
        return deepMemoryService.memorizeMessage(userMessage);
    }

    @PostMapping("/findInformation")
    public String findInformation(@RequestBody String description) {
        return simpleMemoryFinderService.findRelevantMemories(description);
    }
}
