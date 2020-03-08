package com.example.junit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Testcontainers
@Slf4j
public class  TestContainer {

//    @Container
//    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer().withDatabaseName("studytest");

    //docker image name 으로 생성하는 방법
    @Container
    static GenericContainer postgreSQLContainer = new GenericContainer("postgres")
            .withExposedPorts(5432) //testcontainer 는 호스트설정 불가(랜덤 포트) (docker 실행시 hostport:container expose port)
            .withEnv("POSTGRES_DB", "studytest");

    @BeforeEach
    void beforeEach() {
//        studyRepository.deleteAll();
        System.out.println(postgreSQLContainer.getMappedPort(5432)); //mapping 된 port 확인

//        System.out.println(postgreSQLContainer.getLogs());
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
}
