package representation;

/**
 * Represents a graph structure stored in an adjacency matrix
 * @author Chang Kon Han
 * @author Wesley Yep
 * @author John Law
 */

public class AdjMatrix implements GraphRepresentation {

	// [row][column]
	private int[][] matrix;
	private int numOfVertices;
	
	/**
	 * Creates a graph representation as a square adjacency matrix. The graph is |V|x|V| matrix size.
	 * @param numOfVertices
	 */
	
	public AdjMatrix(int numOfVertices) {
		this(numOfVertices, true);
	}
	
	/**
	 * Constructs a graph representation of a graph. Depending on directed or undirected graphs, creates a square or packed triangle array.
	 * @param numOfVertices
	 * @param isUndirected
	 * @see https://en.wikipedia.org/wiki/Triangular_array
	 */
	
	public AdjMatrix(int numOfVertices, boolean isUndirected) {
		this.numOfVertices = numOfVertices;
		
		if (isUndirected) {
			// Creates a packed triangle format array. Stores information in lower portion of array
			matrix = new int[numOfVertices][];
			
			// Creates horizontal array section
			for (int i = 0; i < numOfVertices; i++) {
				matrix[i] = new int[i+1];
			}
			
		} else {
			// Creates a square matrix
			matrix = new int[numOfVertices][numOfVertices];
		}
		// TODO initialise values to Integer.MAX
	}
	
	/**
	 * Sets the edge cost between the vertex. Note that this is a one way setting.
	 * @param to
	 * @param from
	 * @param edgeCost
	 */
	
	public synchronized void makeEdge(int to, int from, int edgeCost) {
		matrix[from][to] = edgeCost;
	}
	
	/**
	 * Gets the edge cost between the "to" vertex and "from" vertex
	 * @param to
	 * @param from
	 * @return edge cost
	 */
	
	public synchronized int getEdgeCost(int to, int from) {
		// TODO can return zero value. Needs to be discussed
		return matrix[from][to];
	}
	
	public int getNumOfVertices() {
		return numOfVertices;
	}
	
}
