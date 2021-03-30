package core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Worker that must defines two tasks (method).
 * The results of this tasks are collected in a fifo.
 * This fifo can be read by launcher (caller) using next() method.
 * First and Second task work in same time, and results of first task are sended to second task through
 * an internal fifo.
 * @param <U> : is the type returned to the caller by second task */
public abstract class DoubleWorker<T,U> implements Runnable {

    private int taskToLaunch = 1;               // Counter to launch each task only once.
    private BlockingQueue<T> internalFifo;      // The internal fifo between first task  and second task
    private BlockingQueue<U> outPutFifo;        // The output fifo into the second task write, and caller read
    private boolean firstHasFinished  = false;  // true when the first task has finished
    private boolean secondHasFinished = false;  // true when the second task has finished

    /** Starts the first and second task in their own thread */
    public final void launch(){
        internalFifo = new ArrayBlockingQueue<T>(10000);
        outPutFifo = new ArrayBlockingQueue<U>(10000);

        Thread t1 = new Thread(this);
        t1.start();
        Thread t2 = new Thread(this);
        t2.start();
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
            default:
                throw new IllegalStateException("Unexpected value: " + numberTaskToLaunch);
        }
    }

    /** First task to execute. This first task has to send its results to the second task using toSecondTask() method.*/
    abstract void firstTask();
    /** Second task to execute. This second task has take results of first task using fromFirstTask() method, and used them.
     * Then return its own results to the caller, using answer() method. */
    abstract void secondTask();

    /** send the specified element to the secondTask.
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void toSecondTask(T element) throws InterruptedException {
        internalFifo.put(element);
    }

    /** @return The next element from the fifo. If fifo is empty and producer is dead, then null is returned */
    protected T fromFirstTask(){
        while(true) {
            T element = internalFifo.poll();
            if (null != element) return element;
            if (firstHasFinished) return null;
        }
    }

    /** send the specified element through main fifo (to the launcher of this object).
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void answer(U element) throws InterruptedException {
        outPutFifo.put(element);
    }

    /** This method has to be used by the caller (which has called launch() methode).
     * @return The next element from the fifo filled by the second task */
    public U next(){
        while(true) {
            U element = outPutFifo.poll();
            if (null != element) return element;
            if (secondHasFinished) return null;
        }
    }

}
