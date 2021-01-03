package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.NodeCoordinates;
import org.moeaframework.problem.tsplib.TSPInstance;

public class GraphGenerator {

	private static String NODE_COORD_SECTION = "NODE_COORD_SECTION";
	private static String EOF = "EOF";
	
	public static Graph getGraph(File tsplib) throws IOException {
		
		// Load up tsp lib
		TSPInstance instance = new TSPInstance(tsplib);
		DistanceTable table = instance.getDistanceTable();
		
		String probName = tsplib.getName();
		
		// Create graph
		Graph graph = new MultiGraph(probName);
		// generate nodes in the graph
		NodeCoordinates coord = instance.getDisplayData();
		
		if (coord == null) {
			// have to manually get the coords by re-reading data
			Map<Integer, Double[]> coordMap = getCoordMap(tsplib);
			
			for (Map.Entry<Integer, Double[]> e : coordMap.entrySet()) {
				Integer nodeId = e.getKey();
				Double[] pos = e.getValue();
				
				String nodeName = String.valueOf(nodeId);
				
				// add node
				graph.addNode(nodeName);
				graph.getNode(nodeName).setAttribute("xy", pos[0], pos[1]);
			}
			
		} else {
			// generate nodes from coord provided
			int[] nodes = table.listNodes();
			for (int i = 0; i < nodes.length; i++) {
				org.moeaframework.problem.tsplib.Node n = coord.get(nodes[i]);
				double[] pos = n.getPosition();
				
				String nodeName = String.valueOf(nodes[i]);
				// add node
				graph.addNode(nodeName);
				graph.getNode(nodeName).setAttribute("xy", pos[0], pos[1]);
			}
		}
		
		return graph;
	}
	
	private static Map<Integer, Double[]> getCoordMap(File tsplib) throws IOException {
		
		Map<Integer, Double[]> coordMap = new HashMap<Integer, Double[]>();
		
		try (BufferedReader reader = Files.newBufferedReader(tsplib.toPath())) {
			String line = "";
			
			// keep reading lines until coord section is reached
			while (!(reader.readLine().equals(NODE_COORD_SECTION)));
			
			// collect co-ordinate values
			while(!((line = reader.readLine()).equals(EOF))) {
				String[] args = line.trim().split("\\s+");
				// extract values
				Integer nodeId = Integer.parseInt(args[0]);
				
				Double x = Double.parseDouble(args[1]);
				Double y = Double.parseDouble(args[2]);
				
				Double[] pos = new Double[2];
				pos[0] = x;
				pos[1] = y;
				
				// add to map
				coordMap.put(nodeId, pos);
			}
		}
		
		return coordMap;
	}
	
}
