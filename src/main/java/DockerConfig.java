package com.example.DockerSDKPractice; // Ensure this package matches your project structure

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
// Correct import for Apache HttpClient5 transport
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient; // <-- Use HttpClient5 transport
import com.github.dockerjava.transport.DockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DockerConfig {

    private static final Logger log = LoggerFactory.getLogger(DockerConfig.class);

    @Bean
    public DockerClient dockerClient() {
        // Explicitly set the correct Windows named pipe host
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("npipe:////./pipe/docker_engine") // Use correct Windows pipe
                .build();

        log.info(">>>> Docker Host detected/configured as: {} <<<<", config.getDockerHost());

        // Use Apache HttpClient5 Builder
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder() // <-- USE HTTPCLIENT5 BUILDER
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}

