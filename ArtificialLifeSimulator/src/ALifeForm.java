import java.util.Random;
import javafx.scene.shape.Circle;

/**
 * Controls all abstract functions shared by life forms. Holds attributes shared
 * by all life forms. Whenever a life form is created, this class constructor is
 * called.
 * 
 * @see derived/subclasses of Herbivore and Carnivore for how this abstract
 *      class is utilised using inheritance.
 */

public abstract class ALifeForm {

	protected Circle lifeFormBody;
	protected Circle senseRadius;
	protected int maxEnergy;
	protected int age;
	protected int energy;
	protected int xPos;
	protected int yPos;
	protected int radius;
	protected int flavour;
	protected boolean isDead;
	protected boolean readyToBreed = false;
	protected boolean isPoisoned = false;
	protected boolean isVisable = true;
	protected boolean canRandMove = true;
	protected SimWorld association;
	protected float intelligenceLvl;
	protected float dx = -1.5f;
	protected float dy = -1.5f;

	// Random generator for setting x and y within the bounds of the map
	Random newRnd = new Random();

	// Constructor for a life form, sets up class attributes such as sense
	// distance and body size
	ALifeForm(SimWorld w) {
		int tempX = newRnd.nextInt(w.getWorldWidth());
		int tempY = newRnd.nextInt(w.getWorldHeight());
		boolean doStuff = true;
		while (doStuff) {
			if (tempX > 45 && tempX < (w.getWorldWidth() - 45) && tempY > 45 && tempY < (w.getWorldHeight() - 45)) {
				this.xPos = tempX;
				this.yPos = tempY;
				doStuff = false;
			} else {
				tempX = newRnd.nextInt(w.getWorldWidth());
				tempY = newRnd.nextInt(w.getWorldHeight());
			}
		}

		this.radius = 15;
		this.energy = newRnd.nextInt(1000) + 1000;
		this.maxEnergy = energy;
		this.lifeFormBody = new Circle(this.xPos, this.yPos, radius);
		this.senseRadius = new Circle(this.xPos, this.yPos, 100);
		this.intelligenceLvl = newRnd.nextFloat();
		this.setAge(newRnd.nextInt(5));
		this.setFlavour(newRnd.nextInt(300) + 80);
	}

	/**
	 * Method used for changing dx and dy values for a life form, in one of the
	 * 8 compass directions.
	 */
	public void getRandDxORDy() {
		int chanceDirection = newRnd.nextInt(8);
		switch (chanceDirection) {

		case 0: // NORTH
			this.setDx(0.0f);
			this.setDy(-1.5f);
			break;
		case 1: // NORTH EAST
			this.setDx(1.5f);
			this.setDy(-1.5f);
			break;
		case 2: // EAST
			this.setDx(1.5f);
			this.setDy(0.0f);
			break;
		case 3: // SOUTH EAST
			this.setDx(1.5f);
			this.setDy(1.5f);
			break;
		case 4: // SOUTH
			this.setDx(0.0f);
			this.setDy(1.5f);
			break;
		case 5: // SOUTH WEST
			this.setDx(1.5f);
			this.setDy(1.5f);
			break;
		case 6: // WEST
			this.setDx(-1.5f);
			this.setDy(0.0f);
			break;
		case 7: // NORTH WEST
			this.setDx(-1.5f);
			this.setDy(-1.5f);
			break;
		default:
			this.setDx(1.5f);
			this.setDy(0.0f);
		}

	}

	/**
	 * Handles each life forms translation on the simulated 2D map. This occurs
	 * each frame and is called in the collision detection methods of a world.
	 * 
	 * @see collideHerbivores and collideCarnivores methods in the SimWorld
	 *      class.
	 */
	public void move() {
		// move the lifeforms around the world in their current direction
		getLifeFormBody().setTranslateX(getLifeFormBody().getTranslateX() + getDx());
		getLifeFormBody().setTranslateY(getLifeFormBody().getTranslateY() + getDy());
		getSenseRadius().setTranslateX(getLifeFormBody().getTranslateX());
		getSenseRadius().setTranslateY(getLifeFormBody().getTranslateY());
	}

	/**
	 * Handles whenever a lifeform uses energy, if its poisoned, it loses twice
	 * as much energy after each action
	 * 
	 * @param value
	 *            represents the amount of energy to be lost after an action
	 */
	public void expendEnergy(int value) {
		if (this.isPoisoned) {
			this.energy -= value * 2;
		} else {
			this.energy -= value;
		}

	}

	// handles the lifeforms gain of energy
	public void gainEnergy(int value) {
		this.energy += value;
	}

	// Start of getters and setters for a lifeform
	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public int getYpos() {
		return yPos;
	}

	public void setYpos(int ypos) {
		yPos = ypos;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int Energy) {
		energy = Energy;
	}

	public int getXpos() {
		return xPos;
	}

	public void setXpos(int xpos) {
		xPos = xpos;
	}

	public int getYPos() {
		return yPos;
	}

	public void setYPos(int ypos) {
		yPos = ypos;
	}

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

	public void setWorld(SimWorld w) {
		this.setAssociation(w);
	}

	public SimWorld getAssociation() {
		return association;
	}

	public void setAssociation(SimWorld association) {
		this.association = association;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isVisable() {
		return isVisable;
	}

	public void setVisable(boolean isVisable) {
		this.isVisable = isVisable;
	}

	public boolean isCanRandMove() {
		return canRandMove;
	}

	public void setCanRandMove(boolean canRandMove) {
		this.canRandMove = canRandMove;
	}

	public boolean isPoisoned() {
		return isPoisoned;
	}

	public void setPoisoned(boolean isPoisoned) {
		this.isPoisoned = isPoisoned;
	}

	public boolean isReadyToBreed() {
		return readyToBreed;
	}

	public void setReadyToBreed(boolean readyToBreed) {
		this.readyToBreed = readyToBreed;
	}

	public int getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(int maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	public float getIntelligenceLvl() {
		return intelligenceLvl;
	}

	public void setIntelligenceLvl(float intelligenceLvl) {
		this.intelligenceLvl = intelligenceLvl;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getFlavour() {
		return flavour;
	}

	public void setFlavour(int flavour) {
		this.flavour = flavour;
	}

}
