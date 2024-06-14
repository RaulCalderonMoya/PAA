package paa.locker.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockerMapDAO implements DAO<Locker, Long>{
	//Definimos el mapa
	Map<Long, Locker>mapa;
	//Ahora se va a inicializar el mismo en el constructor
	
	public LockerMapDAO() {
		mapa = new HashMap<Long,Locker>();
		
	}

	@Override
	public Locker find(Long id) {
		return mapa.get(id);
	}

	@Override
	public List<Locker> findAll() {
		//Este metodo devuelve una lista con todos los objetos locker que encuentra.
		//Va a ser utilizado a la hora de realizar la lectura o read.
		List<Locker>lista = new ArrayList<Locker>();
	//Debemos recorrer el mapa, lo vamos a hacer con un for
		for(Map.Entry<Long, Locker> entry : mapa.entrySet()) {
			lista.add(entry.getValue());
			
		}
		
		return lista;
		//Devolvemos la lista con los elementos encontrados 
		
		
	}

	@Override
	public Locker create(Locker t) {
		//En este caso debemos crear elementos en el mapa, debemos recordar que en mapas 
		//para realizarlo usamos put.
		//Sin embargo antes de hacer el put debemos ver si ese elemento esta o no repetido en el mapa
		//Lo vamos a hacer con un bloque if
		if(mapa.containsKey(t.getCode())) {
			throw new DAOException("El mapa contiene la clave previamente, no se puede crear al ya existir");
		}
		//Una vez comprobado que no esta creado previamente lo que hacemos es: 
		mapa.put(t.getCode(), t);
		
		//Devolvemos el elemento creado que es el objeto t
		
		return t;
	}

	@Override
	public Locker update(Locker t) {
		
		//En el caso de update lo que debemos tener en cuenta es que exista el objeto en 
		//el mapa, ya que si no existe debe saltar la excepcion
		if(!mapa.containsKey(t.getCode())){
			throw new DAOException("La clave no existe en el mapa");
			
		}
		
		//Para actualizar el mapa el proceso es parecido a crear ya que 
		//debemos sobreescribir
		mapa.put(t.getCode(),t);
		
		
		return t;
	}

	@Override
	public void delete(Locker t) {
         //Para hacer delete o eliminar lo que debemos hacer primero es verificar que el elemento esta 
		 //en el mapa y una vez verificado procedemos a eliminar el objeto en cuestion
		//
		
		if(!mapa.containsKey(t.getCode())) {
			throw new DAOException("El elemento no existe en el mapa y no se puede borrar");
			//En el caso que no exista salta la excepcion 
		}
		
		//Si no salta la excepcion debe borrar el elemento
		//mapa.remove(t.getCode(), t); // Devolvía un boolean no seria lo ideal su utilizacion
       mapa.remove(t.getCode());
	}
	
	
	
	

}
