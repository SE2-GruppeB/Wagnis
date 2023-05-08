package at.aau.wagnis;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import at.aau.wagnis.application.GameManager;
import at.aau.wagnis.application.WagnisApplication;

import static android.content.Context.WIFI_SERVICE;

public class GlobalVariables {

    public static GameManager gm;
    public static String agency ="";
    public static ArrayList<Player> players = new ArrayList<>();
    public static ArrayList<String> unavailableAgencies = new ArrayList<>();
    public final static String troop = "troop";

    public static Context baseContext;
    public static String hostIP;
    public static Boolean isClient = false;
    public static String seed= "123455123455123456123455123456123456123456123456123456123456123456123456123456123456";
    public static ArrayList<String> seeds = new ArrayList<>();
    public static ArrayList<Hub> hubs = new ArrayList<>();
    public static  ArrayList<Adjacency> adjacencies = new ArrayList<>();
    static int displayWidthPx,displayHeightPx;
    public static int hubsPerLine;
    public static MediaPlayer mediaPlayer;

    public static Hub findHubById(int id){
        for(Hub h : hubs){
            if(h.getId()==id){
                return h;
            }
        }
        return null;
    }

    public static ArrayList<String> getUnavailableAgencies() {
        return unavailableAgencies;
    }

    public static void addUnavailableAgencies(String unavailableAgency) {
        GlobalVariables.unavailableAgencies.add(unavailableAgency);
    }

    public static String getHostIP() {
        return hostIP;
    }

    public static void setHostIP(String hostIP) {
        GlobalVariables.hostIP = hostIP;
    }

    public static Boolean getIsClient() {
        return isClient;
    }

    public static void setIsClient(Boolean isClient) {
        GlobalVariables.isClient = isClient;
    }

    public static String getAgency() {
        return agency;
    }

    public static void setAgency(String team) {
        GlobalVariables.agency = team;
    }

    public static String getSeed() {
        return seed;
    }

    public static void setSeed(String seed) {
        GlobalVariables.seed = seed;
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

    public static ArrayList<Hub> getHubs() {
        return hubs;
    }


    public static void setAdjacencies(){
        int lineHubCount=1;
        int chance = 0;
        for(int i =0;i<hubs.size()-hubsPerLine;i++){
            chance = Integer.parseInt(seeds.get(i));

            if(chance%2==0){
                if(lineHubCount%hubsPerLine==0) {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + 1)));
                }else{
                    adjacencies.add(new Adjacency(hubs.get(i),findHubById(hubs.get(i).getId()+hubsPerLine)));
                    lineHubCount=1;
                }
            }if (chance %3==0){
                adjacencies.add(new Adjacency(hubs.get(i),findHubById(hubs.get(i).getId()+hubsPerLine)));
            }else if (chance % 5==0) {
                if(lineHubCount == hubsPerLine){
                    adjacencies.add(new Adjacency(hubs.get(i),findHubById(hubs.get(i).getId()+hubsPerLine)));
                }else{
                    adjacencies.add(new Adjacency(hubs.get(i),findHubById(hubs.get(i).getId()+hubsPerLine-1)));
                }
            }else{
                adjacencies.add(new Adjacency(hubs.get(i),findHubById(hubs.get(i).getId()+hubsPerLine-1)));

            }
            lineHubCount++;
        }
        for(int i=hubs.size()-hubsPerLine;i<hubs.size()-1;i++){
            adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + 1)));
        }
    }

    public static void seedGenerator() {
        SecureRandom secureRandom = new SecureRandom();
        String seed = "";
        for (int i = 0; i < 42; i++) {
            int s = 0;
            while (s <= 10) {
                s = secureRandom.nextInt(100);
            }
            seed = seed + s;
        }
        GlobalVariables.setSeed(seed);
    }


    public static String getIpAddress(){
        WifiManager wm = (WifiManager) baseContext.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }


}