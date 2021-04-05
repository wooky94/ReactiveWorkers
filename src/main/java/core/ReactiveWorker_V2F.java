package core;

import core.structure.FifoWriter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Worker with a two task.
 * The results of the last task are returned through a fifo.
 * This fifo can be read by launcher (caller) using next() method.
 * The initial data consumed by first task has to be given by constructor call.
 * @param <T> : is the type of data exchanged between first task and second task.
 * @param <U> : is the type returned by the task into the fifo */
public abstract class ReactiveWorker_V2F<T,U> extends FifoWriter<U> implements Runnable {

    private int taskToLaunch = 1;               // Counter to launch each task only once.
    private BlockingQueue<T> internalFifo;      // The internal fifo between first task  and second task
    private boolean firstHasFinished  = false;  // true when the first task has finished

    /** Starts each task in its own thread. Non blocking. */
    public final void launch(){
        internalFifo = new ArrayBlockingQueue<T>(10000);
        Thread t1 = new Thread(this);
        t1.start();
        Thread t2 = new Thread(this);
        t2.start();
    }

    /** Call the task dedicated to this thread and indicates when its complete */
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
                declareLastTaskIsFinished();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + numberTaskToLaunch);
        }
    }

    /** First task to execute. This first task has to send its results to the second task using toSecondTask() method.*/
    abstract void firstTask();

    /** Second task to execute. This second task has to take results of first task using fromFirstTask() method, and used them.
     * Then return its own results to the caller, using answer() method. */
    abstract void secondTask();

    /** Send the specified element to the secondTask.
     * Some exceptions can be thrown, if element is null, if element contains some attributs that prevent it
     * to be put into the queue, etc... */
    protected void toSecondTask(T element) throws InterruptedException {
        internalFifo.put(element);
    }

    /** @return The next element sended by first task. If fifo is empty and producer is dead, then null is returned */
    protected T fromFirstTask(){
        while(true) {
            T element = internalFifo.poll();
            if (null != element) return element;
            if (firstHasFinished) return null;
        }
    }
}
