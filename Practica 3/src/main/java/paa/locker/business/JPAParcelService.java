package paa.locker.business;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import paa.locker.persistence.DAOException;
import paa.locker.persistence.JPADAO;
import paa.locker.persistence.Locker;
import paa.locker.persistence.LockerJPADAO;
import paa.locker.persistence.Parcel;
import paa.locker.persistence.ParcelJPADAO;

public class JPAParcelService implements ParcelService{

	//Se encuentra definido en el persistence.xml en la etiqueta  <persistence-unit name="paa">
	private static final String PERSISTENCE_UNIT_NAME = "paa"; 
	
	private static final double MAX_LONGITUD = 180.0;
	private static final double MIN_LONGITUD = -180.0;
	private static final double MAX_LATITUD = 90.0;
	private static final double MIN_LATITUD = -90.0;
	
	private EntityManagerFactory factory;
	
	public JPAParcelService () {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}
	

	
	
	
	public Locker createLocker(String name, String address, double longitude, double latitude, int largeCompartments,
			int smallCompartments) {
		
		//Falta por comprobar que el elemento no exista ya en la tabla
		
		if (name ==null || name.isEmpty() || address==null || address.isEmpty()|| largeCompartments < 0 || smallCompartments < 0|| (longitude<MIN_LONGITUD|| longitude >MAX_LONGITUD)
				|| (latitude<MIN_LATITUD || latitude >MAX_LATITUD) ) {
			throw new ParcelServiceException("No se ha podido crear el armario los datos no son correctos.");
		}
		
		
		Locker lock = null;
		EntityManager em = factory.createEntityManager(); //Creamos el gestor de entidades
		EntityTransaction et = em.getTransaction(); //Este objeto es necesario siempre que vayamos a modificar la bbdd (añadir, elimnar o actualizar)
		JPADAO<Locker,Long> dao ;
		
			lock=new Locker(null, name,  address,  longitude, latitude, largeCompartments, 
					smallCompartments);
			try {
				et.begin();
				dao = new JPADAO<Locker,Long> (em, Locker.class );
				
				
				lock= dao.create(lock);
				
				
				et.commit();//Aseguramos que se actualizan los datos en la bbdd
				
				
				
				em.refresh(em.find(Locker.class, lock.getCode()));
				em.refresh(lock);
				
			}catch (DAOException e) {
				
				if (et.isActive()) {
					
					et.rollback();//Aseguramos que no se actualizan los datos de la base de datos
				}
			}catch (Exception e) {
				
				if (et.isActive()) {
					et.rollback();
				}
				
			}finally{ //el codigo que hay aqui dentro se ejecuta tanto si salta una excepcion como si todo va bien.
				em.close();
			}
		
			
			return lock;
	}


	@Override
	public Locker findLocker(Long lockerCode) {
		EntityManager em = factory.createEntityManager();
		
		LockerJPADAO dao = new LockerJPADAO (em, Locker.class );
		Locker l = dao.find(lockerCode);//Devuelve null si no encuentra el elemento en la BBDD
										//Devuelve el objeto 'Locker' si lo encuentra
		em.close();
		return l;
	}


	public List<Locker> findAllLockers() {
		EntityManager em = factory.createEntityManager();
		
		LockerJPADAO dao = new LockerJPADAO (em, Locker.class );
		List<Locker> listaLockers = dao.findAll(); 	//Devuelve un array listo con todos los elementos d ela tabla 
	
		em.close();
		
		return listaLockers;
	}

		@Override
		public int availableCompartments(Long lockerCode, LocalDate date, float parcelWeight) {
			if(parcelWeight<=0 || parcelWeight>LargeMaxWeight) {
				throw new ParcelServiceException("El peso del paquete no es correcto.");
			}
			
			Locker l = findLocker(lockerCode);
			if(l == null) {
				
				throw new ParcelServiceException("No se ha encontrado ningún locker con el código: " +lockerCode+ ".");
			}
			
			EntityManager em = factory.createEntityManager();
			ParcelJPADAO DAO = new ParcelJPADAO(em, Parcel.class);
			
			
			
			int compartments = 0;
			if(parcelWeight<=SmallMaxWeight) {
				
				
				compartments = l.getSmallCompartments();
				for(int i = 0; i<MaxDaysInLocker; i++) {
					compartments = Math.min(compartments, l.getSmallCompartments()-DAO.getReservedAvailability(lockerCode, SmallMaxWeight, 0, date.minusDays(MaxDaysInLocker-1-i), date.plusDays(i)).size());
				}
				
			}else {
				
				
				compartments = l.getLargeCompartments();
				for(int i = 0; i<MaxDaysInLocker; i++) {
					compartments = Math.min(compartments, l.getLargeCompartments()-DAO.getReservedAvailability(lockerCode, LargeMaxWeight, 1.01F, date.minusDays(MaxDaysInLocker-1-i), date.plusDays(i)).size());
				}
			}
			return compartments;
		}

	

