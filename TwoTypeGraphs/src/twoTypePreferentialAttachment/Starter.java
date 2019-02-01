package twoTypePreferentialAttachment;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import Tools.MyDataFilter;

public class Starter {

	public static void main(String[] args) {
		//PrefTwoType m=PrefTwoType.factory(10000000,0.2,0.5,0.5);
		
		double[][] parameters=new double[7][];
		{
			double[] tmp={0.5,0.5,0.5}; // Homogenous
			parameters[0]=tmp;
		}
		{
			double[] tmp={0.2,0.2,0.2}; // Homogenous low
			parameters[1]=tmp;
		}
		{
			double[] tmp={0.1,0.8,0.2}; // Type one rare and everyone wants them
			parameters[2]=tmp;
		}
		{
			double[] tmp={0.5,0.2,0.2}; // Everyone whats to conntect to the other type
			parameters[3]=tmp;
		}
		{
			double[] tmp={0.5,0.8,0.2}; // Type one is common and everyone want them
			parameters[4]=tmp;
		}
		{
			double[] tmp={0.5,0.8,0.8}; // everyone prefers his kind
			parameters[5]=tmp;
		}
		
		{
			double[] tmp={0.5,0.7,0.3}; // Asymmetry
			parameters[6]=tmp;
		}
		
		
		
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
				System.out.println("You did not provide a proper file to save the result to.");
				datei = null;
			}
		} else {
			System.out.println("Files were not approved.");
		}

		if (datei != null) {
			for(int sim=2;sim<3;sim++){
				for(int run=0;run<1;run++){
					System.out.println("start for Example "+sim+" for "+run+ Tools.InputOutputTools.getNiceTime());
					String nameWithEnd = datei.getAbsolutePath()+"S" +sim+"R"+run+ ".txt";
					File file = new File(nameWithEnd);
					OneGraph m=OneGraph.factory(1000000000,parameters[sim][0],parameters[sim][1],parameters[sim][2]);
					m.printGraph();
					m.printDegrees(file);
				}
			}
		}
		else{ 
			System.out.println("Illegal file.");
		}
	}
	
	
}
