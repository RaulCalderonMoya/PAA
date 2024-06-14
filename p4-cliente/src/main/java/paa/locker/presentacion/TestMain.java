package paa.locker.presentacion;

import java.time.LocalDate;

import paa.locker.business.RemoteParcelService;

public class TestMain {

	public static void main(String[] args) {
		RemoteParcelService rps= new RemoteParcelService();
		
		
		//Con esto voy haciendo la configuracion y segun eso voy probando uno a uno para que sea mas facil
		//encontrar errores

		/*rps.findAllLockers();
		System.out.println("\n\n");
		rps.findLocker(1522L);
		System.out.println("\n\n");
		*/
		//rps.createLocker("Prueba1", "calle de pruebas", 1, 9, 3, 3);
	
		//rps.deliverParcel(151L, 60, 0.4F, LocalDate.now());
		//rps.findAllLockers();
		rps.retrieveParcel(161L);
		//rps.findLocker(151L);
		//rps.availablecompartments();

	}

}
