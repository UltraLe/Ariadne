package it.icarramba.ariadne.bean;

import it.icarramba.ariadne.entities.Itinerary;

//TODO check if used later(in DBTask), otherwise delete it
public class DBQueryBean {

    public static String INSERT = "INSERT";
    public static String GET = "GET";

    private Itinerary[] itineraries;
    private String query;
    //the type has to be non-null if query is GET
    private String type;

    public DBQueryBean(String query, Itinerary[] itineraries, String type){
        this.query = query;
        this.itineraries = itineraries;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Itinerary[] getItineraries() {
        return itineraries;
    }

    public String getQuery() {
        return query;
    }

    public void setItineraries(Itinerary[] itineraries) {
        this.itineraries = itineraries;
    }
}
