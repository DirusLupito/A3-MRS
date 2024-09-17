package gui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import io.MacroSaveReader;
import io.MacroSaveWriter;
import macros.Macro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * This class is the GUI for the macro recorder program
 * 
 * @author ABM
 */
public class UI extends JFrame {

    /** Default serial version uid */
	private static final long serialVersionUID = 1L;
	/** Text for the File Menu. */
	private static final String FILE_MENU_TITLE = "File";
	/** Text for the Load menu item. */
	private static final String LOAD_TITLE = "Load Macro(s)";
	/** Text for the Save menu item. */
	private static final String SAVE_TITLE = "Save Macro(s)";
	/** Text for the Clear menu item. */
	private static final String CLEAR_TITLE = "Clear Macro(s)";
	/** Text for the Quit menu item. */
	private static final String QUIT_TITLE = "Quit";
	/** Arraylist of macros */
	private ArrayList<Macro> macroList;
	/** Currently active macro */
	private Macro currentMacro;
	/** Menu bar for the GUI that contains Menus. */
	private JMenuBar menuBar;
	/** Menu for the GUI. */
	private JMenu menu;
	/** Menu item for loading a macro file. */
	private JMenuItem itemLoad;
	/** Menu item for saving macros to a file. */
	private JMenuItem itemSave;
	/** Menu item for clearing system state. */
	private JMenuItem itemClear;
	/** Menu item for quitting the program. */
	private JMenuItem itemQuit;
	/** Label which identifies the current macro */
	private JLabel currentMacroLabel;
	/** List of all the macros currently loaded into the system */
	private JTextArea macroListTextArea;

    /**
     * The main method, constructs the gui
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        new UI();
    }

    /**
     * Constructs a UI
     */
    public UI() {
        super("Macro Recorder");
        macroList = new ArrayList<>();
        this.intializeGUI();
    }

    /**
     * Intializes the UI
    */
    public void intializeGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
		setUpMenuBar();

        Container c = getContentPane();
        JPanel panel1 = new JPanel();
        panel1.setBackground(Color.GRAY);
        JButton saveMacrobutton = new JButton("Save a new macro");
        currentMacroLabel = new JLabel("Current macro: No Current Macro.");
        JButton runMacrobutton = new JButton("Run the current macro");
		panel1.add(saveMacrobutton);
		panel1.add(currentMacroLabel);
		panel1.add(runMacrobutton);

        JScrollPane controlPanelScrollPane = new JScrollPane(panel1);
		c.add(controlPanelScrollPane, BorderLayout.NORTH);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
        panel2.setBackground(Color.DARK_GRAY);

        //Macro inputs
		JLabel inputLabel = new JLabel("Set the inputs of the macro");
		JTextField macroInputField = new JTextField(40);
		JPanel subPanel2North = new JPanel();
		subPanel2North.setBackground(Color.LIGHT_GRAY);
		subPanel2North.add(inputLabel);
		subPanel2North.add(macroInputField);

		//Macro name
		JLabel nameLabel = new JLabel("Set the name of the macro");
		JTextField macroNameField = new JTextField(20);
		JPanel subPanel2Center = new JPanel();
		subPanel2Center.setBackground(Color.LIGHT_GRAY);
		subPanel2Center.add(nameLabel);
		subPanel2Center.add(macroNameField);

		//List of macros
		JButton macroSwitchButton = new JButton("Switch the active macro to:");
		JTextField macroSwitchField = new JTextField(20);
		macroListTextArea = new JTextArea("List of macros: Empty");
		macroListTextArea.setEditable(false);
		JPanel subPanel2South = new JPanel();
		subPanel2South.setBackground(Color.GRAY);
		subPanel2South.add(macroSwitchButton);
		subPanel2South.add(macroSwitchField);
		subPanel2South.add(macroListTextArea);
		JScrollPane macroSwitchScrollPane = new JScrollPane(subPanel2South);
		
		panel2.add(subPanel2North, BorderLayout.NORTH);
		panel2.add(subPanel2Center, BorderLayout.CENTER);
		panel2.add(macroSwitchScrollPane, BorderLayout.SOUTH);

		c.add(panel2, BorderLayout.CENTER);

        saveMacrobutton.addActionListener(new ActionListener() {
			// This is the method that handles adding a new macro
        	//TODO 1 Remove possibility of adding duplicate macros (as in remove possibility of two macros sharing a single name)
        	//TODO 2 Move the macro list text generation into a new method, probably some new ArrayList analog called MacroList (would help with TODO 1)
			public void actionPerformed(ActionEvent a) {
				try {
					Macro m = new Macro(macroNameField.getText(), macroInputField.getText());
					macroList.add(m);
					currentMacro = m;
					currentMacroLabel.setText("Current macro: " + currentMacro.toString());
					generateMacroListTextAreaText();
					System.out.println(macroList.toString());
				} catch (IllegalArgumentException saveE) {
					JOptionPane.showMessageDialog(null, saveE.getMessage());
				}
				itemSave.setEnabled(macroList.size() > 0);
			}
		});

