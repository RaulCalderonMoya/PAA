package paa.locker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import paa.locker.business.JPAParcelService;
import paa.locker.business.ParcelService;
import paa.locker.business.ParcelServiceException;
import paa.locker.persistence.Locker;
import paa.locker.persistence.Parcel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class ParcelServiceTest {
    ParcelService ps = null;

    @Before
    public void initializeService() {
        wipeDatabase();
        this.ps = new JPAParcelService();
    }

    // This method is run after every test to ensure that we know the whole contents of the DB on every test
    @After
    public void wipeDatabase() {
        this.ps = null;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("paa");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        for (Parcel p : em.createQuery("SELECT p from Parcel p", Parcel.class).getResultList()) {
            p = em.merge(p);
            em.remove(p);
        }
        for (Locker l : em.createQuery("SELECT l from Locker l", Locker.class).getResultList()) {
            l = em.merge(l);
            em.remove(l);
        }
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @Test
    public void tryCreateLockerWithNullName() {
        try {
            this.ps.createLocker(null, "address", 0, 0, 1, 1);
            fail("Trying to create a locker with null name should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */  }
    }

    @Test
    public void tryCreateLockerWithEmptyName() {
        try {
            this.ps.createLocker("", "address", 0, 0, 1, 1);
            fail("Trying to create a locker with empty name (\"\") should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithNullAddress() {
        try {
            this.ps.createLocker("name", null, 0, 0, 1, 1);
            fail("Trying to create a locker with null address should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithEmptyAddress() {
        try {
            this.ps.createLocker("name", "", 0, 0, 1, 1);
            fail("Trying to create a locker with empty address (\"\") should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithTooLowLongitude() {
        double lowLongitude = Math.nextAfter(-180, Double.NEGATIVE_INFINITY); // This is the biggest number < -180.0
        try {
            this.ps.createLocker("name", "address", lowLongitude, 0, 1, 1);
            fail("Trying to create a locker with longitude < -180 should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithTooHighLongitude() {
        double highLongitude = Math.nextAfter(180, Double.POSITIVE_INFINITY); // This is the smallest number > -180.0
        try {
            this.ps.createLocker("name", "address", highLongitude, 0, 1, 1);
            fail("Trying to create a locker with longitude > 180 should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithTooLowLatitude() {
        double lowLatitude = Math.nextAfter(-90, Double.NEGATIVE_INFINITY); // This is the biggest number < -90.0
        try {
            this.ps.createLocker("name", "address", 0, lowLatitude, 1, 1);
            fail("Trying to create a locker with latitude < -90 should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithTooHighLatitude() {
        double highLatitude = Math.nextAfter(90, Double.POSITIVE_INFINITY); // This is the smallest number > -90.0
        try {
            this.ps.createLocker("name", "address", 0, highLatitude, 1, 1);
            fail("Trying to create a locker with latitude > 90 should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithNegativeLargeCompartments() {
        try {
            this.ps.createLocker("name", "address", 0, 0, -1, 1);
            fail("Trying to create a locker with a negative number of large compartments should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerWithNegativeSmallCompartments() {
        try {
            this.ps.createLocker("name", "address", 0, 0, 1, -1);
            fail("Trying to create a locker with a negative number of small compartments should throw ParcelServiceException");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryCreateLockerNormal() {
        Locker l = new Locker();
        l.setName("Name-tryCreateLockerNormal");
        l.setAddress("Address-tryCreateLockerNormal");
        l.setLongitude(0);
        l.setLatitude(0);
        l.setLargeCompartments(1);
        l.setSmallCompartments(1);
        assertNull("Upon creation an unbound Locker should have null Id", l.getCode());
        Locker boundLocker = this.ps.createLocker(l.getName(),
                l.getAddress(),
                l.getLongitude(),
                l.getLatitude(),
                l.getLargeCompartments(),
                l.getSmallCompartments());
        assertNotNull("createLocker should return a non null Locker!", boundLocker);
        assertNotNull("createLocker should return a Locker with non-null code", boundLocker.getCode());
        assertTrue("createLocker returned a Locker with altered fields!", areLockersEqual(l, boundLocker));
    }

    @Test
    public void tryCreateLockerWithMinimumLongitude() {
        Locker l = new Locker();
        l.setName("Name-tryCreateLockerWithMinimumLongitude");
        l.setAddress("Address-tryCreateLockerWithMinimumLongitude");
        l.setLongitude(-180);
        l.setLatitude(0);
        l.setLargeCompartments(1);
        l.setSmallCompartments(1);
        assertNull("Upon creation an unbound Locker should have null Id", l.getCode());
        try {
            Locker boundLocker = this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
            assertNotNull("createLocker should return a non null Locker!", boundLocker);
            assertNotNull("createLocker should return a Locker with non-null code", boundLocker.getCode());
            assertTrue("createLocker returned a Locker with altered fields!", areLockersEqual(l, boundLocker));
        } catch (ParcelServiceException ignored) {
            fail("createLocker failed to create a locker with longitude == -180, which should be valid!");
        }
    }

    @Test
    public void tryCreateLockerWithMaximumLongitude() {
        Locker l = new Locker();
        l.setName("Name-tryCreateLockerWithMaximumLongitude");
        l.setAddress("Address-tryCreateLockerWithMaximumLongitude");
        l.setLongitude(180);
        l.setLatitude(0);
        l.setLargeCompartments(1);
        l.setSmallCompartments(1);
        assertNull("Upon creation an unbound Locker should have null Id", l.getCode());
        try {
            Locker boundLocker = this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
            assertNotNull("createLocker should return a non null Locker!", boundLocker);
            assertNotNull("createLocker should return a Locker with non-null code", boundLocker.getCode());
            assertTrue("createLocker returned a Locker with altered fields!", areLockersEqual(l, boundLocker));
        } catch (ParcelServiceException ignored) {
            fail("createLocker failed to create a locker with longitude == 180, which should be valid!");
        }
    }

    @Test
    public void tryCreateLockerWithMinimumLatitude() {
        Locker l = new Locker();
        l.setName("Name-tryCreateLockerWithMinimumLatitude");
        l.setAddress("Address-tryCreateLockerWithMinimumLatitude");
        l.setLongitude(0);
        l.setLatitude(-90);
        l.setLargeCompartments(1);
        l.setSmallCompartments(1);
        assertNull("Upon creation an unbound Locker should have null Id", l.getCode());
        try {
            Locker boundLocker = this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
            assertNotNull("createLocker should return a non null Locker!", boundLocker);
            assertNotNull("createLocker should return a Locker with non-null code", boundLocker.getCode());
            assertTrue("createLocker returned a Locker with altered fields!", areLockersEqual(l, boundLocker));
        } catch (ParcelServiceException ignored) {
            fail("createLocker failed to create a locker with latitude == -90, which should be valid!");
        }
    }

    @Test
    public void tryCreateLockerWithMaximumLatitude() {
        Locker l = new Locker();
        l.setName("Name-tryCreateLockerWithMaximumLatitude");
        l.setAddress("Address-tryCreateLockerWithMaximumLatitude");
        l.setLongitude(180);
        l.setLatitude(0);
        l.setLargeCompartments(1);
        l.setSmallCompartments(1);
        assertNull("Upon creation an unbound Locker should have null Id", l.getCode());
        try {
            Locker boundLocker = this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
            assertNotNull("createLocker should return a non null Locker!", boundLocker);
            assertNotNull("createLocker should return a Locker with non-null code", boundLocker.getCode());
            assertTrue("createLocker returned a Locker with altered fields!", areLockersEqual(l, boundLocker));
        } catch (ParcelServiceException ignored) {
            fail("createLocker failed to create a locker with latitude == 90, which should be valid!");
        }
    }

    @Test
    public void tryCreateLockerWithZeroLargeCompartments() {
        Locker l = new Locker();
        l.setName("Name-tryCreateLockerWithZeroLargeCompartments");
        l.setAddress("Address-tryCreateLockerWithZeroLargeCompartments");
        l.setLongitude(0);
        l.setLatitude(0);
        l.setLargeCompartments(0);
        l.setSmallCompartments(1);
        assertNull("Upon creation an unbound Locker should have null Id", l.getCode());
        try {
            Locker boundLocker = this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
            assertNotNull("createLocker should return a non null Locker!", boundLocker);
            assertNotNull("createLocker should return a Locker with non-null code", boundLocker.getCode());
            assertTrue("createLocker returned a Locker with altered fields!", areLockersEqual(l, boundLocker));
        } catch (ParcelServiceException ignored) {
            fail("createLocker failed to create a locker with zero large compartments, which should be valid!");
        }
    }

    @Test
    public void tryCreateLockerWithZeroSmallCompartments() {
        Locker l = new Locker();
        l.setName("Name-tryCreateLockerWithZeroSmallCompartments");
        l.setAddress("Address-tryCreateLockerWithZeroSmallCompartments");
        l.setLongitude(0);
        l.setLatitude(0);
        l.setLargeCompartments(1);
        l.setSmallCompartments(0);
        assertNull("Upon creation an unbound Locker should have null Id", l.getCode());
        try {
            Locker boundLocker = this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
            assertNotNull("createLocker should return a non null Locker!", boundLocker);
            assertNotNull("createLocker should return a Locker with non-null code", boundLocker.getCode());
            assertTrue("createLocker returned a Locker with altered fields!", areLockersEqual(l, boundLocker));
        } catch (ParcelServiceException ignored) {
            fail("createLocker failed to create a locker with zero small compartments, which should be valid!");
        }
    }

    @Test
    public void tryFindLocker() {
        Locker l = new Locker();
        l.setName("Name-tryFindLocker");
        l.setAddress("Address-tryFindLocker");
        l.setLongitude(0);
        l.setLatitude(0);
        l.setLargeCompartments(1);
        l.setSmallCompartments(1);
        Locker boundLocker = this.ps.createLocker(l.getName(),
                l.getAddress(),
                l.getLongitude(),
                l.getLatitude(),
                l.getLargeCompartments(),
                l.getSmallCompartments());
        Locker foundLocker = this.ps.findLocker(boundLocker.getCode());
        assertNotNull("Locker could not be found!", foundLocker);
        assertTrue("The locker that was found is not the one it should be!", areLockersEqual(boundLocker, foundLocker));
        try {
            assertNull("Received non-null Locker while looking for inexistent locker!",
                    this.ps.findLocker(boundLocker.getCode() + 1)); //This code should not exist
        } catch (ParcelServiceException ignored) {
            fail("Received ParcelServiceException while looking for inexistent locker; findLocker should instead return null!");
        }
    }
    
    @Test
    public void tryFindAllLockers() {
        final int numLockers = 100;
        for (int i = 0; i < numLockers; ++i) {
            Locker l = new Locker();
            l.setName("Name-tryFindAllLockers " + i);
            l.setAddress("Address-tryFindAllLockers " + i);
            l.setLongitude(0);
            l.setLatitude(0);
            l.setLargeCompartments(1);
            l.setSmallCompartments(1);
            this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
        }
        List<Locker> foundLockers = this.ps.findAllLockers();
        assertNotNull("findAllLockers did not return a valid list!", foundLockers);
        assertEquals("findAll did not return as many lockers as previously inserted", numLockers, foundLockers.size());
    }

    @Test
    public void testAvailableCompartmentsEmptyLockers() {
        final int numLockers = 100;
        final int dateDelta = 15;
        final int largeMultiplier = 2;
        final int smallMultiplier = 3;
        for (int i = 0; i < numLockers; ++i) {
            Locker l = new Locker();
            l.setName("Name-testAvailableCompartmentsEmptyLockers " + i);
            l.setAddress("Address-testAvailableCompartmentsEmptyLockers " + i);
            l.setLongitude(0);
            l.setLatitude(0);
            final int largeCompartments = largeMultiplier * i;
            l.setLargeCompartments(largeCompartments);
            final int smallCompartments = smallMultiplier * i;
            l.setSmallCompartments(smallCompartments);
            Locker boundLocker = this.ps.createLocker(l.getName(),
                    l.getAddress(),
                    l.getLongitude(),
                    l.getLatitude(),
                    l.getLargeCompartments(),
                    l.getSmallCompartments());
            for (int j = -dateDelta; j <= dateDelta; ++j) {
                LocalDate date = LocalDate.now().plusDays(j);
                assertEquals(String.format("The expected number of available large compartments on %s is %d!", date, largeCompartments),
                        largeCompartments, this.ps.availableCompartments(boundLocker.getCode(), date, ParcelService.LargeMaxWeight));
                assertEquals(String.format("The expected number of available small compartments on %s is %d!", date, smallCompartments),
                        smallCompartments, this.ps.availableCompartments(boundLocker.getCode(), date, ParcelService.SmallMaxWeight));
            }
        }
    }

    @Test
    public void testAvailableCompartmentsNegativeWeight() {
        try {
            Locker l = this.ps.createLocker("name", "address", 0, 0, 1, 1);
            this.ps.availableCompartments(l.getCode(), LocalDate.of(2023, 5, 18), Math.nextAfter(0.0f, Float.NEGATIVE_INFINITY));
            fail("availableCompartments should throw ParcelServiceException when asked about a parcel with negative weight!");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void testAvailableCompartmentsZeroWeight() {
        try {
            Locker l = this.ps.createLocker("name", "address", 0, 0, 1, 1);
            this.ps.availableCompartments(l.getCode(), LocalDate.of(2023, 5, 18), 0);
            fail("availableCompartments should throw ParcelServiceException when asked about a parcel with zero weight!");
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void testAvailableCompartmentsExcessWeight() {
        try {
            Locker l = this.ps.createLocker("name", "address", 0, 0, 1, 1);
            this.ps.availableCompartments(l.getCode(), LocalDate.of(2023, 5, 18), Math.nextAfter(ParcelService.LargeMaxWeight, Double.POSITIVE_INFINITY));
            fail(String.format("availableCompartments should throw ParcelServiceException when asked about a parcel with weight over %f!", ParcelService.LargeMaxWeight));
        } catch (ParcelServiceException ignored) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void testDeliverParcelSimple() {
        LocalDate deliveryDate = LocalDate.of(2023, 1, 1);
        final int addressee = 1234;
        Locker l = this.ps.createLocker("name", "address", 0, 0, 1, 1);
        Parcel p = this.ps.deliverParcel(l.getCode(), addressee, ParcelService.LargeMaxWeight, deliveryDate);
        assertNotNull("The parcel returned by deliverParcel should never be null!", p);
        assertNotNull("The parcel returned by deliverParcel must have a unique code!", p.getCode());
        assertEquals("The parcel returned by deliverParcel has the wrong addressee!", addressee, p.getAddressee());
        assertTrue("The parcel returned by deliverParcel has the wrong weight!", ParcelService.LargeMaxWeight == p.getWeight());
        assertEquals("The parcel returned by deliverParcel is not linked to the correct locker!", l.getCode(), p.getLocker().getCode());
        /*
        * Si después de llamar a deliverParcel la lista de paquetes del Locker asociado no se actualiza,
        * la forma más sencilla de solucionarlo es insertar inmediatamente después del commit de la transacción una
        * operación refresh sobre el Locker que ha sido alterado; alternativamente puedes añadir la propiedad
        * <shared-cache-mode>NONE</shared-cache-mode> a la unidad de persistencia en persistence.xml.
        * */
        assertEquals("The indirect list .getParcels() of Locker does not update when inserting a parcel!", 1, this.ps.findLocker(l.getCode()).getParcels().size());
        assertEquals("The indirect list .getParcels() of Locker does not contain the correct parcel!", p.getCode(), this.ps.findLocker(l.getCode()).getParcels().get(0).getCode());
    }



    @Test
    public void testAvailableCompartmentsSingleLargeParcel() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        LocalDate deliveryDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        Locker l2 = this.ps.createLocker("name2", "address2", 0, 0, largeCompartments, smallCompartments);
        Locker l3 = this.ps.createLocker("name3", "address3", 0, 0, largeCompartments, smallCompartments);
        this.ps.deliverParcel(l2.getCode(), 1234, ParcelService.LargeMaxWeight, deliveryDate);
        assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        for (int deltaDays = 1 - ParcelService.MaxDaysInLocker; deltaDays < ParcelService.MaxDaysInLocker; ++deltaDays) {
            assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(deltaDays), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
            assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(deltaDays), ParcelService.SmallMaxWeight));
            assertEquals(largeCompartments - 1, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(deltaDays), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
            assertEquals(smallCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(deltaDays), ParcelService.SmallMaxWeight));
            assertEquals(largeCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(deltaDays), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
            assertEquals(smallCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(deltaDays), ParcelService.SmallMaxWeight));
        }
        assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
    }

    @Test
    public void testAvailableCompartmentsSingleSmallParcel() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        LocalDate deliveryDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        Locker l2 = this.ps.createLocker("name2", "address2", 0, 0, largeCompartments, smallCompartments);
        Locker l3 = this.ps.createLocker("name3", "address3", 0, 0, largeCompartments, smallCompartments);
        this.ps.deliverParcel(l2.getCode(), 1234, ParcelService.SmallMaxWeight, deliveryDate);
        assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.minusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        for (int deltaDays = 1 - ParcelService.MaxDaysInLocker; deltaDays < ParcelService.MaxDaysInLocker; ++deltaDays) {
            assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(deltaDays), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
            assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(deltaDays), ParcelService.SmallMaxWeight));
            assertEquals(largeCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(deltaDays), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
            assertEquals(smallCompartments - 1, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(deltaDays), ParcelService.SmallMaxWeight));
            assertEquals(largeCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(deltaDays), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
            assertEquals(smallCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(deltaDays), ParcelService.SmallMaxWeight));
        }
        assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l2.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
        assertEquals(largeCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), Math.nextAfter(ParcelService.SmallMaxWeight, Double.POSITIVE_INFINITY)));
        assertEquals(smallCompartments, this.ps.availableCompartments(l3.getCode(), deliveryDate.plusDays(ParcelService.MaxDaysInLocker), ParcelService.SmallMaxWeight));
    }

    /**
     * Esta prueba es más compleja y en caso de fallo probablemente sea buena idea dibujar los paquetes en un
     * cronograma para entender lo que está pasando. Nótese que todas las pruebas deberían funcionar incluso
     * ampliando el valor de MaxDaysInLocker, pero esta prueba fallará si se reduce por debajo de 3
     */
    @Test
    public void testAvailableCompartmentsMultipleLargeParcels() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        final float referenceWeight = ParcelService.LargeMaxWeight;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        this.ps.deliverParcel(l1.getCode(), 1, referenceWeight, referenceDate);
        this.ps.deliverParcel(l1.getCode(), 2, referenceWeight, referenceDate.plusDays(1));
        this.ps.deliverParcel(l1.getCode(), 3, referenceWeight, referenceDate.plusDays(2));
        this.ps.deliverParcel(l1.getCode(), 4, referenceWeight, referenceDate.plusDays(ParcelService.MaxDaysInLocker));
        assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(-ParcelService.MaxDaysInLocker), referenceWeight));
        assertEquals(largeCompartments - 1, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(1-ParcelService.MaxDaysInLocker), referenceWeight));
        assertEquals(largeCompartments - 2, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(2-ParcelService.MaxDaysInLocker), referenceWeight));
        for (int d = 3-ParcelService.MaxParcelsInLocker; d <= ParcelService.MaxDaysInLocker; ++d) {
            assertEquals(largeCompartments - 3, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(d), referenceWeight));
        }
        assertEquals(largeCompartments - 2, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(1+ParcelService.MaxDaysInLocker), referenceWeight));
        for (int d = 2+ParcelService.MaxDaysInLocker; d <= 2*ParcelService.MaxDaysInLocker-1; ++d) {
            assertEquals(largeCompartments - 1, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(d), referenceWeight));
        }
        assertEquals(largeCompartments, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(2 * ParcelService.MaxDaysInLocker), referenceWeight));
    }

    /**
     * Esta prueba es más compleja y en caso de fallo probablemente sea buena idea dibujar los paquetes en un
     * cronograma para entender lo que está pasando. Nótese que todas las pruebas deberían funcionar incluso
     * ampliando el valor de MaxDaysInLocker, pero esta prueba fallará si se reduce por debajo de 3.
     */
    @Test
    public void testAvailableCompartmentsMultipleSmallParcels() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        final float referenceWeight = ParcelService.SmallMaxWeight;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        this.ps.deliverParcel(l1.getCode(), 1, referenceWeight, referenceDate);
        this.ps.deliverParcel(l1.getCode(), 3, referenceWeight, referenceDate.plusDays(2));
        this.ps.deliverParcel(l1.getCode(), 4, referenceWeight, referenceDate.plusDays(ParcelService.MaxDaysInLocker));
        assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(-ParcelService.MaxDaysInLocker), referenceWeight));
        assertEquals(smallCompartments - 1, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(1-ParcelService.MaxDaysInLocker), referenceWeight));
        assertEquals(smallCompartments - 1, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(2-ParcelService.MaxDaysInLocker), referenceWeight));
        for (int d = 3-ParcelService.MaxParcelsInLocker; d <= 1 + ParcelService.MaxDaysInLocker; ++d) {
            assertEquals(smallCompartments - 2, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(d), referenceWeight));
        }
        for (int d = 2+ParcelService.MaxDaysInLocker; d <= 2*ParcelService.MaxDaysInLocker-1; ++d) {
            assertEquals(smallCompartments - 1, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(d), referenceWeight));
        }
        assertEquals(smallCompartments, this.ps.availableCompartments(l1.getCode(), referenceDate.plusDays(2 * ParcelService.MaxDaysInLocker), referenceWeight));
    }

    @Test
    public void tryDeliverSmallParcelOnFullLocker() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        final float referenceWeight = ParcelService.SmallMaxWeight;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        try {
            for (int i = 0; i < smallCompartments; ++i) {
                this.ps.deliverParcel(l1.getCode(), i + 1, referenceWeight, referenceDate);
            }
        } catch (ParcelServiceException ignore) {
            fail("There should still be room for these parcels");
        }
        try {
            this.ps.deliverParcel(l1.getCode(), 3, referenceWeight, referenceDate.plusDays(ParcelService.MaxDaysInLocker - 1));
            fail("The locker is full and this parcel should have been rejected");
        } catch (ParcelServiceException ignore) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryDeliverLargeParcelOnFullLocker() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        final float referenceWeight = ParcelService.LargeMaxWeight;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        try {
            for (int i = 0; i < largeCompartments; ++i) {
                this.ps.deliverParcel(l1.getCode(), i + 1, referenceWeight, referenceDate);
            }
        } catch (ParcelServiceException ignore) {
            fail("There should still be room for these parcels");
        }
        try {
            this.ps.deliverParcel(l1.getCode(), 3, referenceWeight, referenceDate.plusDays(ParcelService.MaxDaysInLocker - 1));
            fail("The locker is full and this parcel should have been rejected");
        } catch (ParcelServiceException ignore) { /* We expect this exception to be thrown */ }
    }

    @Test
    public void tryDeliverOverweightParcel() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        try {
            this.ps.deliverParcel(l1.getCode(), 1, Math.nextAfter(ParcelService.LargeMaxWeight, Float.POSITIVE_INFINITY), referenceDate);
            fail("deliverParcel should throw ParcelServiceException for parcels with negative weight");
        } catch (ParcelServiceException ignore) {
            // Check the parcel was not delivered before throwing exception
            assertEquals("Wrong parcels must not be delivered before throwing exception", 0, this.ps.findLocker(l1.getCode()).getParcels().size());
        }
    }

    @Test
    public void tryDeliverZeroWeightParcel() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        try {
            this.ps.deliverParcel(l1.getCode(), 1, 0.0f, referenceDate);
            fail("deliverParcel should throw ParcelServiceException for parcels with zero weight");
        } catch (ParcelServiceException ignore) {
            // Check the parcel was not delivered before throwing exception
            assertEquals("Wrong parcels must not be delivered before throwing exception", 0, this.ps.findLocker(l1.getCode()).getParcels().size());
        }
    }

    @Test
    public void tryDeliverNegativeWeightParcel() {
        final int largeCompartments = 5;
        final int smallCompartments = 10;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        try {
            this.ps.deliverParcel(l1.getCode(), 1, Math.nextAfter(0.0f, Float.NEGATIVE_INFINITY), referenceDate);
            fail("deliverParcel should throw ParcelServiceException for parcels with excess weight");
        } catch (ParcelServiceException ignore) {
            // Check the parcel was not delivered before throwing exception
            assertEquals("Wrong parcels must not be delivered before throwing exception", 0, this.ps.findLocker(l1.getCode()).getParcels().size());
        }
    }

    @Test
    public void tryDeliverTooManyParcelsInOneLocker() {
        final int largeCompartments = ParcelService.MaxParcelsInLocker * 2;
        final int smallCompartments = ParcelService.MaxParcelsInLocker * 2;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        try {
            this.ps.deliverParcel(l1.getCode(), 1, ParcelService.SmallMaxWeight, referenceDate);
            for (int i = 1; i < ParcelService.MaxParcelsInLocker; ++i) {
                this.ps.deliverParcel(l1.getCode(), 1, ParcelService.LargeMaxWeight, referenceDate);
                this.ps.deliverParcel(l1.getCode(), 2, ParcelService.LargeMaxWeight, referenceDate);
            }
        } catch (ParcelServiceException ignored) {
            fail("The locker is not full yet, all parcels so far should have been delivered");
        }
        try {
            this.ps.deliverParcel(l1.getCode(), 1, ParcelService.SmallMaxWeight, referenceDate.plusDays(ParcelService.MaxDaysInLocker - 1));
            fail("Addressee 1 has too many parcels awaiting retrieval in this locker, ParcelServiceException should have been thrown!");
        } catch (ParcelServiceException ignored) {
            assertEquals("The parcel for addressee 1 has been wrongly delivered despite throwing the exception", ParcelService.MaxParcelsInLocker * 2 - 1, this.ps.findLocker(l1.getCode()).getParcels().size());
        }
    }

    @Test
    public void tryDeliverTooManyParcelsAnywhere() {
        final int largeCompartments = ParcelService.MaxParcelsAnywhere * 2;
        final int smallCompartments = ParcelService.MaxParcelsAnywhere * 2;
        LocalDate referenceDate = LocalDate.of(2023, 1, 1);
        Locker l1 = this.ps.createLocker("name1", "address1", 0, 0, largeCompartments, smallCompartments);
        Locker l2 = this.ps.createLocker("name2", "address2", 0, 0, largeCompartments, smallCompartments);
        Locker l3 = this.ps.createLocker("name3", "address3", 0, 0, largeCompartments, smallCompartments);
        try {
            this.ps.deliverParcel(l1.getCode(), 1, ParcelService.SmallMaxWeight, referenceDate);
            for (int i = 1; i < ParcelService.MaxParcelsAnywhere; ++i) {
                Locker l = l1;
                if (0 == i % 2) {
                    l = l2;
                }
                this.ps.deliverParcel(l.getCode(), 1, ParcelService.LargeMaxWeight, referenceDate);
                this.ps.deliverParcel(l.getCode(), 2, ParcelService.LargeMaxWeight, referenceDate);
            }
        } catch (ParcelServiceException ignored) {
            fail("The locker is not full yet, all parcels so far should have been delivered");
        }
        try {
            this.ps.deliverParcel(l3.getCode(), 1, ParcelService.SmallMaxWeight, referenceDate.plusDays(ParcelService.MaxDaysInLocker - 1));
            fail("Addressee 1 has too many parcels awaiting retrieval anywhere, ParcelServiceException should have been thrown!");
        } catch (ParcelServiceException ignored) {
            assertEquals("The parcel for addressee 1 has been wrongly delivered despite throwing the exception", 0, this.ps.findLocker(l3.getCode()).getParcels().size());
        }
    }

    @Test
    public void testRetrieveParcel() {
        LocalDate deliveryDate = LocalDate.now().minusDays(ParcelService.MaxDaysInLocker - 1);
        final int addressee = 1234;
        Locker l = this.ps.createLocker("name", "address", 0, 0, 1, 1);
        Parcel p = this.ps.deliverParcel(l.getCode(), addressee, ParcelService.LargeMaxWeight, deliveryDate);
        this.ps.retrieveParcel(p.getCode());
        /*
         * Si después de llamar a deliverParcel la lista de paquetes del Locker asociado no se actualiza,
         * la forma más sencilla de solucionarlo es insertar inmediatamente después del commit de la transacción una
         * operación refresh sobre el Locker que ha sido alterado; alternativamente puedes añadir la propiedad
         * <shared-cache-mode>NONE</shared-cache-mode> a la unidad de persistencia en persistence.xml.
         * */
        assertEquals("The indirect list .getParcels() of Locker does not update when deleting a parcel!", 0, this.ps.findLocker(l.getCode()).getParcels().size());
    }

    @Test
    public void tryRetrieveExpiredParcel() {
        LocalDate deliveryDate = LocalDate.now().minusDays(ParcelService.MaxDaysInLocker);
        final int addressee = 1234;
        Locker l = this.ps.createLocker("name", "address", 0, 0, 1, 1);
        Parcel p = this.ps.deliverParcel(l.getCode(), addressee, ParcelService.LargeMaxWeight, deliveryDate);
        try {
            this.ps.retrieveParcel(p.getCode());
            fail("The parcel has been returned to sender and cannot be retrieved, ParcelServiceException should have been thrown instead.");
        } catch (ParcelServiceException ignored) {
            assertEquals("Expired parcels don't have to be deleted from the database.", 1, this.ps.findLocker(l.getCode()).getParcels().size());
        }
    }

    @Test
    public void tryRetrieveFutureParcel() {
        LocalDate deliveryDate = LocalDate.now().plusDays(1);
        final int addressee = 1234;
        Locker l = this.ps.createLocker("name", "address", 0, 0, 1, 1);
        Parcel p = this.ps.deliverParcel(l.getCode(), addressee, ParcelService.LargeMaxWeight, deliveryDate);
        try {
            this.ps.retrieveParcel(p.getCode());
            fail("The parcel has not arrived yet and cannot therefore be retrieved, ParcelServiceException should have been thrown instead.");
        } catch (ParcelServiceException ignored) {
            assertEquals("The parcel has been wrongly deleted from the database.", 1, this.ps.findLocker(l.getCode()).getParcels().size());
        }
    }

    public static boolean areLockersEqual(Locker a, Locker b) {
        return a.getName().equals(b.getName())
                && a.getAddress().equals(b.getAddress())
                && a.getLongitude() == b.getLongitude()
                && a.getLatitude() == b.getLatitude()
                && a.getLargeCompartments() == b.getLargeCompartments()
                && a.getSmallCompartments() == b.getSmallCompartments();
    }
}