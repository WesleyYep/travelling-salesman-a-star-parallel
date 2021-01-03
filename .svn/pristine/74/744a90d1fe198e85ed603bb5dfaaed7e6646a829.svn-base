package mst;

public class Path implements Comparable<Path> {
	
	private int distance;
	private City city1;
	private City city2;
	
	public Path(int dist, City c1, City c2) {
		distance = dist;
		city1 = c1;
		city2 = c2;
	}

	public City getFromCity() {
		// TODO Auto-generated method stub
		return city1;
	}

	public City getToCity() {
		// TODO Auto-generated method stub
		return city2;
	}

	public int getEdgeCost() {
		// TODO Auto-generated method stub
		return distance;
	}

	@Override
	public int compareTo(Path arg0) {
		
		return  this.distance - arg0.getEdgeCost();
	}

}
