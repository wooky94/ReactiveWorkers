package core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Worker with four task.
 * The results of the last task are returned through a fifo.
 * This fifo can be read by launcher (caller) using next() method.
 * The initial data consumed by first task has to be given by constructor call.
 * @param <T> : is the type of data exchanged between first task and second task.
 * @param <U> : is the type of data exchanged between second task and third task.
 * @param <V> : is the type of data exchanged between third task and fourth task.
 * @param <W> : is the type returned by the task into the fifo */
public abstract class ReactiveWorker_F4F<T,U,V,W,X> extends FifoReaderWriter<T,X> implements Runnable {

    private int taskToLaunch = 1;               // Counter to launch each task only once.
    private BlockingQueue<T> internalFifo1;     // The internal fifo between first task  and second task
    private BlockingQueue<U> internalFifo2;     // The internal fifo between second task and third task
    private BlockingQueue<V> internalFifo3;     // The internal fifo between third task and fourth task
    private boolean firstHasFinished  = false;  // true when the first task has finished
    private boolean secondHasFinished = false;  // true when the second task has finished
    private boolean thirdHasFinished  = false;  // true when the third task has finished

    /** Starts each task in its own thread */
    public final void launch(){
        internalFifo1 = new ArrayBlockingQueue<T>(10000);
        internalFifo2 = new ArrayBlockingQueue<U>(10000);
        internalFifo3 = new ArrayBlockingQueue<V>(10000);

        Thread t1 = new Thread(this);
        t1.start();
        Thread t2 = new Thread(this);
        t2.start();
        Thread t3 = new Thread(this);
        t3.start();
        Thread t4 = new Thread(this);
        t4.start();
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
                secondHasFinished = true;
                break;
            case 3:
                thirdTask();
                thirdHasFinished = true;
                break;
            case 4:
                fourthTask();
                lastTaskIsFinished();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + numberTaskToLaunch);
        }
    }

    /** First task to execute. This first task has to send its results to the second task using toSecondTask() method.*/
    abstract void firstTask();
    /** Second task to execute. This second task has to take results of first task using fromFirstTask() method, and used them.
     * Then return its own results to the third task using toThirdTask() method. */
    abstract void secondTask();
    /** Third task to execute. This third task has to take results of second task using fromSecondTask() method, and used them.
     * Then return its own results to the fourth task using toFourthTask() method. */
    abstract void thirdTask();
    /** Fourth task to execute. This fourth task has to take results of third task using fromThirdTask() method, and used them.
     * Then return its own results to the caller, using answer() method. */
    abstract void fourthTask();

    /** Send the specified element to the secondTask.
     * Some exceptions can be thrown, if element is null, if element contains some attributs that prevent it
     * to be put into the queue, etc... */
    protected void toSecondTask(T element) throws InterruptedException {
        internalFifo1.put(element);
    }

    /** Send the specified element to the thirdTask.
     * Some exceptions can be thrown, if element is null, if element contains some attributs that prevent it
     * to be put into the queue, etc... */
    protected void toThirdTask(U element) throws InterruptedException {
        internalFifo2.put(element);
    }

    /** Send the specified element to the fourthTask.
     * Some exceptions can be thrown, if element is null, if element contains some attributs that prevent it
     * to be put into the queue, etc... */
    protected void toFourthTask(V element) throws InterruptedException {
        internalFifo3.put(element);
    }

    /** @return The next element sended by first task. If fifo is empty and producer is dead, then null is returned */
    protected T fromFirstTask(){
        while(true) {
            T element = internalFifo1.poll();
            if (null != element) return element;
            if (firstHasFinished) return null;
        }
    }

    /** @return The next element sended by second task. If fifo is empty and producer is dead, then null is returned */
    protected U fromSecondTask(){
        while(true) {
            U element = internalFifo2.poll();
            if (null != element) return element;
            if (secondHasFinished) return null;
        }
    }

    /** @return The next element sended by third task. If fifo is empty and producer is dead, then null is returned */
    protected V fromThirdTask(){
        while(true) {
            V element = internalFifo3.poll();
            if (null != element) return element;
            if (secondHasFinished) return null;
        }
    }
}
