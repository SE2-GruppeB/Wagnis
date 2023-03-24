package at.aau.wagnis;

import android.widget.Button;

import java.util.ArrayList;

public class Hub {
    int id;
    Button hubButton;
    ArrayList<Hub> adjacencies = new ArrayList<>();

    public Hub(Button hubButton) {
        this.hubButton = hubButton;
        this.id = hubButton.getId();
    }

    public ArrayList<Hub> getAdjacencies() {
        return adjacencies;
    }

    public Button getHubButton() {
        return hubButton;
    }

    public void addAdjacency(int adjacend){
        this.adjacencies.add(GlobalVariables.findHubById(adjacend));
    }

    public int getId() {
        return id;
    }
}
