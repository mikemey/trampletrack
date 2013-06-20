package at.mm.trampletrack.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import at.mm.trampletrack.R;
import at.mm.trampletrack.Statics;
import at.mm.trampletrack.dto.SettingData;

public class MapSettingActivity extends Activity {
	private static final String DIR_FWD = "Forward";
	private static final String DIR_BACK = "Backward";

	private Button setStartPointBtn;
	private Button setDirectionBtn;
	private Button setVisiblePointsBtn;
	private Button setSkipPointsBtn;

	private SettingData settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_settings);

		setStartPointBtn = (Button) findViewById(R.id.setStartPointBtn);
		setDirectionBtn = (Button) findViewById(R.id.setDirectionBtn);
		setVisiblePointsBtn = (Button) findViewById(R.id.setVisiblePointsBtn);
		setSkipPointsBtn = (Button) findViewById(R.id.setSkippedPointsBtn);

		settings = (SettingData) getIntent().getSerializableExtra(Statics.SETTINGS);

		setStartPointBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createStartPointDialog();
			}
		});
		setDirectionBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createDirectionDialog();
			}
		});
		setVisiblePointsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createVisiblePointsDialog();
			}
		});
		setSkipPointsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createSkipPointsDialog();
			}
		});
		Intent result = new Intent();
		result.putExtra(Statics.SETTINGS, settings);
		setResult(RESULT_OK, result);
	}

	private void createDirectionDialog() {
		int itemChecked = settings.isDirectionForward() ? 0 : 1;
		final CharSequence[] directionText = { DIR_FWD, DIR_BACK };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Set track direction");
		builder.setSingleChoiceItems(directionText, itemChecked, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int itemIndex) {
				if (directionText[itemIndex].equals(DIR_FWD)) {
					settings.setDirectionForward(true);
				}
				if (directionText[itemIndex].equals(DIR_BACK)) {
					settings.setDirectionForward(false);
				}
			}
		});
		builder.setPositiveButton("Ok", null);
		builder.show();
	}

	private void createVisiblePointsDialog() {
		new NumberInputDialog(this, settings.getVisiblePoints(), "Visible Points", "Enter number of points visible:", new INumberDialogListener() {

			@Override
			public void ready(int newNumber) {

				if ((settings.getStartPointIndex() + newNumber) > settings.getMaxPoints()) {
					echoMessage(//
					String.format("Number of visible points extends max. number of points (%s), set to end of track!", settings.getMaxPoints()));
					newNumber = settings.getMaxPoints() - settings.getStartPointIndex();
				}
				settings.setVisiblePoints(newNumber);
			}
		});
	}

	private void createSkipPointsDialog() {
		new NumberInputDialog(this, settings.getPointSkips(), "Skip Points", "Enter number of points to skip:",// 
				new INumberDialogListener() {

					@Override
					public void ready(int newNumber) {
						settings.setPointSkips(newNumber);
					}
				});
	}

	private void createStartPointDialog() {
		new NumberInputDialog(this, settings.getStartPointIndex() + 1, "Start Point", "Enter start point:",// 
				new INumberDialogListener() {

					@Override
					public void ready(int newNumber) {
						newNumber--;
						if (newNumber >= settings.getMaxPoints()) {
							echoMessage(String.format(//
									"Start point greater than max. points (%s) - set to end of track!", settings.getMaxPoints()));
							newNumber = settings.getMaxPoints() - 1;
						}
						settings.setStartPointIndex(newNumber);
					}
				});
	}

	private void echoMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}