package pttask;//####[1]####
//####[1]####
import java.util.PriorityQueue;//####[3]####
import java.util.concurrent.atomic.AtomicBoolean;//####[4]####
import java.util.concurrent.ConcurrentHashMap;//####[5]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[6]####
import util.StateCompare;//####[7]####
import tsp.State1;//####[8]####
import util.Channel;//####[9]####
import util.Problem;//####[10]####
import java.util.List;//####[11]####
import java.util.Map;//####[12]####
import java.util.Queue;//####[13]####
import org.moeaframework.problem.tsplib.DistanceTable;//####[15]####
import pt.runtime.CurrentTask;//####[17]####
import pt.runtime.TaskID;//####[18]####
import pt.runtime.TaskIDGroup;//####[19]####
import heuristic.Heuristic;//####[21]####
import tsp.AbstractSolver;//####[22]####
import java.util.concurrent.CountDownLatch;//####[23]####
import java.util.concurrent.BrokenBarrierException;//####[24]####
//####[24]####
//-- ParaTask related imports//####[24]####
import pt.runtime.*;//####[24]####
import java.util.concurrent.ExecutionException;//####[24]####
import java.util.concurrent.locks.*;//####[24]####
import java.lang.reflect.*;//####[24]####
import pt.runtime.GuiThread;//####[24]####
import java.util.concurrent.BlockingQueue;//####[24]####
import java.util.ArrayList;//####[24]####
import java.util.List;//####[24]####
//####[24]####
/**
 * Hash Distributed A*
 * @author Chang Kon Han
 * @author Wesley Yep
 * @author John Law 
 *///####[30]####
