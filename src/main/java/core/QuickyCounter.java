package core;


import java.util.ArrayList;
import java.util.List;

public class QuickyCounter extends ReactiveWorker_F2V<Integer, Integer,List<Integer>> {

    /**
     * Task to execute.<br>
     * This task has to get its input data using fromLauncher() method.
     * This task has to return its results to the launcher using answer() method.
     */
    @Override
    void firstTask() {
        Integer input = null;
        Integer result = 0;

        while((input = fromLauncher()) != null){
            result = result + input;
            try {
                toSecondTask(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Second task to execute. This second task has to take results of first task using fromFirstTask() method, and used them.
     * Then return its own results to the caller, using answer() method.
     */
    @Override
    void secondTask() {
        Integer input = null;
        ArrayList<Integer> result = new ArrayList<>();

        while((input = fromFirstTask()) != null){
            result.add(input);
        }
        answer(result);
    }
}
