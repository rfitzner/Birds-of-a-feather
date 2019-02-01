package twoTypeERRG;

import java.io.File;

public class Starter {

	public static void main(String[] args) {
		forExample22();
	}

	/*
	 * fix a1=a2 and keep the expected degree constant
	 */
	public static void forExample21() {
		File[] files = Tools.InputOutputTools.openFiles();
		File datei = files[0];
		File dateilog = files[1];

		if (dateilog != null) {
			System.out.println("start for Example 2.1.");
			long time = System.currentTimeMillis();
			int nr = 10000;
			int nrSims = 100;
			double[] betas = new double[21];
			for (int i = 0; i < betas.length; i++)
				betas[i] = 1.0*i * 0.005;
			double[] expectedDegree = new double[21];
			for (int i = 0; i < expectedDegree.length; i++)
				expectedDegree[i] = (i+10) * 0.05;	

			for (int itbeta = 0; itbeta < betas.length; itbeta ++) {
				for (int itexDeg = 0; itexDeg < expectedDegree.length; itexDeg ++) {
					double a = 2 * expectedDegree[itexDeg] - betas[itbeta];
					if(a>=0){
					SimulationSet sim = new SimulationSet(a, a, 0.5, 0.5, betas[itbeta], nr, nrSims);
					sim.performSimulation(0);
					Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() + System.getProperty("line.separator"));
					if (System.currentTimeMillis() - time > 3600000) {
						System.out.println(Tools.InputOutputTools.getNiceTime() + " Bussy with example 2.1" + a + " : " + betas[itbeta]
								+ " : " + expectedDegree[itexDeg]);
						time = System.currentTimeMillis();
					}
					}
				}
			}
			System.out.println("Done.");
		}
	}
	
	/*
	 * fix E[D1] and  E[D2], with p=0.5
	 * also used for Example 2.3, with different part of the code uncommented..
	 */
	public static void forExample22() {
		File[] files = Tools.InputOutputTools.openFiles();
		File datei = files[0];
		File dateilog = files[1];

		if (dateilog != null) {
			
			//double[] expectedDegree1 = { 0.7, 0.8, 0.7, 0.8, 1.5, 1.5, 1.2, 1.2,1.5, 1.5, 1.2, 1.2};	
			//double[] expectedDegree2 = { 1.5, 1.5, 1.2, 1.2, 0.7, 0.8, 0.7, 0.8,1.5, 1.2, 1.5, 1.2 };
			
			double[] expectedDegree1 = { 0.5, 0.7 };	
			double[] expectedDegree2 = { 1.2, 1.1 };	
			
			for(int part = 0; part<expectedDegree2.length;part++){
				System.out.println("start for Example 2.2. part "+(part+1));
				long time = System.currentTimeMillis();
				int nr = 10000;
				int nrSims = 100;
				double[] betas = new double[101];
				for (int i = 0; i < betas.length; i++)
					betas[i] = 1.0*i * 0.05;
				
				for (int itbeta = 0; itbeta < betas.length; itbeta ++) {
					double a1 = 2 * expectedDegree1[part] - betas[itbeta];
					double a2 = 2 * expectedDegree2[part] - betas[itbeta];
					if((a1>=0)&&(a2>=0)){
						SimulationSet sim = new SimulationSet(a1, a2, 0.5, 0.5, betas[itbeta], nr, nrSims);
						sim.performSimulation(0);
						Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() + System.getProperty("line.separator"));
						if (System.currentTimeMillis() - time > 1800000) {
							System.out.println(Tools.InputOutputTools.getNiceTime() + " Bussy with example part "+part+" of extra ER " + expectedDegree1[part] + ":" + expectedDegree2[part] + ":" + betas[itbeta]);
							time = System.currentTimeMillis();
						}
					}
				}
				System.out.println("Done part "+(part+1)+" " +Tools.InputOutputTools.getNiceTime() );
				}
		}
	}

	

	public static void forExample24() {
		File[] files = Tools.InputOutputTools.openFiles();
		File datei = files[0];
		File dateilog = files[1];

		if (dateilog != null) {
			System.out.println("start for Example 2.3.");
			long time = System.currentTimeMillis();
			int nr = 10000;
			int nrSims = 100;
			double[] betas = new double[21];
			for (int i = 0; i < betas.length; i++)
				betas[i] = 1.0*i * 0.05;
			double expectedDegree1 = 0.5;
			double expectedDegree2 = 1.2;
			for (int itbeta = 0; itbeta < betas.length; itbeta ++) {
				double a1 = (expectedDegree1 - 0.1*betas[itbeta])/0.9;
				double a2 = (expectedDegree2 - 0.9*betas[itbeta])/0.1;
				if((a1>=0)&&(a2>=0)){
					SimulationSet sim = new SimulationSet(a1, a2, 0.9, 0.1, betas[itbeta], nr, nrSims);
					sim.performSimulation(0);
					Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() + System.getProperty("line.separator"));
					if (System.currentTimeMillis() - time > 3600000) {
						Tools.InputOutputTools.writeToFile(dateilog,
								Tools.InputOutputTools.getNiceTime() + " Bussy with example 2.3a " + a1 + ":" + a2 + ":" + betas[itbeta]);
						time = System.currentTimeMillis();
					}
				}
			}
			System.out.println("Done.");
		}

		if (dateilog != null) {
			System.out.println("start for Example 2.3. second");
			long time = System.currentTimeMillis();
			int nr = 10000;
			int nrSims = 100;
			double[] betas = new double[21];
			for (int i = 0; i < betas.length; i++)
				betas[i] = 1.0*i * 0.05;
			double expectedDegree1 = 0.5;
			double expectedDegree2 = 1.2;
			for (int itbeta = 0; itbeta < betas.length; itbeta ++) {
				double a1 = (expectedDegree1 - 0.9*betas[itbeta])/0.1;
				double a2 = (expectedDegree2 - 0.1*betas[itbeta])/0.9;
				if((a1>=0)&&(a2>=0)){
					SimulationSet sim = new SimulationSet(a1, a2, 0.1, 0.9, betas[itbeta], nr, nrSims);
					sim.performSimulation(0);
					Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() + System.getProperty("line.separator"));
					if (System.currentTimeMillis() - time > 3600000) {
						Tools.InputOutputTools.writeToFile(dateilog,
								Tools.InputOutputTools.getNiceTime() + " Bussy with example 2.3a " + a1 + ":" + a2 + ":" + betas[itbeta]);
						time = System.currentTimeMillis();
					}
				}
			}
			System.out.println("Done.");
		}

	}

	public static void forExample25() {
		File[] files = Tools.InputOutputTools.openFiles();
		File datei = files[0];
		File dateilog = files[1];

		if (dateilog != null) {
			System.out.println("start for Example 2.5.");
			long time = System.currentTimeMillis();
			int nr = 10000;
			int nrSims = 100;
			double expectedDegree1 = 1.2;
			double[] expectedDegree2 = {1.1,0.3};
			double beta=0.5;
			
			double[] ps = new double[9];
			for (int i = 0; i < ps.length; i++)
				ps[i] = 1.0*(i) * 0.1;

			for (int itDeg2 = 0; itDeg2 < expectedDegree2.length; itDeg2 ++) {
				for (int itPs = 0; itPs < ps.length; itPs ++) {
					double a1 = (expectedDegree1 - (1-ps[itPs])*beta)/ps[itPs];
					double a2 = (expectedDegree2[itDeg2] - ps[itPs]*beta)/(1-ps[itPs]);
					SimulationSet sim = new SimulationSet(a1, a2, ps[itPs], (1-ps[itPs]), beta, nr, nrSims);
					sim.performSimulation(0);
					Tools.InputOutputTools.writeToFile(datei, sim.savingFormat() + System.getProperty("line.separator"));
					if (System.currentTimeMillis() - time > 1800000) {
						System.out.println(
								Tools.InputOutputTools.getNiceTime() + " Bussy with example 2.5 " + ps[itPs] );
						time = System.currentTimeMillis();
				}
			}
			System.out.println("Done.");
		}
		}
	}



}
