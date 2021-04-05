package core;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        QuickyCounter compteur = new QuickyCounter();

        for(int i = 0; i< 20; i+=2)
            compteur.feed(Integer.valueOf(i));

        compteur.noMoreInput();
        List<Integer> result = compteur.launch();
        System.out.println(result);


    }

}
