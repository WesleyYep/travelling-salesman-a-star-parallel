package mst;
import java.util.ArrayList;
import java.util.List;


public class City {
	private String name;
	private List<City> neighbours;
	
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((neighbours == null) ? 0 : neighbours.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (neighbours == null) {
			if (other.neighbours != null)
				return false;
		} else if (!neighbours.equals(other.neighbours))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "City [name=" + name + "]";
	}

	public City(String n) {
		name = n;
		neighbours = new ArrayList<City>();
	}
	
	public void addNeighbour(City n) {
		neighbours.add(n);
	}

	public List<City> getNeighbour() {
		return neighbours;
	}
}
