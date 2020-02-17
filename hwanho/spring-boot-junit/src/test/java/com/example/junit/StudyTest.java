package com.example.junit;

import jdk.jfr.Enabled;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
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

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//@ExtendWith(FindSlowTestExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //테스트 클래스당 하나의 인스턴스를 사용
public class StudyTest {

    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);

    @Test
    @DisplayName("스터디 만들기 - Assertion Test - fast")
    @Tag("fast")
    void create_new_study() {
        Study study = new Study(10);
        assertNotNull(study);
        assertEquals(StudyStatus.DRAFT, study.getStatus(), "message");
        assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "message");
        assertEquals(StudyStatus.DRAFT,  study.getStatus(), new Supplier<String>() {
            @Override
            public String get() {
                return "supplier message";
            }
        });
    }

    @Test
    @DisplayName("스터디 만들기 - assertAll - slow")
    @Tag("slow")
    void assertAllTest() {
        Study study = new Study(20);

        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.ENDED, study.getStatus(), "message"),
                () -> assertEquals(StudyStatus.STARTD, study.getStatus(), () -> "message")
        );
    }

    @Test
    void assertThrowsTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Study(-10));
        assertEquals("limit은 0 보다 커야함", exception.getMessage());
    }

    @Test
    void assertTimeoutTest() {
        //수행되는 시간을 기다려야함
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(5);
            Thread.sleep(1000);
        });
    }

    @SlowTest
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "testtest")
    void assertTimeoutPreemptivelyTest() throws InterruptedException {
        Thread.sleep(10005L);
        //코드 블럭을 별도의 쓰레드로 실행, Duration 만 체크해서 바로 확인 가능
        //사용시 주의해야함, Transaction 을 사용하는 경우 제대로 적용이 안된 수 있음!!
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(5);
            Thread.sleep(30000);
        });
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "testtest")
    void assumeTrueTest() {
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assumeTrue("LOCAL".equalsIgnoreCase(test_env));

        //조건에 맞지 않는 경우 실행 안함
        assumingThat("DEV".equalsIgnoreCase(test_env), () -> {
            System.out.println(test_env);
        });

        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            System.out.println(test_env);
        });
    }

    @FastTest
    void customAnnotationTest() {
        System.out.println("customAnnotationTest");
    }

    @RepeatedTest(3)
    void repeatTest(RepetitionInfo repetitionInfo) {
        System.out.println("repeatTest" + repetitionInfo.getCurrentRepetition() + " / "
            + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("테스트 반복")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest2(RepetitionInfo repetitionInfo) {
        System.out.println("repeatTest" + repetitionInfo.getCurrentRepetition() + " / "
                + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("파라이터 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"월", "화", "수"})
//    @EmptySource
//    @NullSource
    @NullAndEmptySource
    void parameterizedTest(String message) {
        System.out.println(message);
    }

    @DisplayName("cvsSources 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 30})
    void cvsSourceTest(Integer integer) {
        System.out.println(integer);
    }

    @DisplayName("인자 값 타입 변환 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 30})
    void cvsSourceTest(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }

    @DisplayName("인자 값 타입 변환 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '십'", "20, '이십'"})
    void cvsSourceTest2(Integer limit, String name) {
        Study study = new Study(limit, name);
        System.out.println(study);
    }

    @DisplayName("인자 값 타입 변환 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '십'", "20, '이십'"})
    void cvsSourceTest3(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println(study);
    }

    @DisplayName("인자 값 타입 변환 테스트")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, '십'", "20, '이십'"})
    void cvsSourceTest4(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }


    static class StudyConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    static class StudyAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(accessor.getInteger(0), accessor.getString(1));
        }
    }




}
