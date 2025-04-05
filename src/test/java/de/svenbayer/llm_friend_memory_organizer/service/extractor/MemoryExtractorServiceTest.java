package de.svenbayer.llm_friend_memory_organizer.service.extractor;

import de.svenbayer.llm_friend_memory_organizer.model.message.RelevantMemories;
import de.svenbayer.llm_friend_memory_organizer.model.message.UserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemoryExtractorServiceTest {

    @Autowired
    private MemoryExtractorService memoryExtractorService;

    @Test
    void extractRelevantInformationForUserMessage() {
        UserMessage userMessage = new UserMessage("Hello, my name is Sven, how are you? I am a bit tired since I got sick today. I was working from home. I was talking a lot with my boss. After that, I continued to program my project on my PC with my RTX 4080 graphics card.");
        RelevantMemories relevantMemories = memoryExtractorService.extractRelevantInformationForUserMessage(userMessage);
        System.out.println("Relevant Memories:\n" + relevantMemories.toString());
    }
}