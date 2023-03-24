package at.aau.wagnis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.ThemedSpinnerAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlPullParser;

public class MainActivity extends AppCompatActivity {


    FloatingActionButton endTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideNavigationBar();

        endTurn = findViewById(R.id.btn_EndTurn);

       drawHubs("100;100/200;200/150;100");

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        hideNavigationBar();
    }
    private void hideNavigationBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    public void diceRollPopUp(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_diceroll, null);


        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
        int width = (int)px;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(popUp, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        Button btnBack = popUp.findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                return;
            }
        });


        NumberPicker n1 = popUp.findViewById(R.id.dice1);
        NumberPicker n2 = popUp.findViewById(R.id.dice2);
        NumberPicker n3 = popUp.findViewById(R.id.dice3);
        NumberPicker n4 = popUp.findViewById(R.id.dice4);
        NumberPicker n5 = popUp.findViewById(R.id.dice5);
        NumberPicker n6 = popUp.findViewById(R.id.dice6);

        NumberPicker[] dice = {n1,n2,n3,n4,n5,n6};

        int[] values ={1,2,3,4,5,6};
        setDice(dice,values);
    }

    public void setDice(NumberPicker[] dice, int[] values){
        for(int i = 0;i<dice.length;i++){
            dice[i].setMaxValue(6);
            dice[i].setMinValue(1);
            dice[i].setValue(values[i]);
        }
    }

    public void drawHubs(String seed){//seed: margin TOP; margin LEFT / distance to previous TOP;distance to previous LEFT;...
        ConstraintLayout layout = findViewById(R.id.layout_activity_main);
        ConstraintSet cs = new ConstraintSet();
        int nextId = 80;
        int lastTOP = 0;
        int lastLEFT = 0;

        for(String s : seed.split("/")){
            String[] coords = s.split(";");
            lastTOP = lastTOP+Integer.parseInt(coords[0]);
            lastLEFT = lastLEFT+Integer.parseInt(coords[1]);


            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            nextId++;
            hub.setId(nextId);
            hub.setText("Hub: "+hub.getId());
            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    diceRollPopUp(hub);
                }
            });
            layout.addView(hub);

            cs.clone(layout);
            cs.connect(hub.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.TOP,lastTOP);
            cs.connect(hub.getId(),ConstraintSet.LEFT,layout.getId(),ConstraintSet.LEFT,lastLEFT);
            cs.applyTo(layout);
        }
    }
}