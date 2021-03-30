package core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Worker that must defines two task (method) that run at the same time,
 * and whose results of the first feed the second. The second task return the result
 * to launch method that return to caller.
 * @param <T> : is the type of exchanged data between first task and second task */
public abstract class TripleComputer<T,U,V> implements Runnable {

    private int taskToLaunch = 1;               // Counter to launch each task only once.
    private BlockingQueue<T> internalFifo1;     // The internal fifo between first task  and second task
    private BlockingQueue<U> internalFifo2;     // The output fifo into the second task write, and caller read
    private boolean firstHasFinished  = false;  // true when the first task has finished
    private boolean secondHasFinished = false;  // true when the second task has finished
    private boolean thirdHasFinished  = false;  // true when the second task has finished
    private V computedResult = null;

    /** Starts the first and second task in their own thread */
    public final V launch(){
        internalFifo1 = new ArrayBlockingQueue<T>(10000);
        internalFifo2 = new ArrayBlockingQueue<U>(10000);

        Thread t1 = new Thread(this);
        t1.start();
        Thread t2 = new Thread(this);
        t2.start();
        Thread t3 = new Thread(this);
        t3.start();
        while( thirdHasFinished == false){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return computedResult;
    }

    /** Launch tasks and indicates when each is completed */
    @Override
    public final void run() {
        int numberTaskToLaunch = 0;
        synchronized (this){
            numberTaskToLaunch = taskToLaunch;
            taskToLaunch++;
        }

        switch(numberTaskToLaunch){
            case 1:
                firstTask();
                firstHasFinished = true;
                break;
            case 2:
                secondTask();
                secondHasFinished = true;
                break;
            case 3:
                thirdTask();
                thirdHasFinished = true;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + numberTaskToLaunch);
        }
    }

    /** First task to execute. This first task has to send its results to the second task using send() method.*/
    abstract void firstTask();
    /** Second task to execute. This second task has take results of first task using next() method, and used them */
    abstract void secondTask();
    /** Second task to execute. This second task has take results of first task using next() method, and used them */
    abstract void thirdTask();

    /** send the specified element to the secondTask.
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void toSecondTask(T element) throws InterruptedException {
        internalFifo1.put(element);
    }
    /** send the specified element to the secondTask.
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void toThirdTask(U element) throws InterruptedException {
        internalFifo2.put(element);
    }

    /** @return The next element from the fifo. If fifo is empty and producer is dead, then null is returned */
    protected T fromFirstTask(){
        while(true) {
            T element = internalFifo1.poll();
            if (null != element) return element;
            if (firstHasFinished) return null;
        }
    }
    /** @return The next element from the fifo. If fifo is empty and producer is dead, then null is returned */
    protected U fromSecondTask(){
        while(true) {
            U element = internalFifo2.poll();
            if (null != element) return element;
            if (secondHasFinished) return null;
        }
    }

    /** return the given result to the caller of this object */
   protected void returnResult(V result){
        computedResult = result;
   }
}
