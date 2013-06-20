package at.mm.trampletrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import at.mm.trampletrack.dialog.MapSettingActivity;
import at.mm.trampletrack.dto.SettingData;
import at.mm.trampletrack.dto.Track;
import at.mm.trampletrack.overlay.LocationOverlay;
import at.mm.trampletrack.overlay.TrackOverlay;
import at.mm.trampletrack.overlay.ZoomOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class GMapActivity extends MapActivity {
	private final int CHANGE_SETTINGS_REQUEST = 100;
	private static final String NO_TRACK_AVAILABLE = "No track visible!";
	private static final String[] LAYER_ITEMS = { "Satellite" };
	private static final boolean[] LAYER_SELECT = { false };

	private MapView mapView;
	private boolean showTrack;
	private LocationOverlay locOverlay;
	private TrackOverlay trackOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapView.getOverlays().add(new ZoomOverlay());
		locOverlay = new LocationOverlay(getApplicationContext(), mapView);
		locOverlay.enableMyLocation();

		mapView.getOverlays().add(locOverlay);

		Bundle extras = getIntent().getExtras();
		showTrack = extras != null;
		if (showTrack) {
			Track track = (Track) extras.getSerializable(Statics.TRACK_DATA_KEY);
			trackOverlay = new TrackOverlay(mapView, track);
			mapView.getOverlays().add(trackOverlay);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.myLoc:
			locOverlay.setLocationFocusEnabled(true);
			GeoPoint geoPoint = locOverlay.getMyLocation();
			if (geoPoint != null) {
				mapView.getController().animateTo(geoPoint);
			} else {
				printMessage("GPS not available!");
			}
			break;
		case R.id.nextWaypoint:
			changeWaypoint(true);
			break;
		case R.id.prevWaypoint:
			changeWaypoint(false);
			break;
		case R.id.mapLayers:
			showLayerOptions();
			break;
		case R.id.trackSettings:
			if (trackOverlay != null) {
				SettingData settings = trackOverlay.getCurrentSettings();
				Intent showSettings = new Intent(this, MapSettingActivity.class);
				showSettings.putExtra(Statics.SETTINGS, settings);
				startActivityForResult(showSettings, CHANGE_SETTINGS_REQUEST);
			} else {
				printMessage(NO_TRACK_AVAILABLE);
			}
			break;
		case R.id.selectTrack:
			this.setResult(RESULT_OK);
			this.finish();
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHANGE_SETTINGS_REQUEST) {
			if (resultCode == RESULT_OK) {
				SettingData settings = (SettingData) data.getSerializableExtra(Statics.SETTINGS);
				if (settings.anyChanges()) {
					trackOverlay.setSettings(settings);
					mapView.postInvalidate();
				}
			}
		}
	}

	private void printMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private void changeWaypoint(boolean next) {
		if (trackOverlay != null) {
			SettingData currSettings = trackOverlay.getCurrentSettings();
			int indexSkip = currSettings.getPointSkips() + 1;
			int nextIndex = currSettings.getStartPointIndex() + (next ? indexSkip : -indexSkip);
			try {
				currSettings.setStartPointIndex(nextIndex);
				trackOverlay.setSettings(currSettings);
				mapView.postInvalidate();
			} catch (IndexOutOfBoundsException iobe) {
				if (nextIndex < 0) {
					printMessage("Waypoint before first track point!");
				} else {
					printMessage(String.format("Waypoint beyond last track point (%s)!", currSettings.getMaxPoints()));
				}
			}
		} else {
			printMessage(NO_TRACK_AVAILABLE);
		}
	}

	private void showLayerOptions() {
		LAYER_SELECT[0] = mapView.isSatellite();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Layer");
		builder.setMultiChoiceItems(LAYER_ITEMS, LAYER_SELECT, new OnMultiChoiceClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (which == 0) {
					mapView.setSatellite(isChecked);
				}

			}
		});
		builder.setPositiveButton("Ok", null);
		builder.show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}