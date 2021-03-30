package core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Worker that must defines a single task (method).
 * The results of this task are collected in a fifo.
 * This fifo can be read by launcher (caller) using next() method
 * @param <U> : is the type sended by task */
public abstract class SimpleWorker<U> implements Runnable {

    private BlockingQueue<U> outPutFifo;    // The output fifo.
    private boolean taskFinished = false;   // True when the task is completed

    /** Starts the first and second task in their own thread */
    public final void launch(){
        outPutFifo = new ArrayBlockingQueue<U>(10000);

        Thread t1 = new Thread(this);
        t1.start();
    }

    @Override
    public final void run() {
        task();
        taskFinished = true;
    }

    /** Task to execute.<br>
     *  This task has to send its results to the launcher using answer() method. */
    abstract void task();

    /** send the specified element through main fifo (to the launcher of this object).
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc...<br>
     *
     * @throws InterruptedException - if interrupted while waiting
     * @throws ClassCastException - if the class of the specified element prevents it from being added to this queue
     * @throws NullPointerException - if the specified element is null
     * @throws IllegalArgumentException - if some property of the specified element prevents it from being added to this queue
     *  */
    protected void answer(U element) throws InterruptedException,ClassCastException,NullPointerException,IllegalArgumentException {
        outPutFifo.put(element);
    }

    /** This method has to be used by the caller (which has called launch() methode).
     * @return The next element from the fifo filled by the task */
    public U next(){
        while(true) {
            U element = outPutFifo.poll();
            if (null != element) return element;
            if (taskFinished) return null;
        }
    }

}
