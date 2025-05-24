package code.javafx.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EventModel {
    private final SimpleStringProperty title;
    private final SimpleIntegerProperty numberOfSeats;
    private final SimpleStringProperty dateTime;
    private final SimpleStringProperty location;
    private final SimpleStringProperty format;

    public EventModel(String title, int numberOfSeats, String dateTime, String location, String format) {
        this.title = new SimpleStringProperty(title);
        this.numberOfSeats = new SimpleIntegerProperty(numberOfSeats);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.location = new SimpleStringProperty(location);
        this.format = new SimpleStringProperty(format);
    }

    public String getTitle() { return title.get(); }
    public int getNumberOfSeats() { return numberOfSeats.get(); }
    public String getDateTime() { return dateTime.get(); }
    public String getLocation() { return location.get(); }
    public String getFormat() { return format.get(); }
}
