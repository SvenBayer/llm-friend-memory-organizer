package de.svenbayer.llm_friend_memory_organizer.service.process;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class OllamaProcessService {

    private final Environment environment;

    public OllamaProcessService(Environment environment) {
        this.environment = environment;
    }

    public void stopOllamaContainer() {
        try {
            String ollama = environment.getProperty("spring.ai.ollama.chat.model");
            ProcessBuilder processBuilder = new ProcessBuilder("ollama", "stop", ollama);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Failed to stop Ollama container, exit code: " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("Error occurred while stopping Ollama container: " + e.getMessage());
        }
    }
}
