package it.icarramba.ariadne.entities;

public class ItineraryMonument {

    private Monument Monument;
    private String Position;
    private String ExpectedArrTime;

    public ItineraryMonument(Monument monument, String position, String expectedArrTime){
        this.ExpectedArrTime = expectedArrTime;
        this.Position = position;
        this.Monument = monument;
    }

    public String getExpectedArrTime() {
        return ExpectedArrTime;
    }

    public String getPosition() {
        return Position;
    }

    public it.icarramba.ariadne.entities.Monument getMonument() {
        return Monument;
    }
}
