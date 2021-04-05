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
public abstract class VariableSender<U> {
    private boolean lastTaskIsFinished = false;     // True when the last task is complete
    private U computedResult = null;                // The return to return

    /*============================================================================================*/
    /*                                           OUTPUT                                           */
    /*============================================================================================*/
    /** return the given result to the caller of this object. This method has to be called by the last task */
    final protected void answer(U result){
        computedResult = result;
    }

    /** declare that last task is finished. This means that if the fifo is empty, then no more data should be expected. */
    final protected void declareLastTaskIsFinished() {
        lastTaskIsFinished = true;
    }


    /** Wait that last task ended, then return the value saved by answer() method */
    final protected U answerWhenAvailable() {
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
