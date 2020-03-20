package it.icarramba.ariadne.entities;

public class Monument {

    private String Name;
    private String Coordinates;
    private byte[] Picture;

    public Monument(String name, String coordinates, byte[] picture){
        this.Coordinates = coordinates;
        this.Name = name;
        this.Picture = picture;
    }

    public byte[] getPicture() {
        return Picture;
    }

    public String getName() {
        return Name;
    }

    public String getCoordinates() {
        return Coordinates;
    }
}
