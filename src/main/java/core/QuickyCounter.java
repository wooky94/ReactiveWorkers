package core;


public class QuickyCounter extends ReactiveWorker_V4F<Integer,Integer,Integer,Integer> {

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
                toThirdTask(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    void thirdTask() {
        Integer result = 0;
        while(true) {
            Integer val = fromSecondTask();
            if (val == null) return;
            result += val;
            try {
                toFourthTask(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    void fourthTask() {
        Integer result = 0;
        while(true) {
            Integer val = fromThirdTask();
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
