package paa.locker.persistence;

import java.time.LocalDate;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import paa.locker.model.*;
public class ParcelJPADAO extends JPADAO<Parcel, Long> {

	public ParcelJPADAO(EntityManager em, Class<Parcel> entityClass) {
		super(em, entityClass);
		
	}
	
	public List<Parcel> disponibilidadReservas (Long lockerCode , float maxWeight, float minWeight, LocalDate startDate, LocalDate endDate) {
		 TypedQuery<Parcel> query = em.createQuery(
			        "SELECT p FROM Parcel p WHERE p.locker.code =:lockerCode "
			        + "AND p.weight BETWEEN :minWeight AND :maxWeight "
			        + "AND p.arrivalDate BETWEEN :startDate AND :endDate", Parcel.class);
			    return query.setParameter("lockerCode", lockerCode)
			    		.setParameter("startDate", startDate)
			    		.setParameter("endDate", endDate)
			    		.setParameter("maxWeight", maxWeight)
			    		.setParameter("minWeight", minWeight)
			    		.getResultList();
		
	}
	
	//
	public List<Parcel> codePaquetes (Long lockerCode) {
		 TypedQuery<Parcel> query = em.createQuery(
			        "SELECT p FROM Parcel p WHERE p.locker.code =:lockerCode", Parcel.class);
			    return query.setParameter("lockerCode", lockerCode)
			    		.getResultList();
		
	}
	//
	public List<Parcel> paquetesDireccionyFecha (int addressee ,LocalDate startDate, LocalDate endDate) {
		 TypedQuery<Parcel> query = em.createQuery(
			        "SELECT p FROM Parcel p WHERE p.addressee =:addressee AND p.arrivalDate BETWEEN :startDate AND :endDate", Parcel.class);
			    return query
			    		.setParameter("addressee", addressee)
			    		.setParameter("startDate", startDate)
			    		.setParameter("endDate", endDate)
			    		.getResultList();
			    		
	}
	//
	public List<Parcel> paquetesCodeyAddress (Long lockerCode,int addressee, LocalDate startDate, LocalDate endDate) {
		 TypedQuery<Parcel> query = em.createQuery(
			     "SELECT p FROM Parcel p WHERE p.locker.code =:lockerCode AND p.addressee =:addressee AND p.arrivalDate BETWEEN :startDate AND :endDate",Parcel.class);
			    return query
			    		.setParameter("lockerCode", lockerCode)
			    		.setParameter("addressee", addressee)
			    		.setParameter("startDate", startDate)
			    		.setParameter("endDate", endDate)
			    		.getResultList();
		
	}
	//
	public Parcel findParcelsOnDate (Long parcelCode) {
		
		 TypedQuery<Parcel> query = em.createQuery(
			    "SELECT p FROM Parcel p WHERE p.code =:parcelCode "
			    + "AND (p.arrivalDate =:hoy OR p.arrivalDate=:tomorrow OR p.arrivalDate=:afterTomorrow OR p.arrivalDate=:yesterday OR p.arrivalDate=:beforeYesterday)",Parcel.class);
			    return query.setParameter("parcelCode", parcelCode)
			    		.setParameter("hoy",  LocalDate.now())
			    		.setParameter("tomorrow", LocalDate.now().plusDays(1))
			    		.setParameter("afterTomorrow", LocalDate.now().plusDays(2))
			    		.setParameter("yesterday", LocalDate.now().minusDays(1))
			    		.setParameter("beforeYesterday", LocalDate.now().minusDays(2))
			    		.getSingleResult();
			    
			
	}

	
	
}
