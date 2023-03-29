package at.aau.wagnis;

import android.widget.Button;

import java.util.ArrayList;

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

    public void setText(int infantry,int cavalary,int artillery){
        this.hubButton.setText(infantry+"/"+cavalary+"/"+artillery);
    }
}
