package de.svenbayer.llm_friend_memory_organizer;

import de.svenbayer.llm_friend_memory_organizer.service.StartupService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LlmFriendMemoryOrganizerApplication {

	private final StartupService service;

    public LlmFriendMemoryOrganizerApplication(StartupService service) {
        this.service = service;
    }

    public static void main(String[] args) {
		SpringApplication.run(LlmFriendMemoryOrganizerApplication.class, args);
	}

}
