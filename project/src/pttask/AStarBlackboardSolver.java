package pttask;//####[1]####
//####[1]####
import java.util.concurrent.PriorityBlockingQueue;//####[3]####
import java.util.PriorityQueue;//####[4]####
import org.moeaframework.problem.tsplib.DistanceTable;//####[5]####
import tsp.State1;//####[6]####
import pt.runtime.*;//####[7]####
import tsp.MainRunner;//####[8]####
import java.util.AbstractQueue;//####[9]####
import tsp.AbstractSolver;//####[10]####
import util.Problem;//####[11]####
import util.StateCompare;//####[12]####
import heuristic.Heuristic;//####[13]####
import java.util.concurrent.CountDownLatch;//####[14]####
//####[14]####
//-- ParaTask related imports//####[14]####
import pt.runtime.*;//####[14]####
import java.util.concurrent.ExecutionException;//####[14]####
import java.util.concurrent.locks.*;//####[14]####
import java.lang.reflect.*;//####[14]####
import pt.runtime.GuiThread;//####[14]####
import java.util.concurrent.BlockingQueue;//####[14]####
import java.util.ArrayList;//####[14]####
import java.util.List;//####[14]####
//####[14]####
public class AStarBlackboardSolver extends AbstractSolver {//####[16]####
    static{ParaTask.init();}//####[16]####
    /*  ParaTask helper method to access private/protected slots *///####[16]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[16]####
        if (m.getParameterTypes().length == 0)//####[16]####
            m.invoke(instance);//####[16]####
        else if ((m.getParameterTypes().length == 1))//####[16]####
            m.invoke(instance, arg);//####[16]####
        else //####[16]####
            m.invoke(instance, arg, interResult);//####[16]####
    }//####[16]####
//####[17]####
    private static final double TOLERANCE = 10.0;//####[17]####
//####[18]####
    private State1 finalState;//####[18]####
//####[20]####
    private CountDownLatch latch;//####[20]####
//####[22]####
    public AStarBlackboardSolver(Heuristic heuristic, DistanceTable table) {//####[22]####
        super(heuristic, table);//####[23]####
        latch = new CountDownLatch(Runtime.getRuntime().availableProcessors());//####[25]####
    }//####[26]####
//####[28]####
    public State1 search() {//####[28]####
        PriorityBlockingQueue<State1> blackboard = new PriorityBlockingQueue<State1>(1, new StateCompare());//####[30]####
        blackboard.add(new State1(initialAndGoal, table.listNodes().length));//####[31]####
        expand(blackboard, null, false);//####[32]####
        TaskIDGroup task = loop(blackboard);//####[35]####
        try {//####[36]####
            task.waitTillFinished();//####[37]####
        } catch (Exception e) {//####[38]####
            e.printStackTrace();//####[38]####
        }//####[38]####
        return finalState;//####[39]####
    }//####[40]####
//####[45]####
    private static volatile Method __pt__loop_PriorityBlockingQueueState1_method = null;//####[45]####
    private synchronized static void __pt__loop_PriorityBlockingQueueState1_ensureMethodVarSet() {//####[45]####
        if (__pt__loop_PriorityBlockingQueueState1_method == null) {//####[45]####
            try {//####[45]####
                __pt__loop_PriorityBlockingQueueState1_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__loop", new Class[] {//####[45]####
                    PriorityBlockingQueue.class//####[45]####
                });//####[45]####
            } catch (Exception e) {//####[45]####
                e.printStackTrace();//####[45]####
            }//####[45]####
        }//####[45]####
    }//####[45]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[45]####
    public TaskIDGroup<Void> loop(PriorityBlockingQueue<State1> blackboard) {//####[45]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[45]####
        return loop(blackboard, new TaskInfo());//####[45]####
    }//####[45]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[45]####
    public TaskIDGroup<Void> loop(PriorityBlockingQueue<State1> blackboard, TaskInfo taskinfo) {//####[45]####
        // ensure Method variable is set//####[45]####
        if (__pt__loop_PriorityBlockingQueueState1_method == null) {//####[45]####
            __pt__loop_PriorityBlockingQueueState1_ensureMethodVarSet();//####[45]####
        }//####[45]####
        taskinfo.setParameters(blackboard);//####[45]####
        taskinfo.setMethod(__pt__loop_PriorityBlockingQueueState1_method);//####[45]####
        taskinfo.setInstance(this);//####[45]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[45]####
    }//####[45]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[45]####
    public TaskIDGroup<Void> loop(TaskID<PriorityBlockingQueue<State1>> blackboard) {//####[45]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[45]####
        return loop(blackboard, new TaskInfo());//####[45]####
    }//####[45]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[45]####
    public TaskIDGroup<Void> loop(TaskID<PriorityBlockingQueue<State1>> blackboard, TaskInfo taskinfo) {//####[45]####
        // ensure Method variable is set//####[45]####
        if (__pt__loop_PriorityBlockingQueueState1_method == null) {//####[45]####
            __pt__loop_PriorityBlockingQueueState1_ensureMethodVarSet();//####[45]####
        }//####[45]####
        taskinfo.setTaskIdArgIndexes(0);//####[45]####
        taskinfo.addDependsOn(blackboard);//####[45]####
        taskinfo.setParameters(blackboard);//####[45]####
        taskinfo.setMethod(__pt__loop_PriorityBlockingQueueState1_method);//####[45]####
        taskinfo.setInstance(this);//####[45]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[45]####
    }//####[45]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[45]####
    public TaskIDGroup<Void> loop(BlockingQueue<PriorityBlockingQueue<State1>> blackboard) {//####[45]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[45]####
        return loop(blackboard, new TaskInfo());//####[45]####
    }//####[45]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[45]####
    public TaskIDGroup<Void> loop(BlockingQueue<PriorityBlockingQueue<State1>> blackboard, TaskInfo taskinfo) {//####[45]####
        // ensure Method variable is set//####[45]####
        if (__pt__loop_PriorityBlockingQueueState1_method == null) {//####[45]####
            __pt__loop_PriorityBlockingQueueState1_ensureMethodVarSet();//####[45]####
        }//####[45]####
        taskinfo.setQueueArgIndexes(0);//####[45]####
        taskinfo.setIsPipeline(true);//####[45]####
        taskinfo.setParameters(blackboard);//####[45]####
        taskinfo.setMethod(__pt__loop_PriorityBlockingQueueState1_method);//####[45]####
        taskinfo.setInstance(this);//####[45]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[45]####
    }//####[45]####
    /**
	 * This keeps calling expanding nodes until we find the solution
	 *///####[45]####
    public void __pt__loop(PriorityBlockingQueue<State1> blackboard) {//####[45]####
        PriorityQueue<State1> frontier = new PriorityQueue<State1>(1, new StateCompare());//####[46]####
        try {//####[47]####
            frontier.add(blackboard.poll());//####[48]####
        } catch (NullPointerException e) {//####[49]####
        }//####[49]####
        int iteration = 0;//####[50]####
        boolean localFlag = false;//####[51]####
        while (latch.getCount() != 0) //####[53]####
        {//####[53]####
            iteration++;//####[54]####
            localFlag = expand(frontier, blackboard, localFlag);//####[55]####
        }//####[56]####
    }//####[57]####
