package at.mm.trampletrack;

import java.util.Iterator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import at.mm.trampletrack.dto.Coordinate;
import at.mm.trampletrack.dto.Track;


public class GMapActivity extends Activity {
	private GoogleMap map;
	private boolean showTrack;

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		MapFragment fragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment));
		map = fragment.getMap();

		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		LatLng position = new LatLng(loc.getLatitude(), loc.getLongitude());
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.myloc);
		MarkerOptions options = new MarkerOptions().position(position).icon(icon );
		map.addMarker(options);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5));
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		
//		mapView.getOverlays().add(new ZoomOverlay());
//		locOverlay = new LocationOverlay(getApplicationContext(), mapView);
//		locOverlay.enableMyLocation();
//
//		mapView.getOverlays().add(locOverlay);
		
		Bundle extras = getIntent().getExtras();
		showTrack = extras != null;
		if (showTrack) {
			Track track = (Track) extras.getSerializable(Statics.TRACK_DATA_KEY);
			Iterator<Coordinate> coordinates = track.iterator();
			PolylineOptions polyOptions= new PolylineOptions();
			while(coordinates.hasNext()){
				Coordinate coord = coordinates.next();
				polyOptions.add(new LatLng(coord.getLatitude(), coord.getLongitude()));		
			}
			
			map.addPolyline(polyOptions);
			//trackOverlay = new TrackOverlay(mapView, track);
			//mapView.getOverlays().add(trackOverlay);
		}
	}
}
