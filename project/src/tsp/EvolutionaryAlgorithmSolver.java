package tsp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Arrays;
import java.util.Properties;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.TSP2OptHeuristic;
import org.moeaframework.problem.tsplib.TSPInstance;
import org.moeaframework.problem.tsplib.TSPPanel;
import org.moeaframework.problem.tsplib.Tour;

import gui.DisplayFrame;
import heuristic.Heuristic;

public class EvolutionaryAlgorithmSolver extends AbstractSolver {
	private static final Color lightGray = new Color(128, 128, 128, 64);
	
	public EvolutionaryAlgorithmSolver(Heuristic heuristic, DistanceTable table) {
		super(heuristic, table);
	}

	public State1 search() {
		// create the optimization problem and evolutionary algorithm
		Problem problem = new TSPProblem(DisplayFrame.getInstance());
		
		Properties properties = new Properties();
		properties.setProperty("swap.rate", "0.7");
		properties.setProperty("insertion.rate", "0.9");
		properties.setProperty("pmx.rate", "0.4");
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				"NSGAII", properties, problem);
		
		int iteration = 0;
		
		// now run the evolutionary algorithm
		while (true) {
			algorithm.step();
			iteration++;
			
			// display population with light gray lines
			if (algorithm instanceof EvolutionaryAlgorithmSolver) {
				EvolutionaryAlgorithm ea = (EvolutionaryAlgorithm)algorithm;
				
//				for (Solution solution : ea.getPopulation()) {
//					panel.displayTour(toTour(solution), lightGray);
//				}
			}
			StringBuilder progress = new StringBuilder();
			// display current optimal solutions with red line
			Tour best = toTour(algorithm.getResult().get(0));
		//	panel.displayTour(best, Color.RED, new BasicStroke(2.0f));
			progress.insert(0, "Iteration " + iteration + ": " +
					best.distance(DisplayFrame.getInstance()) + "\n" + "tour: " + Arrays.toString(best.toArray()));
			System.out.println(progress.toString());
			//progressText.setText(progress.toString());
			// repaint the TSP display
		//	panel.repaint();
			//MainRunner.display(best);
		}
	}
	
	public static Tour toTour(Solution solution) {
		int[] permutation = EncodingUtils.getPermutation(
				solution.getVariable(0));
		
		// increment values since TSP nodes start at 1
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]++;
		}
		
		return Tour.createTour(permutation);
	}
	
	/**
	 * Saves a {@link Tour} into a MOEA Framework solution.
	 * 
	 * @param solution the MOEA Framework solution
	 * @param tour the tour
	 */
	public static void fromTour(Solution solution, Tour tour) {
		int[] permutation = tour.toArray();
		
		// decrement values to get permutation
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]--;
		}
		
		EncodingUtils.setPermutation(solution.getVariable(0), permutation);
	}
	
	/**
	 * The optimization problem definition.  This is a 1 variable, 1 objective
	 * optimization problem.  The single variable is a permutation that defines
	 * the nodes visited by the salesman.
	 */
	public static class TSPProblem extends AbstractProblem {

		/**
		 * The TSP problem instance.
		 */
		private final TSPInstance instance;
		
		/**
		 * The TSP heuristic for aiding the optimization process.
		 */
		private final TSP2OptHeuristic heuristic;
		
		/**
		 * Constructs a new optimization problem for the given TSP problem
		 * instance.
		 * 
		 * @param instance the TSP problem instance
		 */
		public TSPProblem(TSPInstance instance) {
			super(1, 1);
			this.instance = instance;
			
			heuristic = new TSP2OptHeuristic(instance);
		}

		@Override
		public void evaluate(Solution solution) {
			Tour tour = toTour(solution);
			
			// apply the heuristic and save the modified tour
			heuristic.apply(tour);
			fromTour(solution, tour);

			solution.setObjective(0, tour.distance(instance));
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(1, 1);
			
			solution.setVariable(0, EncodingUtils.newPermutation(
					instance.getDimension()));
			
			return solution;
		}
	}
}
