package com.example.DockerSDKPractice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private final DockerInteractionService dockerService;

    // Constructor injection
    public HelloWorldController(DockerInteractionService dockerService) {
        this.dockerService = dockerService;
    }

    @GetMapping("/run-hello-world")
    public ResponseEntity<String> runHelloWorld() {
        String logs = dockerService.runHelloWorldAndGetLogs();
        if (logs.startsWith("Error")) {
            return ResponseEntity.internalServerError().body(logs);
        } else {
            // Return logs as plain text
            return ResponseEntity.ok(logs);
        }
    }
}
