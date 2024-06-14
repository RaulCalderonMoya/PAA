package paa.locker.persistence;

public class DAOException extends RuntimeException{//En el enunciado dicen que DAOException hereda de RuntimeException
	//A la hora de crear una excepcion siempre hay dos constructores
	public DAOException() {
		super();
	}
	
	public DAOException(String mensaje) {
		super(mensaje);		
	}

}
