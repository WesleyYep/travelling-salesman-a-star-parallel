package tsp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.Node;
import org.moeaframework.problem.tsplib.NodeCoordinates;
import org.moeaframework.problem.tsplib.TSPPanel;
import org.moeaframework.problem.tsplib.Tour;

import heuristic.Heuristic;
import util.Problem;

public class IDAStarSolver extends AbstractSolver {
	private State1 finalState;
	
	public IDAStarSolver(Heuristic heuristic, DistanceTable table) {
		super(heuristic, table);
	}
	
	public State1 search() {
		//create the initial state and initial bound
		int initialAndGoal = table.listNodes()[0];
		State1 currentState = new State1(initialAndGoal, table.listNodes().length); //initialise current state
		double bound = Math.round(heuristic.heuristicCost(currentState.getCurrentCity(), table, currentState.getCities(), currentState));
		
		//keep searching until the current state reach goal state.
		while (true) {
//			System.out.println(bound);
			//search the state and it will return the next bound based on the current search
			double t = search (currentState,0.0,bound,table);
			
			//-1 mean it found the solution
			if (t == -1) {
				//System.out.println("done");
				break;
			}
			
			//stop the search when it's maximum
			if (t == Double.MAX_VALUE)  {
				System.out.println("NOT FOUND");
				break;
			}
			
			//if the search continue then update the bound
			bound = t;
		}
		return finalState;
		
	}
	
	
	private double search(State1 state, double g, double bound, DistanceTable table) {
		
		//calculate the f(state) and check if it's still within bound else stop searching
		double f = g + Math.round(heuristic.heuristicCost(state.getCurrentCity(), table, state.getCities(), state));
		State1 currentState = state;
		if ( f > bound) return f;
		
		//check the it reach the goal state
		if (currentState.getCities().size() == table.listNodes().length) {
			int initialAndGoal = table.listNodes()[0];
			currentState = new State1(currentState, initialAndGoal, currentState.getCurrentTourDistance() + Math.round(Problem.getDistanceBetween(table,currentState.getCurrentCity(), initialAndGoal)),0);
			
			List<Integer> result = currentState.getCities();
			finalState = currentState;
			return -1.0;
		}
		double min = Double.MAX_VALUE;
		
		
		/*
		 * (if the search haven't found the goal state so expand it's children,
		 * return the minimum value from the children as the bound
		 */
		for (int s : table.getNeighborsOf(currentState.getCurrentCity())) {
			if  (currentState.pathContains(s)) {
				continue;
			}
			double currentDistance = currentState.getCurrentTourDistance() + Math.round(Problem.getDistanceBetween(table,currentState.getCurrentCity(), s));
			State1 tempState = new State1(currentState, s, currentDistance, 0);
			notifyObservers(tempState, 0);
			double cost = Math.round(Problem.getDistanceBetween(table,currentState.getCurrentCity(), s));
			double t = search(tempState, g + cost, bound,table);
			if (t == -1.0) return -1.0;
			if (t < min) min = t;
		} 
		return min;
	}
}
