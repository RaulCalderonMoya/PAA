package paa.locker.persistence;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import javax.persistence.*;

//Nota: Serializable es la informacion que sirve para pasar informacion de la base de datos a los 
//objetos de las entidades que estamos creando 



@Entity
//@Table(name="TABLA DE LOCKER") // Si en vez de crear una tabla con el nombre de la clase 
                            //Se creara con el nombre que nos indica este comando @Table

                            //No sería util utilizar Table aqui ya que por defecto el mapeado  
                            // se realiza con el mismo nombre
public class Locker implements Serializable{
	
	
	@Id //Nos sirve para indicar la clave primaria 
	@GeneratedValue
    private Long   code;
	
	@Column(name="nombre")
    private String name;
    private String addresse;
    private double longitude, latitude;
    private int largeCompartments, smallCompartments;
    @OneToMany(mappedBy="locker")//locker pertenece a la clase parcel el valor es locker al igual que parcel
    //nombre del atributo locker de la clase parcel. Sirve para relacionar las bases de Datos
    private List<Parcel> parcels;

    public Locker() {}
  //Nota : Un locker tiene varias parcel (1:N) De tal manera que un locker esta asociado 
    //a varios de la tabla parcels, qeu es una lista 
    
   
    public Locker(Long code, String name, String addresse, double longitude, double latitude, int largeCompartments, int smallCompartments) {
        this.code = code;
        this.name = name;
        this.addresse = addresse;
        this.longitude = longitude;
        this.latitude = latitude;
        this.largeCompartments = largeCompartments;
        this.smallCompartments = smallCompartments;
        this.parcels = new ArrayList<>();
    }



    public Long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddresse() {
        return addresse;
    }

    public void setAddresse(String addresse) {
        this.addresse = addresse;
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

    public List<Parcel> getParcels() {
        return parcels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Locker)) return false;

        Locker locker = (Locker) o;

        return Objects.equals(code, locker.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }
}
