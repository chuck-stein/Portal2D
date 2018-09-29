import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * A button on the floor, which is only pressed when the Player or a Cube is on top of it
 */
public class FloorButton extends AbstractCollisionObj{

  // the original y position of this FloorButton
  private int yOrigin;
  // whether or not the FloorButton is being pressed
  private boolean pressed;

  /**
   * Constructs a new unpressed 50x10 FloorButton at the given coordinates, which uses the given
   * Processing library
   * @param x the x coordinate of the FloorButton
   * @param y the y coordinate of the FloorButton
   * @param processing the Processing library
   */
  public FloorButton(int x, int y, PApplet processing) {
    p = processing;
    this.x = x;
    this.y = y;
    yOrigin = y;
    width = 50;
    height = 10;
    pressed = false;
  }

  /**
   * Draws this FloorButton at its coordinates using the given sprite corresponding to its state
   * (pressed or unpressed), setting its size and position to accommodate that sprite
   * @param unpressedSprite the unpressed floor button sprite
   * @param pressedSprite the pressed floor button sprite
   */
  public void draw(PImage unpressedSprite, PImage pressedSprite) {
    if (pressed) {
      height = 5;
      y = yOrigin+5;
      p.image(pressedSprite, x, y, width, height);
    } else {
      height = 10;
      y = yOrigin;
      p.image(unpressedSprite, x, y, width, height);
    }
  }

  /**
   * Sets this FloorButton to pressed if the player or any of the given Cubes are touching it.
   * Sets it to unpressed if not.
   * @param player the Player being checked for collision
   * @param cubes the Cubes being checked for collision
   */
  public void checkIfPressed(Player player, ArrayList<Cube> cubes) {
    pressed = false;
    if (player.touchingObj(x, y, width, height)) {
      pressed = true;
      return;
    }
    for (Cube c : cubes) {
      if (c.touchingObj(x, y, width, height)) {
        pressed = true;
        return;
      }
    }
  }

  /**
   * Returns true if this FloorButton is pressed
   * @return whether or not the FloorButton is pressed
   */
  public boolean isPressed() {
    return pressed;
  }

}
