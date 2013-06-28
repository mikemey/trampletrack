package at.mm.trampletrack;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import at.mm.trampletrack.backend.DBAdapter;

public class StartActivity extends Activity {

	private Button importBtn;
	private Button showFilesBtn;
	private Button deleteTracksBtn;
	private Button showMapBtn;

	private DBAdapter dba;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		importBtn = (Button) findViewById(R.id.importBtn);
		showFilesBtn = (Button) findViewById(R.id.showFilesBtn);
		deleteTracksBtn = (Button) findViewById(R.id.deleteAllTracksBtn);
		showMapBtn = (Button) findViewById(R.id.showMapBtn);

		dba = DBAdapter.getInstance(this);
		importBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent showFiles = new Intent(v.getContext(), ImportActivity.class);
				startActivity(showFiles);
			}
		});

		showFilesBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent tracksActivity = new Intent(v.getContext(), ShowTracksActivity.class);
				startActivity(tracksActivity);
			}
		});

		deleteTracksBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deleteAllTracks();
			}
		});
		showMapBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent mapsActivity = new Intent(v.getContext(), GMapActivity.class);
				startActivity(mapsActivity);
			}
		});
	}

	private void deleteAllTracks() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon);
		builder.setTitle("Delete tracks:");
		builder.setMessage("Delete all imported tracks?");
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("Kill 'em", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				List<String> trackNames = dba.getAllTrackNames();
				for (String trnames : trackNames) {
					dba.deleteTrack(trnames);
				}
			}
		});
		builder.show();
	}

}