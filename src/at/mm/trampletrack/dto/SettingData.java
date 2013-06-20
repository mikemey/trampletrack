/**
 * 
 */
package at.mm.trampletrack.dto;

import java.io.Serializable;

public class SettingData implements Serializable {
	private static final long serialVersionUID = -5624111951823455796L;

	private final int geoPointLength;

	private boolean dirForward = true;
	private boolean directionChanged = false;

	private int visiblePoints = 10;
	private boolean visiblePointsChanged = false;

	private int pointSkips = 0;
	private boolean pointSkipsChanged = false;

	private int startPointIndex = 0;
	private boolean startPointChanged = false;

	public SettingData(int numOfPoints) {
		geoPointLength = numOfPoints;
	}

	public int getMaxPoints() {
		return geoPointLength;
	}

	public boolean hasStartPointIndexChanged() {
		return startPointChanged;
	}

	public void setStartPointIndex(int index) {
		if (startPointIndex != index) {
			checkIndex(index);
			startPointChanged = true;
			startPointIndex = index;
		}
	}

	public int getStartPointIndex() {
		return startPointIndex;
	}

	public boolean hasDirectionChanged() {
		return directionChanged;
	}

	public boolean hasVisiblePointsChanged() {
		return visiblePointsChanged;
	}

	public boolean hasPointSkipsChanged() {
		return pointSkipsChanged;
	}

	public boolean isDirectionForward() {
		return dirForward;
	}

	public void setDirectionForward(boolean directionForward) {
		if (this.dirForward != directionForward) {
			directionChanged = true;
			this.dirForward = directionForward;
		}
	}

	public int getVisiblePoints() {
		return visiblePoints;
	}

	public void setVisiblePoints(int visiblePoints) {
		if (this.visiblePoints != visiblePoints) {
			visiblePointsChanged = true;
			this.visiblePoints = visiblePoints;
		}
	}

	public int getPointSkips() {
		return pointSkips;
	}

	public void setPointSkips(int pointSkips) {
		if (this.pointSkips != pointSkips) {
			this.pointSkips = pointSkips;
			pointSkipsChanged = true;
		}
	}

	public void reset() {
		directionChanged = false;
		visiblePointsChanged = false;
		pointSkipsChanged = false;
		startPointChanged = false;
	}

	public boolean anyChanges() {
		return directionChanged || visiblePointsChanged || pointSkipsChanged || startPointChanged;
	}

	private void checkIndex(int index) {
		if (index < 0 || index >= geoPointLength) {
			throw new IndexOutOfBoundsException("index out of bounds: " + index);
		}
	}
}