package com.example.mdpandroidcontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class MapArena extends View { //implements Serializable

    //private static final long serialVersionUID = 1L;
    private Context context;
    private AttributeSet attrs;
    private boolean mapDrawn = false;
    private static ArrayList<String[]> arrowCoord = new ArrayList<>();
    private static Cell[][] cells;
    private static final int COL = Constants.TWENTY;
    private static final int ROW = Constants.TWENTY;
    private static float cellSize;
    private static boolean canDrawRobot = false;
    private static String robotMovement = Constants.NONE; // the direction its going
    private static String robotFacing = Constants.NONE;

    private static boolean robotReverse = false;  // at first always move forward
    private static int robotSize = 3;
    private static int oldFacing;
    private static int newFacing;
    private static String[] robotFacingEnum = new String[] {Constants.NORTH, Constants.NORTHEAST, Constants.EAST, Constants.SOUTHEAST, Constants.SOUTH, Constants.SOUTHWEST, Constants.WEST, Constants.NORTHWEST};
    private static int[] curCoord = new int[]{1, 1};

    private static ArrayList<int[]> obstacleCoord = new ArrayList<>();
    // Stores obstacle information
    // Integer --> obstacle number
    // obstacleDetails --> stores obstacle info
    private static Map <Integer, ObstacleDetails> obstacleInformation = new HashMap<>();
    private static int[] oldCoord = new int[]{-1, -1};

    private Paint black = new Paint();
    Typeface mainFont = Typeface.MONOSPACE;
    private Paint lineColor = new Paint();
    private Paint unexploredCellColor = new Paint();
    private Paint robotColor = new Paint();


    public MapArena(Context c) {
        super(c);
        black.setStyle(Paint.Style.FILL_AND_STROKE);
        unexploredCellColor.setColor(Color.BLACK); // light teal: 0xFFD4F6F2
        robotColor.setColor(Color.RED); //GREEN
        lineColor.setColor(0xFFBDBDBD); // white / 0xFF757575 / 0xFFBDBDBD LIGHTER
    }

    public MapArena(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;

        black.setStyle(Paint.Style.FILL_AND_STROKE);
        unexploredCellColor.setColor(0xFFe2f2fc);
        robotColor.setColor(Color.RED);
        lineColor.setColor(0xFFBDBDBD);
    }


    /**
     * where u start everything
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // on first time drawing?
        if (!mapDrawn) {
            this.createCell();
            setRobotFacing(Constants.NORTH);
            mapDrawn = true;

        }

        drawGridAxes(canvas);
        drawCell(canvas);
        lineColor.setColor(0xFFE20D0F);
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
    }

    private void createCell() {
        cells = new Cell[COL + 1][ROW + 1];
        this.calculateDimension();
        cellSize = this.getCellSize();

        for (int x = 0; x <= COL; x++) {
            for (int y = 0; y <= ROW; y++) {
                cells[x][y] = new Cell(x * cellSize + (cellSize / 30), y * cellSize + (cellSize / 30), (x + 1) * cellSize, (y + 1) * cellSize, unexploredCellColor, "unexplored");
            }
        }
    }

    // Called whenever obstacles are placed, removed or moved
    public void generateObstacleInformationTableRows(TableLayout obstacleInformationTable, Map <Integer, ConstraintLayout> obstacleViews, ViewGroup mapParentView, TextView outputNotifView, Map<Integer, int[]> latestObstacleCoordinates) {
        TableRow header = (TableRow) obstacleInformationTable.getChildAt(0);
        System.out.println(header);
        obstacleInformationTable.removeAllViews();
        obstacleInformationTable.addView(header);
        obstacleInformation.forEach((obstacleNumber, obstacleDetails) -> {
            System.out.println(String.format("Obstacle Number %d", obstacleNumber));
            TableRow tableRow = new TableRow(this.getContext());
            TextView obstacleNumberText = new TextView(this.getContext());
            obstacleNumberText.setText(String.valueOf(obstacleNumber));
            obstacleNumberText.setTextColor(Color.WHITE);
            obstacleNumberText.setTypeface(mainFont);
            int textSize = 15;
            obstacleNumberText.setTextSize(textSize);
            obstacleNumberText.setPadding(20, 5, 0, 10);
            int[] obstacleCoordinates = obstacleDetails.getCoordinates();
            int xCoordinate = obstacleCoordinates[0];
            int yCoordinate = obstacleCoordinates[1];
            EditText xCoordinateText = new EditText(this.getContext());
            xCoordinateText.setTextColor(Color.WHITE);
            xCoordinateText.setText(String.valueOf(xCoordinate));
            xCoordinateText.setTypeface(mainFont);
            xCoordinateText.setTextSize(textSize);
            xCoordinateText.setPadding(18, 0, 0, 10);
            EditText yCoordinateText = new EditText(this.getContext());
            yCoordinateText.setText(String.valueOf(yCoordinate));
            yCoordinateText.setTextColor(Color.WHITE);
            yCoordinateText.setTextSize(textSize);
            yCoordinateText.setTypeface(mainFont);
            yCoordinateText.setPadding(35, 5, 0, 10);
            TextView faceText = new TextView(this.getContext());
            faceText.setText(getTargetFaceDisplayString(obstacleDetails.getObstacleFace()));
            faceText.setTextColor(Color.WHITE);
            faceText.setTextSize(textSize);
            faceText.setTypeface(mainFont);
            faceText.setPadding(30, 5, 0, 10);
            // Delete obstacle button
            int desiredWidthInPixels = 16; // Replace with your desired width
            int desiredHeightInPixels = 16; // Replace with your desired height
            ImageButton deleteObstacleButton = new ImageButton(this.getContext());
            deleteObstacleButton.setBackgroundColor(Color.BLACK);
            Drawable originalDrawable = getResources().getDrawable(R.drawable.delete_button);
            Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidthInPixels, desiredHeightInPixels, true);
            Drawable scaledDrawable = new BitmapDrawable(getResources(), scaledBitmap);
            deleteObstacleButton.setImageDrawable(scaledDrawable);
            deleteObstacleButton.setPadding(30, 5, 0, 10);
            // Save obstacle co-ordinates
            ImageButton saveCoordinatesButton = new ImageButton(this.getContext());
            saveCoordinatesButton.setBackgroundColor(Color.BLACK);
            Drawable saveIcon = getResources().getDrawable(R.drawable.save_icon);
            Bitmap saveIconBitmap = ((BitmapDrawable) saveIcon).getBitmap();
            Bitmap scaledSaveIconBitmap = Bitmap.createScaledBitmap(saveIconBitmap, desiredWidthInPixels, desiredHeightInPixels, true);
            Drawable scaledSaveIconDrawable = new BitmapDrawable(getResources(), scaledSaveIconBitmap);
            saveCoordinatesButton.setImageDrawable(scaledSaveIconDrawable);
            saveCoordinatesButton.setPadding(35, 5, 0, 10);
            deleteObstacleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapParentView.removeView(obstacleViews.get(obstacleNumber));
                    removeObstacle(obstacleNumber);
                    obstacleInformationTable.removeView(tableRow);
                    invalidate();
                }
            });
            saveCoordinatesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mapXCoordinate = xCoordinateText.getText().toString();
                    String mapYCoordinate = yCoordinateText.getText().toString();
                    int newXCoordinate = (int) (((Integer.parseInt(mapXCoordinate) + 1) * cellSize) + getX());
                    int newYCoordinate = (int) (((19 - Integer.parseInt(mapYCoordinate)) * cellSize) + getY());
                    updateObstacleCoordinatesInArena(obstacleNumber, newXCoordinate, newYCoordinate);
                    String outputNotification = String.format("Obstacle: %d, Col: %s, Row: %s", obstacleNumber, mapXCoordinate, mapYCoordinate);
                    outputNotifView.setText(outputNotification);
                    ViewGroup obstacle = obstacleViews.get(obstacleNumber);
                    obstacle.setX(newXCoordinate);
                    obstacle.setY(newYCoordinate);
                    latestObstacleCoordinates.put(obstacleNumber, new int[] {newXCoordinate, newYCoordinate});
                    if (Constants.connected) {
                        byte[] bytes = outputNotification.getBytes(Charset.defaultCharset());
                        BluetoothChat.writeMsg(bytes);
                    }
                    invalidate();
                }
            });
            tableRow.addView(obstacleNumberText);
            tableRow.addView(xCoordinateText);
            tableRow.addView(yCoordinateText);
            tableRow.addView(faceText);
            tableRow.addView(deleteObstacleButton);
            tableRow.addView(saveCoordinatesButton);
            obstacleInformationTable.addView(tableRow);
        });
    }

    private String getTargetFaceDisplayString(ObstacleDetails.ObstacleFace location) {
        String displayString = new String();
        switch (location) {
            case EAST:
                displayString = "E";
                break;
            case WEST:
                displayString = "W";
                break;
            case NORTH:
                displayString = "N";
                break;
            case SOUTH:
                displayString = "S";
                break;
            case NONE:
                displayString = "None";
                break;
        }
        return displayString;
    }

    public void highlightCell(int mapXCoordinate, int mapYCoordinate) {
        cells[mapXCoordinate][mapYCoordinate].setHighlighted(true);
    }

    public void unhighlightCell(int mapXCoordinate, int mapYCoordinate) {
        cells[mapXCoordinate][mapYCoordinate].setHighlighted(false);
    }

    /**
     * Drawing of cells
     * @param canvas
     */
    public void drawCell(Canvas canvas) {
        for (int x = 1; x <= COL; x++) {
            for (int y = 0; y < ROW; y++) {
                for (int i = 0; i < robotSize; i++) {
                    canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
                    if (cells[x][y].getHighlighted()) {
                        Paint outlinePaint = new Paint();
                        outlinePaint.setStyle(Paint.Style.STROKE); // Set the style to stroke
                        outlinePaint.setStrokeWidth(2); // Set the outline width (adjust as needed)
                        outlinePaint.setColor(Color.RED); // Set the outline color (adjust as needed)
                        // Draw the outline around the cell
                        canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, outlinePaint);
                    }
                }
            }
        }
    }

    /**
     * Draws vertical lines for each of the cells
     * @param canvas
     */
    private void drawVerticalLines(Canvas canvas) {
        for (int x = 0; x <= COL; x++)
            canvas.drawLine(cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][0].startY - (cellSize / 30), cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][19].endY + (cellSize / 30), lineColor);
    }

    /**
     * Draws horizontal lines for each of the cells
     * @param canvas
     */
    private void drawHorizontalLines(Canvas canvas) {
        for (int y = 0; y <= ROW; y++)
            canvas.drawLine(cells[1][y].startX, cells[1][y].startY - (cellSize / 30), cells[ROW][y].endX, cells[15][y].startY - (cellSize / 30), lineColor); // black lines
    }

    private void drawGridAxes(Canvas canvas) {
        black.setColor(Color.WHITE);
        black.setTextSize(16);
        black.setTypeface(mainFont);
        for (int x = 1; x <= COL; x++) {
            if (x > 9)
                canvas.drawText(Integer.toString(x-1), cells[x][20].startX, cells[x][20].startY + (cellSize / 3) + 5, black);
            else
                canvas.drawText(Integer.toString(x-1), cells[x][20].startX + (cellSize / 3), cells[x][20].startY + (cellSize / 3) + 5, black);
        }
        for (int y = 0; y < ROW; y++) {
            if ((20 - y) > 10)
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX, cells[0][y].startY + (cellSize / 1.5f), black);
            else
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX + (cellSize / 2.5f), cells[0][y].startY + (cellSize / 1.5f), black);
        }
    }

    /**
     * have smt to move the robot
     * HOW TO MAKE THE OLD ONE NOT COUNT
     */
    public void moveRobot() {
        int[] tempCoord = this.getCurCoord();
        this.setOldRobotCoord(tempCoord[0], tempCoord[1]);
        int[] oldCoord = this.getOldRobotCoord();

        int transX = 0, transY = 0;
        int prev0 = tempCoord[0], prev1 = tempCoord[1];

        oldFacing = convertFacingToIndex(getRobotFacing());
        switch( this.getRobotMovement()) {
            case Constants.UP:
                // Includes check for end of the grid
                transX = 0;
                transY = 1;
                break;
            case Constants.DOWN:
                transX = 0;
                transY = -1;
                break;
            case Constants.LEFT:
                transX = -1;
                transY = 2;

                //changing to new facing
                if (robotReverse) {
                    newFacing = (oldFacing + 2) % 8;
                } else {
                    newFacing = (oldFacing + 6) % 8;
                }
                setRobotFacing(robotFacingEnum[newFacing]);


                break;
            case Constants.RIGHT:
                transX = 1;
                transY = 2;

                if (robotReverse) {
                    newFacing = (oldFacing + 6) % 8;
                } else {
                    newFacing = (oldFacing + 2) % 8;
                }
                setRobotFacing(robotFacingEnum[newFacing]);

                break;
            case Constants.FRIGHT:
                // Includes check for end of the grid
                transX = 1;
                transY = 1;

                if (robotReverse) {
                    newFacing = (oldFacing + 7) % 8;
                } else {
                    newFacing = (oldFacing + 1) % 8;
                }
                setRobotFacing(robotFacingEnum[newFacing]);
                break;
            case Constants.FLEFT:
                // Includes check for end of the grid
                transX = -1;
                transY = 1;

                if (robotReverse) {
                    newFacing = (oldFacing + 1) % 8;
                } else {
                    newFacing = (oldFacing + 7) % 8;
                }
                setRobotFacing(robotFacingEnum[newFacing]);
                break;
            case Constants.BRIGHT:
                // Includes check for end of the grid
                transX = 1;
                transY = -1;

                if (robotReverse) {
                    newFacing = (oldFacing + 1) % 8;
                } else {
                    newFacing = (oldFacing + 7) % 8;
                }
                setRobotFacing(robotFacingEnum[newFacing]);
                break;
            case Constants.BLEFT:
                // Includes check for end of the grid
                transX = -1;
                transY = -1;

                if (robotReverse) {
                    newFacing = (oldFacing + 7) % 8;
                } else {
                    newFacing = (oldFacing + 1) % 8;
                }
                setRobotFacing(robotFacingEnum[newFacing]);
                break;
            default:
                System.out.println("Error in moveRobot() direction input");
                break;
        }

        if (robotReverse) {
            transY = transY * -1;
        }

        // to change the direction according to formula!
        switch (oldFacing) {
            case 0: //North
                tempCoord[0] = tempCoord[0] + transX;
                tempCoord[1] = tempCoord[1] + transY;
                break;
            case 1: //NorthEast
                switch (this.getRobotMovement()) {
                    case Constants.UP:
                    case Constants.DOWN:
                        tempCoord[0] += transY;
                        tempCoord[1] += transY;
                        break;
                    case Constants.FRIGHT:
                    case Constants.BLEFT:
                        tempCoord[0] += transX;
                        break;
                    case Constants.RIGHT:
                        tempCoord[0] += transY;
                        tempCoord[1] += transX;
                        break;
                    case Constants.BRIGHT:
                    case Constants.FLEFT:
                        tempCoord[1] += transY;
                        break;
                    case Constants.LEFT:
                        tempCoord[0] -= transX;
                        tempCoord[1] += transY;
                        break;
                }
                break;
            case 2: //East
                tempCoord[0] = tempCoord[0] + transY;
                tempCoord[1] = tempCoord[1] - transX;
                break;
            case 3: //SouthEast
                switch (this.getRobotMovement()) {
                    case Constants.UP:
                    case Constants.DOWN:
                        tempCoord[0] += transY;
                        tempCoord[1] -= transY;
                        break;
                    case Constants.FRIGHT:
                    case Constants.BLEFT:
                        tempCoord[1] -= transY;
                        break;
                    case Constants.RIGHT:
                        tempCoord[0] += transX;
                        tempCoord[1] -= transY;
                        break;
                    case Constants.BRIGHT:
                    case Constants.FLEFT:
                        tempCoord[0] -= transX;
                        break;
                    case Constants.LEFT:
                        tempCoord[0] += transX;
                        tempCoord[1] += transY;
                        break;
                }
                break;
            case 4: //South
                tempCoord[0] = tempCoord[0] - transX;
                tempCoord[1] = tempCoord[1] - transY;
                break;
            case 5: //SouthWest
                switch (this.getRobotMovement()) {
                    case Constants.UP:
                    case Constants.DOWN:
                        tempCoord[0] -= transY;
                        tempCoord[1] -= transY;
                        break;
                    case Constants.FRIGHT:
                    case Constants.BLEFT:
                        tempCoord[0] -= transX;
                        break;
                    case Constants.RIGHT:
                        tempCoord[0] -= transY;
                        tempCoord[1] -= transX;
                        break;
                    case Constants.BRIGHT:
                    case Constants.FLEFT:
                        tempCoord[1] += transY;
                        break;
                    case Constants.LEFT:
                        tempCoord[0] += transX;
                        tempCoord[1] -= transY;
                        break;
                }
                break;
            case 6: //West
                tempCoord[0] = tempCoord[0] - transY;
                tempCoord[1] = tempCoord[1] + transX;
                break;
            case 7: //NorthWest
                switch (this.getRobotMovement()) {
                    case Constants.UP:
                    case Constants.DOWN:
                        tempCoord[0] -= transY;
                        tempCoord[1] += transY;
                        break;
                    case Constants.FRIGHT:
                    case Constants.BLEFT:
                        tempCoord[1] += transY;
                        break;
                    case Constants.RIGHT:
                        tempCoord[0] -= transX;
                        tempCoord[1] += transY;
                        break;
                    case Constants.BRIGHT:
                    case Constants.FLEFT:
                        tempCoord[0] += transX;
                        break;
                    case Constants.LEFT:
                        tempCoord[0] -= transX;
                        tempCoord[1] -= transY;
                        break;
                }
                break;
        }

        // CHECKS OUT OF BOUNDS
        if (Objects.equals(this.getRobotMovement(), Constants.FRIGHT) || Objects.equals(this.getRobotMovement(), Constants.FLEFT) || Objects.equals(this.getRobotMovement(), Constants.BRIGHT) || Objects.equals(this.getRobotMovement(), Constants.BLEFT)){
            if (tempCoord[0] < 1) {
                tempCoord[0] = 1;
                tempCoord[1] = prev1;
            } else if (tempCoord[0] > COL-2) {
                tempCoord[0] = Math.min(tempCoord[0],COL-2);
                tempCoord[1] = prev1;
            } else{
                tempCoord[0] = Math.min(tempCoord[0],COL-2);
            }
            if (tempCoord[1] < 1) {
                tempCoord[1] = 1;
                tempCoord[0] = prev0;
            } else if (tempCoord[1] > ROW-2){
                tempCoord[1] = Math.min(tempCoord[1],ROW-2);
                tempCoord[0] = prev0;
            } else{
                tempCoord[1] = Math.min(tempCoord[1],ROW-2);
            }
        }
        else{
            if (tempCoord[0] < 1) {
                tempCoord[0] = Math.max(tempCoord[0],1);
            } else {
                tempCoord[0] = Math.min(tempCoord[0],COL-2);
            }
            if (tempCoord[1] < 1) {
                tempCoord[1] = Math.max(tempCoord[1],1);
            } else {
                tempCoord[1] = Math.min(tempCoord[1],ROW-2);
            }
        }

        // set oldcoord wont happen as of now - useless btw
        setCurCoord(tempCoord);

    }

    /**
     *
     * @param column
     * @param row
     */
    public int[] setRobotImagePosition(int column, int row, float left, float top) {

        int[] newRobotLocation= {(int) ((column) * cellSize + left), (int) ((row - 2) * cellSize + top)};

        return newRobotLocation;
    }

    public void colourCell(int[] mapCoordinates) {
        cells[mapCoordinates[0]][mapCoordinates[1]].setType("explored");
    }


    public int[] calculateObstacleCoordinatesOnMap(int x, int y) {
        int column = (int) Math.floor(x / cellSize);
        int row = (int) Math.floor(y / cellSize);

        if (column < 1) {
            column = Math.max(column,0);
        } else {
            column = Math.min(column,COL);
        }
        if (row< 1) {
            row = Math.max(row,0);
        } else {
            row = Math.min(row,ROW-1);
        }
        return new int[]{column, row};
    }

    public void insertNewObstacleIntoArena(int obstacleNumber, int x, int y) {
        int column = (int) Math.floor(x / cellSize);
        int row = (int) Math.floor(y / cellSize);

        if (column < 1) {
            column = Math.max(column,0);
        } else {
            column = Math.min(column,COL);
        }
        if (row< 1) {
            row = Math.max(row,0);
        } else {
            row = Math.min(row,ROW-1);
        }
        System.out.println(String.format("Added obstacle at X: %d Y: %d", column, row));
        setObstacleCoord(new int[] {column, row});
        ObstacleDetails newObstacleDetails = new ObstacleDetails();
        newObstacleDetails.setCoordinates(new int[] {column-1, convertRow(row)-1});
        System.out.println(String.format("insert new obstacle coordinates: %d, %d", column-1, convertRow(row)-1));
        obstacleInformation.put(obstacleNumber, newObstacleDetails);
    }

    public int[] getObstacleCoordinates(int obstacleNumber) {
        ObstacleDetails obstacle = obstacleInformation.get(obstacleNumber);
        return obstacle.getCoordinates();
    }

    // Takes drag x and drag y and
    public int[] calculateCoordinates(int x, int y) {
        int column = (int) Math.floor(x / cellSize);
        int row = (int) Math.floor(y / cellSize);

        if (column < 1) {
            column = Math.max(column,0);
        } else {
            column = Math.min(column,COL);
        }
        if (row< 1) {
            row = Math.max(row,0);
        } else {
            row = Math.min(row,ROW-1);
        }
        int[] newObstacleDrag= {(int) (column * cellSize), (int) (row * cellSize), column-1, convertRow(row)-1};
        return newObstacleDrag;
    }

    public void updateObstacleCoordinatesInArena(int obstacleNumber, int x, int y) {
        int column = (int) Math.floor(x / cellSize);
        int row = (int) Math.floor(y / cellSize);

        if (column < 1) {
            column = Math.max(column,0);
        } else {
            column = Math.min(column,COL);
        }
        if (row< 1) {
            row = Math.max(row,0);
        } else {
            row = Math.min(row,ROW-1);
        }
        ObstacleDetails obstacle = obstacleInformation.get(obstacleNumber);
        obstacle.setCoordinates(new int[] {column-1, convertRow(row)-1});
    }

    /**
     * this one is using COORDINATES
     * @param originalX
     * @param originalY
     */
    public void removeObstacleUsingCoord(float originalX, float originalY) {
        int column = (int) Math.floor(originalX / cellSize);
        int row = (int) Math.floor(originalY / cellSize);
        removeObstacleCoord(new int[] {column, row});
    }

    public int[] getColRowFromXY(float x, float y, float mapLeft, float mapTop) {

        int column = (int) Math.floor((x - mapLeft + cellSize/2) / cellSize);
        int row = (int) Math.floor((y - mapTop + cellSize/2) / cellSize);

        int[] result = new int[] {column-1, convertRow(row)-1};
        return result;
    }





    /** its to make the old tracks
     * Saves the old robot coords and also resets the cell to the old one
     * (a little inefficient as most of the robot cells will still be robot)
     */
    public void setOldRobotCoord(int oldCol, int oldRow) {
        this.oldCoord[0] = oldCol;
        this.oldCoord[1] = oldRow;
        oldRow = this.convertRow(oldRow);
        for (int x = oldCol; x <= oldCol + 2; x++)
            for (int y = oldRow - 2; y <= oldRow; y++)
                cells[x][y].setType("explored");
    }
    

    /**
     * Called when create cell called --> to set the size of the cells --> so that it will fit the size?
     * COL+1 to make sure that the cell is full
     */
    private void calculateDimension() {
        this.setCellSize(getWidth()/(COL+1));
    }

    /**
     * cos row 5 is array[][15]
     * @param row
     * @return
     */
    public int convertRow(int row) {
        return (20 - row);
    }

    public int convertFacingToIndex(String facing) {
        for (int i = 0; i < robotFacingEnum.length; i++) {
            if (robotFacingEnum[i] == facing) {
                return i;
            }
        }
        System.out.println("ERROR in facing index");
        return -1;
    }

    public int convertFacingToRotation(String facing) {
        switch (facing) {
            case Constants.NORTH:
                return 0;
            case Constants.NORTHEAST:
                return 45;
            case Constants.EAST:
                return 90;
            case Constants.SOUTHEAST:
                return 135;
            case Constants.SOUTH:
                return 180;
            case Constants.SOUTHWEST:
                return 225;
            case Constants.WEST:
                return 270;
            case Constants.NORTHWEST:
                return 315;
            default:
                return 0;    // assume
        }
    }

    public String convertRotationToFacing(int rotation) {
        switch (rotation) {
            case 0:
                return Constants.NORTH;
            case 45:
                return Constants.NORTHEAST;
            case 90:
                return Constants.EAST;
            case 135:
                return Constants.SOUTHEAST;
            case 180:
                return Constants.SOUTH;
            case 225:
                return Constants.SOUTHWEST;
            case 270:
                return Constants.WEST;
            case 315:
                return Constants.NORTHWEST;
            default:
                return Constants.ERROR;    // assume
        }
    }

    private void setCellSize(float cellSize) {
        MapArena.cellSize = cellSize;
    }

    public void setObstacleCoord(int[] coordinates) {
        obstacleCoord.add(coordinates);
    }

    public void updateTargetLocation(int obstacleNumber, ObstacleDetails.ObstacleFace location) {
        ObstacleDetails obstacle = obstacleInformation.get(obstacleNumber);
        obstacle.setObstacleFace(location);
    }

    /**
     * does the actual removing
     * @param coordinates
     */
    public void removeObstacleCoord(int[] coordinates) {
        //printObstacleCoord();
        // Set as unexplored
        cells[coordinates[0]][coordinates[1]].setType("unexplored");
        // Remove coordinate
        for (int i = 0; i < obstacleCoord.size(); i++) {
            if (Arrays.equals(obstacleCoord.get(i), coordinates)) {
                obstacleCoord.remove(i);
                break;
            }
        }
        //printObstacleCoord();
    }

    public void changeObstacleNumber(int oldObstacleNumber, int newObstacleNumber) {
        ObstacleDetails oldObstacleDetails = obstacleInformation.get(oldObstacleNumber);
        obstacleInformation.remove(oldObstacleNumber);
        obstacleInformation.put(newObstacleNumber, oldObstacleDetails);
    }

    public boolean obstacleInMap(int obstacleNumber) {
        return obstacleInformation.containsKey(obstacleNumber);
    }
    public void removeObstacle(int obstacleNumber) {
        System.out.println("removeObstacle was called");
        ObstacleDetails obstacleDetails = obstacleInformation.get(obstacleNumber);
        System.out.println("Obstacle Number:");
        System.out.println(obstacleNumber);
        int[] obstacleCoordinates = obstacleDetails.getCoordinates();
        System.out.println(String.format("X: %d, Y: %d", obstacleCoordinates[0], obstacleCoordinates[1]));
        cells[obstacleCoordinates[0]][obstacleCoordinates[1]].setType("unexplored");
        obstacleInformation.remove(obstacleNumber);
    }

    public void removeAllObstacles() {
        obstacleInformation.clear();
    }

    public void setMapAsUnexplored() {
        for (int i = 1; i <= 20; i++) {
            for (int j = 0; j < 20; j++) {
                cells[i][j].setType("unexplored");
            }
        }
    }

    public ArrayList<int[]> getObstacleCoord() {
        return obstacleCoord;
    }

    public float getCellSize() { return cellSize; }
    public String getRobotMovement() {
        return robotMovement;
    }
    public String getRobotFacing() {
        return robotFacing;
    }


    public void setCanDrawRobot(boolean isDrawRobot) {
        canDrawRobot = isDrawRobot;
    }
    public boolean getCanDrawRobot() {
        return canDrawRobot;
    }
    private ArrayList<String[]> getArrowCoord() {
        return arrowCoord;
    }

    public void setRobotMovement(String direction) {
        robotMovement = direction;}

    public void setRobotReverse(Boolean reverse) {
        robotReverse = reverse;
    }

    public void setRobotFacing(String facing) {
        robotFacing = facing;}

    public void saveFacingWithRotation(int rotation) {
        robotFacing = robotFacingEnum[(int) (rotation / 45)];
    }

    /**
     * col then row
     * @param coordinates
     */
    public void setCurCoord(int[] coordinates) {curCoord = coordinates;}
    public void setCurCoord(int col, int row) {
        curCoord[0] = col;
        curCoord[1] = row;
    }

    public int[] getCurCoord() {
        return curCoord;
    }

    public int[] getOldRobotCoord() {
        return oldCoord;
    }

    public int getCol() {
        return COL;
    }

    public int[][] getObstacleData() {
        int[][] obstacleData;
        Integer obstacleNum;
        ObstacleDetails details;
        int index = 0;
        if (!obstacleInformation.isEmpty()) {
            obstacleData = new int[obstacleInformation.size()][4];
            for (Map.Entry<Integer, ObstacleDetails> set :
                    obstacleInformation.entrySet()) {
                obstacleNum = set.getKey();
                details = set.getValue();
                obstacleData[index][0] = obstacleNum;
                obstacleData[index][1] = details.getCoordinates()[0];
                obstacleData[index][2] = details.getCoordinates()[1];
                switch(details.getObstacleFace()){
                    case NONE:
                        obstacleData[index][3] = 0;
                        break;
                    case NORTH:
                        obstacleData[index][3] = 1;
                        break;
                    case SOUTH:
                        obstacleData[index][3] = 2;
                        break;
                    case EAST:
                        obstacleData[index][3] = 3;
                        break;
                    case WEST:
                        obstacleData[index][3] = 4;
                        break;
                }
                index++;
            }
            return obstacleData;
        }
        return null;
    }


    //WAS USED FOR SERIALIZABLE
    //private void writeObject(ObjectOutputStream out) throws IOException {
    //    out.defaultWriteObject();
    //    out.writeObject(context);
    //    out.writeObject(attrs);
    //}

     //private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
     //    in.defaultReadObject();
     //   context = (Context) in.readObject();
     //   attrs = (AttributeSet) in.readObject();
    //}


}

