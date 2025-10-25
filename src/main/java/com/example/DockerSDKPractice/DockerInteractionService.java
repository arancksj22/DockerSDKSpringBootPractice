package com.example.DockerSDKPractice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Service
public class DockerInteractionService {

    private static final Logger log = LoggerFactory.getLogger(DockerInteractionService.class);
    private static final String HELLO_WORLD_IMAGE = "hello-world:latest";

    public String runHelloWorldAndGetLogs() {
        // Use try-with-resources to ensure the container is stopped automatically
        // We create a *new* container instance for each request in this simple example.
        try (GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(HELLO_WORLD_IMAGE))) {

            log.info("Starting hello-world container...");
            // Testcontainers handles pulling the image if needed and starting the container.
            container.start();
            log.info("Container started.");

            // The hello-world container runs its command and exits very quickly.
            // Testcontainers automatically waits for the main process to finish
            // when you call start() on an image like this.

            // Get all logs produced by the container after it has run.
            String logs = container.getLogs();
            log.info("Logs captured successfully.");

            return logs; // Return the captured logs

        } catch (Exception e) {
            log.error("Error running hello-world container", e);
            return "Error running container: " + e.getMessage();
        }
        // The container will be stopped automatically by the try-with-resources block.
    }
}
