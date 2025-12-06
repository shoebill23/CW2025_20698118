# Tetris JavaFX — COMP2042

## GitHub Repository Link
- https://github.com/shoebill23/CW2025_20698118

## Compilation Instructions

- Prerequisites
  - Install JDK 23 and ensure `JAVA_HOME` points to it
  - Maven Wrapper is provided; no separate Maven install required


- Build and Run
  - From the project root:
    - Clean and run the JavaFX application
      ``` 
      ./mvnw clean javafx:run
      ```
    - On Windows (PowerShell or CMD):
        ```
        mvn javafx:run
        ```


## Implemented and Working Properly

1. Level System and Drop Speed
   - Levels increase every 10 lines cleared
   - Drop speed uses the provided formula which increases the speed exponentially and updates dynamically
   - Formula:
      ```java
        double seconds = 0.75 * Math.pow(0.8 - ((level - 1) * 0.010), (level - 1));
      ```


2. Music
   - Start menu loops `Pokemon_Center.mp3`
   - Classic mode plays `Littleroot_Town.mp3`
   - Time Attack plays `Battle!_Trainer.mp3`
   - Pausing reduces music volume to 50%


3. Time Attack Mode 
   - 60-second countdown with on-screen timer
   - Game ends when the player reaches the maximum allowed height (20 Blocks) or when the timer runs out
   - High score for this game mode is tracked separately to the Classic game mode.


4. 7‑Bag Brick Randomization
   - Fair distribution of pieces and gives the player a greater sense of randomness in the gameplay. 
   - Creates a "bag" of a random permutation of the 7 available bricks
   - Reduces the case where bricks keep repeating for long periods of time
   - When the "bag" is empty it is refilled
   - When the game is restarted the "bag" is reset to avoid returning `NULL` or junk values


5. Next Brick Preview
   - Displays the next brick to be dropped in the `Next Brick` panel
   - Updates dynamically as the bag is refilled


6. Hold Functionality
   - Allows players to hold a brick while dropping another
   - At first, when the game starts it does not hold any bricks then stores the currently active brick when the player desires
   - Every hold after that the game swaps the "held" brick with the current brick
   - Shows the currently "held" brick in a preview panel
   - Hold Logic:
      ```java
          if (holdBrick == null) {
              holdBrick = currentBrick;
              Brick nextBrick = brickGenerator.getBrick();
              brickRotator.setBrick(nextBrick);
              currentOffset = new Point(BRICK_START_X, BRICK_START_Y);
              canHold = false;
          } else {
              Brick temp = holdBrick;
              holdBrick = currentBrick;
              brickRotator.setBrick(temp);
              currentOffset = new Point(BRICK_START_X, BRICK_START_Y);
              canHold = false;
          }
      ```


7. Start Screen
   - Displays the game title and start buttons for Classic and Time Attack modes
   - Loops `Pokemon_Center.mp3` upon start
   - Transitions to the desired scenes based on the buttons pressed (Classic/Time Attack game mode)


8. Hard Drop Functionality
   - Allows players to instantly drop the active brick to the lowest possible position on the playfield
   - Calculates the lowest possible y-value the brick can be dropped to and merges the brick to the background at said y-value
   - Calculating the lowest possible y-value:
      ```java
      Point p = new Point(currentOffset);
            while (true) {
                Point nextP = new Point(p);
                nextP.translate(0, MOVE_DOWN_DELTA);
                boolean conflict = MatrixOperations.intersect(currentMatrix, currentShape, (int) nextP.getX(), (int) nextP.getY());
                if (conflict) {
                    break;
                }
                p = nextP;
            }
      ```
   - Returns the number of rows the brick dropped to add to the score.


9. Menus and Font
   - Pause, Game Over, and Controls menus implemented to be displayed when the corresponding events occur
   - Implement centralized styling for a standardized look for all menus
   - Labels and UI use the bundled pixel font (`janinosjuosta.ttf`) throughout the game

## Features Not Implemented


1. Centering the Bricks within the preview panels
   - Was supposed to ensure the preview bricks were centered in the preview panels
   - The method I used was to compute the smallest box that contains the preview piece, then shift it so that box sits in the middle of the preview grid but ended up not working as expected.
   - The bricks were instead aligned to the top-left corner of the preview panels
   - Code snippet of the buggy code: 
      ```java
          int[] offset = calculateCenteringOffset(nextBrickData, PREVIEW_GRID_SIZE);
          for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
              for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                  setRectangleData(0, nextBrickRectangles[i][j]);
              }
          }
          for (int i = 0; i < nextBrickData.length; i++) {
              for (int j = 0; j < nextBrickData[i].length; j++) {
                  int targetRow = i + offset[0];
                  int targetCol = j + offset[1];
                  if (targetRow >= 0 && targetRow < PREVIEW_GRID_SIZE && 
                      targetCol >= 0 && targetCol < PREVIEW_GRID_SIZE) {
                      setRectangleData(nextBrickData[i][j], nextBrickRectangles[targetRow][targetCol]);
      ```


