package representation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Creates a graph representation using an Adjacency List
 * @author Chang Kon Han
 * @author Wesley Yep
 * @author John Law
 * @see http://www.mathcs.emory.edu/~cheung/Courses/171/Syllabus/11-Graph/weighted.html
 */

public class AdjList implements GraphRepresentation {

	/**
	 * Represents an edge in a graph for an adjacency list
	 * @author Chang Kon Han
	 * @author Wesley Yep
	 * @author John Law
	 */
	
	public static class Edge {
		public int nodeId;
		public int weight;
		public Edge next;
	}
	
	private List<ConcurrentLinkedQueue<Edge>> adjLists;
	private int numOfVertices;
	
	/**
	 * Creates an adjacency list representation of an array. Graph contains an array of linkedlists.
	 * @param numOfVertices
	 */
	
	public AdjList(int numOfVertices) {
		this.numOfVertices = numOfVertices;
		adjLists = new ArrayList<ConcurrentLinkedQueue<Edge>>();
		
		// TODO can be parallelised. Don't use java 8 method. Probably won't work in labs
		for (int i = 0; i < numOfVertices; i++) {
			// populate graph with linkedlists
			ConcurrentLinkedQueue<Edge> linkedList = new ConcurrentLinkedQueue<Edge>();
			adjLists.add(linkedList);
		}
	}
	
	/**
	 * Adds an edge to the "from" linkedlist. NOTE: This is a unidirectional process.
	 * @param to
	 * @param from
	 * @param edgeCost
	 */
	
	public synchronized void makeEdge(int to, int from, int edgeCost) {
		Edge e = new Edge();
		e.nodeId = to;
		e.weight = edgeCost;
		
		// add to tail of list
		ConcurrentLinkedQueue<Edge> linkedList = adjLists.get(from);
		linkedList.add(e);
	}
	
	public synchronized int getEdgeCost(int to, int from) {
		// TODO needs to be discussed if returned cost is -1 if not found
		int cost = -1;
		ConcurrentLinkedQueue<Edge> linkedList = adjLists.get(from);
		
		for (Edge e : linkedList) {
			if (e.nodeId == to) {
				cost = e.weight;
				break;
			}
		}
		
		return cost;
	}
	
	public int getNumOfVertices() {
		return numOfVertices;
	}
}
