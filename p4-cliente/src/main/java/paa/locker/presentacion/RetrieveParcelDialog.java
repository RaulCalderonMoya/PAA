package paa.locker.presentacion;

import java.awt.BorderLayout;




import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import paa.locker.business.RemoteParcelService;
import paa.locker.business.ParcelServiceException;
import paa.locker.model.Locker;
import paa.locker.model.Parcel;



public class RetrieveParcelDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;
	private JButton botonOK;
	private JButton botonCancel;
	private JPanel panelbotones;
	private JPanel panelCentral;
	private JComboBox <Locker> lockers;
	private JComboBox <Parcel>infoParcel;
	private JLabel barra1;
	private JLabel barra2;
	
	private  Locker clickLocker;
	private Parcel clickParcel;
	
	private RemoteParcelService service;
	
	public RetrieveParcelDialog (FramePrincipal frame,String title,List<Locker> listaLockers, Locker clickLockers, int index) {
		super(frame,title,true);
		this.clickLocker = clickLockers;
		lockers = new JComboBox<Locker>(new Vector <Locker> (listaLockers));
		lockers.setSelectedIndex(index);
		lockers.setSelectedItem(clickLockers);
		infoParcel = new JComboBox <Parcel>(new Vector<Parcel>(listaLockers.get(index).getParcels())); 
		service = new RemoteParcelService();
		if(clickLockers.getParcels().size()>0) {
			clickParcel = clickLockers.getParcels().get(0);
			infoParcel.setSelectedIndex(0);
		}
		botonOK = new JButton("OK");
		botonCancel = new JButton("Cancel");
		panelbotones = new JPanel();
		panelbotones.add(botonOK);
		panelbotones.add(botonCancel);
		
		barra1 = new JLabel("Locker: ");
		barra2 = new JLabel("Parcel: ");
		panelCentral = new JPanel (new GridLayout (2,2));
		panelCentral.add(barra1);
		panelCentral.add(lockers);
		panelCentral.add(barra2);
		panelCentral.add(infoParcel);
		
		

		setLayout(new BorderLayout());
		add(panelbotones, BorderLayout.SOUTH);
		add(panelCentral,BorderLayout.NORTH);
		
		lockers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((Locker)lockers.getSelectedItem() != null) {
					clickLocker = (Locker) lockers.getSelectedItem();
					if(clickLocker.getParcels().size()>0) {
						infoParcel.removeAllItems();
						for(Parcel s: clickLocker.getParcels()) {
							infoParcel.addItem(s);
						}
						clickParcel = clickLocker.getParcels().get(0);
						infoParcel.setSelectedItem(clickParcel);
					}else {
						infoParcel.removeAllItems();
					}
					
					
					
				}
				
			}
		});
		
		infoParcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(infoParcel.getSelectedItem()!=null)
					clickParcel = (Parcel) infoParcel.getSelectedItem();
			}
		});
		
		botonCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
			
		});
		botonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
                if (clickParcel != null) {
					
					try {
						service.retrieveParcel(clickParcel.getCode());
						
						frame.updateCombo (clickLocker,-1);
						JOptionPane.showMessageDialog(null,"La reserva con: "+clickParcel.getCode()+" se ha eliminado. ");
						dispose(); 

					}catch(ParcelServiceException ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(),
								  "ERROR", 2);
					}
				}else {
					JOptionPane.showMessageDialog(null, "No se ha seleccionado el Locker o Parcel",
							  "ERROR", 2);
				}
				
			}
			
		});
		
	}
	
}
