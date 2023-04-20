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
    public void setHubImage(String agency){
        switch(agency){
            case("ESA"):
               this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome_x42);
                break;
            case("NASA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome6_x42);
                break;
            case("ISRO"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome5_x42);
                break;
            case("JAXA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome4_x42);
                break;
            case("Roskosmos"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome3_x42);
                break;
            case("China Manned Space Agency"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome2_x42);
                break;
            default:
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0,0,0, R.drawable.dome_x42);
        }
    }
}


