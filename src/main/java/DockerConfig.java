package com.example.DockerSDKPractice;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    private static final Logger log = LoggerFactory.getLogger(DockerConfig.class);

    @Bean
    public DockerClient dockerClient() {
        // Create config - will auto-detect Windows named pipe
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        log.info(">>>> Docker Host configured as: {} <<<<", config.getDockerHost());

        // Use Netty factory explicitly (better for Windows named pipes)
        DockerClient dockerClient = DockerClientImpl.getInstance(
                config,
                new NettyDockerCmdExecFactory()
        );

        log.info(">>>> DockerClient created successfully <<<<");

        return dockerClient;
    }
}