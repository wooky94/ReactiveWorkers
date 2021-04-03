package core;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** abtract class that contains tools to be fed by launcher.<br>
 * An inputFifo
 * A feed() method to allow launcher to send input data to the task.
 * A fromLauncher() method to allow task to get input data from the inputFifo.
 * @param <U> : is the type of object that are put into the input fifo
 */
public abstract class Feedable<U> {
    private BlockingQueue<U> inPutFifo;     // The input fifo.
    private boolean noMoreInput = false;    // True when the launcher declare it

    public Feedable() {
        inPutFifo = new ArrayBlockingQueue<U>(10000);
    }

    /** This method has to be used by the launcher to add element into inputFifo
     * @return true if adding has successed, false if it has failed. */
    public boolean feed(U element) {
        try {
            inPutFifo.put(element);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** @return The next element sended by launcher. If fifo is empty and launcher has declared there's no more data,
     * then null is returned. */
    protected U fromLauncher(){
        while(true) {
            U element = inPutFifo.poll();
            if (null != element) return element;
            if (noMoreInput) return null;
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /** declare that no more input data will be put into the inputFifo */
    public void noMoreInput(){
        noMoreInput = true;
    }
}
