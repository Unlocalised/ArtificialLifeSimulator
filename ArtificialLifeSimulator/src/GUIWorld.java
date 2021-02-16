import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Handles the display of all controls for the simulation Each control has an
 * associated event handler which utilises specific button/menu commands
 * Interacts with the SimWorld by calling constructors on a SimWorld Object
 * 
 * @see SimWorld functions in source for relationship between action and button
 *      calls
 */
public class GUIWorld extends Application {
	private SimWorld w;
	private String currentFileName;

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Set up the top menu and the menu bar
		Menu fileMenu = new Menu("File");
		Menu viewMenu = new Menu("View");
		Menu editMenu = new Menu("Edit");
		Menu simulationMenu = new Menu("Simulation");
		Menu helpMenu = new Menu("Help");
		MenuBar myMenuBar = new MenuBar();
		myMenuBar.getMenus().addAll(fileMenu, viewMenu, editMenu, simulationMenu, helpMenu);

		// Add items to the file menu which will hold the functions
		MenuItem menuNewConfig = new MenuItem("New Config");
		MenuItem menuLoadConfig = new MenuItem("Load Config");
		MenuItem menuSaveConfig = new MenuItem("Save Config");
		MenuItem menuSaveAsConfig = new MenuItem("Save Config As");
		MenuItem menuExit = new MenuItem("Exit Simulation");
		fileMenu.getItems().addAll(menuNewConfig, menuLoadConfig, menuSaveConfig, menuSaveAsConfig, menuExit);

		// Add items to the view menu which will hold the functions
		MenuItem menuDisplayConfig = new MenuItem("Display Config");
		MenuItem menuEditConfig = new MenuItem("Edit Config");
		MenuItem menuDisplayLifeForm = new MenuItem("Display LifeForms");
		MenuItem menuDisplayMapInfo = new MenuItem("Display Map Info");
		viewMenu.getItems().addAll(menuDisplayConfig, menuEditConfig, menuDisplayLifeForm, menuDisplayMapInfo);

		// Add items to the edit menu which will hold the functions
		MenuItem menuModifyCLifeForm = new MenuItem("Modify Carnivore LifeForm");
		MenuItem menuModifyHLifeForm = new MenuItem("Modify Herbivore LifeForm");
		MenuItem menuRemoveHerbivore = new MenuItem("Remove Herbivore");
		MenuItem menuRemoveCarnivore = new MenuItem("Remove Carnivore");
		MenuItem menuAddHerbivore = new MenuItem("Add Herbivore");
		MenuItem menuAddCarnivore = new MenuItem("Add Carnivore");
		editMenu.getItems().addAll(menuModifyCLifeForm, menuModifyHLifeForm, menuAddCarnivore, menuRemoveCarnivore,
				menuAddHerbivore, menuRemoveHerbivore);

		// Add items to the simulation menu which will hold the functions
		MenuItem menuRunSim = new MenuItem("Run Simulation");
		MenuItem menuPauseSim = new MenuItem("Pause Simulation");
		MenuItem menuResetSim = new MenuItem("Reset Simulation");
		MenuItem menuCycleByCycleSim = new MenuItem("Cycle-By-Cycle Simulation");
		simulationMenu.getItems().addAll(menuRunSim, menuPauseSim, menuResetSim, menuCycleByCycleSim);

		// Add items to the help menu which will hold the functions
		MenuItem menuAppInfo = new MenuItem("Application Information");
		MenuItem menuAuthorInfo = new MenuItem("Author Information");
		helpMenu.getItems().addAll(menuAppInfo, menuAuthorInfo);

