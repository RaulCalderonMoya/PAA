package paa.locker.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import paa.locker.business.JPAParcelService;
import paa.locker.business.ParcelService;
import paa.locker.business.ParcelServiceException;
import paa.locker.model.Locker; //ASI SE VAN MUCHOS ERRORES
import paa.locker.model.Parcel; // IGUAL CON ESTA SENTENCIA
import paa.locker.persistence.*;
import paa.locker.persistence.*;

/**
 * Servlet implementation class LockerServer
 */
@WebServlet("/LockerServer")
public class LockerServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    private ParcelService ps;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LockerServer() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
    public void init(ServletConfig config) throws ServletException {
   	 super.init(config);
   	 
   	 //Le pasamos la bbdd//
   	 final String absoluteDiskPath = getServletContext().getRealPath("./WEB-INF/bdatos");
   	 
   	 try{
   		 ps = new JPAParcelService(absoluteDiskPath);
   		 
   	 } catch(Exception e) {
   		 
   		 System.out.println("Error al instanciar");
   		 e.printStackTrace();
   		 
   		 throw new ServletException(e);
   	 }
   }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Recogemos los parametros de la URL
		String accion = request.getParameter("accion");
		
		Long lockerCode = null;
		
		if (accion == null) {
			accion = "findAllLockers"; 
		}	
		
		
		response.setContentType("text/json");
		
	//	response.setContentType("application/json"); //ESTA SENTENCIA PUEDE DAR PROBLEMAS 
		response.setCharacterEncoding("UTF-8");
		

		PrintWriter out = response.getWriter();
	
			switch (accion) {
			
			case "createLocker":
				/* Está acción permitirá invocar el método correspondiente de JPAParcelService.
				 * Cuando se usa esta acción es obligatorio incluir los siguientes 6 parámetros 
				 * (en el orden que se desee):
				 * name, address, longitude, latitude, largeCompartments y smallCompartments.
				 * Ejemplo:
				 * http://localhost:8080/NOMBRE/LockerServer?accion=createLocker&name=test1&address=calle1&longitude=-3.62&latitude=40.39&largeCompartments=10&smallCompartments=10
				 * */
				
				String name = request.getParameter("name");
				String address = request.getParameter("address");
				double longitude = Double.parseDouble(request.getParameter("longitude"));
				double latitude = Double.parseDouble(request.getParameter("latitude"));
				
				
				int largeCompartments = Integer.parseInt(request.getParameter("largeCompartments"));
				int smallCompartments = Integer.parseInt(request.getParameter("smallCompartments"));

				createLocker(response, out, name,  address,  longitude,  latitude,  largeCompartments,  smallCompartments);

				break; 
		
		case "findLocker":
			/*Está acción permitirá invocar el método correspondiente de JPAParcelService. Cuando
			 * se usa esta acción es obligatorio incluir el siguiente parámetro: lockerCode.
			 * Ejemplo:
			 * http://localhost:8080/Nombre/LockerServer?accion=findLocker&lockerCode=153
			 */
			lockerCode = Long.parseLong(request.getParameter("lockerCode"));
			findLocker ( out, lockerCode);

			break;
			
		case "findAllLockers":
				/* Está acción permitirá invocar el método correspondiente de JPAParcelService. Esta
				 * acción no necesita de más parámetros en la petición.
				 * Ejemplo:
				 * http://localhost:8080/Nombre/LockerServer?accion=findAllLockers
				 */
				findAllLockers (response, out); 

			break;
			case "availableCompartments":
				/* Está acción permitirá invocar el método correspondiente de JPAParcelService.
				 * Cuando se usa esta acción es obligatorio incluir los siguientes 3 parámetros 
				 * (en el orden que se desee):
				 * 		lockerCode, date y parcelWeight.
				 * 
				 * Ejemplo:
				 * http://localhost:8080/NOmbre/LockerServer?accion=availableCompartments&lockerCode=32&date=2022-04-01&parcelWeight=1.1
				 * 
				 * NOTA: Se recomienda que los valores de fecha del parámetro date estén en formato ISO
				 * Local Date como en el ejemplo
				 * */
				LocalDate date = LocalDate.parse(request.getParameter("date"));
				float parcelWeight = Float.parseFloat(request.getParameter("parcelWeight"));
				lockerCode = Long.parseLong(request.getParameter("lockerCode"));

				availableCompartments(response,  out,  lockerCode,  date,  parcelWeight);

				break;
			
			case "deliverParcel":
				/*
				 * Está acción permitirá invocar el método correspondiente de JPAParcelService. Cuando
				 * se usa esta acción es obligatorio incluir los siguientes 4 parámetros (en el orden que se desee):
				 * lockerCode, addressee, weight y arrivalDate.
				 * 
				 * Ejemplo:
				 * http://localhost:8080/Nombre/LockerServer?accion=deliverParcel&lockerCode=151&addressee=1234&weight=1.32&arrivalDate=2022-04-01
				 *
				 * NOTA: Se recomienda que los valores de fecha del parámetro arrivalDate estén en
				 * formato ISO Local Date como en el ejemplo.
				 */
				int addressee = Integer.parseInt(request.getParameter("addressee"));
				LocalDate arrivalDate = LocalDate.parse(request.getParameter("arrivalDate"));
				
				Float weight = Float.parseFloat(request.getParameter("weight"));
				lockerCode = Long.parseLong(request.getParameter("lockerCode"));

				deliverParcel (response, out, lockerCode, addressee, weight,  arrivalDate);

				break;
			
			case "retrieveParcel":
				/*	Está acción permitirá invocar el método correspondiente de JPAParcelService. Cuando
				 * se usa esta acción es obligatorio incluir el siguiente parámetro: parcelCode.
				 * Ejemplo:
				 * http://localhost:8080/Nombre/LockerServer?accion=retrieveParcel&parcelCode=345
				 */
				Long parcelCode = Long.parseLong(request.getParameter("parcelCode"));
				retrieveParcel (response, out, parcelCode);
				break;
		}				

			
		
	}


	private void createLocker( HttpServletResponse response, PrintWriter out, String name, String address, double longitude, double latitude, int largeCompartments, int smallCompartments) throws JsonProcessingException, IOException {
		 try {
		 	Locker newLocker = ps.createLocker(name, address, longitude, latitude, largeCompartments,smallCompartments);

		 	ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
			objectMapper.findAndRegisterModules();
			
			
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newLocker);
			//Object jsonObject = objectMapper.readValue(json, Object.class);
			
			//Mostrar en el navegador
			//String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			
			
			response.setStatus(200);
			
			response.getWriter().println(json);
			
			
		}catch(ParcelServiceException e) {
			response.setStatus(400);
			
			response.addHeader("error", e.getMessage());
			response.setStatus(400);
			//response.sendError(400,  e.getMessage());
						
		} catch (IOException e) {
			response.setStatus(500);
			
			response.addHeader("error", "Atencion!!! error inesperado");
			
			response.setStatus(400);
			//response.sendError(500,  e.getMessage());
		}finally {
			
			out.close();

		}
	 }
	 
	 private void findLocker(PrintWriter out,Long lockerCode) throws StreamWriteException, DatabindException, IOException {
					
		 	Locker locker = ps.findLocker(lockerCode);
			
		 	ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
			objectMapper.findAndRegisterModules();
			
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(locker);
			
			//Object jsonObject = objectMapper.readValue(json, Object.class);
			
			
			//Mostrar en el navegador
			//String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			out.println(json);
			
			out.close();		
	
	 }
	 
	 @SuppressWarnings("unused") //Con esto se  quita el warning 
	 
	private void findAllLockers(HttpServletResponse response,PrintWriter out) throws JsonProcessingException {
			List<Locker> lockerList = ps.findAllLockers();

			ObjectMapper objectMapper = new ObjectMapper();
			
			
			objectMapper.findAndRegisterModules();
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(lockerList);
			//Object jsonObject = objectMapper.readValue(json, Object.class);
			
			
			//Mostrar en el navegador los resultados para que aparezcan y sean visibles
			//String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			out.println(json);
			
			out.close();
	 }
	 
	private void availableCompartments(HttpServletResponse response, PrintWriter out, Long lockerCode, LocalDate date, float parcelWeight) throws JsonProcessingException {
		try {
			int disponibles = ps.availableCompartments(lockerCode, date, parcelWeight);
			ObjectMapper objectMapper = new ObjectMapper();
			
			
			objectMapper.findAndRegisterModules();
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(disponibles);
			//Object jsonObject = objectMapper.readValue(json, Object.class);
			
			
			
			//Mostrar en el navegador
			//String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			out.println(json);
			
			out.close();
			
		}catch(ParcelServiceException e) {
			
			response.setStatus(400);
			response.addHeader("error", e.getMessage());
			
			out.println(e.getMessage());
			
		}finally {
			
			out.close();
		}
		
	 }
	 
	 private void deliverParcel(HttpServletResponse response, PrintWriter out, Long lockerCode, int addressee, float weight, LocalDate arrivalDate) throws JsonProcessingException {
		 try {
			Parcel parcel = ps.deliverParcel(lockerCode, addressee, weight, arrivalDate);
			 
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.findAndRegisterModules();
			
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parcel);
			//Object jsonObject = objectMapper.readValue(json, Parcel.class);
				
			//Mostrar en el navegador los resultados correspondientes 
			//String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			out.println(json);
			
			out.close();
			 
		 }catch(ParcelServiceException e) {
			 
			 
			response.setStatus(400);
			response.addHeader("error", e.getMessage());
			
			out.println(e.getMessage());

		 }finally {
			 
			 out.close();
		 }
		
	 }
	 
	 private void retrieveParcel(HttpServletResponse response, PrintWriter out, Long parcelCode) {
		 try {
			 
			 ps.retrieveParcel(parcelCode); 
			 response.setStatus(200);
			 
		 }catch(ParcelServiceException e) {
			 
			response.setStatus(400);
			response.addHeader("error", e.getMessage());
			
			out.println(e.getMessage());
	
		 }finally {
			 
			 out.close();
		 }
	 }

}

