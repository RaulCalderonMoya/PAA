package paa.locker.persistence;

import java.util.List;

public interface DAO <T,K>{
	T find (K id); 
	List<T> findAll ();
	T create (T t);
	T update(T t);
	void delete(T t);
	
}
