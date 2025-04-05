package de.svenbayer.llm_friend_memory_organizer.service.entity;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntityTimeServiceTest {

    @Autowired
    private EntityTimeService entityTimeService;

    @Test
    void getTimeRangeForTimeDescription() {
        List<DateTime> timeRangeForTimeDescription = entityTimeService.getTimeRangeForTimeDescription("last year in October");
        assertNotNull(timeRangeForTimeDescription);
        assertEquals(2, timeRangeForTimeDescription.size());

        int currentYear = DateTime.now().getYear();
        assertEquals(new DateTime().withYear(currentYear - 1).withMonthOfYear(10).withDayOfMonth(1), timeRangeForTimeDescription.get(0));
        assertEquals(new DateTime().withYear(currentYear - 1).withMonthOfYear(10).withDayOfMonth(31), timeRangeForTimeDescription.get(1));
    }
}