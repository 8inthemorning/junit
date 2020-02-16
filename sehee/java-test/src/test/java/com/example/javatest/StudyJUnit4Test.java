package com.example.javatest;

//org.junit.jupiter.api.Test 는 JUnit 5 버전임
import org.junit.*;

public class StudyJUnit4Test {

    @Test
    public void test() {
        System.out.println("test");
    }

    @Before
    public void before() {
        System.out.println("before");
    }

    @After
    public void after() {
        System.out.println("after");
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("before class");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("after class");
    }

}
