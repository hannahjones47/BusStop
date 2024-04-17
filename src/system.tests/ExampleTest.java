package system.tests;

import org.junit.jupiter.api.Test;
import system.impl.Example;

import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    @Test
    void add() {
        assertEquals(4, Example.add(2, 2));
    }

    @Test
    void add2() {
        assertEquals(5, Example.add(3, 2));
    }
}