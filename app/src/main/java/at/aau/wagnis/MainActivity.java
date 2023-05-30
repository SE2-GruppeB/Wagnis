package at.aau.wagnis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import at.aau.wagnis.application.GameManager;
import at.aau.wagnis.application.WagnisApplication;
import at.aau.wagnis.gamestate.GameData;


public class MainActivity extends AppCompatActivity {

    FloatingActionButton endTurn;
    FloatingActionButton btnCards;
    FloatingActionButton btnSettings;
    FloatingActionButton btnChat;
    ImageView adjacencyView;
    GameData currentState;
    boolean wasDrawn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalVariables.setBaseContext(this);

        adjacencyView = findViewById(R.id.adjacenciesView);
        endTurn = findViewById(R.id.btn_EndTurn);
        btnCards = findViewById(R.id.btn_Cards);
        btnSettings = findViewById(R.id.btn_Settings);
        btnChat = findViewById(R.id.btn_Chat);

        setDisplayMetrics();

        btnCards.setOnClickListener(view -> {
            popupCards();

        });
        btnSettings.setOnClickListener(view -> {
            popupSettings();

        });

        btnChat.setOnClickListener(view -> {
            popupChat();

        });

        ((WagnisApplication)getApplication()).getGameManager().setGameStateListener(newGameState -> runOnUiThread(() -> {
            // code to be executed on the UI thread
            currentState = newGameState;
            if(newGameState != null && !wasDrawn){
                    generateMap(newGameState.getSeed());
                    wasDrawn = true;
            }
        }));

