package heuristic;

import java.util.List;

import org.moeaframework.problem.tsplib.DistanceTable;

import tsp.State1;

public interface Heuristic {
	double heuristicCost(int neighbourState, DistanceTable table, List<Integer> cities, State1 currentState);
}