public class AStarHDA extends AbstractSolver {//####[32]####
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
//####[35]####
    private Map<Long, Channel<State1>> channels = new ConcurrentHashMap<Long, Channel<State1>>();//####[35]####
//####[36]####
    private Queue<Thread> threads = new ConcurrentLinkedQueue<Thread>();//####[36]####
//####[38]####
    private State1 finalState;//####[38]####
//####[40]####
    private AtomicBoolean discovered = new AtomicBoolean(false);//####[40]####
//####[41]####
    private AtomicBoolean initialised = new AtomicBoolean(false);//####[41]####
//####[42]####
    private CountDownLatch latch = new CountDownLatch(Runtime.getRuntime().availableProcessors());//####[42]####
//####[44]####
    public AStarHDA(Heuristic heuristic, DistanceTable table) {//####[44]####
        super(heuristic, table);//####[45]####
    }//####[46]####
//####[48]####
    public State1 search() {//####[48]####
        TaskIDGroup taskGroup = loop();//####[49]####
        try {//####[51]####
            taskGroup.waitTillFinished();//####[52]####
        } catch (Exception e) {//####[53]####
        }//####[55]####
        return finalState;//####[56]####
    }//####[57]####
//####[59]####
    private static volatile Method __pt__loop__method = null;//####[59]####
    private synchronized static void __pt__loop__ensureMethodVarSet() {//####[59]####
        if (__pt__loop__method == null) {//####[59]####
            try {//####[59]####
                __pt__loop__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__loop", new Class[] {//####[59]####
                    //####[59]####
                });//####[59]####
            } catch (Exception e) {//####[59]####
                e.printStackTrace();//####[59]####
            }//####[59]####
        }//####[59]####
    }//####[59]####
    public TaskIDGroup<Void> loop() {//####[59]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[59]####
        return loop(new TaskInfo());//####[59]####
    }//####[59]####
    public TaskIDGroup<Void> loop(TaskInfo taskinfo) {//####[59]####
        // ensure Method variable is set//####[59]####
        if (__pt__loop__method == null) {//####[59]####
            __pt__loop__ensureMethodVarSet();//####[59]####
        }//####[59]####
        taskinfo.setParameters();//####[59]####
        taskinfo.setMethod(__pt__loop__method);//####[59]####
        taskinfo.setInstance(this);//####[59]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[59]####
    }//####[59]####
    public void __pt__loop() {//####[59]####
        PriorityQueue<State1> open = new PriorityQueue<State1>(1, new StateCompare());//####[63]####
        long id = CurrentTask.relativeID();//####[66]####
        Channel<State1> channel = new Channel<State1>(id);//####[69]####
        channels.put(id, channel);//####[72]####
        if (!initialised.getAndSet(true)) //####[75]####
        {//####[75]####
            open.add(new State1(initialAndGoal, table.listNodes().length));//####[76]####
        }//####[77]####
        threads.add(Thread.currentThread());//####[79]####
        while (true) //####[83]####
        {//####[83]####
            if (discovered.get() && channel.isEmpty()) //####[84]####
            {//####[84]####
                State1 state = open.peek();//####[85]####
                if (state == null || state.getCities().size() == table.listNodes().length + 1 && state.getCurrentTourDistance() > finalState.getCurrentTourDistance()) //####[86]####
                {//####[86]####
                    try {//####[87]####
                        CurrentTask.barrier();//####[88]####
                        return;//####[89]####
                    } catch (Exception e) {//####[90]####
                    }//####[91]####
                }//####[92]####
            }//####[93]####
            if (!channel.isEmpty()) //####[95]####
            {//####[95]####
                State1 state = channel.receive();//####[97]####
                open.add(state);//####[100]####
            } else {//####[101]####
                State1 state = open.poll();//####[103]####
                if (state == null) //####[105]####
                {//####[105]####
                    try {//####[107]####
                        channel.callWait();//####[108]####
                    } catch (InterruptedException e) {//####[109]####
                    }//####[111]####
                    continue;//####[113]####
                }//####[114]####
                notifyObservers(state, CurrentTask.relativeID());//####[117]####
                if (state.getCities().size() == table.listNodes().length + 1) //####[121]####
                {//####[121]####
                    if (finalState == null) //####[122]####
                    {//####[122]####
                        finalState = state;//####[123]####
                        discovered.set(true);//####[124]####
                        for (Map.Entry<Long, Channel<State1>> e : channels.entrySet()) //####[127]####
                        {//####[127]####
                            Channel<State1> ch = e.getValue();//####[128]####
                            if (ch.getIsWaiting()) //####[129]####
                            {//####[129]####
                                ch.callNotifyAll();//####[130]####
                            }//####[131]####
                        }//####[132]####
                    } else if (state.getCurrentTourDistance() < finalState.getCurrentTourDistance()) //####[133]####
                    {//####[133]####
                        finalState = state;//####[134]####
                    }//####[135]####
                } else if (state.getCities().size() == table.listNodes().length) //####[136]####
                {//####[136]####
                    double g = state.getCurrentTourDistance() + Problem.getDistanceBetween(table, state.getCurrentCity(), initialAndGoal);//####[138]####
                    open.add(new State1(state, initialAndGoal, g, 0));//####[139]####
                }//####[140]####
                for (int s : table.getNeighborsOf(state.getCurrentCity())) //####[144]####
                {//####[144]####
                    if (state.pathContains(s)) //####[145]####
                    {//####[145]####
                        continue;//####[146]####
                    }//####[147]####
                    double g = state.getCurrentTourDistance() + Problem.getDistanceBetween(table, state.getCurrentCity(), s);//####[149]####
                    double h = heuristic.heuristicCost(s, table, state.getCities(), state);//####[152]####
                    double fValue = g + h;//####[154]####
                    State1 newState = new State1(state, s, g, fValue);//####[157]####
                    long processorId = hashFunction(newState);//####[158]####
                    Channel<State1> c = channels.get(processorId);//####[160]####
                    if (discovered.get() && newState.getCurrentTourDistance() < finalState.getCurrentTourDistance()) //####[162]####
                    {//####[162]####
                        open.add(newState);//####[163]####
                    } else {//####[164]####
                        c.send(newState);//####[165]####
                        if (c.getIsWaiting()) //####[168]####
                        {//####[168]####
                            c.callNotifyAll();//####[169]####
                        }//####[170]####
                    }//####[171]####
                }//####[172]####
            }//####[173]####
        }//####[174]####
    }//####[175]####
//####[175]####
//####[177]####
    public long hashFunction(State1 state) {//####[177]####
        int size = channels.size();//####[178]####
        int index = state.hashCode() % size;//####[179]####
        List<Long> processes = new ArrayList<Long>(channels.keySet());//####[180]####
        return processes.get(index);//####[181]####
    }//####[182]####
}//####[182]####
