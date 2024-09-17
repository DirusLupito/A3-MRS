package mkbd.kbd;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.AWTException;

/**
 * This class is used to simulate keyboard presses.
 * 
 * @author ABM
 */
public class KeyboardPresser {

    /** 5 seconds in terms of milliseconds */
    public static final int FIVE_SECONDS = 5000;

    /** Half second in terms of milliseconds */
    public static final int HALF_SECOND = 500;

    /**
     * Simulates an arbitrary key tap
     * 
     * @param k the key to press
     */
    public void tapKey(int k) {
        try {
            Robot r = new Robot();
            r.keyPress(k);
            r.keyRelease(k); 
        } catch (AWTException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Simulates an arbitrary key tap while shift is being held
     * 
     * @param k the key to press
     */
    public void tapKeyShift(int k) {
        try {
            Robot r = new Robot();
            r.keyPress(KeyEvent.VK_SHIFT);
            r.keyPress(k);
            r.keyRelease(k); 
            r.keyRelease(KeyEvent.VK_SHIFT);
        } catch (AWTException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Makes the program sleep for a given amount of time
     * 
     * @param time the amount of time to sleep
     */
    public void sleep(int time) {
        try {
            Robot r = new Robot();
            r.delay(time);
        } catch (AWTException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
