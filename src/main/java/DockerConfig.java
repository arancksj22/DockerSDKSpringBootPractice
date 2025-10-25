package com.example.DockerSDKPractice; // Ensure this package matches

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder; // Use the builder
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// *** No HttpClient, Netty, or Duration imports are needed here ***

@Configuration
public class DockerConfig {

    private static final Logger log = LoggerFactory.getLogger(DockerConfig.class);

    @Bean
    public DockerClient dockerClient() {
        // Let DefaultConfig detect the host (should find the named pipe on Windows)
        // If necessary for debugging, explicitly set it:
        // .withDockerHost("npipe:////./pipe/docker_engine")
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                // You can try letting it auto-detect first, or explicitly set the pipe:
                .withDockerHost("npipe:////./pipe/docker_engine")
                .build();

        // Log what host it detected/used
        log.info(">>>> Docker Host detected/configured as: {} <<<<", config.getDockerHost());

        log.info(">>>> Attempting to build DockerClient with default transport (expecting JUnixSocket due to classpath changes) <<<<");

        // Use DockerClientBuilder.getInstance with only the config.
        // It should automatically find and use junixsocket if the other HTTP transports
        // are removed from the pom.xml classpath.
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();

        log.info(">>>> DockerClient created successfully (hopefully using JUnixSocket) <<<<");

        return dockerClient;
    }
}

