import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

/**
 * A subclass from the superClass ALifeForm, handles creating carnivores. Calls
 * the constructor of the superclass when created.
 * 
 * @param World object is used to get the world width and height.
 * @see the SimWorld class for implementation of this class
 */
public class Carnivore extends ALifeForm {
	// String to store location of the Carnivore Graphic
	private static final String wolfURL = "AlternativeWolf.png";

	// Consturctor that calls the life form constructor then sets properties of
	// its body and sense range
	Carnivore(SimWorld w) {
		super(w);
		Image wolf = new Image(wolfURL);
		this.senseRadius.setFill(Color.TRANSPARENT);
		this.lifeFormBody.setFill(Color.DARKGRAY);
		this.lifeFormBody.setFill(new ImagePattern(wolf, this.getXpos(), this.getYPos(), 1, 1, true));
	}
}
