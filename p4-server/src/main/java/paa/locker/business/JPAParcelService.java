package paa.locker.business;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;


import paa.locker.persistence.DAOException;

import paa.locker.model.Locker;
import paa.locker.persistence.LockerJPADAO;
import paa.locker.model.Parcel;
import paa.locker.persistence.ParcelJPADAO;


public class JPAParcelService implements ParcelService {
	//Se encuentra definido en el persistence.xml en la etiqueta  <persistence-unit name="paa">
	private static final String PERSISTENCE_UNIT_NAME = "paa"; 


	private static final double MAX_LONGITUD = 180.0;
	private static final double MIN_LONGITUD = -180.0;
	private static final double MAX_LATITUD = 90.0;
	private static final double MIN_LATITUD = -90.0;
	
	private EntityManagerFactory factory;
	private  EntityManager em; //Objeto del tipo entityManager, para 
	
	
	public JPAParcelService () {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		
	}
	
	//NUEVO CONSTRUCTOR DE LA PRACTICA 4 //
	//IMPORTANTE PONERLO Y GESTIONAR LOS ERRORES//
	public JPAParcelService(String path) {
		 Map<String, String> properties = new HashMap<String, String>();
		 properties.put("javax.persistence.jdbc.url", "jdbc:derby:"+path+";create=true");
		 factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
		 em = factory.createEntityManager();
		 }
	
	//********************************************/
	
	
	
	/**
	 * Crea un nuevo armario con los parámetros indicados y lo devuelve. Debe
	 * comprobar que los parámetros tienen sentido (p.ej., número de
	 * compartimentos no negativo, longitud y latitud en los rangos correctos,
	 * nombre y dirección no vacíos…); en caso contrario, deberá lanzar una
	 * excepción de tipo ParcelServiceException con un mensaje adecuado.
	 * @param name 
	 * @param address
	 * @param longitude
	 * @para latitude
	 * @param largeCompartments
	 * @param smallCompartments
	 * 
	 * @return objeto del tipo locker dado de alta en la BBDD
	 * 
	 * @throws ParcelServiceException se lazara si los parametros no son correctos
	 * */
	@Override
	public Locker createLocker(String name, String address, double longitude, double latitude, int largeCompartments,
			int smallCompartments) {
		
		
		if(name == null || name == " ") {
			throw new ParcelServiceException("No se ha podido crear el armario, el dato del nombre no es correcto.");
		}
		if(address == null || address == " ") {
			throw new ParcelServiceException("No se ha podido crear el armario, el dato de la direccion no es correcto.");
		}
		if(largeCompartments< 0) {
			throw new ParcelServiceException("No se ha podido crear el armario, el dato del numero de los compartimentos grandes es incorrecto.");
		}
		if(smallCompartments< 0) {
			throw new ParcelServiceException("No se ha podido crear el armario, el dato del numero de los compartimentos pequeños es incorrecto.");
		}
		
		if(longitude<MIN_LONGITUD|| longitude >MAX_LONGITUD) {
			
			throw new ParcelServiceException("No se ha podido crear el armario, los datos de la longitud no están entre el máximo y mínimo.");
		}
        if(latitude<MIN_LATITUD || latitude >MAX_LATITUD) {
			
			throw new ParcelServiceException("No se ha podido crear el armario, los datos de la latitud no están entre el máximo y mínimo.");
		}

		
		Locker lock = null;
		em = factory.createEntityManager();
		EntityTransaction et = em.getTransaction();
		LockerJPADAO dao;
		
		lock=new Locker(null, name,  address,  longitude, latitude, largeCompartments, smallCompartments );
		try {
			et.begin();
			dao = new LockerJPADAO (em, Locker.class );
			lock= dao.create(lock);
			et.commit();
			em.refresh(em.find(Locker.class, lock.getCode())); //
			em.refresh(lock); // solucion correo
			
		}catch (DAOException e) {
			if (et.isActive()) {
				et.rollback();
			}
		}catch (Exception e) {
			if (et.isActive()) {
				et.rollback();
			}
		}finally{
			em.close();
		}
	
		return lock;
	}	
		

	
	@Override
	public Locker findLocker(Long lockerCode) {
		 em = factory.createEntityManager();
		LockerJPADAO dao = new LockerJPADAO ( em, Locker.class);
		Locker l = dao.find(lockerCode);//Devuelve null si no encuentra el elemento en la BBDD
		                                //Devuelve el objeto 'Locker' si lo encuentra
		
		em.close();
		return l;
	}
	
	
	
