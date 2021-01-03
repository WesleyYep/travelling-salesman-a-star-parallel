package pttask;//####[1]####
//####[1]####
import java.util.concurrent.PriorityBlockingQueue;//####[3]####
import java.util.concurrent.CountDownLatch;//####[4]####
import org.moeaframework.problem.tsplib.DistanceTable;//####[5]####
import tsp.State1;//####[6]####
import util.StateCompare;//####[7]####
import tsp.AbstractSolver;//####[8]####
import heuristic.Heuristic;//####[9]####
import pt.runtime.*;//####[10]####
import util.Problem;//####[11]####
//####[11]####
//-- ParaTask related imports//####[11]####
import pt.runtime.*;//####[11]####
import java.util.concurrent.ExecutionException;//####[11]####
import java.util.concurrent.locks.*;//####[11]####
import java.lang.reflect.*;//####[11]####
import pt.runtime.GuiThread;//####[11]####
import java.util.concurrent.BlockingQueue;//####[11]####
import java.util.ArrayList;//####[11]####
import java.util.List;//####[11]####
//####[11]####
public class AStarSolverParallel extends AbstractSolver {//####[13]####
    static{ParaTask.init();}//####[13]####
    /*  ParaTask helper method to access private/protected slots *///####[13]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[13]####
        if (m.getParameterTypes().length == 0)//####[13]####
            m.invoke(instance);//####[13]####
        else if ((m.getParameterTypes().length == 1))//####[13]####
            m.invoke(instance, arg);//####[13]####
        else //####[13]####
            m.invoke(instance, arg, interResult);//####[13]####
    }//####[13]####
//####[15]####
    private State1 finalState;//####[15]####
//####[16]####
    private PriorityBlockingQueue<State1> frontier;//####[16]####
//####[18]####
    private CountDownLatch latch;//####[18]####
//####[20]####
    public AStarSolverParallel(Heuristic heuristic, DistanceTable table) {//####[20]####
        super(heuristic, table);//####[21]####
        latch = new CountDownLatch(Runtime.getRuntime().availableProcessors());//####[23]####
    }//####[24]####
//####[26]####
    public State1 search() {//####[26]####
        frontier = new PriorityBlockingQueue<State1>(1, new StateCompare());//####[27]####
        frontier.add(new State1(initialAndGoal, table.listNodes().length));//####[28]####
        TaskIDGroup task = loop();//####[31]####
        try {//####[32]####
            task.waitTillFinished();//####[33]####
        } catch (Exception e) {//####[34]####
            e.printStackTrace();//####[34]####
        }//####[34]####
        return finalState;//####[36]####
    }//####[37]####
//####[42]####
    private static volatile Method __pt__loop__method = null;//####[42]####
    private synchronized static void __pt__loop__ensureMethodVarSet() {//####[42]####
        if (__pt__loop__method == null) {//####[42]####
            try {//####[42]####
                __pt__loop__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__loop", new Class[] {//####[42]####
                    //####[42]####
                });//####[42]####
            } catch (Exception e) {//####[42]####
                e.printStackTrace();//####[42]####
            }//####[42]####
        }//####[42]####
    }//####[42]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[42]####
    public TaskIDGroup<Void> loop() {//####[42]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[42]####
        return loop(new TaskInfo());//####[42]####
    }//####[42]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[42]####
    public TaskIDGroup<Void> loop(TaskInfo taskinfo) {//####[42]####
        // ensure Method variable is set//####[42]####
        if (__pt__loop__method == null) {//####[42]####
            __pt__loop__ensureMethodVarSet();//####[42]####
        }//####[42]####
        taskinfo.setParameters();//####[42]####
        taskinfo.setMethod(__pt__loop__method);//####[42]####
        taskinfo.setInstance(this);//####[42]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[42]####
    }//####[42]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[42]####
    public void __pt__loop() {//####[42]####
        int iteration = 0;//####[43]####
        boolean localFlag = false;//####[44]####
        while (latch.getCount() != 0) //####[46]####
        {//####[46]####
            iteration++;//####[47]####
            localFlag = expand(localFlag);//####[48]####
        }//####[50]####
    }//####[52]####
//####[52]####
//####[58]####
    /**
	 * This method expands a single node at the front of the priority queue
	 * return: true if thread should still be searching, otherwise false
	 *///####[58]####
    public boolean expand(boolean localFlag) {//####[58]####
        State1 currentState;//####[59]####
        currentState = frontier.poll();//####[61]####
        if (currentState == null) //####[62]####
        {//####[62]####
            return localFlag;//####[64]####
        }//####[65]####
        if (currentState.getCities().size() == table.listNodes().length + 1) //####[67]####
        {//####[67]####
            if (finalState == null || currentState.getCurrentTourDistance() < finalState.getCurrentTourDistance()) //####[68]####
            {//####[68]####
                foundGoalState(currentState);//####[69]####
            }//####[70]####
        } else if (currentState.getCities().size() == table.listNodes().length) //####[71]####
        {//####[71]####
            double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), initialAndGoal);//####[73]####
            frontier.add(new State1(currentState, initialAndGoal, g, 0));//####[74]####
        }//####[75]####
        for (int s : table.getNeighborsOf(currentState.getCurrentCity())) //####[77]####
        {//####[77]####
            if (currentState.pathContains(s)) //####[78]####
            {//####[78]####
                continue;//####[79]####
            }//####[80]####
            double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), s);//####[82]####
            double h = heuristic.heuristicCost(s, table, currentState.getCities(), currentState);//####[85]####
            double fValue = g + h;//####[87]####
            frontier.add(new State1(currentState, s, g, fValue));//####[88]####
        }//####[89]####
        notifyObservers(currentState, CurrentTask.relativeID());//####[91]####
        if (finalState != null && !localFlag && currentState.getFValue() > finalState.getCurrentTourDistance()) //####[93]####
        {//####[93]####
            latch.countDown();//####[94]####
            localFlag = true;//####[95]####
        }//####[96]####
        return localFlag;//####[97]####
    }//####[98]####
//####[100]####
    public void foundGoalState(State1 newFinalState) {//####[100]####
        finalState = newFinalState;//####[101]####
    }//####[102]####
}//####[102]####
