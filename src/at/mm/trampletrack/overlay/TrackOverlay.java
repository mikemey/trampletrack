package at.mm.trampletrack.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import at.mm.trampletrack.R;
import at.mm.trampletrack.dto.Coordinate;
import at.mm.trampletrack.dto.SettingData;
import at.mm.trampletrack.dto.Track;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class TrackOverlay extends Overlay {

	private static Paint txtPaint;
	private static Bitmap bmp;

	private final GeoPoint[] geoPoints;
	private SettingData settings;

	public TrackOverlay(MapView mapView, Track track) {
		super();
		bmp = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.marker);
		geoPoints = calculateGeoPoints(track);
		txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
		txtPaint.setTextSize(10.0f);
		txtPaint.setColor(Color.DKGRAY);

		settings = new SettingData(track.size());
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		if (!shadow) {
			drawMarkers(mapView, canvas);
		}
	}

	private GeoPoint[] calculateGeoPoints(Track track) {
		GeoPoint[] points = new GeoPoint[track.size()];
		int index = 0;
		for (Coordinate coordinate : track) {
			points[index] = new GeoPoint(//
					(int) (coordinate.getLatitude() * 1E6),// 
					(int) (coordinate.getLongitude() * 1E6));
			index++;
		}
		return points;
	}

	private void drawMarkers(MapView mapView, Canvas canvas) {
		int drawedPoints = 0;
		int pointsPassed = 0;

		int startIndex = settings.getStartPointIndex();
		int visiblePoints = settings.getVisiblePoints();
		int pointSkip = settings.getPointSkips();

		if ((startIndex + visiblePoints) > geoPoints.length) {
			visiblePoints = geoPoints.length - startIndex;
		}
		for (int i = startIndex; drawedPoints < visiblePoints; i++) {
			if (pointsPassed == 0) {
				pointsPassed = pointSkip;
			} else {
				pointsPassed--;
				continue;
			}
			if (i >= geoPoints.length) {
				drawedPoints = visiblePoints;
				continue;
			}
			Point screenPts = new Point();
			mapView.getProjection().toPixels(geoPoints[i], screenPts);

			int picX = screenPts.x - bmp.getWidth() / 2;
			int picY = screenPts.y - bmp.getHeight();

			String text = (i + 1) + "";

			int textX = screenPts.x - text.length() * 3;
			int textY = picY + (bmp.getHeight() / 2) - 3;

			canvas.drawBitmap(bmp, picX, picY, null);
			canvas.drawText(text, textX, textY, txtPaint);
			drawedPoints++;
		}
	}

	private void flipGeoPoints() {
		int middle = (int) Math.floor(geoPoints.length / 2.0d);
		int tailIndex;
		GeoPoint tmp;
		for (int i = 0; i < middle; i++) {
			tailIndex = geoPoints.length - 1 - i;
			tmp = geoPoints[i];
			geoPoints[i] = geoPoints[tailIndex];
			geoPoints[tailIndex] = tmp;
		}
	}

	public SettingData getCurrentSettings() {
		settings.reset();
		return settings;
	}

	public void setSettings(SettingData newSettings) {
		settings = newSettings;
		if (settings.hasDirectionChanged()) {
			flipGeoPoints();
		}
	}
}
