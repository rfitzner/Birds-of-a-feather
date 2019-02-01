package twoTypeCM;

import java.util.LinkedList;

/**
 * This file creates an graph according to the configuration model.
 * We consider a model with two type of nodes.
 * Type 1 nodes have degrees, that are distributed according a Poisson distribtion.
 * Type 2 nodes have degrees, that are distributed according a Simon-Yule distributions.
 * The model has the following parameters:
 * - p1 the probability that a now has type 1
 * - p2=1-p1 the probability that a now has type 2
 * - x1,x2 the probability that and half-egde will connected to a same-type node
 * - mu1,mu2 the average degree of the nodes for the two types
 * @author Robert Fitzner
 */
public class OneGraphOneHeavyTail {
	Node[] nodes;
	double[] p, xi, mus;
	double rho;
	int nrOfCluster, nrOfSingles, rnEmpty;
	int[] clustersizes;

	public OneGraphOneHeavyTail(int nr, double p1, double xi1, double xi2, double mu1, double mu2) {
		// read parameters
		this.p = new double[2];
		this.p[0] = p1;
		this.p[1] = 1 - p1;
		this.xi = new double[2];
		this.xi[0] = xi1;
		this.xi[1] = xi2;
		rho=mu2/(mu2-1);
		this.mus = new double[2];
		this.mus[0] = mu1;
		this.mus[1] = mu2;
		// create Nodes
		nodes = new Node[nr];
		rnEmpty=0;
		nrOfCluster = -1;
		nrOfSingles = -1;
		long nrTotalOneOne = 0;
		long nrTotalTwoTwo = 0;
		long nrTotalOneTwo = 0;
		long nrTotalTwoOne = 0;
		for (int i = 0; i < nr; i++) {
			int type = 0;
			int totalConnections = 0;
			int sameConnections = 0;
			if (Math.random() < p1)
				type = 1;
			else
				type = 2;

			totalConnections = this.getNumberOfHalfEdges(type);
			sameConnections = Tools.Mathtool.getBinomial(totalConnections, xi[type - 1]);
			if (type == 1) {
				nrTotalOneOne += sameConnections;
				nrTotalOneTwo += totalConnections - sameConnections;
			} else {
				nrTotalTwoTwo += sameConnections;
				nrTotalTwoOne += totalConnections - sameConnections;
			}
			if (totalConnections==0) this.rnEmpty++;
			nodes[i] = new Node(type, sameConnections, totalConnections - sameConnections, i);
		}

		// Now we pair the half edges
		// first the type one to type one
		while (nrTotalOneOne > 1) {
			int[] ends = getNumbersSameKind(nrTotalOneOne, 1);
			nodes[ends[0]].contect(nodes[ends[1]]);

			nrTotalOneOne = nrTotalOneOne - 2;
		}
		// then type two to type two
		while (nrTotalTwoTwo > 1) {
			int[] ends = getNumbersSameKind(nrTotalTwoTwo, 2);
			nodes[ends[0]].contect(nodes[ends[1]]);
			nrTotalTwoTwo = nrTotalTwoTwo - 2;
		}
		// Now finally the mixed kind
		while ((nrTotalOneTwo > 0) && (nrTotalTwoOne > 0)) {
			int[] ends = getNumbersOtherKind(nrTotalOneTwo, nrTotalTwoOne);
			nodes[ends[0]].contect(nodes[ends[1]]);
			nrTotalOneTwo--;
			nrTotalTwoOne--;
		}
	}

	/**
	 * Using this method we find two random unassigned half-edges and connect them
	 * @param nrhalfEdges
	 * @param type
	 * @return
	 */
	public int[] getNumbersSameKind(long nrhalfEdges, int t) {
		int[] result = new int[2];
		long index1 = 0;
		long index2 = 0;
		while (index1 == index2) {
			index1 = Tools.Mathtool.randomLong(nrhalfEdges);
			index2 = Tools.Mathtool.randomLong(nrhalfEdges);
		}

		for (int i = 0; (i < nodes.length) && (index1 >= 0); i++) {
			if (nodes[i].type == t) {
				index1 = index1 - nodes[i].unassignedSameKind;
				if (index1 < 0) {
					result[0] = i;
					if (nodes[i].unassignedSameKind == 0)
						System.out.println("Mistake when choosing index1 " + i);
				}
			}
		}
		for (int i = 0; (i < nodes.length) && (index2 >= 0); i++) {
			if (nodes[i].type == t) {
				index2 = index2 - nodes[i].unassignedSameKind;
				if (index2 < 0) {
					result[1] = i;
					if (nodes[i].unassignedSameKind == 0)
						System.out.println("Mistake when choosing index2 " + i);
				}
			}
		}
		return result;
	}

	/**
	 * Using this method we find two random unassigned half-edges and connect to point of different kinds
	 * @param nrHalfEdgeLeadingtoType1
	 * @param nrHalfEdgeLeadingtoType2
	 * @return
	 */
	public int[] getNumbersOtherKind(long nrHalfEdgeLeadingtoType1, long nrHalfEdgeLeadingtoType2) {
		int[] result = new int[2];
		long index1 = Tools.Mathtool.randomLong(nrHalfEdgeLeadingtoType1);
		long index2 = Tools.Mathtool.randomLong(nrHalfEdgeLeadingtoType2);
		for (int i = 0; (i < nodes.length) && (index1 >= 0); i++) {
			if (nodes[i].type == 1) {
				index1 = index1 - nodes[i].unassignedOtherKind;
				if (index1 < 0) {
					result[0] = i;
					if (nodes[i].unassignedOtherKind == 0)
						System.out.println("T1diff: Mistake when choosing index1 " + i + " " + nodes[i].type);
				}
			}
		}
		for (int i = 0; (i < nodes.length) && (index2 >= 0); i++) {
			if (nodes[i].type == 2) {
				index2 = index2 - nodes[i].unassignedOtherKind;
				if (index2 < 0) {
					result[1] = i;
					if (nodes[i].unassignedOtherKind == 0)
						System.out.println("T2diff: Mistake when choosing index2 " + i + " " + nodes[i].type);
				}
			}
		}
		return result;
	}


