package mksm.ecco.model;

import android.util.Log;

/**
 * Created by mksm on 09.08.2016.
 */
public class EccoShop {

    private static final String TAG = "resource";

    private final String id;
    private final String name;
    private final String country;
    private final String region;
    private final String town;
    private final String address;
    private final String metro;
    private final String phone;
    private final String worktime;
    private final String type;
    private final String longtitude;
    private final String latitude;
    private final String isNew;
    private double fromCenter;

    public EccoShop(String id, String name, String country, String region, String town, String address, String metro, String phone, String worktime, String type, String longtitude, String latitude, String isNew) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.region = region;
        this.town = town;
        this.address = address;
        this.metro = metro;
        this.phone = phone;
        this.worktime = worktime;
        this.type = type;
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.isNew = isNew;
        try {
            double longF = Double.parseDouble(longtitude);
            double latF = Double.parseDouble(latitude);
            fromCenter = Math.sqrt(Math.pow(Math.abs(longF - EccoService.MOSCOW_LONGTITUDE), 2) + Math.pow(Math.abs(latF - EccoService.MOSCOW_LATITUDE), 2));
        } catch (NumberFormatException ex) {
            Log.e(TAG, "NFE: longtitude = " + longtitude + ", latitude = " + latitude);
            fromCenter = 10; // лушче в конец списка, чем в начало
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getTown() {
        return town;
    }

    public String getAddress() {
        return address;
    }

    public String getMetro() {
        return metro;
    }

    public String getPhone() {
        return phone;
    }

    public String getWorktime() {
        return worktime;
    }

    public String getType() {
        return type;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getIsNew() {
        return isNew;
    }

    public double getFromCenter() {
        return fromCenter;
    }
}
