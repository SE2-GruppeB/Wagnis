package at.aau.wagnis.ObjectTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.wagnis.Adjacency;
import at.aau.wagnis.Hub;

class AdjacencyTest {

    @Mock
    Hub hub1;
    @Mock
    Hub hub2;

    Adjacency adjacency;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(hub1.toString()).thenReturn("hub1");
        when(hub2.toString()).thenReturn("hub2");

        adjacency = new Adjacency(hub1, hub2);
    }

    @Test
    void getHub1() {
        assertSame(hub1, adjacency.getHub1());
        assertNotSame(hub2, adjacency.getHub1());
    }

    @Test
    void getHub2() {
        assertSame(hub2, adjacency.getHub2());
        assertNotSame(hub1, adjacency.getHub2());
    }
    @Test
    void setHub1() {
        Hub tester = new Hub(10);
        adjacency.setHub1(tester);
        assertSame(tester, adjacency.getHub1());
    }

    @Test
    void setHub2() {
        Hub tester = new Hub(10);
        adjacency.setHub2(tester);
        assertSame(tester, adjacency.getHub2());
    }
    @Test
    void isInPair() {
     Hub hub3 = new Hub(15);
     assertTrue(adjacency.isInPair(hub1,hub2));
     assertTrue(adjacency.isInPair(hub2,hub1));
     assertFalse(adjacency.isInPair(hub1,hub3));
    }
}