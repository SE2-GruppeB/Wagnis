package at.aau.wagnis;

import android.widget.Button;

import java.util.Objects;

public class Hub {

    private final int id;
    private final Button hubButton;
    private Player owner;
    private int amountTroops;

    public Hub(Button hubButton) {
        this.hubButton = hubButton;
        this.id = hubButton.getId();
        this.amountTroops = 0;
        this.owner = new Player();
    }

    public Hub(int id) {
        this.hubButton = null;
        this.id = id;
        this.amountTroops = 0;
        this.owner = new Player();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hub hub = (Hub) o;
        return id == hub.id && amountTroops == hub.amountTroops && Objects.equals(owner, hub.owner);
    }

    public Button getHubButton() {
        return this.hubButton;
    }

    public int getId() {
        return this.id;
    }

    public void setText(String troops) {
        this.hubButton.setText(troops);
    }

    public int getAmountTroops() {
        return this.amountTroops;
    }

    public void setAmountTroops(int amountTroops) {
        this.amountTroops = amountTroops;
    }

    //Todo
    public Player getOwner() {
        return this.owner;
    }

    //to do:
    public void setOwner(Player hubOwner) {
        this.owner = hubOwner;
    }

    public void setHubImage(String agency) {
        switch (agency) {
            case ("ESA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome_x42);
                break;
            case ("NASA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome6_x42);
                break;
            case ("ISRO"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome5_x42);
                break;
            case ("JAXA"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome4_x42);
                break;
            case ("Roskosmos"):
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome3_x42);
                break;
            default:
                this.getHubButton().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.dome2_x42);
        }
    }

    public void addTroops(int troops) {
        this.amountTroops += troops;
    }

    @Override
    public String toString() {
        return "Hub{" +
                "id=" + id +

                ", amountTroops=" + amountTroops +
                '}';
    }


}