//####[57]####
//####[63]####
    /**
	 * This method expands a single node at the front of the priority queue
	 * return: true if thread should still be searching, otherwise false
	 *///####[63]####
    public boolean expand(AbstractQueue<State1> frontier, PriorityBlockingQueue<State1> blackboard, boolean localFlag) {//####[63]####
        State1 currentState;//####[64]####
        currentState = frontier.poll();//####[66]####
        if (currentState == null) //####[67]####
        {//####[67]####
            if (finalState != null && blackboard.isEmpty()) //####[68]####
            {//####[68]####
                latch.countDown();//####[69]####
                localFlag = true;//####[70]####
            }//####[71]####
            return localFlag;//####[72]####
        }//####[73]####
        if (blackboard != null) //####[75]####
        {//####[75]####
            State1 blackboardBest = blackboard.peek();//####[76]####
            if ((blackboard != null && blackboardBest != null) && (finalState != null || currentState.getFValue() > blackboardBest.getFValue() + TOLERANCE)) //####[77]####
            {//####[79]####
                for (int i = 0; i < 5; i++) //####[81]####
                {//####[81]####
                    State1 transferState = blackboard.poll();//####[82]####
                    if (transferState != null) //####[83]####
                    {//####[83]####
                        frontier.add(transferState);//####[84]####
                    }//####[85]####
                }//####[86]####
            } else if (finalState == null && blackboardBest != null && currentState != null && currentState.getFValue() < blackboardBest.getFValue() - TOLERANCE) //####[87]####
            {//####[88]####
                for (int i = 0; i < 5 && i < frontier.size(); i++) //####[90]####
                {//####[90]####
                    blackboard.add(frontier.poll());//####[91]####
                }//####[92]####
            }//####[93]####
        }//####[94]####
        if (finalState != null && !blackboard.isEmpty()) //####[96]####
        {//####[96]####
            return localFlag;//####[97]####
        }//####[98]####
        if (currentState.getCities().size() == table.listNodes().length + 1) //####[101]####
        {//####[101]####
            if (finalState == null || currentState.getCurrentTourDistance() < finalState.getCurrentTourDistance()) //####[102]####
            {//####[102]####
                foundGoalState(currentState);//####[103]####
            }//####[104]####
        } else if (currentState.getCities().size() == table.listNodes().length) //####[105]####
        {//####[105]####
            double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), initialAndGoal);//####[107]####
            frontier.add(new State1(currentState, initialAndGoal, g, 0));//####[108]####
        }//####[109]####
        for (int s : table.getNeighborsOf(currentState.getCurrentCity())) //####[111]####
        {//####[111]####
            if (currentState.pathContains(s)) //####[112]####
            {//####[112]####
                continue;//####[113]####
            }//####[114]####
            double g = currentState.getCurrentTourDistance() + Problem.getDistanceBetween(table, currentState.getCurrentCity(), s);//####[116]####
            double h = heuristic.heuristicCost(s, table, currentState.getCities(), currentState);//####[119]####
            double fValue = g + h;//####[121]####
            frontier.add(new State1(currentState, s, g, fValue));//####[122]####
        }//####[123]####
        if (blackboard != null) //####[125]####
        {//####[125]####
            notifyObservers(currentState, CurrentTask.relativeID());//####[126]####
        }//####[127]####
        if (finalState != null && !localFlag && currentState.getFValue() > finalState.getCurrentTourDistance()) //####[130]####
        {//####[130]####
            latch.countDown();//####[131]####
            localFlag = true;//####[132]####
        }//####[133]####
        return localFlag;//####[134]####
    }//####[135]####
//####[137]####
    public void foundGoalState(State1 newFinalState) {//####[137]####
        finalState = newFinalState;//####[138]####
    }//####[139]####
}//####[139]####
