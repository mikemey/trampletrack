package at.mm.trampletrack.overlay;

import android.view.MotionEvent;
import at.mm.trampletrack.Statics;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ZoomOverlay extends Overlay {

	private long lastUpEvent = 0;
	private float lastX;
	private float lastY;

	@Override
	public boolean onTouchEvent(MotionEvent ev, MapView mapView) {
		int zoomControlHeight = mapView.getZoomButtonsController().getContainer().getHeight() / 2;
		int mapViewHeight = mapView.getHeight();

		float xcoord = ev.getX();
		if (xcoord <= (mapViewHeight - zoomControlHeight)) {
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				float ycoord = ev.getY();
				if (withinRange(lastX, lastY, xcoord, ycoord)
						&& (System.currentTimeMillis() - lastUpEvent) < Statics.ZOOM_DOUBLE_CLICK_TIME) {
					GeoPoint point = mapView.getProjection().fromPixels((int) xcoord, (int) ycoord);
					mapView.getController().animateTo(point);
					mapView.getController().zoomInFixing((int) xcoord, (int) ycoord);
					return true;
				}
				lastX = xcoord;
				lastY = ycoord;
			}
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				lastUpEvent = System.currentTimeMillis();
			}
			if (ev.getAction() == MotionEvent.ACTION_MOVE) {
				// TODO: disable animateTo for new gpslocations
			}
		}
		return false;
	}

	private boolean withinRange(float originalX, float originalY, float targetX, float targetY) {
		float deltaX = originalX - targetX;
		float deltaY = originalY - targetY;

		double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
		return distance < Statics.ZOOM_DOUBLE_CLICK_DISTANCE;
	}
}
