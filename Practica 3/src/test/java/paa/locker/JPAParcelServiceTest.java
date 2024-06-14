package paa.locker;

import static org.junit.Assert.*;



import java.time.LocalDate;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import paa.locker.business.JPAParcelService;
import paa.locker.business.ParcelServiceException;
import paa.locker.persistence.Locker;
import paa.locker.persistence.Parcel;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)//ejecuta los test por orden alfabetico

public class JPAParcelServiceTest {
	
	@Test
	public void a_createLockerTest() {
		JPAParcelService s = new JPAParcelService();
		 s.createLocker("Prueba", "Direccion", 0, 0, 90, 10);
		 s.createLocker("Vallecas", "Nikola Tesla, S/N", -3.6286138, 40.3901617, 4, 8);
	     s.createLocker( "Atocha", "Glorieta del Emperador Carlos V", -3.679583, 40.3965354, 10, 20);
	     s.createLocker( "Moncloa", "Plaza de la Moncloa", -3.7116853, 40.4317665, 4, 16);
	     s.createLocker( "América", "Avenida de América, 1", -3.6770431, 40.4390217, 5, 4);
	     Locker l = s.createLocker( "Chamartín", "Agustín de Foxá, 40", -3.681379, 40.4715852, 20, 10);
	     
	    s.deliverParcel(l.getCode(), 001, 1,LocalDate.now().plusDays(2)); 
		s.deliverParcel(l.getCode(), 002, 1,LocalDate.now().plusDays(1));	
		s.deliverParcel(l.getCode(), 003, 3,LocalDate.now().plusDays(3));
		s.deliverParcel(l.getCode(), 10, 4,LocalDate.now().plusDays(2));	
		s.deliverParcel(l.getCode(), 11, 5,LocalDate.now());	

	}
/*	@Test
	public void a_createLockerTest() {
		JPAParcelService s = new JPAParcelService();
		Locker l = s.createLocker("Prueba", "Direccion", 0, 0, 90, 10);
		assertTrue(l != null);
		assertTrue(l.getCode() != null);
		Locker l1 = s.findLocker(l.getCode());
		assertTrue(l1 != null);
		assertTrue(l1.equals(l));
		assertTrue(l1.getName().equals("Prueba"));
		assertTrue(l1.getAddress().equals("Direccion"));
		assertTrue(l1.getLargeCompartments() == 90);
		assertTrue(l1.getSmallCompartments() == 10);
		assertTrue(l1.getLatitude() == 0);
		assertTrue(l1.getLongitude() == 0);
	
		//Eliminamos los parking creados
		 s.deleteAllLockers();	
	}
	
	@Test
	public void a_createLockerTestNOK() {
		JPAParcelService s = new JPAParcelService();
		try {
			s.createLocker(null, "Direccion", 0, 0, 3, 10);
			fail("Deberia saltar la excepcion");
		} catch(ParcelServiceException e) {
			assertEquals(e.getLocalizedMessage(), "No se ha podido crear el armario los datos no son correctos.");
		}
		
		try {
			s.createLocker("Prueba", null, 0, 0, 3, 10);
			fail("Deberia saltar la excepcion");
		} catch(ParcelServiceException e) {
			assertEquals(e.getLocalizedMessage(), "No se ha podido crear el armario los datos no son correctos.");
		}
		
		try {
			s.createLocker("Prueba", "Direccion", -181, 0, 3, 10);
			fail("Deberia saltar la excepcion");
		} catch(ParcelServiceException e) {
			assertEquals(e.getLocalizedMessage(), "No se ha podido crear el armario los datos no son correctos.");
		}
		
		try {
			s.createLocker("Prueba", "Direccion", 0, -100, 3, 10);
			fail("Deberia saltar la excepcion");
		} catch(ParcelServiceException e) {
			assertEquals(e.getLocalizedMessage(), "No se ha podido crear el armario los datos no son correctos.");
		}
		
		try {
			s.createLocker("Prueba", "Direccion", 0, 0, -3, 10);
			fail("Deberia saltar la excepcion");
		} catch(ParcelServiceException e) {
			assertEquals(e.getLocalizedMessage(), "No se ha podido crear el armario los datos no son correctos.");
		}
		
		try {
			s.createLocker("Prueba", "Direccion", 0, 0, 3, -10);
			fail("Deberia saltar la excepcion");
		} catch(ParcelServiceException e) {
			assertEquals(e.getLocalizedMessage(), "No se ha podido crear el armario los datos no son correctos.");
		}
		
		//Eliminamos los parking creados
		 s.deleteAllLockers();	
	}

	@Test
	public void b_findAllLockerTest () {
		
		JPAParcelService s = new JPAParcelService();
		//Añadimos elementos en la bbdd
		s.createLocker("PruebaA", "DireccionA", 0, 0, 200, 5);
		s.createLocker("PruebaB", "DireccionB", 50, 50, 50, 100);
		s.createLocker("PruebaC", "DireccionC", 0, -40, 25, 40);
		s.createLocker("PruebaD", "DireccionD", -40, 0, 12, 5);

		List<Locker> listaLockers = s.findAllLockers();
		assertSame (listaLockers.size(), 4);
		
		s.deleteAllLockers();	
		
	}
	
	@Test
	public void c_findLockerTestOK () {
		JPAParcelService s = new JPAParcelService();
		s.createLocker("PruebaA", "DireccionA", 0, 0, 200, 5);

		List<Locker> listaLockers = s.findAllLockers();
		Locker l = s.findLocker(listaLockers.get(0).getCode());
		assertNotNull (l);
		
		Locker lNoExiste = s.findLocker(345L);
		assertNull(lNoExiste);
		
		 s.deleteAllLockers();	
	}

	
	@Test
	public void d_deliverParcelTest() {
		JPAParcelService s = new JPAParcelService();
		Locker newLocker=s.createLocker("PruebaA", "DireccionA", 0, 0, 200, 5);
		//Long latestLockerCode = s.findAllLockers().get(0).getCode();
		Parcel p = s.deliverParcel(newLocker.getCode(), 001, 5,LocalDate.now().plusDays(1));
	
		assertNotNull(p);
		
		//Vaciamos la BBDD
		List<Locker> listaLockers =  s.findAllLockers();
		 for (Locker l: listaLockers) {
				 s.deleteAllParcelsByLockerID (l.getCode());
		 }
		 s.deleteAllLockers();
	}
	
	
	
	@Test
	public void e_availableCompartmentsTestOK() {
		JPAParcelService s = new JPAParcelService();

		s.createLocker("Prueba", "Direccion", 0, 0, 5, 5);
		

		List<Locker> listaLockers =  s.findAllLockers();
		Long latestLockerCode = listaLockers.get(0).getCode();
		s.deliverParcel(latestLockerCode, 001, 1,LocalDate.now().minusDays(7)); //7 dias antes
		s.deliverParcel(latestLockerCode, 002, 1,LocalDate.now().plusDays(1));	//mañana
		s.deliverParcel(latestLockerCode, 003, 10,LocalDate.now().minusDays(3));//hace 3 dias
		s.deliverParcel(latestLockerCode, 004, 10,LocalDate.now().plusDays(2));	//Pasado mañana

		 int num = s.availableCompartments(latestLockerCode, LocalDate.now().plusDays(1), 10) ;
		 assertSame(num, 4);
		 
		 //Vaciamos la BBDD
		 for (Locker l: listaLockers) {
				 s.deleteAllParcelsByLockerID (l.getCode());
		 }
		 s.deleteAllLockers();
		 
	}
	
	@Test
	public void e_retrieveParcelTestOK() {
		
		JPAParcelService s = new JPAParcelService();

		s.createLocker("Prueba", "Direccion", 0, 0, 5, 4);
		List<Locker> lockers = s.findAllLockers();
		Long latestLockerCode = lockers.get(0).getCode();
		Parcel p1 = s.deliverParcel(latestLockerCode, 001, 1,LocalDate.now());
		Parcel p2 = s.deliverParcel(latestLockerCode, 002, 1,LocalDate.now().plusDays(1));
		
		Parcel p3 = s.deliverParcel(latestLockerCode, 003, 10,LocalDate.now());
		Parcel p4 = s.deliverParcel(latestLockerCode, 004, 10,LocalDate.now().plusDays(1));
		
		s.retrieveParcel(p1.getCode());
		//s.retrieveParcel(p2.getCode());
		s.retrieveParcel(p3.getCode());
		//s.retrieveParcel(p4.getCode());
		 int numLarge = s.availableCompartments(latestLockerCode, LocalDate.now().plusDays(1), 10) ;
		assertSame (numLarge,3);
		
		int small = s.availableCompartments(latestLockerCode, LocalDate.now().plusDays(1), 1) ;
		assertSame (small,3);
		
		//Vaciamos la BBDD
		 for (Locker l: lockers) {
				 s.deleteAllParcelsByLockerID (l.getCode());
		 }
		 s.deleteAllLockers();
	}
	
	@Test 
	public void f_deliverParcelNOK () {
		JPAParcelService s = new JPAParcelService();

		//Comprpbamos que salta la excepcion si el locker no existe
		try {
			Parcel p = s.deliverParcel(825L, 123, 3, LocalDate.now());

		}catch(ParcelServiceException parcelException) {
			assertEquals (parcelException.getMessage(), "No existe locker con id "+825 +" por favor selecciones otro");
		}
		
		Locker l = s.createLocker("prueba1", "dirPrueba1", 0, 0, 3, 3);
		
		try {
			s.deliverParcel(l.getCode(), 123, 3, LocalDate.now());
			s.deliverParcel(l.getCode(), 123, 3, LocalDate.now().plusDays(1));
			s.deliverParcel(l.getCode(), 133, 3, LocalDate.now().plusDays(1));
			s.deliverParcel(l.getCode(), 124, 3, LocalDate.now().plusDays(1));
		
		}catch(ParcelServiceException parcelException) {
			assertEquals (parcelException.getMessage(), "[ERROR][deliverParcel] No hay disponibilidad para hacer mas reservas en este armario.");
		}
		
		
		try {
			
			Parcel p = s.deliverParcel(l.getCode(), 123, 3, LocalDate.now());
			s.deliverParcel(l.getCode(), 123, 3, LocalDate.now().plusDays(2));
			s.deliverParcel(l.getCode(), 123, 3, LocalDate.now().plusDays(1));
					
		}catch(ParcelServiceException parcelException) {
			assertEquals (parcelException.getMessage(), "[ERROR][deliverParcel] Se ha superado el numero de reservas en este armario.");
		}
		
		//Vaciamos la BBDD
		List<Locker> listaLockers =  s.findAllLockers();
		for (Locker locks: listaLockers) {
			s.deleteAllParcelsByLockerID (locks.getCode());
		}
		s.deleteAllLockers();
		
	}*/
}

