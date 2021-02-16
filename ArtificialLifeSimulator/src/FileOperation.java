import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Controls all functions concerning files Uses the .Csv file format to store
 * and fetch data Returns SimWorld Objects with varying parameters Controlled by
 * the GUIWorld class
 * 
 * @see GUIWorld class for reference to applying these functions.
 */
public class FileOperation {

	private boolean hasSucceeded;

	/**
	 * Creates a world based on the users specifications
	 * 
	 * @param param1-param6
	 *            are the holders for the data of number of each item, world
	 *            width and world height
	 * 
	 * @return a new world object based on parameters given
	 */
	// Returns a SimWorld object based on parameters given by the user
	public SimWorld setNewConfiguration(int param1, int param2, int param3, int param4, int param5, int param6) {
		return new SimWorld(param1, param2, param3, param4, param5, param6);
	}

	/**
	 * Uses a filechooser to navigate to a .csv file to fetch data for a world.
	 * Uses an extension filter so only .Csv files can be chosen Then saves the
	 * world to the last loaded config file
	 * 
	 * @return loadedWorld returns a world object with the parameters lifted
	 *         from the csv file chosen by the user.
	 * @return null if the user cancels the choose file option
	 */
	public SimWorld loadConfig() {
		// Create a new SimWorld object as a base
		SimWorld loadedWorld = new SimWorld(10, 10, 1, 1, 1, 1);

		// Open a filechooser and get the user to make a selection
		Stage fChoose = new Stage();
		Scene fScene = new Scene(new VBox(), 300, 300);
		fChoose.setScene(fScene);
		FileChooser fileChoice = new FileChooser();
		fileChoice.setTitle("Open CSV File");
		// Allow only .Csv files to be selected by the user
		fileChoice.getExtensionFilters().add(new ExtensionFilter("Comma Seperated Values", "*.csv"));
		File selectedFile = fileChoice.showOpenDialog(fChoose);
		// if a file has been selected
		if (selectedFile != null) {
			Scanner csvReader;
			try {
				// Set the appropriate values for the world
				csvReader = new Scanner(selectedFile);
				csvReader.useDelimiter(",");
				loadedWorld.setWorldHeight(Integer.parseInt(csvReader.next()));
				loadedWorld.setWorldWidth(Integer.parseInt(csvReader.next()));
				loadedWorld.setNumOfCarnivores(Integer.parseInt(csvReader.next()));
				loadedWorld.setNumOfHerbivores(Integer.parseInt(csvReader.next()));
				loadedWorld.setNumOfObstacles(Integer.parseInt(csvReader.next()));
				loadedWorld.setNumOfFood(Integer.parseInt(csvReader.next()));
				loadedWorld.setWorldName(fileChoice.getInitialFileName());
				csvReader.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Then try writing the writtenValues to the last loaded config file
			try {
				File newConfig = new File("lastLoadedConfig.csv");
				FileWriter writer = new FileWriter(newConfig);
				PrintWriter dataStream = new PrintWriter(writer);

				// write data to Csv file
				dataStream.print(loadedWorld.getWorldWidth() + ",");
				dataStream.print(loadedWorld.getWorldHeight() + ",");
				dataStream.print(loadedWorld.getNumOfCarnivores() + ",");
				dataStream.print(loadedWorld.getNumOfHerbivores() + ",");
				dataStream.print(loadedWorld.getNumOfObstacles() + ",");
				dataStream.print(loadedWorld.getNumOfFood() + ",");
				dataStream.close();
				System.out.println("World Saved Successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// If successful, return the world created by the user
			this.setHasSucceeded(true);
			return loadedWorld;
		} else {
			// return null world in order to show an error state
			this.setHasSucceeded(false);
			return null;
		}

	}

	// Loads the .Csv file named lastLoadedConfig for use whenever the program
	// is opened
	/**
	 * Loads the .Csv file named lastLoadedConfig for use whenever the program
	 * is opened.
	 * 
	 * @return loadedWorld returns the world currently in the lastLoadedConfig
	 *         file.
	 */
	public SimWorld loadDefaultConfig() {
		String fileName = "lastLoadedConfig";
		// Create a temporary world
		SimWorld loadedWorld = new SimWorld(300, 300, 0, 0, 0, 0);
		BufferedReader r;
		try {
			r = new BufferedReader(new FileReader(fileName + ".csv"));
			String lines = null;
			Scanner s = null;
			// Read the file and set the temporary worlds values to the read
			// values
			try {
				while ((lines = r.readLine()) != null) {
					s = new Scanner(lines);
					s.useDelimiter(",");
					loadedWorld.setWorldWidth(Integer.parseInt(s.next()));
					loadedWorld.setWorldHeight(Integer.parseInt(s.next()));
					loadedWorld.setNumOfCarnivores(Integer.parseInt(s.next()));
					loadedWorld.setNumOfHerbivores(Integer.parseInt(s.next()));
					loadedWorld.setNumOfObstacles(Integer.parseInt(s.next()));
					loadedWorld.setNumOfFood(Integer.parseInt(s.next()));
					System.out.println("Configuration Loaded Successfully");
					s.close();
					// return the new world with the specified parameters
					return loadedWorld;
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// else return the default world with no entities
		return loadedWorld;
	}

	/**
	 * Saves the world currently being simulated as its filename specified by
	 * the user at world create
	 * 
	 * @param fileName
	 *            String that stores the filename the user chose at the
	 *            beginning
	 * @param worldToSave
	 *            holds the world that needs its data extracted and written to a
	 *            .Csv file
	 */
	// saves the current world in use via the file name given at the start
	public void saveCurrentWorld(String fileName, SimWorld worldToSave) {
		try {
			File newConfig = new File(fileName + ".csv");
			FileWriter writer = new FileWriter(newConfig);
			PrintWriter dataStream = new PrintWriter(writer);

			// write data to the .Csv file created
			dataStream.print(worldToSave.getWorldWidth() + ",");
			dataStream.print(worldToSave.getWorldHeight() + ",");
			dataStream.print(worldToSave.getNumOfCarnivores() + ",");
			dataStream.print(worldToSave.getNumOfHerbivores() + ",");
			dataStream.print(worldToSave.getNumOfObstacles() + ",");
			dataStream.print(worldToSave.getNumOfFood() + ",");
			worldToSave.setWorldName(fileName);
			dataStream.close();

			// update the lastLoadedConfig file when the world is saved
			File updateCurrentConfig = new File("lastLoadedConfig.csv");
			writer = new FileWriter(updateCurrentConfig);
			dataStream = new PrintWriter(writer);

			// write all data to the lastLoadedConfig file so this world is now
			// the current one
			dataStream.print(worldToSave.getWorldWidth() + ",");
			dataStream.print(worldToSave.getWorldHeight() + ",");
			dataStream.print(worldToSave.getNumOfCarnivores() + ",");
			dataStream.print(worldToSave.getNumOfHerbivores() + ",");
			dataStream.print(worldToSave.getNumOfObstacles() + ",");
			dataStream.print(worldToSave.getNumOfFood() + ",");

			dataStream.close();
			System.out.println("World Saved Successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Getters and setters for the File operations
	public boolean isHasSucceeded() {
		return hasSucceeded;
	}

	public void setHasSucceeded(boolean hasSucceeded) {
		this.hasSucceeded = hasSucceeded;
	}

}
