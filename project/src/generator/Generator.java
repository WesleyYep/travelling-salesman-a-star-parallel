package generator;

import java.io.File;
import java.io.IOException;

import org.moeaframework.problem.tsplib.DataType;
import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.TSPInstance;

import representation.AdjList;
import representation.AdjMatrix;
import representation.GraphRepresentation;
import representation.Representation;

/**
 * Generates a GraphRepresentation of a TSP problem from a TSPLIB dataset
 * @author Chang Kon Han
 */

public class Generator {

	public static GraphRepresentation getRepresentation(File tsplib, Representation representation) throws IOException {
		TSPInstance instance = new TSPInstance(tsplib);
		DistanceTable table = instance.getDistanceTable();
		
		if (representation == Representation.ADJACENCY_LIST) {
			
		} else if (representation == Representation.ADJACENCY_MATRIX) {
			DataType type = instance.getDataType();
			boolean isSymmetric = type == DataType.TSP;
			getAdjMatrix(table, isSymmetric);
		}
		
		return null;
	}
	
	private static AdjList getAdjList(DistanceTable tspDistTable) {
		// TODO Generate Adjacency list using TSPLIB4J library
		return null;
	}
	
	private static AdjMatrix getAdjMatrix(DistanceTable tspDistTable, boolean isSymmetric) {
		//TODO Generate Adjacency matrix using TSPLIB4J library
		int numOfVertices = tspDistTable.listNodes().length;
		
		AdjMatrix matrix = new AdjMatrix(numOfVertices, isSymmetric);
		
		for (int node : tspDistTable.listNodes()) {
			for (int neighbour : tspDistTable.getNeighborsOf(node)) {
				double distance = tspDistTable.getDistanceBetween(node, neighbour);
				
				if (isSymmetric) {
					// make sure it targets lower left side of triangle
					matrix.makeEdge(Math.min(neighbour-1, node-1), Math.max(neighbour-1, node-1), (int)distance);
				} else {
					// note: values start from 1 on DistanceTable
					matrix.makeEdge(neighbour-1, node-1, (int)distance);
				}

			}
		}
		
		return matrix;
	}
	
}
