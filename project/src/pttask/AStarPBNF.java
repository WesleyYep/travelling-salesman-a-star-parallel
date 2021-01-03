package pttask;//####[1]####
//####[1]####
import java.util.concurrent.ExecutionException;//####[3]####
import java.util.concurrent.PriorityBlockingQueue;//####[4]####
import java.util.concurrent.atomic.AtomicBoolean;//####[5]####
import java.util.concurrent.locks.*;//####[6]####
import org.moeaframework.problem.tsplib.DistanceTable;//####[8]####
import org.moeaframework.problem.tsplib.TSPPanel;//####[9]####
import java.lang.reflect.*;//####[11]####
import pt.runtime.GuiThread;//####[12]####
import tsp.State1;//####[13]####
import java.util.concurrent.BlockingQueue;//####[15]####
import java.util.ArrayList;//####[16]####
import java.util.List;//####[17]####
import util.StateCompare;//####[19]####
import util.Channel;//####[20]####
//####[20]####
//-- ParaTask related imports//####[20]####
import pt.runtime.*;//####[20]####
import java.util.concurrent.ExecutionException;//####[20]####
import java.util.concurrent.locks.*;//####[20]####
import java.lang.reflect.*;//####[20]####
import pt.runtime.GuiThread;//####[20]####
import java.util.concurrent.BlockingQueue;//####[20]####
import java.util.ArrayList;//####[20]####
import java.util.List;//####[20]####
//####[20]####
/**
 * Implementation of Parallel Best-N Block First
 * @author Chang Kon Han
 * @author Wesley Yep
 * @author John Law
 * @see https://pdfs.semanticscholar.org/36d9/e216a74b7e11fce1c69067564613acce8505.pdf
 *///####[28]####
public class AStarPBNF {//####[30]####
    static{ParaTask.init();}//####[30]####
    /*  ParaTask helper method to access private/protected slots *///####[30]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[30]####
        if (m.getParameterTypes().length == 0)//####[30]####
            m.invoke(instance);//####[30]####
        else if ((m.getParameterTypes().length == 1))//####[30]####
            m.invoke(instance, arg);//####[30]####
        else //####[30]####
            m.invoke(instance, arg, interResult);//####[30]####
    }//####[30]####
//####[33]####
    private int city;//####[33]####
//####[36]####
    private PriorityBlockingQueue<State1> open = new PriorityBlockingQueue<State1>(1, new StateCompare());//####[36]####
//####[38]####
    private DistanceTable table;//####[38]####
//####[39]####
    private TSPPanel panel;//####[39]####
//####[42]####
    private AtomicBoolean done = new AtomicBoolean(false);//####[42]####
//####[44]####
    public AStarPBNF(DistanceTable table, TSPPanel panel) {//####[44]####
        this.table = table;//####[45]####
        this.panel = panel;//####[46]####
    }//####[47]####
//####[49]####
    public void search() {//####[49]####
        int initialAndGoal = table.listNodes()[0];//####[51]####
        open.add(new State1(initialAndGoal, table.listNodes().length));//####[53]####
    }//####[54]####
//####[56]####
    private static volatile Method __pt__threadsearch__method = null;//####[56]####
    private synchronized static void __pt__threadsearch__ensureMethodVarSet() {//####[56]####
        if (__pt__threadsearch__method == null) {//####[56]####
            try {//####[56]####
                __pt__threadsearch__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__threadsearch", new Class[] {//####[56]####
                    //####[56]####
                });//####[56]####
            } catch (Exception e) {//####[56]####
                e.printStackTrace();//####[56]####
            }//####[56]####
        }//####[56]####
    }//####[56]####
    public TaskIDGroup<Void> threadsearch() {//####[56]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[56]####
        return threadsearch(new TaskInfo());//####[56]####
    }//####[56]####
    public TaskIDGroup<Void> threadsearch(TaskInfo taskinfo) {//####[56]####
        // ensure Method variable is set//####[56]####
        if (__pt__threadsearch__method == null) {//####[56]####
            __pt__threadsearch__ensureMethodVarSet();//####[56]####
        }//####[56]####
        taskinfo.setParameters();//####[56]####
        taskinfo.setMethod(__pt__threadsearch__method);//####[56]####
        taskinfo.setInstance(this);//####[56]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, -1);//####[56]####
    }//####[56]####
    public void __pt__threadsearch() {//####[56]####
    }//####[67]####
//####[67]####
//####[82]####
    /**
	 * Given a state, returns an abstract state. Abstraction function which takes the city and returns its index
	 * @return abstract state
	 *///####[82]####
    public int abstractionFunction(State1 state) {//####[82]####
        return 0;//####[83]####
    }//####[84]####
}//####[84]####
