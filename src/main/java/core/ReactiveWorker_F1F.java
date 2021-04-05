package core;

import core.structure.FifoReaderWriter;

/** Worker with a single task.
 * The results of this task are returned through a fifo.
 * This fifo can be read by launcher (caller) using next() method
 * The initial data consumed by the single task are sent by launcher using feed() method, and read by the single task using
 * fromLauncher() method.
 * @param <T> : is the type sent by launcher to the single task.
 * @param <U> : is the type returned by the task into the fifo */
public abstract class ReactiveWorker_F1F<T,U> extends FifoReaderWriter<T,U> implements Runnable {

    /** Starts the single task in its own thread . Non blocking.*/
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
     *  This task has to get its input data using fromLauncher() method.
     *  This task has to return each result to the launcher using answer() method. */
    abstract void task();

}
