package de.svenbayer.llm_friend_memory_organizer.service.llm.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(2)
public class BreakdownIntroLineFilter implements LlmMessageFilter {

    @Override
    public String filter(String message) {
        return message.lines()
                .filter(line -> !line.contains("Here's a breakdown of the user's message"))
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);
    }
}
