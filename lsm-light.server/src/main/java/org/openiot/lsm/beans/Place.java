package org.openiot.lsm.beans;

/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/
/**
 * 
 * @author Hoan Nguyen Mau Quoc
 *
 */
import java.util.Date;

import org.openiot.lsm.utils.ConstantsUtil;
import org.openiot.lsm.utils.DateUtil;

/**
 * 
 * @author Hoan Nguyen Mau Quoc
 * 
 */
public class Place implements java.io.Serializable{
	private String id;
	private String woeid = "";
	private String geonameid = "";
	private String zipcode = "";
	private String street = "";
	private String city = "";
	private String province = "";
	private String country = "";
	private String continent = "";
	private double lat;// not null
	private double lng;// not null
	private String infor = "no infor";
	private String author = "admin";// not null and not empty
	private Date times = new Date();// not null created time

	public Place() {
		super();
		id = "http://lsm.deri.ie/resource/"+System.nanoTime();
	}
	
	public Place(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	public String getId() {
		return id;
	}

	@SuppressWarnings("unused")
	public void setId(String id) {
		this.id = id;
	}


	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getWoeid() {
		return woeid;
	}

	public void setWoeid(String woeid) {
		this.woeid = woeid;
	}

	public String getGeonameid() {
		return geonameid;
	}

	public void setGeonameid(String geonameid) {
		this.geonameid = geonameid;
	}

	public String getStreet() {
		return street;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getTimes() {
		return times;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public void setTimes(Date times) {
		this.times = times;
	}
	

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
//		sb.append(geonameid == null ? "" : (geonameid.trim().equals("") ? "" : ("geonameid:" + geonameid + ", ")));
//		sb.append(woeid == null ? "" : (woeid.trim().equals("") ? "" : ("woeid:" + woeid + ", ")));
//		sb.append(zipcode == null ? "" :(zipcode.trim().equals("") ? "" : ("zipcode:" + zipcode + ", ")));
		sb.append(street == null ? "" : (street.trim().equals("") ? "" : (street + ", ")));
		sb.append(city == null ? "" : (city.trim().equals("") ? "" : (city + ", ")));
		sb.append(province == null ? "" : (province.trim().equals("") ? "" : (province + ", ")));
		sb.append(country == null ? "" : (country.trim().equals("") ? "" : (country + ", ")));
		sb.append(continent == null ? "" : (continent.trim().equals("") ? "" : (continent)));
//		sb.append(infor == null ? "" : (infor.trim().equals("") ? "" : ("information:" + infor + ", ")));
//		sb.append("lat:" + lat + ", ");
//		sb.append("lng:" + lng + ", ");		
//		sb.append("times:"+ DateUtil.date2StandardString(times) + ", ");
		
		return sb.toString();
	}
	
}
