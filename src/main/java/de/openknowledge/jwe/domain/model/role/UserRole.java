package de.openknowledge.jwe.domain.model.role;

public enum UserRole {

    USER(Names.USER),
    MODERATOR(Names.MODERATOR);

    public class Names{
        public static final String USER = "User";
        public static final String MODERATOR = "Moderator";
    }

    private final String label;

    private UserRole(String label) {
        this.label = label;
    }

    public String toString() {
        return this.label;
    }
}