        popupStart(btnCards);

    }
    private void generateMap(String seed) {
        GlobalVariables.setSeed(seed);
        drawHubs(GlobalVariables.getSeed());
        GlobalVariables.setAdjacencies();
        drawAdjacencies();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public void setDisplayMetrics(){
        DisplayMetrics displayMetrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        GlobalVariables.setDisplayWidthPx(displayMetrics.widthPixels);
        GlobalVariables.setDisplayHeightPx(displayMetrics.heightPixels);
    }

    public static int dpToPx(int dp){
        return dp *(GlobalVariables.getBaseContext().getResources().getDisplayMetrics().densityDpi/160);
    }
    public PopupWindow createPopUp(int popupId){

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        return new PopupWindow(popUp, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
    }
    private GameManager getGameManager() {
        return ((WagnisApplication) getApplication()).getGameManager();
    }

    public void drawHubs(String seed){

        ConstraintLayout layout = findViewById(R.id.layout_activity_main);
        ConstraintSet cs = new ConstraintSet();

        for (int i = 1; i <= seed.length(); i++) {
            if (i % 2 == 0) {
                GlobalVariables.seeds.add(seed.substring(i - 2, i));
            }
        }

        int hubs = 0;
        GlobalVariables.setHubsPerLine((int) Math.ceil(GlobalVariables.seeds.size() / 6f));
        int lineHubCount = 0;

        int hubWidthSpace = (GlobalVariables.getDisplayWidthPx() - dpToPx(100)) / GlobalVariables.getHubsPerLine();
        int height = GlobalVariables.getDisplayHeightPx();
        int heightSpace = height / 6;

        for (String s : GlobalVariables.seeds) {
            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            hub.setId(100 + hubs);

            hub.setOnClickListener(view -> {
                /* Testing Grounds;*/
                int[] v = {1,2,3,4,5};
                popupDiceRoll(v);
                GlobalVariables.findHubById(hub.getId()).setHubImage(GlobalVariables.getAgency());

                System.out.println("Hub:" +hub.getId());

            });
            GlobalVariables.hubs.add(new Hub(hub));
            layout.addView(hub);

            int top = (hubs / GlobalVariables.getHubsPerLine()) * heightSpace;
            int pos = hubWidthSpace / 100 * Integer.parseInt(s);
            int left = hubWidthSpace * lineHubCount + pos;

            cs.clone(layout);
            cs.connect(hub.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, top);
            cs.connect(hub.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, left);
            cs.applyTo(layout);
            hubs++;
            lineHubCount++;
            if (lineHubCount == GlobalVariables.getHubsPerLine()) {
                lineHubCount = 0;
            }
        }
    }

    public void drawAdjacencies() {
        int height = GlobalVariables.getDisplayHeightPx();
        int width = GlobalVariables.getDisplayWidthPx();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setAntiAlias(true);

        for (Adjacency adjacency : GlobalVariables.adjacencies) {
            int pxWidth = dpToPx(21);
            int pxHeight = dpToPx(60);

            int startX = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int startY = ((ConstraintLayout.LayoutParams) adjacency.getHub1().getHubButton().getLayoutParams()).topMargin + pxHeight;
            int endX = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).leftMargin + pxWidth;
            int endY = ((ConstraintLayout.LayoutParams) adjacency.getHub2().getHubButton().getLayoutParams()).topMargin + pxHeight;
            canvas.drawLine(startX, startY, endX, endY, paint);

        }
        adjacencyView.setImageBitmap(bitmap);
    }
    public void popupStart(View view){
        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.popup_start, null);//root null since no ui element should exist at that time
        final PopupWindow popupWindow = new PopupWindow(layout ,  FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,false);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_start);
        TextView playerCount = popupWindow.getContentView().findViewById(R.id.txtPlayerCount);
        ImageView qrCode = popupWindow.getContentView().findViewById(R.id.qrCode);

       view.post(() -> popupWindow.showAtLocation(view,Gravity.CENTER, 0, 0));//Call popUp after setup has finished

        if(GlobalVariables.getIsClient()){
            btnClose.setEnabled(false);
        }
       //TODO: update playerCount
        btnClose.setOnClickListener(view1 -> {
            //TODO:startNewGame

            popupWindow.dismiss();
        });

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix = mWriter.encode(GlobalVariables.getIpAddress(), BarcodeFormat.QR_CODE, 500,500);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            qrCode.setImageBitmap(mBitmap);

        } catch (Exception e) {
            restart();
        }
    }

    public void restart(){
        Intent restartActivity = new Intent(getApplicationContext(), MenuActivity.class);
        int pendingIntent = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), pendingIntent,restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
    public void popupSettings(){

        PopupWindow popupWindow= createPopUp(R.layout.popup_settings);

        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnClose.setOnClickListener(view -> {
            popupWindow.dismiss();
        });

        SwitchCompat switchMusic = popupWindow.getContentView().findViewById(R.id.switch_Music);
        switchMusic.setTextOn("On");
        switchMusic.setTextOff("Off");

        switchMusic.setChecked(GlobalVariables.getMediaPlayer().isPlaying());

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                GlobalVariables.getMediaPlayer().start();
            }else{
                GlobalVariables.getMediaPlayer().pause();
            }
        });

        FloatingActionButton btnRestart = popupWindow.getContentView().findViewById(R.id.btn_Restart);
        btnRestart.setOnClickListener(view -> restart());

    }
    public  void popupCards(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_cards);
        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);

        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnBack.setOnClickListener(view -> {
            popupWindow.dismiss();
        });

        Cards c1 = new Cards(4000,Troops.INFANTRY,null);
        Cards c2 = new Cards(4001,Troops.ARTILLERY,null);
        Cards c3 = new Cards(4002,Troops.CAVALRY,null);
        Cards[] cards = {c1,c2,c3};

        Button btn0 = popupWindow.getContentView().findViewById(R.id.btn_Card0);
        Button btn1 = popupWindow.getContentView().findViewById(R.id.btn_Card1);
        Button btn2 = popupWindow.getContentView().findViewById(R.id.btn_Card2);
        Button btn3 = popupWindow.getContentView().findViewById(R.id.btn_Card3);
        Button btn4 = popupWindow.getContentView().findViewById(R.id.btn_Card4);
        Button[] btns = {btn0,btn1,btn2,btn3,btn4};

        for(int i = 0;i<btns.length;i++){

            try {
                switch (cards[i].getType()) {
                    case INFANTRY:
                        btns[i].setText(R.string.infantry);
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.infantry, 0, 0);

                        break;
                    case CAVALRY:
                        btns[i].setText(R.string.cavalry);
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cavalry, 0, 0);

                        break;
                    case ARTILLERY:
                        btns[i].setText(R.string.artillery);
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.artillery, 0, 0);

                        break;
                    default:
                        btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
                        break;
                }
            }catch(Exception e ){
                btns[i].setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
            }

        }

    }

    public  void popupDiceRoll(int[] values) {
        PopupWindow popupWindow= createPopUp(R.layout.popup_diceroll);
        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);


        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(view -> {
            popupWindow.dismiss();
        });


        NumberPicker n1 = popupWindow.getContentView().findViewById(R.id.dice1);
        NumberPicker n2 = popupWindow.getContentView().findViewById(R.id.dice2);
        NumberPicker n3 = popupWindow.getContentView().findViewById(R.id.dice3);
        NumberPicker n4 = popupWindow.getContentView().findViewById(R.id.dice4);
        NumberPicker n5 = popupWindow.getContentView().findViewById(R.id.dice5);

        NumberPicker[] dice = {n1,n2,n3,n4,n5};
        setDice(dice,values);
    }

    public static void setDice(NumberPicker[] dice, int[] values){
        for(int i = 0;i<dice.length;i++){
            dice[i].setMaxValue(6);
            dice[i].setMinValue(1);
            dice[i].setValue(values[i]);
        }
    }

    public void popupReinforceTroops(Button hubButton){
        PopupWindow popupWindow= createPopUp(R.layout.popup_movetroops);
        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);

        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        NumberPicker np = popupWindow.getContentView().findViewById(R.id.np_troops);
        np.setMaxValue(10);     //setMaxValue(Player.getUnassignedAvailableTroops)
        np.setMinValue(0);
        btnClose.setOnClickListener(view -> {
            int troops = np.getValue();
            //player.setUnassindeAvailableTroops(=-troops)      //delete now used troops
            Hub selected = GlobalVariables.findHubById(hubButton.getId());
            //selected.setText(selected.getHubButton().getText().toString()+troops);
            //selected.setAmountTroops(selected.getAmmountTroops+troops);   //set new troop count
            popupWindow.dismiss();
        });
    }

    public void popupMovetroops(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_movetroops);
        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        NumberPicker np = popupWindow.getContentView().findViewById(R.id.np_troops);
        np.setMaxValue(10);     //setMaxValue(Hub.getAmountTroops)
        np.setMinValue(1);
        btnClose.setOnClickListener(view -> {

            int troops = np.getValue();
            popupWindow.dismiss();
        });
    }
    public  void popupChat(){
        PopupWindow popupWindow= createPopUp(R.layout.popup_chat);
        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);
        Button btnExit = popupWindow.getContentView().findViewById(R.id.btn_Exit);
        Button btnSend = popupWindow.getContentView().findViewById(R.id.btn_Send);
        TextView msg = popupWindow.getContentView().findViewById(R.id.chatMsg);
        EditText sendMsg = popupWindow.getContentView().findViewById(R.id.sendMsg);
        btnExit.setOnClickListener(view -> {
            popupWindow.dismiss();
            return;
        });

        btnSend.setOnClickListener(view -> {
            System.out.println(sendMsg.getText());
        });
    }
}