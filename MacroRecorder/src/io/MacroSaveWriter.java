/**
 * 
 */
package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import macros.Macro;

/**
 * Writes a list of macros to a file.
 *  
 * @author ABM
 *
 */
public class MacroSaveWriter {
	
	/**
	 * Receives a File with the file name to write to and an ArrayList of Macro object to save to a file. 
	 * @param f file to write to
	 * @param macroList ArrayList of Macro objects being written to the file f
	 * @throws IllegalArgumentException with the message "Unable to save file." If there are any errors or exceptions,
	 */
	public static void writeMacroListToFile(File f, ArrayList<Macro> macroList) {
		try {
			PrintStream fileWriter = new PrintStream(f);
			for (int i = 0; i < macroList.size(); i++) {
				fileWriter.println(macroList.get(i).toString());
			}
			

			fileWriter.close();
		}
		catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Unable to save file.");
		}
	}
}
