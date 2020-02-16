package com.example.javatest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //annotation 타겟은 Method에 쓸수 있다
@Retention(RetentionPolicy.RUNTIME) //annotation 전략을 RUNTIME에 쓸수있게 설정
@Test
@Tag("fast")
@DisplayName("커스텀 태그 - FastTest")
//위에 명시된 Annotation을 조합해서 만든 FastTest Annotation 생성
public @interface FastTest {
}
