# Tetris JavaFX — CW2025

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
      mvnw.cmd clean javafx:run
      ```

- Notes
  - Resources (FXML, CSS, images, audio, fonts) are under `src/main/com/comp2042/resources`
  - `pom.xml` is configured to load resources from `src/main/com/comp2042/resources` and use the JavaFX Maven plugin
  - No manual module-path flags are necessary when running via Maven

## Implemented and Working Properly

- Level System and Drop Speed
  - Levels increase every 10 lines cleared
  - Drop speed uses the provided formula and updates dynamically

- Music
  - Start menu loops `Pokemon_Center.mp3`
  - Classic mode plays `Littleroot_Town.mp3`
  - Time Attack plays `Battle!_Trainer.mp3`
  - Pausing reduces music volume to 50%

- Time Attack Mode
  - 60-second countdown with on-screen timer
  - Separate high scores tracked for Classic and Time Attack

- 7‑Bag Brick Randomization
  - Fair distribution of pieces and gives the player a greater sense of randomness in the gameplay. 

- Next Brick Preview
  - Displays the next brick to be dropped in the `Next Brick` panel
  - Updates dynamically as the bag is refilled

- Hold Functionality
  - Allows players to hold a brick while dropping another
  - Switches the active brick with the held one and resets the hold
  - Shows the currently "held" brick in a preview panel

- Start Screen
  - Displays the game title and start buttons for Classic and Time Attack modes

- Hard Drop Functionality
  - Allows players to instantly drop the active brick to the lowest possible position on the playfield

- Menus and Font
  - Start, Pause, Game Over, and Controls menus implemented to be displayed when the corresponding events occur
  - Labels and UI use the bundled pixel font (`janinosjuosta.ttf`)

## Features Not Implemented

- Centering the Bricks within the preview panels
  - Was supposed to ensure the preview bricks were centered in the preview panels
  - However, the implementation did not account for the different sizes of the preview panels. I tried multiple approaches to center the bricks, but none worked as expected.
  - The method I used was to compute the smallest box that contains the preview piece, then shift it so that box sits in the middle of the 4×4 preview grid but ended up not working as expected.
  - The bricks were instead aligned to the top-left corner of the preview panels

- Ghost Brick
  - Displays a ghost of the active brick, showing where it will land if dropped
  - I ran into a problem with the ghost brick not updating correctly when the active brick changes and caused the bricks to instead land on top of the ghost brick.
  - Tried multiple fixes but could not get it to work. 
  - Decided to just remove the ghost brick feature as it was not working as expected.


## New Java Classes

### Game and Presentation Logic

These are files that handle the game logic and presentation which were extracted from GuiController to improve code organization and maintainability.

- GameLoop.java
  - Owns the drop `Timeline`, level tracking, and recomputing of brick falling speed
  - Location: `src/main/java/com/comp2042/model/GameLoop.java`

- TimeAttackManager.java
  - Manages the 60‑second countdown timer, pause/resume, and timeout callback for the Time Attack mode
  - Location: `src/main/java/com/comp2042/model/TimeAttackManager.java`

- GameBoardRenderer.java
  - Renders the main playfield background cells, active brick, grid lines, and the thick border
  - Location: `src/main/java/com/comp2042/render/GameBoardRenderer.java`

- PreviewRenderer.java
  - Renders the 4×4 `Hold` and `Next` preview grids
  - Location: `src/main/java/com/comp2042/render/PreviewRenderer.java`

### View Abstractions

This file acts as the glue between the game logic and the presentation layer.

- GameView.java
  - Defines the UI contract for the game, including the game board, preview grids, and overlays
  - Location: `src/main/java/com/comp2042/render/GameView.java`

### Menu Controllers

These classes handle the user interface for the start screen, pause menu, controls menu, and game over menu.

- StartController.java
  - Handles the start screen UI and game mode selection and scene creation.
  - Location: `src/main/java/com/comp2042/controller/StartController.java`

- PauseMenuController.java
  - Handles the pause menu UI and actions depending on the buttons clicked (Resume, Restart, Controls, Quit)
  - Location: `src/main/java/com/comp2042/PauseMenuController.java`

- ControlsMenuController.java
  - Displays the controls menu with keybindings for both game modes.
  - Location: `src/main/java/com/comp2042/ControlsMenuController.java`

- GameOverMenuController.java
  - Displays the game over menu with options to restart or quit the game.
  - Location: `src/main/java/com/comp2042/GameOverMenuController.java`

### Input Classes

These classes handle and manage user input for the game.

- GameInputHandler.java 
  - Translates keyboard events to game actions (move, rotate, drop)
  - Location: `src/main/java/com/comp2042/input/GameInputHandler.java`

- GameInputReceiver.java 
  - Controller interface consumed by `GameInputHandler.java` to apply actions
  - Location: `src/main/java/com/comp2042/input/GameInputReceiver.java`

- InputEventListener.java 
  - Game logic API providing operations to move, rotate, hold, and drop bricks
  - Location: `src/main/java/com/comp2042/input/InputEventListener.java`

### Manager and Helper Classes 

These following classes handle specific jobs.

- HighScoreManager.java
  - Manages high scores for both game modes by reading from and writing to text files depending on the game mode.
  - Only overwrites the highscore when a new highscore is achieved 
  - Location: `src/main/java/com/comp2042/model/HighScoreManager.java`

- OverlayManager.java
  - Manages the display of overlays (show/hide/bring-to-front for the pause menu, game over menu, and controls menu)
  - Location: `src/main/java/com/comp2042/model/OverlayManager.java`-

- Navigationhelper.java
  - Helper class to switch to the start menu by quitting from the game over menu or pause menu.
  - Location: `src/main/java/com/comp2042/general_utility/NavigationHelper.java`

- UIConstants.java
  - Contains constants to store magic numbers (Brick sizer, grid dimensions, FXML file paths)
  - Location: `src/main/java/com/comp2042/general_utility/UIConstants.java`

- FontLoader.java
  - Loads the font `janinosjuosta.ttf` from the resources folder
  - Location: `src/main/java/com/comp2042/render/utility/FontLoader.java`

- FontHelper.java
  - Applies the font to Labels/Text nodes
  - Diagnoses font availability and handles fallback if not found
  - Location: `src/main/java/com/comp2042/render/utility/FontHelper.java`


## Modified Java Classes

- GuiController.java
  - Refactored it from handling all game logic, rendering and input into a lightweight controller
  - Now delegates: 
    - Rendering to GameBoardRenderer.java
    - Timing and loops to GameLoop.java
    - Input to GameInputHandler.java
  - Now implements:
    - GameView.java
    - GameInputReceiver.java
  - Location: `src/main/java/com/comp2042/controller/GuiController.java`

- GameController.java
  - No longer depends on GuiController.java
  - Interacts with the UI via GameView.java
  - Updated to support the new Hold Brick and Hard Drop features
  - Location: `src/main/java/com/comp2042/controller/GameController.java`

- Main.java
  - Removed direct scene setup and font loading logic 
  - Delegates initialization to StartController.java and FontLoader.java
  - Location: `src/main/java/com/comp2042/main/Main.java`

- SimpleBoard.java
  - Added implementation for Hard Drop and Hold Brick features
  - Added logic to check if the game is over directly within the board logic
  - Location: `src/main/java/com/comp2042/model/SimpleBoard.java`

- Board.java
  - Updated the interface to include methods for hardDrop, holdBrick, getHoldBrickData, and isGameOver
  - Location: `src/main/java/com/comp2042/model/Board.java`

### Removed Classes

- GameOverPanel.java
  - Replaced with GameOverMenuController.java to implement restart and quit functions.

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

- Font Loading Issues:
  - The font would not load from the resources folder even though it was specified in the `window_styles.css`
  - Solution:
    - Introduced a separate class to load the font programatically (FontLoader.java)
    - Added a Helper class (FontHelper.java) to apply the font to labels and text nodes

- Game Not Restarting as Expected:
  - After changing the brick randomization logic to the 7-Bag system the game would not restart as expected and would instead just crash.
  - Solution:
    - Debugged the logic and found the issue 
    - The issue was that the previous restart logic was not working with the new 7-Bag system and would sometimes return a NULL value for the next brick.
    - Solution:
      - Added a method to reset the Bag every time the game is restarted within the RandomBrickGenerator.java to ensure there always is a full bag when the game restarts.
      - Inadvertantly made the randomisation better

-  The Border for the GameBoard:
  - The border for the game board would not render properly and was not fitting to the size of the grid
  - The border was either too big or too small and would cover up part of the GameBoard at times making the game hard to play
  - There was also a problem where the border would only render halfway around the grid and not cover the entire grid
  - Solution:
    - Removed the border entirely
    - Made the grid of the GameBoard visible and made the outside edges of the grid black as a new minimalistic border
    - Made the background of the grid black (with adjustable opacity via UIConstants.java) to increase visibility of the bricks

- Ghost Brick Collision Bugs:
  - The ghost brick implementation caused false-positive collisions, making bricks stop in mid-air.
  Prioritized game stability over features. I removed the ghost brick code entirely rather than submitting a buggy version.