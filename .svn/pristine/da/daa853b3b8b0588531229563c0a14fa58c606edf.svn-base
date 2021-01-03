package util;

import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.Node;
import org.moeaframework.problem.tsplib.NodeCoordinates;

public class Problem {
	
	public static double getDistanceBetween(DistanceTable table, int city1, int city2) {
		Node n1 = ((NodeCoordinates)table).get(city1);
		Node n2 = ((NodeCoordinates)table).get(city2);
		
		double[] position1 = n1.getPosition();
		double[] position2 = n2.getPosition();

		double result = 0.0;

		for (int i = 0; i < position1.length; i++) {
			result += Math.pow(position1[i] - position2[i], 2.0);
		}

		return Math.sqrt(result);
	}
}
