package at.aau.wagnis;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables {
    private static String agency = "";

    private static ArrayList<Player> players = new ArrayList<>();
    private static Context baseContext;
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

    public static ArrayList<String> getSeeds() {
        return seeds;
    }

    public static void setHubs(ArrayList<Hub> hubs) {
        GlobalVariables.hubs = hubs;
    }

    public static ArrayList<Adjacency> getAdjacencies() {
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

    public static Context getBaseContext() {
        return baseContext;
    }

    public static void setBaseContext(Context baseContext) {
        GlobalVariables.baseContext = baseContext;
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


    private static int setAdjacenciesChanceModTwoEqualsZero(int lineHubCount, int i){
        if (lineHubCount % hubsPerLine == 0) {
            adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + 1)));
            return lineHubCount;
        } else {
            adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine)));
            return 1;
        }
    }

    public static void setAdjacencies() {
        int lineHubCount = 1;
        int chance = 0;

        for (int i = 0; i < hubs.size() - hubsPerLine; i++) {

            chance = Integer.parseInt(seeds.get(i));

            if (chance % 2 == 0) {
               /* if (lineHubCount % hubsPerLine == 0) {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + 1)));
                } else {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine)));
                    lineHubCount = 1;
                }*/
               lineHubCount = setAdjacenciesChanceModTwoEqualsZero(lineHubCount, i);
            }

            if (chance % 3 == 0) {
                adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine)));
            } else if (chance % 5 == 0) {
                if (lineHubCount == hubsPerLine) {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine)));
                } else {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine - 1)));
                }
            } else {
                adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + hubsPerLine - 1)));

            }
            lineHubCount++;
        }
        for (int i = hubs.size() - hubsPerLine; i < hubs.size() - 1; i++) {
            adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + 1)));
        }
    }

    public static String getIpAddress() {
        WifiManager wm = (WifiManager) baseContext.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }
}