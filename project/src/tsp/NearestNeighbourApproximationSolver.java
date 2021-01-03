package tsp;

import java.util.PriorityQueue;

import org.moeaframework.problem.tsplib.DistanceTable;

import heuristic.Heuristic;
import util.Problem;
import util.StateCompare;

public class NearestNeighbourApproximationSolver extends AbstractSolver {
	
	public NearestNeighbourApproximationSolver(Heuristic heuristic, DistanceTable table) {
		super(heuristic, table);
	}

	public State1 search() {
		initialAndGoal = table.listNodes()[0];
		State1 currentState = new State1(initialAndGoal, table.listNodes().length); //initialise current state
	
		//keep searching until the current state reach goal state.
		int iteration = 0;
		while (!(currentState.getCities().size() == table.listNodes().length+1)) {
			double nearestDistance = Integer.MAX_VALUE;
			State1 nearestState = currentState;
			System.out.println("Iteration:" + iteration);
			if (currentState.getCities().size() == table.listNodes().length) {
				//add the path back to the initial city
				double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), initialAndGoal);
				currentState = new State1(currentState, initialAndGoal, g,0);
			} else {
//				System.out.println("calculation neighbours");
 				for (int s : table.getNeighborsOf(currentState.getCurrentCity())) {
					if  (currentState.pathContains(s)) {
						continue;
					}
//					System.out.println("not cont");
					double dist = Problem.getDistanceBetween(table, currentState.getCurrentCity(), s);
					if (dist < nearestDistance) {
						double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), s);
						nearestDistance = dist;
						nearestState = new State1(currentState, s, g, g + dist);
					}
				}
				System.out.println("nearest: " + nearestState.getCurrentCity());
				currentState = nearestState;
				System.out.println("current: " + currentState.getCities().size());
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
			notifyObservers(currentState, 0);
			iteration++;
			
//			MainRunner.display(currentState);
		}
		return currentState;
	}
}
