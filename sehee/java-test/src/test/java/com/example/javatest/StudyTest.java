package com.example.javatest;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class StudyTest {

    @Test
    void create() {
        Study study = new Study();
        assertNotNull(study);
        System.out.println("create");
    }

    @Test //@Test
    @Disabled //@Ignore, Test 실행하지 않고 싶을때 사용 (deprecate 된 코드의 경우)
    void create1() {
        System.out.println("create1");
    }

    //static 으로 작성 (private 불가)
    //return 타입 설정 불가
    //모든 테스트 실행 이전 딱 한번 실행
    @BeforeAll //@BeforeClass
    static void beforeAll() {
        System.out.println("before all");
    }

    //static 으로 작성 (private 불가)
    //return 타입 설정 불가
    //모든 테스트 실행 이후 딱 한번 실행
    @AfterAll //@AfterClass
    static void afterAll() {
        System.out.println("after all");
    }

    //각각의 테스트 실행 이전 실행
    @BeforeEach //@Before
    void beforeEach() {
        System.out.println("before each");
    }

    //각각의 테스트 실행 이후 실행
    @AfterEach //@After
    void afterEach() {
        System.out.println("after each");
    }

}