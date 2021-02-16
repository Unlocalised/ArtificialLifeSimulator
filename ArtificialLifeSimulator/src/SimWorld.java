import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Handles all interactions between entities/ life forms. Implements collision
 * methods and dictates what happens when specific collision events occur.
 * <p>
 * Sets the background image of the world, and keeps track of events based on
 * seconds elapsed. The simTicks variable is incremented at each second, then
 * the modulus operator is used to handle timed events.
 * 
 * @see The simTicks variable in this class.
 * 
 */
public class SimWorld {
	private String worldName;
	private int worldWidth;
	private int worldHeight;
	private int numOfCarnivores;
	private int numOfHerbivores;
	private int numOfObstacles;
	private int numOfFood;
	private int totalLifeforms = 0;
	private boolean canAddC = false;
	private boolean canRemC = false;
	private boolean canRemH = false;
	private boolean isPlaying = true;
	private boolean toggleDev = false;
	private boolean toggleBreed = false;
	private boolean disaster = false;
	private int simTicks = 0;
	private int countDown = 5;
	private Timeline circleTimeLine = new Timeline();
	private Circle blastRadius;
	private ArrayList<Carnivore> carnivoreArray = new ArrayList<Carnivore>();
	private ArrayList<Herbivore> herbivoreArray = new ArrayList<Herbivore>();
	private ArrayList<Obstacle> obstacleArray = new ArrayList<Obstacle>();
	private ArrayList<FoodSource> foodArray = new ArrayList<FoodSource>();
	private ArrayList<Den> denArray = new ArrayList<Den>();
	private boolean canAddH = false;
	private ImagePattern backgroundImage = new ImagePattern(new Image("GrassBackGroundforLifeSim.png"));

	// Global variables for start time for simulation
	// Gets the start time for the world
	long startTime = System.currentTimeMillis();

