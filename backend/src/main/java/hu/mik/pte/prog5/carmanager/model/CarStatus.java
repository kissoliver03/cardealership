package hu.mik.pte.prog5.carmanager.model;

public enum CarStatus {
    AVAILABLE("Szabad"),
    RENTED("Bérelve"),
    MAINTENANCE("Szervizben"),
    RESERVED("Foglalt");

    private final String displayName;

    CarStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
