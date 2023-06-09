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
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import at.aau.wagnis.application.GameManager;
import at.aau.wagnis.application.WagnisApplication;
import at.aau.wagnis.gamestate.ChatMessage;
import at.aau.wagnis.gamestate.GameData;

import at.aau.wagnis.server.communication.command.ChooseAttackCommand;
import at.aau.wagnis.server.communication.command.IdentifyCommand;

import at.aau.wagnis.server.communication.command.ProcessChatMessageCommand;
import at.aau.wagnis.server.communication.command.StartGameCommand;


public class MainActivity extends AppCompatActivity {

    FloatingActionButton endTurn;
    FloatingActionButton btnCards;
    FloatingActionButton btnSettings;
    FloatingActionButton btnChat;
    ImageView adjacencyView;
    GameData currentGameData;
    boolean wasDrawn = false;
    PopupWindow startpopup;
    TextView playerCount;
    ArrayList<Hub> clicked= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGameManager().postCommand(new IdentifyCommand(GlobalVariables.getLocalIpAddress()));

        setContentView(R.layout.activity_main);
        GlobalVariables.setBaseContext(this);

        adjacencyView = findViewById(R.id.adjacenciesView);
        endTurn = findViewById(R.id.btn_EndTurn);
        btnCards = findViewById(R.id.btn_Cards);
        btnSettings = findViewById(R.id.btn_Settings);
        btnChat = findViewById(R.id.btn_Chat);

        btnCards.setVisibility(View.GONE);

        setDisplayMetrics();

        btnSettings.setOnClickListener(view -> popupSettings());

        btnChat.setOnClickListener(view -> popupChat());

        btnCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupCards(new Player());
            }//TODO: irgendwoher brauch ma den Player der den Button geklickt hat
        });


        ((WagnisApplication) getApplication()).getGameManager().setGameDataListener(newGameData -> runOnUiThread(() -> {


            if (newGameData != null && currentGameData != null && !(currentGameData.getMessages().equals(newGameData.getMessages()))) {
                btnChat.setCustomSize(300);
            }

            // code to be executed on the UI thread
            currentGameData = newGameData;
            if (newGameData != null) {
                //System.out.println(currentState.getMessages());

                try {
                    System.out.println("Main"+currentGameData.getCurrentGameLogicState());
                    if (startpopup.isShowing()) {
                        updatePlayerCount();
                        //playerCount.setText("PlayerCount: "+currentState.getPlayers().size());
                    }

                    if (!(currentGameData.getCurrentGameLogicState().equals("LobbyState"))&&startpopup.isShowing()) {
                        startpopup.dismiss();
                    }
                }catch (Exception e){
                    /**StartPopup already dismissed*/
                }
            }

            if (!wasDrawn) {
                generateMap(newGameData.getSeed());
                wasDrawn = true;
            } else {
                for (Hub h : currentGameData.getHubs()) {
                    Hub uiHub = GlobalVariables.findHubById(h.getId());
                    uiHub.setText(h.getAmountTroops() + ", "+h.getId());
                    if (h.getOwner()!=null)// TODO check why this is null sometimes
                        uiHub.setHubImage(h.getOwner().getPlayerId() == 0 ? "ESA" : "NASA");
                }
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
        if (hasFocus) {
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

    public void setDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        GlobalVariables.setDisplayWidthPx(displayMetrics.widthPixels);
        GlobalVariables.setDisplayHeightPx(displayMetrics.heightPixels);
    }


    private int dpToPx(int dp){
        return dp *(getResources().getDisplayMetrics().densityDpi/160);

    }

    public PopupWindow createPopUp(int popupId) {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        return new PopupWindow(popUp, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
    }

    private GameManager getGameManager() {
        return ((WagnisApplication) getApplication()).getGameManager();
    }


    private Button lastClickedHub = null;

    public void drawHubs(String seed) {

        ConstraintLayout layout = findViewById(R.id.layout_activity_main);
        ConstraintSet cs = new ConstraintSet();

        for (int i = 1; i <= seed.length(); i++) {
            if (i % 2 == 0) {
                GlobalVariables.getSeeds().add(seed.substring(i - 2, i));
            }
        }

        int hubs = 0;
        GlobalVariables.setHubsPerLine((int) Math.ceil(GlobalVariables.getSeeds().size() / 6f));
        int lineHubCount = 0;

        int hubWidthSpace = (GlobalVariables.getDisplayWidthPx() - dpToPx(100)) / GlobalVariables.getHubsPerLine();
        int height = GlobalVariables.getDisplayHeightPx();
        int heightSpace = height / 6;

        for (String s : GlobalVariables.getSeeds()) {
            Button hub = new Button(new ContextThemeWrapper(this, R.style.btn_hub_style), null, R.style.btn_hub_style);
            hub.setId(100 + hubs);

            hub.setOnClickListener(view -> {
                /* Testing Grounds;*/
                int[] v = {1,2,3,4,5};
                popupDiceRoll(v);
                GlobalVariables.findHubById(hub.getId()).setHubImage(GlobalVariables.getAgency());

                   /* System.out.println("CLICKER: "+hub.getId());
                    clicked.add(new Hub(hub));


                    if(clicked.size()==2){
                        System.out.println("TESTING: "+clicked.get(0)+", "+clicked.get(1));

                        for(Adjacency a :GlobalVariables.adjacencies){
                            if(a.getHub1().getId()==clicked.get(0).getId()&&a.getHub2().getId()==clicked.get(1).getId()||
                                    a.getHub1().getId()==clicked.get(1).getId()&&a.getHub2().getId()==clicked.get(0).getId()){
                                System.out.println("CONNECTION: "+ a.getHub1().getId()+", "+a.getHub2().getId());
                            }
                        }

                        clicked.clear();
                    }*/

                   if (lastClickedHub != null){
                        getGameManager().postCommand(new ChooseAttackCommand(lastClickedHub.getId(),hub.getId() ));
                        lastClickedHub = null;
                    }else{
                        lastClickedHub = hub;
                    }


            });
            GlobalVariables.getHubs().add(new Hub(hub));
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

        for (Adjacency adjacency : GlobalVariables.getAdjacencies()) {
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

    private void updatePlayerCount() {
        if (startpopup != null && startpopup.isShowing()) {
            playerCount.setText("PlayerCount: " + currentGameData.getPlayers().size());
        }
    }

    public void popupStart(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.popup_start, null);
        startpopup = new PopupWindow(layout, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, false);
        Button btnClose = startpopup.getContentView().findViewById(R.id.btn_start);
        playerCount = startpopup.getContentView().findViewById(R.id.txtPlayerCount);
        ImageView qrCode = startpopup.getContentView().findViewById(R.id.qrCode);


        if (view.post(() -> startpopup.showAtLocation(view, Gravity.CENTER, 0, 0))) { //Call popUp after setup has finished
            updatePlayerCount();

        }

        if (GlobalVariables.getIsClient()) {
            btnClose.setEnabled(false);
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGameManager().postCommand(new StartGameCommand());
                btnClose.setEnabled(false);
                //popupWindow.dismiss();
            }

        });

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix;
            if(GlobalVariables.getIsClient()){
                mMatrix = mWriter.encode(GlobalVariables.getHostIP(), BarcodeFormat.QR_CODE, 500,500);
            }else{
                mMatrix = mWriter.encode(GlobalVariables.getIpAddress(), BarcodeFormat.QR_CODE, 500,500);
            }

            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            qrCode.setImageBitmap(mBitmap);

        } catch (Exception e) {
            restart();
        }
    }

    public void restart() {
        Intent restartActivity = new Intent(getApplicationContext(), MenuActivity.class);
        int pendingIntent = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), pendingIntent,restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);

        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public void popupSettings() {

        PopupWindow popupWindow = createPopUp(R.layout.popup_settings);

        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnClose.setOnClickListener(view -> popupWindow.dismiss());

        Switch switchMusic = popupWindow.getContentView().findViewById(R.id.switch_Music);
        switchMusic.setText("Music");

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

    public void popupCards(Player player) {
        PopupWindow popupWindow = createPopUp(R.layout.popup_cards);
        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);

        Button btnPlay = popupWindow.getContentView().findViewById(R.id.btn_play);
        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnBack.setOnClickListener(view -> popupWindow.dismiss());

        Cards[] cards = player.getHand();

        Button[] btns = new Button[5];
        btns[0] = popupWindow.getContentView().findViewById(R.id.btn_Card0);
        btns[1] = popupWindow.getContentView().findViewById(R.id.btn_Card1);
        btns[2] = popupWindow.getContentView().findViewById(R.id.btn_Card2);
        btns[3] = popupWindow.getContentView().findViewById(R.id.btn_Card3);
        btns[4] = popupWindow.getContentView().findViewById(R.id.btn_Card4);

        boolean[] btnsPressed = new boolean[5];
        AtomicInteger countOfBtnsPressed = new AtomicInteger();

        updateCards(btns, cards);

        for (int i = 0; i < btns.length; i++) {
            int index = i;
            btns[i].setOnClickListener(view -> {
                if (Boolean.TRUE.equals(btnsPressed[index])) {
                    btnsPressed[index] = false;
                    countOfBtnsPressed.getAndDecrement();
                } else {
                    if (countOfBtnsPressed.get() < 4) {
                        btnsPressed[index] = true;
                        countOfBtnsPressed.getAndIncrement();
                    }
                }
            });
        }


        btnPlay.setOnClickListener(view -> {
            if (countOfBtnsPressed.get() < 4) {
                int[] chosenBtns = new int[3];
                int counter = 0;
                for (int i = 0; i < chosenBtns.length; i++) {
                    for (; counter < btns.length; counter++) {
                        if (btnsPressed[counter]) {
                            chosenBtns[i] = counter;
                            break;
                        }
                    }
                }
                player.useCards(chosenBtns[0], chosenBtns[1], chosenBtns[2]);
                updateCards(btns, cards);
            }
        });
    }

    public void updateCards(Button[] btns, Cards[] cards) {
        for (int i = 0; i < btns.length; i++) {
            drawCards(cards[i], btns[i]);
        }
    }

    public void drawCards(Cards card, Button btn) {
        try {
            switch (card.getType()) {
                case INFANTRY:
                    btn.setText("Infantry");
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.infantry, 0, 0);

                    break;
                case CAVALRY:
                    btn.setText("Cavallary");
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cavalry, 0, 0);

                    break;
                case ARTILLERY:
                    btn.setText("Artillery");
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.artillery, 0, 0);

                    break;
                default:
                    btn.setText("Empty");
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
                    break;
            }
        } catch (Exception e) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
        }
    }

    public void popupDiceRoll(int[] values) {
        PopupWindow popupWindow = createPopUp(R.layout.popup_diceroll);
        popupWindow.showAtLocation(new View(GlobalVariables.getBaseContext()), Gravity.CENTER, 0, 0);


        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });



        NumberPicker n1 = popupWindow.getContentView().findViewById(R.id.dice1);
        NumberPicker n2 = popupWindow.getContentView().findViewById(R.id.dice2);
        NumberPicker n3 = popupWindow.getContentView().findViewById(R.id.dice3);
        NumberPicker n4 = popupWindow.getContentView().findViewById(R.id.dice4);
        NumberPicker n5 = popupWindow.getContentView().findViewById(R.id.dice5);

        NumberPicker[] dice = {n1, n2, n3, n4, n5};
        setDice(dice, values);
    }

    public static void setDice(NumberPicker[] dice, int[] values) {
        for (int i = 0; i < dice.length; i++) {
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
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int troops = np.getValue();
                //player.setUnassindeAvailableTroops(=-troops)      //delete now used troops
                Hub selected = GlobalVariables.findHubById(hubButton.getId());
                //selected.setText(selected.getHubButton().getText().toString()+troops);
                //selected.setAmountTroops(selected.getAmmountTroops+troops);   //set new troop count
                popupWindow.dismiss();
            }
        });
    }

    public void popupMovetroops() {
        PopupWindow popupWindow = createPopUp(R.layout.popup_movetroops);
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

        btnChat.clearCustomSize();

        Button btnExit = popupWindow.getContentView().findViewById(R.id.btn_Exit);
        Button btnSend = popupWindow.getContentView().findViewById(R.id.btn_Send);

        TextView msg = popupWindow.getContentView().findViewById(R.id.chatMsg);
        if(currentGameData != null) {
            String messages = currentGameData.getMessages()

                    .stream()
                    .map(ChatMessage::toString)
                    .collect(Collectors.joining("\n"));

            msg.setText(messages);
        }

        EditText sendMsg = popupWindow.getContentView().findViewById(R.id.sendMsg);
        btnExit.setOnClickListener(view -> popupWindow.dismiss());

        btnSend.setOnClickListener(view -> getGameManager().postCommand(new ProcessChatMessageCommand(sendMsg.getText().toString())));
    }
}