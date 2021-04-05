package core.structure;

import core.structure.FiFoReader;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** abtract class that contains tools to be fed by launcher, and to return its owns result into outputFifo.<br>
 * An inputFifo
 * A feed() method to allow launcher to send input data to the task.
 * A fromLauncher() method to allow task to get input data from the inputFifo.
 * A outPutFifo
 * A answers() method to allow last task to send response to the launcher.
 * A next() method to allow the launcher to get response from the outputFifo.
 * @param <U> : is the type of object that are put into the input fifo
 * @param <V> : is the type returned by the task into the fifo */
public abstract class FifoReaderWriter<U,V> extends FiFoReader<U> {
    private BlockingQueue<V> outPutFifo;    // The output fifo.
    private boolean lastTaskIsFinished = false;   // True when the task is completed

    public FifoReaderWriter() {
        super();
        outPutFifo = new ArrayBlockingQueue<V>(10000);
    }

    /** send the specified element through main fifo (to the launcher).
     * Some exceptions can be throwed, if element is null, if element contains some attributs that prevent it
     * to be putted into the queue, etc...<br>
     *
     * @throws InterruptedException - if interrupted while waiting
     * @throws ClassCastException - if the class of the specified element prevents it from being added to this queue
     * @throws NullPointerException - if the specified element is null
     * @throws IllegalArgumentException - if some property of the specified element prevents it from being added to this queue
     */
    protected void answer(V element) throws InterruptedException,ClassCastException,NullPointerException,IllegalArgumentException {
        outPutFifo.put(element);
    }

    /** declare that last task is finished. This means that if the fifo is empty, then no more data should be expected. */
    protected void declareLastTaskIsFinished() {
        lastTaskIsFinished = true;
    }


    /** This method has to be used by the launcher.
     * @return The next element from the fifo filled by the task */
    public V next(){
        while(true) {
            V element = outPutFifo.poll();
            if (null != element) return element;
            if (lastTaskIsFinished) return null;
        }
    }
}
