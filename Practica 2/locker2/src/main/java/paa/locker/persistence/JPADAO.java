package paa.locker.persistence;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityExistsException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class JPADAO<T, K> implements DAO<T, K> {
	protected EntityManager em;
	protected Class<T> clazz;

	public JPADAO(EntityManager em, Class<T> entityClass) {
		this.clazz = entityClass;
		this.em = em;
	}

	@Override
	public T find(K id) {
		//Buscamos por ID el elemento en la Base de Datos
		return em.find(clazz, id); 
	}

	@Override
	public T create(T t) {
		try {
			
			em.persist(t);// hace una entidad persistente y gestionada.
			//Cuando yo almaceno algo, se persisten o guardan esos datos y no se pierden
			//Cuando se ejecuta se crea un nuevo registro en la tabla
			em.flush();
			//Sincroniza el contexto de persistencia con la base de datos
			em.refresh(t);
			// refresca el estado de la entidad con los valores
			//de la base de datos, sobreescribiendo los cambios que se hayan podido
			
			//realizar en ella.
			return t;
			
		} catch (EntityExistsException ex) {//
			
			throw new DAOException("¡Atención, ya existe!", ex);
		}
	}

	@Override
	public T update(T t) {
		return (T) em.merge(t); // incorpora una entidad al contexto de persistencia,
			
		                        // haciendola gestionada. Devuelve una nueva referencia de la entidad gestionada.
		
	}

	@Override
	public void delete(T t) {
		
		//El registro ya existe, se tiene que actualizar su valor 
		//para eso se utiliza merge
		t = em.merge(t); 
				
		em.remove(t);    
		em.flush();  //Para que los cambios persistan hacemos esta sentencia
		           //Ademas daria lugar a un error en el test si no se 
		           //incluye esta sentencia de codigo       
		
	}

	
	@Override
	public List<T> findAll() {
		
		List<T> listado = null;
		//Objeto Query, que sirve para hacer la llamada a la base de datos
		
		Query q = em.createQuery("select t from " + clazz.getName() + " t"); 
		
		listado = q.getResultList(); 
		//Devuelve una lista que se llama listado de objetos de tipo T
		//; es decir, devolverá un listado de lockers
		
		return listado; 

	}
}
