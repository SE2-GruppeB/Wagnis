package at.aau.wagnis;

import android.widget.Button;

import java.util.Map;

public class Hub {

    int id;
    Button hubButton;
    Player owner;

    public Hub(Button hubButton) {
        this.hubButton = hubButton;
        this.id = hubButton.getId();
    }

    public Button getHubButton() {
        return hubButton;
    }

    public int getId() {
        return id;
    }

    public void setText(int infantry, int cavalary, int artillery) {
        this.hubButton.setText(infantry + "/" + cavalary + "/" + artillery);
    }

    public Map<String, Integer> getTroops() {
        return null;
    }

    //to do:
    public void setOwner(Player hubOwner) {
        /*

         */
    }

    public void addHub(Hub targetHub) {
    }

    //Todo
    public void setTroops(Map<String, Integer> attackerTroops) {

    }

    public Player getOwner() {
        return this.owner;
    }
}


