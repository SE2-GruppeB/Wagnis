package at.aau.wagnis.gamestate;

import androidx.annotation.VisibleForTesting;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import at.aau.wagnis.Adjacency;

import at.aau.wagnis.Hub;
import at.aau.wagnis.Player;

public class LobbyState extends GameLogicState{
    private int hubsPerLine;
    private List<Player> players;
    private List<Hub> hubs;
    private List<Adjacency> adjacencies;
    private String seed;

    public LobbyState(){
        players = new ArrayList<>();
        hubs = new ArrayList<>();
        adjacencies = new ArrayList<>();
        seedGenerator();
        hubs = new ArrayList<>(generateHubs());
        hubsPerLine = (int) Math.ceil(hubs.size()/6f);
        setAdjacencies(seed);
    }

    public GameData getGameData() {
        GameData gameData = new GameData();
        gameData.setSeed(seed);
        gameData.setHubs(hubs);
        gameData.setPlayers(players);

        return gameData;
    }

    private void seedGenerator() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 42; i++) {
            int s = 0;
            while (s <= 10) {
                s = secureRandom.nextInt(100);
            }
            stringBuilder.append(s);
        }
        this.seed = stringBuilder.toString();
    }

    @VisibleForTesting
    public void setAdjacencies(String seed){
        adjacencies = new ArrayList<>();
        int lineHubCount=1;
        int chance = 0;

        List<String> seeds = splitSeed(seed);
        
        for(int i =0;i<hubs.size()-hubsPerLine;i++){
            chance = Integer.parseInt(seeds.get(i));

            if(chance%2==0){
                if(lineHubCount%hubsPerLine==0) {
                    adjacencies.add(new Adjacency(hubs.get(i), findHubById(hubs.get(i).getId() + 1)));
                }else{
                    adjacencies.add(new Adjacency(hubs.get(i),findHubById(hubs.get(i).getId()+hubsPerLine)));
                    lineHubCount=1;
                }
            }
            if (chance %3==0){
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

    private List<String> splitSeed(String seed) {
        List<String> seeds = new ArrayList<>();
        for(int i = 2; i <= seed.length(); i++){
            if(i%2==0){
                seeds.add(seed.substring(i-2,i));
            }
        }
        return seeds;
    }

    public Hub findHubById(int id){
        for(Hub h : hubs){
            if(h.getId()==id){
                return h;
            }
        }
        return null;
    }

    private List<Hub> generateHubs(){
        List<Hub> generatedHubs = new ArrayList<>();
        for(int i = 100; i < 142; i++){
            Hub hub = new Hub(i);
            generatedHubs.add(hub);
        }
        return generatedHubs;
    }

    public String getSeed() {
        return seed;
    }

    public List<Hub> getHubs() {
        return hubs;
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

    public void addPlayer(Player player){
        players.add(player);
    }

    public void next(){
        GameData gameData = new GameData();
        gameData.setSeed(seed);
        gameData.setHubs(hubs);
        gameData.setPlayers(players);
        gameServer.setGameLogicState(new StartGameState(gameData));
    }
}
