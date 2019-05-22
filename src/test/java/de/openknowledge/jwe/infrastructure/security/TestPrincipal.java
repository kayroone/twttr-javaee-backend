package de.openknowledge.jwe.infrastructure.security;

import javax.security.auth.Subject;
import java.security.Principal;

public class TestPrincipal implements Principal {

    String username;

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