2. Ghost Brick
   - Displays a ghost of the active brick, showing where it will land if dropped
   - I ran into a problem with the ghost brick not updating correctly when the active brick changes and caused the bricks to instead land on top of the ghost brick.
   - Tried multiple fixes but could not get it to work. 
   - Decided to just remove the ghost brick feature as it was not working as expected.


## New Java Classes

### Game and Presentation Logic

These are files that handle the game logic and presentation which were extracted from `GuiController.java` to improve code organization and maintainability.


1.  GameLoop.java
    - Encapsulates the game heartbeat logic by managing the `Timeline` and `tickAction` runnable.
    - Handles the calculation of the drop speed interval based on the current level.
    - Provides specific methods to `start()`, `stop()`, `pause()`, and `resume()` the game cycle independent of the UI.
    - Location: `src/main/java/com/comp2042/model/GameLoop.java`


2.  TimeAttackManager.java
    - Encapsulates the Time Attack logic, managing the 60‑second countdown via a dedicated Timeline.
    - Updates the UI Label directly and triggers a callback Runnable when the timer hits zero.
    - Handles state management (pause/resume) specifically for the timer to ensure it syncs with the game loop.
    - Location: `src/main/java/com/comp2042/model/TimeAttackManager.java`


3. GameBoardRenderer.java
    - Handles the visual representation of the game state by managing `Rectangl`e objects within a `GridPane`.
    - Separates the rendering of the static background matrix from the dynamic active brick.
    - Draws the grid lines and board boundaries using a JavaFX Group overlay.
    - Location: `src/main/java/com/comp2042/render/GameBoardRenderer.java`


4.  PreviewRenderer.java
    - A reusable renderer component designed to display any brick matrix (Next or Hold) in a specific `GridPane`.
    - Manages a fixed grid of `Rectangle` objects and updates their fill color based on the `ColorMapper`.

    - Location: `src/main/java/com/comp2042/render/PreviewRenderer.java`

### View Abstractions

This file acts as the glue between the game logic and the presentation layer.

1. GameView.java
    - An interface defining the contract for the UI, ensuring the logic layer (`GameController`) does not depend directly on JavaFX components.
    - Specifies methods for initializing the view, refreshing the background/bricks, updating previews, and binding the score property.
    - Location: `src/main/java/com/comp2042/render/GameView.java`

### Menu Controllers

These classes handle the user interface for the start screen, pause menu, controls menu, and game over menu. All the files have their own `.fxml` files that they reference too. 


1.  StartController.java
    - Manages the entry point UI, handling scene transitions and loading the main `gameLayout.fxml`.
    - Initializes the game with the selected mode (Classic or Time Attack) and triggers the appropriate music via `Main`.
    - Location: `src/main/java/com/comp2042/controller/StartController.java`


2.  PauseMenuController.java
    - Controls the in-game pause overlay, providing logic to Resume, Restart, view Controls, or Quit to the main menu.
    - Injects the main `GuiController` to trigger game state changes directly from the overlay.
    - Location: `src/main/java/com/comp2042/PauseMenuController.java`


3. ControlsMenuController.java
    - Displays the keybindings overlay and handles navigation back to the Pause menu.
    - Uses `FontHelper` to ensure all instructional labels match the game's pixel art style.
    - Location: `src/main/java/com/comp2042/ControlsMenuController.java`


4. GameOverMenuController.java
    - Manages the end-game state, offering "Retry" (which resets the board) or "Quit" (which returns to Start).
    - Location: `src/main/java/com/comp2042/GameOverMenuController.java`

### Input Classes

These classes handle and manage user input for the game.


1.  GameInputHandler.java 
    - Implements `EventHandler<KeyEvent>` to map raw keyboard inputs (WASD/Arrows) to specific game actions.
    - Includes logic for a "Hard Drop Cooldown" to prevent accidental double-drops.
    - Delegates valid actions to the `GameInputReceiver` (for UI) and `InputEventListener` (for Logic).
    - Location: `src/main/java/com/comp2042/input/GameInputHandler.java`


2. GameInputReceiver.java 
    - A controller interface that allows the input handler to manipulate the View state (e.g., toggling pause, refreshing the brick view).
    - Location: `src/main/java/com/comp2042/input/GameInputReceiver.java`


3. InputEventListener.java 
    - The API for the game logic, defining operations for `onDownEvent`, `onRotateEvent`, `onHoldEvent`, and `onHardDropEvent`.
    - Location: `src/main/java/com/comp2042/input/InputEventListener.java`

### Manager and Helper Classes 

These following classes handle specific jobs. Helped break down God Classes during refactoring.


1.  HighScoreManager.java
    - Manages persistence by reading/writing to `highscore_classic.txt` and `highscore_time_attack.txt`.
    - Contains logic to compare current scores against stored high scores, ensuring files are only updated when a record is broken.
    - Location: `src/main/java/com/comp2042/model/HighScoreManager.java`


2.  OverlayManager.java
    - A utility for loading FXML menus into Group containers and managing their Z-order (visibility/front-layering).
    - Uses reflection to inject the main `GuiController` into the menu controllers dynamically.
    - Location: `src/main/java/com/comp2042/model/OverlayManager.java`-


