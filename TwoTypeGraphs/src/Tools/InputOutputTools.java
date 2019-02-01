package Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
/**
 * 
 * @author Robert Fitzner
 * 
 */
public class InputOutputTools {

	/**
	 * Opens a FileChooser and return two files.
	 * One is the file in which the result will be written into.
	 * A second file is provided in case you would like to write the progress into a file.
	 * @return two files, if a file was successfully selected.
	 */
	public static File[] openFiles() {
		JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));
		FileFilter filter = new MyDataFilter("txt");
		fc.setFileFilter(filter);
		File datei = null;
		File dateilog = null;

		int retVal = fc.showSaveDialog(new JFrame());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			datei = fc.getSelectedFile();
			if (MyDataFilter.getExtension(datei) == null) {
				String nameWithEnd = datei.getAbsolutePath() + ".txt";
				String nameWithEndlog = datei.getAbsolutePath() + "log.txt";
				datei = new File(nameWithEnd);
				dateilog = new File(nameWithEndlog);
			}
			if (!filter.accept(datei)) {
				System.out.println("You did not provide a proper file to save the result to.");
				datei = null;
			}
		} else {
			System.out.println("Files were not approved.");
		}

		File[] returnvalue = new File[2];
		returnvalue[0] = datei;
		returnvalue[1] = dateilog;
		return returnvalue;
	}
	
	/**
	 * Write the given text into the provided file
	 * @param f
	 * @param text
	 */
	public static void writeToFile(File f, String text) {
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(f, true));
			output.append(text);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Provides a nice format of the time, that will be used for status/progress messages.
	 * @return
	 */
	public static String getNiceTime() {
		Runtime.getRuntime().gc();
		GregorianCalendar gcalendar = new GregorianCalendar();
		Calendar rightNow = Calendar.getInstance();
		return gcalendar.get(Calendar.DAY_OF_MONTH) + "th of "
				+ (new java.text.SimpleDateFormat("MMM")).format(rightNow.getTime()) + " at "
				+ gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":"
				+ gcalendar.get(Calendar.SECOND);
	}

}
