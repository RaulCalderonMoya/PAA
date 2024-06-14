package paa.locker.presentacion;



import java.awt.BorderLayout;









import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import paa.locker.business.RemoteParcelService;

import paa.locker.model.Locker;
import paa.locker.model.Parcel;
import paa.locker.util.LockerMap;

		public class FramePrincipal extends JFrame {
			
			
			
			private static final long serialVersionUID = 1L;
			private JMenuBar menuPrincipal;
			private JMenu menu1;
			private JMenu menu2;
			private JMenuItem lockerNuevo;
			private JMenuItem reservaParcel;
			private JMenuItem eliminarParcel;
			private JMenuItem submenu;
			
			private JMenuItem salirMenu;
			private JToolBar barraFotos;
			private LockerMap mapa;
			private JComboBox <Locker> boxLockers;
			private List<Locker> listaLockers;
			private List<Parcel> listaParcels;
			private JList <Parcel> JListaParcels;
		
			private Locker clickLocker;
			private JComboBox<LocalDate> boxFecha;
			private RemoteParcelService service;
			private FramePrincipal frame;
			
			public FramePrincipal (String title) {
				super(title);
				frame =this;
				service = new RemoteParcelService();
				listaLockers = service.findAllLockers();
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//Inicializamos Menu principal
				menuPrincipal = new JMenuBar();
			
				
				
				//Menu de File
				menu1 = new JMenu ("File");
				lockerNuevo = new JMenuItem("New Locker");
				reservaParcel = new JMenuItem("Deliver Parcel");
				eliminarParcel = new JMenuItem("Retrieve Parcel");
				salirMenu = new JMenuItem("Quit");
				
				menuPrincipal.add(menu1);
				menu1.add(lockerNuevo);
				menu1.add(reservaParcel);
				menu1.add(eliminarParcel);
				menu1.add(salirMenu);
				
				//	Menu Help
				menu2 = new JMenu ("Help");
				submenu = new JMenuItem("About");
				menuPrincipal.add(menu2);
				menu2.add(submenu);
				

				//Visualizamos el menu principal
				setJMenuBar(menuPrincipal);
				
				lockerNuevo.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						CreateLockerDialog newLocker = new CreateLockerDialog(frame,"Create Locker", service); 
						newLocker.setBounds(400,200, 500, 200);
						newLocker.setResizable(true);
						newLocker.setVisible(true);
						
					}
					
				});
				
				reservaParcel.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (clickLocker!=null) {
							DeliverParcelDialog nuevaReserva = new DeliverParcelDialog (frame,"Deliver Parcel", listaLockers, clickLocker, boxLockers.getSelectedIndex() );
							nuevaReserva.setBounds(400,200, 500, 200);
							nuevaReserva.setResizable(true);
							nuevaReserva.setVisible(true);	
						}else {
							JOptionPane.showMessageDialog(null, "Debe seleccionar primero un Locker",
									  "ERROR", 2);
						}
						
					}
					
				});
				eliminarParcel.addActionListener(new ActionListener () {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (clickLocker!=null) {
							RetrieveParcelDialog  deleteParcel = new RetrieveParcelDialog (frame,"Retrieve Parcel", listaLockers, clickLocker, boxLockers.getSelectedIndex()); 
							deleteParcel.setBounds(400,200, 500, 200);
							deleteParcel.setResizable(true);
							deleteParcel.setVisible(true);
						}else {
							JOptionPane.showMessageDialog(null, "Debe seleccionar primero un Locker",
									  "ERROR", 2);
						}
						
					}
					
				});
				salirMenu.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
						
					}
					
					
					
					
				});
				
				submenu.addActionListener(new ActionListener () {

					@Override
					public void actionPerformed(ActionEvent e) {
						AboutDialog about = new AboutDialog (null,"About");
						about.setBounds(400,200, 500, 200);
						setResizable(true);
						about.setVisible(true);
						
					}
					
					
				});
				
				//Creamos la barra de botones, los botones e iamgenes
				
				barraFotos = new JToolBar();
				ImageIcon foto1 =new ImageIcon (getClass().getResource("/locker.png"));
				ImageIcon foto2 =new ImageIcon (getClass().getResource("/deliver.png"));
				ImageIcon foto3 =new ImageIcon (getClass().getResource("/retrieve.png"));
				JButton locker = new JButton(foto1);
				JButton deliver = new JButton(foto2);
				JButton retrieve = new JButton(foto3);
				
				
				//Añadimos los botones a la barra de botones
				barraFotos.add(locker);
				barraFotos.add(deliver);
				barraFotos.add(retrieve);
				
				
				boxLockers = new JComboBox<Locker>( new Vector<Locker>(listaLockers));
				TitledBorder tituloLocker = BorderFactory.createTitledBorder("Lockers");
				
				
				boxLockers.setBorder(tituloLocker);
				boxLockers.setEditable(false);
				if(listaLockers!= null && listaLockers.size()>0) {
					clickLocker = listaLockers.get(0);									
					boxLockers.setSelectedItem(clickLocker);
					listaParcels=clickLocker.getParcels();
				}
				
				TitledBorder tituloParcel = BorderFactory.createTitledBorder("Parcels");
				
				JListaParcels = new JList <Parcel> ();
				if(clickLocker != null) {
					
					JListaParcels.setListData(new Vector <Parcel>(clickLocker.getParcels()));
					
				}else {
					JListaParcels.setListData(new Vector<Parcel>());
				}
				
				JListaParcels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				
				JScrollPane scroller = new JScrollPane( JListaParcels );
				scroller.setBorder(tituloParcel);
				
				

				
				JPanel parteIzquierda = new JPanel();
                parteIzquierda.setLayout(new BorderLayout());
				parteIzquierda.add(boxLockers,BorderLayout.NORTH);
				parteIzquierda.add(scroller,BorderLayout.CENTER);
				

				parteIzquierda.setVisible(true);
				
				//Añadimos el mapa y fecha  al frame y le ponemos el titulo
				
				JPanel mapayFecha =new JPanel();
				TitledBorder mapaTitulo = BorderFactory.createTitledBorder("Locker Map");
				mapayFecha.setBorder(mapaTitulo);
				
				mapa = new LockerMap(200,250,service);
				mapa.showAvailability(LocalDate.now());
				boxFecha = new JComboBox <LocalDate>() ;
				int dias=0;
				while(dias<15) {
					boxFecha.addItem(LocalDate.now().plusDays(dias));
					dias++;
				}
				
				boxFecha.setEditable(false);
				boxFecha.setSelectedItem(LocalDate.now());
				
				mapayFecha.setLayout(new BorderLayout());
				mapayFecha.add(boxFecha,BorderLayout.NORTH);
				mapayFecha.add(mapa,BorderLayout.CENTER);
				
				//TOTAL FRAMEEEE
				setLayout(new BorderLayout());
				add(barraFotos,BorderLayout.NORTH);
				add(mapayFecha,BorderLayout.CENTER);
				add(parteIzquierda,BorderLayout.WEST); 
			
				setVisible(true);
                boxLockers.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						if(boxLockers.getSelectedItem()  != null) {
							clickLocker = (Locker) boxLockers.getSelectedItem();
							updateList(clickLocker);
							
							
						}
					}
				});
                JListaParcels.addListSelectionListener(new ListSelectionListener() {
				      public void valueChanged(ListSelectionEvent e) {
				    	  if (JListaParcels.getSelectedValue() !=null) {
				    		  
							        InfoReservasDialog infoReserva =  new InfoReservasDialog(frame,JListaParcels.getSelectedValue()); 
				    		 infoReserva.setVisible(true);
				    	  }
				      }
				});
                boxFecha.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
					    mapa.showAvailability((LocalDate) boxFecha.getSelectedItem());
				    }
			   });
               
                locker.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						CreateLockerDialog newLocker = new CreateLockerDialog(frame, "Create Locker",service);
						setResizable(true);
						newLocker.setBounds(400,200, 500, 200);
						newLocker.setVisible(true);
						
						
					}
					
				}); 
                
                deliver.addActionListener(new ActionListener () {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (clickLocker!=null) {
							DeliverParcelDialog deliverParcel = new DeliverParcelDialog(frame,"Deliver Parcel", listaLockers, clickLocker,boxLockers.getSelectedIndex() );
							deliverParcel.setBounds(400,200, 500, 200);
							setResizable(true);
							deliverParcel.setVisible(true);	
						}else {
							JOptionPane.showMessageDialog(null, "Debe seleccionar primero un Locker",
									  "ERROR", 2);
						}
						
						
						
						
						
					}
					
					
				});
                
                retrieve.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(clickLocker != null) {
							RetrieveParcelDialog retrieveParcelDialog = new RetrieveParcelDialog (frame,"Retrieve Parcel", listaLockers, clickLocker, boxLockers.getSelectedIndex());
							retrieveParcelDialog.setBounds(400,200, 500, 200);
							setResizable(true);
							retrieveParcelDialog.setVisible(true);
						}else {
							JOptionPane.showMessageDialog(null, "Debe seleccionar primero un Locker",
									  "ERROR", 2);
							
						}
						
						
					}
					
				});
                

			}
			
			
			
			
			public void updateCombo (Locker clickLocker, int selectedIndex) {
				boxLockers.removeAllItems();

				this.clickLocker = clickLocker;
				listaLockers = service.findAllLockers();

				int i=0;
				for (Locker l:listaLockers) 
				{
					boxLockers.addItem(l);
					if(l.equals(clickLocker)) {
						selectedIndex=i;
					}
					i++;
				}
				boxLockers.setSelectedIndex(selectedIndex);
				boxLockers.setSelectedItem(this.clickLocker);
				updateList(this.clickLocker);
				
				
			
				
				
			}
			public void updateList( Locker updateLocker ) {
				if(updateLocker.getCode().equals(clickLocker.getCode())) {
					listaParcels = service.findLocker(updateLocker.getCode()).getParcels();
					clickLocker = updateLocker;
					JListaParcels.setListData(new Vector<Parcel> (listaParcels));
					
				}
				mapa.showAvailability(LocalDate.now());
			}
			
			
			
			

		}
		




