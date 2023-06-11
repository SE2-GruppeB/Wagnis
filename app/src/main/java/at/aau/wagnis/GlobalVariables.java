package at.aau.wagnis;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables {
    private GlobalVariables(){
        /*Private Constructor to hide implicit one -> SonarCloud*/
    }
    private static String agency = "";

    private static ArrayList<Player> players = new ArrayList<>();
    private static String hostIP;
    private static Boolean isClient = false;
    private static String seed= "42";
    private static ArrayList<String> seeds = new ArrayList<>();
    private static ArrayList<Hub> hubs = new ArrayList<>();
    private static ArrayList<Adjacency> adjacencies = new ArrayList<>();
    private static int hubsPerLine;
    private static MediaPlayer mediaPlayer;
    private static int displayWidthPx;
    private static int displayHeightPx;
    private static String localIpAddress;

    public static Hub findHubById(int id) {
        for (Hub h : hubs) {
            if (h.getId() == id) {
                return h;
            }
        }
        return null;
    }

    public static String getHostIP() {
        return hostIP;
    }

    public static void setHostIP(String hostIP) {
        GlobalVariables.hostIP = hostIP;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(List <Player> players) {
        GlobalVariables.players = (ArrayList<Player>) players;
    }

    public static List<String> getSeeds() {
        return seeds;
    }

    public static void setHubs(List<Hub> hubs) {
        GlobalVariables.hubs = (ArrayList<Hub>) hubs;
    }

    public static List<Adjacency> getAdjacencies() {
        return adjacencies;
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

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void setMediaPlayer(MediaPlayer mediaPlayer) {
        GlobalVariables.mediaPlayer = mediaPlayer;
    }

    public static int getHubsPerLine() {
        return hubsPerLine;
    }

    public static void setHubsPerLine(int hubsPerLine) {
        GlobalVariables.hubsPerLine = hubsPerLine;
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
}