        runMacrobutton.addActionListener(new ActionListener() {
			// This is the method that handles running the currently active macro
			public void actionPerformed(ActionEvent a) {
				if (currentMacro != null) {
					System.out.println(currentMacro.toString());
					try {
						currentMacro.interpretMacro();
					} catch (IllegalArgumentException runE) {
						JOptionPane.showMessageDialog(null, runE.getMessage());
					}
				} else {
					JOptionPane.showMessageDialog(null, "There is no macro selected to run.");
				}
			}
		});

        macroSwitchButton.addActionListener(new ActionListener() {
			// This is the method that handles switching the active macro
			public void actionPerformed(ActionEvent a) {
				if (currentMacro != null && currentMacro.getMacroName().equals(macroSwitchField.getText())) {
					JOptionPane.showMessageDialog(null, "The current macro already matches the provided name.");
					return;
				}
				boolean match = false;
				for (int i = 0; i < macroList.size(); i++) {
					if (macroList.get(i).getMacroName().equals(macroSwitchField.getText())) {
						currentMacro = macroList.get(i);
						currentMacroLabel.setText("Current macro: " + currentMacro.toString());
						match = true;
						System.out.println(currentMacro.toString());
					}
				}
				if (!match) {
					JOptionPane.showMessageDialog(null, "There is no macro matching the provided name.");
				}
			}
		});

        this.setVisible(true);
        
    }

    /**
     * Creates the bar at the top responsible for file options.
     * This includes the functionality for loading and saving the list of macros.
     */
	private void setUpMenuBar() {
		//Construct Menu items
		menuBar = new JMenuBar();
		menu = new JMenu(FILE_MENU_TITLE);
		itemLoad = new JMenuItem(LOAD_TITLE);
		itemSave = new JMenuItem(SAVE_TITLE);
		itemClear = new JMenuItem(CLEAR_TITLE);
		itemQuit = new JMenuItem(QUIT_TITLE);
		
		//Start with save button disabled since there should be no macros to save initially
		itemSave.setEnabled(false);
		
		//Adds all the items to the menu
		menu.add(itemLoad);
		menu.add(itemSave);
		menu.add(itemClear);
		menu.add(itemQuit);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		
		itemLoad.addActionListener(new ActionListener() {
			//This is the method that handles loading a new list of macros from a file
			public void actionPerformed(ActionEvent a) {
				currentMacro = null;
		        currentMacroLabel.setText("Current macro: No Current Macro.");
		        macroList = MacroSaveReader.readMacroSaveFile(new File(getFileName(true)));
		        generateMacroListTextAreaText();
			}
		});
		
		itemSave.addActionListener(new ActionListener() {
			//This is the method that handles saving the current list of macros to a file
			public void actionPerformed(ActionEvent a) {
				try {
					MacroSaveWriter.writeMacroListToFile(new File(getFileName(false)), macroList);
				} catch (IllegalArgumentException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			}
		});
		
		itemClear.addActionListener(new ActionListener() {
			//This is the method that handles clearing the system
			public void actionPerformed(ActionEvent a) {
				macroList = new ArrayList<Macro>();
				currentMacro = null;
		        currentMacroLabel.setText("Current macro: No Current Macro.");
				macroListTextArea.setText("List of macros: Empty");
			}
		});
		
		itemQuit.addActionListener(new ActionListener() {
			//This is the method that handles quitting from the system
			public void actionPerformed(ActionEvent a) {
				System.exit(0);
			}
		});
	}
	
	/**
	 * Returns a file name generated through interactions with a JFileChooser
	 * object.
	 * 
	 * @author Dr. Sarah Heckman
	 * 
	 * @param load true if loading a file, false if saving
	 * @return the file name selected through JFileChooser
	 * @throws IllegalStateException if no file name provided
	 */
	private String getFileName(boolean load) {
		//Open JFileChooser to current working directory
		JFileChooser fc = new JFileChooser("./");  
		int returnVal = Integer.MIN_VALUE;
		if (load) {
			returnVal = fc.showOpenDialog(this);
		} else {
			returnVal = fc.showSaveDialog(this);
		}
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			//Error or user canceled, either way no file name.
			throw new IllegalStateException();
		}
		File gameFile = fc.getSelectedFile();
		return gameFile.getAbsolutePath();
	}
	
	/**
	 * Generates the text for the macro list text area
	 */
	public void generateMacroListTextAreaText() {
		String s = "";
		for (int i = 0; i < macroList.size(); i++) {
			s += "\n";
			s += macroList.get(i);
		}
		macroListTextArea.setText("List of macros" + s);
	}
}
