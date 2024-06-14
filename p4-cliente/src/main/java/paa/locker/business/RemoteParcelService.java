package paa.locker.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import paa.locker.model.Locker;
import paa.locker.model.Parcel;

public class RemoteParcelService implements ParcelService{

	private String url; 
	private int statusCode;
	
	public RemoteParcelService () {
		url = "http://localhost:8080/Practica4-Server2/LockerServer";
	}
	
	//Raul practica 4- Parte RemoteParcelService repasar
	
	
	@Override
	public Locker createLocker(String name, String address, double longitude, double latitude, int largeCompartments,
			int smallCompartments) {
		
		Locker l = null;//Devuelve null si no encuentra el elemento en la BBDD
		//Devuelve el objeto 'Locker' si lo encuentra
		
		//cuidado la url no acepta espacios en blanco
		//https://www.w3bai.com/es/tags/ref_urlencode.html#:~:text=URL%20no%20pueden%20contener%20espacios,(%2B)%20signo%20o%20con%2020%25.
		String urlString = url + "?accion=createLocker&name="+name+"&address="+address+"&longitude="+longitude+"&latitude="+latitude+"&largeCompartments="+largeCompartments+"&smallCompartments="+smallCompartments;
		urlString = urlString.replaceAll(" ", "%20");
		
		String json = enviarPeticion (urlString);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.findAndRegisterModules();
		try {
			if (statusCode==200) {
		
				l = objectMapper.readValue ( json,Locker.class);
			
			System.out.println("Code:"+l.getCode()+"nombre:"+l);
		
		}else if (statusCode == 400){
			//si el error es 400
			throw new ParcelServiceException(json);
			
		}else {
			//errores varios
			throw new ParcelServiceException(" Error inesperado");
		}
		
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}	
		return l;
	}

