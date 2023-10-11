import bagel.*;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Sample solution for SWEN20003 Project 1, Semester 2, 2023
 *
 * @author Stella Li
 */
public class ShadowDance extends AbstractGame  {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DANCE";
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    private final static String CSV_FILE_1 = "res/level1.csv";
    private final static String CSV_FILE_2 = "res/level2.csv";
    private final static String CSV_FILE_3 = "res/level3.csv";
    private String CSV_FILE;
    public final static String FONT_FILE = "res/FSO8BITR.TTF";
    private final static int TITLE_X = 220;
    private final static int TITLE_Y = 250;
    private final static int INS_X_OFFSET = 100;
    private final static int INS_Y_OFFSET = 190;
    private final static int SCORE_LOCATION = 35;
    private final Font TITLE_FONT = new Font(FONT_FILE, 64);
    private final Font INSTRUCTION_FONT = new Font(FONT_FILE, 24);
    private final Font SCORE_FONT = new Font(FONT_FILE, 30);
    private static final String INSTRUCTIONS = "SELECT LEVELS WITH\nNUMBER KEYS\n\n     1       2       3";
    private static final int CLEAR_SCORE = 150;
    private static final String CLEAR_MESSAGE = "CLEAR!";
    private static final String TRY_AGAIN_MESSAGE = "TRY AGAIN";
    private final Accuracy accuracy = new Accuracy();
    private final Lane[] lanes = new Lane[4];
    private int numLanes = 0;
    private int score = 0;
    private static int currFrame = 0;
    private Track track = new Track("res/track1.wav");
    private boolean started = false;
    private boolean finished = false;
    private boolean paused = false;

    public ShadowDance(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        CSV_FILE = CSV_FILE_2;
    }


    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDance game = new ShadowDance();
        game.run();
    }


    private void readCsv() {
        System.out.println("Reading CSV: " + CSV_FILE);
        numLanes = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String textRead;
            while ((textRead = br.readLine()) != null) {
                System.out.println("Processing line: " + textRead);  // Debug line
                String[] splitText = textRead.split(",");

                if (splitText[0].equals("Lane") && numLanes < 4) {
                    // reading lanes
                    String laneType = splitText[1];
                    int pos = Integer.parseInt(splitText[2]);
                    Lane lane = new Lane(laneType, pos);
                    lanes[numLanes++] = lane;
                    System.out.println("Added Lane: " + laneType + " at position " + pos);  // Debug line
                    System.out.println("Total lanes now: " + numLanes);
                    System.out.println("lanes.length: " + lanes.length);
                    if (numLanes > lanes.length) {
                        System.out.println("Error: Too many lanes in CSV!");
                        return;
                    }
                } else if (splitText[0].equals("Lane")){
                    System.out.println("Attempting to add another lane: " + textRead);
                } else {
                    // reading notes
                    String dir = splitText[0];
                    Lane lane = null;
                    for (int i = 0; i < numLanes; i++) {
                        if (lanes[i].getType().equals(dir)) {
                            lane = lanes[i];
                        }
                    }

                    if (lane != null) {
                        switch (splitText[1]) {
                            case "Normal":
                                Note note = new Note(dir, Integer.parseInt(splitText[2]));
                                lane.addNote(note);
                                break;
                            case "Hold":
                                HoldNote holdNote = new HoldNote(dir, Integer.parseInt(splitText[2]));
                                lane.addHoldNote(holdNote);
                                break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        if (!started) {
            // starting screen
            TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
            INSTRUCTION_FONT.drawString(INSTRUCTIONS,
                    TITLE_X + INS_X_OFFSET, TITLE_Y + INS_Y_OFFSET);

            if (input.wasPressed(Keys.NUM_1)) {
                System.out.println("Key 1 Pressed");
                CSV_FILE = CSV_FILE_1;
                resetGameState();
                readCsv();
                started = true;
                System.out.println("Loading CSV: " + CSV_FILE);
                track = new Track("res/track1.wav");
                track.start();
            } else if (input.wasPressed(Keys.NUM_2)) {
                System.out.println("Key 2 Pressed");
                CSV_FILE = CSV_FILE_2;
                resetGameState();
                readCsv();
                started = true;
                System.out.println("Loading CSV: " + CSV_FILE);
                track = new Track("res/track2.wav");
                track.start();
            } else if (input.wasPressed(Keys.NUM_3)) {
                System.out.println("Key 3 Pressed");
                CSV_FILE = CSV_FILE_3;
                resetGameState();
                readCsv();
                started = true;
                System.out.println("Loading CSV: " + CSV_FILE);
                track = new Track("res/track3.wav");
                track.start();
            }
        } else if (finished) {
            // end screen
            if (score >= CLEAR_SCORE) {
                TITLE_FONT.drawString(CLEAR_MESSAGE,
                        WINDOW_WIDTH/2 - TITLE_FONT.getWidth(CLEAR_MESSAGE)/2,
                        WINDOW_HEIGHT/2);
            } else {
                TITLE_FONT.drawString(TRY_AGAIN_MESSAGE,
                        WINDOW_WIDTH/2 - TITLE_FONT.getWidth(TRY_AGAIN_MESSAGE)/2,
                        WINDOW_HEIGHT/2);
            }
        } else {
            // gameplay

            SCORE_FONT.drawString("Score " + score, SCORE_LOCATION, SCORE_LOCATION);

            if (paused) {
                if (input.wasPressed(Keys.TAB)) {
                    paused = false;
                    track.run();
                }

                for (int i = 0; i < numLanes; i++) {
                    lanes[i].draw();
                }

            } else {
                currFrame++;
                for (int i = 0; i < numLanes; i++) {
                    score += lanes[i].update(input, accuracy);
                }

                accuracy.update();
                finished = checkFinished();
                if (input.wasPressed(Keys.TAB)) {
                    paused = true;
                    track.pause();
                }
            }
        }

    }

    public static int getCurrFrame() {
        return currFrame;
    }

    private boolean checkFinished() {
        for (int i = 0; i < numLanes; i++) {
            if (!lanes[i].isFinished()) {
                return false;
            }
        }
        return true;
    }

    private void resetGameState() {
        System.out.println("Resetting game state...");
        // Reset the game variables
        started = false;
        finished = false;
        paused = false;
        score = 0;
        currFrame = 0;
        numLanes = 0;
        accuracy.reset();  // Assuming you have a reset method in the Accuracy class

        for (int i = 0; i < lanes.length; i++) {
            lanes[i] = null;
        }
    
        // Reset the lanes
        for (Lane lane : lanes) {
            if (lane != null) {
                lane.reset();  // Assuming you have a reset method in the Lane class
            }
        }
    
        // Reset the track
        track.reset();  // Assuming you have a reset method in the Track class
    
        // Reload the CSV file
    }
    
    
}
