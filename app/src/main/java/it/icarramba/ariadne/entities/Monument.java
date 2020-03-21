package it.icarramba.ariadne.entities;

public class Monument {

    private String Name;
    private String Coordinates;
    private byte[] Picture;
    private String Description;

    public Monument(String name, String coordinates, byte[] picture, String description){
        this.Coordinates = coordinates;
        this.Name = name;
        this.Picture = picture;
        this.Description = description;
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

    public String getDescription() {
        return Description;
    }
}