	/**
	 * Busca el armario con el código indicado y lo devuelve. 
	 * En caso de que no exista devuelve null.
	 * @param lockerCode codigo del armario a buscar
	 * @return el armario si se encuentra o null en caso contrario
	 * */
	@Override
	public Locker findLocker(Long lockerCode) {
		
		Locker l = null;//Devuelve null si no encuentra el elemento en la BBDD
						//Devuelve el objeto 'Locker' si lo encuentra
		String urlString = url + "?accion=findLocker&lockerCode="+lockerCode;
		String json = enviarPeticion (urlString);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.findAndRegisterModules();
			l = objectMapper.readValue ( json,Locker.class);
		
			System.out.println(l);//borrar despues de las pruebas
						
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}	
		return l;
	}

	@Override
	/**
	 * Busca en la bbdd todos los lockers almacenados. 
	 * @return Devuelve una lista de todos los armarios existentes o null si no hay lockers
	 * */
	public List<Locker> findAllLockers() {
		List<Locker> lista =new ArrayList<Locker>();
		String urlString = url + "?accion=findAllLockers";
		String json = enviarPeticion (urlString);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.findAndRegisterModules();
			lista = objectMapper.readValue ( json, new TypeReference<List<Locker> >() {});
		
			//Borrar el for, esto solo para pruebas
			for (Locker l : lista) {
				System.out.println(l);
			}
			
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	/**
	 * 
	 * Devuelve el número de compartimentos libres en el armario y fecha indicados donde se podría alojar 
	 * un paquete del peso indicado, de acuerdo con las siguientes reglas de negocio:
	 * 		1. Un paquete grande (peso > smallMaxWeight) solo se puede alojar en un compartimento grande, 
	 * 		y uno pequeño solo en un compartimento pequeño.
	 * 		2. El peso del paquete debe ser positivo y no superior a largeMaxWeight; de lo contrario se 
	 * 		lanzará una excepción de tipo ParcelServiceException con un mensaje adecuado.
	 * 		3. Un paquete puede ocupar un compartimento del armario como máximo maxDaysInLocker días,
	 * 		 contando el de la entrega. Si pasado este tiempo no ha sido retirado, caducará y su compartimento
	 * 		 podrá ser reutilizado por otro envío.
	 * 		4. El armario debe existir; de lo contrario se lanzará una
	 * 		excepción de tipo ParcelServiceException con un mensaje adecuado
	 * 
	 * @param lockerCode codigo del armario 
	 * @param date fecha a consultar la disponibilidad
	 * @param parcelWeight peso del paquete
	 * @return un entero con el numero de cajones disponibles.
	 * @throws ParcelServiceException cuando: 
	 * 	- el armario no existe
	 * */

		@Override
		public int availableCompartments(Long lockerCode, LocalDate date, float parcelWeight) {
			
			int disponibles=0;
			String urlString = url + "?accion=availableCompartments&lockerCode="+lockerCode+"&date="+date+"&parcelWeight="+parcelWeight;
			
			String json = enviarPeticion (urlString);
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.findAndRegisterModules();
			try {
				if (statusCode==200) {
			
					 disponibles= objectMapper.readValue ( json,Integer.class);
				
				System.out.println("Disponibles: "+disponibles);
			
			}else if (statusCode == 400){
				//si el error es 400
				throw new ParcelServiceException(json);
				
			}else {
				//errores varios
				throw new ParcelServiceException("Oooooops! Error inesperado");
			}
			
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}	
			
			
			return disponibles;
		}	
	
	/**
	 * Crea una nueva entrega de un paquete con el destinatario, peso y fecha 
	 * de entrega indicados, siempre y cuando se cumplan las siguientes 
	 * reglas de negocio:
	 * 		5. Debe haber un compartimento disponible en el armario de destino,
	 * 		 siguiendo las reglas del método anterior.
	 * 		6. Un mismo destinatario no puede tener más de 
	 * 		maxParcelsInLocker compartimentos ocupados simultáneamente en ningún 
	 * 		armario. 
	 * 		7. Un mismo destinatario no puede tener más de maxParcelsAnywhere 
	 * 		compartimentos ocupados simultáneamente en total entre todos los 
	 * 		armarios.
	 * En caso de infracción de cualquiera de estas reglas de negocio se lanzará
	 * una excepción de tipo ParcelServiceException con un mensaje explicativo
	 * y no se realizará la reserva. 
	 * 
	 * @param lockerCode
	 * @param addressee
	 * @param weight
	 * @param arrivalDate
	 * 
	 * @return 
	 * 
	 * @throws ParcelServiceException cuando:
	 * 		...
	 * */
	//TODO: CONTROLAR EXCEPCIONES
	@Override
	public Parcel deliverParcel(Long lockerCode, int addressee, float weight, LocalDate arrivalDate) {
			
		Parcel p = null;//Devuelve null si no encuentra el elemento en la BBDD
		
		//cuidado la url no acepta espacios en blanco
		//https://www.w3bai.com/es/tags/ref_urlencode.html#:~:text=URL%20no%20pueden%20contener%20espacios,(%2B)%20signo%20o%20con%2020%25.
		String urlString = url + "?accion=deliverParcel&lockerCode="+lockerCode+"&addressee="+addressee+"&weight="+weight+"&arrivalDate="+arrivalDate;
		urlString = urlString.replaceAll(" ", "%20");
		
		String json = enviarPeticion (urlString);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.findAndRegisterModules();
		try {
			if (statusCode==200) {
				p = objectMapper.readValue ( json,Parcel.class);		
			}else if (statusCode == 400){
				//si el error es 400
				throw new ParcelServiceException(json);	
			}else {
				//errores varios
				throw new ParcelServiceException(" Error inesperado");
			}
		
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new ParcelServiceException(" Error inesperado");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ParcelServiceException(" Error inesperado");
		}	
		return p;
	}

	/**
	 * Esta operación modela la recogida por parte del destinatario de un
	 * paquete, eliminándolo del armario y liberando sitio. Solo es posible
	 * recoger paquetes que estén en el armario en el día en curso, ni futuros ni
	 * ya caducados. Si el paquete no existe o se viola la condición anterior, no
	 * se eliminará del sistema y se lanzará una excepción de tipo
	 * ParcelServiceException con un mensaje explicativo.
	 * @param parcelCode codigo del cajon a liberar
	 * @thorws ParcelServiceException cuando : recojan el paquete en un dia que no es el actual. 
	 * */
	@Override
	public void retrieveParcel(Long parcelCode) {
	
		String urlString = url + "?accion=retrieveParcel&parcelCode="+parcelCode;
		String json = enviarPeticion (urlString);
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.findAndRegisterModules();
		if (statusCode == 400){
			//si el error es 400
			throw new ParcelServiceException(json);	
		}else if (statusCode!=200){
			//errores varios, cuidado con este tipo de errores
			throw new ParcelServiceException("Error inesperado");
		}			
	}
		
	/**
	 * Envía una petición, espera arecibir la respuesta y retorna lo  
	 * que ha recibido como respuesta
	 * @param url Contiene una url correctamente construida
	 * @return Una cadena de caracteres con la respuesta recibida
	 * @throws  
	 */

	private  String enviarPeticion (String url){
	      String entrada, respuesta ="";
	      URLConnection connection = null;
	      InputStream is = null;
	      HttpURLConnection httpConn=null;
	     
	      try {
	    	  URL urlEnvio = new URL(url);
	    	  connection = urlEnvio.openConnection();
	    	  is = connection.getInputStream();
		      httpConn = (HttpURLConnection) connection;
		      statusCode = httpConn.getResponseCode();

		       // BufferedReader in = new BufferedReader(new InputStreamReader(urlEnvio.openStream()));
			   BufferedReader in = new BufferedReader(new InputStreamReader(is));

		    	while ((entrada = in.readLine()) != null)
		          respuesta = respuesta + entrada;
		        in.close();
		 
	      }catch (IOException ioe) {
	    	  
			try{
				if (connection instanceof HttpURLConnection) {
				    httpConn = (HttpURLConnection) connection;
				    statusCode = httpConn.getResponseCode();
					respuesta = httpConn.getHeaderField("error");
				}
		      }catch(IOException e1) {
		    	  e1.printStackTrace();
		      }
			 if (statusCode != 200 ){/* or statusCode >= 200 && statusCode < 300 *///) {
			     is = httpConn.getErrorStream();
			   }  
	      }
	      return respuesta;
	
	}

}

	
	/*URLConnection connection = url.openConnection();
	InputStream is = connection.getInputStream();
	if (connection instanceof HttpURLConnection) {
	   HttpURLConnection httpConn = (HttpURLConnection) connection;
	   int statusCode = httpConn.getResponseCode();
	   if (statusCode != 200 /* or statusCode >= 200 && statusCode < 300 *///) {
/*	     is = httpConn.getErrorStream();
	   }
	}
	*/
	
	
	
	
	/*public class URLReader {
		public static void main(String[] args) throws Exception {
		URL oracleWeb = new URL("http://www.oracle.com/");
		BufferedReader in = new BufferedReader(
		new InputStreamReader( oracleWeb.openStream()) );
		String result = "";
		while ((inputLine = in.readLine()) != null)
		result += inputLine;
		in.close();
		System.out.println(result); // Información leída desde la URL
		}
		}*/


	
