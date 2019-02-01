package twoTypePreferentialAttachment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import Tools.MyDataFilter;


/**
 * A implementation that allows us to analyze the degree histogram produced by twoTypePreferentialAttachment.OneGraph. 
 * @author rfitzner
 *
 */
public class AnalyseOfDegrees {
	
	long nrNodes;
	// [0]- number of all vertices
	// [1]- Type 1
	// [2]- Type 2
	long[] nrVertices;

	// first source type, 0,1,2 second target type 0,1,2 and last is k=degree of sgiven type.
	int[][] maxdegree;
	double[][] averageFraction;
	double[][] averageDegree;
	long[][][] Nksequence;
	long[][][] inverseCDF;
	long total, totalNr1, totalNr2;

	public AnalyseOfDegrees() {
		nrNodes = 0;
		nrVertices = new long[3];
		maxdegree = new int[3][3];
		averageFraction = new double[3][3];
		averageDegree = new double[3][3];
		for (int i = 0; i < 3; i++) {
			nrVertices[i] = 0;
			for (int j = 0; j < 3; j++) {
				maxdegree[i][j] = 0;
				averageFraction[i][j] = 0;
				averageDegree[i][j] = 0;
			}
		}
		inverseCDF = new long[3][3][];
		Nksequence = new long[3][3][];
	}

	public static void main(String[] args) {
		File datei = selectFile();

		if (datei != null) {
			String name = datei.getAbsolutePath();

			for (int s = 0; s < 7; s++) {
				for (int t = 0; t < 10; t++) {
					String expID = "S" + s + "R" + t;
					String pattern = name.substring(0, name.indexOf(".") + 4) + expID + ".txt";
					File f = new File(pattern);
					if (f.exists() && !f.isDirectory()) {
						System.out.println(" File " + pattern + " exists.");
						String outputPattern = name.substring(0, name.indexOf(".")) + expID;

						perform(f, outputPattern);
						System.out.println("Done. " + expID);
					} else
						System.out.println(" File " + pattern + " does not exists.");

				}
			}
		} else {
			System.out.println("Illegal file.");
		}

	}

