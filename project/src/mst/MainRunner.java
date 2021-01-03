package mst;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class MainRunner {

	

	public static void main(String[] args) {
		State currentState = new State();
		City a = new City("a");
		City b = new City("b");
		City c = new City("c");
		City d = new City("d");
		City e = new City("e");
		
		a.addNeighbour(e);
		a.addNeighbour(b);
		a.addNeighbour(d);
		
		b.addNeighbour(a);
		b.addNeighbour(d);
		b.addNeighbour(c);
		
		c.addNeighbour(b);
		c.addNeighbour(d);
		
		d.addNeighbour(a);
		d.addNeighbour(b);
		d.addNeighbour(e);
		d.addNeighbour(c);
		
		e.addNeighbour(a);
		e.addNeighbour(c);
		e.addNeighbour(d);
		
		currentState.addPath(a);
		currentState.addUnseenList(b);
		currentState.addUnseenList(c);
		currentState.addUnseenList(d);
		currentState.addUnseenList(e);
		
	//	int numberOfCity = 5;
		
		//ArrayList<State> openList = new ArrayList<State>();
		ArrayList<City> cityList = new ArrayList<City>();
		ArrayList<Path> edgeList = new ArrayList<Path>();
		
		cityList.add(a);
		cityList.add(b);
		cityList.add(c);
		cityList.add(d);
		cityList.add(e);
		
		Path p1 = new Path(5,a,e);
		Path p2 = new Path(12,a,d);
		Path p3 = new Path(2,a,b);
		Path p4 = new Path(4,b,c);
		Path p5 = new Path(8,b,d);
		Path p6 = new Path(3,c,d);
		Path p7 = new Path(3,c,e);
		Path p8 = new Path(10,d,e);
		
		edgeList.add(p1);
		edgeList.add(p2);
		edgeList.add(p3);
		edgeList.add(p4);
		edgeList.add(p5);
		edgeList.add(p6);
		edgeList.add(p7);
		edgeList.add(p8);
		
		solveAStar(currentState, cityList, edgeList);
		
		
	}
	
	private static void solveAStar(State currentState, List<City> cityList, List<Path> edgeList) {
		City goalCity = cityList.get(0);
		int numberOfCity = cityList.size();
		HashMap<State,Integer> fValueMap = new HashMap<State,Integer>();
		int[][] edgeCostList = new int[numberOfCity][numberOfCity];
		int max = Integer.MAX_VALUE;
		
		for (int i = 0; i<numberOfCity; i++) {
			for (int j = 0; j<numberOfCity; j++) {
				edgeCostList[i][j] = max;
			}
		}
		
		
		for (Path p : edgeList) {
			int i = cityList.indexOf(p.getFromCity());
			int j = cityList.indexOf(p.getToCity());
			
			edgeCostList[i][j] = p.getEdgeCost();
			edgeCostList[j][i] = p.getEdgeCost();
		}
		
		
		Collections.sort(edgeList);
		int iteration = 0;
		//keep searching until the current state reach goal state.
		while (!(currentState.getCurrentCity().equals(goalCity) && currentState.getPathLength() == numberOfCity+1)) {
			System.out.println("Itreation:" + iteration);
			for (State s : currentState.getSuccessor()) {
				
				//evaluate the g value of the state
				int g = getStartToCurrentCost(s,cityList,edgeCostList);
				
				//evaluate the h value of the state
				int h = heuristicCost(s,cityList,edgeCostList,edgeList);
				
				//store the f value in the map
				int fValue = g + h;
				fValueMap.put(s, fValue);
			}
			System.out.print("Current State: ");
			for (City n : currentState.getPath()) {
				System.out.print(n.getName() + " ");
			}
			System.out.println();
 			
			//finding the best f value in the open list and set is as current.
			int minimum = Integer.MAX_VALUE;
			State minimumState = null;
			int numberOfState = 0;
			for (Entry<State, Integer> entry : fValueMap.entrySet()) {
				System.out.print("State " + numberOfState + ":");
				for (City n : entry.getKey().getPath()) {
					System.out.print(n + " ");
				}
				System.out.println("fValue:" + entry.getValue());
				if (entry.getValue() < minimum) {
					minimum = entry.getValue();
					minimumState = entry.getKey();
				};
				numberOfState++;
			}
			fValueMap.remove(minimumState,minimum);
			currentState = minimumState;
			System.out.print("new Current State: ");
			for (City n : currentState.getPath()) {
				System.out.print(n.getName() + " ");
			}
			System.out.println();
			iteration++;
		}
		List<City> result = currentState.getPath();
		
		for (City city : result) {
			System.out.println(city.getName());
		}
		
	}

	public static int heuristicCost(State s, List<City> cityList, int[][] edgeCostList, List<Path> edgeList) {
		int a = s.getNearestUnvisitedCityCost(cityList,edgeCostList);
		int b = s.getSpanningTreeCost(cityList,edgeList);
		int c = s.getNearestStartCityCost(cityList,edgeCostList);
		
		return a + b + c;
	}
	
	public static int getStartToCurrentCost(State s, List<City> cityList, int[][] edgeCostList) {
		List<City> path =  s.getPath();
		int sum = 0;
		for (int i = 0; i<path.size()-1; i++) {
			City c1 = path.get(i);
			City c2 = path.get(i+1);
			sum += edgeCostList[cityList.indexOf(c1)][cityList.indexOf(c2)];
		}
		
		return sum;
	}

}
