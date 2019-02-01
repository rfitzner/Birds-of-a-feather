package twoTypePreferentialAttachment;

import java.io.File;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * This file creates an preferential attachment graph, in which the nodes have two types.
 * The implement model is basically a Barabasi–Albert model in which each new vertex decides on its own type,
 * and then to which type its want to connect.
 * Note that this implementation is optimized to simulate huge graphs (>10^7).
 * For will concentrate on the degree structure of the graph and will not save/store any other properties of the graph.
 * 
 * - p the probability that a now has type 1
 * - theta1,theta2 the probability that a new node decides to connected to the same type of node
 * @author Robert Fitzner
 */
public class OneGraph {
	// Parameters of the model
	double p;
	double[] theta;
	
	// we save the nodes in three batches.
	final int smallMaxDeg=500;
	/**
	 * The small Nodes, having low degree:
	 * smallNode[t-1][x][y] number of type t nodes with x edges leading to type 1 nodes and y edges leading to type 2 nodes.
	 */
	int[][][] smallNode;
	
	final int largeCapacity=10000;
	/**
	 * The largest node, with the highest degree. By the mechanism of the preferential attachment, these 
	 * are the nodes that we will most likely connect to,
	 */
	LinkedList<Node>[] largeNodes;	

	/**
	 * All the nodes that have either small degree or highest degree.
	 */
	LinkedList<Node>[] mediumNodes;

	
	long smallestLarge=0;

	
	long finalNrNodes,nrCurrentNodes;
	/**
	 * verticInformattron[0][xx] regarding all nodes
	 * verticInformattron[1][xx] regarding largeNodes
	 * verticInformattron[2][xx] regarding mediumNodes  
	 * verticInformattron[3][xx] regarding smallNodes
	 *
	 * verticInformattron[xx][t-1] number of type t nodes 
	 * verticInformattron[xx][t+1] total degree of type t nodes
	 */
	long[][] vertexInformation;
	
	/**
	 * constructor, creates the graph with 2 nodes
	 * @param nrFinal
	 * @param p1
	 * @param theta1
	 * @param theta2
	 */
	public OneGraph(int nrFinal, double p1, double theta1, double theta2) {
		// read parameters
		this.finalNrNodes=nrFinal;
		this.p= p1;
		this.theta=new double[2];
		this.theta[0]=theta1;
		this.theta[1]=theta2;
		this.nrCurrentNodes=0;
		
		vertexInformation=new long [4][4];
		for (int i=0; i<4;i++){
			for (int j=0; j<4;j++){
				vertexInformation[i][j]=0;
			}
		}
		
		largeNodes=new LinkedList[2];
		largeNodes[0]=new LinkedList<Node>();
		largeNodes[1]=new LinkedList<Node>();

		
		mediumNodes=new LinkedList[2];
		mediumNodes[0]=new LinkedList<Node>();
		mediumNodes[1]=new LinkedList<Node>();

		
		smallNode=new int[2][smallMaxDeg+1][];
		for (int t=0; t<2;t++){
			for (int i=0; i<smallMaxDeg+1;i++){
			smallNode[t][i]=new int[smallMaxDeg-i+1];
				for (int j=0; j<smallNode[t][i].length;j++){
					smallNode[t][i][j]=0;
				}
			}
		}
		
		
	}



	
	/**
	 * Create the graph to given parameters
	 * @param nrFinal number of nodes
	 * @param p1 proportion of type 1 nodes
	 * @param theta1 - change that type 1 connects to type 1
	 * @param theta2 - change that type 2 connects to type 2
	 * @return
	 */
	public static OneGraph factory(int nrFinal, double p1, double theta1, double theta2){
		OneGraph m =new OneGraph(nrFinal, p1, theta1, theta2);
		m.createRoots();
		while(m.nrCurrentNodes<nrFinal){
			// we first decide about the properties of the new node
			int nodetype=0;
			int neighbortype=0;
			
			if (Math.random()<m.p) {
				nodetype=1;
				if (Math.random()<m.theta[0]) neighbortype=1; else neighbortype=2;
			} else {
				nodetype=2;
				if (Math.random()<m.theta[1]) neighbortype=2; else neighbortype=1;
			}
			// then we connect the to new node to the existing graph
			m.connect(nodetype,neighbortype);
			// and only then do we add the node itself
			m.addNode(nodetype,neighbortype);
			m.nrCurrentNodes++;
		}
		return m;
	}
	


