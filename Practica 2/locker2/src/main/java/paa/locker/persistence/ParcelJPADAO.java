package paa.locker.persistence;

import java.time.LocalDate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

//@NamedQuery(name = "existsByName", query = "SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b where b.licencePlate= :licencePlate")
@NamedQuery(name = "findBookings", query = "SELECT b FROM Booking AS b WHERE b.parking_code =:parking_code AND b.date =:date")
@NamedQuery(name = "getBookingsByLicencePlateAndDate", query = "SELECT b FROM Booking AS b WHERE b.licencePlate =:licencePlate AND b.date =:date")

public class ParcelJPADAO extends JPADAO<Parcel ,Long >{
	
	public ParcelJPADAO(EntityManager em, Class<Parcel> entityClass) {
		super(em, entityClass);
	}
	
	public List<Parcel> getParcelByWeight (Long lockerCode , double weight, int maxDaysInLocker){
		
		 TypedQuery<Parcel> query = em.createQuery(
				 //Seleccciona p de Parcel donde el codigo de p es el siguiente : 
				 //Utilizando la teoria del tema nos ayudamos para hacer las siguientes sentencias 
				 
				 
			        "SELECT p FROM Parcel p WHERE p.locker.code =:lockerCode AND p.weight=:weight", Parcel.class);
			    return query.setParameter("lockerCode", lockerCode).setParameter("weight", weight).getResultList();
			    		
			    		
			    		
		
	}

	 //Metodo complementario para eliminar los campos del armario(Nota: Un armario esta formado por N parcels )
	public void deleteAllParcel () {
		 em.createQuery("DELETE FROM Locker", Parcel.class);
		    
	}
	
	public List<Parcel> getParcelsByLockerID (Long lockerCode) {
		 TypedQuery<Parcel> query = em.createQuery(
			        "SELECT p FROM Parcel p WHERE p.locker.code =:lockerCode", Parcel.class);
			    return query.setParameter("lockerCode", lockerCode).getResultList();
			    		
		
	}
	
	public List<Parcel> getParcelsByLockerIDAndAddresse (Long lockerCode, int addressee) {
		 TypedQuery<Parcel> query = em.createQuery(
			        "SELECT p FROM Parcel p WHERE p.locker.code =:lockerCode AND p.addressee =:addressee", Parcel.class);
			    return query.setParameter("lockerCode", lockerCode).setParameter("addressee", addressee).getResultList();
			    		
			    		
		
	}
	
	public List<Parcel> getParcelsByAddresseeAndDate (int addressee , int maxDaysInLocker) {
		 TypedQuery<Parcel> query = em.createQuery(
			        "SELECT p FROM Parcel p WHERE p.addressee =:addressee", Parcel.class);
			    return query.setParameter("addressee", addressee)
			    		.getResultStream()
			    		.filter(p -> p.getArrivalDate().isAfter(LocalDate.now().minusDays(maxDaysInLocker)))
		                .collect(Collectors.toList());
			    		
		
	}
	
	public List<Parcel> getParcelByWeightAndDate (Long lockerCode , double weight, int maxDaysInLocker){
		
		 TypedQuery<Parcel> query = em.createQuery(
			        "SELECT p FROM Parcel p WHERE p.locker.code =:lockerCode AND p.weight=:weight", Parcel.class);
			    return query.setParameter("lockerCode", lockerCode)
			    		.setParameter("weight", weight)
			    		.getResultStream()
			    		.filter(p -> p.getArrivalDate().isBefore(LocalDate.now().minusDays(maxDaysInLocker)))
		                .collect(Collectors.toList());
	}
	
	


}
