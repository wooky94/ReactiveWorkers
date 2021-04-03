package core;


public class QuickyCounter extends ReactiveWorker_F1F<Integer,String> {


    /** Task to execute.<br>
     * This task has to get its input data from inputFifo using fromLauncher() method.
     * This task has to return its results to the launcher using answer() method.
     */
    @Override
    void task() {
        Integer val;
        Integer somme = 0;
        while ((val = fromLauncher()) != null){
            somme += val;
            try {
                answer(somme.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
