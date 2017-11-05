import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Event test class.
 */
class EventTest {
    private HashMap<EventType, Event> session;
    private Event event;

    @BeforeEach
    void setUp() {
        this.session = new HashMap<>();
    }

    @AfterEach
    void tearDown() {
        List<Event> events = new ArrayList<>(this.session.values());

        for(Event event : events) {
            if(EventType.CHECK != event.getType()) event.stop();
            this.session.remove(event.getType());
        }
    }

    /**
     * Validates that an event timer is canceled.
     */
    @Test
    void stop() {
        this.event = new Event(EventType.TOUCH, this.session, 5);
        Assertions.assertTrue(event.isEventTimerActive());
        this.event.stop();
        Assertions.assertFalse(event.isEventTimerActive());
    }

    /**
     * Validates if event timer is active.
     */
    @Test
    void isEventTimerActiveCheckEvent() {
        this.event = new Event(EventType.CHECK, this.session, 0);
        Assertions.assertFalse(event.isEventTimerActive());
    }

    /**
     * Validates that event type is correct for event.
     */
    @Test
    void getType() {
        this.event = new Event(EventType.TOUCH, this.session, 5);
        Assertions.assertTrue(EventType.TOUCH == this.event.getType());
    }

}