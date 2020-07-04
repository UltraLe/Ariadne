package it.icarramba.ariadne.entities;

public class Monument {

    private String Name;
    private String Coordinates;
    private String Picture;
    private String Description;

    public Monument(String name, String coordinates, String picture, String description){
        this.Coordinates = coordinates;
        this.Name = name;
        this.Picture = picture;
        this.Description = description;
    }

    public String getPicture() {
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
