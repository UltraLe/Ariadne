package it.icarramba.ariadne.entities;

public class BootstrapRequest {

    private String reqtype;
    private String lat;
    private String lon;
    private int numFogNodes;

    public BootstrapRequest(String reqType, String lat, String lon, int numFogNodes){
        this.lat = lat;
        this.lon = lon;
        this.reqtype = reqType;
        this.numFogNodes = numFogNodes;
    }

    public int getNumFogNodes() {
        return numFogNodes;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getReqType() {
        return reqtype;
    }
}
