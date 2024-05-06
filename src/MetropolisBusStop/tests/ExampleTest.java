package MetropolisBusStop.tests;

import org.junit.jupiter.api.Test;
import MetropolisBusStop.impl.Example;

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

// todo test that exceptions are thrown, e.g. expected route can't have a negative delay or journeyNo