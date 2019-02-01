package twoTypeCM;

/**
 * We sample the graph according to the given parameters, for a given number of times.
 * We save the number of single nodes, number of disjoint, non-trivial, connected sub-graphs and the size of the largest clusters.
 * 
 * @author Robert Fitzner
 */
public class SimulationSetBothHeavyTail {
	double[] ps,xi,mus;
	int nrNodes, nrSimulations;
	int[] nrOfSingles;
	int[] nrOfConnectedComp;
	int[][] largestsizes;
	boolean valideParameters;

	
	public SimulationSetBothHeavyTail(int nr, double p1, double xi1,double xi2, double mu1, double mu2, int nrSimulations) {
		super();
		this.ps=new double[2];
		this.ps[0] = p1;
		this.ps[1] = 1-p1;
		this.xi=new double[2];
		this.xi[0] = xi1;
		this.xi[1] = xi2;
		
		this.mus=new double[2];
		this.mus[0] = mu1;
		this.mus[1] = mu2;
		valideParameters=true;
		if ((this.ps[0]>1)||(this.ps[1]>1)) valideParameters=false;
		if ((this.ps[0]<0)||(this.ps[1]<0)) valideParameters=false;
		if ((this.xi[0]>1)||(this.xi[1]>1)) valideParameters=false;
		if ((this.xi[0]<0)||(this.xi[1]<0)) valideParameters=false;
		if ((this.mus[0]<=1)||(this.mus[1]<=1)) valideParameters=false;
		this.nrNodes = nr;
		this.nrSimulations=nrSimulations;
	}
	
	
	public void performSimulation(){
		if ( valideParameters ){
			nrOfSingles	=	new int[this.nrSimulations];
			nrOfConnectedComp=	new int[this.nrSimulations];
			largestsizes=	new int[4][this.nrSimulations];
			for(int i =0; i<this.nrSimulations;i++){
				OneGraphBothHeavyTail model= new OneGraphBothHeavyTail(nrNodes, ps[0],xi[0],xi[1],mus[0],mus[1]);
				model.labeling();
	
				nrOfSingles[i]=model.nrOfSingles;
				nrOfConnectedComp[i]=model.nrOfCluster;
				for(int j=0;j<4;j++) largestsizes[j][i]=0;
				
				for(int j=0;j<Math.min(4, model.nrOfCluster);j++){
					largestsizes[j][i]=model.clustersizes[model.nrOfCluster-1-j];
				}
			}
		}
	}
	
	public String savingFormat(){
		if(valideParameters){
		StringBuffer sb=new StringBuffer();
		sb.append(ps[0]+":"+xi[0]+":"+xi[1]+":"+mus[0]+":"+mus[1]+":"+nrNodes+":"+nrSimulations);
		double[] r=Tools.Mathtool.roundedStaticts(nrOfSingles);
		sb.append(":"+r[0]+":"+r[1]);
		r=Tools.Mathtool.roundedStaticts(nrOfConnectedComp);
		sb.append(":"+r[0]+":"+r[1]);
		for(int i=0;i<4;i++){
			r=Tools.Mathtool.roundedStaticts(largestsizes[i]);
			sb.append(":"+r[0]+":"+r[1]);
		}
		return sb.toString();
		} else return "";
	}
	
}
