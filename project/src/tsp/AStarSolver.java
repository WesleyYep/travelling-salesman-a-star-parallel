package tsp;

import java.util.PriorityQueue;

import org.moeaframework.problem.tsplib.DistanceTable;

import heuristic.Heuristic;
import util.Problem;
import util.StateCompare;

public class AStarSolver extends AbstractSolver {
	
	public AStarSolver(Heuristic heuristic, DistanceTable table) {
		super(heuristic, table);
	}

	public State1 search() {
		initialAndGoal = table.listNodes()[0];
		State1 currentState = new State1(initialAndGoal, table.listNodes().length); //initialise current state
		PriorityQueue<State1> frontier = new PriorityQueue<State1>(1, new StateCompare());

		//keep searching until the current state reach goal state.
		while (!(currentState.getCities().size() == table.listNodes().length+1)) {
	//		System.out.println("Iteration:" + iteration);
			if (currentState.getCities().size() == table.listNodes().length) {
				//add the path back to the initial city
				double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), initialAndGoal);
				frontier.add(new State1(currentState, initialAndGoal, g,0));
			} else {
				for (int s : table.getNeighborsOf(currentState.getCurrentCity())) {
					if  (currentState.pathContains(s)) {
						continue;
					}
					//evaluate the g value of the state
					//	int g = getStartToCurrentCost(s,cityList,edgeCostList);
					double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), s);
	
					//evaluate the h value of the state
					double h = heuristic.heuristicCost(s, table, currentState.getCities(), currentState);
					
					//store the f value in the map
					double fValue = g + h;
					frontier.add(new State1(currentState, s, g, fValue));
				}
			}
 			
			//finding the best f value in the open list and set is as current.
			State1 minimumState = frontier.poll();
			
			currentState = minimumState;
			notifyObservers(currentState, 0);
//			MainRunner.display(currentState);
		}
		return currentState;
	}
}
