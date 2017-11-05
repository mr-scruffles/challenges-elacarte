import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Event class is responsible for managing the state of a given event.
 */
public class Event {
    public Boolean DEBUG = false;

    private HashMap<EventType, Event> session;
    private EventType type;
    private Timer timer;

    /**
     * Event constructor.
     * @param type The event type.
     * @param session The session for events.
     * @param intervalSeconds The interval in seconds for the timer action.
     */
    public Event(EventType type, HashMap<EventType, Event> session, int intervalSeconds ) {
        this.session = session;
        this.type = type;
        createTimer(intervalSeconds);
    }

    /**
     * Creates a new event timer and task to be executed at the specified interval.
     * @param intervalSeconds - The timer interval in seconds.
     */
    private void createTimer(int intervalSeconds) {
        if(intervalSeconds > 0) {
            this.timer = new Timer(true);
            this.timer.schedule(new EventTask(), intervalSeconds * 1000);
        }
    }

    /**
     * Will reset the timer for an event to given interval.
     * @param intervalSeconds - The timer interval in seconds.
     */
    public void setTimer(int intervalSeconds) {
        if(intervalSeconds > 0) {
            stop();
            createTimer(intervalSeconds);
        }
    }

    /**
     * Will stop the events timer and purge any scheduled events from the timer queue.
     */
    public void stop() {
        if(this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }
        this.timer = null;
    }

    /**
     * Gets event type.
     * @return The event type.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Checks if event timer is active.
     * @return True|active, otherwise false|inactive
     */
    public Boolean isEventTimerActive() {
        return this.timer != null;
    }

    /**
     * This class is responsible for defining that action that will be taken
     * by the time once its interval is reached.
     */
    private class EventTask extends TimerTask {
        @Override
        public void run() {
            session.remove(type);
            if(DEBUG) System.out.println("[REMOVE]\tEvent: " + type + " | event count: " + session.size() + " | end time:\t\t" + new Date());
            stop();
        }
    }
}
