package at.aau.wagnis;

import java.util.ArrayList;

public class GlobalVariables {

    public static ArrayList<Hub> hubs = new ArrayList<>();

    public static Hub findHubById(int id){
        for(Hub h : hubs){
            if(h.getId()==id){
                return h;
            }
        }
        return null;
    }
}