		// Creates a new toolbar to be added to the scene
		// Sets up tooltips and button text properties
		ToolBar toolBar = new ToolBar();
		toolBar.setOrientation(Orientation.VERTICAL);
		Button btnIncreaseAnimationRate = new Button("->");
		Button btnDecreaseAnimationRate = new Button("<-");
		Button btnAddCarnivore = new Button("+C");
		Button btnAddHerbivore = new Button("+H");
		Button btnTriggerNaturalDisaster = new Button("^~");
		RadioButton rbtnToggleBreeding = new RadioButton("<3");
		RadioButton rbtnToggleDevMode = new RadioButton("dev");
		btnAddCarnivore.setTooltip(new Tooltip("Add a carnivore"));
		btnAddHerbivore.setTooltip(new Tooltip("Add a herbivore"));
		btnIncreaseAnimationRate.setTooltip(new Tooltip("Increase Animation Speed"));
		btnDecreaseAnimationRate.setTooltip(new Tooltip("Decrease Animation Speed"));
		rbtnToggleBreeding.setTooltip(new Tooltip("Toggle Breeding"));
		rbtnToggleDevMode.setTooltip(new Tooltip("Toggle developer mode"));
		btnTriggerNaturalDisaster.setTooltip(new Tooltip("Trigger a natural disaster"));
		toolBar.getItems().addAll(btnAddCarnivore, btnAddHerbivore, btnTriggerNaturalDisaster, btnIncreaseAnimationRate,
				btnDecreaseAnimationRate, new Separator(), rbtnToggleBreeding, rbtnToggleDevMode);

		// Sets the world for when the program is opened to the last used
		// configuration
		FileOperation fileFunction = new FileOperation();
		setW(fileFunction.loadDefaultConfig());

		// Sets up VBox for allignment of controls on form and creates a
		// simulation
		VBox hpane = new VBox();
		VBox newpane = new VBox();
		hpane.setPadding(new Insets(25, 100, 100, 0));
		hpane.getChildren().addAll(toolBar);
		newpane.setAlignment(Pos.TOP_LEFT);
		newpane.getChildren().addAll(myMenuBar);
		w.createWorld();
		w.runWorldCycles(primaryStage, newpane, hpane);

