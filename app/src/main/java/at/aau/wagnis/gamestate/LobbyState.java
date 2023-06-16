package at.aau.wagnis.gamestate;

import androidx.annotation.VisibleForTesting;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import at.aau.wagnis.Adjacency;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class LobbyState extends GameLogicState{
    static final int NUMBER_OF_HUBS=42;
    static final int MIN_SEEDVALUE_PER_HUB=10;
    static final int MIN_HUB_ID=100;                        /*0-indexing would mess up map generation badly*/
    static final int MAX_HUB_ID=MIN_HUB_ID+NUMBER_OF_HUBS;
    private int hubsPerLine;
    private List<Player> players;
    private HashMap<Integer,Hub> hubs;
    private List<Adjacency> adjacencies;
    private String seed;


    public LobbyState(){
        players = new ArrayList<>();
        hubs = new HashMap<>();
        adjacencies = new ArrayList<>();
        seedGenerator();
        hubs = generateHubs();
        hubsPerLine = (int) Math.ceil(hubs.size()/6f);
        setAdjacencies(seed);
    }

    public GameData getGameData() {
        GameData gameData = new GameData();
        gameData.setSeed(seed);
        gameData.setHubs(new ArrayList<>(hubs.values()));
        gameData.setPlayers(players);
        gameData.setAdjacencies(adjacencies);
        return gameData;
    }

    private void seedGenerator() {
        SecureRandom secureRandom = new SecureRandom(); //secureRandom because of SonarCloud
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < NUMBER_OF_HUBS; i++) {
            int s = 0;
            while (s <= MIN_SEEDVALUE_PER_HUB) {       //every two digits define one hub, therefore 42 Hubs = 84 digit seed
                s = secureRandom.nextInt(100);
            }
            stringBuilder.append(s);
        }
        this.seed = stringBuilder.toString();
    }

    @VisibleForTesting
    public void setAdjacencies(String seed){
        int lineHubCount = 0;
        int chance;
        boolean isConnected;
        int flag=0;
        List<String> seeds =splitSeed(seed);



        for(int i =MIN_HUB_ID;i<MAX_HUB_ID-hubsPerLine;i++){
            isConnected=false;
            lineHubCount++;

            chance = Integer.parseInt(seeds.get(i-100));

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
            adjacencies.add(new Adjacency(hubs.get(i+100), findHubById(hubs.get(i+100).getId() + 1)));                  /* +100 because of map*/
        }
    }

    private List<String> splitSeed(String seed) {
        List<String> seeds = new ArrayList<>();
        for(int i = 2; i <= seed.length(); i++){
            if(i%2==0){
                seeds.add(seed.substring(i-2,i));
            }
        }
        return seeds;
    }

    public Hub findHubById(int id) {
            return hubs.get(id);
    }

    private HashMap<Integer,Hub> generateHubs(){
        HashMap<Integer,Hub> generatedHubs = new HashMap<>();
        for(int i = MIN_HUB_ID; i < MAX_HUB_ID; i++){
            Hub hub = new Hub(i);
            generatedHubs.put(hub.getId(),hub);
        }
        return generatedHubs;
    }

    public String getSeed() {
        return seed;
    }

    public List<Hub> getHubs() {
        return new ArrayList<>(hubs.values());
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Adjacency> getAdjacencies() {
        return adjacencies;
    }

    public int getHubsPerLine() {
        return hubsPerLine;
    }

    public void addPlayer(String playerAddress){
        int playerID = this.players.size();
        Player player = new Player(playerID);
        players.add(player);
        gameServer.getGameData().addPlayerIdentifier(playerID, playerAddress);
    }

    @Override
    public void next(){
        gameServer.setGameLogicState(new StartGameState());
    }
}
