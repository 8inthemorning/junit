package com.example.junit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Slf4j
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = DockerComposeTest.ContainerPropertyInitializer.class)
public class DockerComposeTest {

    @Container
    static DockerComposeContainer composeContainer =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
            .withExposedService("study-db", 5432);

    @Value("${container.port") int port;

    @Test
    void test() {
        System.out.println("DockerComposeTest");
        System.out.println(port);
    }

    static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of("container.port=" + composeContainer.getServicePort("study-db", 5432))
                    .applyTo(context.getEnvironment());
        }
    }
}
