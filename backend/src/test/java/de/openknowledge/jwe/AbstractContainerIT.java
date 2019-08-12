package de.openknowledge.jwe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the abstract container class {@link AbstractContainer}.
 */

public class AbstractContainerIT extends AbstractContainer {

    @BeforeEach
    public void startContainer() {

        startApiContainer();
    }

    @Test
    public void testStartWithDatabase() {

        startDatabaseContainer();

        assertTrue(isApiContainerRunning());
        assertTrue(isDatabaseContainerRunning());
    }

    @Test
    public void testStartWithoutDatabase() {

        stopDatabaseContainer();

        assertTrue(isApiContainerRunning());
        assertFalse(isDatabaseContainerRunning());
    }

    @Test
    public void testConstructApiUrl() {

        String url = getApiUrl("test");

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            fail();
        }
    }

    @Test
    public void testConstructInvalidApiUrl() {

        String url = getApiUrl("test");
        String invalidUrl = ":" + url;

        assertThrows(MalformedURLException.class, () -> new URL(invalidUrl), "Not a valid URL");
    }

    @Test
    public void testStopContainersManually() {

        stopApiContainer();
        stopDatabaseContainer();

        assertFalse(isDatabaseContainerRunning());
        assertFalse(isApiContainerRunning());
    }
}
