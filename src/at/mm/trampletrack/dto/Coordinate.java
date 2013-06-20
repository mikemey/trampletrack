package at.mm.trampletrack.dto;

import java.io.Serializable;

public class Coordinate implements Serializable {
	private static final long serialVersionUID = 6505726104333548868L;
	private double latitude;
	private double longitude;

	public Coordinate(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		return Double.valueOf(latitude).hashCode() ^ Double.valueOf(longitude).hashCode();
	}
}
