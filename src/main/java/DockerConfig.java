package com.example.DockerSDKPractice;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    private static final Logger log = LoggerFactory.getLogger(DockerConfig.class);

    @Bean
    public DockerClient dockerClient() {
        // Auto-detect Docker configuration (will find the named pipe)
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        log.info(">>>> Docker Host configured as: {} <<<<", config.getDockerHost());

        // Use Jersey transport which supports Windows named pipes
        // Timeouts are in milliseconds (Integer)
        DockerHttpClient httpClient = new JerseyDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .connectTimeout(30000)  // 30 seconds in milliseconds
                .readTimeout(45000)     // 45 seconds in milliseconds
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        log.info(">>>> DockerClient created successfully <<<<");

        return dockerClient;
    }
}