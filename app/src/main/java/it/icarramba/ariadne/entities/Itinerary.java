package it.icarramba.ariadne.entities;

public class Itinerary {

    private String ID;
    private String Type;
    private String Departure;
    private String MeansOfTransp;
    private ItineraryMonument[] ItineraryMonuments;

    public Itinerary(String id, String type, String departure,
                     String meansOfTransp, ItineraryMonument[] itinMon){
        this.ID = id;
        this.Type = type;
        this.Departure = departure;
        this.MeansOfTransp = meansOfTransp;
        this.ItineraryMonuments = itinMon;
    }

    public String getDeparture() {
        return Departure;
    }

    public String getID() {
        return ID;
    }

    public String getMeansOfTransp() {
        return MeansOfTransp;
    }

    public ItineraryMonument[] getItineraryMonuments() {
        return ItineraryMonuments;
    }

    public String getType() {
        return Type;
    }
}
