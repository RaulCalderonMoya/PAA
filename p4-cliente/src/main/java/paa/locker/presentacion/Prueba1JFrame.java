package paa.locker.presentacion;




import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;


public class Prueba1JFrame extends JFrame {
	
	Icon icono = new ImageIcon (getClass().getResource("/locker.png"));
	JButton boton = new JButton("Añadir", icono);
	
	public Prueba1JFrame () { //creamos constructor
		//1º opcion
		
		add(boton);
		setSize(500,300);//ancho;alto
		setTitle("Prueba1JFrame");
		
		//2º opcion
		
		/*setSize(500,300);
		setTitle("Prueba1JFrame");
		JButton botonTexto = new JButton("añadir"); //puede ir como atributo
	    JButton botonImagen = new JButton(icono); //puede ir como atributo
			
		this.setLayout(new BorderLayout());
		add(botonTexto,BorderLayout.WEST);
		add(botonImagen,BorderLayout.EAST);
		*/
		
		
		
		//VARIABLES QUE PUEDEN SERVIR
		/*
		 
		Toolkit mipantalla = Toolkit.getDefaultToolkit();
		
		Dimension tamanoPantalla = mipantalla.getScreenSize(); //resolucion de mi pantalla
		
	
		int alturaPantalla = tamanoPantalla.height; //alto
		int anchoPantalla = tamanoPantalla.width; //ancho
		setSize(anchoPantalla/2,alturaPantalla/2);
		setLocation(anchoPantalla/4,alturaPantalla/4); // para colocarlo en el centro el frame
		
		
		//setLocation(400,200); //cambiar la localizacion del frame (x,y) coordenadas
		
		//setBounds(500,300,400,200); // indicamos la localizzacion y el tamaño a lavez
		
		
		
		//setResizable(false); // false --> indica que no se puede redimensionar  y no sale la opcion de maximizar.
		                     // true --> esta por defecto.
		
		//setExtendedState(Frame.MAXIMIZED_BOTH); //maximiza tanto horizontalmente como verticalmente
		                
		// maximized_both es static final int (como static hay que llamar a la clase padre) y su valor es 6
		
		*/
		
		
		                                                 
		
		
	}
	
	
	
}
