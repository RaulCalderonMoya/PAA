package paa.locker.presentacion;

import java.awt.GridLayout;


import javax.swing.JDialog;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class AboutDialog extends JDialog{

	
	private static final long serialVersionUID = 1L;
	JLabel nameProgram;
	JLabel nameAutor;
	JLabel version;
	JTextField texto1;
	JTextField texto2;
	JTextField texto3;
	
	public AboutDialog(FramePrincipal frame,String title) {
		super(frame,"About",true);
		
		nameProgram = new JLabel("Nombre del programa: ");
		nameAutor = new JLabel("Autor del programa: ");
		version = new JLabel("Version del programa: ");
		
		texto1 = new JTextField ("BOX-WORLDWIDE");
		texto2 = new JTextField ("Raúl Calderón Moya");
		texto3 = new JTextField ("11");
		
		setLayout (new GridLayout(3,2));
		add(nameProgram);
		
		add(texto1);
		add(nameAutor);
		
		add(texto2);
		add(version);
		add(texto3);
		
		
		texto1.setEditable(false);
		texto2.setEditable(false);
		texto3.setEditable(false);
		
		
		
		
	}
	
}
