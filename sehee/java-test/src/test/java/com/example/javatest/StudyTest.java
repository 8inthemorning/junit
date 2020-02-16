package com.example.javatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.platform.commons.util.StringUtils;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//Run Dashboard에 테스트명 노출 전략
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @Test
    @DisplayName("assumption이 true 이면 이하 코드 실행하도록 조건 명시")
    @EnabledOnOs({OS.WINDOWS, OS.MAC})
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10, JRE.JAVA_11, JRE.OTHER})
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
    void assume_test() {
        String test_env = StringUtils.defaultToString(System.getenv("TEST_ENV"));

        assumingThat("NULL".equalsIgnoreCase(test_env), () -> {
            System.out.println("System env is NULL");
            Study actual = new Study(100);
        });

        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            System.out.println("System env is LOCAL");
            Study actual = new Study(-1);
        });

        assumeTrue("LOCAL".equalsIgnoreCase(test_env));
        System.out.println("System env is LOCAL");
    }

    @Disabled
    @DisplayName("특정 값과 비교 확인")
    void assertThat_test() {
        Study actual = new Study(10);
        assertThat(actual.getLimit()).isGreaterThan(0);
    }

    @Disabled
    @DisplayName("특정 시간 내로 끝나는지 확인 (100ms 소요)")
    void assertTimeoutPreemptively_test() {
        //해당 assert는 100ms 소요
        //코드블럭을 별도의 thread에서 실행하기에 주의 필요
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
        // TODO ThreadLocal는 다른 thread에서 공유가 안되기에 트랜잭션 처리가 제대로 안됨
    }

    @Disabled
    @DisplayName("특정 시간 내로 끝나는지 확인")
    void assertTimeout_test() {
        //해당 assert는 315ms 소요
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
    }

    @Disabled
    @DisplayName("오류가 예상과 같은지 확인")
    void assertThrows_test() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit 은 0보다 커야한다.", exception.getMessage());
    }

    @Disabled
    @DisplayName("연관된 assert를 한번에 묶어서 확인")
    void assertAll_test() {
        Study study = new Study(-10);

        assertAll(
                () -> assertNotNull(study), //study instance null check
                //lambda 식으로 test 코드를 작성하면, expected != actual 인 오류시에만 문자열 연산을 실행하기에 효율적이다.
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(),
                        () -> "스터디를 처음 만들면 " + StudyStatus.DRAFT + " 상태다."),
                //        assertEquals(StudyStatus.DRAFT, study.getStatus(), new Supplier<String>() {
                //            @Override
                //            public String get() {
                //                return "스터디를 처음 만들면 DRAFT 상태다.";
                //            }
                //        });
                () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다.")
        );
    }

    @Disabled //@Ignore, Test 실행하지 않고 싶을때 사용 (deprecate 된 코드의 경우)
    @DisplayName("스터디 생성") // 테스트 별로 이름 지정 가능
    void create_new_study() {
        System.out.println("create");
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
    @BeforeEach
    //@Before
    void beforeEach() {
        System.out.println("before each");
    }

    //각각의 테스트 실행 이후 실행
    @AfterEach
    //@After
    void afterEach() {
        System.out.println("after each");
    }

}