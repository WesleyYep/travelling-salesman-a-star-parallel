package pttask;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.Arrays;//####[4]####
import java.util.HashMap;//####[5]####
import java.util.List;//####[6]####
import java.util.Map;//####[7]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[8]####
import java.util.concurrent.atomic.AtomicBoolean;//####[9]####
import org.moeaframework.problem.tsplib.DistanceTable;//####[11]####
import org.moeaframework.problem.tsplib.TSPPanel;//####[12]####
import org.moeaframework.problem.tsplib.Node;//####[13]####
import org.moeaframework.problem.tsplib.NodeCoordinates;//####[14]####
import pt.runtime.CurrentTask;//####[16]####
import pt.runtime.TaskID;//####[17]####
import pt.runtime.TaskIDGroup;//####[18]####
import pu.RedLib.Reduction;//####[19]####
import tsp.State1;//####[20]####
import tsp.Solver;//####[21]####
import tsp.MainRunner;//####[22]####
import tsp.AbstractSolver;//####[24]####
import heuristic.Heuristic;//####[25]####
import util.StateCompare;//####[26]####
import util.Problem;//####[27]####
//####[27]####
//-- ParaTask related imports//####[27]####
import pt.runtime.*;//####[27]####
import java.util.concurrent.ExecutionException;//####[27]####
import java.util.concurrent.locks.*;//####[27]####
import java.lang.reflect.*;//####[27]####
import pt.runtime.GuiThread;//####[27]####
import java.util.concurrent.BlockingQueue;//####[27]####
import java.util.ArrayList;//####[27]####
import java.util.List;//####[27]####
//####[27]####
/**
 * Algorithm based on Parallel multithreaded IDA* heuristic search: algorithm design and performance evaluation article
 *///####[31]####
public class IDAStarSolverParallel extends AbstractSolver {//####[32]####
    static{ParaTask.init();}//####[32]####
    /*  ParaTask helper method to access private/protected slots *///####[32]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[32]####
        if (m.getParameterTypes().length == 0)//####[32]####
            m.invoke(instance);//####[32]####
        else if ((m.getParameterTypes().length == 1))//####[32]####
            m.invoke(instance, arg);//####[32]####
        else //####[32]####
            m.invoke(instance, arg, interResult);//####[32]####
    }//####[32]####
//####[34]####
    private AtomicBoolean finishFlag;//####[34]####
//####[35]####
    private double bound;//####[35]####
//####[36]####
    private State1 finalState;//####[36]####
//####[38]####
    private long lowestThreadId = Long.MAX_VALUE;//####[38]####
//####[40]####
    public IDAStarSolverParallel(Heuristic heuristic, DistanceTable table) {//####[40]####
        super(heuristic, table);//####[41]####
        finishFlag = new AtomicBoolean();//####[42]####
        finishFlag.set(false);//####[43]####
        bound = 0;//####[44]####
    }//####[45]####
//####[47]####
    public State1 search() {//####[47]####
        ConcurrentLinkedQueue<State1> queue = generateQueue(table);//####[52]####
        while (!finishFlag.get()) //####[56]####
        {//####[56]####
            TaskIDGroup<Double> g = new TaskIDGroup<Double>(queue.size());//####[57]####
            for (State1 s : queue) //####[59]####
            {//####[59]####
                TaskID<Double> id = parallelIDAStar(s, table);//####[60]####
                g.add(id);//####[61]####
            }//####[62]####
            try {//####[63]####
                g.waitTillFinished();//####[64]####
                bound = g.getReturnResult(Reduction.DoubleMIN);//####[65]####
            } catch (ExecutionException e) {//####[66]####
                e.printStackTrace();//####[67]####
            } catch (InterruptedException e) {//####[68]####
                e.printStackTrace();//####[69]####
            }//####[70]####
        }//####[72]####
        return finalState;//####[73]####
    }//####[74]####