	@Override
	public Parcel deliverParcel(Long lockerCode, int addressee, float weight, LocalDate arrivalDate) {
			
		if (weight<=0 || weight>LargeMaxWeight) {
			throw new ParcelServiceException ("[ERROR]El peso seleccionado no puede ser 0.");
		}
		
		EntityManager emLocker = factory.createEntityManager();
		EntityTransaction etLocker = emLocker.getTransaction(); 		

		EntityManager emParcel = factory.createEntityManager();
		EntityTransaction etParcel = emParcel.getTransaction();
		
		
		
		Locker locker = findLocker(lockerCode); //Obtener el objeto Locker asociado a la reserva que vamos a realizar
		if (locker == null) {
			throw new ParcelServiceException ("No existe locker con id "+lockerCode +" por favor selecciones otro");
		}

		
				int disponibilidad =  availableCompartments(lockerCode, arrivalDate, weight);
				if (disponibilidad == 0) {
					throw new ParcelServiceException ("[ERROR][deliverParcel] No hay disponibilidad para hacer mas reservas en este armario.");
				}
				
				ParcelJPADAO dao = new ParcelJPADAO (emParcel, Parcel.class );

			
				List<Parcel> parcelUserReservedInLocker = dao.getParcelsByLockerIDAndAddresse (lockerCode, addressee, arrivalDate, LocalDate.now().plusDays(MaxDaysInLocker)) ;
				if (parcelUserReservedInLocker!=null) {
					if (parcelUserReservedInLocker.size()>MaxParcelsInLocker) {
						throw new ParcelServiceException ("[ERROR][deliverParcel] Se ha superado el numero de reservas en este armario.");
					}
				}
						
				
				List<Parcel> parcelsList = dao.getParcelsByDateAndAddresse (addressee,  arrivalDate, LocalDate.now().plusDays(MaxDaysInLocker));
				if (parcelsList!=null) {
					if(	parcelsList.size()> MaxParcelsAnywhere) {
						throw new ParcelServiceException ("[ERROR][deliverParcel] Ha superado el numero de reservas.");
					}
				}
		
		
		
		LockerJPADAO lockerDao = new LockerJPADAO (emLocker, Locker.class);
		dao = new ParcelJPADAO (emParcel, Parcel.class );
		
		Parcel parcel = new Parcel(null, addressee, weight, arrivalDate, locker);
		try {
			etParcel.begin();
			parcel=dao.create(parcel);
			etParcel.commit();//Aseguramos que se actualizan los datos en la bbdd
			
			emParcel.refresh(emParcel.find(Locker.class, parcel.getLocker().getCode()));
			emParcel.refresh(parcel);
			
		}catch (DAOException e) {
			if (etParcel.isActive()) {
				etParcel.rollback();//Aseguramos que no se actualizan los datos de la base de datos
			}
		}catch (Exception e) {
			if (etParcel.isActive()) {
				etParcel.rollback();
			}
		}finally{
			emParcel.close();
		}
		return parcel;
	}


	public void retrieveParcel(Long parcelCode) {

		EntityManager em=factory.createEntityManager();
		ParcelJPADAO jbd=new ParcelJPADAO(em, Parcel.class);
		Parcel b=jbd.find(parcelCode);
		//LocalDate arrivalDate=b.getArrivalDate();
		try {
		Parcel listaParcels = jbd.findParcelsOnDate ( parcelCode);
		if (b != null && listaParcels!=null){			//cancelamos la reserva
			EntityTransaction et= em.getTransaction();
			try {
				et.begin();
				jbd.delete(b);
				et.commit();
				
				em.refresh(em.find(Locker.class, b.getLocker().getCode()));
				em.refresh(b);
				
			}catch(Exception e) {
				if(et.isActive()) {
					et.rollback();
				}
			}finally {
				em.close();
			}	
		}else {
			throw new ParcelServiceException("[Error]No se ha eliminado el parcel.");
		}	
		}catch(Exception e) {
			throw new ParcelServiceException ("[Error]No se ha eliminado el parcel revise la fecha.");
	
		}
	}
	
	
	/*
	
	/*USADOS PARA LAS PRUEBAS*/
	/*
	public List<Parcel> deleteAllParcelsByLockerID (Long lockerID) {
		
		EntityManager em = factory.createEntityManager();
		ParcelJPADAO dao = new ParcelJPADAO (em, Parcel.class );
		List<Parcel> listaParcels = dao.getParcelsByLockerID(lockerID); 	//Devuelve un array listo con todos los elementos d ela tabla 

		for (Parcel p : listaParcels) {
			eliminarAllParcelsFromLockerID(p.getCode());
		}
		em.close();
		
		return listaParcels;
	}
	
	private void eliminarAllParcelsFromLockerID(Long parcelCode) {

		EntityManager em=factory.createEntityManager();
		ParcelJPADAO jbd=new ParcelJPADAO(em, Parcel.class);
		Parcel b=jbd.find(parcelCode);
	
			EntityTransaction et= em.getTransaction();
			try {
				et.begin();
				jbd.delete(b);
				et.commit();
				
			}catch(Exception e) {
				if(et.isActive()) {
					et.rollback();
				}
			}finally {
				em.close();
			}	
	
	}
	
	public void deleteAllLockers () {
		
		List<Locker> lockers =  findAllLockers();
		
		for (Locker l : lockers) {
			EntityManager em=factory.createEntityManager();
			LockerJPADAO jbd=new LockerJPADAO(em, Locker.class);
			EntityTransaction et= em.getTransaction();
			try {
				et.begin();
				jbd.delete(l);
				et.commit();
			}catch(Exception e) {
				if(et.isActive()) {
					et.rollback();
				}
			}finally {
				em.close();
			}	
		}

	}
	*/
	
}	