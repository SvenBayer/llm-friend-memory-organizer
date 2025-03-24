package de.svenbayer.llm_friend_memory_organizer.service.message;

import de.svenbayer.llm_friend_memory_organizer.model.message.EnrichedMessage;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.InformationExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.UsersExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.service.llm.MessageExtractorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class MessageExtractorServiceTest {

    @Autowired
    private MessageExtractorService messageExtractorService;

    @Test
    void extractCategorizedWithUserMessage() {
        EnrichedMessage enrichedMessage = new EnrichedMessage();
        List<InformationExtractedLine> lines = new ArrayList<>();
        lines.add(new InformationExtractedLine("The user's name is Sven."));
        lines.add(new InformationExtractedLine("The user asked about the recipient's state of being."));
        lines.add(new InformationExtractedLine("The user mentioned feeling tired."));
        lines.add(new InformationExtractedLine("The user got sick today."));
        lines.add(new InformationExtractedLine("The user was working from home."));
        lines.add(new InformationExtractedLine("The user talked a lot with their boss."));
        lines.add(new InformationExtractedLine("The user has a PC."));
        lines.add(new InformationExtractedLine("The user used their RTX 4080 graphics card to program their project."));
        lines.add(new InformationExtractedLine("Sven and his boss have a professional relationship (boss/employee)"));
        enrichedMessage.setInformationExtractedLines(lines);

        List<UsersExtractedLine> usersExtractedLines = messageExtractorService.extractCategorizedWithUserMessage(enrichedMessage);
        String result = usersExtractedLines.stream()
                .map(UsersExtractedLine::getUsers)
                .map(users -> String.join(", ", users))
                .collect(Collectors.joining("\n"));
        System.out.println(result);
    }
}