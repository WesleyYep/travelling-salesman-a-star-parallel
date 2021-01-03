package tsp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.problem.tsplib.DistanceTable;
import org.moeaframework.problem.tsplib.TSPPanel;
import org.moeaframework.problem.tsplib.Tour;
import gui.DisplayFrame;
import heuristic.Heuristic;
import heuristic.MinimumSpanningTree;
import pt.runtime.ParaTask;
import pttask.AStarBlackboardSolver;
import pttask.*;

public class MainRunner {
	
	public static final String[] sequentialAlgorithms = { "A*", "IDA*", "Genetic Algorithm" };
	public static final String[] parallelAlgorithms = { "A* Centralised", "A* Blackboard", "IDA* Parallel", "HDA* Parallel" };
	private long startTime;
	private static TSPPanel workingPanel;
	private static DisplayFrame frame;
	
	public MainRunner() {
		
	}
	
	public static void main(String[] args){
		ParaTask.init();
		MainRunner main = new MainRunner();
		frame = new DisplayFrame("Travelling Salesman A Star", main);
		frame.display();
	}
	
	public void runAlgorithm(String file, String type, JButton startButton, DistanceTable table, TSPPanel workingPanel, TSPPanel solutionPanel) {
		System.out.println(type);
		this.workingPanel = workingPanel;
		startTime = System.currentTimeMillis();
		workingPanel.clearTours();
		solutionPanel.clearTours();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Heuristic mst = new MinimumSpanningTree();
				Solver solver = new AStarSolver(mst, table); //default to normal A*
				if (type.equals("A*")) { solver = new AStarSolver(mst, table); }
				else if (type.equals("NN")) { solver = new NearestNeighbourApproximationSolver(mst, table); }
				else if (type.equals("IDA*")) { solver = new IDAStarSolver(mst, table); }
				else if (type.equals("A* Centralised")) { solver = new AStarSolverParallel(mst, table); }
				else if (type.equals("IDA* Parallel")) { solver = new IDAStarSolverParallel(mst, table); }
				else if (type.equals("A* Blackboard")) { solver = new AStarBlackboardSolver(mst, table); }
				else if (type.equals("Genetic Algorithm")) { solver = new EvolutionaryAlgorithmSolver(mst, table); }
				else if (type.equals("HDA* Parallel")) { solver = new AStarHDA(mst, table); }
				State1 solutionState = solver.search();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						startButton.setEnabled(true);
						finish(solutionState, solutionPanel);	
					}
				});
			}
		});
		thread.start();
	}
	
	public long getTime(){
		return System.currentTimeMillis() - startTime;
	}
		
	public void finish(State1 finalState, TSPPanel panel) {
		StringBuffer buffer = new StringBuffer();
		List<Integer> result = finalState.getCities();
		buffer.append("Final path is: ");
		for (Integer city : result) {
			buffer.append(city + " ");
		}
		buffer.append("Final distance travelled is: " + finalState.getCurrentTourDistance());
		buffer.append(" at time: " + getTime());
		System.err.println(buffer.toString());
		Integer[] cities = finalState.getCities().toArray(new Integer[]{});
		Tour best = Tour.createTour(ArrayUtils.toPrimitive(cities));
		panel.clearTours();
		panel.displayTour(best, Color.RED, new BasicStroke(2.0f));
		panel.repaint();
		
		System.out.println("time: " + (System.currentTimeMillis() - startTime));
	//	System.out.println("memory: " + Runtime.getRuntime().totalMemory());
	}
	
	public static void display(State1 currentState) {
		if (!frame.showWorkingEnabled()) {
			return;
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Color[] colors = new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.BLACK, Color.CYAN, Color.PINK, Color.MAGENTA };
		long threadId = Thread.currentThread().getId();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Integer[] cities = currentState.getCities().toArray(new Integer[]{});
				Tour best = Tour.createTour(ArrayUtils.toPrimitive(cities));
				workingPanel.clearTours();
				workingPanel.displayTour(best, colors[(int)threadId%colors.length], new BasicStroke(2.0f));
				workingPanel.repaint();
			}
		});
	}
	
	public static void display(Tour best) {
		workingPanel.clearTours();
		workingPanel.displayTour(best, Color.RED, new BasicStroke(2.0f));
		workingPanel.repaint();
	}
	
}
