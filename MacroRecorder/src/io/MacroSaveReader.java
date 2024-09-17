/**
 * 
 */
package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import macros.Macro;

/**
 * Reads a list of macros from a file.
 * @author ABM
 *
 */
public class MacroSaveReader {
	
	/**
	 * Receives a File with the file to read the macro list from. If the file cannot be loaded because it doesn’t exist, the method will 
	 * throw an IllegalArgumentException with the message "Unable to load file."
	 * Macro lists are stored as follows:
	 * NAME,INPUT then a newline
	 * Inputs are stored as letters 
	 * (Ex: the macro to press aab is just a .txt with aab)
	 * followed by delays denoted by dollar signs and numbers 
	 * (Ex: the macro to press a, then wait 50ms then press a again is a .txt with a$50$a)
	 * @param f file to read
	 * @return String containing the macro to be interpreted
	 * @throws IllegalArgumentException with the message "Unable to load file." If the file cannot be loaded because it doesn’t exist
	 */
	public static ArrayList<Macro> readMacroSaveFile(File f) {
		
		if (f == null) {
			throw new IllegalArgumentException("Unable to load file.");
		}
		
		ArrayList<Macro> mList = new ArrayList<>();
		try {
			Scanner fileReader = new Scanner(new FileInputStream(f));
			Scanner lineScanner;
			
			while (fileReader.hasNextLine()) {
				lineScanner = new Scanner(fileReader.nextLine());
				lineScanner.useDelimiter(",");
				try {
					String mName = lineScanner.next();
					String mInputs = lineScanner.next();
					Macro macro = new Macro(mName, mInputs);
					mList.add(macro);
				} catch (IllegalArgumentException e) {
					lineScanner.close();
					fileReader.close();
					throw new IllegalArgumentException(e.getMessage());
				}
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Unable to load file.");
		}
		
		return mList;
	}
}
