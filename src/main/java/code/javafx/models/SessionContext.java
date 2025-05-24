package code.javafx.models;

import code.store.entities.OrganizerEntity;

public class SessionContext {
    private static OrganizerEntity currentOrganizer;

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