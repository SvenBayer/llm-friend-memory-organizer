package de.svenbayer.llm_friend_memory_organizer.service.entity;

import com.zoho.hawking.HawkingTimeParser;
import com.zoho.hawking.datetimeparser.configuration.HawkingConfiguration;
import com.zoho.hawking.language.english.model.DateRange;
import com.zoho.hawking.language.english.model.DatesFound;
import com.zoho.hawking.language.english.model.ParserOutput;
import de.svenbayer.llm_friend_memory_organizer.model.entity.MemoryEntity;
import de.svenbayer.llm_friend_memory_organizer.model.message.EnrichedMessage;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.InformationExtractedLine;
import de.svenbayer.llm_friend_memory_organizer.model.message.lines.TimeExtractedIndexes;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class EntityTimeService {

    private final MemoryEntityService memoryEntityService;

    public EntityTimeService(MemoryEntityService memoryEntityService) {
        this.memoryEntityService = memoryEntityService;
    }

    protected void setTimeForEntities(EnrichedMessage enrichedMessage) {
        TimeExtractedIndexes timeExtractedIndexes = enrichedMessage.getTimeExtractedIndexes();

        for (Map.Entry<String, List<Integer>> entry : timeExtractedIndexes.getTimeIndexes().entrySet()) {
            String timeDescription = entry.getKey();
            System.out.println("Parsing time: " + timeDescription);

            List<DateTime> timeRangeForTimeDescription = getTimeRangeForTimeDescription(timeDescription);
            if (timeRangeForTimeDescription != null && timeRangeForTimeDescription.size() > 1) {
//                if (timeRangeForTimeDescription.get(0).isAfterNow()) {
//                    Integer numberLine = entry.getValue().get(0);
//                    InformationExtractedLine lineWithTime = enrichedMessage.getInformationExtractedLines().get(numberLine - 1);
//                    timeRangeForTimeDescription = getTimeRangeForTimeDescription(lineWithTime.getLine());
//                }
                for (int lineNumber : entry.getValue()) {
                    List<MemoryEntity> memories = memoryEntityService.getMemories();
                    if (memories.size() > lineNumber && lineNumber >= 0) {
                        InformationExtractedLine informationExtractedLine = enrichedMessage.getInformationExtractedLines().get(lineNumber);
                        Optional<MemoryEntity> foundMemory = memories.stream()
                                .filter(mem -> mem.getEmbeddingText().equals(informationExtractedLine.getLine()))
                                .findFirst();
                        if (foundMemory.isPresent()) {
                            MemoryEntity memoryEntity = foundMemory.get();
                            memoryEntity.setStartTime(timeRangeForTimeDescription.get(0));
                            memoryEntity.setEndTime(timeRangeForTimeDescription.get(1));
                        }
                    }
                }
            }
        }
    }

    private List<DateTime> getTimeRangeForTimeDescription(String timeDescription) {
        HawkingTimeParser parser = new HawkingTimeParser();
        DatesFound datesFound = parser.parse(timeDescription, new Date(), new HawkingConfiguration(), "eng");
        List<ParserOutput> parserOutputs = datesFound.getParserOutputs();

        if (!parserOutputs.isEmpty()) {
            List<DateTime> timeRange = new ArrayList<>();
            DateRange dateRange = parserOutputs.getFirst().getDateRange();

            DateTime startDateTime = dateRange.getStart();
            timeRange.add(startDateTime);

            DateTime endDateTime = dateRange.getEnd();
            timeRange.add(endDateTime);

            return timeRange;
        } else {
            return null;
        }
    }
}
