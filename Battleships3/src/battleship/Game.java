package battleship;

// necessary imports

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ships.AircraftCarrier;
import ships.BattleShip;
import ships.Coordinate;
import ships.Destroyer;
import ships.PatrolBoat;
import ships.Ship;

/* 	this class creates the ComputerGrid and UserGrid and calls their respective functions to 
	add ships to the user and computer fleet. 
	It also provides the functionality to load the previously saved game
*/ 

public class Game extends Application {

    private static ComputerGrid computerGrid;
    private static UserGrid userGrid;
    private static boolean isComputerTurn;
    private static boolean allShipsCreated;
    private int gridDimension;
    private boolean isOldGame;
    private int totalAirCrafts;
    private int totalBattleShips;
    private int totalDestroyers;
    private int totalPatrolBoats;

    // the function which starts the game
    
    public void runGame(String[] args) {
        gridDimension = 10;
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    	// create the main window
        primaryStage.setTitle("BattleShip Game");
        primaryStage.setResizable(false);

        // get the Computer Display Monitors` max Resolution and set the size of window
        
        Rectangle2D primaryScreen = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreen.getMinX());
        primaryStage.setY(primaryScreen.getMinY());
        primaryStage.setWidth(primaryScreen.getWidth());
        primaryStage.setHeight(primaryScreen.getHeight());
        double padding = 20;
        double maxHeight = primaryScreen.getHeight() / 2 - (padding * 2) - 10;

        AnchorPane root = new AnchorPane();

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Commands");
        MenuItem createShipsMenuItem = new MenuItem("Create Ships");
        MenuItem saveMenuItem = new MenuItem("Save Game");
        fileMenu.getItems().addAll(createShipsMenuItem, saveMenuItem);
        menuBar.getMenus().add(fileMenu);

        root.getChildren().add(menuBar);
        AnchorPane.setTopAnchor(menuBar, 0.0);
        AnchorPane.setRightAnchor(menuBar, 0.0);
        AnchorPane.setLeftAnchor(menuBar, 0.0);
        
        // ask if user wants to load the previously saved game
        
