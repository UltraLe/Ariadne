package it.icarramba.ariadne.entities;

public class FogNode {

    private String state;
    private int distance;
    private String ip;
    private int beatPort;
    private String lat;
    private String lon;

    public FogNode(String lat, String lon, String ip, String state, int distance, int beatPort){
        this.state = state;
        this.lat = lat;
        this.lon = lon;
        this.beatPort = beatPort;
        this.ip = ip;
        this.distance = distance;
    }

    public String getLon() {
        return lon;
    }

    public String getLat() {
        return lat;
    }

    public int getBeatPort() {
        return beatPort;
    }

    public int getDistance() {
        return distance;
    }

    public String getIp() {
        return ip;
    }

    public String getState() {
        return state;
    }
}
