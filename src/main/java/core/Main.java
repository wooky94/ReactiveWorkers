package core;

import java.util.List;

public class Main {
    public static void main(String[] args){
        QuickyCounter compteur = new QuickyCounter(200);
        compteur.launch();
        Integer val = null;
        while ((val = compteur.next()) != null)
            System.out.println(val);
    }
}
