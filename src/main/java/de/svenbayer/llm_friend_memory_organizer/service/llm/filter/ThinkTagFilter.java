package de.svenbayer.llm_friend_memory_organizer.service.llm.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(1)
public class ThinkTagFilter implements LlmMessageFilter {
    @Override
    public String filter(String message) {
        if (message.contains("</think>")) {
            return message.substring(message.lastIndexOf("</think>") + 8);
        }
        return message;
    }
}
