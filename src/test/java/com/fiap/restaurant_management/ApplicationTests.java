package com.fiap.restaurant_management;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests extends PostgreSQLIntegrationTestSupport {

	@Test
	void contextLoads() {
	}

	@Test
	void shouldRunMainMethod() {
		Application.main(new String[] {});
	}

}
