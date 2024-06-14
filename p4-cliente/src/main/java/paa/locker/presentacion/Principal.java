package paa.locker.presentacion;

import javax.swing.JDialog;

import javax.swing.JFrame;

import javax.swing.SwingUtilities;

public class Principal {

	
	public static void main(String[] args) {
		
	
		
		
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
			
			
			
				FramePrincipal prueba =  new FramePrincipal("Locker Manager");
				
			//	JFrame prueba3 =  new RetrieveParcelDialog("Retrieve Parcel");
			//	JFrame prueba4 =  new AboutDialog("About");
			//	JFrame prueba5 =  new DeliverParcelDialog("Deliver Parcel");
				
			    prueba.setSize(500,500);
				prueba.setVisible(true);
				prueba.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			/*	
				prueba2.setVisible(true);
				prueba2.setResizable(false); 
				prueba2.setBounds(400,200, 500, 200);
				prueba2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				prueba3.setVisible(true);
				prueba3.setResizable(false); 
				prueba3.setBounds(300,200, 700, 200);
				prueba3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				prueba4.setVisible(true);
				prueba4.setResizable(false); 
				prueba4.setBounds(400,200, 500, 200);
				prueba4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				prueba5.setVisible(true);
				prueba5.setResizable(false); 
				prueba5.setBounds(400,200, 500, 200);
				prueba5.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				*/
			}
			
			});

		
	
	}

}
