package at.aau.wagnis;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.security.SecureRandom;
import java.util.ArrayList;

public class GlobalVariables {
    public static String agency = "";

    public static ArrayList<Player> players = new ArrayList<>();
    public static ArrayList<String> unavailableAgencies = new ArrayList<>();
    public static Context baseContext;
    public static String hostIP;
    public static Boolean isClient = false;
    public static String seed= "42";
    public static ArrayList<String> seeds = new ArrayList<>();

    public static ArrayList<Hub> hubs = new ArrayList<>();
    public static ArrayList<Adjacency> adjacencies = new ArrayList<>();
    public static int hubsPerLine;
    public static MediaPlayer mediaPlayer;
    static int displayWidthPx, displayHeightPx;
    private static String localIpAddress;

    //public static Deck mainDeck = new Deck(hubs.size());

    public static Hub findHubById(int id) {
        for (Hub h : hubs) {
            if (h.getId() == id) {
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

    public static String getLocalIpAddress() {
        return localIpAddress;
    }

    public static void setLocalIpAddress(String localIpAddress) {
        GlobalVariables.localIpAddress = localIpAddress;
    }


    public static void setAdjacencies() {
        int lineHubCount = 0;
        int chance;
        boolean isConnected;
        int flag=0;

        for (int i = 0; i < hubs.size() - hubsPerLine; i++) {
            isConnected=false;
            lineHubCount++;
            chance = Integer.parseInt(seeds.get(i));

            if (chance % 2 == 0) {
                isConnected=true;
                if (lineHubCount != hubsPerLine) {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + 1)));                  /*Neighbour right if not last in row*/
                } else {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine)));        /*Neighbour bottom for last hub of row*/
                    lineHubCount = 0;
                }
            }
            if (chance % 3 == 0) {
                isConnected=true;
                adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine)));            /*Neighbour bottom*/
            }

            if(!isConnected){
                if(lineHubCount>1){
                    if(flag!=hubs.get(i).getId()-1){                                                                    /*check if previous connection wont intercept new one*/
                        adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine-1)));  /*Neighbour bottom left*/
                        flag=hubs.get(i).getId();
                    }else{
                        adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine)));    /*Neighbour bottom*/
                        flag=0;
                    }
                }else{
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine + 1)));    /*Neighbour bottom right*/
                    flag = hubs.get(i).getId();
                }

            }
            if(lineHubCount==hubsPerLine){
                lineHubCount=0;
            }
        }
        for (int i = hubs.size() - hubsPerLine; i < hubs.size() - 1; i++) {                                             /*Connect last row with respective neighbour*/
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


    public static String getIpAddress() {
        WifiManager wm = (WifiManager) baseContext.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }


}