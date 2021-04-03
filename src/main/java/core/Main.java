package core;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        QuickyCounter compteur = new QuickyCounter();
        compteur.launch();
        for(int i = 0; i< 200; i++){
            compteur.feed(Integer.valueOf(i));

            System.out.println(compteur.next());
        }
        compteur.noMoreInput();

    }

}
