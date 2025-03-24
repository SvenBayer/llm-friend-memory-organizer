package de.svenbayer.llm_friend_memory_organizer.service.llm;

import de.svenbayer.llm_friend_memory_organizer.service.llm.filter.LlmMessageFilter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LlmMessageProcessor {

    private final ChatClient chatClient;
    private final List<LlmMessageFilter> llmMessageFilters;

    public LlmMessageProcessor(ChatClient.Builder chatClientBuilder, List<LlmMessageFilter> llmMessageFilters) {
        this.chatClient = chatClientBuilder.build();
        this.llmMessageFilters = llmMessageFilters;
    }

    public String processMessage(Prompt informationRelevantPrompt) {
        String response = chatClient.prompt()
                .user(informationRelevantPrompt.getContents())
                .call()
                .content();

        return llmMessageFilters.stream()
                .reduce(response,
                        (message, filter) -> filter.filter(message),
                        (m1, m2) -> m2);
    }
}
