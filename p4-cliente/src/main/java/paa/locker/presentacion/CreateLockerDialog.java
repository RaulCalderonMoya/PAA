package paa.locker.presentacion;

import java.awt.BorderLayout;


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JDialog;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import paa.locker.business.ParcelService;
import paa.locker.business.ParcelServiceException;
import paa.locker.model.Locker;

public class CreateLockerDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	JButton botonOK;
	JButton botonCancel;
	JPanel panelBotones;
	JPanel panelInformacion;
	JLabel linea1;
	JLabel linea2;
	JLabel linea3;
	JLabel linea4;
	JLabel linea5;
	JLabel linea6;
	JTextField barra1;
	JTextField barra2;
	JTextField barra3;
	JTextField barra4;
	JTextField barra5;
	JTextField barra6;
	public CreateLockerDialog (FramePrincipal frame,String title,ParcelService service) {

		super(frame,title,true);
		botonOK = new JButton("OK");
		botonCancel = new JButton("Cancel");
		panelBotones = new JPanel();
		panelInformacion = new JPanel();
		panelBotones = new JPanel();
		panelBotones.add(botonOK);
		panelBotones.add(botonCancel);

		linea1 = new JLabel("Name: ");
		barra1 = new JTextField();
		linea2 = new JLabel("Address: ");
		barra2 = new JTextField();
		linea3 = new JLabel("Large Compartments: ");
		barra3 = new JTextField();
		linea4 = new JLabel("Small Compartments: ");
		barra4 = new JTextField();
		linea5 = new JLabel("Longitude: ");
		barra5 = new JTextField();
		linea6 = new JLabel("Latitude: ");
		barra6 = new JTextField();

		panelInformacion.setLayout(new GridLayout(6,2)); //Grid Layout ordena pordefecto por filas

		panelInformacion.add(linea1);
		panelInformacion.add(barra1);
		panelInformacion.add(linea2);
		panelInformacion.add(barra2);
		panelInformacion.add(linea3);
		panelInformacion.add(barra3);
		panelInformacion.add(linea4);
		panelInformacion.add(barra4);
		panelInformacion.add(linea5);
		panelInformacion.add(barra5);
		panelInformacion.add(linea6);
		panelInformacion.add(barra6);


		setLayout(new BorderLayout());
		add(panelBotones,BorderLayout.SOUTH);
		add(panelInformacion,BorderLayout.CENTER);


		botonCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();

			}

		});

		botonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(barra1.getText().isEmpty() || barra2.getText().isEmpty() || barra3.getText().isEmpty()
						|| barra3.getText().isEmpty()  || barra4.getText().isEmpty()|| barra5.getText().isEmpty()
						|| barra6.getText().isEmpty()) {

					JOptionPane.showMessageDialog(null, "Tiene que rellenar todos los campos","ERROR", 2);

				}else {

					try {
						Locker newLocker = service.createLocker(barra1.getText().trim(), barra2.getText().trim(), 
								Double.parseDouble(barra5.getText().trim()), Double.parseDouble(barra6.getText().trim()),
								Integer.parseInt(barra3.getText().trim()) ,Integer.parseInt(barra4.getText()) );

						if(null!= newLocker) {

							frame.updateCombo(newLocker,-1);
							JOptionPane.showMessageDialog(null,"El locker se ha añadido de manera correcta ");
							dispose(); 
						} else {
							JOptionPane.showMessageDialog(null, "No se ha podido añadir el Locker.",
									"ERROR", 2);
						}



					}catch(ParcelServiceException pe) {
						JOptionPane.showMessageDialog(null, pe.getMessage() ,
								"WARNING_MESSAGE", JOptionPane.WARNING_MESSAGE);
					}

					//Codigo capturado en una excepcion por medio de Try-Catch //Util para hacer alguna prueba
					/*	
					Locker newLocker = service.createLocker(barra1.getText().trim(), barra2.getText().trim(), 
				    		Double.parseDouble(barra5.getText().trim()), Double.parseDouble(barra6.getText().trim()),
				    		Integer.parseInt(barra3.getText().trim()) ,Integer.parseInt(barra4.getText()) );

					if(null!= newLocker) {

						frame.updateCombo(newLocker,-1);
						JOptionPane.showMessageDialog(null,"El locker se ha añadido de manera correcta ");
						dispose(); 
					} else {
						JOptionPane.showMessageDialog(null, "No se ha podido añadir el Locker.",
								  "ERROR", 2);
					}


					 */

				}	

			}

		});




	}
	public void resetTextFields() {
		barra1.setText(null);
		barra2.setText(null);
		barra3.setText(null);
		barra4.setText(null);
		barra5.setText(null);
		barra6.setText(null);
	}

}
