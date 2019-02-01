package twoTypeCM;

import java.io.File;

public class Starter {

	public static void main(String[] args) {
		forExampleBothYS();
		forExamplePoiYS(true);
	}

	public static void forExampleBothYS() {
		File[] files = Tools.InputOutputTools.openFiles();
		File datei = files[0];

		if (datei != null) {
			System.out.println("start for Example YS,YS.");
			long time = System.currentTimeMillis();
			int nr = 10000;
			int nrSims = 100;
			double[] xi1 = new double[11];
			double[] mu1 = { 1.1, 1.2, 1.5, 1.9, 2, 2.5 };
			double[] mu2 = { 1.1, 1.2, 1.5, 1.9, 2, 2.5 };
			double p1= 0.3;
			for (int i = 0; i < xi1.length; i++)
				xi1[i] = 0.1 * i;
			for (int itxi1 = 0; itxi1 < xi1.length; itxi1++) {
				for (int itmu1 = 0; itmu1 < mu1.length; itmu1++) {
					for (int itmu2 = 0; itmu2 < mu2.length; itmu2++) {
						double x1 = xi1[itxi1];
						double m1 = mu1[itmu1];
						double m2 = mu2[itmu2];
						double x2 = 1 - p1/(1-p1)*m1 / m2 * (1 - x1);
						if (x1 == 1)
							x2 = 1;
						if ((x2 >= 0) && (x2 <= 1)) {
							SimulationSetBothHeavyTail sim = new SimulationSetBothHeavyTail(nr, p1, x1, x2, m1, m2,
									nrSims);
							if (sim.valideParameters) {
								sim.performSimulation();
								Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() + System.getProperty("line.separator"));
								if (System.currentTimeMillis() - time > 7200000) {
									System.out.println(Tools.InputOutputTools.getNiceTime() + " Bussy doing Example YS,YS " + itxi1);
									time = System.currentTimeMillis();
								}
							}
						}
					}
				}
				System.out.println("finished xi1 " + xi1[itxi1]);
			}
		}
	}

	public static void forExamplePoiYS(boolean reverse) {
		File[] files = Tools.InputOutputTools.openFiles();
		File datei = files[0];
		if (datei != null) {
			System.out.println("start for Example Poi, YS.  ");
			int nr = 10000;
			int nrSims = 100;
			double[] ps1 = new double[4];
			for (int i = 0; i < ps1.length; i++)
				ps1[i] = 0.1 * (i + 4);
			
			double[] xi1 = new double[11];
				for (int i = 0; i < xi1.length; i++)
					xi1[i] = 0.1 * (i + 0);
			double[] mu1 = { 0.5, 0.8, 1, 1.1, 1.2, 1.5, 1.9, 2, 2.5 };
			double[] mu2 = { 0.5,1.1, 0.8, 1, 1.2, 1.5,1.9, 2, 2.5 };
			//double[] mu2 = { , 1.9};

			for (int itps1 = 0; itps1 < ps1.length; itps1++) {
				for (int itxi1 = 0; itxi1 < xi1.length; itxi1++) {
					for (int itmu1 = 0; itmu1 < mu1.length; itmu1++) {
						for (int itmu2 = 0; itmu2 < mu2.length; itmu2++) {
							double p1 = ps1[itps1];
							double x1 = xi1[itxi1];
							double m1 = mu1[itmu1];
							double m2 = mu2[itmu2];
							double x2;
							if (p1 == 1) {
								x2 = 1;
							} else
								x2 = 1 - p1 / (1 - p1) * m1 / m2 * (1 - x1);
							if (!reverse) 
							{
								SimulationSetOneHeavyTail sim = new SimulationSetOneHeavyTail(nr, p1, x1, x2, m1, m2, nrSims);
								if (sim.valideParameters) {
									System.out.println("Simulating: (" + p1 + ", " + x1 + ", " + x2 + ", " + m1 + ", "
											+ m2 + ")  " + Tools.InputOutputTools.getNiceTime() + " (" + itps1 + "," + itxi1 + ", " + itmu1
											+ "," + itmu2 + ")");
									 sim.performSimulation();
									 Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() +
									 System.getProperty("line.separator"));
								}
							} else 
							{
								SimulationSetOneHeavyTail sim = new SimulationSetOneHeavyTail(nr, 1 - p1, x2, x1, m2, m1,
										nrSims);
								if (sim.valideParameters) {
									System.out.println("SimulatingSym: (" + (1 - p1) + ", " + x2 + ", " + x1 + ", " + m2
											+ ", " + m1 + ")  " + Tools.InputOutputTools.getNiceTime() + " (" + itps1 + "," + itxi1 + ", "
											+ itmu1 + "," + itmu2 + ")");
									sim.performSimulation();
									Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() + System.getProperty("line.separator"));
								}
							}
						}
					}
					System.out.println(Tools.InputOutputTools.getNiceTime() + " finished xi " + xi1[itxi1]);
				}
				System.out.println(Tools.InputOutputTools.getNiceTime() + " finished p1 " + ps1[itps1]);
				System.out.println();
				System.out.println();
			}
		}
		System.out.println("Done.");
	}
}