	public int getNumberOfHalfEdges(int t) {
		if (t == 1)
			return (int) Tools.Mathtool.getPoisson(mus[0]);
		else
			return (int) Math.min(Tools.Mathtool.sampleYuleSimon(rho), 3000);
	}
	
	/**
	 * This methods identifies all connected sub-graphs of the graph.
	 * For this we perform a depth-first search.
	 */
	public void labeling() {
		nrOfCluster = 0;
		nrOfSingles = 0;

		LinkedList<Integer> cs = new LinkedList<Integer>();
		for (int i = 0; i < nodes.length; i++) {
			// first I throw out the singletons
			if (nodes[i].neighbors.size() == 0) {
				nodes[i].clusterlabel = -1;
				nrOfSingles++;
			} else if (nodes[i].clusterlabel == 0) {
				// in this case we explore the whole connected sub-graph=cluster that he is in.
				// the used method returns the number of nodes in this sub-graph
				nrOfCluster++;
				cs.add(new Integer(exploreCluster(nodes[i], nrOfCluster)));
			}
		}

		this.clustersizes = new int[nrOfCluster];
		for (int i = 0; i < nrOfCluster; i++) {
			clustersizes[i] = cs.get(i).intValue();
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
		int size = 1;
		node.clusterlabel = l;
		LinkedList<Node> toExplore = new LinkedList<Node>();
		toExplore.addAll(node.neighbors);
		while (toExplore.size() > 0) {
			Node c = toExplore.remove();
			if (c.clusterlabel == 0) {
				c.clusterlabel = l;
				size++;
				toExplore.addAll(c.neighbors);
			} else if (c.clusterlabel != l) {
				System.out.println("Error in cluster labeling exploration.");
			}
		}
		return size;
	}

	public void printClusterSize() {
		if (clustersizes != null) {
			System.out.println(
					"We have " + this.nrOfSingles + " singletones and a total of " + nrOfCluster + " components.");
			for (int i = 0; i < nrOfCluster; i++) {
				System.out.print(this.clustersizes[i] + ", ");
			}
			System.out.println();
			System.out.println();
		}
	}

	public void printGraph() {
		System.out.println("The graph has " + nodes.length + " nodes, " + this.nrOfSingles + " Of them are single. By default "+this.rnEmpty);
		if (this.clustersizes!=null)
		System.out.println("We see " + this.nrOfCluster + " cluster, the biggest has "
				+ this.clustersizes[this.clustersizes.length - 1] + " points");
		System.out.println();
	}
	
	
	// Implementation of a node. 		
	public class Node {
		// The type and the label for the node.
		int type, clusterlabel, name;
		// number of half-edges, that are not jet assigned
		int unassignedSameKind, unassignedOtherKind;
		// All direct neighbors of the node.
	LinkedList<Node> neighbors;

		public Node(int t, int same, int other, int name) {
			type = t;
			this.name = name;
			clusterlabel = 0;
			unassignedSameKind = same;
			unassignedOtherKind = other;
			neighbors = new LinkedList<Node>();
		}

		public void contect(Node other) {
			if (this == other) {
				this.unassignedSameKind -= 2;
				return;
			}
			if (neighbors.contains(other)) {
				if (other.type == this.type) {
					this.unassignedSameKind--;
					other.unassignedSameKind--;
				} else {
					this.unassignedOtherKind--;
					other.unassignedOtherKind--;
				}
				return;
			}
			if (this.type == other.type) {
				this.unassignedSameKind--;
				other.unassignedSameKind--;
				this.neighbors.add(other);
				other.neighbors.add(this);
				if (this.unassignedSameKind < 0)
					System.out.println("Mistake when connecting points " + this.name + " and " + other.name
							+ ", doing same " + this.type + ":" + other.type);
				if (other.unassignedSameKind < 0)
					System.out.println("Mistake when connecting points " + this.name + " and " + other.name
							+ ", doing same" + this.type + ":" + other.type);
			} else {
				this.unassignedOtherKind--;
				other.unassignedOtherKind--;
				this.neighbors.add(other);
				other.neighbors.add(this);
				if (this.unassignedOtherKind < 0)
					System.out.println("Mistake when connecting points, doing different.");
				if (other.unassignedOtherKind < 0)
					System.out.println("Mistake when connecting points, doing different.");
			}
		}

		public String toStringbrief() {
			return "(" + name + "-" + type + "-" + clusterlabel + ")";
		}

		public String toString() {
			String str = this.toStringbrief();
			if (neighbors.size() > 0) {
				str += " and is connected to ";
				for (int i = 0; i < this.neighbors.size(); i++) {
					str += this.neighbors.get(i).toStringbrief() + "  ";
				}
			} else {
				str += " noneighbors.";
			}
			if (unassignedSameKind + unassignedOtherKind > 0)
				str += " To process " + unassignedSameKind + " :" + unassignedOtherKind;
			return str;
		}

	}
}
