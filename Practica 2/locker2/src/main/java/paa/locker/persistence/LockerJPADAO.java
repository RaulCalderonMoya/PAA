package paa.locker.persistence;

import javax.persistence.EntityManager;


public class LockerJPADAO extends JPADAO<Locker , Long>{
     //Se comunica con JPADAO para hacer llamadas a la base de datos 
	// para que sea realmente persistente, esto es de verdadera 
	//importancia y a tener en cuenta a la hora de programar 
	public LockerJPADAO(EntityManager em,Class<Locker> entityClass) {
		
		super(em, entityClass);
	}
	
}

