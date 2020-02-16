package com.example.javatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//@ExtendWith(FindSlowTestExtension.class) //Extension 선언적 등록 방법 (Instance 생성 방법을 커스텀 할수 없음)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//@Order Annotation 으로 @Test Method 순서를 정함
class StudyTest {

    Logger log = LoggerFactory.getLogger("com.example.javatest.studyTest");

    //Extension 코딩적 등록 방법 (Instance 생성 방법 커스텀 가능하여 인자를 넘길수 있음)
    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension =
            new FindSlowTestExtension(1000L);

    int value = 1;

    @Test
    @SlowTest
    @DisplayName("확장 모델 사용")
    void ExtensionTest_1() {
        try {
            Long val = new Long(1005L);
            Thread.sleep(val);
            log.info("Extension Test - sleep : {}ms", val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //@Test 마다 StudyTest class Instance를 새로 만들기에 value값이 증가하지 않음
    //why? Test는 순서가 정해지지 않기에 Test 간 의존성을 없애기 위함
    @Disabled
    @DisplayName("테스트 인스턴스 1")
    @Order(2) //낮은 값일수록 높은 우선순위
    void InstanceTest_1() {
        log.info("value : {}", String.valueOf(value));
        Study study = new Study(value++);
        log.info("value : {}", String.valueOf(value));
    }

    @Disabled
    @DisplayName("테스트 인스턴스 2")
    @Order(1) //낮은 값일수록 높은 우선순위
    void InstanceTest_2() {
        log.info("value : {}", String.valueOf(value++));
        log.info("value : {}", String.valueOf(value));
    }

    @Disabled
    @ParameterizedTest(name = "{displayName} message({index}) = {0}")
    @CsvSource({"10, '테스트 첫번째'", "20, '테스트 두번째'"})
    @DisplayName("파라미터 주입 실행 - ArgumentsAggregator")
    void parameterizedTest_5(@AggregateWith(StudyAggregator.class) Study study) {
        log.info(study.toString());
    }

    //static inner class거나 public 만 가능
    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }
    }

    @Disabled
    @ParameterizedTest(name = "{displayName} message({index}) = {0}")
    @CsvSource({"10, '테스트 첫번째'", "20, '테스트 두번째'"})
    @DisplayName("파라미터 주입 실행 - ArgumentsAccessor")
    void parameterizedTest_4(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        log.info(study.toString());
    }

    @Disabled
    @ParameterizedTest(name = "{displayName} message({index}) = {0}")
    @CsvSource({"10, '테스트 첫번째'", "20, '테스트 두번째'"})
    @DisplayName("파라미터 주입 실행 - CsvSource")
    void parameterizedTest_3(Integer limit, String name) {
        Study study = new Study(limit, name);
        log.info(study.toString());
    }

    @Disabled
    @ParameterizedTest(name = "{displayName} message({index}) = {0}")
    @ValueSource(ints = {10, 20, 30})
    @DisplayName("파라미터 주입 실행 - ValueSource and ConvertWith")
    void parameterizedTest_2(@ConvertWith(StudyConverter.class) Study study) {
        log.info(String.valueOf(study.getLimit()));
    }

    static class StudyConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    //@ParameterizedTest 은 @Test와 같은 Test class이기에 중복 사용 불가함
    @Disabled
    @ParameterizedTest(name = "{displayName} message({index}) = {0}")
    @ValueSource(strings = {"테스트", "코드를", "치기", "매우", "귀찮다"})
    @NullAndEmptySource
    @DisplayName("파라미터 주입 실행 - ValueSource")
    void parameterizedTest_1(String message) {
        log.info(message);
    }

    @Disabled
    @DisplayName("테스트 반복 실행")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition} / {totalRepetitions}")
    void repeatTest(RepetitionInfo repetitionInfo) {
        log.info("test : {} / total : {}"
                , repetitionInfo.getCurrentRepetition(), repetitionInfo.getTotalRepetitions());
    }

    @FastTest
    void custom_tag_fast_test() {
        log.info("custom tag is fast");
    }

    @SlowTest
    void custom_tag_slow_test() {
        log.info("custom tag is slow");
    }


    @Disabled
    @DisplayName("태깅 - local에서 실행")
    @Tag("fast")
    void tag_fast_test() {
        Study actual = new Study(10);
        assertThat(actual.getLimit()).isGreaterThan(0);
        log.info("tag fast test");
    }

    @Disabled
    @DisplayName("태깅 - CI 환경에서 실행")
    @Tag("slow")
    void tag_slow_test() {
        Study actual = new Study(10);
        assertThat(actual.getLimit()).isGreaterThan(0);
        log.info("tag slow test");
    }

    @Disabled
    @DisplayName("assumption이 true 이면 이하 코드 실행하도록 조건 명시")
    @EnabledOnOs({OS.WINDOWS, OS.MAC})
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9, JRE.JAVA_10, JRE.JAVA_11, JRE.OTHER})
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
    void assume_test() {
        String test_env = StringUtils.defaultToString(System.getenv("TEST_ENV"));

        assumingThat("NULL".equalsIgnoreCase(test_env), () -> {
            log.info("System env is NULL");
            Study actual = new Study(100);
        });

        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            log.info("System env is LOCAL");
            Study actual = new Study(-1);
        });

        assumeTrue("LOCAL".equalsIgnoreCase(test_env));
        log.info("System env is LOCAL");
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
    @DisplayName("스터디 생성")
        // 테스트 별로 이름 지정 가능
    void create_new_study() {
        log.info("create");
    }

    //static 으로 작성 (@TestInstance 사용하면 static 일 필요 X)
    //return 타입 설정 불가
    //모든 테스트 실행 이전 딱 한번 실행
    @BeforeAll //@BeforeClass
    void beforeAll() {
        System.out.println("before all");
    }

    //static 으로 작성 (@TestInstance 사용하면 static 일 필요 X)
    //return 타입 설정 불가
    //모든 테스트 실행 이후 딱 한번 실행
    @AfterAll //@AfterClass
    void afterAll() {
        System.out.println("after all");
    }

    //각각의 테스트 실행 이전 실행
    @BeforeEach
    //@Before
    void beforeEach() {
        log.info("before each");
    }

    //각각의 테스트 실행 이후 실행
    @AfterEach
    //@After
    void afterEach() {
        log.info("after each");
    }

}