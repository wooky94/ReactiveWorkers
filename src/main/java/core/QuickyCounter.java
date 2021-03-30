package core;

import lombok.extern.slf4j.XSlf4j;

import java.util.ArrayList;
import java.util.List;

public class QuickyCounter extends DoubleWorker<Integer,Integer> {

    int numberOfCycle = 0;

    public QuickyCounter(int nb) {
        numberOfCycle = nb;
    }


    /**
     * First task to execute. This first task has to send its results to the second task using toSecondTask() method.
     */
    @Override
    void firstTask() {
        try {
            for(int i = 0;i<numberOfCycle;i++)
                toSecondTask(Integer.valueOf(i));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Second task to execute. This second task has take results of first task using fromFirstTask() method, and used them.
     * Then return its own results to the caller, using answer() method.
     */
    @Override
    void secondTask() {
        Integer result = 0;
        while(true) {
            Integer val = fromFirstTask();
            if (val == null) return;
            result += val;
            try {
                answer(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
