package core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Worker that must defines three tasks (method).
 * The results of this tasks are collected in a fifo.
 * This fifo can be read by launcher (caller) using next() method.
 * First and Second task work in same time, and results of first task are sended to second task through
 * an internal fifo.
 * Second and Third task work in same time too, and results of second task are sended to third task through
 * a second internal fifo.
 * @param <T> : is the type exchanged between first and second task
 * @param <U> : is the type exchanged between second and third task
 * @param <V> : is the type returned to the caller by second task */
public abstract class ReactiveWorker_V4F<T,U,V,W> implements Runnable {

    private int taskToLaunch = 1;               // Counter to launch each task only once.
    private BlockingQueue<T> internalFifo1;     // The internal fifo between first task  and second task
    private BlockingQueue<U> internalFifo2;     // The internal fifo between second task and third task
    private BlockingQueue<V> internalFifo3;     // The internal fifo between third task and fourth task
    private BlockingQueue<W> outPutFifo;        // The output fifo into the second task write, and caller read
    private boolean firstHasFinished  = false;  // true when the first task has finished
    private boolean secondHasFinished = false;  // true when the second task has finished
    private boolean thirdHasFinished  = false;  // true when the third task has finished
    private boolean fourthHasFinished = false;  // true when the fourth task has finished;

    /** Starts the first, second and third task in their own thread */
    public final void launch(){
        internalFifo1 = new ArrayBlockingQueue<T>(10000);
        internalFifo2 = new ArrayBlockingQueue<U>(10000);
        internalFifo3 = new ArrayBlockingQueue<V>(10000);
        outPutFifo = new ArrayBlockingQueue<W>(10000);

        Thread t1 = new Thread(this);
        t1.start();
        Thread t2 = new Thread(this);
        t2.start();
        Thread t3 = new Thread(this);
        t3.start();
        Thread t4 = new Thread(this);
        t4.start();
    }

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
                fourthHasFinished = true;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + numberTaskToLaunch);
        }
    }

    /** First task to execute. This first task has to send its results to the second task using send() method.*/
    abstract void firstTask();
    /** Second task to execute. This second task has take results of first task using next() method, and used them */
    abstract void secondTask();
    /** Third task to execute. This third task has take results of second task using next() method, and used them */
    abstract void thirdTask();
    /** Fourth task to execute. This fourth task has take results of third task using fromThirdTask() method, and used them */
    abstract void fourthTask();

    /** send the specified element to the secondTask.
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void toSecondTask(T element) throws InterruptedException {
        internalFifo1.put(element);
    }
    /** send the specified element to the thirdTask.
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void toThirdTask(U element) throws InterruptedException {
        internalFifo2.put(element);
    }

    /** send the specified element to the fourthTask.
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void toFourthTask(V element) throws InterruptedException {
        internalFifo3.put(element);
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

    /** @return The next element from the fifo. If fifo is empty and producer is dead, then null is returned */
    protected V fromThirdTask(){
        while(true) {
            V element = internalFifo3.poll();
            if (null != element) return element;
            if (secondHasFinished) return null;
        }
    }

    /** send the specified element through main fifo (to the launcher of this object).
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc... */
    protected void answer(W element) throws InterruptedException {
        outPutFifo.put(element);
    }

    /** This method has to be used by the caller (which has called launch() methode).
     * @return The next element from the fifo filled by the second task */
    public W next(){
        while(true) {
            W element = outPutFifo.poll();
            if (null != element) return element;
            if (thirdHasFinished) return null;
        }
    }

}
