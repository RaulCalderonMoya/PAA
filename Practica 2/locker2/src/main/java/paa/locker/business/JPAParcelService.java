package paa.locker.business;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
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

public class JPAParcelService implements ParcelService {
	//Nombre de la unidad de persistencia 
	private static final String PERSISTENCE_UNIT_NAME= "paa";

	
	//He decidido poner estos valores para constantes debido a que nunca 
	//pueden ser infinitos estos valores, tienen el limite definido
	private static final double MAXIMA_LONGITUD= 180.0;
	private static final double MINIMA_LONGITUD= -180.0;
    private static final double MAXIMA_LATITUD=90.0;
    private static final double MINIMA_LATITUD= -90.0;
    
    //Obejeto factory de tipo EntityManagerFactory con el que despues podremos dar lugar al EntityManager
   private EntityManagerFactory factory; 
	
	public JPAParcelService () {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}
 
	
	
	//En este caso si no recibe los parametros esperados debe saltar la excepcion, para garantizar un correcto funcionamiento del programa

	@Override
	public Locker createLocker(String name, String address, double longitude, double latitude, int largeCompartments,int smallCompartments) {
		//Con un elemento erróneo debe saltar la excepcion
		if (name ==null ||address==null || largeCompartments < 0 || smallCompartments < 0|| (longitude<MINIMA_LONGITUD|| longitude >MAXIMA_LONGITUD)|| (latitude<MINIMA_LATITUD || latitude >MAXIMA_LATITUD) ) {
				
			throw new ParcelServiceException("No se ha podido crear el armario debido a que los datos no son los esperados.");
			
			
		}
		//Debemos comprobar que el elemento no exista en la tabla tambien
		Locker lock = null;
		EntityManager em = factory.createEntityManager();
		EntityTransaction et = em.getTransaction();
		
		JPADAO<Locker,Long> dao ;
		
		
		lock=new Locker(null, name,  address,  longitude, latitude, largeCompartments, smallCompartments );
		
		
		//Bloque try-catch que me sirve para recoger diferentes casos posibles en los que puede saltar una excepcion
		
		try {
			et.begin();
			dao = new JPADAO<Locker,Long> (em, Locker.class);
			lock= dao.create(lock);
			et.commit();//No se deben actualizar los datos en la base de datos en esta ocasion
			
		}catch (DAOException e) {
			if (et.isActive()) {
				et.rollback();//Debemos ver  que no se actualizan los datos de la base de datos
			}
		}catch (Exception e) {
			if (et.isActive()) {
				et.rollback();//Si hay problemas se deshacen los cambios en la base de datos
			}
		}finally{
			em.close();
		}
		return lock;
		
		
	}
	
	@Override
	public Locker findLocker(Long lockerCode) {
		EntityManager em = factory.createEntityManager(); //Creamos el entityManager 
		
		LockerJPADAO dao = new LockerJPADAO (em, Locker.class);
		
		Locker l1 = dao.find(lockerCode);//Devuelve null si no encuentra el elemento en la Base de Datos 
		
		em.close();//El entityManager siempre hay que cerrarlo
		return l1;
		
		
		//Si encuentra el locker lo devuelve en este metodo
	}


	@Override
	public List<Locker> findAllLockers() {
		EntityManager em = factory.createEntityManager();
		LockerJPADAO dao = new LockerJPADAO(em, Locker.class);
		
		List<Locker>listaLockers = dao.findAll();
		em.close();//El entityManager siempre hay que cerrarlo //
		           //Debemos poner esta clausula para cerrar EntityManager
		    
		
		return listaLockers;
		//Devuevle una lista con los elementos que haya encontrado
	}

