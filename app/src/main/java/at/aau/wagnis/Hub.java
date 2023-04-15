package at.aau.wagnis;

import android.widget.Button;

import java.util.Map;

public class Hub {
    int id;
    Button hubButton;

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

    public Map<DefaultTroop, Integer> getTroops() {
        return null;
    }

    //to do:
    public void setOwner(Object hubOwner) {
        /*

         */
    }

    public void addHub(Hub targetHub) {
    }

    //Todo
    public void setTroops(Map<DefaultTroop, Integer> attackerTroops) {

    }

    public Player getownedHub() {
    return null;}
//Todo

    public Object getHubOwner() {
        return null;
    }

    public int getOwner() {
        return 0;
    }
}


