import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for SessionController.
 */
class SessionControllerTest {

    private SessionController controller;

    @BeforeEach
    void setUp() {
        this.controller = new SessionController();
    }

    @AfterEach
    void tearDown() {
        this.controller.stopEventMonitor();
    }

    /**
     * This test validates that session monitor that monitors all events
     * is active once started.
     */
    @Test
    void startEventMonitor() {
        this.controller.startEventMonitor();
        Assertions.assertTrue(this.controller.isEvenMonitorActive());
    }

    /**
     * This test validates that session monitor that monitors all events
     * is inactive once stopped.
     */
    @Test
    void stopEventMonitor() {
        Assertions.assertFalse(this.controller.isEvenMonitorActive());
        this.controller.startEventMonitor();
        this.controller.stopEventMonitor();
        Assertions.assertFalse(this.controller.isEvenMonitorActive());
    }

    /**
     * This test validates that events can be added.
     */
    @Test
    void addEvent() {
        Assertions.assertFalse(this.controller.hasSession());
        this.controller.addEvent(EventType.NONE);
        Assertions.assertEquals(0, this.controller.getEventCount());
        this.controller.addEvent(EventType.TOUCH);
        Assertions.assertEquals(1, this.controller.getEventCount());
    }

    /**
     * This test validates that duplicate events are not being created.
     */
    @Test
    void addEventNoDuplicateEvents() {
        this.controller.addEvent(EventType.TOUCH);
        this.controller.addEvent(EventType.TOUCH);
        this.controller.addEvent(EventType.TOUCH);
        Assertions.assertEquals(1, this.controller.getEventCount());
    }

    /**
     * This test validates that if events exists then there is an
     * open session.
     */
    @Test
    void hasSession() {
        Assertions.assertFalse(this.controller.hasSession());
        this.controller.addEvent(EventType.SWIPE);
        Assertions.assertTrue(this.controller.hasSession());
        this.controller.stopEventMonitor();
        Assertions.assertFalse(this.controller.hasSession());
    }
    /**
        This test validates a single session open and close with events.

        Session started at  14:18:42
        [ADD]		Event: SWIPE | event count:	1 | start time:		 14:18:42
        [ADD]		Event: TOUCH | event count:	2 | start time:		 14:18:47
        [UPDATED]	Event: TOUCH | event count:	2 | updated time:	 14:18:48
        [REMOVE]	Event: SWIPE | event count: 1 | end time:		 14:18:52
        [REMOVE]	Event: TOUCH | event count: 0 | end time:		 14:18:53
        [STATUS]	Session 	 | event count: 0 | current time:	 14:18:57
        Session closed:  14:18:57
    */
    @Test
    void simpleFlowSingleSession() {
        this.controller.DEBUG = true;
        try {
            // Session start
            this.controller.addEvent(EventType.SWIPE); // host swipe 10s
            Thread.sleep(5000);
            this.controller.addEvent(EventType.TOUCH); // user touch 5s
            Assertions.assertEquals(2, this.controller.getEventCount());
            Thread.sleep(1000);
            this.controller.addEvent(EventType.TOUCH); // user touch 5s
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Assertions.assertTrue(false, e.toString());
        }
        Assertions.assertFalse(this.controller.hasSession());
    }

    /**
        This test will test the case where multiple sessions can occur. Session
        Controller started. The events in the first session close during the
        time the monitor checks the status of the sessions and then begins a
        new session when another event is detected.

        Session started at 13:51:43
        [ADD]		Event: SWIPE | event count:	1 | start time:		13:51:43
        [ADD]		Event: TOUCH | event count:	2 | start time:		13:51:48
        [UPDATED]	Event: TOUCH | event count:	2 | updated time:	13:51:49
        [REMOVE]	Event: SWIPE | event count: 1 | end time:		13:51:53
        [REMOVE]	Event: TOUCH | event count: 0 | end time:		13:51:54
        [STATUS]	Session 	 | event count: 0 | current time:	13:51:58
        Session closed:                                             13:51:58
        Session started at                                          13:51:59
        [ADD]		Event: SWIPE | event count:	1 | start time:		13:51:59
        [ADD]		Event: TOUCH | event count:	2 | start time:		13:52:00
        [ADD]		Event: CHECK | event count:	3 | start time:		13:52:01
        [UPDATED]	Event: TOUCH | event count:	3 | updated time:	13:52:02
        [UPDATED]	Event: CHECK | event count:	2 | updated time:	13:52:02
        [REMOVE]	Event: TOUCH | event count: 1 | end time:		13:52:07
        [REMOVE]	Event: SWIPE | event count: 0 | end time:		13:52:09
        [STATUS]	Session 	 | event count: 0 | current time:	13:52:14
        Session closed:                                             13:52:14
     */
    @Test
    void simpleFlowMultipleSessions() {
        this.controller.DEBUG = true;
        try {
            // Session start
            this.controller.addEvent(EventType.SWIPE); // host swipe 10s
            Thread.sleep(5000);
            this.controller.addEvent(EventType.TOUCH); // user touch 5s
            Thread.sleep(1000);
            this.controller.addEvent(EventType.TOUCH); // user touch 5s
            Assertions.assertEquals(2, this.controller.getEventCount());
            Thread.sleep(10000);
            Assertions.assertFalse(this.controller.hasSession());

            // Session start
            this.controller.addEvent(EventType.SWIPE); // server swipe
            Thread.sleep(1000);
            this.controller.addEvent(EventType.TOUCH); // server order
            Thread.sleep(1000);
            this.controller.addEvent(EventType.CHECK); // order sent to pos
            Thread.sleep(1000);
            this.controller.addEvent(EventType.TOUCH); // user touch
            Assertions.assertEquals(3, this.controller.getEventCount());
            this.controller.addEvent(EventType.CHECK); // check close
            Thread.sleep(15000); // Wait duration of status monitor
        } catch (InterruptedException e) {
            Assertions.assertTrue(false, e.toString());
        }
        Assertions.assertFalse(this.controller.hasSession());
    }

    /**
     * This test validates that the session does not close if there is not
     * a check close event or that there are existing events.
     */
    @Test
    void simpleFlowNoCheckCloseEvent() {
        this.controller.DEBUG = true;
        try {
            this.controller.addEvent(EventType.SWIPE);
            Thread.sleep(5000);
            this.controller.addEvent(EventType.TOUCH);
            Thread.sleep(1000);
            this.controller.addEvent(EventType.TOUCH);
            Thread.sleep(1000);
            this.controller.addEvent(EventType.SWIPE);
            Thread.sleep(1000);
            this.controller.addEvent(EventType.TOUCH);
            Thread.sleep(1000);
            this.controller.addEvent(EventType.CHECK);
            Thread.sleep(1000);
            this.controller.addEvent(EventType.TOUCH);
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Assertions.assertTrue(false, e.toString());
        }
        Assertions.assertTrue(this.controller.hasSession());
    }
}