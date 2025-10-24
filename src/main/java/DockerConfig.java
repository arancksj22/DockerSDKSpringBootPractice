package com.example.DockerSDKPractice;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
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
        // Auto-detect Docker configuration (will find the named pipe)
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        log.info(">>>> Docker Host configured as: {} <<<<", config.getDockerHost());

        // Use ApacheDockerHttpClient which supports Windows named pipes
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        log.info(">>>> DockerClient created successfully <<<<");

        return dockerClient;
    }
}