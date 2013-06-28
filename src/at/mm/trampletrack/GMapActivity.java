package at.mm.trampletrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import at.mm.trampletrack.dto.Coordinate;
import at.mm.trampletrack.dto.Track;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class GMapActivity extends Activity {
	private GoogleMap map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		MapFragment fragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment));
		map = fragment.getMap();
		map.setMyLocationEnabled(true);		

		updateTrack();
	}

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapmenu, menu);   
		return true;
    }
	
   @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   executeOption(item.getItemId());
	   return true;
   }

	private void executeOption(int itemId) {

		switch (itemId) {
			case R.id.mapLayers:
				showLayerOptions();
				break;
			case R.id.selectTrack:
				exitMap();
				break;
			default: 
				break;
			}
	}
   
	private void exitMap() {
		this.setResult(RESULT_OK);
		this.finish();
	}

	private void showLayerOptions() {

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.select_maptype)
	           .setItems  (R.array.maptypes_array, new DialogInterface.OnClickListener() {
	        	   
	       		@Override
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position of the selected item
	            	   map.setMapType(which + 1);
	           }
	    });
		builder.show();
	}
	
	private void updateTrack() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			Track currentTrack = (Track) extras.getSerializable(Statics.TRACK_DATA_KEY);
			showOnMapAndZoomIn(currentTrack);
		} else{
			LatLng england = new LatLng(51, 0);
			zoomIn(england);
		}
	}

	private void showOnMapAndZoomIn(Track track) {
		if( track == null) {
			return;
		}
		PolylineOptions path= new PolylineOptions();
		for(Coordinate coord : track){
			path.add(new LatLng(coord.getLatitude(), coord.getLongitude()));	
		}
		
		map.addPolyline(path);
		
		Coordinate origin = track.iterator().next();
		zoomIn(new LatLng(origin.getLatitude(), origin.getLongitude()));
	}
	
	private void zoomIn(LatLng latLng) {
		
		printMessage(latLng.toString());
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng , 5));
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	}
	
	private void printMessage(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
}
