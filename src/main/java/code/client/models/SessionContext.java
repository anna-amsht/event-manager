package code.client.models;

import code.api.dto.OrganizerDto;
import code.api.dto.ParticipantDto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SessionContext {
    private static OrganizerDto currentOrganizer;
    private static ParticipantDto currentParticipant;
    private static ObjectProperty<ParticipantDto> currentParticipantProperty =
            new SimpleObjectProperty<>();

    public static void setCurrentParticipant(ParticipantDto participant){
        currentParticipant = participant;
        currentParticipantProperty.set(participant);
    }

    public static ParticipantDto getCurrentParticipant(){
        return currentParticipant;
    }
    public static ObjectProperty<ParticipantDto> currentParticipantProperty() {
        return currentParticipantProperty;
    }

    public static void setCurrentOrganizer(OrganizerDto organizer) {
        currentOrganizer = organizer;
    }

    public static OrganizerDto getCurrentOrganizer() {
        return currentOrganizer;
    }

    public static void clear() {
        currentOrganizer = null;
    }
}