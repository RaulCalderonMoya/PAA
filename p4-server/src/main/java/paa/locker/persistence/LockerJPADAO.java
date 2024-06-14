package paa.locker.persistence;

import javax.persistence.EntityManager;
import paa.locker.model.*;

public class LockerJPADAO extends JPADAO<Locker, Long> {

	public LockerJPADAO(EntityManager em, Class<Locker> entityClass) {
		super(em, entityClass);
		
	}
	

}
