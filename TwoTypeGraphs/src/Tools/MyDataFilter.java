package Tools;

/**
 * This filter is used when we save or load from a file. It 
 * garanties that the files have the wished file ending.
 */

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * We would like to open only files with specific file extension = type name.
 * Java uses of the FileFilter. This is a generic implementation of such a file filter.
 * @author Robert Fitzner.
 *
 */
public class MyDataFilter extends FileFilter {

	String wantedextension;

	public MyDataFilter(String extension) {
		super();
		wantedextension = extension;
	}
	
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (wantedextension != null) {
				if (extension.equalsIgnoreCase(wantedextension)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getDescription() {
		return "Desired file extension :" + wantedextension + ".";
	}

	public String getWantedExtension() {
		return this.wantedextension;
	}

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}
