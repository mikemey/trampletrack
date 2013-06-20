package at.mm.trampletrack.backend;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import at.mm.trampletrack.dto.Coordinate;
import at.mm.trampletrack.dto.Track;

public class DBAdapter {
	private static final String COL_ID = "id";
	private static final String COL_TRACK_NAME = "track_name";
	private static final String COL_LATITUDE = "lat_coord";
	private static final String COL_LONGITUDE = "long_coord";

	private static final String TABLE_NAME = "geo_coordinates";
	private static final String DATABASE_NAME = "trackpoint";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "(" + // 
			COL_ID + " integer primary key autoincrement, " + COL_TRACK_NAME + " text not null, " + // 
			COL_LATITUDE + " text not null, " + COL_LONGITUDE + " text not null);";
	private static final String DATABASE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

	private static DBAdapter instance;
	private final Context context;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	private DBAdapter(Context ctx) {
		this.context = ctx;
		dbHelper = new DatabaseHelper(context);
	}

	public static DBAdapter getInstance(Context ctx) {
		if (instance == null) {
			instance = new DBAdapter(ctx);
		}
		return instance;
	}

	private void startTx() throws SQLException {
		db = dbHelper.getWritableDatabase();
		db.beginTransaction();
	}

	private void endTx() {
		db.setTransactionSuccessful();
		db.endTransaction();
		dbHelper.close();
	}

	public void addTrack(Track track) {
		startTx();

		String trackName = track.getName();
		Cursor query = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = ?"//
				, TABLE_NAME, COL_TRACK_NAME),// 
				new String[] { trackName });

		if (query.getCount() > 0) { // already a track with that name
			trackName = trackName + "_X";
		}
		query.close();

		for (Coordinate coordinate : track) {
			ContentValues values = new ContentValues();
			values.put(COL_TRACK_NAME, trackName);
			values.put(COL_LATITUDE, coordinate.getLatitude() + "");
			values.put(COL_LONGITUDE, coordinate.getLongitude() + "");
			db.insertOrThrow(TABLE_NAME, null, values);
		}
		endTx();
	}

	public void deleteTrack(Track track) {
		deleteTrack(track.getName());
	}

	public void deleteTrack(String trackName) {
		startTx();
		db.delete(TABLE_NAME, COL_TRACK_NAME + "=?", new String[] { trackName });
		endTx();
	}

	public List<String> getAllTrackNames() {
		List<String> trackNames = new ArrayList<String>();

		startTx();
		Cursor query = db.rawQuery(String.format("SELECT DISTINCT %s FROM %s"//
				, COL_TRACK_NAME, TABLE_NAME)//
				, null);

		if (query.moveToFirst()) {
			do {
				trackNames.add(query.getString(0));
			} while (query.moveToNext());
		}
		query.close();
		endTx();
		return trackNames;
	}

	public Track getTrack(String trackName) throws SQLException {
		startTx();

		Track track = null;
		Cursor query = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = ? ORDER BY %s"//
				, TABLE_NAME, COL_TRACK_NAME, COL_ID)//
				, new String[] { trackName });

		if (query.moveToFirst()) {
			int trackIx = query.getColumnIndex(COL_TRACK_NAME);
			track = new Track(query.getString(trackIx));

			int latIx = query.getColumnIndex(COL_LATITUDE);
			int longIx = query.getColumnIndex(COL_LONGITUDE);
			do {
				Double latValue = Double.parseDouble(query.getString(latIx));
				Double longValue = Double.parseDouble(query.getString(longIx));
				track.addCoordinate(new Coordinate(latValue, longValue));
			} while (query.moveToNext());
		}
		query.close();
		endTx();
		return track;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("DBAdapter", "Upgading database from version [%o] to version [%n]"//
					.replace("%o", oldVersion + "")//
							.replace("%n", newVersion + ""));
			db.execSQL(DATABASE_DROP);
			onCreate(db);
		}
	}
}