//####[76]####
    private ConcurrentLinkedQueue<State1> generateQueue(DistanceTable table) {//####[76]####
        ConcurrentLinkedQueue<State1> result = new ConcurrentLinkedQueue<State1>();//####[77]####
        int initialAndGoal = table.listNodes()[0];//####[78]####
        State1 rootState = new State1(initialAndGoal, table.listNodes().length);//####[79]####
        result.offer(rootState);//####[80]####
        double maxH = Double.MAX_VALUE;//####[81]####
        int iteration = 1;//####[84]####
        int depth = 1;//####[85]####
        int permutation = table.listNodes().length - 1;//####[86]####
        int temp = 1;//####[87]####
        for (int i = 0; i < depth; i++) //####[88]####
        {//####[88]####
            temp *= (permutation - i);//####[89]####
            iteration += temp;//####[90]####
        }//####[92]####
        for (int i = 0; i < iteration; i++) //####[96]####
        {//####[96]####
            State1 currentState = result.poll();//####[97]####
            for (int s : table.getNeighborsOf(currentState.getCurrentCity())) //####[99]####
            {//####[99]####
                if (currentState.pathContains(s)) //####[100]####
                {//####[100]####
                    continue;//####[101]####
                }//####[102]####
                double g = currentState.getCurrentTourDistance() + Math.round(Problem.getDistanceBetween(table, currentState.getCurrentCity(), s));//####[103]####
                double h = Math.round((heuristic.heuristicCost(s, table, currentState.getCities(), currentState)));//####[104]####
                if (h < maxH) //####[106]####
                maxH = h;//####[106]####
                result.offer(new State1(currentState, s, g, 0));//####[108]####
            }//####[109]####
        }//####[110]####
        bound = maxH;//####[111]####
        return result;//####[113]####
    }//####[114]####
