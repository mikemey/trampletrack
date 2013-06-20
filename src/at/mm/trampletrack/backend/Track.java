package at.mm.trampletrack.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Track implements Serializable, Iterable<Coordinate> {
	private static final long serialVersionUID = 5552388240915876671L;
	private List<Coordinate> coordinates;
	private String name;

	public Track(String trackName) {
		coordinates = new ArrayList<Coordinate>();
		name = trackName;
	}

	public void addCoordinate(Coordinate coordinate) {
		coordinates.add(coordinate);
	}

	public String getName() {
		return name;
	}

	public int size() {
		return coordinates.size();
	}

	@Override
	public Iterator<Coordinate> iterator() {
		return coordinates.iterator();
	}
}