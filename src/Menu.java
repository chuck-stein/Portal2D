import processing.core.PApplet;

/**
 * Represents the level select menu in a game of 2D Portal
 */
public class Menu {

  // the Processing applet to use Processing functions
  PApplet p;

  public Menu(PApplet processing) {
    p = processing;
  }

  /**
   * Displays the level select menu
   */
  public void display(int levelsCompleted) {
    p.textSize(50);
    p.fill(0);
    p.text("LEVEL SELECT", p.width / 2, p.height / 7);
    drawButtons(levelsCompleted);
  }

  /**
   * Displays the level buttons for the level select menu
   */
  public void drawButtons(int levelsCompleted) {
    p.stroke(0);
    for (int i = 0; i < 12; i++) {
      if (i < 6) {
        fillLevelBox(i, levelsCompleted);
        p.rect(p.width / 6 * i + p.width / 28, p.height / 12 * 4, 100, 100);
        p.fill(255);
        p.text(i + 1, p.width / 6 * i + p.width / 28 + 50, p.height / 12 * 4 + 50);
      } else if (i < 12) {
        fillLevelBox(i, levelsCompleted);
        p.rect(p.width / 6 * (i - 6) + p.width / 28, p.height / 12 * 8, 100, 100);
        p.fill(255);
        p.text(i + 1, p.width / 6 * (i - 6) + p.width / 28 + 50, p.height / 12 * 8 + 50);
      }
    }
  }

  /**
   * Determines the fill color for a level button:
   * blue = unlocked but not beaten,
   * orange = unlocked and beaten,
   * black = locked
   *
   * @param levelNum the level number of the box to be filled
   */
  public void fillLevelBox(int levelNum, int levelsCompleted) {
    if (levelsCompleted > levelNum) {
      p.fill(255, 153, 0);
    } else if (levelsCompleted == levelNum) {
      p.fill(0, 204, 255);
    } else {
      p.fill(0);
    }
  }

  /**
   * Returns the number of the level button which the mouse is currently over, or 0 if the mouse
   * is not currently over any level buttons
   * @param levelsCompleted the number of levels that the player has completed so far
   * @return the number of the level button at the mouse's current coordinates if it is over one,
   * or 0 if not
   */
  public int buttonNumClicked(int levelsCompleted) {
    for (int i = 0; i < 18; i++) {
      // first row of buttons:
      if ((i < 6 && p.mouseX > p.width / 6 * i + p.width / 28
              && p.mouseX < p.width / 6 * i + p.width / 28 + 100 && p.mouseY > p.height / 12 * 4
              && p.mouseY < p.height / 12 * 4 + 100 && levelsCompleted >= i)
              // second row of buttons:
              || (i < 12 && p.mouseX > p.width / 6 * (i - 6) + p.width / 28
              && p.mouseX < p.width / 6 * (i - 6) + p.width / 28 + 100
              && p.mouseY > p.height / 12 * 8 && p.mouseY < p.height / 12 * 8 + 100
              && levelsCompleted >= i)) {
        return i + 1;
      }
    }
    return 0;
  }

}