	public static void perform(File d, String outputPattern) {
		// preparing the data
		AnalyseOfDegrees base = new AnalyseOfDegrees();

		// loading the data and find data range
		try {
			BufferedReader br = new BufferedReader(new FileReader(d));
			String line;
			while ((line = br.readLine()) != null) {
				Node n = new Node(line);

				base.nrVertices[0] += n.count;
				base.nrVertices[n.type] += n.count;

				base.maxdegree[0][0] = Math.max((int) n.degree[0], base.maxdegree[0][0]);
				base.maxdegree[0][1] = Math.max((int) n.degree[1], base.maxdegree[0][1]);
				base.maxdegree[0][2] = Math.max((int) n.degree[2], base.maxdegree[0][2]);

				base.maxdegree[n.type][0] = Math.max((int) n.degree[0], base.maxdegree[n.type][0]);
				base.maxdegree[n.type][1] = Math.max((int) n.degree[1], base.maxdegree[n.type][1]);
				base.maxdegree[n.type][2] = Math.max((int) n.degree[2], base.maxdegree[n.type][2]);

				base.averageFraction[0][1] += n.count*n.degree[1] * 1.0 / n.degree[0];
				base.averageFraction[0][2] += n.count*n.degree[2] * 1.0 / n.degree[0];

				base.averageFraction[n.type][1] += n.count*n.degree[1] * 1.0 / n.degree[0];
				base.averageFraction[n.type][2] += n.count*n.degree[2] * 1.0 / n.degree[0];

				base.averageDegree[0][1] += n.count*n.degree[1];
				base.averageDegree[0][2] += n.count*n.degree[2];

				base.averageDegree[n.type][1] += n.count*n.degree[1];
				base.averageDegree[n.type][2] += n.count*n.degree[2];

			}
			br.close();
			// preparing the storage structure
			for (int s = 0; s < 3; s++) {
				for (int t = 0; t < 3; t++) {
					base.inverseCDF[s][t] = new long[base.maxdegree[s][t] + 1];
					base.Nksequence[s][t] = new long[base.maxdegree[s][t] + 1];
					for (int c = 0; c < base.maxdegree[s][t] + 1; c++) {
						base.inverseCDF[s][t][c] = 0;
						base.Nksequence[s][t][c] = 0;
					}
				}
			}
			// we load the file once more and actually process the data
			br = new BufferedReader(new FileReader(d));
			while ((line = br.readLine()) != null) {
				Node n = new Node(line);

				for(int i=0;i<3;i++){
					for(int c=0; c<n.degree[i]+1; c++)
					{
						base.inverseCDF[0][i][ c ] += n.count;
						base.inverseCDF[n.type][i][ c ] += n.count;
					}
					base.Nksequence[0][i][ n.degree[i] ] += n.count;
					base.Nksequence[n.type][i][ n.degree[i] ] += n.count;
				}
			}
			br.close();

			for (int s = 0; s < 3; s++) {
				for (int t = 1; t < 3; t++) {
					base.averageFraction[s][t] = base.averageFraction[s][t] / base.nrVertices[s];
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[][] headingAll = { { "AlltoAll", "AlltoOne", "AlltoTwo" },
									{ "OnetoAll", "OnetoOne", "OnetoTwo" },
									{ "TwotoAll", "TwotoOne", "TwotoTwo" } };
		try {
			for (int s = 0; s < 3; s++) {
				for (int t = 0; t < 3; t++) {
					File datei = new File(outputPattern + "CDF"+headingAll[s][t] + ".csv");
					BufferedWriter output = new BufferedWriter(new FileWriter(datei, true));
					output.append("nn, " + headingAll[s][t] + System.lineSeparator());
					long oldvalue=-1;
					for(int c=0;c<base.inverseCDF[s][t].length;c++){
						if (oldvalue != base.inverseCDF[s][t][c])
							output.append( c + ", " + (base.inverseCDF[s][t][c] * 1.0 / base.nrVertices[s]) + System.lineSeparator());
						oldvalue = base.inverseCDF[s][t][c];
					}
					output.close();
				}
			}
			{
				File datei = new File(outputPattern + "fractioninfro" + ".csv");
				BufferedWriter output = new BufferedWriter(new FileWriter(datei, true));
				output.append("Fraction All to 1 ," + base.averageFraction[0][1] + System.lineSeparator());
				output.append("Fraction All to 2 ," + base.averageFraction[0][2] + System.lineSeparator());
				output.append("Fraction 1 to 1 ," + base.averageFraction[1][1] + System.lineSeparator());
				output.append("Fraction 1 to 2 ," + base.averageFraction[1][2] + System.lineSeparator());
				output.append("Fraction 2 to 1 ," + base.averageFraction[2][1] + System.lineSeparator());
				output.append("Fraction 2 to 2 ," + base.averageFraction[2][2] + System.lineSeparator());
				
				output.append("Average All to 1 ," + (base.averageDegree[0][1]*1.0/base.nrVertices[0]) + System.lineSeparator());
				output.append("Average All to 2 ," + (base.averageDegree[0][2]*1.0/base.nrVertices[0] ) + System.lineSeparator());
				output.append("Average 1 to 1 ," + (base.averageDegree[1][1]*1.0/base.nrVertices[1]) + System.lineSeparator());
				output.append("Average 1 to 2 ," + (base.averageDegree[1][2]*1.0/base.nrVertices[1]) + System.lineSeparator());
				output.append("Average 2 to 1 ," + (base.averageDegree[2][1]*1.0/base.nrVertices[2]) + System.lineSeparator());
				output.append("Average 2 to 2 ," + (base.averageDegree[2][2]*1.0/base.nrVertices[2]) + System.lineSeparator());
				
				
				
				output.close();
			}
			
			for (int s = 0; s < 3; s++) {
				for (int t = 0; t < 3; t++) {
					File datei = new File(outputPattern +"NK"+ headingAll[s][t] + ".csv");
					BufferedWriter output = new BufferedWriter(new FileWriter(datei, true));
					output.append("nn, " + headingAll[s][t] + System.lineSeparator());
					for(int c=0;c<base.Nksequence[s][t].length;c++){
						if (0 != base.Nksequence[s][t][c])
							output.append( c + ", " + (base.Nksequence[s][t][c]) + System.lineSeparator());
					}
					output.close();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String listPrint(int[] list) {
		String s = "(";
		for (int i = 0; i < list.length - 1; i++) {
			s += list[i] + ",";
		}
		s += list[list.length - 1] + ")";
		return s;
	}

	public static String getNiceTime() {
		Runtime.getRuntime().gc();
		GregorianCalendar gcalendar = new GregorianCalendar();
		Calendar rightNow = Calendar.getInstance();
		return gcalendar.get(Calendar.DAY_OF_MONTH) + "th of "
				+ (new java.text.SimpleDateFormat("MMM")).format(rightNow.getTime()) + " at "
				+ gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":"
				+ gcalendar.get(Calendar.SECOND);
	}

	public static File selectFile() {
		JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setCurrentDirectory(new File("."));
		FileFilter filter = new MyDataFilter("txt");
		fc.setFileFilter(filter);
		File datei = null;

		int retVal = fc.showSaveDialog(new JFrame());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			datei = fc.getSelectedFile();
			if (MyDataFilter.getExtension(datei) == null) {
				String nameWithEnd = datei.getAbsolutePath() + ".txt";
				datei = new File(nameWithEnd);
			}
			if (!filter.accept(datei)) {
				System.out.println("You did not provide a proper file to lead.");
				datei = null;
			}
		} else {
			System.out.println("Files were not approved.");
		}
		return datei;
	}

	public static class Node {
		int type;
		int count;
		int[] degree;

		public Node(String s) {
			String[] result = s.split(":");
			this.type = new Integer(result[0]).intValue();
			this.count = new Integer(result[1]).intValue();
			this.degree = new int[3];
			this.degree[0] = new Integer(result[2]).intValue();
			this.degree[1] = new Integer(result[3]).intValue();
			this.degree[2] = new Integer(result[4]).intValue();

		}

		public Node(int t, int othert) {
			type = t;
			count = 1;
			degree = new int[3];
			degree[0] = 1;
			degree[1] = 0;
			degree[2] = 0;
			degree[othert]++;
		}

		public Node clone() {
			Node n = new Node(this.type, 1);
			n.count = this.count;
			n.degree[0] = this.degree[0];
			n.degree[1] = this.degree[1];
			n.degree[2] = this.degree[2];
			return n;
		}

		public String toString() {
			return this.type + "," + this.count + "," + this.degree[0] + "," + this.degree[1] + "," + this.degree[2];
		}

	}
}
