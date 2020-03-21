package it.icarramba.ariadne.entities;

public class ItineraryMonument {

    private Monument Monument;
    private int Position;
    private String ExpectedArrTime;

    public ItineraryMonument(Monument monument, int position, String expectedArrTime){
        this.ExpectedArrTime = expectedArrTime;
        this.Position = position;
        this.Monument = monument;
    }

    public String getExpectedArrTime() {
        return ExpectedArrTime;
    }

    public int getPosition() {
        return Position;
    }

    public Monument getMonument() {
        return Monument;
    }
}
