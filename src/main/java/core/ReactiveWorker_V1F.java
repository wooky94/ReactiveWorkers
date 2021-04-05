package core;

import core.structure.FifoWriter;

/** Worker with a single task.
 * The results of this task are returned through a fifo.
 * This fifo can be read by launcher (caller) using next() method
 * The initial data consumed by single task has to be given by constructor call.
 * @param <U> : is the type returned by the task into the fifo */
public abstract class ReactiveWorker_V1F<U> extends FifoWriter<U> implements Runnable {

    /** Starts the single task in its own thread. Non blocking. */
    public final void launch(){
        Thread t1 = new Thread(this);
        t1.start();
    }

    @Override
    public final void run() {
        task();
        declareLastTaskIsFinished();
    }

    /** Task to execute.<br>
     *  This task has to return its results to the launcher using answer() method. */
    abstract void task();

}
