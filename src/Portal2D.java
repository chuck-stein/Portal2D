import processing.core.PApplet;
import processing.core.PImage;

/**
 * Main class -- runs a game of 2D Portal using the Processing 2.2.1 library
 */
public class Portal2D extends PApplet {
  // background image for displaying the game
  private PImage background;
  // the level select menu for the game
  private Menu menu;
  // true if player is in a level, false if on level select
  private boolean inLevel;
  // number of distinct levels that the player has completed
  private int levelsCompleted;
  // the level being played when the player is in level
  private Level currLevel;
  // opacity of the fadeout upon level completion
  private int fade;

  public static void main(String[] args) {
    PApplet.main("Portal2D");
  }

  /**
   * The Processing setup method, which runs once when the program is initiated. Sets the size of
   * the window, loads the background image, assigns the main game variables, and sets the text
   * mode
   */
  public void setup() {
    size(1000, 600);
    menu = new Menu(this);
    background = loadImage("background.png");
    inLevel = false;
    levelsCompleted = 0;
    currLevel = new Level(1, this);
    fade = 0;
    textAlign(CENTER, CENTER);
  }

  /**
   * The Processing draw method, which runs once every frame after setup() has completed. Sets the
   * frame rate to 60, displays the background, and either plays a level or draws the level select
   * menu, depending on whether the player is in a level or not
   */
  public void draw() {
    frameRate(60);
    background(background);
    if (inLevel) {
      currLevel.play();
      if (currLevel.levelEnding()) {
        endLevel();
      }
    } else {
      menu.display(levelsCompleted);
    }
  }

  /**
   * Fades out the screen and marks the current level as completed when the player successfully
   * completes it, then returns to title screen
   */
  public void endLevel() {
    currLevel.exitLevel();
    fill(0, fade);
    rect(0, 0, width, height);
    fade += 5;
    if (fade >= 255) {
      if (currLevel.getLevelNum() == levelsCompleted + 1) {
        levelsCompleted++;
      }
      inLevel = false;
      fade = 0;
    }
  }

  /**
   * The Processing key handler for when a key is being pressed (stores which key in the variable
   * 'key'). A = move left, D = move right, W = jump, BACKSPACE = return to menu, L = cheat code to
   * unlock the next level
   */
  public void keyPressed() { // change to switch statement?
    if (!currLevel.levelEnding()) {
      if (key == 'A' || key == 'a') {
        currLevel.movePlayer(MovementType.LEFT);
      }
      if (key == 'D' || key == 'd') {
        currLevel.movePlayer(MovementType.RIGHT);
      }
      if (key == 'W' || key == 'w') {
        currLevel.movePlayer(MovementType.JUMP);
      }
    }
    if (inLevel && (key == 'E' || key == 'e')) {
      currLevel.actionButton();
    }
    if (keyCode == BACKSPACE) {
      inLevel = false;
    }
    //DEV TOOL/CHEAT CODE:
    if (key == 'L' || key == 'l') {
      levelsCompleted++;
    }
  }

  /**
   * The Processing key handler for when a key is released (stores which key in the variable 'key').
   * A = stop moving left, D = stop moving right
   */
  public void keyReleased() {
    if (key == 'A' || key == 'a') {
      currLevel.stopPlayer(MovementType.LEFT);
    }
    if (key == 'D' || key == 'd') {
      currLevel.stopPlayer(MovementType.RIGHT);
    }
  }

  /**
   * The Processing mouse button handler (stores which button is being pressed in the variable
   * 'mouseButton'). LEFT = fire blue portal, RIGHT = fire orange portal, CENTER = action button
   * (pickup/drop a cube or press a pedestal button). If the player is on the level select menu,
   * then any mouse button will instead attempt to select a level button from the menu
   */
  public void mousePressed() {
    if (inLevel) {
      if (mouseButton == LEFT || mouseButton == RIGHT) {
        currLevel.fireProjectile();
      } else if (mouseButton == CENTER) {
        currLevel.actionButton();
      }
    } else if (menu.buttonNumClicked(levelsCompleted) > 0) {
      currLevel = new Level(menu.buttonNumClicked(levelsCompleted), this);
      inLevel = true;
    }
  }

//  /**
//   * Displays the level select menu
//   */
//  public void drawMenu() {
//    textSize(50);
//    fill(0);
//    text("LEVEL SELECT", width / 2, height / 9);
//    drawButtons();
//  }
//
//  /**
//   * Displays the level buttons for the level select menu
//   */
//  public void drawButtons() {
//    stroke(0);
//    for (int i = 0; i < 18; i++) {
//      if (i < 6) {
//        fillLevelBox(i);
//        rect(width / 6 * i + width / 28, height / 12 * 3, 100, 100);
//        fill(255);
//        text(i + 1, width / 6 * i + width / 28 + 50, height / 12 * 3 + 50);
//      } else if (i < 12) {
//        fillLevelBox(i);
//        rect(width / 6 * (i - 6) + width / 28, height / 12 * 6, 100, 100);
//        fill(255);
//        text(i + 1, width / 6 * (i - 6) + width / 28 + 50, height / 12 * 6 + 50);
//      } else if (i < 18) {
//        fillLevelBox(i);
//        rect(width / 6 * (i - 12) + width / 28, height / 12 * 9, 100, 100);
//        fill(255);
//        text(i + 1, width / 6 * (i - 12) + width / 28 + 50, height / 12 * 9 + 50);
//      }
//    }
//  }
//
//  /**
//   * Determines the fill color for a level button:
//   * blue = unlocked but not beaten,
//   * orange = unlocked and beaten,
//   * black = locked
//   *
//   * @param levelNum the level number of the box to be filled
//   */
//  public void fillLevelBox(int levelNum) {
//    if (levelsCompleted > levelNum) {
//      fill(255, 153, 0);
//    } else if (levelsCompleted == levelNum) {
//      fill(0, 204, 255);
//    } else {
//      fill(0);
//    }
//  }
//
//  /**
//   * If the mouse is on a level button, then enters the corresponding level
//   */
//  public void chooseLevel() {
//    for (int i = 0; i < 18; i++) {
//      // first row of buttons:
//      if ((i < 6 && mouseX > width / 6 * i + width / 28
//              && mouseX < width / 6 * i + width / 28 + 100 && mouseY > height / 12 * 3
//              && mouseY < height / 12 * 3 + 100 && levelsCompleted >= i)
//              // second row of buttons:
//              || (i < 12 && mouseX > width / 6 * (i - 6) + width / 28
//              && mouseX < width / 6 * (i - 6) + width / 28 + 100
//              && mouseY > height / 12 * 6 && mouseY < height / 12 * 6 + 100
//              && levelsCompleted >= i)
//              // third row of buttons:
//              || (i < 18 && mouseX > width / 6 * (i - 12) + width / 28
//              && mouseX < width / 6 * (i - 12) + width / 28 + 100 && mouseY > height / 12 * 9
//              && mouseY < height / 12 * 9 + 100 && levelsCompleted >= i)) {
//        currLevel = new Level(i + 1, this);
//        inLevel = true;
//      }
//    }
//  }
//
//  /**
//   * Fades out the screen and marks the current level as completed when the player successfully
//   * completes it, then returns to title screen
//   */
//  public void endLevel() {
//    currLevel.exitLevel();
//    fill(0, fade);
//    rect(0, 0, width, height);
//    fade += 5;
//    if (fade >= 255) {
//      if (currLevel.getLevelNum() == levelsCompleted+1) {
//        levelsCompleted++;
//      }
//      inLevel = false;
//      fade = 0;
//    }
//  }

}
