package paa.locker.util.impl;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;

/**
 * A waypoint that also has a color and a label
 * 
 * @author Martin Steiger
 */
public class LockerWaypoint extends DefaultWaypoint {
	public int getSmallAvailable() {
		return smallAvailable;
	}

	public int getSmallTotal() {
		return smallTotal;
	}

	public int getLargeAvailable() {
		return largeAvailable;
	}

	public int getLargeTotal() {
		return largeTotal;
	}

	private final int smallAvailable;
	private final int smallTotal;
	private final int largeAvailable;
	private final int largeTotal;
	private final Long lockerCode;

	public Long getLockerCode() {
		return lockerCode;
	}

	/**
	 *
	 * @param lockerCode
	 * @param longitude
	 * @param latitude
	 */
	public LockerWaypoint(int smallAvailable, int smallTotal, int largeAvailable, int largeTotal, Long lockerCode, double longitude, double latitude) { //GeoPosition coord) {
		super(new GeoPosition(latitude, longitude));
		this.smallAvailable = smallAvailable;
		this.smallTotal = smallTotal;
		this.largeAvailable = largeAvailable;
		this.largeTotal = largeTotal;
		this.lockerCode = lockerCode;
	}
}
