import processing.core.PApplet;
import processing.core.PImage;

/**
 * A cube that can be held and moved by the player, as well as teleported and used to press floor
 * buttons
 */
public class Cube extends AbstractPhysicsObj {
  // whether or not the player is currently holding the cube
  private boolean held;

  /**
   * Constructs a new 25x25 cube with 0.92 friction and 0 velocity at the given coordinates,
   * which is not being held, and uses the given Processing library
   * @param x the x position of the Cube
   * @param y the y position of the Cube
   * @param processing the Processing library
   */
  public Cube(float x, float y, PApplet processing) {
    p = processing;
    this.x = x;
    this.y = y;
    vx = 0;
    vy = 0;
    friction = 0.92f; //92
    width = 25;
    height = 25;
    held = false;
  }

  /**
   * Draws this Cube at its current position using the given sprite
   * @param sprite the cube sprite
   */
  public void draw(PImage sprite) {
    p.image(sprite, x, y, width, height);
  }

  /**
   * Sets this Cube be at the given player coordinates if it is currently being held
   * @param playerX the x coordinate of the player holding the Cube
   * @param playerY the y coordinate of the player holding the Cube
   */
  public void move(float playerX, float playerY) {
    if (held) {
      x = playerX + 2.5f; //so that cube is in the middle of the player sprite
      y = playerY + 2.5f; //so that cube is in the middle of the player sprite
    }
  }

  /**
   * Handles the player interacting with this Cube: sets it to being held if it currently is
   * not, and releases it with the player's given velocities if it is currently being held
   * @param playerVX the player's X velocity
   * @param playerVY the player's Y velocity
   */
  public void playerInteraction(float playerVX, float playerVY) {
    if (!held) {
      held = true;
    } else {
      held = false;
      vx = playerVX;
      vy = playerVY;
    }
  }

  /**
   * Returns true if this Cube is being held
   * @return whether or not this Cube is being held
   */
  public boolean isHeld() {
    return held;
  }

}
