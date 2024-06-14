package paa.locker.util;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import paa.locker.business.ParcelService;
import paa.locker.persistence.Locker;
import paa.locker.util.impl.LockerWaypointRenderer;
import paa.locker.util.impl.LockerWaypoint;

import java.awt.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


public class LockerMap extends JXMapKit {
	
    private static final long serialVersionUID = 1L;
    protected WaypointPainter<LockerWaypoint> waypointPainter;
    protected Set<LockerWaypoint> waypoints;
    protected ParcelService service;
    

    /**
     * Construye un nuevo mapa con la vista centrada en Madrid, con el tamaño
     * preferido indicado por el usuario.
     *
     * @param preferredWidth Ancho preferido
     * @param preferredHeight Alto preferido
     */
    public LockerMap(int preferredWidth, int preferredHeight, ParcelService service) {
    	
        super();
        this.setDefaultProvider(DefaultProviders.OpenStreetMaps);
        this.service = service;
        

        TileFactoryInfo info = new OSMTileFactoryInfo();
        TileFactory tf = new DefaultTileFactory(info);
        
        this.setTileFactory(tf);
        this.setZoom(7);
        this.setAddressLocation(new GeoPosition(40.438889, -3.691944)); // Madrid
        this.getMainMap().setRestrictOutsidePanning(true);
        this.getMainMap().setHorizontalWrapped(false);

        this.waypointPainter = new WaypointPainter<LockerWaypoint>();
        waypointPainter.setRenderer(new LockerWaypointRenderer());
        this.getMainMap().setOverlayPainter(this.waypointPainter);
        this.waypoints = new HashSet<LockerWaypoint>();

        ((DefaultTileFactory) this.getMainMap().getTileFactory()).setThreadPoolSize(8);
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    }

  
    public void showAvailability(LocalDate date) {
        this.waypoints.clear();
        for (Locker p: this.service.findAllLockers()) {
            waypoints.add(new LockerWaypoint(this.service.availableCompartments(p.getCode(), date, ParcelService.SmallMaxWeight),
                    p.getSmallCompartments(),
                    this.service.availableCompartments(p.getCode(), date, ParcelService.LargeMaxWeight),
                    p.getLargeCompartments(),
                    p.getCode(),
                    p.getLongitude(),
                    p.getLatitude()));
        }
        this.waypointPainter.setWaypoints(waypoints);
        this.repaint();
    }
}
