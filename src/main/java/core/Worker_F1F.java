package core;

/** Worker with a single task.
 * The results of this task are returned through a fifo.
 * This fifo can be read by launcher (caller) using next() method
 * The initial data consumed by single task has to be given through input fifo using feed() method.
 * WARNING: launcher must declared  the end of feeding by calling noMoreInput() method
 * @param <U> : is the type of object that are put into the input fifo
 * @param <V> : is the type returned by the task into the fifo */
public abstract class Worker_F1F<U,V> extends FifoReaderWriter<U,V> implements Runnable {

    public Worker_F1F() {
        super();
    }

    /** Starts the single task in its own thread */
    public final void launch(){
        Thread t1 = new Thread(this);
        t1.start();
    }

    @Override
    public final void run() {
        task();
        lastTaskIsFinished();
    }

    /** Task to execute.<br>
     *  This task has to get its input data from inputFifo using fromLauncher() method.
     *  This task has to return its results to the launcher using answer() method. */
    abstract void task();

}