	@Override
	/**
	 * Busca en la bbdd todos los lockers almacenados. 
	 * @return Devuelve una lista de todos los armarios existentes o null si no hay lockers
	 * */
	public List<Locker> findAllLockers() {
		
		em = factory.createEntityManager(); 
		
		LockerJPADAO dao = new LockerJPADAO (em, Locker.class);
		
		List <Locker> lista = dao.findAll();
		
		
		
			
		em.close();		
				
		return lista;
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
		em = factory.createEntityManager();
		ParcelJPADAO DAO = new ParcelJPADAO(em, Parcel.class);
		int huecosLibres = 0;
		if(parcelWeight<=SmallMaxWeight) {
			huecosLibres = l.getSmallCompartments();
			for(int i = 0; i<MaxDaysInLocker; i++) {
				huecosLibres = Math.min(huecosLibres, l.getSmallCompartments()-DAO.disponibilidadReservas(lockerCode, SmallMaxWeight, 0, date.minusDays(MaxDaysInLocker-1-i), date.plusDays(i)).size());
			}
		}else {
			huecosLibres = l.getLargeCompartments();
			for(int i = 0; i<MaxDaysInLocker; i++) {
				huecosLibres = Math.min(huecosLibres, l.getLargeCompartments()-DAO.disponibilidadReservas(lockerCode, LargeMaxWeight, 1.01F, date.minusDays(MaxDaysInLocker-1-i), date.plusDays(i)).size());
			}
		}
		return huecosLibres;
				
				
			}
	
	
	
	
	@Override
	public Parcel deliverParcel(Long lockerCode, int addressee, float weight, LocalDate arrivalDate) {
		if (weight<=0 || weight>LargeMaxWeight) {
			throw new ParcelServiceException ("El peso seleccionado no puede ser 0.");
		}
		em = factory.createEntityManager();
		EntityTransaction et = em.getTransaction(); 		
		Locker locker = findLocker(lockerCode); 
		if (locker == null) {
			throw new ParcelServiceException ("No existe locker con "+lockerCode +",seleccione otro");
		}
		int huecosLibres =  availableCompartments(lockerCode, arrivalDate, weight);
		if (huecosLibres == 0) {
			throw new ParcelServiceException (" En este armario ya no se pueden hacer más reservas.");
		}
		ParcelJPADAO dao = new ParcelJPADAO (em, Parcel.class );
		
		List<Parcel> listaPaquetesReservadosArmario = dao.paquetesCodeyAddress ( lockerCode,  addressee, arrivalDate, LocalDate.now().plusDays(MaxDaysInLocker)) ;
		if (listaPaquetesReservadosArmario!=null) {
			if (listaPaquetesReservadosArmario.size()>MaxParcelsInLocker) {
				throw new ParcelServiceException ("Se ha superado el maximo de numero de armarios reservados.");
			}
            
			
		}
		List<Parcel> listaPaquetes = dao.paquetesDireccionyFecha (addressee, arrivalDate, LocalDate.now().plusDays(MaxDaysInLocker));
		if (listaPaquetes!=null) {
			if(	listaPaquetes.size()> MaxParcelsAnywhere) {
				throw new ParcelServiceException ("Se ha superado el maximo de numero de paquetes reservados.");
			}
			
		}
		Parcel b = new Parcel(null, addressee, weight, arrivalDate, locker);
		try {
			et.begin();
			b= dao.create(b); 
			et.commit();
			
			em.refresh(em.find(Locker.class, b.getLocker().getCode()));
			em.refresh(b);
			
			
					
		}catch (DAOException e) {
			if (et.isActive()) {
				et.rollback();
			}
		}catch (Exception e) {
			if (et.isActive()) {
				et.rollback();
			}
		}finally{
			em.close();
		}
		return b;
	}
	
	
	
	
	
	@Override
	public void retrieveParcel(Long parcelCode) {

		 em=factory.createEntityManager();
		ParcelJPADAO baseDatos=new ParcelJPADAO(em, Parcel.class);
		Parcel b=baseDatos.find(parcelCode);
		

		try {
			Parcel listaParcels = baseDatos.findParcelsOnDate ( parcelCode);
			if (b != null && listaParcels!=null && b.getArrivalDate().equals(LocalDate.now()))  {
				
				EntityTransaction et= em.getTransaction();
				try {
					et.begin();
					baseDatos.delete(b);
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
				throw new ParcelServiceException("El paquete no se ha podido eliminar.");
			}
			
		}catch(Exception e) {
			throw new ParcelServiceException ("No se ha eliminado el paquete, revise la fecha.");
	
		}
				
	}

	

	
		
	
}
