package tsp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.Tour;

import heuristic.Heuristic;

public abstract class AbstractSolver implements Solver {
	protected List<SolverObserver> observers = new ArrayList<SolverObserver>();
	protected Heuristic heuristic;
	protected DistanceTable table;
	
	protected int initialAndGoal;
	
	public AbstractSolver(Heuristic heuristic, DistanceTable table) {
		this.heuristic = heuristic;
		this.table = table;
		this.initialAndGoal = table.listNodes()[0];
	}

	public Heuristic getHeuristic() {
		return heuristic;
	}

	public DistanceTable getTable() {
		return table;
	}

	public int getInitialAndGoal() {
		return initialAndGoal;
	}
	
	public void notifyObservers(State1 state, int id) {
		Integer[] cities = state.getCities().toArray(new Integer[]{});
		Tour tour = Tour.createTour(ArrayUtils.toPrimitive(cities));
		for (SolverObserver obs : observers) {
			obs.update(tour, id);
		}
	}
	
	public void addObserver(SolverObserver obs) {
		observers.add(obs);
	}
	
}