        Alert newGameAlert = new Alert(AlertType.CONFIRMATION,
                "Do you want to load a previously saved game?",
                ButtonType.YES, ButtonType.NO);
        newGameAlert.setTitle("BattleShip Game");
        newGameAlert.setHeaderText("Load Game");
        newGameAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                isOldGame = true;
            }
        });

        // creating new game
        
        if (!isOldGame) {
        	
        	// ask for Grid Dimensions
        	
            TextInputDialog userInput = new TextInputDialog("10");
            userInput.setHeaderText("Enter N (Grid Dimension)");
            userInput.setTitle("Grid Dimensions");

            Optional<String> userInputString = userInput.showAndWait();

            while (!userInputString.isPresent()) {
                userInputString = userInput.showAndWait();
            }
            gridDimension = Integer.valueOf(userInputString.get());

            computerGrid = new ComputerGrid(gridDimension);
            userGrid = new UserGrid(gridDimension);

            // ask for the number of Aircraft Carriers in the game
            
            userInput.setTitle("Ships");
            userInput.getEditor().setText("1");
            userInput.setHeaderText("Enter number of AirCraft Carriers");
            userInputString = userInput.showAndWait();
            while (!userInputString.isPresent()) {
                userInputString = userInput.showAndWait();
            }
            totalAirCrafts = Integer.parseInt(userInputString.get());

            // ask for the number of BattleShips in the game
            
            userInput.getEditor().setText("2");
            userInput.setHeaderText("Enter number of BattleShips");
            userInputString = userInput.showAndWait();
            while (!userInputString.isPresent()) {
                userInputString = userInput.showAndWait();
            }

            totalBattleShips = Integer.parseInt(userInputString.get());

         // ask for the number of Destroyers in the game
            
            userInput.getEditor().setText("2");
            userInput.setHeaderText("Enter number of Destroyers");
            userInputString = userInput.showAndWait();
            while (!userInputString.isPresent()) {
                userInputString = userInput.showAndWait();
            }
            totalDestroyers = Integer.parseInt(userInputString.get());

         // ask for the number of PatrolBoats in the game
            
            userInput.getEditor().setText("3");
            userInput.setHeaderText("Enter number of Patrol Boats");
            userInputString = userInput.showAndWait();

            while (!userInputString.isPresent()) {
                userInputString = userInput.showAndWait();
            }
            totalPatrolBoats = Integer.parseInt(userInputString.get());

        } else {
        	
        	// if user wants to load the game then load it
            loadGame();
        }

        // create the computer grid
        
        computerGrid.setPrefSize(primaryScreen.getWidth(), maxHeight);
        root.getChildren().add(computerGrid);
        AnchorPane.setTopAnchor(computerGrid, padding + 10);
        AnchorPane.setRightAnchor(computerGrid, padding);
        AnchorPane.setLeftAnchor(computerGrid, padding);

        // create the user grid
        
        userGrid.setPrefSize(primaryScreen.getWidth(), maxHeight);
        root.getChildren().add(userGrid);
        AnchorPane.setBottomAnchor(userGrid, padding);
        AnchorPane.setRightAnchor(userGrid, padding);
        AnchorPane.setLeftAnchor(userGrid, padding);

        // if user wants, then save the game
        
        saveMenuItem.setOnAction(action -> {
            Alert saveGameAlert = new Alert(AlertType.CONFIRMATION);
            saveGameAlert.setTitle("BattleShip Game");
            saveGameAlert.setHeaderText("Game Save");
            String message = "";

            if (saveGame()) {
                message = "The Game was saved succesfully!!";
            } else {
                message = "The Game was not saved succesfully!!";
            }

            saveGameAlert.setContentText(message);
            saveGameAlert.show();

        });

        // when user clicks on Commands>Create Ships Menu then create a ship
        
        createShipsMenuItem.setOnAction(action -> {
            if (!allShipsCreated) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Battleship Game");
                alert.setHeaderText("Creating new ship");
                alert.setContentText("See output console. You will be creating ship from there.");
                alert.showAndWait();
                if (totalAirCrafts != 0) {
                    computerGrid.addShip(new AircraftCarrier());
                    System.out.println("New AirCraftCarrier :");
                    userGrid.addShip(new AircraftCarrier());
                    totalAirCrafts--;
                } else if (totalBattleShips != 0) {
                    computerGrid.addShip(new BattleShip());
                    System.out.println("New BattleShip :");
                    userGrid.addShip(new BattleShip());
                    totalBattleShips--;
                } else if (totalDestroyers != 0) {
                    computerGrid.addShip(new Destroyer());
                    System.out.println("New Destroyer :");
                    userGrid.addShip(new Destroyer());
                    totalDestroyers--;
                } else if (totalPatrolBoats != 0) {
                    computerGrid.addShip(new PatrolBoat());
                    System.out.println("New PatrolBoat :");
                    userGrid.addShip(new PatrolBoat());
                    totalPatrolBoats--;
                } else {
                    allShipsCreated = true;
                    alert.setHeaderText("Start Attacking");
                    alert.setContentText("Now your fleet is ready to attack opponent. Good Luck!!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Battleship Game");
                alert.setHeaderText("Start Attacking");
                alert.setContentText("No more ship to add to your fleet.");
                alert.showAndWait();
            }

        });

        // create the scene, set it to stage and show up the stage 
        Scene myScene = new Scene(root);
        primaryStage.setScene(myScene);
        primaryStage.show();

    }
    
    // tells whether all ships are created or not

    public static boolean doAllShipsCreated() {
        return allShipsCreated;
    }

    // returns the user grid
    public static UserGrid getUserGrid() {
        return userGrid;
    }

    // toggles the turn of each player 
    public static void toggleTurn() {
        isComputerTurn = !isComputerTurn;
    }

    // tells whether it is now computers turn or not
    public static boolean isComputerTurn() {
        return isComputerTurn;
    }

    // set the dimension of the grid
    public void setGameDimensions(int dimension) {
        gridDimension = dimension;
    }

    // returns the dimension of the grid
    public int getGameDimensions() {
        return gridDimension;
    }

    
    // this method saves the game to the file "GameState.dat"
    public boolean saveGame() {

    	// check whether the game was initialized completely or not
        if (!doAllShipsCreated()) {
            Alert userTurnAlert = new Alert(AlertType.INFORMATION);
            userTurnAlert.setTitle("Battleship Game");
            userTurnAlert.setHeaderText("Some ships still need to be added to your deck");
            userTurnAlert.setContentText("Please add all the ships to your fleet.");
            userTurnAlert.showAndWait();
        } else {

            try (BufferedWriter bfw = new BufferedWriter(new FileWriter("GameState.dat"));) {

            	// write the current state of computer grid and its ships
            	
                bfw.write(String.valueOf(gridDimension));
                bfw.newLine();
                Vector<Ship> shipsList = computerGrid.getAllShipsList();
                for (int i = 0; i < shipsList.size(); i++) {
                    bfw.write("C#");
                    Ship aShip = shipsList.get(i);
                    Vector<Coordinate> shipCoordinates = aShip.getAllShipsCoordinates();
                    for (int j = 0; j < shipCoordinates.size(); j++) {
                        Coordinate shipCoordinate = shipCoordinates.get(j);
                        bfw.write(String.format("%d,%d", shipCoordinate.getX(), shipCoordinate.getY()));
                        bfw.write("#");
                    }
                    bfw.newLine();
                }

                // write the current state of user grid and its ships
                
                shipsList = userGrid.getAllShipsList();
                for (int i = 0; i < shipsList.size(); i++) {
                    bfw.write("U#");
                    Ship aShip = shipsList.get(i);
                    Vector<Coordinate> shipCoordinates = aShip.getAllShipsCoordinates();
                    for (int j = 0; j < shipCoordinates.size(); j++) {
                        Coordinate shipCoordinate = shipCoordinates.get(j);
                        bfw.write(String.format("%d,%d", shipCoordinate.getX(), shipCoordinate.getY()));
                        bfw.write("#");
                    }
                    bfw.newLine();
                }
                return true;

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }
    
    // this method loads the game form the file "GameState.dat"

    public void loadGame() {
        try (BufferedReader bfr = new BufferedReader(new FileReader("GameState.dat"));) {

        	
        	// read the grid dimensions
            String line = bfr.readLine();
            setGameDimensions(Integer.parseInt(line));
            Vector<Ship> computerShipsList = new Vector<Ship>();
            Vector<Ship> userShipList = new Vector<Ship>();
            line = bfr.readLine();
            
            // read a line until there is none
            while (line != null && !line.isEmpty()) {
                
            	// if the line starts with "C" it contains the data about a computer ship extract it
            	if (line.startsWith("C")) {
                    String[] tokens = line.split("#");
                    Vector<Coordinate> shipCoordinates = new Vector<>();
                    int countCoordinates = 0;
                    for (int i = 1; i < tokens.length; i++) {
                        if (!tokens[i].isEmpty()) {
                            String points[] = tokens[i].split(",");
                            int x = Integer.valueOf(points[0]);
                            int y = Integer.valueOf(points[1]);
                            Coordinate shipCoordinate = new Coordinate(x, y);
                            shipCoordinates.add(shipCoordinate);
                            countCoordinates++;
                        }
                    }
                    Ship computerShip = new Ship(countCoordinates);
                    computerShip.setShipCoordinates(shipCoordinates);
                    computerShipsList.add(computerShip);
                } 
            	
            	// if the line starts with "U" it contains the data about a user ship extract it
            	else if (line.startsWith("U")) {
                    String[] tokens = line.split("#");
                    Vector<Coordinate> shipCoordinates = new Vector<>();
                    int countCoordinates = 0;
                    for (int i = 1; i < tokens.length; i++) {
                        if (!tokens[i].isEmpty()) {
                            String points[] = tokens[i].split(",");
                            int x = Integer.valueOf(points[0]);
                            int y = Integer.valueOf(points[1]);
                            Coordinate shipCoordinate = new Coordinate(x, y);
                            shipCoordinates.add(shipCoordinate);
                            countCoordinates++;
                        }
                    }
                    Ship userShip = new Ship(countCoordinates);
                    userShip.setShipCoordinates(shipCoordinates);
                    userShipList.add(userShip);
                }
                line = bfr.readLine();
            }

            // create the computer and user grids from the data loaded form the file 
            
            computerGrid = new ComputerGrid(getGameDimensions());
            userGrid = new UserGrid(getGameDimensions());
            
            // set the ships of the computer fleet and user fleet respectively
            
            computerGrid.setShips(computerShipsList, true);
            userGrid.setShips(userShipList, true);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
