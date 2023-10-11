gimport bagel.*;

public class SpecialNote extends Note {
    // Enum to represent the type of the special note
    public enum SpecialType {
        SPEED_UP, SLOW_DOWN, DOUBLE_SCORE
    }

    private SpecialType type;
    private final Image image;
    

    public SpecialNote(String dir, String typeString, int appearanceFrame) {
        super(dir, appearanceFrame);

        // Convert the string representation of the type to the enum
        switch (typeString) {
            case "SpeedUp":
                this.type = SpecialType.SPEED_UP;
                image = new Image("res/noteSpeedUp.png");
            case "SlowDown":
                this.type = SpecialType.SLOW_DOWN;
                image = new Image("res/noteSlowDown.png");
                break;
            case "DoubleScore":
                this.type = SpecialType.DOUBLE_SCORE;
                image = new Image("res/note2x.png"); // Replace with the path to your double score image
                break;
            default:
                throw new IllegalArgumentException("Invalid special note type: " + typeString);
        }
    }

    public SpecialType getType() {
        return type;
    }

    // Override the draw method to render the special note image
    @Override
    public void draw(int x) {
        if (isActive()) {
            image.draw(x, 100);
        }
    }

    // If there's any unique behavior for the special note, override the update method
    @Override
    public void update() {
        super.update();
        // Add any unique behavior for the special note here
    }
}
