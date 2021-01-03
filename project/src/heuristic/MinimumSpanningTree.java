package heuristic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.moeaframework.problem.tsplib.DistanceTable;

import tsp.State1;
import util.Problem;

public class MinimumSpanningTree implements Heuristic {

	@Override
	public double heuristicCost(int neighbourState, DistanceTable table, List<Integer> cities, State1 currentState) {
		double a = currentState.getNearestUnvisitedCityCost(neighbourState, table);
		double[] values = getSpanningTreeCost(cities, neighbourState, table);
		double b = values[1];
		double c = values[0];
		
		return a + b + c;
	}

	public double[] getSpanningTreeCost(List<Integer> cities, int neighbourState, DistanceTable table) {
		double[] returnValues = new double[2];
		List<Integer> unvisitedNodes = new ArrayList<Integer>();
		int[] nodes = table.listNodes();
		Map<Integer, Integer> hash = new HashMap<Integer, Integer>();
		double minimumDistanceToStart = Integer.MAX_VALUE;
		
		//get unvisited nodes
		for (int n : cities) {
			hash.put(n, n);
		}
		hash.put(neighbourState, neighbourState);
		for (int n : nodes) {
			if (!hash.containsKey(n)) {
				unvisitedNodes.add(n);
				minimumDistanceToStart = Math.min(Problem.getDistanceBetween(table, n, 1), minimumDistanceToStart);
			}
		}
		
		if (unvisitedNodes.size() <= 0) {
	        returnValues[0] = 0;
	        returnValues[1] = 0;
			return returnValues;
		}
		
		//prim's algorithm
        double[] cost=new double[unvisitedNodes.size()]; // distance to MST
        boolean[] visit=new boolean [unvisitedNodes.size()];
		Arrays.fill (cost, Double.MAX_VALUE);
        cost[0]=0.0D;
        double total = 0.0;
        for (int i=0; i<cost.length; i++) {
           // Find next node to visit: minimum distance to MST
           double m=Double.MAX_VALUE; 
           int v = -1;
           for (int j=0; j<cost.length; j++) {
              if (!visit[j] && cost[j]<m) { v=j; m=cost[j]; }
           }
           visit[v]=true;
    	   total += m;
           for (int j=0; j<cost.length; j++) {
  //            final double d = Problem.getDistanceBetween(table, v+1, j+1);
              final double d = Problem.getDistanceBetween(table, unvisitedNodes.get(v), unvisitedNodes.get(j));

    		  //Math.hypot (x[v]-x[j],y[v]-y[j]);
              if (d<cost[j]) cost[j]=d;
           }
        }
        returnValues[0] = minimumDistanceToStart;
        returnValues[1] = total;
        //System.out.println("total MST:" + total);
        return returnValues;
	}
	
	private double kruskalTree(List<Integer> cities, int neighbourState, DistanceTable table) {
		HashMap<String,Double> map = new HashMap<String,Double>();
		ArrayList<Entry<String,Double>> sortedSet = new ArrayList<Entry<String,Double>>();
		for (int i = 1; i<table.listNodes().length; i++) {
			for (int j = i+1; j<=table.listNodes().length; j++) {
				double distance = Problem.getDistanceBetween(table, i,j);
				String s = String.valueOf(i) + " " + String.valueOf(j);
				map.put(s, distance);
			}
		}
		
		sortedSet.addAll(map.entrySet());
		
		final Comparator<Entry<String,Double>> valueComparator = new Comparator<Entry<String,Double>>() {
			@Override public int compare(Entry<String,Double> c1, Entry<String,Double> c2) { 
				return   c1.getValue().compareTo(c2.getValue());
			} 
		};

		Collections.sort(sortedSet,valueComparator);
		
		List<Integer> unvisitedNodes = new ArrayList<Integer>();
		int[] nodes = table.listNodes();
		Map<Integer, Integer> hash = new HashMap<Integer, Integer>();
		
		//get unvisited nodes
		for (int n : cities) {
			hash.put(n, n);
		}
		hash.put(neighbourState, neighbourState);
		for (int n : nodes) {
			if (!hash.containsKey(n)) {
				unvisitedNodes.add(n);
			}
		}
		
		double sum = 0;
		HashMap<Integer,Set<Integer>> forest = new HashMap<Integer,Set<Integer>>();
		for (Integer vertex : table.listNodes()) {
			Set<Integer> vs = new HashSet<Integer>();
			vs.add(vertex);
			forest.put(vertex, vs);
		}
		
		
		for (int i = 0; i<sortedSet.size(); i++) {
			Entry<String,Double> entry = sortedSet.get(i);
			System.out.println(entry);
			String[] data = entry.getKey().split(" ");
			int c1 = Integer.parseInt(data[0]);
			int c2 = Integer.parseInt(data[1]);
			
			Set<Integer> vs1 = forest.get(c1);
			Set<Integer> vs2 = forest.get(c2);
			
			if (!unvisitedNodes.contains(c1) || !unvisitedNodes.contains(c2)) continue;
			if (vs1.equals(vs2)) continue;
			sum+= entry.getValue();
			vs1.addAll(vs2);
			for (Integer tempValue : vs1) {
				forest.put(tempValue,vs1);
			}
			
			if (vs1.size() == unvisitedNodes.size()) break;
		}
		
		
		return sum;
	}
	
}
