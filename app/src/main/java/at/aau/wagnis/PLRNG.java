package at.aau.wagnis;

import android.util.Log;

import java.security.SecureRandom;
import java.util.Random;

//Klassen mit PL sind reine Platzhalter Klassen deren Methoden später extrahiert werden
public class PLRNG {

    public int diceRoll(){
        Random randomGen = new SecureRandom();
        int diceValue = randomGen.nextInt(6) + 1;
        Log.d("Info :","" + diceValue);
        return diceValue;
    }

    public Cards genCard(){
        Random randomGen = new SecureRandom();
        // Int range ist 0 bis (1-hubs.size()); addiert mit 100 liefert 100 bis (100 + hubs.size())
        int hubId = randomGen.nextInt(GlobalVariables.getHubs().size()) + 100;
        int type = randomGen.nextInt(3);
        if (type == 0){
            Log.d("Info :","Created new Card ID :" + hubId + " ; Type : " + Troops.INFANTRY);
            return new Cards(hubId,Troops.INFANTRY);
        } else if (type == 1) {
            Log.d("Info :","Created new Card ID :" + hubId + " ; Type : " + Troops.CAVALRY);
            return new Cards(hubId,Troops.CAVALRY);
        } else {
            Log.d("Info :","Created new Card ID :" + hubId + " ; Type : " + Troops.ARTILLERY);
            return new Cards(hubId,Troops.ARTILLERY);
        }
    }

    public  void deployment(){
        //TODO implement Method waiting for Merge
    }

}
