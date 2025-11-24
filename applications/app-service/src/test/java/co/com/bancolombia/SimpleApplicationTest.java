package co.com.bancolombia;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleApplicationTest {

    @Test
    void mainApplicationClassExists() {
        assertNotNull(MainApplication.class);
    }

    @Test
    void mainMethodExists() throws NoSuchMethodException {
        assertNotNull(MainApplication.class.getMethod("main", String[].class));
    }
}