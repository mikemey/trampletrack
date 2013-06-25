package at.mm.trampletrack;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.mm.trampletrack.backend.DBAdapter;
import at.mm.trampletrack.backend.GPXImporter;

public class ImportActivity extends ListActivity {
	private List<String> item = null;
	private List<String> path = null;
	private String root = "/";
	private TextView myPath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.var_list);
		myPath = (TextView) findViewById(R.id.list);
		getDir(root);
		
		importDefaultTrackForTest();

	}

	private void importDefaultTrackForTest() {
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/dir1/dir2");
			dir.mkdirs();
			File file = new File(dir, "filename.gpx");
			InputStream in = getAssets().open("SWCP13.gpx");
			FileOutputStream f = new FileOutputStream(file);
			
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);

		item = new ArrayList<String>();
		path = new ArrayList<String>();

		File f = new File(dirPath);
		File[] unsortedFiles = f.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory())
					return true;
				return pathname.getName().endsWith("gpx");
			}
		});

		if (!dirPath.equals(root)) {

			item.add(root);
			path.add(root);

			item.add("../");
			path.add(f.getParent());

		}

		File[] sortedFiles = sortFiles(unsortedFiles);

		for (int i = 0; i < sortedFiles.length; i++) {
			File file = sortedFiles[i];
			if (!file.canRead()) {
				continue;
			}

			path.add(file.getPath());
			if (file.isDirectory())
				item.add(file.getName() + "/");
			else
				item.add(file.getName());
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.row, item);
		setListAdapter(fileList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		final File file = new File(path.get(position));
		if (file.isDirectory()) {
			getDir(path.get(position));
		} else {
			Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.icon);
			builder.setTitle("Import file:");
			builder.setMessage(file.getName());
			builder.setNegativeButton("Cancel", null);
			builder.setPositiveButton("Import File", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					importTrackFile(file);
				}
			});
			builder.show();
		}
	}

	private void importTrackFile(File importFile) {
		String msg = null;
		try {
			DBAdapter dba = DBAdapter.getInstance(null);
			GPXImporter.getInstance().parseImport(dba, importFile);
			msg = importFile.getName() + " imported!";
		} catch (Exception e) {
			msg = e.getMessage();
		} finally {
			Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.icon);
			builder.setTitle("Import file");
			builder.setMessage(msg);
			builder.setPositiveButton("OK", null);
			builder.show();
		}
	}

	private File[] sortFiles(File[] unsortedFiles) {
		Arrays.sort(unsortedFiles, new Comparator<File>() {

			@Override
			public int compare(File file1, File file2) {
				if (file1.isDirectory() && !file2.isDirectory()) {
					return -1;
				}
				if (!file1.isDirectory() && file2.isDirectory()) {
					return 1;
				}
				return file1.compareTo(file2);
			}
		});
		return unsortedFiles;
	}
}
