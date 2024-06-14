package paa.locker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import paa.locker.persistence.DAOException;
import paa.locker.persistence.Locker;
import paa.locker.persistence.LockerMapDAO;

public class MapDAOTest {
	//Esta clase me sirve para probar cada uno de los metodos 
	//de la clase LockerMapDAO
	
	//Vamos a crear una serie de objetos, que vamos a usar para hacer las pruebas 
	

	@Test
	public void TestCreateOK() {
		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		Locker obj2 = new Locker(2L, "OBJETO2", "DIRECCION2", 200, 400, 30 ,53);
		Locker obj3 = new Locker(3L, "OBJETO3", "DIRECCION3", 400, 698, 460 ,59);
		LockerMapDAO mapa = new LockerMapDAO();
		
		mapa.create(obj1);
		mapa.create(obj2);
		mapa.create(obj3);
	    
	}
	@Test(expected = DAOException.class)
	public void TestCreateNOK() {
		LockerMapDAO mapa = new LockerMapDAO();
		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		//La excepcion va a saltar cuando tengamos dos objetos creados que sean iguales 
		//Esto ocurre al definir el metodo equals mediante el cual
		//si hay elementos repetidos salta la excepcion
		mapa.create(obj1);
		mapa.create(obj1);
				
	}
	
	@Test
	public void TestDeleteOK() {
		
		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		Locker obj2 = new Locker(2L, "OBJETO2", "DIRECCION2", 200, 400, 30 ,53);
		Locker obj3 = new Locker(3L, "OBJETO3", "DIRECCION3", 400, 698, 460 ,59);
		LockerMapDAO mapa = new LockerMapDAO();
		//Para comprobar que elimina correctamente lo que debemos hacer es 
		//ver si el numero de elementos es el mismo en tamaño o numero
		mapa.create(obj1);
		mapa.create(obj2);
		mapa.create(obj3);
		
		assertEquals(mapa.findAll().size(),3);
		
		//Ahora veamos si se ha borrado o no.
		mapa.delete(obj1);
		assertEquals(mapa.findAll().size(),2);

		
	}
	
	@Test(expected = DAOException.class)
	public void TestDeleteNOK() {
		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		Locker obj2 = new Locker(2L, "OBJETO2", "DIRECCION2", 200, 400, 30 ,53);
		Locker obj3 = new Locker(3L, "OBJETO3", "DIRECCION3", 400, 698, 460 ,59);
		LockerMapDAO mapa = new LockerMapDAO();
		//Para forzar que salte la excepcion
		//debemos introducir un elemento que no existe para borrar
		mapa.create(obj1);
		mapa.create(obj2);
		mapa.create(obj3);
		
		//Para hacer que salte la excepcion debemos conseguir que se borre un elemento 2 veces
		//Creamos un objeto que no exista en el mapa
		mapa.delete(new Locker(4L, "OBJETO4", "DIRECCION4", 1400, 698, 460 ,59));
		
		
	}
	@Test
	public void TestfindAll() {
		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		Locker obj2 = new Locker(2L, "OBJETO2", "DIRECCION2", 200, 400, 30 ,53);
		Locker obj3 = new Locker(3L, "OBJETO3", "DIRECCION3", 400, 698, 460 ,59);
		LockerMapDAO mapa = new LockerMapDAO();
		//En este caso debemos crear los objetos y para ver si funciona el 
		//numero total de objetos en el mapa debe coincidir con los objetos creados
		mapa.create(obj1);
		mapa.create(obj2);
		mapa.create(obj3);
		
		assertEquals(mapa.findAll().size(),3);

		
	}
	
	//No seria necesario codificar TestfindAllNok ya que no saltan excepciones en este metodo 
	//Pasamos directamente a los test de update
	@Test
	public void TestFindOK() {
		
		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		Locker obj2 = new Locker(2L, "OBJETO2", "DIRECCION2", 200, 400, 30 ,53);
		Locker obj3 = new Locker(3L, "OBJETO3", "DIRECCION3", 400, 698, 460 ,59);
		LockerMapDAO mapa = new LockerMapDAO();
		
		mapa.create(obj2);
		//No debe dar nulo ya que si esta registrado en el mapa
		assertNotNull(mapa.find(obj2.getCode()));
		//Debe dar nulo ya que no esta en el mapa
		assertNull(mapa.find(obj1.getCode()));
		
	}
	//Al igual que con el metodo findAll como el metodo find
	//no salta una excepcion no es necesario codificar TestfindNOK
	
	@Test
	public void TestUpdateOk() {
		
		
		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		Locker obj2 = new Locker(2L, "OBJETO2", "DIRECCION2", 200, 400, 30 ,53);
		Locker obj3 = new Locker(3L, "OBJETO3", "DIRECCION3", 400, 698, 460 ,59);
		LockerMapDAO mapa = new LockerMapDAO();
		//Para ver si actualiza valor creamos objetos en el mapa primero
		mapa.create(obj1);
		mapa.create(obj2);
		mapa.create(obj3);
		
		obj1.setName("Objeto nuevo");
		assertEquals(mapa.update(obj1), obj1);
		
		assertEquals(mapa.update(obj1).getName(), "Objeto nuevo");
		
		
	}
	@Test(expected = DAOException.class)
	public void TestUpdateNOK() {

		Locker obj1 = new Locker(1L, "OBJETO1", "DIRECCION1", 100, 200, 30 ,51);
		Locker obj2 = new Locker(2L, "OBJETO2", "DIRECCION2", 200, 400, 30 ,53);
		Locker obj3 = new Locker(3L, "OBJETO3", "DIRECCION3", 400, 698, 460 ,59);
		LockerMapDAO mapa = new LockerMapDAO();
	   //Para forzar que salte la excepcion debemos conseguir que el mapa
		//no contenga la clave que espera 
		mapa.create(obj1);
		mapa.create(obj2);
		mapa.create(obj3);
		
		//Para ello creamos un nuevo objeto no existente en el mapa
		Locker obj6 = new Locker(6L, "OBJETO6", "DIRECCION6", 4000, 6998, 4600 ,589);
        mapa.update(obj6);
		
	}
	
	

}
