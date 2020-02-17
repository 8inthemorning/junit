package com.example.junit;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JunitApplicationTests {

	@BeforeAll
	static void beforeAll() {
		System.out.println("beforeAll");
	}

	@AfterAll
	static void afterAll() {
		System.out.println("afterAll");
	}

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("이름 지정 테스트")
	void displayNameTest() {
		System.out.println("displayNameTest");
	}

}
