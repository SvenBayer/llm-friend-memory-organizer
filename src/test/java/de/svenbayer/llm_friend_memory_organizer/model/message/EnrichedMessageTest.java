package de.svenbayer.llm_friend_memory_organizer.model.message;

import de.svenbayer.llm_friend_memory_organizer.model.message.lines.InformationExtractedLine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnrichedMessageTest {

    @Test
    void getInformationExtractedLinesWithNumbering() {
        List<InformationExtractedLine> informationExtractedLines = List.of(
                new InformationExtractedLine("The user's name is Sven.; Personal Information; the user (Sven)"),
                new InformationExtractedLine("The user works as a GenAI Expert.; Activity| Asset; the user (Sven)"),
                new InformationExtractedLine("The user likes taking videos with his camera.; Activity| Preference; the user (Sven)"));

        EnrichedMessage enrichedMessage = new EnrichedMessage();
        enrichedMessage.setInformationExtractedLines(informationExtractedLines);

        String informationExtractedLinesWithNumbering = enrichedMessage.getInformationExtractedLinesWithNumbering();

        String expected = "1. The user's name is Sven.; Personal Information; the user (Sven)\n" +
                "  2. The user works as a GenAI Expert.; Activity| Asset; the user (Sven)\n" +
                "  3. The user likes taking videos with his camera.; Activity| Preference; the user (Sven)";

        assertEquals(expected, informationExtractedLinesWithNumbering);
    }
}