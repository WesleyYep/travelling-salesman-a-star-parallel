package mst;
import java.util.ArrayList;
import java.util.List;


public class State {
	
	private ArrayList<City> path;
	private ArrayList<City> unseenList;
	
	public State() {
		path = new ArrayList<City>();
		unseenList = new ArrayList<City>();
	}
	
	public List<City> getPath() {
		return path;
	}
	
	public void addPath(City c) {
		path.add(c);
	}
	
	public void addPath(List<City> cities) {
		path.addAll(cities);
	}
	
	public void addUnseenList(City c) {
		unseenList.add(c);
	}
	
	public void addUnseenList(List<City> list) {
		unseenList.addAll(list);
	}
	
	public City getCurrentCity() {
		if (path.size() > 0) {
			return path.get(path.size()-1);
		} else {
			return null;
		}
		
	}
	
	public int getPathLength() {
		return path.size();
	}

	public List<State> getSuccessor() {
		ArrayList<State> tempList = new ArrayList<State>();
		
		for (City neighbour : getCurrentCity().getNeighbour()) {
			//skip if its already in the path
			if (path.contains(neighbour) && !neighbour.equals(path.get(0))) continue;
			
			State newState = new State();
			
			//update the path for successor state
			newState.addPath(this.path);
			newState.addPath(neighbour);
			
			//update unseenlist for successor state
			for (City c : unseenList) {
				if (c != neighbour) {
					newState.addUnseenList(c);
				}
			}
			
			tempList.add(newState);
		}
		
		return tempList;
	}

	public int getNearestUnvisitedCityCost(List<City> cityList,
			int[][] edgeCostList) {
		int min = Integer.MAX_VALUE;
		for (City neighbour : getCurrentCity().getNeighbour()) {
			int cost = edgeCostList[cityList.indexOf(getCurrentCity())][cityList.indexOf(neighbour)];
			
			if (cost < min) {
				min = cost;
			}
		}
		return min;
	}

	public int getNearestStartCityCost(List<City> cityList, int[][] edgeCostList) {
		int min = Integer.MAX_VALUE;
		for (City unseen : unseenList) {
			int cost = edgeCostList[cityList.indexOf(unseen)][cityList.indexOf(path.get(0))];
			
			if (cost < min) {
				min = cost;
			}
		}
		
		return min;
	}

	public int getSpanningTreeCost(List<City> cityList, List<Path> edgeList) {
		//assumed edgeList is always sorted
		List<City> tempList = new ArrayList<City>();
		tempList.addAll(unseenList);
		int sum = 0;
		
		if (tempList.size() == 0) {
			return Integer.MAX_VALUE;
		}
		for (Path p : edgeList) {
			if (tempList.contains(p.getFromCity()) && tempList.contains(p.getToCity()) ){
				tempList.remove(p.getFromCity());
				tempList.remove(p.getToCity());
				
				sum += p.getEdgeCost();
			} else if (tempList.contains(p.getFromCity())) {
				tempList.remove(p.getFromCity());
				sum += p.getEdgeCost();
			} else if (tempList.contains(p.getToCity())) {
				tempList.remove(p.getToCity());
				sum += p.getEdgeCost();
			}
			
			if (tempList.size() == 0) {
				break;
			}
		}
		
		return sum;
	}
	
	


	
	
}
