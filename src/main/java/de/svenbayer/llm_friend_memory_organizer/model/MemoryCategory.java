package de.svenbayer.llm_friend_memory_organizer.model;

import lombok.Getter;

@Getter
public enum MemoryCategory {
    PERSONAL_INFO("Personal Info", "User’s personal details (name, age, demographics)"),
    EMOTIONS("Emotions", "User’s mood, feelings, emotional state, and how the user is currently feeling"),
    PREFERENCES("Preferences", "User’s interests, likes, dislikes, and opinions"),
    ACTIVITIES("Activities", "User’s hobbies, events, tasks, and day-to-day actions"),
    PLACES("Places", "Locations the user visits or lives in"),
    RELATIONSHIPS("Relationships", "Family, friends, acquaintances, and social connections"),
    LIFE_EVENTS("Life Events", "Important events, milestones, memories, or achievements"),
    PLANS_GOALS("Plans & Goals", "User’s intentions, aspirations, tasks, and long-term objectives"),
    CHALLENGES("Challenges", "Obstacles, doubts, and issues the user faces"),
    BELIEFS("Beliefs", "User’s principles or beliefs"),
    EXPERIENCES("Experiences", "Encounters, lessons, and insights gained over time");

    private final String memoryCategory;
    private final String memoryCategoryDescription;

    MemoryCategory(String memoryCategory, String memoryCategoryDescription) {
        this.memoryCategory = memoryCategory;
        this.memoryCategoryDescription = memoryCategoryDescription;
    }
}
