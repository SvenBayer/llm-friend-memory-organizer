package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.model.MemoryCategory;
import de.svenbayer.llm_friend_memory_organizer.service.process.OllamaProcessService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DeepMemoryService {

    private final DeepMemorySystemPromptService promptService;
    private final ChatClient chatClient;
    private final OllamaProcessService ollamaProcessService;

    public DeepMemoryService(DeepMemorySystemPromptService promptService, ChatClient.Builder chatClientBuilder, OllamaProcessService ollamaProcessService) {
        this.promptService = promptService;
        this.chatClient = chatClientBuilder.build();
        this.ollamaProcessService = ollamaProcessService;
    }

    public void memorizeMessage(String userMessage) {
        for (String sentence : splitIntoSentences(userMessage)) {
            boolean storeMessage = isStoreMessage(sentence);
            if (storeMessage) {
                System.out.println("Message found '" + sentence);
            }
        }
        ollamaProcessService.stopOllamaContainer();
    }

    private boolean isStoreMessage(String userMessage) {
        System.out.println("User message: " + userMessage);
        Prompt informationRelevantPrompt = promptService.getInformationRelevantPrompt(userMessage);

        String storeMessage = chatClient.prompt()
                .user(informationRelevantPrompt.getContents())
                .call()
                .content();

        String decisionMessage;
        if (storeMessage.contains("</think>")) {
            decisionMessage = storeMessage.substring(storeMessage.lastIndexOf("</think>") + 8);
        } else {
            decisionMessage = storeMessage;
        }
        System.out.println("Decision: " + decisionMessage);
        if (decisionMessage.contains("<YES>")) {
            return true;
        } else if (decisionMessage.contains("<NO>")) {
            return false;
        }
        return false;
        //throw new IllegalStateException("No decision could be made: " + decisionMessage);
    }

    private List<String> splitIntoSentences(String userMessage) {
        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(userMessage);
        int start = iterator.first();
        int end = iterator.next();

        while (end != BreakIterator.DONE) {
            String sentence = userMessage.substring(start, end).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
            start = end;
            end = iterator.next();
        }
        return sentences;
    }
}
