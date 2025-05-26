package code.javafx.models;

import code.store.entities.OrganizerEntity;
import code.store.entities.ParticipantEntity;

public class SessionContext {
    private static OrganizerEntity currentOrganizer;
    private static ParticipantEntity currentParticipant;

    public static void setCurrentParticipant(ParticipantEntity participant){
        currentParticipant = participant;
    }

    public static ParticipantEntity getCurrentParticipant(){
        return currentParticipant;
    }

    public static void setCurrentOrganizer(OrganizerEntity organizer) {
        currentOrganizer = organizer;
    }

    public static OrganizerEntity getCurrentOrganizer() {
        return currentOrganizer;
    }

    public static void clear() {
        currentOrganizer = null;
    }
}