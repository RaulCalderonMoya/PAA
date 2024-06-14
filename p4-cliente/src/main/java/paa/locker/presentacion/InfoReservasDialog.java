package paa.locker.presentacion;

import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import paa.locker.model.Parcel;


public class InfoReservasDialog extends JDialog {
	
	JLabel code; 
	JLabel fecha; 
	JLabel adresse; 
	JLabel locker; 

	public InfoReservasDialog (FramePrincipal frame,Parcel p) {
		
		super(frame,"Informacion Parcel",true);
		JPanel panel=new JPanel();
		
		code = new JLabel(p.getCode().toString());
		fecha = new JLabel(p.getArrivalDate().toString());
		adresse= new JLabel(""+p.getAddressee());
		locker= new JLabel(""+p.getLocker());
		
		panel.setLayout(new GridLayout(4,2));
		panel.add(new JLabel("Codigo"));
		panel.add(code);
		panel.add(new JLabel("Addressee"));
		panel.add(adresse);
		panel.add(new JLabel("Fecha"));
		panel.add(fecha);
		panel.add(new JLabel("Info Locker"));
		panel.add(locker);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(getParent());
		this.add(panel);
		this.setSize(500, 200);
		this.setVisible(false);
	}
	
public InfoReservasDialog (FramePrincipal frame) {
		
		super(frame,"Info Parcel",true);
		JPanel panel=new JPanel();
		
		code = new JLabel("");
		fecha = new JLabel("");
		adresse= new JLabel("");
		locker=  new JLabel("");
		
		panel.setLayout(new GridLayout(4,2));
		panel.add(new JLabel("Codigo"));
		panel.add(code);
		panel.add(new JLabel("Addressee"));
		panel.add(adresse);
		panel.add(new JLabel("Fecha"));
		panel.add(fecha);
		panel.add(new JLabel("Info Locker"));
		panel.add(locker);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(getParent());
		this.add(panel);
		this.setSize(500, 200);
		this.setVisible(false);
	}
	 
	public void updateInfo (Parcel p) {
		code.setText(p.getCode().toString());
		String addresseeString = ""+p.getAddressee()+"";;
		adresse.setText(addresseeString);
		fecha.setText(p.getArrivalDate().toString());
		locker.setText(p.getLocker().toString());

	}


}
