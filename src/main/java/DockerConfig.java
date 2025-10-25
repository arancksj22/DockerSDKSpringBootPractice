package com.example.DockerSDKPractice; // Ensure this package matches

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
        // Explicitly set the correct Windows named pipe host AGAIN
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("npipe:////./pipe/docker_engine") // Use correct Windows pipe
                .build();

        // Log the host that SHOULD be used
        log.info(">>>> Docker Host configured in DockerClientConfig as: {} <<<<", config.getDockerHost());

        // Use Apache HttpClient5 Builder
        // Pass the config object's host setting explicitly to the builder
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost()) // Ensure this uses the correct config value
                .sslConfig(config.getSSLConfig())
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        log.info(">>>> Using Apache HttpClient5 Transport, configured for host: {} <<<<", config.getDockerHost());

        // Instantiate the DockerClient using the config and the HttpClient
        return DockerClientImpl.getInstance(config, httpClient);
    }
}

