import java.util.*;

/**
 * This class is responsible for creating new events and monitoring their state for
 * when a session is open and closed.
 */
public class SessionController {

    public Boolean DEBUG = false;

    private HashMap<EventType, Event> session;
    private HashMap<EventType, Integer> eventTypeDuration;
    private Timer timer;

    private Boolean sessionStarted = false;

    public SessionController() {
        this.session = new HashMap<>();

        eventTypeDuration = new HashMap<>();
        eventTypeDuration.put(EventType.SWIPE, 10);
        eventTypeDuration.put(EventType.TOUCH, 5);
        eventTypeDuration.put(EventType.CHECK, 0); // CHECK events do not have intervals.
    }

    /**
     * This method will explicitly start the session controller timer to
     * monitor events for the session.
     */
    public void startEventMonitor() {
        if(DEBUG) System.out.println("Session Controller started");
        int intervalSeconds = 15;
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new SessionTask(), intervalSeconds * 1000, intervalSeconds * 1000);
    }

    /**
     * This method will explicitly stop the session controller timer
     * that monitors the events for the session and clean up all events
     * in the session.
     */
    public void stopEventMonitor() {

        List<Event> events = new ArrayList<>(this.session.values());

        for(Event event : events) {
            if(EventType.CHECK != event.getType()) event.stop();
            this.session.remove(event.getType());
        }

        if(this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }
        this.timer = null;
        if(DEBUG) System.out.println("Session Controller stopped");
    }

    /**
     * Checks if session monitor has an active timer.
     * @return true|active otherwise false|inactive.
     */
    public Boolean isEvenMonitorActive() {
        return this.timer != null;
    }

    /**
     * Adds a new event.
     * @param type The event type.
     */
    public void addEvent(EventType type) {

        if(!this.sessionStarted) {
            if(this.timer == null ) startEventMonitor();
            this.sessionStarted = true;
            System.out.println("Session started at " + new Date());
        }

        switch (type) {
            case SWIPE:
                handleEvent(type);
                break;
            case CHECK:
                handleEvent(type);
                break;
            case TOUCH:
                handleEvent(type);
                break;
            default:
                break;
        }
    }

    /**
     * Processes the event to be created.
     *
     * @param eventType The event type.
     */
    private void handleEvent(EventType eventType) {
        if(this.session.containsKey(eventType)) {
            Event event = this.session.get(eventType);
            if(DEBUG) event.DEBUG = true;
            if(EventType.CHECK == eventType) {
                // Close check event.
                this.session.remove(eventType);
            } else {
                event.setTimer(this.eventTypeDuration.get(eventType));
            }
            if(DEBUG) System.out.println("[UPDATED]\tEvent: " + eventType + " | event count:\t" + this.session.size() + " | updated time:\t" + new Date());
        } else {
            Event event = new Event(eventType, this.session, this.eventTypeDuration.get(eventType));
            if(DEBUG) event.DEBUG = true;
            this.session.put(eventType, event);
            if(DEBUG) System.out.println("[ADD]\t\tEvent: " + eventType + " | event count:\t" + this.session.size() + " | start time:\t\t" + new Date());
        }
    }

    /**
     * Checks if there are any active events, if so there is a session active.
     *
     * @return true|active otherwise false|inactive.
     */
    public Boolean hasSession() {
        return this.session.size() > 0;
    }

    /**
     * Returns the event count for a given session.
     *
     * @return Number of events.
     */
    public int getEventCount() {
        return this.session.size();
    }

    /**
     * This class is responsible for defining that action that will be taken
     * by the time once its interval is reached.
     */
    private class SessionTask extends TimerTask {
        @Override
        public void run() {
            if(DEBUG) System.out.println("[STATUS]\tSession \t | event count: " + session.size() + " | current time:\t" + new Date());
            if(!hasSession() && sessionStarted) {
                System.out.println("Session closed: " + new Date());
                sessionStarted = false;
                stopEventMonitor();
            }
        }
    }
}
