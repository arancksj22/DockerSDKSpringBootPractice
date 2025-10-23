package com.example.DockerSDKPractice;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
// Use Netty transport imports
import com.github.dockerjava.netty.NettyDockerHttpClient; // <-- CHANGE IMPORT
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
        // Keep explicitly setting the correct Windows named pipe host
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("npipe:////./pipe/docker_engine")
                .build();

        log.info(">>>> Docker Host detected/configured as: {} <<<<", config.getDockerHost());

        // Use Netty HTTP Client Builder
        DockerHttpClient httpClient = new NettyDockerHttpClient.Builder() // <-- USE NETTY BUILDER
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .connectionTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(45)) // Netty uses readTimeout
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}