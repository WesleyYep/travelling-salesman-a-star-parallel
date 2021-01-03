package tsp;

import java.util.ArrayList;
import java.util.List;

import org.moeaframework.problem.tsplib.DistanceTable;

import util.Problem;

public class State1 {
	private List<Integer> tour = new ArrayList<Integer>();;
	private double currentTourDistance;
	private double fValue;
	
	public State1(int initialState, int size) {
		tour.add(initialState);
		currentTourDistance = 0;
	}
	
	public State1(State1 previous, int next, double distance, double fValue) {
		tour.addAll(previous.tour);
		tour.add(next);
		currentTourDistance = distance;
		this.fValue = fValue;
	}
	
	public List<Integer> getCities() {
		return tour;
	}
	
	public int getCurrentCity() {
		return tour.get(tour.size()-1);
	}
	
	public double getCurrentTourDistance() {
		return currentTourDistance;
	}
	
	public double getFValue() {
		return fValue;
	}
	
	public boolean pathContains(int state) {
		return tour.contains(state);
	}
	
	public double getNearestUnvisitedCityCost(int currentNode, DistanceTable table) {
		double min = Integer.MAX_VALUE;
		for (int neighbour : table.getNeighborsOf(currentNode)) {
			if  (this.pathContains(neighbour)) {
				continue;
			}
			double cost = Problem.getDistanceBetween(table, currentNode, neighbour);
			
			if (cost < min) {
				min = cost;
			}
		}
		if (min == Integer.MAX_VALUE) {
			return 0;
		}
		return min;
	}
}
