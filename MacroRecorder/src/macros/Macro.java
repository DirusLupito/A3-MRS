package macros;

import mkbd.kbd.KeyboardPresser;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines an object which represents a macro
 * Has one field which is a string interpretation of the macro
 * Uses the fields from KeyboardPresser to iterate through the macro string and interpret each character as a keyboard input
 * 
 * @author ABM
 *
 */
public class Macro {
	
	/** Difference between capital letters and their lower case equivalents in ASCII */
	public static final int ASCII_CAPITAL_DIFFERENCE = 32;
	/** Character that represents holding shift down */
	public static final char SHIFT_ENABLER = '<';
	/** Character that represents releasing shift */
	public static final char SHIFT_DISABLER = '>';
    /** Character that marks the start of a special button sequence */
    public static final char SPECIAL_MARKER = '\\';
	/** ASCII value of 0 */
	public static final char ASCII_0 = 48;
	/** ASCII value of 9 */
	public static final char ASCII_9 = 57;
	/** ASCII value of a */
	public static final char ASCII_LOWERCASE_A = 97;
	/** ASCII value of z */
	public static final char ASCII_LOWERCASE_Z = 122;
	/** Holds info related to the actual result */
	private String macroString;
	/** Name of a particular macro */
	private String macroName;
    /** Maps characters to their corresponding special button */
    private Map<Character, Integer> specialCharMap;
	
	/**
	 * Constructor for macro object
	 * @param macroString String representation of the macro 
	 * @param macroName name of the macro
	 * 
	 * @throws IllegalArgumentException if macroString is null or empty
	 */
	public Macro(String macroName, String macroString) {
		setMacroName(macroName);
        //Create the special character map
        initSpecialCharMap();
        //Check that the macroString is valid
		setMacroString(macroString);
	}

    /**
     * Initializes the special character map
     */
    private void initSpecialCharMap() {
        specialCharMap = new HashMap<Character, Integer>();
        specialCharMap.put('W', KeyEvent.VK_WINDOWS);
        specialCharMap.put('E', KeyEvent.VK_ENTER);
		specialCharMap.put('\\', KeyEvent.VK_BACK_SLASH);
    }
	
	/**
	 * Sets the name of the macro
	 * 
	 * @param macroName name of the macro to set
	 * @throws IllegalArgumentException if the the string is null or empty
	 */
	private void setMacroName(String macroName) {
		if ("".equals(macroName) || macroName == null) {
			throw new IllegalArgumentException("Invalid name.");
		}
		this.macroName = macroName;
		
	}

	/**
	 * Sets the macroString field
	 * 
	 * @param macroString String representation of the macro 
	 * 
	 * @throws IllegalArgumentException if macroString is null or empty
	 */
	private void setMacroString(String macroString) {
		if ("".equals(macroString) || macroString == null) {
			throw new IllegalArgumentException("Empty Macro String.");
		}
		checkMacroString(macroString);
		this.macroString = macroString;
	}
	
	/**
	 * Gets the macroString
	 * @return the macro string
	 */
	public String getMacroString() {
		return macroString;
	}
	
	/**
	 * Gets the name of the macro
	 * @return the macro's name
	 */
	public String getMacroName() {
		return macroName;
	}
	
	
	/**
	 * Gets the string representation of the macro
	 * @return the macroString
	 */
	public String toString() {
		return macroName + "," + macroString;
	}

    /**
     * Tells if a character is an allowed character
     * @param c character to check
     * @return whether or not the character is a valid macro character
     */
    public boolean isValidMacroChar(char c) {
        return c <= ASCII_9 && c >= ASCII_0 
            || c <= ASCII_LOWERCASE_Z && c >= ASCII_LOWERCASE_A 
            || c == SHIFT_ENABLER 
            || c == SHIFT_DISABLER
            || c == ' ';
    }

    /**
     * Tells if a backslashed character refers to a valid special button
     * @param c character to check
     * @return whether or not the character refers to a valid special button
     */
    public boolean isValidSpecialChar(char c) {
        return specialCharMap.containsKey(c);
    }
	
