package it.icarramba.ariadne.entities;

public class Itinerary {

    private int ID;
    private String Type;
    private String Departure;
    private String MeansOfTransp;
    private ItineraryMonument[] ItineraryMonuments;

    public Itinerary(int id, String type, String departure,
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

    public int getID() {
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

    public void setType(String type) {
        Type = type;
    }

    public void showInfo(){
        for (ItineraryMonument itiMon : ItineraryMonuments){
            System.out.println("Position: "+itiMon.getPosition());
            System.out.println("Monument Name: "+itiMon.getMonument().getName());
            System.out.println("Coordinates: "+itiMon.getMonument().getCoordinates());
            System.out.println("Expected Arrival Time: "+itiMon.getExpectedArrTime());
            System.out.println("Picture: todo later...");
        }
    }
}
