package at.aau.wagnis;

import java.util.ArrayList;

public class GlobalVariables {

    public static ArrayList<Hub> hubs = new ArrayList<>();
    public static  ArrayList<Adjacency> adjacencies = new ArrayList<>();
    static int displayWidthPx,displayHeightPx;


    public static Hub findHubById(int id){
        for(Hub h : hubs){
            if(h.getId()==id){
                return h;
            }
        }
        return null;
    }

    public static int getDisplayWidthPx() {
        return displayWidthPx;
    }

    public static void setDisplayWidthPx(int displayWidthPx) {
        GlobalVariables.displayWidthPx = displayWidthPx;
    }

    public static int getDisplayHeightPx() {
        return displayHeightPx;
    }

    public static void setDisplayHeightPx(int displayHeightPx) {
        GlobalVariables.displayHeightPx = displayHeightPx;
    }
}
