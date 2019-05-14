package de.openknowledge.jwe.domain.model.role;

public enum UserRole {

    USER(Names.USER),
    MODERATOR(Names.MODERATOR),
    GUEST(Names.GUEST);

    public class Names{
        public static final String USER = "USER";
        public static final String MODERATOR = "MODERATOR";
        public static final String GUEST = "GUEST";
    }

    private final String label;

    private UserRole(String label) {
        this.label = label;
    }

    public String toString() {
        return this.label;
    }
}
