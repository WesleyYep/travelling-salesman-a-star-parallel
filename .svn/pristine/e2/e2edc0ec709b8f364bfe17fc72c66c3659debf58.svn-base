package generator;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.NodeCoordinates;
import org.moeaframework.problem.tsplib.TSPInstance;

public class RandomGraphGenerator {

	private static String NODE_COORD_SECTION = "NODE_COORD_SECTION";
	private static String EOF = "EOF";
	
	public static Graph createRandomGraph(int dimension) throws IOException {
		String fp = System.getProperty("user.dir") + File.separator + "data" + File.separator + "tsp" + File.separator + "prob" + File.separator + "random.tsp";
		File tsplib = new File(fp);

		BufferedWriter bw = Files.newBufferedWriter(tsplib.toPath(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
		bw.write("NAME : Random");
		bw.newLine();
		bw.write("TYPE : TSP");
		bw.newLine();
		bw.write("DIMENSION : " + dimension);
		bw.newLine();
		bw.write("EDGE_WEIGHT_TYPE : EUC_2D");
		bw.newLine();
		bw.write("NODE_COORD_SECTION");
		bw.newLine();
		Map<Point, Integer> cities = new HashMap<Point, Integer>();
		Random randomGenerator = new Random();
		for (int i = 1; i <= dimension; i++) {
			while (true) {
				int x = randomGenerator.nextInt(500);
				int y = randomGenerator.nextInt(500);
				Point p = new Point(x,y);
				if (!cities.containsKey(p)) {
					bw.write(i + " " + x + " " + y);
					bw.newLine();
					cities.put(new Point(x,y), i);
					break;
				}
			}
		}
		bw.write("EOF");
		bw.newLine();
		bw.close();
		
		// Load up tsp lib
		TSPInstance instance = new TSPInstance(tsplib);
		DistanceTable table = instance.getDistanceTable();

		// Create graph
		Graph graph = new MultiGraph("Random Graph - " + dimension);
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