	public int availableCompartments(Long lockerCode, LocalDate date, float parcelWeight) {
		//El peso del paquete debe ser positivo y no superior a LargeMaxWeight; de lo contrario se 
		//lanzará una excepción de tipo ParcelServiceException con un mensaje adecuado.
		
		
		if (parcelWeight<0 || parcelWeight>LargeMaxWeight) {
			throw new ParcelServiceException ("[ERROR][ParcelServiceException][availableCompartments] peso del paquete no admitido.");
		}
		
		//El armario debe existir y si no existe se manda una
		//excepción  ParcelServiceException 
		Locker armario = findLocker(lockerCode);
		if (armario ==  null) {
			throw new ParcelServiceException ("¡Atención! No se ha"	+ " encontrado un Locker con el codigo "+lockerCode);
				
		}
		EntityManager em = factory.createEntityManager(); //El entityManager es necesario crearlo de nevo en esta clase 
		ParcelJPADAO parcelJPA = new ParcelJPADAO (em, Parcel.class);
				
		// Un paquete puede ocupar un compartimento del armario como máximo maxDaysInLocker días,
		//  contando el de la entrega. Si pasado este tiempo no ha sido retirado, caducará y su compartimento podrá ser reutilizado por otro envío.
	
		// Un paquete grande (peso > smallMaxWeight) solo se puede alojar en un compartimento grande, y uno pequeño solo en un compartimento pequeño.
				
		List<Parcel> res;
		
		int disponible =0; //(Total de parcelas pequeñas o grandes)- (numero de parcels ocupados y no caducados)
		
		//Obtenemos todos los parcel asociados a un locker
		List<Parcel> listaParcelbyLocker = parcelJPA.getParcelsByLockerID (lockerCode); 
		
		//Obtenemos una lista con los parcel disponible.
		
		//Ademas debemos mirar las fechas y el tamaño del paquete
		
		List<Parcel> parcelasPequeñas = new ArrayList<Parcel>();
		List<Parcel> parcelasGrandes = new ArrayList<Parcel>();

		for (Parcel parcel: listaParcelbyLocker) {
			
			
			if (parcel.getWeight()<= SmallMaxWeight && parcel.getArrivalDate().isAfter(LocalDate.now().minusDays(MaxDaysInLocker+1))) {
					
				parcelasPequeñas.add(parcel); //Añadimos los elementos que ya estan listos (elementos ya vigentes)

			}
			if (parcel.getWeight()> SmallMaxWeight && parcel.getArrivalDate().isAfter(LocalDate.now().minusDays(MaxDaysInLocker+1))){
					
				parcelasGrandes.add(parcel);

			}
		}
		
		if (parcelWeight<= SmallMaxWeight) {
			disponible = armario.getSmallCompartments()-parcelasPequeñas.size();//total de cajones restados a los cajones que ya estan reservados

		}else {
			disponible = armario.getSmallCompartments()-parcelasGrandes.size();//total de cajones restados a los cajones que ya estan reservados

		}
		
		return disponible;
	}


	@Override
	public Parcel deliverParcel(Long lockerCode, int addressee, float weight, LocalDate arrivalDate) {
		Locker locker = findLocker(lockerCode);
		//Debemos obtner el objeto locker		
		
		
		
		EntityManager em = factory.createEntityManager(); //Creamos el entitymanager y entityTransaction
		
		EntityTransaction et = em.getTransaction();
		
		//Hay que poner <Parcel, Long> al controlar la generica aqui
		
		 JPADAO<Parcel, Long> dao = new JPADAO(em, Parcel.class);
		 
		 //Ponemos null para que lo genere la base de datos en Parcel b de ahora después
		 
		 Parcel p1 = new Parcel(null, addressee, weight, arrivalDate, locker);
		 
		 try {
				et.begin();
				p1=dao.create(p1);
				et.commit();//Aseguramos que se actualizan los datos en la base de datos con la clausula commit
				
			}catch (DAOException e) {
				if (et.isActive()) {
					et.rollback();//Aseguramos que no se actualizan los datos de la base de datos
				}
			}catch (Exception e) {
				if (et.isActive()) {
					et.rollback();
				}
			}finally{
				em.close();
			}
			return p1;

		
	}
public List<Parcel> deleteAllParcelsByLockerID (Long lockerID) {
		
		EntityManager em = factory.createEntityManager();
		
		ParcelJPADAO dao = new ParcelJPADAO (em, Parcel.class);
		
		List<Parcel> listaParcels = dao.getParcelsByLockerID(lockerID); 	//Devuelve un array listo con todos los elementos de la tabla 

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
	
	//Este metodo lo utilizo para evitar errores relacionados con el guardado de elementos en la base de datos 
	// de esta manera nos podemos asegurar que esta vacia 
	
public void deleteAllLockers () {
		
		List<Locker> lockers =  findAllLockers();
		
		for (Locker l : lockers) {
			EntityManager em=factory.createEntityManager(); //El entityManager es necesario crearlo de nevo en esta clase 
			
			LockerJPADAO j=new LockerJPADAO(em, Locker.class);
			
			EntityTransaction et= em.getTransaction();
			
			try {
				et.begin();
				j.delete(l);
				et.commit();
				
			}catch(Exception e) {
				
				if(et.isActive()) {
					et.rollback();
				}
				
			}finally {
				em.close(); //Siempre debemos cerrar el objeto entityManager 
			}	
		}

	}

	
	//Elimina un parcel y no un locker
	@Override
	public void retrieveParcel(Long parcelCode) {
		EntityManager em = factory.createEntityManager();//El entityManager es necesario crearlo de nevo en esta clase 
		
		ParcelJPADAO j = new ParcelJPADAO(em, Parcel.class);
		
		Parcel p =j.find(parcelCode);
		LocalDate arrivalDate = p.getArrivalDate();
		
		
		if(p != null && p.getArrivalDate().isBefore(LocalDate.now().minusDays(MaxDaysInLocker))) {
			
			EntityTransaction et = em.getTransaction();
			if(p != null) {
				
				try {
					et.begin();
					j.delete(p);
					et.commit();
				}catch(Exception e) {
					if(et.isActive()) {
						et.rollback();
				    }
					
					
				}finally{
						em.close();
				}
					
				
				
			}else {
				throw new ParcelServiceException("Atencion, el parcel no se ha eliminado");
				
			}
			
			
			
				
				
			
			
				
		}
		
	}

}
