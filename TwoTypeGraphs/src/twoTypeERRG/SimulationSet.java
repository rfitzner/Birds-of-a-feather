package twoTypeERRG;

/**
 * We sample the graph according to the given parameters, for a given number of times.
 * We save the number of single nodes, number of disjoint, non-trivial, connected sub-graphs and the size of the largest clusters.
 * 
 * @author Robert Fitzner
 */
public class SimulationSet {
	double a1,a2,p1,p2,beta;
	int nrNodes, nrSimulations;
	int[] nrOfSingles;
	int[] nrOfConnectedComp;
	int[][] largestsizes;
	
	public SimulationSet(double a1, double a2, double p1, double p2, double beta, int nrNodes, int nrSimulations) {
		super();
		this.a1 = a1;
		this.a2 = a2;
		this.p1 = p1;
		this.p2 = p2;
		this.beta = beta;
		this.nrNodes = nrNodes;
		this.nrSimulations=nrSimulations;
	}
	
	public void performSimulation(int docdegree){
		nrOfSingles=new int[this.nrSimulations];
		nrOfConnectedComp=new int[this.nrSimulations];
		largestsizes=new int[4][this.nrSimulations];
		for(int i =0; i<this.nrSimulations;i++){
			OneGraph model= new OneGraph(nrNodes, a1, a2, p1, beta);
			model.labeling();
			
			nrOfSingles[i]=model.nrOfSingles;
			nrOfConnectedComp[i]=model.nrOfCluster;
			for(int j=0;j<4;j++) largestsizes[j][i]=0;
			
			for(int j=0;j<Math.min(4, model.nrOfCluster);j++){
				largestsizes[j][i]=model.clustersizes[model.nrOfCluster-1-j];
			}
		}
	}
	
	/**
	 * Produced the format used the store the information into a file.
	 * @return
	 */
	public String savingFormat(){
		StringBuffer sb=new StringBuffer();
		sb.append(Math.round(a1*1000)*1.0/1000+":"+Math.round(a2*1000)*1.0/1000+":"+Math.round(p1*1000)*1.0/1000+":"+Math.round(beta*1000)*1.0/1000+":"+nrNodes+":"+nrSimulations);
		double[] r=Tools.Mathtool.roundedStaticts(nrOfSingles);
		sb.append(":"+r[0]+":"+r[1]);
		r=Tools.Mathtool.roundedStaticts(nrOfConnectedComp);
		sb.append(":"+r[0]+":"+r[1]);
		for(int i=0;i<4;i++){
			r=Tools.Mathtool.roundedStaticts(largestsizes[i]);
			sb.append(":"+r[0]+":"+r[1]);
		}
		return sb.toString();
	}
	


}
