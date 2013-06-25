package at.mm.trampletrack.backend;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import at.mm.trampletrack.dto.Coordinate;
import at.mm.trampletrack.dto.Track;

public class GPXImporter {
	private static GPXImporter instance = null;
//	private final String TRACK_TAG = "trk";
	private final String TRACK_NAME_TAG = "name";
//	private final String TRACK_SEG_TAG = "trkseg";
	private final String TRACK_POINT_TAG = "trkpt";
	private final String LAT_ATTR = "lat";
	private final String LONG_ATTR = "lon";

	private GPXImporter() {
		//
	}

	public static GPXImporter getInstance() {
		if (instance == null) {
			instance = new GPXImporter();
		}
		return instance;
	}

	public void parseImport(DBAdapter dba, File gpxfile) throws ParserConfigurationException, SAXException, FactoryConfigurationError, IOException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		GPXHandler gpxhandle = new GPXHandler(dba);
		parser.parse(gpxfile, gpxhandle);
	}

	class GPXHandler extends DefaultHandler {
		private DBAdapter dba;
		private boolean recordTrackName;
		private Track track;

		public GPXHandler(DBAdapter dba) {
			this.dba = dba;
		}

		@Override
		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			recordTrackName = localName.equals(TRACK_NAME_TAG);
			if (localName.equals(TRACK_POINT_TAG)) {
				double latCoord = Double.parseDouble(atts.getValue(LAT_ATTR));
				double longCoord = Double.parseDouble(atts.getValue(LONG_ATTR));
				track.addCoordinate(new Coordinate(latCoord, longCoord));
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (recordTrackName) {
				char[] newStr = new char[length];
				System.arraycopy(ch, start, newStr, 0, length);
				String trackName = new String(newStr);

				List<String> trackNames = dba.getAllTrackNames();
				if (trackNames.contains(trackName)) {
					fatalError(new SAXParseException(String.format("Track %s already imported!", trackName), null));
				}
				track = new Track(trackName);
				recordTrackName = false;
			}
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
			if (track != null) {
				dba.addTrack(track);
			}
		}
	}
}
