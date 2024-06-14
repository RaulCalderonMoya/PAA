package paa.locker;

import paa.locker.persistence.Locker;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class LockerTest {
    @Test
    public void testEquals () {
        Locker locker1 = new Locker();
        Locker locker2 = new Locker();
        locker1.setCode(1L);
        locker2.setCode(1L);
        assertTrue(locker1.equals(locker2));
        locker2.setCode(2L);
        assertFalse(locker1.equals(locker2));
    }
    
}
