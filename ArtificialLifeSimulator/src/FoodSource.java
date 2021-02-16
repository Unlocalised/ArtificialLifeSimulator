import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Used to handle food eaten by herbivores Contains certain amounts of calories
 * which are randomised upon the constructor call
 */
public class FoodSource {

	private static final String foodURL = "LongGrassSmaller.png";
	private static final String pfoodURL = "BerryBush.png";
	private Circle lifeFormBody;
	private Circle senseRadius;
	private int xPos;
	private int yPos;
	private int calories;
	private boolean isPoisonous = false;
	private boolean canBeEaten = true;
	Random newRnd = new Random();

	// Constructor for a food source, defines x and y, creates the image and
	// decides if the food is poisonous
	FoodSource(SimWorld aWorld) {
		// Temporary holders for x and y to ensure the food is placed within the
		// map
		int tempX = newRnd.nextInt(aWorld.getWorldWidth());
		int tempY = newRnd.nextInt(aWorld.getWorldHeight());
		boolean doStuff = true;
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

		int chanceOfPoison = newRnd.nextInt(10);
		this.lifeFormBody = new Circle(xPos, yPos, 20);
		this.senseRadius = new Circle(xPos, yPos, 20);
		this.senseRadius.setFill(Color.TRANSPARENT);
		this.calories = newRnd.nextInt(10);

		// Generates a random chance that a given food source will be poisonous
		// or not (3/10)
		if (chanceOfPoison % 3 == 2) {
			this.isPoisonous = true;
			Image pfood = new Image(pfoodURL);
			this.lifeFormBody.setFill(new ImagePattern(pfood, this.xPos, this.yPos, 1, 1, true));
		} else {
			this.isPoisonous = false;
			Image food = new Image(foodURL);
			this.lifeFormBody.setFill(new ImagePattern(food, this.xPos, this.yPos, 1, 1, true));
		}
	}

	// Start of getters and setters for a food source
	public Circle getLifeFormBody() {
		return lifeFormBody;
	}

	public void setLifeFormBody(Circle lifeFormBody) {
		this.lifeFormBody = lifeFormBody;
	}

	public Circle getSenseRadius() {
		return senseRadius;
	}

	public void setSenseRadius(Circle senseRadius) {
		this.senseRadius = senseRadius;
	}

	public boolean isCanBeEaten() {
		return canBeEaten;
	}

	public void setCanBeEaten(boolean canBeEaten) {
		this.canBeEaten = canBeEaten;
	}

	public boolean isPoisonous() {
		return isPoisonous;
	}

	public void setPoisonous(boolean isPoisonous) {
		this.isPoisonous = isPoisonous;
	}

	public int getCalories() {
		return calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}
}
