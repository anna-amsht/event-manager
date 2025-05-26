package code.javafx.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventModel {
    private Long id;
    private String title;
    private String description;
    private Integer numberOfSeats;
    private String dateTime;
    private String place;
    private String format;
    private Long organizerId;

    public EventModel(Long id, String title, Integer numberOfSeats,
                      String dateTime, String location, String format) {
        this.id = id;
        this.title = title;
        this.numberOfSeats = numberOfSeats;
        this.dateTime = dateTime;
        this.place = location;
        this.format = format;
    }

    public EventModel(Long id, String title, String description, Integer numberOfSeats,
                      String dateTime, String location, String format) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.numberOfSeats = numberOfSeats;
        this.dateTime = dateTime;
        this.place = location;
        this.format = format;
    }
}