		menuNewConfig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent a) {
				// GUI Stage and data setup for the world generation
				Stage enterData = new Stage();
				enterData.setTitle("Enter configuration data");
				Button submitConfigData = new Button("Sumbit Data");
				Label lblFileName = new Label("File Name:");
				Label lblWorldHeight = new Label("World Height(Max 1000):");
				Label lblWorldWidth = new Label("World Width(Max 1800):");
				Label lblNumOfObstacles = new Label("Number Of Obstacles(Max 100):");
				Label lblNumOfHerbivores = new Label("Number Of Herbivores(Max 100):");
				Label lblNumOfCarnivores = new Label("Number Of Carnivores(Max 100):");
				Label lblNumOfFood = new Label("Number Of Food(Max 100):");

				TextField txtFile = new TextField();
				TextField txtHeight = new TextField();
				TextField txtWidth = new TextField();
				TextField txtNumOfObstacles = new TextField();
				TextField txtNumOfHerbivores = new TextField();
				TextField txtNumOfCarnivores = new TextField();
				TextField txtNumOfFood = new TextField();
				VBox layout = new VBox(0);
				layout.getChildren().addAll(lblFileName, txtFile, lblWorldHeight, txtHeight, lblWorldWidth, txtWidth,
						lblNumOfObstacles, txtNumOfObstacles, lblNumOfHerbivores, txtNumOfHerbivores,
						lblNumOfCarnivores, txtNumOfCarnivores, lblNumOfFood, txtNumOfFood, submitConfigData);
				Scene dataScene = new Scene(layout, 300, 320);
				enterData.setScene(dataScene);
				enterData.show();

				submitConfigData.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						txtFile.setText(txtFile.getText().replaceAll("[^a-zA-Z0-9.-]", "_"));
						// If any of the fields are empty dont allow acces
						if (txtFile.getText() != null && txtHeight.getText() != null && txtWidth.getText() != null
								&& txtNumOfHerbivores.getText() != null && txtNumOfCarnivores.getText() != null
								&& txtNumOfObstacles.getText() != null && txtNumOfFood.getText() != null) {

							if (isNumeric(txtHeight.getText(), txtWidth.getText(), txtNumOfObstacles.getText(),
									txtNumOfHerbivores.getText(), txtNumOfCarnivores.getText(),
									txtNumOfFood.getText())) {
								// If all of the textboxes contain numerical
								// answers, proceed to create the new config

								int param1 = Integer.parseInt(txtWidth.getText());
								int param2 = Integer.parseInt(txtHeight.getText());
								int param3 = Integer.parseInt(txtNumOfCarnivores.getText());
								int param4 = Integer.parseInt(txtNumOfHerbivores.getText());
								int param5 = Integer.parseInt(txtNumOfObstacles.getText());
								int param6 = Integer.parseInt(txtNumOfFood.getText());

								// Data constraints on the entry parameters
								if (param1 > 1800) {
									param1 = 1800;
								} else if (param1 < 300) {
									param1 = 300;
								}
								;
								if (param2 > 1000) {
									param2 = 1000;
								} else if (param2 < 300) {
									param2 = 300;
								}
								;
								if (param3 > 100) {
									param3 = 100;
								} else if (param3 < 0) {
									param3 = 0;
								}
								;
								if (param4 > 100) {
									param4 = 100;
								} else if (param4 < 0) {
									param4 = 0;
								}
								;
								if (param5 > 100) {
									param5 = 100;
								} else if (param5 < 0) {
									param5 = 0;
								}
								;
								if (param6 > 100) {
									param6 = 100;
								} else if (param6 < 0) {
									param6 = 0;
								}
								;
								// Stop the old simulation from running and set
								// the currentWorld to the newly produced one
								setCurrentFileName(txtFile.getText());
								w.getCircleTimeLine().stop();
								setW(fileFunction.setNewConfiguration(param1, param2, param3, param4, param5, param6));
								// Run the new simulation
								w.createWorld();
								w.runWorldCycles(primaryStage, newpane, hpane);
								enterData.close();
							}
						}
					}

				});
			}

		});

		menuLoadConfig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent a) {
				// Get the current world in use and load into temporary variable
				SimWorld tempWorld = getW();

				// Set the current world to the selected file from the
				// filechooser
				setW(fileFunction.loadConfig());
				// if the file has been sucessfully loaded, create a new world
				// and run it
				if (fileFunction.isHasSucceeded()) {
					w.createWorld();
					w.runWorldCycles(primaryStage, newpane, hpane);

				} else {
					// If unsuccessful, stick with the old world
					setW(tempWorld);
				}
			}
		});

		menuRunSim.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent h) {
				// Run the current loaded world's timeline
				w.getCircleTimeLine().play();
			}

		});

		menuPauseSim.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Pause the current loaded world's timeline
				w.getCircleTimeLine().pause();
			}

		});

		menuCycleByCycleSim.setOnAction(new EventHandler<ActionEvent>() {
			boolean toggle = false;

			@Override
			public void handle(ActionEvent arg0) {
				// Dependant on when the button is clicked...
				if (toggle == false) {
					toggle = true;
					// Slow animation to almost frame by frame
					w.getCircleTimeLine().setRate(0.1);
				} else {
					// Set animation to default rate
					w.getCircleTimeLine().setRate(2.5);
					toggle = false;
				}

			}

		});

		menuAddHerbivore.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Enables adding a herbivore for one frame
				w.setCanAddH(true);
			}

		});

		menuAddCarnivore.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Enables adding a carnivore for one frame
				w.setCanAddC(true);
			}

		});

		menuRemoveCarnivore.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Allows removal of the latest carnivore
				w.setCanRemC(true);
			}

		});

		menuRemoveHerbivore.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Allows removal of the latest herbivore
				w.setCanRemH(true);
			}

		});

		menuResetSim.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Stop the current timeline
				w.getCircleTimeLine().stop();

				// Set the world parameters back to the last loaded config
				setW(fileFunction.loadDefaultConfig());

				// Run the simulation
				w.createWorld();
				w.runWorldCycles(primaryStage, newpane, hpane);
			}

		});

		menuSaveConfig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Call the file operation save
				fileFunction.saveCurrentWorld(getCurrentFileName(), w);
			}

		});

		menuSaveAsConfig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// ask user for a new name for the file, then save as normal
				// GUI Stage and data setup for the save as function
				Stage enterData = new Stage();
				enterData.setTitle("Enter file name:");
				Button submitFileData = new Button("Save");
				Label lblFileName = new Label("File Name:");

				TextField txtFile = new TextField();

				VBox layout = new VBox(0);
				layout.getChildren().addAll(lblFileName, txtFile, submitFileData);
				Scene dataScene = new Scene(layout, 300, 70);
				enterData.setScene(dataScene);
				enterData.show();

				submitFileData.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						// Saves the current world
						fileFunction.saveCurrentWorld(txtFile.getText(), w);
						enterData.close();
					}

				});
			}

		});

		menuExit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// create dialog to ask if they want to save the world
				Stage areYouSure = new Stage();
				areYouSure.setTitle("Before you go");
				Text text = new Text();
				text.setText("Do you want to save the world before you exit?");

				Button btn = new Button();
				Button btn2 = new Button();
				btn.setText("Yes");
				btn2.setText("No");
				btn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						// If yes, save the world then close
						fileFunction.saveCurrentWorld(w.getWorldName(), w);
						System.exit(0);
					}
				});

				btn2.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						// Otherwise, just close the program
						System.exit(0);
					}

				});

				VBox pane = new VBox();
				pane.setAlignment(Pos.TOP_CENTER);
				pane.setPadding(new Insets(10, 25, 25, 25));
				pane.getChildren().add(text);
				pane.getChildren().add(btn);
				pane.getChildren().add(btn2);
				areYouSure.setScene(new Scene(pane, 270, 80));
				areYouSure.show();

			}

		});

		menuEditConfig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Set up GUI menu for editing config data
				Stage enterData = new Stage();
				enterData.setTitle("Change configuration data");
				Button submitConfigData = new Button("Sumbit Data");
				Label lblFileName = new Label("File Name:");
				Label lblWorldHeight = new Label("World Height(Max 1000):");
				Label lblWorldWidth = new Label("World Width(Max 1800):");
				Label lblNumOfObstacles = new Label("Number Of Obstacles(Max 100):");
				Label lblNumOfHerbivores = new Label("Number Of Herbivores(Max 100):");
				Label lblNumOfCarnivores = new Label("Number Of Carnivores(Max 100):");
				Label lblNumOfFood = new Label("Number Of Food(Max 100):");

				TextField txtFile = new TextField();
				TextField txtHeight = new TextField();
				TextField txtWidth = new TextField();
				TextField txtNumOfObstacles = new TextField();
				TextField txtNumOfHerbivores = new TextField();
				TextField txtNumOfCarnivores = new TextField();
				TextField txtNumOfFood = new TextField();
				VBox layout = new VBox(0);
				layout.getChildren().addAll(lblFileName, txtFile, lblWorldHeight, txtHeight, lblWorldWidth, txtWidth,
						lblNumOfObstacles, txtNumOfObstacles, lblNumOfHerbivores, txtNumOfHerbivores,
						lblNumOfCarnivores, txtNumOfCarnivores, lblNumOfFood, txtNumOfFood, submitConfigData);
				Scene dataScene = new Scene(layout, 300, 320);
				enterData.setScene(dataScene);
				enterData.show();

				submitConfigData.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						// As long as all fields are not empty and all numeric
						// Replace all illegal file characters with an
						// underscore
						txtFile.setText(txtFile.getText().replaceAll("[^a-zA-Z0-9.-]", "_"));
						if (txtFile.getText() != null && txtHeight.getText() != null && txtWidth.getText() != null
								&& txtNumOfHerbivores.getText() != null && txtNumOfCarnivores.getText() != null
								&& txtNumOfObstacles.getText() != null && txtNumOfFood.getText() != null) {
							System.out.println("Fields Empty, Please Re Enter Data");
							if (isNumeric(txtHeight.getText(), txtWidth.getText(), txtNumOfObstacles.getText(),
									txtNumOfHerbivores.getText(), txtNumOfCarnivores.getText(),
									txtNumOfFood.getText())) {
								// Edit the world with new parameters
								int param1 = Integer.parseInt(txtHeight.getText());
								int param2 = Integer.parseInt(txtWidth.getText());
								int param3 = Integer.parseInt(txtNumOfCarnivores.getText());
								int param4 = Integer.parseInt(txtNumOfHerbivores.getText());
								int param5 = Integer.parseInt(txtNumOfObstacles.getText());
								int param6 = Integer.parseInt(txtNumOfFood.getText());

								// Data entry constraints
								if (param1 > 1000) {
									param1 = 1000;
								} else if (param1 < 300) {
									param1 = 300;
								}
								;
								if (param2 > 1800) {
									param2 = 1800;
								} else if (param2 < 300) {
									param2 = 300;
								}
								;
								if (param3 > 100) {
									param3 = 100;
								} else if (param3 < 0) {
									param3 = 0;
								}
								;
								if (param4 > 100) {
									param4 = 100;
								} else if (param4 < 0) {
									param4 = 0;
								}
								;
								if (param5 > 100) {
									param5 = 100;
								} else if (param5 < 0) {
									param5 = 0;
								}
								;
								if (param6 > 100) {
									param6 = 100;
								} else if (param6 < 0) {
									param6 = 0;
								}

								w.getCircleTimeLine().stop();
								setCurrentFileName(txtFile.getText());
								// Set the current world to the one just editedm
								// and run the simulation
								setW(fileFunction.setNewConfiguration(param1, param2, param3, param4, param5, param6));
								w.createWorld();
								w.runWorldCycles(primaryStage, newpane, hpane);
								enterData.close();
							}
						}
					}

				});
			}

		});

		btnTriggerNaturalDisaster.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				// Runs the function to trigger a disaster
				w.setDisaster(true);
			}
		});

		menuDisplayConfig.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Creates GUI and uses getters to get data for the world
				Stage displayStage = new Stage();
				Label lbl2 = new Label("World Width:" + w.getWorldWidth());
				Label lbl3 = new Label("World Height:" + w.getWorldHeight());
				Label lbl4 = new Label("Number of Carnivores:" + w.getNumOfCarnivores());
				Label lbl5 = new Label("Number of Herbivores:" + w.getNumOfHerbivores());
				Label lbl6 = new Label("Number of Obstacles:" + w.getNumOfObstacles());
				Label lbl7 = new Label("Number of Food:" + w.getNumOfFood());

				VBox pane = new VBox();
				pane.setAlignment(Pos.CENTER_LEFT);
				pane.setPadding(new Insets(25, 25, 25, 25));
				pane.getChildren().addAll(lbl2, lbl3, lbl4, lbl5, lbl6, lbl7);

				displayStage.setScene(new Scene(pane));
				displayStage.sizeToScene();

				// makes the scene slightly translucent so that the view of the
				// simulation is not entirely obscured
				displayStage.setOpacity(0.9);
				displayStage.show();
			}

		});

		menuDisplayMapInfo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Dispays the information specific only to the map
				Stage displayStage = new Stage();
				Label lbl2 = new Label("World Width:" + w.getWorldWidth());
				Label lbl3 = new Label("World Height:" + w.getWorldHeight());

				VBox pane = new VBox();
				pane.setAlignment(Pos.CENTER_LEFT);
				pane.setPadding(new Insets(25, 25, 25, 25));
				pane.getChildren().addAll(lbl2, lbl3);

				displayStage.setScene(new Scene(pane));
				displayStage.sizeToScene();

				displayStage.setOpacity(0.9);
				displayStage.show();
			}

		});

		menuDisplayLifeForm.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Creates a string Arraylist to hold data
				ArrayList<String> lifeFormData = w.getAllLifeFormData();

				// Defines a new text area with a row count the same as number
				// of lifeforms
				// as passed in by the getAllLifeFormData() function
				TextArea textArea = new TextArea();
				textArea.setPrefRowCount(lifeFormData.size());
				// For each row in the arraylist append text to the text area
				for (int i = 0; i < lifeFormData.size(); i++) {
					textArea.appendText(lifeFormData.get(i) + "\n");

				}
				// show the data in a new stage
				Stage lifeFormStats = new Stage();
				lifeFormStats.setScene(new Scene(textArea));
				lifeFormStats.show();
			}

		});

		btnAddCarnivore.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Allows the addition of carnivores to the world
				w.setCanAddC(true);
			}

		});

		btnAddHerbivore.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Allows the addition of herbivores to the world
				w.setCanAddH(true);
			}

		});

		btnIncreaseAnimationRate.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Increases the rate of the frames in the simulation
				w.getCircleTimeLine().setRate(w.getCircleTimeLine().getRate() + 0.1);
			}

		});

		btnDecreaseAnimationRate.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Decreases the reate of the frames in the simulation
				w.getCircleTimeLine().setRate(w.getCircleTimeLine().getRate() - 0.1);
			}

		});

		rbtnToggleDevMode.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// If the radiobutton is selected, call a toggle to the world to
				// show sense radius
				if (rbtnToggleDevMode.isSelected()) {
					w.setToggleDev(true);
				} else if (!rbtnToggleDevMode.isSelected()) {
					w.setToggleDev(false);
				}

			}

		});

		rbtnToggleBreeding.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// If the toggle breeding radiobutton is selected, allow animals
				// to breed if their energy is high enough
				if (rbtnToggleBreeding.isSelected()) {
					w.setToggleBreed(true);
				} else if (!rbtnToggleBreeding.isSelected()) {
					w.setToggleBreed(false);
				}
			}

		});

		menuModifyCLifeForm.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Allow the increase of energy and intelligence level of
				// carnivores
				w.editCarnivores();
			}

		});

		menuModifyHLifeForm.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Allows the increase of energy and intelligence of herbivores
				w.editHerbivores();
			}

		});

		btnTriggerNaturalDisaster.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// Allows a disaster in the world to occur
				w.setDisaster(true);

			}

		});
		// Displays the app infomation
		menuAppInfo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ShowDebugMessage("Appname: Artificial Life Simulator\nVersion: 1.0.0");
			}

		});
		// Displaus the author information
		menuAuthorInfo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ShowDebugMessage("AuthorName: Nathan Brown\nAuthorID: yq009086\nGraphics/ArtWork: Sarah Brown");
			}

		});

	}

	/**
	 * Takes all given world parameters and trys to parse to an integer
	 * 
	 * @param param1-param6
	 *            all world data(for example height, width, number of
	 *            obstacles).
	 * @return true if all parameters can be converted to integers, false
	 *         otherwise
	 */

	public boolean isNumeric(String param1, String param2, String param3, String param4, String param5, String param6) {
		try
		// Try and convert all given parameters into integers
		{
			Integer.parseInt(param1);
			Integer.parseInt(param2);
			Integer.parseInt(param3);
			Integer.parseInt(param4);
			Integer.parseInt(param5);
			Integer.parseInt(param6);
		} catch (NumberFormatException exception) {
			// If you fail, notify the caller of the function
			return false;
		}
		// Notify the function caller that all passed arguments are integers
		return true;
	}

	/**
	 * Handles all of the pop up and notification text given for any needed pop
	 * up windows
	 * 
	 * @param Debug
	 *            string to display in a window with the specified format
	 */
	public void ShowDebugMessage(String Debug) {
		Stage DebugMessage = new Stage();
		DebugMessage.initModality(Modality.WINDOW_MODAL);
		DebugMessage.setScene(new Scene(
				VBoxBuilder.create().children(new Text(Debug)).alignment(Pos.CENTER).padding(new Insets(5)).build()));
		DebugMessage.show();
	}

	public static void main(String[] args) {
		// Launches the main application
		launch();
	}

	// Getters and setters for world filename and current world

	public SimWorld getW() {
		return w;
	}

	public void setW(SimWorld w) {
		this.w = w;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}

}
