package com.example.mdpandroidcontroller;

import static java.lang.Integer.parseInt;

import android.Manifest;
import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import java.util.Map;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdpandroidcontroller.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static Context context;

    private static final String[] BL_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN
    };
    private static final int BT1_PERMISSION_CODE = 101;
    private int clickedObstacleNumber = 0;
    private static final int BT2_PERMISSION_CODE = 102;
    private static final int BT3_PERMISSION_CODE = 103;
    private static final int BT4_PERMISSION_CODE = 104;
    private static final int BT5_PERMISSION_CODE = 105;
    private static final int BT6_PERMISSION_CODE = 106;
    private static final int BT7_PERMISSION_CODE = 107;
    private static final int request_code = 200;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private int[] latestHighlightedCellCoordinates = new int[]{0, 0};

    TextView mStatusBlueTv;
    TextView mPairedTv;

    Button mOnBtn;
    Button mOffBtn;
    Button mDiscoverBtn;
    Button mPairedBtn;

    ListView listview;

    ListView listview_paireddevices;
    ListView listview_availabledevices;
    ArrayList<String> availabledevicelist = new ArrayList<>();
    ArrayList<String> paireddevicelist = new ArrayList<>();
    ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ArrayList<BluetoothDevice> mPairDevices = new ArrayList<>();

    /*public static Map<String, String> obstacleIDs;
    static {
        obstacleIDs = new HashMap<>();
        obstacleIDs.put("11", "1");
        obstacleIDs.put("12", "2");
        obstacleIDs.put("13", "3");
        obstacleIDs.put("14", "4");
        obstacleIDs.put("15", "5");
        obstacleIDs.put("16", "6");
        obstacleIDs.put("17", "7");
        obstacleIDs.put("18", "8");
        obstacleIDs.put("19", "9");
        obstacleIDs.put("20", "A");
        obstacleIDs.put("21", "B");
        obstacleIDs.put("22", "C");
        obstacleIDs.put("23", "D");
        obstacleIDs.put("24", "E");
        obstacleIDs.put("25", "F");
        obstacleIDs.put("26", "G");
        obstacleIDs.put("27", "H");
        obstacleIDs.put("28", "S");
        obstacleIDs.put("29", "T");
        obstacleIDs.put("30", "U");
        obstacleIDs.put("31", "V");
        obstacleIDs.put("32", "W");
        obstacleIDs.put("33", "X");
        obstacleIDs.put("34", "Y");
        obstacleIDs.put("35", "Z");
        obstacleIDs.put("36", "^");
        obstacleIDs.put("37", "\/");
        obstacleIDs.put("38", "->");
        obstacleIDs.put("39", "<-");
        obstacleIDs.put("40", "");
    }*/


    private static final String TAG = "btlog";

    private static final UUID my_uuid_insecure = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    BluetoothDevice mBTDevice;
    BluetoothDevice mPairDevice;
    BluetoothAdapter myBluetoothAdapter;

    //BluetoothConnectionService mBluetoothConnection;

    // grid stuff
    private static MapArena map;
    private static int mapLeft; // only at robot generate button
    private static int mapTop;
    private static int rotation = 0;
    private static ConstraintLayout obstacle1Grp;
    private static ImageView obstacle1Box;
    private static ImageView obstacle1Face;
    private static TextView obstacle1Id;

    private static ConstraintLayout obstacle2Grp;
    private static ImageView obstacle2Box;
    private static ImageView obstacle2Face;
    private static TextView obstacle2Id;

    private static ConstraintLayout obstacle3Grp;
    private static ImageView obstacle3Box;
    private static ImageView obstacle3Face;
    private static TextView obstacle3Id;

    private static ConstraintLayout obstacle4Grp;
    private static ImageView obstacle4Box;
    private static ImageView obstacle4Face;
    private static TextView obstacle4Id;

    private static ConstraintLayout obstacle5Grp;
    private static ImageView obstacle5Box;
    private static ImageView obstacle5Face;
    private static TextView obstacle5Id;

    private static ConstraintLayout obstacle6Grp;
    private static ImageView obstacle6Box;
    private static ImageView obstacle6Face;
    private static TextView obstacle6Id;

    private static ConstraintLayout obstacle7Grp;
    private static ImageView obstacle7Box;
    private static ImageView obstacle7Face;
    private static TextView obstacle7Id;

    private static ConstraintLayout obstacle8Grp;
    private static ImageView obstacle8Box;
    private static ImageView obstacle8Face;
    private static TextView obstacle8Id;

    private static ConstraintLayout obstacle10Grp;
    private static ImageView obstacle10Box;
    private static ImageView obstacle10Face;
    private static TextView obstacle10Id;

    private static ImageView obstacleFaceCur;

    private static String obstacleFaceText;
    private static int obstacleFaceNumber;

    private static TextView outputNotifView; // for all the notifications!!
    private static TextView locationNotifView;
    private static String outputNotif;
    private static String locationNotif;

    private static String instruction = "Robot, 4, 10, S";

    private static ConstraintLayout popup;
    private static ConstraintLayout robot_popup;

    private static int robotColPopup = 1;
    private static int robotRowPopup = 1;
    private static String robotFacingPopup = "N";

    private static ImageView robot;
    float pastX, pastY;
    private static String longPress;

    private Runnable runnable;
    private Handler handler;

    TextView incomingMessages;
    StringBuilder messages;
    Intent connectIntent;


    private static int[][] originalObstacleCoords = new int[20][2];
    private static int[] originalObstacleCoordinates2 = new int[2];
    // Key: osbtacleNumber; value: obstacle ID generated by View.generateViewId()
    private static Map <Integer, Integer> obstacleIds = new HashMap<>();
    private static int[][] currentObstacleCoords = new int[20][2]; // remember to expand this
    private static Map <Integer, int[]> latestObstacleCoordinates = new HashMap<>();

    // this one is for constraint
    private Map <Integer, ConstraintLayout> obstacleViews = new HashMap<>(); // cant be static!! - COS ITS REGENRATED ALL THE TIME - change eventually.
    private Map <Integer, ImageView> obstacleFaceViews2 = new HashMap<>();
    private Map <Integer, TextView> obstacleTextViews = new HashMap<>();
    private List<ImageView> obstacleBoxViews = new ArrayList<>();

    boolean connectedState = false;
    static String connectedDevice;
    BluetoothDevice myBTConnectionDevice;
    boolean currentActivity;

    private boolean mBound = false;

    //UUID
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.e("Activity result", "OK");
                    // There are no request codes
                    Intent data = result.getData();
                }
            });
    private View.OnTouchListener obstacleOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                pastX = view.getX();
                pastY = view.getY();
                // return true;
                // } else {
                //    return false;
                //}
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        TableLayout obstacleInformationTable = findViewById(R.id.obstacleInformation);

        //mStatusBlueTv = findViewById(R.id.statusBluetoothTv);

        //REGISTER BROADCAST RECEIVER FOR INCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));
        connectedDevice = null;
        currentActivity = true;

        //REGISTER BROADCAST RECEIVER FOR IMCOMING MSG
        //LocalBroadcastManager.getInstance(this).registerReceiver(incomingMsgReceiver, new IntentFilter("IncomingMsg"));




        //GUI

        map = findViewById(R.id.mapView);
        for (int i = 0; i < originalObstacleCoords.length; i++) {
            for (int j = 0; j < originalObstacleCoords[i].length; j++) {
                originalObstacleCoords[i][j] = -1;
            }
        }

        //TEST INSTRUCTION
        if (!Constants.instruction.equals("null")) {
            System.out.println("AT VIEW CREATE");
            System.out.println(Constants.instruction);
            executeInstruction();
        }


        //OLD ON VIEW CREATED


        System.out.println("OnViewCreated");

        //LISTEN FOR COMMANDS
        MySubject subject = new MySubject();
        MyObserver observer = new MyObserver(subject);
        Constants constants = new Constants(subject);

        ConstraintLayout initialObstacleGrp = (ConstraintLayout) findViewById(R.id.initialObstacle);
        ImageView initialObstacleBox = (ImageView) findViewById(R.id.initialObstacleBox);
        ImageView initialObstacleFace = (ImageView) findViewById(R.id.initialObstacleFace);
        TextView initialObstacleId = (TextView) findViewById(R.id.initialObstacleId);
        initialObstacleId.setTag("Obstacle 1 text view");
        TextView obstacleCoordinatesTextView = (TextView) findViewById(R.id.obstacleCoordinates);
        obstacleViews.put(1, initialObstacleGrp);
        System.out.println(initialObstacleGrp);
        System.out.println("obstacle views");
        System.out.println(obstacleViews.size());
        obstacleBoxViews.add(initialObstacleBox);
        obstacleFaceViews2.put(1, initialObstacleFace);
        obstacleTextViews.put(1, initialObstacleId);
        Button preloadObstaclesButton = (Button) findViewById(R.id.preloadObstaclesButton);
        Spinner spinner = findViewById(R.id.numberOfPreloadedObstacles);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.number_of_obstacles_to_preload,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ViewGroup parentView = (ViewGroup) map.getParent();
        preloadObstaclesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = spinner.getSelectedItem().toString();
                int numberOfObstacles = 0;
                try {
                    numberOfObstacles = Integer.parseInt(userInput);}
                catch (NumberFormatException e) {
                    numberOfObstacles = 0;
                }
                int mapCellSize = (int) map.getCellSize();
                int xMapCoordinate = 19;
                int yMapCoordinate = 1;
                for (int j = 0; j < numberOfObstacles; j++) {
                    int newObstacleNumber = obstacleViews.size() + 1;
                    while (obstacleViews.get(newObstacleNumber) != null) {
                        newObstacleNumber++;
                    }
                    ConstraintLayout newObstacle = createNewObstacle(newObstacleNumber);
                    ConstraintLayout fullScreen = findViewById(R.id.fullScreen);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(fullScreen);
                    constraintSet.clear(newObstacle.getId(), ConstraintSet.START);
                    constraintSet.clear(newObstacle.getId(), ConstraintSet.END);
                    constraintSet.clear(newObstacle.getId(), ConstraintSet.TOP);
                    constraintSet.clear(newObstacle.getId(), ConstraintSet.BOTTOM);
                    constraintSet.connect(newObstacle.getId(), ConstraintSet.START, R.id.fullScreen, ConstraintSet.START);
                    constraintSet.connect(newObstacle.getId(), ConstraintSet.TOP, R.id.fullScreen, ConstraintSet.TOP);
                    // Apply the constraints to the parent ConstraintLayout
                    constraintSet.applyTo(fullScreen);
                    int xCoordinate = ((xMapCoordinate + 1) * mapCellSize) + (int) map.getX();
                    int yCoordinate = ((19 - yMapCoordinate) * mapCellSize) + (int) map.getY();
                    map.insertNewObstacleIntoArena(newObstacleNumber, xCoordinate, yCoordinate);
                    latestObstacleCoordinates.put(newObstacleNumber, new int[] {xCoordinate, yCoordinate});
                    newObstacle.setX(xCoordinate);
                    newObstacle.setY(yCoordinate);
                    map.generateObstacleInformationTableRows(obstacleInformationTable, obstacleViews, parentView, outputNotifView, latestObstacleCoordinates);
                    map.invalidate();
                    // Notification
                    outputNotif = String.format("Obstacle: %d, Col: %d, Row: %d", newObstacleNumber, xMapCoordinate, yMapCoordinate);
                    outputNotifView.setText(outputNotif);
                    /*if (Constants.connected) {
                        byte[] bytes = outputNotif.getBytes(Charset.defaultCharset());
                        BluetoothChat.writeMsg(bytes);
                    }*/
                    yMapCoordinate += 2;
                    // Check that popup still opens (if branch)
                }
            }
        }
        );

        //TEXTVIEWS
        outputNotifView = (TextView) findViewById(R.id.notifications);
        locationNotifView = (TextView) findViewById(R.id.robot_location);

        popup = (ConstraintLayout) findViewById(R.id.popup_window);
        popup.setVisibility(View.INVISIBLE);

        robot_popup = (ConstraintLayout) findViewById(R.id.popup_window_robot);
        robot_popup.setVisibility(View.INVISIBLE);

        printAllObstacleCoords();

        initialObstacleGrp.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("current coordinates");
                printAllObstacleCoords();
                // check where to add this paragraph
                initialObstacleBox.getLayoutParams().height = (int) map.getCellSize();
                initialObstacleBox.getLayoutParams().width = (int) map.getCellSize();
                initialObstacleBox.requestLayout();
                initialObstacleFace.getLayoutParams().height = (int) map.getCellSize();
                initialObstacleFace.getLayoutParams().width = (int) map.getCellSize();
                initialObstacleFace.requestLayout();
                initialObstacleFace.setVisibility(View.INVISIBLE);
                obstacleCoordinatesTextView.setVisibility(View.INVISIBLE);
                robot.getLayoutParams().height = (int) map.getCellSize() * 3;
                robot.getLayoutParams().width = (int) map.getCellSize() * 3;
                robot.requestLayout();
                // check where to add this paragraph
                //MAP coordinates - for saving
                mapLeft = map.getLeft();
                mapTop = map.getTop();
                // check where to add this
                // save original coords of obstacles
                originalObstacleCoordinates2 = new int[] {initialObstacleGrp.getLeft(), initialObstacleGrp.getTop()};
                System.out.println(String.format("Original Obstacle Coordinates, X: %d, Y: %d", originalObstacleCoordinates2[0], originalObstacleCoordinates2[1]));
                reset(obstacleInformationTable, parentView);
            }
        });


        //ROBOT settings - KEEP IT INVISIBLE AT FIRST
        robot = (ImageView) findViewById(R.id.robotcar);

        if (map.getCanDrawRobot()) {
            robot.setVisibility(View.VISIBLE);
            rotation = map.convertFacingToRotation(map.getRobotFacing());
            trackRobot();
        } else {
            robot.setVisibility(View.INVISIBLE);
        }


        findViewById(R.id.connect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getContext(), SecondFragment.class);
                BluetoothServices bluetoothServices = new BluetoothServices();
                intent.putExtra("bluetooth_services", bluetoothServices);
                startActivity(intent);*/

                Intent intent = new Intent(MainActivity.this, Connect.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.discoverableBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetooth_discoverable();
            }
        });


        // NEW Short press and Long Press for ALL BUTTONS
        ImageButton forwardButton = (ImageButton) findViewById(R.id.arrowForward);
        ImageButton rightButton = (ImageButton) findViewById(R.id.arrowRight);
        ImageButton leftButton = (ImageButton) findViewById(R.id.arrowLeft);
        ImageButton backButton = (ImageButton) findViewById(R.id.arrowBack);
        ImageButton nEButton = (ImageButton) findViewById(R.id.arrowNE);
        ImageButton sEButton = (ImageButton) findViewById(R.id.arrowSE);
        ImageButton sWButton = (ImageButton) findViewById(R.id.arrowSW);
        ImageButton nWButton = (ImageButton) findViewById(R.id.arrowNW);

        View.OnClickListener movementOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (robot.getVisibility() == View.INVISIBLE) {
                    return;
                }
                //byte[] bytes = "f".getBytes(Charset.defaultCharset());
                String instruction1 = "_";
                switch (view.getId()) {
                    case R.id.arrowForward:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        } else {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        }
                        break;
                    case R.id.arrowRight:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        } else {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        }
                        break;
                    case R.id.arrowLeft:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        } else {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        }
                        break;
                    case R.id.arrowBack:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        } else {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        }
                        break;
                    case R.id.arrowNE:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        } else {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        }
                        break;
                    case R.id.arrowSE:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        } else {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        }
                        break;
                    case R.id.arrowSW:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        } else {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        }
                        break;
                    case R.id.arrowNW:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            masterRobotMovement(Constants.FLEFT);
                            instruction1 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            masterRobotMovement(Constants.LEFT);
                            instruction1 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            masterRobotMovement(Constants.BLEFT);
                            instruction1 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            masterRobotMovement(Constants.DOWN);
                            instruction1 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            masterRobotMovement(Constants.BRIGHT);
                            instruction1 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            masterRobotMovement(Constants.RIGHT);
                            instruction1 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            masterRobotMovement(Constants.UP);
                            instruction1 = "stm-FW020";
                        } else {
                            masterRobotMovement(Constants.FRIGHT);
                            instruction1 = "stm-FR045";
                        }
                        break;
                }

                if (Constants.connected) {
                    System.out.println("first fragment - HI ITS CONNECTED");
                    byte[] bytes = instruction1.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                } else {
                    System.out.println("first fragment - NOT CONNECTED");
                }
            }
        };

        View.OnLongClickListener movementOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (robot.getVisibility() == View.INVISIBLE) {
                    return false;
                }
                handler.removeCallbacks(runnable);
                handler.post(runnable);
                String instruction2 = "_";

                switch (view.getId()) {
                    case R.id.arrowForward:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR45";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        } else {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        }
                        break;
                    case R.id.arrowRight:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR045";
                        } else {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        }
                        break;
                    case R.id.arrowLeft:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        } else {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        }
                        break;
                    case R.id.arrowBack:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        } else {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        }
                        break;
                    case R.id.arrowNE:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        } else {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR045";
                        }
                        break;
                    case R.id.arrowSE:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        } else {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        }
                        break;
                    case R.id.arrowSW:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        } else {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        }
                        break;
                    case R.id.arrowNW:
                        if (map.getRobotFacing() == Constants.NORTH) {
                            longPress = Constants.FLEFT;
                            instruction2 = "stm-FL045";
                        } else if (map.getRobotFacing() == Constants.NORTHEAST) {
                            longPress = Constants.LEFT;
                            instruction2 = "stm-FL090";
                        } else if (map.getRobotFacing() == Constants.EAST) {
                            longPress = Constants.BLEFT;
                            instruction2 = "stm-BL045";
                        } else if (map.getRobotFacing() == Constants.SOUTHEAST) {
                            longPress = Constants.DOWN;
                            instruction2 = "stm-BW020";
                        } else if (map.getRobotFacing() == Constants.SOUTH) {
                            longPress = Constants.BRIGHT;
                            instruction2 = "stm-BR045";
                        } else if (map.getRobotFacing() == Constants.SOUTHWEST) {
                            longPress = Constants.RIGHT;
                            instruction2 = "stm-FR090";
                        } else if (map.getRobotFacing() == Constants.NORTHWEST) {
                            longPress = Constants.UP;
                            instruction2 = "stm-FW020";
                        } else {
                            longPress = Constants.FRIGHT;
                            instruction2 = "stm-FR045";
                        }
                        break;
                }
                if (Constants.connected) {
                    System.out.println("HI ITS CONNECTED");
                    byte[] bytes = instruction2.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                } else {
                    System.out.println("NOT CONNECTED");
                }

                return true;
            }
        };

        View.OnTouchListener movementOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (robot.getVisibility() == View.INVISIBLE) {
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.removeCallbacks(runnable);
                }
                return false;
            }
        };

        forwardButton.setOnClickListener(movementOnClickListener);
        forwardButton.setOnLongClickListener(movementOnLongClickListener);
        forwardButton.setOnTouchListener(movementOnTouchListener);

        rightButton.setOnClickListener(movementOnClickListener);
        rightButton.setOnLongClickListener(movementOnLongClickListener);
        rightButton.setOnTouchListener(movementOnTouchListener);

        leftButton.setOnClickListener(movementOnClickListener);
        leftButton.setOnLongClickListener(movementOnLongClickListener);
        leftButton.setOnTouchListener(movementOnTouchListener);

        backButton.setOnClickListener(movementOnClickListener);
        backButton.setOnLongClickListener(movementOnLongClickListener);
        backButton.setOnTouchListener(movementOnTouchListener);

        nEButton.setOnClickListener(movementOnClickListener);
        nEButton.setOnLongClickListener(movementOnLongClickListener);
        nEButton.setOnTouchListener(movementOnTouchListener);

        sEButton.setOnClickListener(movementOnClickListener);
        sEButton.setOnLongClickListener(movementOnLongClickListener);
        sEButton.setOnTouchListener(movementOnTouchListener);

        sWButton.setOnClickListener(movementOnClickListener);
        sWButton.setOnLongClickListener(movementOnLongClickListener);
        sWButton.setOnTouchListener(movementOnTouchListener);

        nWButton.setOnClickListener(movementOnClickListener);
        nWButton.setOnLongClickListener(movementOnLongClickListener);
        nWButton.setOnTouchListener(movementOnTouchListener);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                switch (longPress) {
                    case Constants.UP:
                        masterRobotMovement(Constants.UP);
                        break;
                    case Constants.RIGHT:
                        masterRobotMovement(Constants.RIGHT);
                        break;
                    case Constants.DOWN:
                        masterRobotMovement(Constants.DOWN);
                        break;
                    case Constants.LEFT:
                        masterRobotMovement(Constants.LEFT);
                        break;
                    case Constants.FRIGHT:
                        masterRobotMovement(Constants.FRIGHT);
                        break;
                    case Constants.BRIGHT:
                        masterRobotMovement(Constants.BRIGHT);
                        break;
                    case Constants.BLEFT:
                        masterRobotMovement(Constants.BLEFT);
                        break;
                    case Constants.FLEFT:
                        masterRobotMovement(Constants.FLEFT);
                        break;
                    default:
                        System.out.println("somehow its still null for button press");
                }
                handler.postDelayed(runnable, 100);
            }
        };

        //OBSTACLES
        /*
        View.OnTouchListener obstacleOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    pastX = view.getX();
                    pastY = view.getY();
                    return true;
                } else {
                    return false;
                }
            }
        };*/
        initialObstacleGrp.setOnTouchListener(obstacleOnTouchListener);


        /**
         * finally works - resets all obstacles to the original coordinates
         */
        Button resetObstacles = (Button) findViewById(R.id.resetObstacles);
        resetObstacles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset(obstacleInformationTable, parentView);
            }
        });

        Button sendMapData = (Button) findViewById(R.id.sendMap);
        sendMapData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[][] obstacleData = map.getObstacleData();
                Log.d("MainActivity:", "Attempting to send map data");
                StringBuilder data = new StringBuilder("" + obstacleData[0][0] + "," + obstacleData[0][1] + "," + obstacleData[0][2] + "," + obstacleData[0][3]);
                for (int i = 1; i < obstacleData.length; i++) {
                    data.append("|").append(obstacleData[i][0]).append(",").append(obstacleData[i][1]).append(",").append(obstacleData[i][2]).append(",").append(obstacleData[i][3]);
                }
                data.append(";");
                if (obstacleData != null && Constants.connected) {
                    System.out.println(data);
                    byte[] bytes = data.toString().getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    outputNotifView.setText("Map Data Sent");
                    Log.d("MainActivity", "Map Data sent: " + data.toString());
                }
                else if (obstacleData != null) {
                    Log.d("MainActivity", "BT not connected");
                    Log.d("MainActivity", "Simulated Map Data sent: " + data.toString());
                }
            }
        });

        //POPUP BUTTONS
        Button startRobot = (Button) findViewById(R.id.start_robot);
        startRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //executeInstruction();
                outputNotif = String.format("BEGINNNN");
                outputNotifView.setText(outputNotif);

                if (Constants.connected) {
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "Start Robot", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    byte[] bytes = outputNotif.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                }
            }
        });

        ImageButton calculatePath = (ImageButton) findViewById(R.id.calculate);
        calculatePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //executeInstruction();
                outputNotif = String.format("path");
                outputNotifView.setText(outputNotif);

                if (Constants.connected) {
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "Calculate path", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    byte[] bytes = outputNotif.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                }
            }
        });


        /**
         * Create the robot button - make it visible and decide the coordinates.
         */
        ImageButton robotButton = (ImageButton) findViewById(R.id.generateRobot);
        robotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (robot_popup.getVisibility() == View.VISIBLE) {
                    robot_popup.setVisibility(View.INVISIBLE);
                } else {
                    // Automate the chosen obstacle number first!!

                    robot_popup.setVisibility(View.VISIBLE);
                }
            }
        });

        Spinner spinnerRobotX = findViewById(R.id.spinner_robot_x);
        Spinner spinnerRobotY = findViewById(R.id.spinner_robot_y);
        ArrayAdapter<CharSequence> adapterRobot = ArrayAdapter.createFromResource(
                MainActivity.this,
                R.array.spinner_robot_coord,
                android.R.layout.simple_spinner_item
        );
        adapterRobot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRobotX.setAdapter(adapterRobot);
        spinnerRobotY.setAdapter(adapterRobot);

        Spinner spinnerRobotFacing = findViewById(R.id.spinner_robot_facing);
        ArrayAdapter<CharSequence> adapterRobotFacing = ArrayAdapter.createFromResource(
                MainActivity.this,
                R.array.spinner_robot_facing,
                android.R.layout.simple_spinner_item
        );
        adapterRobotFacing.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRobotFacing.setAdapter(adapterRobotFacing);


        spinnerRobotX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                robotColPopup = parseInt(parent.getItemAtPosition(position).toString());
                System.out.printf("COL: %d\n", robotColPopup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        spinnerRobotY.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                robotRowPopup = parseInt(parent.getItemAtPosition(position).toString());
                System.out.printf("ROW: %d\n", robotRowPopup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        spinnerRobotFacing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                robotFacingPopup = parent.getItemAtPosition(position).toString();
                System.out.printf("FACING: %s\n", robotFacingPopup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Button confirmRobot = (Button) findViewById(R.id.finalise_robot);
        confirmRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (robot.getVisibility() == View.VISIBLE) {
                    map.setOldRobotCoord(map.getCurCoord()[0], map.getCurCoord()[1]);
                }
                map.saveFacingWithRotation(0);
                map.setCanDrawRobot(true);
                map.setCurCoord(robotColPopup, robotRowPopup);
                robot.setVisibility(View.VISIBLE);
                rotation = map.convertFacingToRotation(robotFacingPopup);
                trackRobot();
                map.invalidate();

                robot_popup.setVisibility(View.INVISIBLE);
            }
        });
        Button removeRobot = (Button) findViewById(R.id.remove_robot);
        removeRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setCanDrawRobot(false);
                robot.setVisibility(View.INVISIBLE);
                map.setOldRobotCoord(map.getCurCoord()[0], map.getCurCoord()[1]);
                robot_popup.setVisibility(View.INVISIBLE);
                map.invalidate();
            }
        });


        /**
         * POPUP disappears when the view clicked is not the popup_window!
         */
        View rootView = findViewById(R.id.first_fragment);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() != R.id.popup_window) {
                    popup.setVisibility(View.GONE);
                    robot_popup.setVisibility(View.GONE);
                }
            }
        });

        Button northFace = (Button) findViewById(R.id.face_north);
        Button eastFace = (Button) findViewById(R.id.face_east);
        Button southFace = (Button) findViewById(R.id.face_south);
        Button westFace = (Button) findViewById(R.id.face_west);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        EditText editText = findViewById(R.id.editText);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = editText.getText().toString();
                try {
                    int newObstacleNumber = Integer.parseInt(userInput);
                    if (!map.obstacleInMap(newObstacleNumber)) {
                        Log.d("Main", userInput);
                        int oldObstacleNumber = Integer.parseInt(editText.getHint().toString());
                        map.changeObstacleNumber(oldObstacleNumber, newObstacleNumber);
                        map.generateObstacleInformationTableRows(obstacleInformationTable, obstacleViews, parentView, outputNotifView, latestObstacleCoordinates);
                        ConstraintLayout oldObstacleView = obstacleViews.get(oldObstacleNumber);
                        oldObstacleView.setTag(Integer.toString(newObstacleNumber));
                        for (int i = 0; i < oldObstacleView.getChildCount(); i++) {
                            View childView = oldObstacleView.getChildAt(i);
                            if (childView.getTag() != null && childView.getTag().equals(String.format("Obstacle %d text view", oldObstacleNumber))) {
                                TextView textView = (TextView) childView; // Cast childView to TextView
                                textView.setText(Integer.toString(newObstacleNumber));
                                textView.setTag(String.format("Obstacle %d text view", newObstacleNumber));
                                break;
                            }
                        }
                        obstacleViews.remove(oldObstacleNumber);
                        obstacleViews.put(newObstacleNumber, oldObstacleView);
                        int[] coordinates = map.getObstacleCoordinates(newObstacleNumber);
                        String notification = String.format("Obstacle: %d, Col: %d, Row: %d", newObstacleNumber, coordinates[0], coordinates[1]);
                        outputNotifView.setText(notification);
                        String successfulObstacleChangeMessage = String.format("Successfully changed obstacle number from %d to %d!", oldObstacleNumber, newObstacleNumber);
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), successfulObstacleChangeMessage, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "This obstacle number is already in the map! Please key in a different obstacle number.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                } catch (NumberFormatException e) {
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "Obstacle number invalid! Obstacle numbers must be positive integers.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        /**
         * Relevant for all obstacles!
         * If u press the option again, the face will be invisible!
         * If its a different orientation, then the view will be rotated.
         */
        View.OnClickListener onClickFaceListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obstacleFaceCur = obstacleFaceViews2.get(clickedObstacleNumber);
                ConstraintLayout obstacleGroup = obstacleViews.get(clickedObstacleNumber);
                Object tag = obstacleGroup.getTag();
                int obstacleNumber = -1;
                if (tag instanceof String) {
                    String tagString = (String) tag;
                    obstacleNumber = parseInt(tagString);
                } else if (tag instanceof Integer) {
                    int tagInteger = (int) tag;
                    obstacleNumber = tagInteger;
                }
                String facing = "error";

                switch (view.getId()) {
                    case R.id.face_north:
                        if (obstacleFaceCur.getRotation() == 0 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.NONE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(0);
                            facing = "N";
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.NORTH);
                        }
                        break;
                    case R.id.face_east:
                        if (obstacleFaceCur.getRotation() == 90 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.NONE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(90);
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.EAST);
                            facing = "E";
                        }
                        break;
                    case R.id.face_south:
                        if (obstacleFaceCur.getRotation() == 180 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.NONE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(180);
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.SOUTH);
                            facing = "S";
                        }
                        break;
                    case R.id.face_west:
                        if (obstacleFaceCur.getRotation() == 270 && obstacleFaceCur.getVisibility() == View.VISIBLE) {
                            obstacleFaceCur.setVisibility(View.INVISIBLE);
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.NONE);
                        } else {
                            obstacleFaceCur.setVisibility(View.VISIBLE);
                            obstacleFaceCur.setRotation(270);
                            map.updateTargetLocation(obstacleNumber, ObstacleDetails.ObstacleFace.WEST);
                            facing = "W";
                        }
                        break;
                }
                map.generateObstacleInformationTableRows(obstacleInformationTable, obstacleViews, parentView, outputNotifView, latestObstacleCoordinates);
                System.out.println(String.format("obstacleNum %d", obstacleNumber));
                int[] currentColRow = map.getColRowFromXY(obstacleGroup.getX(), obstacleGroup.getY(), map.getLeft(), map.getTop());

                //FOR CHECKLIST
                int[] obstacleCoordinates = map.getObstacleCoordinates(obstacleNumber);
                outputNotif = String.format("Obstacle: %d, Col: %d, Row: %d, Facing: %s", obstacleNumber, obstacleCoordinates[0], obstacleCoordinates[1], facing);
                if (!facing.equals("error")) {
                    outputNotifView.setText(outputNotif);

                    //SEND VALUE
                    /*if (Constants.connected) {
                        byte[] bytes = outputNotif.getBytes(Charset.defaultCharset());
                        BluetoothChat.writeMsg(bytes);
                    }*/
                }

            }
        };

        northFace.setOnClickListener(onClickFaceListener);
        eastFace.setOnClickListener(onClickFaceListener);
        southFace.setOnClickListener(onClickFaceListener);
        westFace.setOnClickListener(onClickFaceListener);
        // Save button
        // Come back to this
        // map.removeObstacleUsingCoord(pastX - map.getX() + map.getCellSize() / 2, pastY - map.getY() + map.getCellSize() / 2);
        // map.updateObstacleCoordinatesInArena(obstacleNumber, x, y);
        // outputNotif = String.format("Obstacle: %d, Col: %d, Row: %d", obstacleNumber, col, row);
        // outputNotifView.setText(outputNotif);
        // convert the x and y back to raw, multiply by cell size + getX and getY
        // curObstacleGrp.setX(newObstacleCoord[0]); //+ map.getX()); // SHOULD BE INBUILT!!
        // curObstacleGrp.setY(newObstacleCoord[1]);

        /** WHOLE Dropping segment of the obstacles on the map - Do clean up q hard to understand! lots of considerations.
         *
         */
        map.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                int action = dragEvent.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        ConstraintLayout currentObstacleGrp = (ConstraintLayout) dragEvent.getLocalState();
                        currentObstacleGrp.setVisibility(View.GONE);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        map.unhighlightCell(latestHighlightedCellCoordinates[0], latestHighlightedCellCoordinates[1]);
                        map.invalidate();
                        int xCoordinate = (int) dragEvent.getX();
                        int yCoordinate = (int) dragEvent.getY();
                        System.out.println(String.format("dragged into map: x: %d, y: %d", xCoordinate, yCoordinate));
                        int[] obstacleCoordinates = map.calculateObstacleCoordinatesOnMap(xCoordinate, yCoordinate);
                        String coordinatesNotification = String.format("Col: %d | Row: %d", obstacleCoordinates[0]-1, 19-obstacleCoordinates[1]);
                        obstacleCoordinatesTextView.setX(xCoordinate);
                        obstacleCoordinatesTextView.setY(yCoordinate - 80);
                        obstacleCoordinatesTextView.setText(coordinatesNotification);
                        // Highlight the cell on the chess board where the piece is being dragged over
                        map.highlightCell(obstacleCoordinates[0], obstacleCoordinates[1]);
                        latestHighlightedCellCoordinates = new int[]{obstacleCoordinates[0], obstacleCoordinates[1]};
                        map.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        int x1 = (int) dragEvent.getX();
                        int y1 = (int) dragEvent.getY();
                        obstacleCoordinatesTextView.setX(x1);
                        obstacleCoordinatesTextView.setY(y1 + 52);
                        obstacleCoordinatesTextView.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        obstacleCoordinatesTextView.setVisibility(View.INVISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        map.unhighlightCell(latestHighlightedCellCoordinates[0], latestHighlightedCellCoordinates[1]);
                        map.invalidate();
                        break;
                    case DragEvent.ACTION_DROP:
                        map.unhighlightCell(latestHighlightedCellCoordinates[0], latestHighlightedCellCoordinates[1]);
                        ConstraintLayout curObstacleGrp = (ConstraintLayout) dragEvent.getLocalState();
                        int x = (int) dragEvent.getX();
                        int y = (int) dragEvent.getY();
                        System.out.println("Obstacle has been released");
                        // Get obstacle number
                        Object tag = curObstacleGrp.getTag();
                        int obstacleNumber = -1;
                        if (tag instanceof String) {
                            String tagString = (String) tag;
                            obstacleNumber = parseInt(tagString);
                        } else if (tag instanceof Integer) {
                            int tagInteger = (int) tag;
                            obstacleNumber = tagInteger;
                        }

                        // this is the exact location - but we want to snap to grid //myImage.setX(x + map.getX() - map.getCellSize()/2); //myImage.setY(y+ map.getY() - map.getCellSize()/2);
                        // if the past location of obstacle was in the map, u remove the old one.
                        if (pastX >= map.getX() && pastX <= map.getX() + map.getWidth() && pastY >= map.getY() && pastY <= map.getY() + map.getHeight()) {
                            System.out.println("obstacle was previously in map");
                            map.removeObstacleUsingCoord(pastX - map.getX() + map.getCellSize() / 2, pastY - map.getY() + map.getCellSize() / 2);
                            // this function!!!
                            map.updateObstacleCoordinatesInArena(obstacleNumber, x, y);
                        } else {
                            // Move obstacle from outside arena to inside arena
                            System.out.println("Obstacle was moved from outside to inside map");
                            int newObstacleNumber = obstacleViews.size() + 1;
                            while (obstacleViews.get(newObstacleNumber) != null) {
                                newObstacleNumber++;
                            }
                            Log.d("Main", Integer.toString(newObstacleNumber));
                            createNewObstacle(newObstacleNumber);
                            // Updates obstacleInformation and obstacleDetails
                            map.insertNewObstacleIntoArena(obstacleNumber, x, y);
                        }

                        int[] newObstCoordColRow = map.calculateCoordinates(x, y);

                        System.out.println("Notification values:");
                        int col = newObstCoordColRow[2];
                        int row = newObstCoordColRow[3];
                        outputNotif = String.format("Obstacle: %d, Col: %d, Row: %d", obstacleNumber, col, row);
                        outputNotifView.setText(outputNotif);

                        //others
                        // raw column * cell size, raw row * cell size
                        int[] newObstacleCoord = {newObstCoordColRow[0], newObstCoordColRow[1]};
                        System.out.println("Updating new obstacle coord");
                        System.out.println(String.format("newObstacleCoord x: %d y: %d", newObstacleCoord[0], newObstacleCoord[1]));
                        newObstacleCoord[0] = newObstacleCoord[0] + (int) (map.getX());  // NEW 6 feb
                        newObstacleCoord[1] = newObstacleCoord[1] + (int) (map.getY());
                        System.out.println(String.format("newObstacleCoord x: %d y: %d", newObstacleCoord[0], newObstacleCoord[1]));

                        //WHEN U JUST CLICK IT ONLY - releases the popupwindow
                        // Old and new coordinates are the same
                        // There might be errors here due to new obstacle number
                        if (latestObstacleCoordinates.get(obstacleNumber) != null && latestObstacleCoordinates.get(obstacleNumber)[0] == newObstacleCoord[0] && latestObstacleCoordinates.get(obstacleNumber)[1] == newObstacleCoord[1]) {
                            clickedObstacleNumber = obstacleNumber;
                            popup.bringToFront();
                            editText.setText(String.valueOf(obstacleNumber));
                            editText.setHint(String.valueOf(obstacleNumber));
                            popup.setVisibility(View.VISIBLE);
                        } else {
                            // If there was a change in coordinates
                            //SEND to RPI - if not a ®click!! - MESSAGE
                            /*if (Constants.connected) {
                                byte[] bytes = outputNotif.getBytes(Charset.defaultCharset());
                                BluetoothChat.writeMsg(bytes);
                            }*/
                        }
                        //saving the current obstacles
                        latestObstacleCoordinates.put(obstacleNumber, newObstacleCoord);

                        // MUST get from the map class to snap to grid - for the new image
                        // this function!!!
                        // raw column * cell size + (int) (map.getX()), raw row * cell size + (int) (map.getY())
                        curObstacleGrp.setX(newObstacleCoord[0]); //+ map.getX()); // SHOULD BE INBUILT!!
                        curObstacleGrp.setY(newObstacleCoord[1]); // + map.getY());
                        curObstacleGrp.setVisibility(View.VISIBLE);
                        map.generateObstacleInformationTableRows(obstacleInformationTable, obstacleViews, parentView, outputNotifView, latestObstacleCoordinates);
                        map.invalidate();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        parentView.setOnDragListener(new View.OnDragListener() {
            // Outside to outside has errors
            @Override
            public boolean onDrag(View view, DragEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int mapWidth = map.getWidth();
                int mapHeight = map.getHeight();
                int[] mapCoord = new int[2];
                map.getLocationOnScreen(mapCoord);

                if (event.getAction() == DragEvent.ACTION_DROP) {
                    // If obstacle released outside map
                    if (x < mapCoord[0] || x > mapCoord[0] + mapWidth || y < mapCoord[1] || y > mapCoord[1] + mapHeight) {
                        ConstraintLayout curObstacleGrp = (ConstraintLayout) event.getLocalState();
                        Object tag = curObstacleGrp.getTag();
                        int obstacleNumber = -1;
                        if (tag instanceof String) {
                            String tagString = (String) tag;
                            obstacleNumber = parseInt(tagString);
                        } else if (tag instanceof Integer) {
                            int tagInteger = (int) tag;
                            obstacleNumber = tagInteger;
                        }
                        if (map.obstacleInMap(obstacleNumber)) {
                            parentView.removeView(curObstacleGrp);
                            map.removeObstacle(obstacleNumber);
                            map.generateObstacleInformationTableRows(obstacleInformationTable, obstacleViews, parentView, outputNotifView, latestObstacleCoordinates);
                            map.invalidate();
                        } else {
                            curObstacleGrp.setX(originalObstacleCoordinates2[0]);
                            curObstacleGrp.setY(originalObstacleCoordinates2[1]);
                            curObstacleGrp.setVisibility(View.VISIBLE);
                        }

                        // Change currentObstacleCoords to map too?
                        latestObstacleCoordinates.put(obstacleNumber, new int[]{(int) curObstacleGrp.getX(), (int) curObstacleGrp.getY()});
                        printAllObstacleCoords();
                        map.invalidate();
                    }
                }
                return true;
            }
        });


    }

    // ---------------------------- GUI ---------------------------- IDK ABT ONRESUME, ON PAUSE

    /**
     * Summarize the move buttons actions.
     * @param direction
     */
    public void masterRobotMovement(String direction) {
        // setting variables
        map.saveFacingWithRotation(rotation);
        map.setRobotMovement(direction);
        //actual movement
        map.moveRobot();
        map.invalidate();
        rotation = map.convertFacingToRotation(map.getRobotFacing());
        trackRobot();
    }

    public void reset(TableLayout obstacleInformationTable, ViewGroup parentView) {
        robot.setVisibility(View.INVISIBLE);
        for (int obstacleNumber : obstacleViews.keySet()) {
            ConstraintLayout obstacleViewGroup = obstacleViews.get(obstacleNumber);
            parentView.removeView(obstacleViewGroup);
        }
        map.removeAllObstacles();
        map.generateObstacleInformationTableRows(obstacleInformationTable, obstacleViews, parentView, outputNotifView, latestObstacleCoordinates);
        obstacleViews.clear();
        map.setMapAsUnexplored();
        map.invalidate();
        createNewObstacle(1);
    }

    /** RUNS EVERYTIME robot moves!!
     * Purpose is to track the image of the robot to the current coord of the robot in map class. and follows the right rotation
     * The robot will be paired accordingly
     * Does displays as well
     *
     */
    @SuppressLint("DefaultLocale")
    public void trackRobot() {
        //System.out.println("TRACK ROBOT FUNCTION");

        int[] robotImageCoord = map.getCurCoord();
        int[] robotLocation = map.setRobotImagePosition(robotImageCoord[0], map.convertRow(robotImageCoord[1]), map.getLeft(), map.getTop());
        robot.setX(robotLocation[0]);
        robot.setY(robotLocation[1]);
        robot.setRotation(rotation);

        //Setting displays
        locationNotif = String.format("X: %d, Y: %d, Facing: %s\n", robotImageCoord[0], robotImageCoord[1], map.convertRotationToFacing(rotation));
        locationNotifView.setText(locationNotif);
    }


    /**
     * Responding to instructions from external RPI
     */
    public void executeInstruction() {

        if (!Constants.instruction.equals("null")) {
            instruction = Constants.instruction;
        }
        String formattedInstruction = instruction.replaceAll("\\s", "");
        List<String> instructionList = Arrays.asList(formattedInstruction.split(","));

        Log.d("executeInstruction", formattedInstruction);
        System.out.println(instructionList);
        //CLEANING
        String prefix = instructionList.get(0);
        prefix = prefix.toUpperCase();

        //FOR STATUS
        if (prefix.equals("STATUS")) {
            // assuming max 1 comma
            String display = "STATUS: ";
            display = display + instructionList.get(1);
            outputNotifView.setText(display);
        } else if (prefix.equals("TARGET")) {
            int obstacleNumber = Integer.parseInt(instructionList.get(1));
            String targetID = instructionList.get(2);
            if (Objects.equals(targetID, "Noobjectdetected!")) {
                targetID = "-";
            }
            if (obstacleNumber != 0) {
                TextView targetTextView = obstacleTextViews.get(obstacleNumber);
                targetTextView.setText(targetID);
                targetTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                outputNotifView.setText(instruction);
            }
        } else if (prefix.equals("ROBOT")) {
            //SET A MAX AND MIN!!! -- 8 feb
            int col = parseInt(instructionList.get(1));
            int row = parseInt(instructionList.get(2));
            if (col < 1) {
                col = Math.max(col, 1);
            } else {
                col = Math.min(col, map.getCol() - 2);
            }
            if (row < 1) {
                row = Math.max(row, 1);
            } else {
                row = Math.min(row, map.getCol() - 2);
            }
            String face = instructionList.get(3);
            robot.setVisibility(View.VISIBLE);
            map.setOldRobotCoord(map.getCurCoord()[0], map.getCurCoord()[1]); // create tracks
            int[] newCoord = new int[]{col, row};
            map.setCurCoord(newCoord);
            rotation = map.convertFacingToRotation(face);
            map.saveFacingWithRotation(rotation);
            trackRobot();
            map.invalidate();
        } else {
            System.out.println(instruction);
            String errorMsg = "Error: " + instruction;
            outputNotifView.setText(errorMsg);
            System.out.println("DOESNT WORK");
        }
    }

    /**
     * HELPER FUNCTIONS TO CHECK
     */

    public void printAllObstacleCoords() {
        System.out.println("Obstacle Coords");
        for (int i = 0; i < currentObstacleCoords.length; i++) {
            System.out.printf("Obstacle %d |  X: %d, Y: %d\n", i + 1, currentObstacleCoords[i][0], currentObstacleCoords[i][1]);
        }
        System.out.println("Latest obstacle coordinates after releasing outside map");
        latestObstacleCoordinates.forEach((obstacleNumber, obstacleCoordinates) -> {
            System.out.println(String.format("Obstacle Number %d, obstacleCoordinates: %d, %d", obstacleNumber, obstacleCoordinates[0], obstacleCoordinates[1]));
        });
    }

    // Chih Ying changed this
    // Create a new ConstraintLayout
    private ConstraintLayout createNewObstacle(int obstacleNumber) {
        System.out.println("Creating new obstacle...");
        ConstraintLayout newObstacleGroup = new ConstraintLayout(this);
        newObstacleGroup.setId(View.generateViewId());
        obstacleIds.put(obstacleNumber, newObstacleGroup.getId());
        newObstacleGroup.setTag(obstacleNumber);
        int obstacleLength = (int) map.getCellSize();
        System.out.println(String.format("Obstacle Length %d", obstacleLength));
        newObstacleGroup.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT));
        ImageView newObstacleBox = new ImageView(this);
        newObstacleBox.setId(View.generateViewId());
        newObstacleBox.setTag(obstacleNumber);
        newObstacleBox.setImageResource(R.drawable.blue_obstacle);
        ConstraintLayout.LayoutParams boxParams = new ConstraintLayout.LayoutParams(obstacleLength, obstacleLength);
        newObstacleBox.setLayoutParams(boxParams);
        // Create the second ImageView
        ImageView newObstacleFace = new ImageView(this);
        newObstacleFace.setId(View.generateViewId()); // Generate a unique ID for the view
        newObstacleFace.setTag(obstacleNumber);
        newObstacleFace.setImageResource(R.drawable.blue_obstacle_face);
        ConstraintLayout.LayoutParams faceParams = new ConstraintLayout.LayoutParams(obstacleLength, obstacleLength);
        newObstacleFace.setLayoutParams(faceParams);
        newObstacleFace.setVisibility(View.INVISIBLE);
        // Create the TextView
        TextView newObstacleNumber = new TextView(this);
        newObstacleNumber.setId(View.generateViewId()); // Generate a unique ID for the view
        newObstacleNumber.setTag(String.format("Obstacle %d text view", obstacleNumber));
        newObstacleNumber.setText(Integer.toString(obstacleNumber));
        newObstacleNumber.setTextColor(Color.WHITE);
        Typeface mainFont = Typeface.MONOSPACE;
        newObstacleNumber.setTypeface(mainFont);
        newObstacleNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        newObstacleNumber.setGravity(Gravity.CENTER_HORIZONTAL);
        ConstraintLayout.LayoutParams idParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        newObstacleNumber.setLayoutParams(idParams);
        // Add the child views to the ConstraintLayout
        newObstacleGroup.addView(newObstacleBox);
        newObstacleGroup.addView(newObstacleFace);
        newObstacleGroup.addView(newObstacleNumber);
        // Set constraints for child views (if needed)
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(newObstacleGroup);
        // Add constraints to new obstacle Face
        constraintSet.connect(newObstacleFace.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(newObstacleFace.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        // Add constraint to new obstacle box
        constraintSet.connect(newObstacleBox.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(newObstacleBox.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        // Add constraint to new obstacle number
        constraintSet.connect(newObstacleNumber.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(newObstacleNumber.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(newObstacleNumber.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(newObstacleNumber.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.centerVertically(newObstacleNumber.getId(), ConstraintSet.PARENT_ID); // Center vertically
        constraintSet.centerHorizontally(newObstacleNumber.getId(), ConstraintSet.PARENT_ID); // Center horizontally
        constraintSet.applyTo(newObstacleGroup);
        // Add the child ConstraintLayout to its parent ConstraintLayout (assuming your parent ConstraintLayout has an ID)
        ConstraintLayout fullScreen = findViewById(R.id.fullScreen);
        fullScreen.addView(newObstacleGroup);
        constraintSet.clone(fullScreen);
        constraintSet.connect(newObstacleGroup.getId(), ConstraintSet.START, R.id.generator, ConstraintSet.START);
        constraintSet.connect(newObstacleGroup.getId(), ConstraintSet.END, R.id.generator, ConstraintSet.END, 130);
        constraintSet.connect(newObstacleGroup.getId(), ConstraintSet.TOP, R.id.mapView, ConstraintSet.BOTTOM, 10);
        constraintSet.connect(newObstacleGroup.getId(), ConstraintSet.BOTTOM, R.id.generator, ConstraintSet.TOP);
        // Apply the constraints to the parent ConstraintLayout
        constraintSet.applyTo(fullScreen);
        // Insert into lists
        obstacleViews.put(obstacleNumber, newObstacleGroup);
        obstacleFaceViews2.put(obstacleNumber, newObstacleFace);
        obstacleTextViews.put(obstacleNumber, newObstacleNumber);
        obstacleBoxViews.add(newObstacleBox);
        newObstacleGroup.setOnTouchListener(obstacleOnTouchListener);
        return newObstacleGroup;
    }

    public void printOriginalObstacleCoords() {
        System.out.println("OG obstacle Coords");
        for (int i = 0; i < originalObstacleCoords.length; i++) {
            System.out.printf("Obstacle %d |  X: %d, Y: %d\n", i + 1, originalObstacleCoords[i][0], originalObstacleCoords[i][1]);
        }

    }

    public void setInstruction(String receivedInstruction) {
        this.instruction = receivedInstruction;
    }

    public class MyObserver implements Observer {
        private final MySubject subject;

        public MyObserver(MySubject subject) {
            this.subject = subject;
            subject.addObserver(this);
        }


        @Override
        public void update(Observable observable, Object arg) {
            if (observable == subject) {
                onInstructionChanged();
            }
        }

        private void onInstructionChanged() {
            // Do something when the constant variable changes
            executeInstruction();
        }
    }


    // ---------------------------- BLUETOOTH ----------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //@Override
    //public void onClick(View view) {
    //}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Permission Granted");
            } else {
                showToast("Permission Denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    // Bluetooth is on
                    //mBlueIv.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth is on");
                } else {
                    showToast("Failed to connect to bluetooth");
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Toast message function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    //first fragment - turn on bluetooth
    public void turnonbluetooth() {
        if (!mBlueAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activityResultLauncher.launch(intent);
                //return;
            }
            mStatusBlueTv.setText("Bluetooth is on");

            // Intent to On Bluetooth
            //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //  startActivityForResult(intent, REQUEST_ENABLE_BT);

            //activityResultLauncher.launch(intent);
        } else {
            showToast("Bluetooth is already on");
        }
    }

    //first fragment - turn off bluetooth
    public void turnoffbluetooth() {
        if (mBlueAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                mBlueAdapter.disable();
                showToast("Turning Bluetooth Off");
                mStatusBlueTv.setText("Bluetooth is off");
                //mBlueIv.setImageResource(R.drawable.ic_action_off);
            }
        } else {
            showToast("Bluetooth is already off");
        }
    }

    //first fragment - set Bluetooth discoverable
    public void bluetooth_discoverable() {
        if (!mBlueAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1);
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mStatusBlueTv.setText("Making Your Device Discoverable");
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //startActivityForResult(intent ,REQUEST_DISCOVER_BT);
                activityResultLauncher.launch(intent);
            }
        } else {
            mStatusBlueTv.setText("Bluetooth discovery is already on");
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    BroadcastReceiver btConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Receiving btConnectionStatus Msg!!!");

            String connectionStatus = intent.getStringExtra("ConnectionStatus");
            myBTConnectionDevice = intent.getParcelableExtra("Device");
            //myBTConnectionDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //DISCONNECTED FROM BLUETOOTH CHAT
            if (connectionStatus.equals("disconnect")) {

                Log.d("MainActivity:", "Device Disconnected");
                connectedDevice = null;
                connectedState = false;

                if (currentActivity) {

                    //RECONNECT DIALOG MSG
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("BLUETOOTH DISCONNECTED");
                    alertDialog.setMessage("Connection with device: " + myBTConnectionDevice.getName() + " has ended. Do you want to reconnect?");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //START BT CONNECTION SERVICE
                            Intent connectIntent = new Intent(MainActivity.this, BluetoothConnectionService.class);
                            connectIntent.putExtra("serviceType", "connect");
                            connectIntent.putExtra("device", myBTConnectionDevice);
                            connectIntent.putExtra("id", myUUID);
                            startService(connectIntent);
                        }
                    }, 5000);

                    alertDialog.show();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //CLOSE DIALOG
                            alertDialog.cancel();
                        }
                    }, 1000);



                }

                //SUCCESSFULLY CONNECTED TO BLUETOOTH DEVICE
                else if (connectionStatus.equals("connect")) {

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    connectedDevice = myBTConnectionDevice.getName();
                    connectedState = true;
                    Log.d("MainActivity:", "Device Connected " + connectedState);
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "Connection Established: " + myBTConnectionDevice.getName(), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Toast.makeText(MainActivity.this, "Connection Established: " + myBTConnectionDevice.getName(), Toast.LENGTH_LONG).show();
                }

                //BLUETOOTH CONNECTION FAILED
                else if (connectionStatus.equals("connectionFail")) {
                    Toast.makeText(MainActivity.this, "Connection Failed: " + myBTConnectionDevice.getName(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(btConnectionReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //RESUME ACTIVITY
    @Override
    protected void onResume() {
        super.onResume();

        //REGISTER BROADCAST RECEIVER FOR INCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));

        //CHECK FOR EXISTING CONNECTION
        if (connectedState) {
            Log.d(" MainAcitvity:", "OnResume1");

            //SET TEXTFIELD TO DEVICE NAME
        } else {
            Log.d(" MainAcitvity:", "OnResume2");

            //SET TEXTFIELD TO NOT CONNECTED
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
        }
    }


}