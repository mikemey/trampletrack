package at.mm.trampletrack;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.mm.trampletrack.backend.DBAdapter;
import at.mm.trampletrack.dto.Track;

public class ShowTracksActivity extends ListActivity {
	private List<String> trackText = null;

	private static final String REMOVE_TRACK = "Delete track";
	private static final String SHOW_TRACK = "Open track in map";
	private DBAdapter dba;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.var_list);
		TextView header = (TextView) findViewById(R.id.list);
		header.setText("Available tracks;");
		dba = DBAdapter.getInstance(null);

		refreshTracks();
	}

	private void refreshTracks() {
		trackText = new ArrayList<String>();

		List<String> trackNames = dba.getAllTrackNames();
		for (String tname : trackNames) {
			Track track = dba.getTrack(tname);
			trackText.add(String.format("%s [%s]", tname, track.size()));
		}
		ArrayAdapter<String> trackList = new ArrayAdapter<String>(this, R.layout.row, trackText);
		setListAdapter(trackList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String listname = trackText.get(position);
		final String trackname = listname.substring(0, listname.indexOf('[') - 1);
		final CharSequence[] items = { SHOW_TRACK, REMOVE_TRACK };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(trackname);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int itemIndex) {
				if (items[itemIndex].equals(SHOW_TRACK)) {
					Track track = dba.getTrack(trackname);
					Intent mapsActivity = new Intent(getApplicationContext(), GMapActivity.class);
					mapsActivity.putExtra(Statics.TRACK_DATA_KEY, track);
					startActivity(mapsActivity);
				}
				if (items[itemIndex].equals(REMOVE_TRACK)) {
					dba.deleteTrack(trackname);
					refreshTracks();
				}
			}
		});
		builder.show();
	}
}