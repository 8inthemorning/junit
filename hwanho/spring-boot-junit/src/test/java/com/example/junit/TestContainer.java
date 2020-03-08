package com.example.junit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;


//@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@ContextConfiguration(initializers = TestContainer.ContainerPropertyInitializer.class)
public class  TestContainer {

//    @Container
//    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer().withDatabaseName("studytest");

    @Autowired
    Environment environment;

    @Value("${container.port}") int port;

    //docker image name 으로 생성하는 방법
    @Container
    static GenericContainer postgreSQLContainer = new GenericContainer("postgres")
            .withExposedPorts(5432) //testcontainer 는 호스트설정 불가(랜덤 포트) (docker 실행시 hostport:container expose port)
            .withEnv("POSTGRES_DB", "studytest");


    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach");
//        studyRepository.deleteAll();
        System.out.println(postgreSQLContainer.getMappedPort(5432)); //mapping 된 port 확인

//        System.out.println(postgreSQLContainer.getLogs());

//        System.out.println(environment.getProperty("container.port"));
        System.out.println(port);
    }

    @BeforeAll
    static void beforeAll() {
//        postgreSQLContainer.start();
//        System.out.println(postgreSQLContainer.getJdbcUrl());

        //log monitoring 컨테이너 내부 로그 가져와서 표시
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        postgreSQLContainer.followOutput(logConsumer);

    }

    @AfterAll
    static void afterAll() {
//        postgreSQLContainer.stop();
    }

    @Test
    void test() {
        System.out.println("Testcontainer TEST!!");
    }


    //컨테이너 정보를 스프링 테스트에서 참조하기
    static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of("container.port=" + postgreSQLContainer.getMappedPort(5432))
                    .applyTo(context.getEnvironment());
        }
    }
}
