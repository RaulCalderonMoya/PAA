package paa.locker.persistence;


import java.util.List;

import javax.persistence.*;


import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

public class JPADAO<T, K> implements DAO<T, K> {
    protected EntityManager em;
    protected Class<T> clazz;

    
    
    public JPADAO(EntityManager em, Class<T> entityClass) {
        this.clazz = entityClass;
        this.em = em;
    }

    @Override
    public T find(K id) {
    	
    	return em.find(clazz, id);//Buscamos por ID el elemento en la BBDD
        
    }
    
    

    @Override
    public T create(T t) {// hace una entidad persistente y gestionada.
    	                  // Sincroniza el contexto de persistencia con la BBDD
    	                  // refresca el estado de la entidad con los valores
    	                  // de la base de datos, sobreescribiendo los cambios que se hayan podido
		                 // realizar en ella.
        try { 
        	em.persist(t); 
        	em.flush();  
        	em.refresh(t);
        	return t;
     
        }catch(EntityExistsException ex){
        	throw new DAOException("La entidad ya existe", ex);
        	
        	
        }
    }

    @Override
    public T update(T t) {// incorpora una entidad al contexto de persistencia,
		// haciÃ©ndola gestionada. Devuelve una nueva referencia de la entidad gestionada.
    
    	return (T) em.merge(t);
    }

    @Override
    public void delete(T t) {
        t = em.merge(t);
        
        em.remove(t);
        
        em.flush();
    }

    
	@Override
    public List<T> findAll() {
		// Complete este mÃ©todo, que debe listar todas las reservas de un dÃ­a dado.
				// NecesitarÃ¡ hacer consultas a la base datos mediante una TypedQuery, bien
				// empleando una sentencia JPQL o una CriteriaQuery 
				// Por ej.: "select t from " + clazz.getName() + " t"
    	List<T> lista;
    	Query q =  em.createQuery("select t  from "+ clazz.getName() + " t"); 
    	lista = q.getResultList();
    
    	
    	
    	return lista;
    	
    }
}
