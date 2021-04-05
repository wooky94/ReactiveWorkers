package core.structure;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** abtract class that contains tools to be fed by launcher.<br>
 * An inputFifo<br>
 * A feed() method to allow launcher to send input data to the task.<br>
 * A fromLauncher() method to allow task to get input data from the inputFifo.<br>
 * <br>
 * and contains tools to return a variable too.
 * @param <U> : is the type of object that are put into the input fifo
 * @param <V> : is the type of the variable returned by the last task.
 */
public abstract class FiFoReaderVariableSender<U,V> {
    private BlockingQueue<U> inPutFifo;     // The input fifo.
    private boolean noMoreInput = false;    // True when the launcher declare it
    private boolean lastTaskIsFinished = false;   // True when the last task is complete
    private V computedResult = null;        // The return to return

    public FiFoReaderVariableSender() {
        inPutFifo = new ArrayBlockingQueue<U>(10000);
    }

    /*============================================================================================*/
    /*                                           INPUT                                            */
    /*============================================================================================*/

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

    /** declare that no more input data will be put into the inputFifo */
    public void noMoreInput(){
        noMoreInput = true;
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
    /*============================================================================================*/
    /*                                           OUTPUT                                           */
    /*============================================================================================*/
    /** return the given result to the caller of this object. This method has to be called by the last task */
    final protected void answer(V result){
        computedResult = result;
    }

    /** declare that last task is finished. This means that if the fifo is empty, then no more data should be expected. */
    final protected void declareLastTaskIsFinished() {
        lastTaskIsFinished = true;
    }


    /** Wait that last task ended, then return the value saved by answer() method */
    final protected V answerWhenAvailable() {
        while( lastTaskIsFinished == false){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return computedResult;
    }
}
