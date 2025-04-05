package de.svenbayer.llm_friend_memory_organizer.service;

import de.svenbayer.llm_friend_memory_organizer.model.message.RelevantMemories;
import de.svenbayer.llm_friend_memory_organizer.model.message.UserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DeepMemoryServiceTest {

    @Autowired
    private DeepMemoryService deepMemoryService;

    @Test
    public void fillUpDatabase() {
        if (false) {
            return;
        }
        String userMessage = "Hello, my name is Sven. I work as a GenAI Expert and I like taking videos with my camera.\n" +
                "I am still studying Story Telling so I can present better. I also study Japanese so I can talk to the locals there when traveling. It's really fun.\n" +
                "My friend Sophie told me about her YouTube channel. She does videos of landscapes as travel videos. But she does not get many views.\n" +
                "I was in Japan last year in October and had a great experience. There was so much to see. Yesterday, I told Sophie about it and she really wants to visit it, especially Osaka.\n" +
                "I took some videos in my home town, but they are boring.";
        deepMemoryService.memorizeMessage(userMessage);

        userMessage = "We went once for lunch in Munich. It is very expensive now to eat out. The city is also very busy. We were thinking of going to Munich this weekend, but I like shopping online more. Also, my wife can cook really good burgers so there is no need for going out.";
        deepMemoryService.memorizeMessage(userMessage);

        userMessage = "Sophie likes to do landscape videos. Sopie also likes to take videos of cities when she travels.";
        deepMemoryService.memorizeMessage(userMessage);

        userMessage = "Sophie likes to film landscapes. Sopie also likes to take videos of towns when she travels.";
        deepMemoryService.memorizeMessage(userMessage);

        userMessage = "My wife, Petra, is currently sick. We went shopping to Rewe yesterday. We listened to some metal in the car. Then, my wife was baking a cake and Sophie's friend Anna came over.";
        deepMemoryService.memorizeMessage(userMessage);

        userMessage = "I want to go swimming today. My wife eats some bun with ham and cheese. She came up with this recipe and it is really delicious.";
        deepMemoryService.memorizeMessage(userMessage);

        userMessage = "Hello, my name is Sven, how are you? I am a bit tired since I got sick today. I was working from home. I was talking a lot with my boss. After that, I continued to program my project on my PC with my RTX 4080 graphics card.";
        deepMemoryService.memorizeMessage(userMessage);

        userMessage = "Hello, my name is Sven, how are you? I am a bit tired since I got sick today. I was working from home. I was talking a lot with my boss. After that, I continued to program my project on my PC with my RTX 4080 graphics card.";
        deepMemoryService.memorizeMessage(userMessage);
    }

    @Test
    void memorizeMessage() {
        if (false) {
            return;
        }
        String userMessage = "Hello, my name is Sven. I work as a GenAI Expert and I like taking videos with my camera.\n" +
                "I am still studying Story Telling so I can present better. I also study Japanese so I can talk to the locals there when traveling. It's really fun.\n" +
                "My friend Sophie told me about her YouTube channel. She does videos of landscapes as travel videos. But she does not get many views.\n" +
                "I was in Japan last year in October and had a great experience. There was so much to see. Yesterday, I told Sophie about it and she really wants to visit it, especially Osaka.\n" +
                "I took some videos in my home town, but they are boring.";
        RelevantMemories relevantMemories = deepMemoryService.memorizeMessage(userMessage);
        System.out.println("Relevant Memories:\n" + relevantMemories);
    }
}