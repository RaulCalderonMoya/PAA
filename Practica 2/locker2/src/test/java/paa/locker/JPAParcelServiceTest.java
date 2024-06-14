	package paa.locker;

	import static org.junit.Assert.*;

	import java.time.LocalDate;
import java.util.List;

import org.junit.FixMethodOrder;
	import org.junit.Test;
import org.junit.runners.MethodSorters;

import paa.locker.business.JPAParcelService;
	import paa.locker.persistence.Locker;
	import paa.locker.persistence.Parcel;

	
	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	//Debemos garantizar un orden de ejecucion de los tests que ser
	//resuelve utilizando esta linea que hace que se ejecuten en un orden
	
	public class JPAParcelServiceTest {
		
  //A la hora de hacer los test debemos tener en cuenta un orden y asi 
		//se van a ir ejecutando en el orden que nosotros como usuarios deseemos.
		@Test
		public void a_createLockerTest() {
			JPAParcelService s = new JPAParcelService();
			Locker l = s.createLocker("Prueba", "Direccion", 0, 0, 90, 10);
			assertTrue(l != null);
			assertTrue(l.getCode() != null);
			Locker l1 = s.findLocker(l.getCode());
			assertTrue(l1 != null);
			assertTrue(l1.equals(l));
			assertTrue(l1.getName().equals("Prueba"));
			assertTrue(l1.getAddresse().equals("Direccion"));
			assertTrue(l1.getLargeCompartments() == 90);
			assertTrue(l1.getSmallCompartments() == 10);
			assertTrue(l1.getLatitude() == 0);
			assertTrue(l1.getLongitude() == 0);
		
			//Eliminamos los parking creados
			//s.cancelParking(l.getCode());
			//Locker p2 = s.findParking(p.getCode());
			//assertNull(p2);
		
		}
		
		
		@Test
		public void b_findAllLockerTest() {
			JPAParcelService s = new JPAParcelService();
        
			//Añadir elementos en nuestra base de datos y asi probarlo
			Locker l0 = s.createLocker("Prueba0", "Direccion0", 7, 3, 120, 5);
			Locker l1 = s.createLocker("Prueba1", "Direccion1", 50, 1, 50, 10);
			Locker l2 = s.createLocker("Prueba2", "Direccion2", 12, 2, 75, 10);
			Locker l3 = s.createLocker("Prueba3", "Direccion3", 9, 3, 90, 8);

			//Al añadir elementos para hacer el test podemos comenzar a hacer pruebas
			//Creacion de una lista de Lockers
			
			
			List<Locker> listaLockers= s.findAllLockers();
			//Debemos tener en cuenta los creados aqui mas los creados previamente por la persistencia
			//Ya hay un elemento creado, mas los 4 creados en el metodo y deberian dar 5 en total
			
			assertSame(listaLockers.size(), 5);
			
		}
		
		@Test
		public void c_findLockerTest() {
			JPAParcelService s = new JPAParcelService();
			List<Locker> listaLockers= s.findAllLockers();
            
			//Creamos un locker
			Locker lock = s.findLocker(listaLockers.get(0).getCode());
          //En el test debemos probar con un elemento existente y asi vemos si 
			//esta incluido en la lista
			assertNotNull(lock);
			
			
		   //Buscamos un locker que no exista para ver si da o no null
			Locker lock1 = s.findLocker(231L);
			assertNull(lock1);
			
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
			 assertSame(num, 2);
			 
			 //Vaciamos la BBDD
			 for (Locker l: listaLockers) {
					 s.deleteAllParcelsByLockerID (l.getCode());
			 }
			 s.deleteAllLockers();
			 
		}

		
		@Test
		public void e_retrieveParcelTestOk() {
           JPAParcelService s = new JPAParcelService();
			
			s.createLocker("Prueba","Direccion", 0, 0, 5, 5);
			
			Long latestLockerCode = s.findAllLockers().get(0).getCode();
            
			s.deliverParcel(latestLockerCode, 001, 1, LocalDate.now().minusDays(7));//Elemento caducado
			
			s.deliverParcel(latestLockerCode, 001, 1,  LocalDate.now().plusDays(1));//Elemento no caducado
			
			s.deliverParcel(latestLockerCode, 001, 10, LocalDate.now().minusDays(7));
			s.deliverParcel(latestLockerCode, 001, 10,  LocalDate.now().plusDays(1));
			
			
			int num = s.availableCompartments(latestLockerCode, LocalDate.now().plusDays(1), 10);
			
			
			
		}
		
		
		
		
		
		
		

	
/**feb 28, 2022 9:14:19 A. M. org.eclipse.persistence.session./file:/C:/Users/Raúl moya/eclipse-workspace/locker2/target/classes/_paa
INFO: EclipseLink, version: Eclipse Persistence Services - 2.7.10.v20211216-fe64cd39c3
feb 28, 2022 9:14:20 A. M. org.eclipse.persistence.session./file:/C:/Users/Raúl moya/eclipse-workspace/locker2/target/classes/_paa.connection
INFO: Not able to detect platform for vendor name [UCanAccess driver for Microsoft Access databases using HSQLDB[V2000 [VERSION_4], 2]]. Defaulting to [org.eclipse.persistence.platform.database.DatabasePlatform]. The database dialect used may not match with the database you are using. Please explicitly provide a platform using property "eclipselink.target-database".
*/
}
