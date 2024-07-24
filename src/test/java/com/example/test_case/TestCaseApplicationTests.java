package com.example.test_case;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.aot.DisabledInAotMode;

@SpringBootTest
@DisabledInAotMode
@ActiveProfiles("test")
class TestCaseApplicationTests {
	@Test
	void contextLoads() {
	}
}
