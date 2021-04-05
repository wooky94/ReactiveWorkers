package core;

import core.structure.FiFoReaderVariableSender;

/** Worker with a single task.
 * The results of the last task are returned through launch() return.
 * The initial data consumed by single task are sended by launcher using feed() method, and read first task using
 * fromLauncher() method.
 * @param <U> : is the type returned by the task into the fifo */
public abstract class ReactiveWorker_F1V<U,V> extends FiFoReaderVariableSender<U,V> implements Runnable {

    /** Starts the single task in its own thread */
    public final V launch(){
        Thread t1 = new Thread(this);
        t1.start();
        return answerWhenAvailable();
    }



    @Override
    public final void run() {
        task();
        declaresLastTaskIsFinished();
    }

    /** Task to execute.<br>
     *  This task has to get its input data using fromLauncher() method.
     *  This task has to return its results to the launcher using answer() method. */
    abstract void task();


}
