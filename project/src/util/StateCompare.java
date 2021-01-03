package util;
import java.util.Comparator;

import tsp.State1;


/*
 * Compare class object will be used to sort a Priority Queue of State1 object 
 * based on the pathCost or total cost stored in each State. 
 *  
 * */
public class StateCompare implements Comparator<State1> {
	
	@Override
    public int compare(State1 n1, State1 n2){
		return Double.valueOf(n1.getFValue()).compareTo(n2.getFValue());
    }
	
}