package com.example.DockerSDKPractice; // Ensure this package matches your project structure

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig; // Needed for AutoRemove
import com.github.dockerjava.core.command.LogContainerResultCallback;

import org.slf4j.Logger; // Import standard SLF4J Logger
import org.slf4j.LoggerFactory; // Import standard SLF4J LoggerFactory
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MinimalTestDockerController {

    private final DockerClient dockerClient;
    private static final String HELLO_WORLD_IMAGE = "hello-world:latest";
    private static final Logger log = LoggerFactory.getLogger(MinimalTestDockerController.class); // Standard logger

    // Standard constructor injection (no Lombok)
    public MinimalTestDockerController(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        log.info(">>>> MinimalTestDockerController Bean CREATED! <<<<"); // <-- ADDED LOGGING
    }

    @GetMapping("/minimal-test-docker")
    public ResponseEntity<String> testDockerMinimal() {
        log.info(">>>> Accessed /minimal-test-docker endpoint! <<<<"); // <-- ADDED LOGGING
        String containerId = null;
        try {
            log.info("Creating container from image: {}", HELLO_WORLD_IMAGE);
            // Create the container and configure it to auto-remove on exit
            CreateContainerResponse container = dockerClient.createContainerCmd(HELLO_WORLD_IMAGE)
                    .withHostConfig(new HostConfig().withAutoRemove(true)) // Auto-remove!
                    .exec();
            containerId = container.getId();
            log.info("Container created: {}", containerId);

            log.info("Starting container: {}", containerId);
            // Start the container
            dockerClient.startContainerCmd(containerId).exec();
            log.info("Container started: {}", containerId);

            // --- Simplified Log Fetching ---
            // We still need to wait for the logs. logContainerCmd implicitly waits.
            final StringBuilder logs = new StringBuilder();
            LogContainerResultCallback logCallback = new LogContainerResultCallback() {
                @Override
                public void onNext(Frame item) {
                    logs.append(new String(item.getPayload()));
                }
            };

            log.info("Fetching logs for container: {}", containerId);
            // Block until logs are completely fetched
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(false) // Don't follow, just get current logs
                    .withTailAll()
                    .exec(logCallback)
                    .awaitCompletion(); // Wait for logs to finish streaming

            log.info("Logs fetched successfully for container: {}", containerId);
            return ResponseEntity.ok("Container Logs:\n" + logs.toString());

        } catch (Exception e) {
            log.error("Minimal Docker test failed", e);
            // Attempt cleanup even on error, container might still exist if start failed partially
            // Note: AutoRemove should handle cleanup if the container *ran* and exited.
            // This manual remove is a fallback for creation/start failures.
            if (containerId != null) {
                try {
                    log.warn("Attempting cleanup of potentially failed container: {}", containerId);
                    // Force remove might be needed if it failed mid-start
                    dockerClient.removeContainerCmd(containerId).withForce(true).exec();
                } catch (Exception cleanupEx) {
                    log.error("Failed to cleanup container {} after error", containerId, cleanupEx);
                }
            }
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
        // No finally block needed for remove, because we used .withAutoRemove(true)
    }
}
