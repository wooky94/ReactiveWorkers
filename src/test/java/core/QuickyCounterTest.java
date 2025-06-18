package core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuickyCounterTest {

    @Test
    public void testCounterProducesCumulativeSums() {
        QuickyCounter counter = new QuickyCounter(3);
        counter.launch();
        List<Integer> results = new ArrayList<>();
        Integer val;
        while ((val = counter.next()) != null) {
            results.add(val);
        }
        assertEquals(Arrays.asList(0, 1, 3), results);
    }

    @Test
    public void testCounterStopsAtNull() {
        QuickyCounter counter = new QuickyCounter(1);
        counter.launch();
        assertNotNull(counter.next());
        assertNull(counter.next());
    }
}
