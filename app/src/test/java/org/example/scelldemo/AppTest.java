package org.example.scelldemo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AppTest {
    @Test void testsWork() {
        assertDoesNotThrow(App::new, "tests should work");
    }
}
