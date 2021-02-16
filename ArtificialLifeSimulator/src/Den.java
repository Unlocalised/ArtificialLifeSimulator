import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
/**
 * Attributes for a den
 * Will allow a herbivore to enter if ready and not already occupied
 * Derived from the superclass obstacle 
 * 
 * @see Superclass Obstacle for reference to shared collision area
 */
public class Den extends Obstacle {
	// Static string to store the location in the resource folder of the den
	// graphic
	private static final String denURL = "ShelterforLifeSim.png";

	private Circle collisionBody = new Circle(this.getxPos(), this.getyPos(), 20);
	private boolean isOccupied;
	private boolean isReadyToHide;

	// Constructor for a den, calls its parent class obstacle, sets the image
	// and fills a circle with said image
	Den(SimWorld aWorld) {
		super(aWorld);
		Image den = new Image(denURL);
		this.getCollisionArea().setFill(Color.TRANSPARENT);
		this.getCollisionBody().setFill(new ImagePattern(den, this.getxPos(), this.getyPos(), 1, 1, true));
	}

	// Start of getters and setters for the den's attributes
	public boolean isOccupied() {
		return isOccupied;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public boolean isReadyToHide() {
		return isReadyToHide;
	}

	public void setReadyToHide(boolean isReadyToHide) {
		this.isReadyToHide = isReadyToHide;
	}

	public Circle getCollisionBody() {
		return collisionBody;
	}

	public void setCollisionBody(Circle collisionBody) {
		this.collisionBody = collisionBody;
	}

}