3.  NavigationHelper.java
    - Centralizes the logic for switching scenes back to the Start Menu.
    - Handles FXML loading for the start screen and ensures the correct music track plays upon transition.
    - Location: `src/main/java/com/comp2042/general_utility/NavigationHelper.java`


4.  UIConstants.java
    - Central repository for UI configuration to prevent magic numbers.
    - Defines grid gaps, brick sizes, opacity levels, font sizes, and file paths for FXML resources.
    - Location: `src/main/java/com/comp2042/general_utility/UIConstants.java`


5. FontLoader.java
    - handles the low-level loading of `janinosjuosta.ttf` from the resource stream or URL.
    - Location: `src/main/java/com/comp2042/render/utility/FontLoader.java`


6. FontHelper.java
    - A diagnostic wrapper that applies the loaded font to JavaFX `Labeled` and `Text` nodes.
    - Includes logging logic to verify font availability and identify fallback system fonts if loading fails.
    - Location: `src/main/java/com/comp2042/render/utility/FontHelper.java`


## Modified Java Classes

1. GuiController.java
   - Completely refactored from a "God Class" into a lightweight controller.

   - Now delegates: 
     - Rendering to `GameBoardRenderer.java` and `PreviewRenderer.java`.
     - Timing and loops to `GameLoop.java` and `TimeAttackManager.java`.
     - Input handling to `GameInputHandler.java`.

   - Now implements `GameView.java` and `GameInputReceiver.java` to act as the bridge between Logic and UI.

   - Location: `src/main/java/com/comp2042/controller/GuiController.java`


2. GameController.java
   - No longer depends on `GuiController.java`
   - Interacts with the UI via `GameView.java`
   - Implements `InputEventListener` to process moves, rotations, and drops requested by the input handler.
   - Updated to manage the new scoring rules for Soft/Hard drops and integration with the `SimpleBoard`.
   - Location: `src/main/java/com/comp2042/controller/GameController.java`


3. Main.java
   - Simplified the start method to delegate scene initialization to `StartController`.
   - Added a robust background music manager that handles looping, track switching, and volume dimming when the game is paused.
   - Location: `src/main/java/com/comp2042/main/Main.java`


4. SimpleBoard.java
   - Implemented `rotateLeftBrick` with Wall Kick logic (trying alternative offsets if rotation is blocked).
   - Added the `holdBrick` method to swap the active piece with a stored one (implementing the "once per turn" rule).
   - Added `hardDrop` logic which simulates the brick falling to the lowest valid Y-coordinate instantly.
   - Location: `src/main/java/com/comp2042/model/SimpleBoard.java`


5. Board.java
   - Updated the interface contract to support the new features.
   - Added signatures for `holdBrick`, `getHoldBrickData`, `hardDrop`, and `isGameOver`.
   - Location: `src/main/java/com/comp2042/model/Board.java`

### Removed Classes

1. GameOverPanel.java
   - Replaced with `GameOverMenuController.java` to implement restart and quit functions.

## Resources

- FXML:
  - `startLayout.fxml`, `gameLayout.fxml`, `gameOverMenu.fxml`, `pauseMenu.fxml`, `controlsMenu.fxml`

- CSS:
  - `window_style.css` defines backgrounds and UI panel styles

- Audio:
  - `Pokemon_Center.mp3`, `Littleroot_Town.mp3`, `Battle!_Trainer.mp3`
  
- Images/Fonts:
  - Backgrounds and title images under `src/main/resources`
  - `janinosjuosta.ttf` bundled and loaded at startup

## Unexpected Problems Encountered

1. Font Loading Issues:
   - The font would not load from the resources folder even though it was specified in the `window_styles.css`
     - Solution:
       - Introduced a separate Java class (`FontLoader.java`) to load the font programmatically into the necessary files
       - Added a Helper class (`FontHelper.java`) to apply the font to labels and text nodes and to diagnose font availability to keep `FontLoader.java`'s job as just loading the font

2. Game Not Restarting as Expected:
   - After changing the brick randomization logic to the 7-Bag system the game would not restart as expected and would instead just crash.
   - Solution:
     - Debugged the logic and found the issue 
     - The issue was that the previous restart logic was not working with the new 7-Bag system and would sometimes return a `NULL` value for the next brick.
     - Added a method to reset the Bag every time the game is restarted within the `RandomBrickGenerator.java` to ensure there always is a full bag when the game restarts.
     - Inadvertently made the randomisation better

3. The Border for the Game Board
   - The border for the game board would not render properly and was not fitting to the size of the grid
   - The border was either too big or too small and would cover up part of the GameBoard at times making the game hard to play
   - There was also a problem where the border would only render halfway around the grid and not cover the entire grid
   - Solution:
     - Removed the border entirely
     - Made the grid of the GameBoard visible and made the outside edges of the grid black as a new minimalistic border
     - Made the background of the grid black (with adjustable opacity via `UIConstants.java`) to increase visibility of the bricks
  

4. Ghost Brick Collision Bugs:
   - The ghost brick implementation caused false-positive collisions, making bricks stop in midair.
   -  Prioritized game stability over features. I removed the ghost brick code entirely rather than submitting a buggy version.