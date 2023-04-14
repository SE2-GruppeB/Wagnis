package at.aau.wagnis;

import android.util.Log;

import java.util.Random;

//Klassen mit PL sind reine Platzhalter Klassen deren Methoden später extrahiert werden
public class PLRNG {

    public int diceRoll(){
        Random randomGen = new Random();
        int diceValue = randomGen.nextInt(6) + 1;
        Log.d("Info :","" + diceValue);
        return diceValue;
    }

    public Cards genCard(){
        Random randomGen = new Random();
        // Int range ist 0 bis (1-hubs.size()); addiert mit 100 liefert 100 bis (100 + hubs.size())
        int hubId = randomGen.nextInt(GlobalVariables.getHubs().size()) + 100;
        int type = randomGen.nextInt(3);
        if (type == 0){
            Log.d("Info :","Created new Card ID :" + hubId + " ; Type : " + Troops.infantry);
            return new Cards(hubId,Troops.infantry);
        } else if (type == 1) {
            Log.d("Info :","Created new Card ID :" + hubId + " ; Type : " + Troops.cavalry);
            return new Cards(hubId,Troops.cavalry);
        } else {
            Log.d("Info :","Created new Card ID :" + hubId + " ; Type : " + Troops.artillery);
            return new Cards(hubId,Troops.artillery);
        }
    }

    public  void deployment(){
        //TODO implement Method waiting for Merge


        
    }

}
