package de.svenbayer.llm_friend_memory_organizer.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;
import java.util.UUID;

@Node("Memory")
@Data
public class MemoryEntity {

    @Id
    private String embeddingText = "";
    private float[] embedding = new float[0];
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public MemoryEntity() {
        DateTime startOfDay = new DateTime().withTimeAtStartOfDay();
        DateTime endOfDay = new DateTime().withTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59);

        setStartTime(startOfDay);
        setEndTime(endOfDay);
    }

    public void setStartTime(org.joda.time.DateTime dateTime) {
        this.startTime = LocalDateTime.of(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(),
                dateTime.getHourOfDay(),
                dateTime.getMinuteOfHour(),
                dateTime.getSecondOfMinute()
        );
    }

    public void setEndTime(org.joda.time.DateTime dateTime) {
        this.endTime = LocalDateTime.of(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(),
                dateTime.getHourOfDay(),
                dateTime.getMinuteOfHour(),
                dateTime.getSecondOfMinute()
        );
    }
}