	public void createRoots(){
		this.nrCurrentNodes=2;
		if (this.p>0) {
			vertexInformation[0][0]++; // one type 1 node
			vertexInformation[3][0]++; // one small type 1 node
			vertexInformation[0][2]++; // a type 1 degree has one more degree
			vertexInformation[3][2]++; // a small type 1 degree has one more degree
			if (this.p==1) {	
				// in this case we create just a second small point:
				vertexInformation[0][0]++;
				vertexInformation[3][0]++;
				vertexInformation[0][2]++;
				vertexInformation[3][2]++;
				smallNode[0][1][0]=2;
			}
			else smallNode[0][0][1]=1;	
		}
		// one the same, but from the type 2 perspective
		if (this.p<1) {
			this.nrCurrentNodes++;
			vertexInformation[0][1]++;
			vertexInformation[3][1]++;
			vertexInformation[0][3]++;
			vertexInformation[3][3]++;
			if (this.p==0) { 
				// in this case we create just a second small point:
				vertexInformation[0][1]++;
				vertexInformation[3][1]++;
				vertexInformation[0][3]++;
				vertexInformation[3][3]++;
				smallNode[1][0][1]=2;
			} else smallNode[1][1][0]=1;
		}
	}

	
	public void connect(int type, int goalType){
		java.util.Random rnd;
		// We choose proportional to the existing degrees.
		// To avoid decimal numbers, we rephrase this:
		// The total degree of goal-type vertices is verticInformattron[0][goalType+1]
		if (vertexInformation[0][goalType+1]>Integer.MAX_VALUE){
			rnd = new SecureRandom();
		} else rnd = new Random();
		long K=(Math.abs(rnd.nextLong()) % vertexInformation[0][goalType+1]);
		// ===============Add to Large===========
		if (K<vertexInformation[1][goalType+1]) {
			connectToLarge(type, goalType,K);
		} else {
			K=K-vertexInformation[1][goalType+1];
			if (K<vertexInformation[2][goalType+1]){
				connectToMedium(type, goalType,K);
			} else {
				K=K-vertexInformation[2][goalType+1];
				connectToSmall(type, goalType,K);
			} 
		}
		vertexInformation[0][goalType+1]++;
	}

	public void connectToLarge(int type, int goalType, long K){
		// we add to the large nodes (should be the most likely case).
		
		Iterator<Node> it=largeNodes[goalType-1].iterator();
		Node node = null;
		int i=-1;
		while (K>=0 && it.hasNext()){
			node = it.next();
			K = K-node.degree[0];
			i++;
		}
		// So we found the this.largeNodes[goalType-1][i] the node we connect to
		// we first update the node
		node.degree[0]++;
		node.degree[type]++;
		// and then the global storage
		vertexInformation[0][goalType+1]++;
		vertexInformation[1][goalType+1]++;
		
		// we check (and reestablish) that the array largeNodes[xx] is ordered by the total degree of the nodes
		resortlargestList(i,goalType);
		// update the variable smallestLarge 
	}
	
	
	public void resortlargestList(int start, int type){
		if (start>0){
			if (this.largeNodes[type-1].get(start).degree[0]>this.largeNodes[type-1].get(start-1).degree[0]){
				while(start>0){
					if (this.largeNodes[type-1].get(start).degree[0]>this.largeNodes[type-1].get(start-1).degree[0]){
						Node tmp=this.largeNodes[type-1].get(start-1);
						this.largeNodes[type-1].set(start-1, this.largeNodes[type-1].get(start));
						this.largeNodes[type-1].set(start,tmp);
						start--;
					} else {start=-1;}
				}
			}
		}
		smallestLarge=largeNodes[type-1].getLast().degree[0];
	}
	
	public void connectToMedium(int type, int goalType, long K){
		if (mediumNodes[goalType-1].size()==0)
			System.out.println("Tried to add to empty medium list");
		vertexInformation[2][goalType+1]++;
		
		Iterator<Node> it=mediumNodes[goalType-1].iterator();
		Node node = null;
		int i=-1;
		while (K>=0 && it.hasNext()){
			node = it.next();
			i++;
			K = K-node.degree[0];
		}
		if ( node==null ) System.out.println( vertexInformation[2][goalType+1]+" : "+mediumNodes[goalType-1].size() );
		//add the edge to this node
		node.degree[0]++;
		node.degree[type]++;
		
		// by adding this edge this node might belong to the large nodes
		if( node.degree[0]>smallestLarge){
			if (this.largeNodes[goalType-1].size()<this.largeCapacity) System.out.println("Error medium was used before large was filled.");
			Node last=this.largeNodes[goalType-1].getLast();
			largeNodes[goalType-1].set(largeCapacity-1, node);
			mediumNodes[goalType-1].set(i, last);
			resortlargestList(largeCapacity-1, goalType);
			smallestLarge=largeNodes[goalType-1].getLast().degree[0];
		}
	}
	
