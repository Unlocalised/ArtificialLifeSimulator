import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
/**
 * A subclass from the superClass ALifeForm, handles creating herbivores.
 * Sets the image for herbivores and makes their sense range transparent
 * Calls the constructor of the superclass when created.
 * 
 * @param a World object is used to get the world width and height
 * when this class's constructor is called.
 * @see the SimWorld class for implementation of this class.
 */
public class Herbivore extends ALifeForm {

	// Holds the filepath for the graphic of the herbivore
	private static final String sheepURL = "SheepEatingGrassforLifeSim.png";

	Herbivore(SimWorld w) {
		// Constructor for the herbivore which calls its parent constructor,
		// then sets the image for the herbivore
		super(w);
		Image sheep = new Image(sheepURL);
		this.getSenseRadius().setFill(Color.TRANSPARENT);
		this.getLifeFormBody().setFill(Color.WHEAT);
		this.getLifeFormBody().setFill(new ImagePattern(sheep, this.getXpos(), this.getYPos(), 1, 1, true));
	}
}
