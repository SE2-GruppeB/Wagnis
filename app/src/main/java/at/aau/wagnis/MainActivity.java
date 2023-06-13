package at.aau.wagnis;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.net.Inet4Address;
import java.net.InetAddress;
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


    FloatingActionButton btnEndTurn;
    FloatingActionButton btnCards;
    FloatingActionButton btnSettings;
    FloatingActionButton btnChat;
    ImageView adjacencyView;
    GameData currentGameData;
    boolean wasDrawn = false;
    PopupWindow startpopup;
    TextView playerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });

        getGameManager().postCommand(new IdentifyCommand(GlobalVariables.getLocalIpAddress()));

        setContentView(R.layout.activity_main);

        adjacencyView = findViewById(R.id.adjacenciesView);
        btnEndTurn = findViewById(R.id.btn_EndTurn);
        btnCards = findViewById(R.id.btn_Cards);
        btnSettings = findViewById(R.id.btn_Settings);
        btnChat = findViewById(R.id.btn_Chat);


        setDisplayMetrics();

        btnSettings.setOnClickListener(view -> popupSettings());

        btnChat.setOnClickListener(view -> popupChat());
        

        //TODO: irgendwoher brauch ma den Player der den Button geklickt hat
        btnCards.setOnClickListener(view -> popupCards(new Player()));

        btnEndTurn.setOnClickListener(view -> {});


        ((WagnisApplication) getApplication()).getGameManager().setGameDataListener(newGameData -> runOnUiThread(() -> {
            /*Code to be executed on UI thread*/

            if (newGameData != null && currentGameData != null && !(currentGameData.getMessages().equals(newGameData.getMessages()))) {
                btnChat.setCustomSize(300);
            }

            currentGameData = newGameData;


                if (!wasDrawn) {
                    generateMap(currentGameData.getSeed());
                    popupStart(btnCards);
                    wasDrawn = true;
                } else {
                    for (Hub h : currentGameData.getHubs()) {
                        Hub uiHub = GlobalVariables.findHubById(h.getId());
                        uiHub.setText(h.getAmountTroops() + ", "+h.getId());
                        if (h.getOwner()!=null)// TODO check why this is null sometimes
                            uiHub.setHubImage(h.getOwner().getPlayerId() == 0 ? "ESA" : "NASA");
                    }
                }

                if(startpopup.isShowing()) {
                    updatePlayerCount();
                    if (!(currentGameData.getCurrentGameLogicState().equals("LobbyState"))) {
                        startpopup.dismiss();
                    }
                }
        }));



    }

    private void generateMap(String seed) {
        GlobalVariables.setSeed(seed);
        drawHubs(GlobalVariables.getSeed());
        GlobalVariables.setAdjacencies();
        drawAdjacencies();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /*System Bars appearing when focusable popups are opened appears to be a bug*/
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior( WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void setDisplayMetrics() {
        WindowMetrics windowMetrics = getWindowManager().getCurrentWindowMetrics();
        Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
        GlobalVariables.setDisplayWidthPx(windowMetrics.getBounds().width()-insets.top-insets.bottom);          /*width & height swapped because landscape mode*/
        GlobalVariables.setDisplayHeightPx(windowMetrics.getBounds().height()-insets.left-insets.right);
    }


    private int dpToPx(int dp){
       return dp *(getResources().getDisplayMetrics().densityDpi/160);
    }

    public PopupWindow createPopUp(int popupId) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(popupId, null);
        return new PopupWindow(popUp, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
                int[] v = {1,2,3,4,5};
                popupDiceRoll(v);
                GlobalVariables.findHubById(hub.getId()).setHubImage(GlobalVariables.getAgency());

                   if (lastClickedHub != null){
                        getGameManager().postCommand(new ChooseAttackCommand(lastClickedHub.getId(),hub.getId() ));
                        lastClickedHub = null;
                        Toast.makeText(MainActivity.this, "Targethub with id "+hub.getId()+" selected!", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, "Attack started...", Toast.LENGTH_SHORT).show();
                    }else{
                        lastClickedHub = hub;
                        Toast.makeText(MainActivity.this, "Sourcehub with id "+hub.getId()+" selected!\nSelect targethub!", Toast.LENGTH_LONG).show();
                        //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
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
        playerCount.setText("PlayerCount: "+ currentGameData.getPlayers().size());
    }

    public void popupStart(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.popup_start, null);
        startpopup = new PopupWindow(layout, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, false);
        Button btnClose = startpopup.getContentView().findViewById(R.id.btn_start);
        playerCount = startpopup.getContentView().findViewById(R.id.txtPlayerCount);
        ImageView qrCode = startpopup.getContentView().findViewById(R.id.qrCode);


        if (view.post(() -> startpopup.showAtLocation(new View(this), Gravity.CENTER, 0, 0))) { //Call popUp after setup has finished
            updatePlayerCount();
        }

        if (Boolean.TRUE.equals(GlobalVariables.getIsClient())) {
            btnClose.setEnabled(false);
        }

        btnClose.setOnClickListener(view1 -> {
            getGameManager().postCommand(new StartGameCommand());
            btnClose.setEnabled(false);
        });

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            BitMatrix mMatrix;
            if(Boolean.TRUE.equals(GlobalVariables.getIsClient())){
                mMatrix = mWriter.encode(GlobalVariables.getHostIP(), BarcodeFormat.QR_CODE, 500,500);
            }else{
                mMatrix = mWriter.encode(getIpAddress(), BarcodeFormat.QR_CODE, 500,500);
            }

            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
            qrCode.setImageBitmap(mBitmap);

        } catch (Exception e) {
            restart();
        }
    }
    private String getIpAddress() {
        ConnectivityManager manager = getSystemService(ConnectivityManager.class);
        Network network = manager.getActiveNetwork();
        LinkProperties prop = manager.getLinkProperties(network);
        for (LinkAddress linkAddress : prop.getLinkAddresses()){
            InetAddress inetAddress = linkAddress.getAddress();
            if(inetAddress instanceof Inet4Address){
                return inetAddress.getHostAddress();
            }
        }
        return "no fitting ip address found";
    }

    public void restart() {
        Intent restartActivity = new Intent(getApplicationContext(), MenuActivity.class);
        int pendingIntent = 123456;
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), pendingIntent,restartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);

        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public void popupSettings() {

        PopupWindow popupWindow = createPopUp(R.layout.popup_settings);

        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);
        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        btnClose.setOnClickListener(view -> popupWindow.dismiss());

        SwitchCompat switchMusic = popupWindow.getContentView().findViewById(R.id.switch_Music);
        switchMusic.setText(R.string.music);

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
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);

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
                    btn.setText(R.string.infantry);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.infantry, 0, 0);

                    break;
                case CAVALRY:
                    btn.setText(R.string.cavalry);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cavalry, 0, 0);

                    break;
                case ARTILLERY:
                    btn.setText(R.string.artillery);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.artillery, 0, 0);

                    break;
                default:
                    btn.setText(R.string.empty);
                    btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
                    break;
            }
        } catch (Exception e) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.placeholder_card, 0, 0);
        }
    }

    public void popupDiceRoll(int[] values) {
        PopupWindow popupWindow = createPopUp(R.layout.popup_diceroll);
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);


        Button btnBack = popupWindow.getContentView().findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(view -> popupWindow.dismiss());



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
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);

        Button btnClose = popupWindow.getContentView().findViewById(R.id.btn_Close);
        NumberPicker np = popupWindow.getContentView().findViewById(R.id.np_troops);
        np.setMaxValue(10);     //setMaxValue(Player.getUnassignedAvailableTroops)
        np.setMinValue(0);
        btnClose.setOnClickListener(view -> {
            int troops = np.getValue();
            Hub selected = GlobalVariables.findHubById(hubButton.getId());
            popupWindow.dismiss();
        });
    }

    public void popupMoveTroops() {
        PopupWindow popupWindow = createPopUp(R.layout.popup_movetroops);
        popupWindow.showAtLocation(new View(this), Gravity.CENTER, 0, 0);

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
        popupWindow.showAtLocation(new View(this
        ), Gravity.CENTER, 0, 0);

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