	public void connectToSmall(int type, int goalType, long K){
		boolean tocontinue=true;
		for (int i=0; i<smallNode[goalType-1].length&&tocontinue;i++){
			for (int j=0; j<smallNode[goalType-1][i].length && tocontinue;j++){
				if (smallNode[goalType-1][i][j]>0){
					K = K-(i+j)*smallNode[goalType-1][i][j];
					if (K<=0){
						tocontinue=false;
						smallNode[goalType-1][i][j]--;
						if (i+j<smallMaxDeg){							
							if (type==1){
								smallNode[goalType-1][i+1][j]++;
							} else {
								smallNode[goalType-1][i][j+1]++;
							}
							vertexInformation[3][goalType+1]++;
						} else {
							// the new vertex will not fit into the small nodes,
							// so we put it into medium or large.
							Node newNode=new Node(goalType,type);
							newNode.degree[0]=smallMaxDeg+1;
							newNode.degree[1]=i;
							newNode.degree[2]=j;
							newNode.degree[type]++;
							vertexInformation[3][goalType-1]--;
							vertexInformation[3][goalType+1]-=smallMaxDeg;

							
							if (this.largeNodes[goalType-1].size()<largeCapacity){
								this.largeNodes[goalType-1].add(newNode);
								resortlargestList(this.largeNodes[goalType-1].size()-1, goalType);
								vertexInformation[1][goalType-1]++;
								vertexInformation[1][goalType+1]+=smallMaxDeg+1;
							} else {
								this.mediumNodes[goalType-1].add(newNode);
								vertexInformation[2][goalType-1]++;
								vertexInformation[2][goalType+1]+=smallMaxDeg+1;
								if(smallestLarge<smallMaxDeg+1) System.out.println("Why did large contain this?");
							}
						}
					}
				}
			}
		}
	}
	
		
	public void addNode(int type, int goalType) {
		vertexInformation[0][type-1]++;
		vertexInformation[3][type-1]++;
		vertexInformation[0][type+1]++;
		vertexInformation[3][type+1]++;
		if (goalType==1)
			smallNode[type-1][1][0]++;
		else smallNode[type-1][0][1]++;
	}



	public void printGraph() {
		System.out.println("Graph has "+ this.nrCurrentNodes*1.0/1000000 +" nodes of ("+(vertexInformation[0][0]*1.0/1000000)+" : "+ vertexInformation[0][1]*1.0/1000000+")  " 
				+" Large ("+vertexInformation[1][0]+":"+ vertexInformation[1][1]+") "
				+" Medium ("+vertexInformation[2][0]+" : "+ vertexInformation[2][1]+") "
				+" Small ("+vertexInformation[3][0]*1.0/1000000+"M : "+ vertexInformation[3][1]*1.0/1000000+"M )");
		if(largeNodes[0].size()>0)
			System.out.print(" with heavy nodes 1 type " + largeNodes[0].get(0).degree[0]);
		if(largeNodes[0].size()>1)
			System.out.print(" and " + largeNodes[0].get(1).degree[0]);
		if(largeNodes[0].size()>2)
			System.out.print(" and " + largeNodes[0].get(2).degree[0]);
		if(largeNodes[0].size()>3)
			System.out.print(" and " + largeNodes[0].get(3).degree[0]);
		
		
		if(largeNodes[1].size()>0)
			System.out.print(" Type 2: "+largeNodes[1].get(0).degree[0]);
		if(largeNodes[1].size()>1)
			System.out.print(" : "+largeNodes[1].get(1).degree[0]);
		if(largeNodes[1].size()>2)
			System.out.print(" : "+largeNodes[1].get(2).degree[0]);
		if(largeNodes[1].size()>3)
			System.out.print(" : "+largeNodes[1].get(3).degree[0]);
		System.out.println();
	}
	
	/**
	 * We will print the degrees and the number of nodes having this degree.
	 * It will occur that a degree is stated multiple times. This needs to be considered when creating the statistics.
	 * 
	 * @param type
	 */
	public void printDegrees(File datei) {
		 String newLine= System.getProperty("line.separator");
		 
		for(int type=1; type<3;type++){
			Iterator<Node> it=largeNodes[type-1].iterator();
			Node node = null;		
			int count=1;
			while (it.hasNext()){
				node = it.next();
				Tools.InputOutputTools.writeToFile(datei,node.type+":"+count+":"+node.degree[0]+":"+node.degree[1]+":"+node.degree[2]+newLine);
			}
			it=mediumNodes[type-1].iterator();
			while (it.hasNext()){
				node = it.next();
				Tools.InputOutputTools.writeToFile(datei,node.type+":"+count+":"+node.degree[0]+":"+node.degree[1]+":"+node.degree[2]+newLine);
			}
			for (int i=smallMaxDeg; i>0;i--){
				for (int j=i; j>-1;j--){
					if(smallNode[type-1][j][i-j]>0)
						Tools.InputOutputTools.writeToFile(datei,type+":"+smallNode[type-1][j][i-j]+":"+i+":"+j+":"+(i-j)+newLine);
				}
			}
		}
		
		
	}
	
	public class Node {
		int type;
		long[] degree;
		
		public Node(int t, int othert){
			type=t;
			degree=new long[3];
			degree[0]=1;
			degree[1]=0;
			degree[2]=0;
			degree[othert]++;
		}
	}
}