	/**
	 * Adds all entities to a Group to be added to the world. Creates the scene
	 * for the GUI and has a time line which runs cycles of the simulation
	 * indefinitely
	 * <p>
	 * All timed events are handled through this method, utilising the variable
	 * simTicks. Events controlled are, for example: detecting poisoned
	 * entities, detecting collisions, removing dead entities from the world
	 * amongst other events such as breeding.
	 * 
	 * @param primaryStage
	 *            the stage to set the scene containing the simulation upon.
	 * @param pane
	 *            a vertical box used to handle the addition of the menubar and
	 *            menu items onto the scene
	 * @param hpane
	 *            another vertical box to hold the toolbar and the toolbar
	 *            buttons.
	 */
	public void runWorldCycles(Stage primaryStage, VBox pane, VBox hpane) {
		// Method to handle the addition of all entities to the world
		Group root = new Group();

		// Add all carnivores to the world
		for (int i = 0; i < carnivoreArray.size(); i++) {
			root.getChildren().add(carnivoreArray.get(i).getSenseRadius());
			root.getChildren().add(carnivoreArray.get(i).getLifeFormBody());
		}
		// Add all herbivore to the world
		for (int i = 0; i < herbivoreArray.size(); i++) {
			root.getChildren().add(herbivoreArray.get(i).getSenseRadius());
			root.getChildren().add(herbivoreArray.get(i).getLifeFormBody());
		}
		// Add all obstacles to the world
		for (int i = 0; i < obstacleArray.size(); i++) {
			root.getChildren().add(obstacleArray.get(i).getCollisionArea());
		}
		// Add all food to the world
		for (int i = 0; i < foodArray.size(); i++) {
			root.getChildren().add(foodArray.get(i).getSenseRadius());
			root.getChildren().add(foodArray.get(i).getLifeFormBody());
		}
		// Add all dens to the world
		for (int i = 0; i < denArray.size(); i++) {
			root.getChildren().add(denArray.get(i).getCollisionArea());
			root.getChildren().add(denArray.get(i).getCollisionBody());
		}
		root.getChildren().add(hpane);
		root.getChildren().add(pane);

		// Create the scene
		final Scene scene = new Scene(root, worldWidth, worldHeight);

		// Handles all operations by the simulation
		KeyFrame frame = new KeyFrame(Duration.millis(16), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				// Keeps track of simTicks value(number of seconds since the
				// world started
				if (System.currentTimeMillis() > startTime + 1000) {
					startTime = System.currentTimeMillis();
					simTicks++;
					System.out.println(simTicks);
					// Increase all carnivore ages by one each second
					for (int i = 0; i < numOfCarnivores; i++) {
						carnivoreArray.get(i).setAge(carnivoreArray.get(i).getAge() + 1);
					}
					// Increase all herbivore ages by one each second
					for (int i = 0; i < numOfHerbivores; i++) {
						herbivoreArray.get(i).setAge(herbivoreArray.get(i).getAge() + 1);
					}

				}

				// Handle collision of carnivores at each frame
				collideCarnivores(scene, root);
				// Handle collision of herbivores at each frame
				collideHerbivores(scene, root);
				// Check if a herbivore or carnivore needs to be added
				addHerbivore(scene, root);
				addCarnivore(scene, root);

				// Check if a herbivore or carnivore needs to be removed
				removeHerbivore(scene, root);
				removeCarnivore(scene, root);

				// Detect death of any entities and remove from the world
				detectDeath(scene, root);

				// Detect a toggle in dev mode and display if necesary
				toggleDevMode();

				// If a disaster is to be triggered
				if (isDisaster()) {
					// Call the disaster function
					triggerDisaster(scene, root);
				}

				/*
				 * Every frame, if any carnivores or herbivores energy is > //
				 * 2000 they can breed, must be called before forced breeding
				 */
				for (int i = 0; i < numOfCarnivores; i++) {
					if (carnivoreArray.get(i).getEnergy() > 2000) {
						carnivoreArray.get(i).setReadyToBreed(true);
					} else {
						carnivoreArray.get(i).setReadyToBreed(false);
					}
				}

				for (int j = 0; j < numOfHerbivores; j++) {
					if (herbivoreArray.get(j).getEnergy() > 2000) {
						herbivoreArray.get(j).setReadyToBreed(true);
					} else {
						herbivoreArray.get(j).setReadyToBreed(false);
					}

				}

				// If the user selects the quick breeding option
				if (isToggleBreed()) {
					forceBreeding(scene, root);
				}

				// Detect whether animals are allowed to breed at each frame
				detectBreeding(scene, root);

				// Every 2 seconds detect poisoned entities in the world
				if (simTicks % 2 == 0) {
					detectPosion();
				}
				// Every 3 seconds, if an entity is stationary, move it in a
				// random direction
				if (simTicks % 3 == 0) {
					for (int i = 0; i < carnivoreArray.size(); i++) {
						if (carnivoreArray.get(i).getDx() == 0 || carnivoreArray.get(i).getDy() == 0) {
							carnivoreArray.get(i).getRandDxORDy();
						}
					}
					for (int i = 0; i < herbivoreArray.size(); i++) {
						if (herbivoreArray.get(i).getDx() == 0 && herbivoreArray.get(i).getDy() == 0
								&& herbivoreArray.get(i).isVisable() == true) {
							herbivoreArray.get(i).getRandDxORDy();
						}

					}
				}
				// Every 10 seconds if a foodsource has been eaten refresh the
				// foodsource
				if (simTicks % 10 == 0) {
					for (int i = 0; i < foodArray.size(); i++) {
						if (foodArray.get(i).isCanBeEaten() == false) {
							foodArray.get(i).getLifeFormBody().setVisible(true);
							foodArray.get(i).setCanBeEaten(true);
						}
					}
				}

				// Every 20 seconds detect if a herbivore is occupying a den and
				// eject them
				if (simTicks % 20 == 0) {
					for (int h = 0; h < herbivoreArray.size(); h++) {
						// At this point its known that the herbivore is
						// stationary
						if (herbivoreArray.get(h).isVisable() == false) {
							herbivoreArray.get(h).setVisable(true);
							herbivoreArray.get(h).getLifeFormBody().setVisible(true);
							herbivoreArray.get(h).setDx(2.5f);
							herbivoreArray.get(h).setDy(2.5f);
						}
					}
				}
				// Every 25 seconds set the dens to available again if they were
				// occupied
				if (simTicks % 25 == 0) {
					for (int i = 0; i < denArray.size(); i++) {
						if (denArray.get(i).isOccupied() == true) {
							denArray.get(i).setOccupied(false);
						}
					}
				}

			}

		});
		// Set the animation to run indefinietely
		circleTimeLine.setCycleCount(Timeline.INDEFINITE);
		circleTimeLine.getKeyFrames().add(frame);
		circleTimeLine.setRate(1.5);
		// Set up and show the world stage
		primaryStage.setTitle("Artificial Life Simulator");
		scene.setFill(this.backgroundImage);
		primaryStage.setScene(scene);
		primaryStage.setHeight(worldHeight);
		primaryStage.setWidth(worldWidth);
		primaryStage.setResizable(false);
		primaryStage.setX(worldWidth / 3);
		primaryStage.show();
	}

	// Constructor to handle initial setup of world and parameters
	SimWorld(int width, int height, int carnivores, int herbivores, int obstacles, int food) {
		worldWidth = width;
		worldHeight = height;
		numOfCarnivores = carnivores;
		numOfHerbivores = herbivores;
		numOfObstacles = obstacles;
		numOfFood = food;
		simTicks = 0; // Used later to record duration of simulation and to
						// handle random events
		totalLifeforms = numOfCarnivores + numOfHerbivores;
	}

	/**
	 * Creates the entity objects for all pieces of the map (for example,
	 * carnivores, herbivores, dens and obstacles). This is based upon an
	 * individual number of each entity the user has previously defined when
	 * setting up the world.
	 */
	public void createWorld() {
		// Create and add all carnivores and add them to the appropriate
		// ArrayList
		for (int i = 0; i < numOfCarnivores; i++) {
			Carnivore newCarnivore = new Carnivore(this);
			carnivoreArray.add(newCarnivore);
		}

		// Create and add all herbivores and add them to the appropriate
		// ArrayList
		for (int i = 0; i < numOfHerbivores; i++) {
			Herbivore newHerbivore = new Herbivore(this);
			herbivoreArray.add(newHerbivore);

		}

		// Create all obstacles and add them to the appropriate ArrayList
		for (int i = 0; i < numOfObstacles; i++) {
			Obstacle newObstacle = new Obstacle(this);
			obstacleArray.add(newObstacle);
		}

		// Create and add all dens based off the number of lifeforms
		for (int i = 0; i < totalLifeforms; i++) {
			if (i % 20 == 0) { // handles the proportion of dens based off
								// number of lifeforms in the map
				// 1/10th of the total lifeforms = number of dens
				Den newDen = new Den(this);
				denArray.add(newDen);
			}
		}

		// Create all food and add to the food arraylist
		for (int i = 0; i < numOfFood; i++) {
			FoodSource newFood = new FoodSource(this);
			foodArray.add(newFood);
		}
	}

	/**
	 * Handles the collisions necessary for herbivores in the array list to
	 * interact with other items on the map. Primarily deals with collisions on
	 * the world edges (scene width and scene height). Also deals with when a
	 * herbivore collides with one of the following: a food source; a den and an
	 * obstacle.
	 * <p>
	 * Calls the move function in a life form to translate each entities
	 * position in the next frame of the animation. Sensing events are handled
	 * in the world class as they are modelled as collisions of objects in array
	 * lists.
	 * 
	 * @param scene
	 *            the scene possessed by the current world which is used to get
	 *            world specific data.
	 * @param root
	 *            the group of all items on the map.
	 */
	public void collideHerbivores(Scene scene, Group root) {
		// For all herbivores in the map
		for (int i = 0; i < herbivoreArray.size(); i++) {
			if (herbivoreArray.get(i).getLifeFormBody().getCenterX()
					+ herbivoreArray.get(i).getLifeFormBody().getTranslateX() < herbivoreArray.get(i).getLifeFormBody()
							.getRadius()
					|| herbivoreArray.get(i).getLifeFormBody().getCenterX()
							+ herbivoreArray.get(i).getLifeFormBody().getTranslateX()
							+ herbivoreArray.get(i).getLifeFormBody().getRadius() > scene.getWidth()) {
				// Deal with collisions off of the world horizontal borders and
				// lose energy
				herbivoreArray.get(i).setDx(herbivoreArray.get(i).getDx() * -1);
				herbivoreArray.get(i).expendEnergy(100);
			}

			if (herbivoreArray.get(i).getLifeFormBody().getCenterY()
					+ herbivoreArray.get(i).getLifeFormBody().getTranslateY() < herbivoreArray.get(i).getLifeFormBody()
							.getRadius()
					|| herbivoreArray.get(i).getLifeFormBody().getCenterY()
							+ herbivoreArray.get(i).getLifeFormBody().getTranslateY()
							+ herbivoreArray.get(i).getLifeFormBody().getRadius() > scene.getHeight()) {
				// Deal with collisions off of the world vertical borders and
				// lose energy
				herbivoreArray.get(i).setDy(herbivoreArray.get(i).getDy() * -1);
				herbivoreArray.get(i).expendEnergy(100);
			}

			// Handle moving throughout the world
			herbivoreArray.get(i).move();

			// If a herbivore collides with an obstacle
			for (int k = 0; k < obstacleArray.size(); k++) {
				if (hasCollided(herbivoreArray.get(i).getLifeFormBody(),
						obstacleArray.get(k).getCollisionArea()) == true) {
					// Bounce off the obstacle and expend energy
					herbivoreArray.get(i).setDx(herbivoreArray.get(i).getDx() * -1);
					herbivoreArray.get(i).setDy(herbivoreArray.get(i).getDy() * -1);
					herbivoreArray.get(i).expendEnergy(100);
				}
			}

			// If a herbivore collides with a food sense area
			for (int l = 0; l < foodArray.size(); l++) {
				if (hasCollided(herbivoreArray.get(i).getSenseRadius(), foodArray.get(l).getLifeFormBody())
						&& foodArray.get(l).isCanBeEaten() == true && herbivoreArray.get(i).isVisable() == true) {
					// If the X translation vector of the herbivore is greater
					// than the current food being checked
					if (herbivoreArray.get(i).getLifeFormBody().getCenterX() + herbivoreArray.get(i).getLifeFormBody()
							.getTranslateX() > foodArray.get(l).getLifeFormBody().getCenterX()
									+ foodArray.get(l).getLifeFormBody().getTranslateX()) {
						// reverse the direction of movement toward the food
						herbivoreArray.get(i).setDx(-2.5f);

					} else {
						// speed up towards the herbivore
						herbivoreArray.get(i).setDx(2.5f);

					}
					// If the Y translation vector of the herbivore is greater
					// than that of the current food being checked
					if (herbivoreArray.get(i).getLifeFormBody().getCenterY() + herbivoreArray.get(i).getLifeFormBody()
							.getTranslateY() > foodArray.get(l).getLifeFormBody().getCenterY()
									+ foodArray.get(l).getLifeFormBody().getTranslateY()) {
						// Reverse the direction of movement towards herbivore
						herbivoreArray.get(i).setDy(-2.5f);

					} else {
						// speed up towards the herbivore
						herbivoreArray.get(i).setDy(2.5f);

					}

					// If a herbivore collides with the body of the food
					if (hasCollided(herbivoreArray.get(i).getLifeFormBody(),
							foodArray.get(l).getLifeFormBody()) == true) {
						// if food is poisonous and the bug cannot detect that
						if (foodArray.get(l).isPoisonous() && herbivoreArray.get(i).getIntelligenceLvl() < 0.7) {
							// eat food and poison bug
							herbivoreArray.get(i).setPoisoned(true);
							foodArray.get(l).setCanBeEaten(false);
							foodArray.get(l).getLifeFormBody().setVisible(false);
							// try getimagepattern from foodsource and swapping
							// with long/ short grass depending on the value of
							// iseaten

							// Prevent the herbivore from moving shortly after
							// eating
							herbivoreArray.get(i).setDx(0.0f);
							herbivoreArray.get(i).setDy(0.0f);
						} else { // eat around the poisoned food and gain
									// nutrition
							foodArray.get(l).setCanBeEaten(false);
							foodArray.get(l).getLifeFormBody().setVisible(false);

							// Prevent the herbivore from moving shortly after
							// eating
							herbivoreArray.get(i).setDx(0.0f);
							herbivoreArray.get(i).setDy(0.0f);

						}
						herbivoreArray.get(i).gainEnergy(foodArray.get(l).getCalories());
					}

				}

			}
			// If a herbivore collides with a den and it is not occupied
			for (int m = 0; m < denArray.size(); m++) {
				if (hasCollided(herbivoreArray.get(i).getSenseRadius(), denArray.get(m).getCollisionBody())
						&& denArray.get(m).isOccupied() == false) {
					// If the X translation vector of the herbivore is greater
					// than the den
					if (herbivoreArray.get(i).getLifeFormBody().getCenterX() + herbivoreArray.get(i).getLifeFormBody()
							.getTranslateX() > denArray.get(m).getCollisionBody().getCenterX()
									+ denArray.get(m).getCollisionBody().getTranslateX()) {
						// reverse the direction of movement toward the den
						herbivoreArray.get(i).setDx(-2.5f);
					} else {
						// speed up towards the den
						herbivoreArray.get(i).setDx(2.5f);
					}
					// If the Y translation vector of the herbivore is higher
					// than that of the current den being checked
					if (herbivoreArray.get(i).getLifeFormBody().getCenterY() + herbivoreArray.get(i).getLifeFormBody()
							.getTranslateY() > denArray.get(m).getCollisionBody().getCenterY()
									+ denArray.get(m).getCollisionBody().getTranslateY()) {
						// Reverse the direction of movement towards the den
						herbivoreArray.get(i).setDy(-2.5f);
						herbivoreArray.get(i).expendEnergy(1);
					} else {
						// speed up towards the den
						herbivoreArray.get(i).setDy(2.5f);
						herbivoreArray.get(i).expendEnergy(1);
					}
					if (hasCollided(herbivoreArray.get(i).getLifeFormBody(),
							denArray.get(m).getCollisionBody()) == true) {
						// If the herbivore has collided with the den set the
						// den to occupied, and hide the bug from the map
						denArray.get(m).setOccupied(true);
						herbivoreArray.get(i).setVisable(false);
						herbivoreArray.get(i).getLifeFormBody().setVisible(false);

						// Prevent the herbivore from moving until ejected by
						// the den in world
						herbivoreArray.get(i).setDx(0.0f);
						herbivoreArray.get(i).setDy(0.0f);

						// Lose energy upon entering the den
						herbivoreArray.get(i).expendEnergy(100);

					}
				}
			}
		}

	}

	/**
	 * Handles the collisions necessary for carnivores in the array list to
	 * interact with other items on the map. As with the herbivores, it handles
	 * collision with the world borders first, then with herbivores and
	 * obstacles.
	 * <p>
	 * Calls the move function in a life form to translate each entities
	 * position in the next frame of the animation. Also handles the removal of
	 * killed entities once they have been eaten.
	 * 
	 * @param scene
	 *            the scene possessed by the current world which is used to get
	 *            world specific data.
	 * @param root
	 *            the group of all items on the map, used to get the correct
	 *            object in the group to remove upon a herbivores death
	 */
	public void collideCarnivores(Scene scene, Group root) {
		// For all carnivores in the map
		for (int i = 0; i < carnivoreArray.size(); i++) {
			if (carnivoreArray.get(i).getLifeFormBody().getCenterX()
					+ carnivoreArray.get(i).getLifeFormBody().getTranslateX() < carnivoreArray.get(i).getLifeFormBody()
							.getRadius()
					|| carnivoreArray.get(i).getLifeFormBody().getCenterX()
							+ carnivoreArray.get(i).getLifeFormBody().getTranslateX()
							+ carnivoreArray.get(i).getLifeFormBody().getRadius() > scene.getWidth()) {
				// Handle collisions with horizontal world boundaries
				carnivoreArray.get(i).setDx(carnivoreArray.get(i).getDx() * -1);
				carnivoreArray.get(i).expendEnergy(100);
			}
			if (carnivoreArray.get(i).getLifeFormBody().getCenterY()
					+ carnivoreArray.get(i).getLifeFormBody().getTranslateY() < carnivoreArray.get(i).getLifeFormBody()
							.getRadius()
					|| carnivoreArray.get(i).getLifeFormBody().getCenterY()
							+ carnivoreArray.get(i).getLifeFormBody().getTranslateY()
							+ carnivoreArray.get(i).getLifeFormBody().getRadius() > scene.getHeight()) {
				// Handle collisions with vertical world boundaries
				carnivoreArray.get(i).setDy(carnivoreArray.get(i).getDy() * -1);
				carnivoreArray.get(i).expendEnergy(100);
			}
			// Move the carnivores
			carnivoreArray.get(i).move();

			for (int j = 0; j < herbivoreArray.size(); j++) {

				// Check collision between carnivore sense radius and herbivore
				// body
				if (hasCollided(carnivoreArray.get(i).getSenseRadius(), herbivoreArray.get(j).getLifeFormBody()) == true
						&& herbivoreArray.get(j).isVisable() == true) {
					if (herbivoreArray.get(j).isPoisoned() && carnivoreArray.get(i).getIntelligenceLvl() < 0.7) {
						// If the herbivore being checked is poisoned and the
						// intelligence of the carnivore is low
						// If the X translation vector of the carnivore is
						// higher than that of the current herbivore being
						// checked
						if (carnivoreArray.get(i).getLifeFormBody().getCenterX()
								+ carnivoreArray.get(i).getLifeFormBody().getTranslateX() > herbivoreArray.get(j)
										.getLifeFormBody().getCenterX()
										+ herbivoreArray.get(j).getLifeFormBody().getTranslateX()) {
							// reverse the direction of movement toward
							// herbivore
							carnivoreArray.get(i).setDx(-2.0f);
						} else {
							// speed up towards the herbivore
							carnivoreArray.get(i).setDx(2.0f);
						}
						// If the Y translation vector of the carnivore is
						// higher than that of the current herbivore being
						// checked
						if (carnivoreArray.get(i).getLifeFormBody().getCenterY()
								+ carnivoreArray.get(i).getLifeFormBody().getTranslateY() > herbivoreArray.get(j)
										.getLifeFormBody().getCenterY()
										+ herbivoreArray.get(j).getLifeFormBody().getTranslateY()) {
							// Reverse the direction of movement towards
							// herbivore
							carnivoreArray.get(i).setDy(-2.0f);
						} else {
							// speed up towards the herbivore
							carnivoreArray.get(i).setDy(2.0f);
						}

						if (hasCollided(carnivoreArray.get(i).getLifeFormBody(),
								herbivoreArray.get(j).getLifeFormBody()) == true) {
							// Once eaten, poison the carnivore
							carnivoreArray.get(i).setPoisoned(true);
							root.getChildren().remove(herbivoreArray.get(j).getLifeFormBody());
							root.getChildren().remove(herbivoreArray.get(j).getSenseRadius());
							herbivoreArray.get(j).setDead(true);
							herbivoreArray.remove(j);
							numOfHerbivores--;

							// Prevent the carnivore from moving after eating
							carnivoreArray.get(i).setDx(0.0f);
							carnivoreArray.get(i).setDy(0.0f);

						}
					} else if (herbivoreArray.get(j).isPoisoned() && carnivoreArray.get(i).getIntelligenceLvl() > 0.7) {
						// If the herbivore is poisoned and the carnivore is
						// intelligent enough, ignore the herbivore
						carnivoreArray.get(i).setDx(carnivoreArray.get(i).getDx() * -1);
						carnivoreArray.get(i).setDy(carnivoreArray.get(i).getDy() * -1);
					} else {
						// If the herbivore is not posioned, all carnivores can
						// eat it
						// If the X translation vector of the carnivore is
						// higher than that of the current herbivore being
						// checked
						if (carnivoreArray.get(i).getLifeFormBody().getCenterX()
								+ carnivoreArray.get(i).getLifeFormBody().getTranslateX() > herbivoreArray.get(j)
										.getLifeFormBody().getCenterX()
										+ herbivoreArray.get(j).getLifeFormBody().getTranslateX()) {
							// reverse the direction of movement toward
							// herbivore
							carnivoreArray.get(i).setDx(-2.0f);
						} else {
							// speed up towards the herbivore
							carnivoreArray.get(i).setDx(2.0f);
						}
						// If the Y translation vector of the carnivore is
						// higher than that of the current herbivore being
						// checked
						if (carnivoreArray.get(i).getLifeFormBody().getCenterY()
								+ carnivoreArray.get(i).getLifeFormBody().getTranslateY() > herbivoreArray.get(j)
										.getLifeFormBody().getCenterY()
										+ herbivoreArray.get(j).getLifeFormBody().getTranslateY()) {
							// Reverse the direction of movement towards
							// herbivore
							carnivoreArray.get(i).setDy(-2.0f);
						} else {
							// speed up towards the herbivore
							carnivoreArray.get(i).setDy(2.0f);
						}

						// If the carnivore and herbivore have collided, remove
						// herbivore
						if (hasCollided(carnivoreArray.get(i).getLifeFormBody(),
								herbivoreArray.get(j).getLifeFormBody()) == true) {
							root.getChildren().remove(herbivoreArray.get(j).getLifeFormBody());
							root.getChildren().remove(herbivoreArray.get(j).getSenseRadius());
							herbivoreArray.get(j).setDead(true);
							carnivoreArray.get(i).gainEnergy(herbivoreArray.get(j).getFlavour());
							herbivoreArray.remove(j);
							numOfHerbivores--;
							// Prevent the carnivore from moving after eating
							// ::temporary
							carnivoreArray.get(i).setDx(0.0f);
							carnivoreArray.get(i).setDy(0.0f);

						}

					}

				}

			}
			// If a carnivore collides with an obstacle bounce off
			for (int k = 0; k < obstacleArray.size(); k++) {
				if (hasCollided(carnivoreArray.get(i).getLifeFormBody(),
						obstacleArray.get(k).getCollisionArea()) == true) {
					carnivoreArray.get(i).setDx(carnivoreArray.get(i).getDx() * -1);
					carnivoreArray.get(i).setDy(carnivoreArray.get(i).getDy() * -1);
					carnivoreArray.get(i).expendEnergy(100);
				}

			}
		}
	}

	/**
	 * Returns a boolean value as to whether two circles in the world have
	 * collided Works using pythagoras theorem to detect if any two given circle
	 * objects have intersected.
	 * 
	 * @param predAreaToCheck-
	 *            represents the first circle in the world being checked for
	 *            collisions.
	 * @param preyAreaToChheck-
	 *            represents the second circle in the world being checked for
	 *            collisions, usually for the object being consumed by the
	 *            predator.
	 */
	// Function to check if any two given circles in the map have collided
	public boolean hasCollided(Circle predAreaToCheck, Circle preyAreaToCheck) {
		if (Math.pow((predAreaToCheck.getTranslateX() + predAreaToCheck.getCenterX())
				- (preyAreaToCheck.getTranslateX() + preyAreaToCheck.getCenterX()), 2)
				+ Math.pow((predAreaToCheck.getTranslateY() + predAreaToCheck.getCenterY())
						- (preyAreaToCheck.getTranslateY() + preyAreaToCheck.getCenterY()), 2) <= Math
								.pow(predAreaToCheck.getRadius() + preyAreaToCheck.getRadius(), 2)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Finds the last placed herbivore in the world, creates a new herbivore and
	 * adds to the array list. Increments the number of herberivores and adds
	 * the herbivore to the group (whilst sending it to the back of the scene as
	 * to not interfere with menu controls)
	 */
	public void addHerbivore(Scene scene, Group root) {
		// If the user is allowed to add herbivores to the map
		if (isCanAddH() == true) {
			// Create a new herbivore, add to the scene, and send it to the back
			int lastIndex = root.getChildren().size();
			Herbivore newHerbivore = new Herbivore(this);
			herbivoreArray.add(newHerbivore);
			numOfHerbivores++;
			setCanAddH(false);
			root.getChildren().add(newHerbivore.getLifeFormBody());
			root.getChildren().get(lastIndex).toBack();
			lastIndex++;
			root.getChildren().add(newHerbivore.getSenseRadius());
			root.getChildren().get(lastIndex).toBack();
			lastIndex++;
		}
	}

	/**
	 * Finds the last placed carnivore in the world, creates a new carnivore and
	 * adds to the array list. Increments the number of carnivores and adds the
	 * carnivore to the group (whilst sending it to the back of the scene as to
	 * not interfere with menu controls)
	 */
	public void addCarnivore(Scene scene, Group root) {
		// If the user is allowed to add carnivores to the map
		if (isCanAddC() == true) {
			// Create a new carnivore, add to the scene, and send it to the back
			int lastIndex = root.getChildren().size();
			Carnivore newCarnivore = new Carnivore(this);
			carnivoreArray.add(newCarnivore);
			numOfCarnivores++;
			setCanAddC(false);
			root.getChildren().add(newCarnivore.getLifeFormBody());
			root.getChildren().get(lastIndex).toBack();
			lastIndex++;
			root.getChildren().add(newCarnivore.getSenseRadius());
			root.getChildren().get(lastIndex).toBack();
			lastIndex++;
		}
	}

	/**
	 * Removes the last herbivore added to the map through any means. removes
	 * the graphical representation of the life form from the group root and
	 * deletes the last entry in the array list
	 */
	public void removeHerbivore(Scene scene, Group root) { // removes the last
															// added herbivore
															// to the map
		if (herbivoreArray.size() > 0 && isCanRemH() == true) {
			root.getChildren().remove(herbivoreArray.get(numOfHerbivores - 1).getLifeFormBody());
			root.getChildren().remove(herbivoreArray.get(numOfHerbivores - 1).getSenseRadius());
			herbivoreArray.remove(numOfHerbivores - 1);
			numOfHerbivores--;
			setCanRemH(false);

		}

	}

	/**
	 * Removes the last carnivore added to the map through any means. removes
	 * the graphics for the carnivores and then removes the data of the
	 * carnivore object from the array list
	 */
	public void removeCarnivore(Scene scene, Group root) { // removes the last
															// added carnivore
															// to the map
		if (carnivoreArray.size() > 0 && isCanRemC() == true) {
			root.getChildren().remove(carnivoreArray.get(numOfCarnivores - 1).getLifeFormBody());
			root.getChildren().remove(carnivoreArray.get(numOfCarnivores - 1).getSenseRadius());
			carnivoreArray.remove(numOfCarnivores - 1);
			numOfCarnivores--;
			setCanRemC(false);

		}

	}

	/**
	 * Sets the colour of the sense radius to a slightly translucent colour and
	 * visable to the user. Primarily used for demonstration purposes of how the
	 * simulation works. Works on a toggle basis from a radiobutton from the
	 * GUIWorld class If the toggle is off, it resets the colour to transparent
	 * so the user no longer sees it
	 * 
	 * @see GUIWorld class
	 */

	public void toggleDevMode() {
		if (this.isToggleDev() == true) {
			for (int i = 0; i < numOfCarnivores; i++) {
				carnivoreArray.get(i).getSenseRadius().setOpacity(0.3);
				carnivoreArray.get(i).getSenseRadius().setFill(Color.CYAN);
			}
			for (int j = 0; j < numOfHerbivores; j++) {
				herbivoreArray.get(j).getSenseRadius().setOpacity(0.3);
				herbivoreArray.get(j).getSenseRadius().setFill(Color.DARKVIOLET);
			}
		} else if (this.isToggleDev() == false) {
			for (int i = 0; i < numOfCarnivores; i++) {
				carnivoreArray.get(i).getSenseRadius().setOpacity(0);
				carnivoreArray.get(i).getSenseRadius().setFill(Color.TRANSPARENT);
			}
			for (int j = 0; j < numOfHerbivores; j++) {
				herbivoreArray.get(j).getSenseRadius().setOpacity(0);
				herbivoreArray.get(j).getSenseRadius().setFill(Color.TRANSPARENT);
			}
		}

	}

	/**
	 * Used to print data about lifeforms currently in the map. Used to fill a
	 * text area on the scene of the simulation.
	 * 
	 * @see use of the returned list in the GUIWorld class
	 * @return lifeFormData returns a string list of specific data from all
	 *         entities in the world.
	 */
	public ArrayList<String> getAllLifeFormData() {
		ArrayList<String> lifeFormData = new ArrayList<String>();
		// For all of the carnivores in the arraylist, add the data to the
		// arraylist
		for (int i = 0; i < carnivoreArray.size(); i++) {
			lifeFormData.add("\n" + (i + 1) + ".)" + " Carnivore Data:");
			lifeFormData.add("Energy: " + Integer.toString(carnivoreArray.get(i).getEnergy()));
			lifeFormData.add("X Position: " + Double.toString(carnivoreArray.get(i).getLifeFormBody().getCenterX()));
			lifeFormData.add("Y Position: " + Double.toString(carnivoreArray.get(i).getLifeFormBody().getCenterY()));
			lifeFormData.add("X Speed: " + Double.toString(carnivoreArray.get(i).getDx()));
			lifeFormData.add("Y Speed: " + Double.toString(carnivoreArray.get(i).getDy()));
			lifeFormData.add("Bug intelligence: " + (carnivoreArray.get(i).getIntelligenceLvl()));
			lifeFormData.add("Sense Radius: " + carnivoreArray.get(i).getSenseRadius().getRadius());
		}
		// For all of the herbivores in the arraylist, add the data to the
		// textArea
		for (int i = 0; i < herbivoreArray.size(); i++) {
			lifeFormData.add("\n" + (i + 1) + ".)" + " Herbivore Data:");
			lifeFormData.add("Energy: " + Integer.toString(herbivoreArray.get(i).getEnergy()));
			lifeFormData.add("X Position: " + Double.toString(herbivoreArray.get(i).getLifeFormBody().getCenterX()));
			lifeFormData.add("Y Position: " + Double.toString(herbivoreArray.get(i).getLifeFormBody().getCenterY()));
			lifeFormData.add("X Speed: " + Double.toString(herbivoreArray.get(i).getDx()));
			lifeFormData.add("Y Speed: " + Double.toString(herbivoreArray.get(i).getDy()));
			lifeFormData.add("Bug intelligence: " + (herbivoreArray.get(i).getIntelligenceLvl()));
			lifeFormData.add("Sense Radius: " + herbivoreArray.get(i).getSenseRadius().getRadius());

		}
		// Return the arraylist to be used to print the contents to the screen
		return lifeFormData;

	}

	/**
	 * Increases the energy and intelligence level of the carnivores currently
	 * in the map
	 */
	public void editCarnivores() {
		// Increases the energy and intelligence values of the carnivores
		for (int i = 0; i < carnivoreArray.size(); i++) {
			carnivoreArray.get(i).setEnergy(carnivoreArray.get(i).getEnergy() + 200);
			carnivoreArray.get(i).setIntelligenceLvl(carnivoreArray.get(i).getIntelligenceLvl() + 0.1f);

		}
	}

	/**
	 * Increases the energy and intelligence level of the herbivores currently
	 * in the map
	 */
	public void editHerbivores() {
		// Increases the energy and intelligence value of herbivores
		for (int i = 0; i < herbivoreArray.size(); i++) {
			herbivoreArray.get(i).setEnergy(herbivoreArray.get(i).getEnergy() + 200);
			herbivoreArray.get(i).setIntelligenceLvl(herbivoreArray.get(i).getIntelligenceLvl() + 0.1f);
		}
	}

	/**
	 * For all of the life forms in the map, encircle them with red if they have
	 * been poisoned to notify the user.
	 * 
	 * @see the use of modified for loop
	 */
	public void detectPosion() {
		// For all herbivores, set an outline to darkred if poisoned
		for (Herbivore herbivore : herbivoreArray) {
			if (herbivore.isPoisoned()) {
				herbivore.getLifeFormBody().setStroke(Color.DARKRED);
				herbivore.getLifeFormBody().setStrokeWidth(2);
			}
		}
		// For all carnivores, set an outline to darkred if poisoned
		for (Carnivore carnivore : carnivoreArray) {
			if (carnivore.isPoisoned()) {
				carnivore.getLifeFormBody().setStroke(Color.DARKRED);
				carnivore.getLifeFormBody().setStrokeWidth(2);
			}
		}
	}

	/**
	 * Removes nodes from the group in the simulation based on certain factors.
	 * For example, if a herbivores age surpasses 200 or at any time their
	 * energy is below zero, they are removed from the map.
	 * 
	 * @param scene
	 *            used to hold the current scene being operated upon
	 * @param root
	 *            Group passed to the function which is used to remove the
	 *            correct graphical representation of the entity which has died.
	 */
	public void detectDeath(Scene scene, Group root) {
		for (int i = 0; i < herbivoreArray.size(); i++) {
			if (herbivoreArray.get(i).getEnergy() <= 0 || herbivoreArray.get(i).getAge() > 200) {
				root.getChildren().remove(herbivoreArray.get(i).getLifeFormBody());
				root.getChildren().remove(herbivoreArray.get(i).getSenseRadius());
				herbivoreArray.get(i).setDead(true);
				herbivoreArray.remove(i);
				numOfHerbivores--;
			}
		}
		for (int i = 0; i < carnivoreArray.size(); i++) {
			if (carnivoreArray.get(i).getEnergy() <= 0 || carnivoreArray.get(i).getAge() > 200) {
				root.getChildren().remove(carnivoreArray.get(i).getLifeFormBody());
				root.getChildren().remove(carnivoreArray.get(i).getSenseRadius());
				carnivoreArray.get(i).setDead(true);
				carnivoreArray.remove(i);
				numOfCarnivores--;
			}
		}
	}

	/**
	 * Handles collisions of carnivores with other carnivores when they are
	 * ready to breed. This can occur when their breeding boolean value has been
	 * set manually or when their energy is high enough.
	 * 
	 * @see forceBreeing method that sets ready to breed to true
	 */
	public void detectBreeding(Scene scene, Group root) {
		for (int i = 0; i < carnivoreArray.size(); i++) {

			for (int j = 0; j < carnivoreArray.size(); j++) {
				// If the carnivore being checked is not equal to itself and has
				// collided with another carnivore
				if (j != i
						&& hasCollided(carnivoreArray.get(i).getLifeFormBody(), carnivoreArray.get(j).getLifeFormBody())
						&& carnivoreArray.get(i).isReadyToBreed() && carnivoreArray.get(j).isReadyToBreed()) {
					setCanAddC(true);
					// After breeding, set able to breed to false and expend
					// energy
					carnivoreArray.get(i).setReadyToBreed(false);
					carnivoreArray.get(j).setReadyToBreed(false);
					carnivoreArray.get(i).expendEnergy(1000);
					carnivoreArray.get(j).expendEnergy(1000);
				}
			}
		}

		for (int i = 0; i < herbivoreArray.size(); i++) {

			for (int j = 0; j < herbivoreArray.size(); j++) {
				// If the herbivore being checked is not equal to itself and has
				// collided with another herbivore
				if (j != i
						&& hasCollided(herbivoreArray.get(i).getLifeFormBody(), herbivoreArray.get(j).getLifeFormBody())
						&& herbivoreArray.get(i).isReadyToBreed() && herbivoreArray.get(j).isReadyToBreed()) {
					setCanAddH(true);
					// After breeding, set able to breed to false and expend
					// energy
					herbivoreArray.get(i).setReadyToBreed(false);
					herbivoreArray.get(j).setReadyToBreed(false);
					herbivoreArray.get(i).expendEnergy(1000);
					herbivoreArray.get(j).expendEnergy(1000);
				}
			}
		}

	}

	/**
	 * sets all life forms ready to breed despite energy levels
	 */
	public void forceBreeding(Scene scene, Group root) {
		for (Carnivore carnivore : carnivoreArray) {
			carnivore.setReadyToBreed(true);
		}
		for (Herbivore herbivore : herbivoreArray) {
			herbivore.setReadyToBreed(true);
		}
	}

	/**
	 * Invokes a function which removes entities from the map and changes the
	 * background image of the map. A red circle reticule is painted on the map,
	 * then a counter counts down from 5. After the countdown some entities are
	 * removed from the scene and the fill of the map background is set to a
	 * different theme.
	 * 
	 * @param scene
	 *            used to store the current scene the simulation is running
	 * @param root
	 *            used to add the circle reticule when the countdown begins and
	 *            remove it again when the count down ends.
	 */
	public void triggerDisaster(Scene scene, Group root) {
		// Create a new timeline with 1 second frames to count from 5 to 1
		Timeline falloutSiren = new Timeline();
		KeyFrame frame = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				if (getCountDown() == 5) {
					blastRadius = new Circle(worldWidth / 2, worldHeight / 2, worldHeight / 2);
					blastRadius.setFill(Color.CRIMSON);
					blastRadius.setOpacity(0.3);
					root.getChildren().add(blastRadius);

				}

				if (getCountDown() == 1) {
					// remove half of carnivores
					for (int i = 0; i < carnivoreArray.size(); i++) {
						if (carnivoreArray.size() > 0) {
							root.getChildren().remove(carnivoreArray.get(numOfCarnivores - 1).getLifeFormBody());
							root.getChildren().remove(carnivoreArray.get(numOfCarnivores - 1).getSenseRadius());
							carnivoreArray.remove(numOfCarnivores - 1);
							numOfCarnivores--;

						}
					}
					// Remove half of herbivores
					for (int i = 0; i < herbivoreArray.size(); i++) {
						if (herbivoreArray.size() > 0) {
							root.getChildren().remove(herbivoreArray.get(numOfHerbivores - 1).getLifeFormBody());
							root.getChildren().remove(herbivoreArray.get(numOfHerbivores - 1).getLifeFormBody());
							herbivoreArray.remove(numOfHerbivores - 1);
							numOfHerbivores--;
						}
					}
					// Remove the painted target circle and change the
					// background
					root.getChildren().remove(blastRadius);
					setBackgroundImage("WasteLandBackgroundforLifeSim.png");
					scene.setFill(getBackgroundImage());
				}
				setCountDown(getCountDown() - 1);
			}

		});

		// Run for 5 frames and then set the counter back to five
		falloutSiren.setCycleCount(5);
		falloutSiren.getKeyFrames().add(frame);
		falloutSiren.play();
		this.disaster = false;
		setCountDown(5);

	}

	// Start of getters and setters for SimWorld
	public int getNumOfCarnivores() {
		return numOfCarnivores;
	}

	public void setNumOfCarnivores(int numOfCarnivores) {
		this.numOfCarnivores = numOfCarnivores;
	}

	public int getNumOfHerbivores() {
		return numOfHerbivores;
	}

	public void setNumOfHerbivores(int numOfHerbivores) {
		this.numOfHerbivores = numOfHerbivores;
	}

	public int getNumOfObstacles() {
		return numOfObstacles;
	}

	public void setNumOfObstacles(int numOfObstacles) {
		this.numOfObstacles = numOfObstacles;
	}

	public int getNumOfFood() {
		return numOfFood;
	}

	public void setNumOfFood(int numOfFood) {
		this.numOfFood = numOfFood;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public Timeline getCircleTimeLine() {
		return circleTimeLine;
	}

	public void setCircleTimeLine(Timeline circleTimeLine) {
		this.circleTimeLine = circleTimeLine;
	}

	public boolean isCanAddH() {
		return canAddH;
	}

	public void setCanAddH(boolean canAddH) {
		this.canAddH = canAddH;
	}

	public boolean isCanAddC() {
		return canAddC;
	}

	public void setCanAddC(boolean canAddC) {
		this.canAddC = canAddC;
	}

	public boolean isCanRemC() {
		return canRemC;
	}

	public void setCanRemC(boolean canRemC) {
		this.canRemC = canRemC;
	}

	public boolean isCanRemH() {
		return canRemH;
	}

	public void setCanRemH(boolean canRemH) {
		this.canRemH = canRemH;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public boolean isToggleDev() {
		return toggleDev;
	}

	public void setToggleDev(boolean toggleDev) {
		this.toggleDev = toggleDev;
	}

	public boolean isToggleBreed() {
		return toggleBreed;
	}

	public void setToggleBreed(boolean toggleBreed) {
		this.toggleBreed = toggleBreed;
	}

	public ArrayList<FoodSource> getFoodArray() {
		return foodArray;
	}

	public void setFoodArray(ArrayList<FoodSource> foodArray) {
		this.foodArray = foodArray;
	}

	public boolean isDisaster() {
		return disaster;
	}

	public void setDisaster(boolean disaster) {
		this.disaster = disaster;
	}

	public Circle getBlastRadius() {
		return blastRadius;
	}

	public void setBlastRadius(Circle blastRadius) {
		this.blastRadius = blastRadius;
	}

	public ImagePattern getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = new ImagePattern(new Image(backgroundImage));
	}

	public int getWorldWidth() {
		return worldWidth;
	}

	public void setWorldWidth(int worldWidth) {
		this.worldWidth = worldWidth;
	}

	public int getWorldHeight() {
		return worldHeight;
	}

	public void setWorldHeight(int worldHeight) {
		this.worldHeight = worldHeight;
	}

	public int getCountDown() {
		return countDown;
	}

	public void setCountDown(int countDown) {
		this.countDown = countDown;
	}

}
