import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
/**
 * Class to handle the objects that impede life form movement.
 * When a life form object collides with an obstacle
 * energy is lost and the life forms trajectory is flipped.
 * 
 * @param a World object is used to get the world width and height
 * when the constructor is called
 * @see the SimWorld class for implementation of this class
 */
public class Obstacle {
	// Static string to store the location in the resource folder of the
	// obstacle graphic
	private static final String obstacleURL = "DarkTreeSmaller.png";

	private boolean isPassable;
	private int xPos;
	private int yPos;
	private Circle collisionArea;
	// Random seed generator for controlling x and y placement in the world
	Random newRnd = new Random();

	// Constructor for an obstacle, ensures an obstacle is inside the world
	// before setting x and y, and defines the image for the obstacle.
	Obstacle(SimWorld aWorld) {
		int tempX = newRnd.nextInt(aWorld.getWorldWidth());
		int tempY = newRnd.nextInt(aWorld.getWorldHeight());
		boolean doStuff = true;
		// gets the world width and height and determines a safe range within
		// which to place the obstacle
		while (doStuff) {
			if (tempX > 45 && tempX < (aWorld.getWorldWidth() - 45) && tempY > 45
					&& tempY < (aWorld.getWorldHeight() - 45)) {
				this.xPos = tempX;
				this.yPos = tempY;
				doStuff = false;
			} else {
				tempX = newRnd.nextInt(aWorld.getWorldWidth());
				tempY = newRnd.nextInt(aWorld.getWorldHeight());
			}
		}
		Image obstacle = new Image(obstacleURL);
		this.isPassable = false;
		this.collisionArea = new Circle(xPos, yPos, 30);
		this.collisionArea.setFill(Color.WHEAT);
		this.collisionArea.setFill(new ImagePattern(obstacle, this.getxPos(), this.getyPos(), 1, 1, true));
	}

	// Start of getters and setters for an obstacle
	public boolean isPassable() {
		return isPassable;
	}

	public void setPassable(boolean isPassable) {
		this.isPassable = isPassable;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public Circle getCollisionArea() {
		return collisionArea;
	}

	public void setCollisionArea(Circle collisionArea) {
		this.collisionArea = collisionArea;
	}
}