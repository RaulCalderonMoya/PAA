package paa.locker.presentacion;

import javax.swing.BorderFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import paa.locker.business.RemoteParcelService;
import paa.locker.business.ParcelService;
import paa.locker.util.LockerMap;


public class Prueba2JFrame extends JFrame {
	
	
	private LockerMap map;
	
	JButton boton = new JButton();
	public Prueba2JFrame () { //creamos constructor
		//1º opcion
		 
		add(boton);
		setSize(500,300);//ancho;alto
		setTitle("Prueba2JFrame");
		
		JMenuBar barraMenu = new JMenuBar(); //crea la barra del menu
	
		JMenu menu = new JMenu("File"); //opciones que hay en dicho menu
		JMenu menu2 = new JMenu("Help");
    
		barraMenu.add(menu); //añadimos al menu las opciones
		barraMenu.add(menu2);
		setJMenuBar(barraMenu);
		JOptionPane mensaje = new JOptionPane();
		JPanel panel = new JPanel ();
		
		JOptionPane.showMessageDialog(null, "Creado por Bruno Raymundo", "LockerMap",1);
		
		map= new LockerMap(1265,620,null);
		TitledBorder  mapTitulo = BorderFactory.createTitledBorder("Locker Map"); // instanciamos el borde del titulo y nombre
		add(panel); //añadimos el panel al frame
		panel.add(map); //añadimos el mapa al panel
		map.setBorder(mapTitulo); //ponemos el titulo al borde del mapa
		
	
	}
}