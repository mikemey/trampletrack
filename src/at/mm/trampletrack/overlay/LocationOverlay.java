package at.mm.trampletrack.overlay;

import android.content.Context;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationOverlay extends MyLocationOverlay {

	private final MapView mapView;
	private boolean locFocusEnabled = false;

	public LocationOverlay(Context context, MapView mapView) {
		super(context, mapView);
		this.mapView = mapView;
	}

	private Location lastLocation;

	public void setLocationFocusEnabled(boolean enabled) {
		locFocusEnabled = enabled;
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		if (locFocusEnabled) {
			if (lastLocation == null || location.distanceTo(lastLocation) > 2) {
				lastLocation = location;
				int latitude = (int) (lastLocation.getLatitude() * 1E6);
				int longitude = (int) (lastLocation.getLongitude() * 1E6);

				GeoPoint newPoint = new GeoPoint(latitude, longitude);
				mapView.getController().animateTo(newPoint);
			}
		}
	}
}
