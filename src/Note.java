import bagel.*;

/**
 * Class for normal notes
 */
public class Note {
    private final Image image;
    private final int appearanceFrame;
    private final int speed = 2;
    private int y = 100;
    private boolean active = false;
    private boolean completed = false;

    public Note(String dir, int appearanceFrame) {
        if ("Bomb".equals(dir)) {
            image = new Image("res/noteBomb.png");
        } else {
            image = new Image("res/note" + dir + ".png");
        }
        this.appearanceFrame = appearanceFrame;
    }
    

    public boolean isActive() {
        return active;
    }
    public boolean isCompleted() {return completed;}

    public void deactivate() {
        active = false;
        completed = true;
    }

    public void update() {
        if (active) {
            y += speed;
        }

        if (ShadowDance.getCurrFrame() >= appearanceFrame && !completed) {
            active = true;
        }
    }

    public void draw(int x) {
        if (active) {
            image.draw(x, y);
        }
    }

    public int checkScore(Input input, Accuracy accuracy, int targetHeight, Keys relevantKey) {
        if (isActive()) {
            // evaluate accuracy of the key press
            int score = accuracy.evaluateScore(y, targetHeight, input.wasPressed(relevantKey));

            if (score != Accuracy.NOT_SCORED) {
                deactivate();
                return score;
            }

        }

        return 0;
    }
    
    public void reset() {
    y = 100;
    active = false;
    completed = false;
    }


}
