public class Main {
    public static void main(String[] args) {
        SessionController controller = new SessionController();

        if(args.length > 0 && 0 == args[0].compareTo("DEBUG")) {
            controller.DEBUG = true;
        }
        try {
            // Session start
            controller.addEvent(EventType.SWIPE);
            Thread.sleep(1000);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(1000);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(1000);
            controller.addEvent(EventType.SWIPE);
            Thread.sleep(500);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(500);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(500);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(500);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(500);
            controller.addEvent(EventType.CHECK);
            Thread.sleep(2000);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(500);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(500);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(500);
            controller.addEvent(EventType.CHECK);
            Thread.sleep(2000);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(1000);
            controller.addEvent(EventType.TOUCH);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
