package at.aau.wagnis;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

//Klassen mit PL sind reine Platzhalter Klassen deren Methoden später extrahiert werden
public class PLRNG {

    public int diceRoll(){
        Random randomGen = new SecureRandom();
        int diceValue = randomGen.nextInt(6) + 1;
        //Log.d("Info :","" + diceValue);
        return diceValue;
    }


}
