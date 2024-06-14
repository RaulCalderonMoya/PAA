package paa.locker.persistence;

import java.util.Objects;

public class Locker { // Actua como DTO
  private Long code; // code es unico para cada uno de los elementos 
  
  //El resto no son unicos ya que pueden estar repetidos
  //No seran tenidos en cuenta para el metodo equals
  private String name; //El nombre se puede repetir varias veces 
  private String address;
  private int largeCompartments;
  private int smallCompartments;
  private double longitude;
  private double latitude;
  
 public Locker() {
	 
	 
 }
 public Locker(Long code, String name,String address,int largeCompartments,int smallCompartments,double longitude,double latitude) {
	 this.code = code;
	 this.name= name;
	 this.address= address;
	 this.largeCompartments = largeCompartments;
	 this.smallCompartments=smallCompartments;
	 this.longitude= longitude;
	 this.latitude = latitude;
	 
 }
  
  
  
  
  
//Métodos getters/setters a completar

public Long getCode() {
	return code;
}

public void setCode(Long code) {
	this.code = code;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public int getLargeCompartments() {
	return largeCompartments;
}
public void setLargeCompartments(int largeCompartments) {
	this.largeCompartments = largeCompartments;
}
public int getSmallCompartments() {
	return smallCompartments;
}
public void setSmallCompartments(int smallCompartments) {
	this.smallCompartments = smallCompartments;
}
public double getLongitude() {
	return longitude;
}
public void setLongitude(double longitude) {
	this.longitude = longitude;
}
public double getLatitude() {
	return latitude;
}
public void setLatitude(double latitude) {
	this.latitude = latitude;
}


  
  
  
  

// Métodos equals/hashCode a completar 
//EL enunciado indica que el codigo es unico con lo cual solo se puede aplicar a code
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Locker other = (Locker) obj;
	return Objects.equals(code, other.code);
}

@Override
public int hashCode() {
	return Objects.hash(code);
}


}