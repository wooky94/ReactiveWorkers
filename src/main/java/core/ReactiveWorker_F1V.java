package core;

import core.structure.FiFoReaderVariableSender;

/** Worker with a single task.
 * The results of the last task are returned through launch() method return.
 * The initial data consumed by single task are sent by launcher using feed() method, and read by first task using
 * fromLauncher() method.
 * @param <U> : is the type sended by launcher to the single task.
 * @param <V> : is the type returned by the single task through launch() method */
public abstract class ReactiveWorker_F1V<U,V> extends FiFoReaderVariableSender<U,V> implements Runnable {

    /** Starts the single task in its own thread.
     * @return the result of the single task. */
    public final V launch(){
        Thread t1 = new Thread(this);
        t1.start();
        return answerWhenAvailable();
    }



    @Override
    public final void run() {
        task();
        declareLastTaskIsFinished();
    }

    /** Task to execute.<br>
     *  This task has to get its input data using fromLauncher() method.
     *  This task has to return its results to the launcher using answer() method. */
    abstract void task();


}
