package org.openiot.lsm.reasoning.data;

import java.io.Serializable;

//@XmlRootElement
public class Coordinate implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * mean radius of the earth
	 */
	public final static double R = 6371000; // m
	/**
	 * Radius of the earth at equator
	 */
	public final static double R_EQ = 6378137; // m

	private double longitude;
	private double latitude;

	public Coordinate() {
		super();
	}

	public Coordinate(final double longitude, final double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Coordinate(final String stringCoordinate)
			throws CoordinateParseException {

		final String[] stringCoordicateTokens = stringCoordinate.split(" ");

		if (stringCoordicateTokens.length != 2) {
			throw new CoordinateParseException("The string " + stringCoordinate
					+ " does not have 2 parametes");
		} else {
			try {
				longitude = Double.parseDouble(stringCoordicateTokens[0]);
				latitude = Double.parseDouble(stringCoordicateTokens[1]);
			} catch (final NumberFormatException e) {
				throw new CoordinateParseException("The string "
						+ stringCoordinate + " cannot be parsed as Double");
			}
		}

	}

	public double distance(final Coordinate l) {
		final double toLat = l.getLatitude();
		final double toLon = l.getLongitude();
		final double fromLat = latitude;
		final double fromLon = longitude;
		final double sinDeltaLat = Math
				.sin(Math.toRadians(toLat - fromLat) / 2);
		final double sinDeltaLon = Math
				.sin(Math.toRadians(toLon - fromLon) / 2);
		final double normedDist = sinDeltaLat * sinDeltaLat + sinDeltaLon
				* sinDeltaLon * Math.cos(Math.toRadians(fromLat))
				* Math.cos(Math.toRadians(toLat));
		return Math.round(Coordinate.R_EQ * 2
				* Math.asin(Math.sqrt(normedDist)));
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(final double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return longitude + " " + latitude;
	}

	public Coordinate midPoint(Coordinate b) {
		return new Coordinate(
				(this.longitude + b.longitude) / 2,
				(this.latitude + b.latitude) / 2);
	}

}
