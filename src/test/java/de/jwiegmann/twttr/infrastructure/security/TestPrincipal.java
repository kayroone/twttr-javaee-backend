package de.jwiegmann.twttr.infrastructure.security;

import javax.security.auth.Subject;
import java.security.Principal;

public class TestPrincipal implements Principal {

    private final String username;

    public TestPrincipal(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
