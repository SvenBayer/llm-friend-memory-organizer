package de.svenbayer.llm_friend_memory_organizer.component;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Component
public class HumanReadibleDateFormatter {

    private final LocalDate now = LocalDate.now();
    private final Locale locale = Locale.US;

    public String formatDateToHumanReadible(LocalDate date) {
        return formatDate(date, locale).trim() + ", ";
    }

    private String formatDate(LocalDate date, Locale locale) {
        StringBuilder result = new StringBuilder();

        long daysDiff = ChronoUnit.DAYS.between(now, date);

        String withinYears = formatYearsAgoOrHead(date);
        if (withinYears != null) {
            return withinYears;
        }

        String immediateYears = formatImmediateYears(date);
        if (immediateYears != null) {
            result.append(immediateYears).append(" ");
        }

        String withinMonths = formatWithinMonths(date, locale);
        if (withinMonths != null) {
            result.append(withinMonths).append(" ");
        }

        String immediateDays = formatImmediateDays(daysDiff);
        if (immediateDays != null) {
            return immediateDays;
        }

        String withinOneWeek = formatImmediateWeeks(date, locale, daysDiff);
        if (withinOneWeek != null) {
            return withinOneWeek;
        }

        String withinWeeks = formatWithinWeeks(date);
        if (withinWeeks != null && withinMonths == null) {
            result.append(withinWeeks);
        }

        return result.toString();
    }

    private String formatYearsAgoOrHead(LocalDate date) {
        int yearDiff = getYearDiff(date);

        if (yearDiff > 1) {
            return "In " + yearDiff + " years";
        }
        if (yearDiff < -1) {
            return yearDiff * -1 + " years ago";
        }
        return null;
    }

    private String formatImmediateYears(LocalDate date) {
        int yearDiff = getYearDiff(date);

        if (yearDiff == 1) {
            return "Next year";
        }
        if (yearDiff == -1) {
            return "Last year";
        }
        return null;
    }

    private int getYearDiff(LocalDate date) {
        int currentYear = now.getYear();
        int year = date.getYear();
        return year - currentYear;
    }

    private String formatImmediateDays(long daysDiff) {
        if (daysDiff == 0) {
            return "Today";
        } else if (daysDiff == -1) {
            return "Yesterday";
        } else if (daysDiff == 1) {
            return "Tomorrow";
        }
        return null;
    }

    private String formatImmediateWeeks(LocalDate date, Locale locale, long daysDiff) {
        if (Math.abs(daysDiff) > 14) {
            return null;
        }

        long weeksDiff = getWeeksDiff(date);

        if (weeksDiff < -1 || weeksDiff > 1) {
            return null;
        }

        String week;
        if (weeksDiff == -1) {
            week = "Last week";
        } else if (weeksDiff == 1) {
            week = "Next week";
        } else {
            week = "This week";
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, locale);

        String daysAgoOrHead = "";
        if (daysDiff > 1) {
            daysAgoOrHead = " in " + daysDiff + " days";
        } else if (daysDiff < -1) {
            daysAgoOrHead = " " + daysDiff * -1 + " days ago";
        }

        return week + " on " + dayName + daysAgoOrHead;
    }

    private long getWeeksDiff(LocalDate date) {
        LocalDate startOfReferenceWeek = now.with(DayOfWeek.MONDAY);
        LocalDate startOfDateWeek = date.with(DayOfWeek.MONDAY);
        long weeksDiff = ChronoUnit.WEEKS.between(startOfReferenceWeek, startOfDateWeek);
        return weeksDiff;
    }

    private String formatWithinWeeks(LocalDate date) {
        long weeksDiff = getWeeksDiff(date);
        if (weeksDiff > 1) {
            return "in " + weeksDiff + " weeks";
        } else if (weeksDiff < -1) {
            return weeksDiff * -1 + " weeks ago";
        }
        return null;
    }

    private String formatWithinMonths(LocalDate date, Locale locale) {
        Period period = Period.between(now, date);
        int yearsDiff = period.getYears();
        int monthsDiff = date.getMonthValue() - now.getMonthValue();
        int totalMonthsDiff = yearsDiff * 12 + monthsDiff;

        Month month = date.getMonth();
        String monthName = month.getDisplayName(TextStyle.FULL, locale);

        // Handle immediate months (last, this, next)
        if (totalMonthsDiff == -1) {
            return "Last month in " + monthName;
        } else if (totalMonthsDiff == 0) {
            return null;
        } else if (totalMonthsDiff == 1) {
            return "Next month in " + monthName;
        }

        // Handle other months
        int absMonths = Math.abs(totalMonthsDiff);
        if (date.isBefore(now)) {
            return "in " + monthName + " " + absMonths + " months ago";
        } else {
            return "in " + monthName + " in " + absMonths + " months";
        }
    }
}

