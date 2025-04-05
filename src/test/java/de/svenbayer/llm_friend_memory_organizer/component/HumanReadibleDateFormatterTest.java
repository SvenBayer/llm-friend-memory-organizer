package de.svenbayer.llm_friend_memory_organizer.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HumanReadibleDateFormatterTest {

    @Autowired
    private HumanReadibleDateFormatter formatter;

    @Test
    void formatDateToHumanReadible() {
        // Example reference date (pretend "today" = 2025-04-05)
        LocalDate now = LocalDate.of(2025, 4, 5);

        HumanReadibleDateFormatter formatter = new HumanReadibleDateFormatter();
        // Test various dates:
        test(now.minusYears(2));                                // today
        test(now);                                // today
        test(now.minusDays(1));                   // yesterday
        test(now.plusDays(1));                    // tomorrow
        test(now.minusDays(2));                   // this Wednesday 2 days ago (depending on actual day of week)
        test(now.plusDays(2));                    // this Monday in 2 days
        test(now.minusWeeks(1));                  // last week
        test(now.plusWeeks(2));                   // in 2 weeks
        test(now.minusMonths(1));                 // last month
        test(now.plusMonths(3));                  // in 3 months
        test(LocalDate.of(2024, 10, 1));          // last year in October
        test(LocalDate.of(2026, 10, 1));          // in 1 year in October
        test(LocalDate.of(2027, 7, 1));           // in 2 years in July
        test(LocalDate.of(2024, 10, 1));          // last year in October
        test(now.minusDays(14));

        test(now.minusMonths(14));
        test(now.minusMonths(12));
        test(now.minusMonths(6));
        test(now.minusMonths(2));
        test(now.minusMonths(1));
        test(now);
        test(now.plusMonths(1));
        test(now.plusMonths(2));
        test(now.plusMonths(3));
        test(now.plusMonths(14));

        test(now.plusDays(10));
        test(now.plusDays(9));
        test(now.plusDays(8));
        test(now.plusDays(7));
        test(now.plusDays(6));
        test(now.plusDays(5));
        test(now.plusDays(4));
        test(now.plusDays(3));
        test(now.plusDays(2));
        test(now.plusDays(1));
        test(now);
        test(now.minusDays(1));
        test(now.minusDays(2));
        test(now.minusDays(3));
        test(now.minusDays(4));
        test(now.minusDays(5));
        test(now.minusDays(6));
        test(now.minusDays(7));
        test(now.minusDays(8));
        test(now.minusDays(9));
        test(now.minusDays(10));
        test(now.minusDays(11));
        test(now.minusDays(12));
        test(now.minusDays(13));
        test(now.minusDays(14));
    }

    private void test(LocalDate date) {
        String formatted = formatter.formatDateToHumanReadible(date);
        System.out.printf("%s => %s%n", date, formatted);
    }
}