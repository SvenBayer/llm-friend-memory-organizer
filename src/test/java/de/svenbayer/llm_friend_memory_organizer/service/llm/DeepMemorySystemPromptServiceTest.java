package de.svenbayer.llm_friend_memory_organizer.service.llm;

import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.UserMessage;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.InformationExtractedLine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DeepMemorySystemPromptServiceTest {

    @Autowired
    private LlmMessageProcessor llmMessageProcessor;

    @Autowired
    private DeepMemorySystemPromptService promptService;

    private static final List<MemoryEntity> MEMORIES = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
        MemoryEntity m1 = new MemoryEntity();
        m1.setEmbeddingText("The user's wife cooked a Japanese dish.");
        MEMORIES.add(m1);

        MemoryEntity m2 = new MemoryEntity();
        m2.setEmbeddingText("The user likes his wife's cooking");
        MEMORIES.add(m2);

        MemoryEntity m3 = new MemoryEntity();
        m3.setEmbeddingText("The wife Sophie likes to invite her friends over for dinner.");
        MEMORIES.add(m3);
    }

    @Test
    void getExtractInformationPrompt() {
        UserMessage userMessage = new UserMessage("I went to Japan, to Osaka last year in October.");
        Prompt prompt = promptService.getExtractInformationPrompt(userMessage);
        String message = llmMessageProcessor.processMessage(prompt);
        System.out.println(message);
    }

    @Test
    void getExtractTimePrompt() {
        List<InformationExtractedLine> informationExtractedLines = new ArrayList<>();
        informationExtractedLines.add(new InformationExtractedLine("The user went to Osaka."));
        informationExtractedLines.add(new InformationExtractedLine("The user went to Osaka last year."));
        informationExtractedLines.add(new InformationExtractedLine("The user went to Osaka in October."));
        Prompt prompt = promptService.getExtractTimePrompt(informationExtractedLines);
        String message = llmMessageProcessor.processMessage(prompt);
        System.out.println(message);
    }

    @Test
    void getSuggestionPromptForMemories() {
        Prompt prompt = promptService.getSuggestionPromptForMemories(MEMORIES, 3);
        String message = llmMessageProcessor.processMessage(prompt);
        System.out.println(message);
    }

    @Test
    void getAssumptionPromptForMemories() {
        Prompt prompt = promptService.getAssumptionPromptForMemories(MEMORIES, 3);
        String message = llmMessageProcessor.processMessage(prompt);
        System.out.println(message);
    }
}