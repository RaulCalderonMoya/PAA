package paa.locker.presentacion;

import java.awt.BorderLayout;






import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import paa.locker.business.RemoteParcelService;
import paa.locker.business.ParcelService;
import paa.locker.business.ParcelServiceException;
import paa.locker.model.Locker;
import paa.locker.model.Parcel;
import paa.locker.util.ExampleLockers;

public class DeliverParcelDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private JButton botonOK;
	private JButton botonCancel;
	
	private JComboBox <Locker> lockers;
	private JComboBox <LocalDate> fechas;
	
	private LocalDate clickFechas;
	private Locker clickLocker;
	
	private JTextField barra2;
	private JTextField barra3;
	
	private JLabel linea1;
	private JLabel linea2;
	private JLabel linea3;
	private JLabel linea4;
	
	
	
	
	
	
	private ParcelService service;
	private FramePrincipal framePrin;
	public DeliverParcelDialog (FramePrincipal frame,String title, List<Locker> listaLockers, Locker clickLockers, int index ) {
		super(frame,title,true);
		this.framePrin = frame; 
		  this.service = new RemoteParcelService();
		  this.clickLocker = clickLockers;
		
		JPanel panelBotones = new JPanel();
		JPanel panelInfo = new JPanel();
		botonOK = new JButton("OK");
		botonCancel = new JButton("Cancel");
		panelBotones.add(botonOK);
		panelBotones.add(botonCancel);
		add(panelBotones,BorderLayout.SOUTH);
		
		lockers = new JComboBox <Locker>(new Vector <Locker> (listaLockers));
		lockers.setSelectedItem(clickLocker);
		
		linea1 = new JLabel ("Locker: ");
		linea2 = new JLabel ("Adresse (solo numeros):  ");
		linea3 = new JLabel ("Weight (max 10kg): ");
		linea4 = new JLabel ("ArrivalDate: ");
		barra2 = new JTextField ();
		barra3 = new JTextField ();
        fechas = new JComboBox <LocalDate>();
		
		int dias=0;
		while(dias<15) {
			fechas.addItem(LocalDate.now().plusDays(dias));
			dias++;
		}
		fechas.setEditable(false);
		clickFechas = LocalDate.now();
		fechas.setSelectedItem(clickFechas);
		
		panelInfo.setLayout(new GridLayout(4,2));
		panelInfo.add(linea1);
		panelInfo.add(lockers);
		panelInfo.add(linea2);
		panelInfo.add(barra2);
		panelInfo.add(linea3);
		panelInfo.add(barra3);
		panelInfo.add(linea4);
		panelInfo.add(fechas);
		
		add(panelInfo,BorderLayout.CENTER);
		
		botonCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
			
		});
		
		lockers.addActionListener(new ActionListener () {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(lockers.getSelectedItem() !=null) {
					clickLocker = (Locker) lockers.getSelectedItem();
				}
				
			}
		});
		fechas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 if (fechas.getSelectedItem() != null) {
					clickFechas = (LocalDate) fechas.getSelectedItem();
				}
			}
		});
		botonOK.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(barra2.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Tiene que rellenar el campo Adresse",
							  "ERROR", 2);
				}
				else if (barra3.getText().isEmpty()){
					JOptionPane.showMessageDialog(null, "Tiene que rellenar el campo Weight",
							  "ERROR", 2);
					
				}else {
			      try {
					Parcel newParcel = service.deliverParcel(clickLocker.getCode(), Integer.parseInt(barra2.getText()),  Float.parseFloat(barra3.getText()), clickFechas);
					if(null!= newParcel) {
						framePrin.updateCombo(clickLocker,index); 
						JOptionPane.showMessageDialog(null,"El parcel se ha añadido ");
						setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, "No se ha podido añadir el parcel",
								  "ERROR", 2);
					}
				  }catch( ParcelServiceException ex ){
					JOptionPane.showMessageDialog(null, ex.getMessage(),
							  "ERROR", 2);
				  }
				}
				
				
			}
			
		});
		
		
		
	}

}