	/**
	 * Checks if a potential macroString is valid
	 * 
	 * @param macroString the macroString to check
	 * 
	 * @throws IllegalArgumentException if the macroString is invalid
	 */
	private void checkMacroString(String macroString) {
        for (int i = 0; i < macroString.length(); i++) {
            //See if this is a valid special button
            if (macroString.charAt(i) == SPECIAL_MARKER) {
                i++;
                if (i >= macroString.length() || !isValidSpecialChar(macroString.charAt(i))) {
                    throw new IllegalArgumentException("Character at position " + (i - 1) + " is an invalid special character.");
                }
                //Check if the character is valid or if it is a sleep command
            } else if (!isValidMacroChar(macroString.charAt(i))) {
                if (macroString.charAt(i) == '$') {
                    //TODO remove tooLong if code is safe
                    String sleepString = "";
                    i++;
                    Boolean tooLong = false;
                    while (macroString.charAt(i) != '$' && !tooLong) {
                        sleepString += macroString.charAt(i);
                        i++;
                        if (i >= 10000) {
                            tooLong = true;
                        }
                    }
                    try {
                        Integer.parseInt(sleepString);
                    } catch (NumberFormatException nfe) {
                        throw new IllegalArgumentException("Sleep amount near character " + (i + 1) + " is invalid.");
                    }
                } else {
                    throw new IllegalArgumentException("Character at position " + (i + 1) + " is unsupported.");
                }
            }
        }
	}
	
	/**
	 * Interprets the macro string, and executes the instructions within it
	 */
	public void interpretMacro() {
		MacroThread interpreter = new MacroThread();
		interpreter.start();
	}
	
	/**
	 * Inner class which allows for concurrent running of macros with other
	 * parts of program code via executing the macro on it's own thread
	 */
	private class MacroThread extends Thread {
		
		/**
		 * Runs the macro.
		 */
		public void run() {
            KeyboardPresser kp = new KeyboardPresser();
            boolean shiftEnabled = false;
            for (int i = 0; i < macroString.length(); i++) {
                    if(macroString.charAt(i) == '$') {
                        //TODO remove tooLong if code is safe to run
                        String sleepString = "";
                        i++;	
                        Boolean tooLong = false;
                        while (macroString.charAt(i) != '$' && !tooLong) {
                            sleepString += macroString.charAt(i);
                            i++;
                            if (i >= 10000) {
                                tooLong = true;
                            }
                        }
                        try {
                            int sleepTime = Integer.parseInt(sleepString);
                            kp.sleep(sleepTime);
                        } catch (NumberFormatException nfe) {
                            throw new IllegalArgumentException("Sleep amount near character " + (i + 1) + " is invalid.");
                        }
                } else if (macroString.charAt(i) == SPECIAL_MARKER && !shiftEnabled) {
                    i++;
                    kp.tapKey(specialCharMap.get(macroString.charAt(i)));
                } else if (macroString.charAt(i) == SPECIAL_MARKER) {
                    i++;
                    kp.tapKeyShift(specialCharMap.get(macroString.charAt(i)));
                } else if (macroString.charAt(i) == SHIFT_ENABLER) {
                    shiftEnabled = true;
                } else if (macroString.charAt(i) == SHIFT_DISABLER) {
                    shiftEnabled = false;
                } else if (macroString.charAt(i) == ' ') {
                    kp.tapKey(KeyEvent.VK_SPACE);
                } else if(!shiftEnabled && macroString.charAt(i) <= ASCII_LOWERCASE_Z && macroString.charAt(i) >= ASCII_LOWERCASE_A) {
                    kp.tapKey((int) macroString.charAt(i) - ASCII_CAPITAL_DIFFERENCE);
                } else if (!shiftEnabled && macroString.charAt(i) <= ASCII_9 && macroString.charAt(i) >= ASCII_0) {
                    kp.tapKey(macroString.charAt(i));
                } else if(shiftEnabled && macroString.charAt(i) <= ASCII_LOWERCASE_Z && macroString.charAt(i) >= ASCII_LOWERCASE_A) {
                    kp.tapKeyShift((int) macroString.charAt(i) - ASCII_CAPITAL_DIFFERENCE);
                } else if (shiftEnabled && macroString.charAt(i) <= ASCII_9 && macroString.charAt(i) >= ASCII_0) {
                    kp.tapKeyShift(macroString.charAt(i));
                } else {
                    throw new IllegalArgumentException("Character at position " + (i + 1) + " is unsupported.");
                }
            }
        }
	}

	/**
	 * Checks if two macros are equal by comparing their name
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Macro other = (Macro) obj;
		return other.getMacroName().equals(this.getMacroName());
	}

	/**
	 * Hash code for Macro object
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((macroName == null) ? 0 : macroName.hashCode());
		return result;
	}
	
}