//####[119]####
    private static volatile Method __pt__parallelIDAStar_State1_DistanceTable_method = null;//####[119]####
    private synchronized static void __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet() {//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            try {//####[119]####
                __pt__parallelIDAStar_State1_DistanceTable_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__parallelIDAStar", new Class[] {//####[119]####
                    State1.class, DistanceTable.class//####[119]####
                });//####[119]####
            } catch (Exception e) {//####[119]####
                e.printStackTrace();//####[119]####
            }//####[119]####
        }//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(State1 startState, DistanceTable table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(State1 startState, DistanceTable table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(TaskID<State1> startState, DistanceTable table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(TaskID<State1> startState, DistanceTable table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setTaskIdArgIndexes(0);//####[119]####
        taskinfo.addDependsOn(startState);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(BlockingQueue<State1> startState, DistanceTable table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(BlockingQueue<State1> startState, DistanceTable table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setQueueArgIndexes(0);//####[119]####
        taskinfo.setIsPipeline(true);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(State1 startState, TaskID<DistanceTable> table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(State1 startState, TaskID<DistanceTable> table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setTaskIdArgIndexes(1);//####[119]####
        taskinfo.addDependsOn(table);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(TaskID<State1> startState, TaskID<DistanceTable> table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(TaskID<State1> startState, TaskID<DistanceTable> table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[119]####
        taskinfo.addDependsOn(startState);//####[119]####
        taskinfo.addDependsOn(table);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(BlockingQueue<State1> startState, TaskID<DistanceTable> table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(BlockingQueue<State1> startState, TaskID<DistanceTable> table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setQueueArgIndexes(0);//####[119]####
        taskinfo.setIsPipeline(true);//####[119]####
        taskinfo.setTaskIdArgIndexes(1);//####[119]####
        taskinfo.addDependsOn(table);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(State1 startState, BlockingQueue<DistanceTable> table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(State1 startState, BlockingQueue<DistanceTable> table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setQueueArgIndexes(1);//####[119]####
        taskinfo.setIsPipeline(true);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(TaskID<State1> startState, BlockingQueue<DistanceTable> table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(TaskID<State1> startState, BlockingQueue<DistanceTable> table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setQueueArgIndexes(1);//####[119]####
        taskinfo.setIsPipeline(true);//####[119]####
        taskinfo.setTaskIdArgIndexes(0);//####[119]####
        taskinfo.addDependsOn(startState);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(BlockingQueue<State1> startState, BlockingQueue<DistanceTable> table) {//####[119]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[119]####
        return parallelIDAStar(startState, table, new TaskInfo());//####[119]####
    }//####[119]####
    private TaskID<Double> parallelIDAStar(BlockingQueue<State1> startState, BlockingQueue<DistanceTable> table, TaskInfo taskinfo) {//####[119]####
        // ensure Method variable is set//####[119]####
        if (__pt__parallelIDAStar_State1_DistanceTable_method == null) {//####[119]####
            __pt__parallelIDAStar_State1_DistanceTable_ensureMethodVarSet();//####[119]####
        }//####[119]####
        taskinfo.setQueueArgIndexes(0, 1);//####[119]####
        taskinfo.setIsPipeline(true);//####[119]####
        taskinfo.setParameters(startState, table);//####[119]####
        taskinfo.setMethod(__pt__parallelIDAStar_State1_DistanceTable_method);//####[119]####
        taskinfo.setInstance(this);//####[119]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[119]####
    }//####[119]####
    public double __pt__parallelIDAStar(State1 startState, DistanceTable table) {//####[119]####
        if (Thread.currentThread().getId() < lowestThreadId) //####[120]####
        {//####[120]####
            lowestThreadId = Thread.currentThread().getId();//####[121]####
        }//####[122]####
        double t = search(startState, startState.getCurrentTourDistance(), bound, table);//####[124]####
        if (t == -1) //####[126]####
        {//####[126]####
            finishFlag.compareAndSet(false, true);//####[127]####
        }//####[128]####
        if (t == Double.MAX_VALUE) //####[130]####
        {//####[130]####
            finishFlag.compareAndSet(false, true);//####[131]####
        }//####[132]####
        return t;//####[133]####
    }//####[134]####
//####[134]####
//####[139]####
    private double search(State1 state, double g, double bound, DistanceTable table) {//####[139]####
        double f = g + (int) heuristic.heuristicCost(state.getCurrentCity(), table, state.getCities(), state);//####[140]####
        State1 currentState = state;//####[141]####
        if (f > bound) //####[142]####
        return f;//####[142]####
        if (currentState.getCities().size() == table.listNodes().length) //####[144]####
        {//####[144]####
            int initialAndGoal = table.listNodes()[0];//####[145]####
            currentState = new State1(currentState, initialAndGoal, currentState.getCurrentTourDistance() + Math.round(Problem.getDistanceBetween(table, currentState.getCurrentCity(), initialAndGoal)), 0);//####[146]####
            List<Integer> result = currentState.getCities();//####[148]####
            finalState = currentState;//####[149]####
            return -1.0;//####[150]####
        }//####[151]####
        double min = Double.MAX_VALUE;//####[152]####
        for (int s : table.getNeighborsOf(currentState.getCurrentCity())) //####[154]####
        {//####[154]####
            if (currentState.pathContains(s)) //####[155]####
            {//####[155]####
                continue;//####[156]####
            }//####[157]####
            double currentDistance = currentState.getCurrentTourDistance() + Math.round(Problem.getDistanceBetween(table, currentState.getCurrentCity(), s));//####[158]####
            State1 tempState = new State1(currentState, s, currentDistance, 0);//####[159]####
            notifyObservers(tempState, (int) (Thread.currentThread().getId() - lowestThreadId));//####[160]####
            double cost = Math.round(Problem.getDistanceBetween(table, currentState.getCurrentCity(), s));//####[161]####
            double t = search(tempState, g + cost, bound, table);//####[162]####
            if (t == -1.0) //####[163]####
            return -1.0;//####[163]####
            if (t < min) //####[164]####
            min = t;//####[164]####
        }//####[165]####
        return min;//####[166]####
    }//####[167]####
}//####[167]####
