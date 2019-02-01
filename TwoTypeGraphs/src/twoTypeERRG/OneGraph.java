package twoTypeERRG;

import java.util.LinkedList;

/**
 * This file creates an Erdos-Renyi graph, in which the nodes have two types.
 * The model has the following parameters:
 * - p1 the probability that a now has type 1
 * - p2=1-p1 the probability that a now has type 2
 * - a1 the probability that two arbitrary, fixed type 1 nodes are connected
 * - a2 the probability that two arbitrary, fixed type 2 nodes are connected
 * - beta the probability that arbitrary type 1 and type 2 node are connected
 * @author Robert Fitzner
 */
public class OneGraph {
	Node[] nodes;
	double a1,a2,p1,p2,beta;
	int nrOfCluster, nrOfSingles;
	/** 
	 * The size of all the registered clusters
	 */
	int[] clustersizes;

	
	public OneGraph(int nr, double pa1, double pa2, double pp1, double pbeta) {
		this.a1 = Math.min(1,pa1/nr);
		this.a2 = Math.min(1,pa2/nr);
		this.p1 = pp1;
		this.p2 = 1-pp1;
		this.beta = Math.min(1,pbeta/nr);
		nodes=new Node[nr];
		nrOfCluster=-1;
		nrOfSingles=-1;
		// Create all nodes
		for(int i=0;i<nr;i++){
			if (Math.random()<p1) nodes[i]=new Node(1);
			else nodes[i]=new Node(2);
		}
		
		// in the following we ask each pair of vertices whether they want to be connected. 
		for(int i=1;i<nr;i++){
			for(int j=0;j<i;j++){
				if(nodes[i].type==nodes[j].type){
					if(nodes[i].type==1){
						if(Math.random()<a1){
							nodes[i].contect(nodes[j]);
						}
					} else {
						if(Math.random()<a2){
							nodes[i].contect(nodes[j]);
						}
					}
				}
				else if(Math.random()<beta){
					nodes[i].contect(nodes[j]);
				}
			}
		}
	}
	
	/**
	 * Create the degree history of the graph.
	 * In doing so we consider three different kind of degrees:
	 * the total degree: number of connected bond
	 * the number of bonds leading to type 1 nodes
	 * the number of bonds leading to type 2 nodes
	 * @return
	 */
	public int[][] createDegreehistory(){

		int[] maxs= {0,0,0};
		//First we look for the maximal degree, as we number to initialized the array that store the degree count (java uses static size arrays)
		for(int i=0;i<nodes.length;i++){
			for(int j=0;j<3;j++){
				maxs[j]=Math.max(maxs[j],nodes[i].degree[j]);
			}
		}
		
		// initialized the array used to store the degrees history
		int[][] result=new int[3][];
		for(int j=0;j<3;j++){
			result[j]=new int[maxs[j]+1];
			for(int i=0;i<maxs[j]+1;i++){
				result[j][i]=0;
			}
		}
		// here we go through the graph and create the histogram
		for(int i=0;i<nodes.length;i++){
			for(int j=0;j<3;j++){
				result[j][nodes[i].degree[j]]++;
			}
		}
		return result;
	}

	/**
	 * This methods identifies all connected sub-graphs of the graph.
	 * For this we perform a depth-first search.
	 */
	public void labeling(){
		nrOfCluster=0;
		nrOfSingles=0;
		
		LinkedList<Integer> cs=new LinkedList<Integer>();
		for(int i=0;i<nodes.length;i++){
			// first we take care of the singletons
			if(nodes[i].neighbors.size()==0){
				nodes[i].clusterlabel=-1;
				nrOfSingles++;
			} else if (nodes[i].clusterlabel==0){ 
				// here we consider a node been registered before 
				nrOfCluster++;
				// in this case we explore the whole connected sub-graph=cluster that he is in.
				// the used method returns the number of nodes in this sub-graph
				cs.add(new Integer(exploreCluster(nodes[i],nrOfCluster)));
			}// else we have already taken care of nodes[i]
		}
		
		this.clustersizes=new int[nrOfCluster];
		for(int i=0;i<nrOfCluster;i++){
			clustersizes[i]=cs.get(i).intValue();
		}
		java.util.Arrays.sort(this.clustersizes);
	}
	
	/**
	 * We explore the connected sub-graph in which the given node is in. We assume that we have not seen/touched
	 * any of the nodes of the connected sub-graph.
	 * @param node - the node at which we start the exploration of the cluster.
	 * @param l - the label which we will use to make all the nodes of this connected sub-graph.
	 * @return
	 */
	private int exploreCluster(Node node, int l) {
		int size=1;
		node.clusterlabel=l;
		LinkedList<Node> toExplore=new LinkedList<Node>();
		toExplore.addAll(node.neighbors);
		while(toExplore.size()>0){
			Node c=toExplore.remove();
			if( c.clusterlabel==0 )
			{
				c.clusterlabel=l;
				size++;
				toExplore.addAll(c.neighbors);
			} else if( c.clusterlabel!=l){
				System.out.println("Error in cluster labeling exploration.");
			}			
		}
		return size;
	}
	
	// Implementation of a node. 
	public class Node {
		int type,clusterlabel=0;
		// The type and the label for the node. 
		int[] degree;
		// All direct neighbors of the node.
		LinkedList<Node> neighbors;
		
		public Node(int t){
			type=t;
			degree=new int[3];
			degree[0]=0;
			degree[1]=0;
			degree[2]=0;
			clusterlabel=0;
			neighbors=new LinkedList<Node>();
			
		}

		public void contect(Node other) {
			neighbors.add(other);
			other.neighbors.add(this);
			degree[0]++; degree[other.type]++;
			other.degree[0]++; other.degree[this.type]++;
		}
	}
}
