import processing.core.PApplet;
import processing.core.PImage;

/**
 * A button on a pedestal, which the player can toggle on or off
 */
public class PedestalButton extends AbstractCollisionObj {

  // whether or not this PedestalButton is active
  private boolean active;

  /**
   * Constructs a new 10x30 inactive PedestalButton at the given coordinates, which uses the
   * given Processing library
   * @param x the x coordinate of the PedestalButton
   * @param y the x coordinate of the PedestalButton
   * @param processing the Processing library
   */
  public PedestalButton(int x, int y, PApplet processing) {
    p = processing;
    this.x = x;
    this.y = y;
    width = 10;
    height = 30;
    active = false;
  }

  /**
   * Draws this Pedestal Button at its coordinates using the given sprite corresponding to its state
   * (active or inactive)
   * @param inactiveSprite the inactive pedestal button sprite
   * @param activeSprite the active pedestal button sprite
   */
  public void draw(PImage inactiveSprite, PImage activeSprite) {
    if (active) {
      p.image(activeSprite, x, y, width, height);
    } else {
      p.image(inactiveSprite, x, y, width, height);
    }
  }

  /**
   * Toggles this PedestalButton on or off, setting its size and position to accommodate the
   * new sprite
   */
  public void toggle() {
    if (active) {
      active = false;
      y -= 5;
      height = 30;
    } else {
      active = true;
      y += 5;
      height = 25;
    }
  }

  /**
   * Returns true if this PedestalButton is currently activated
   * @return whether or not the PedestalButton is active
   */
  public boolean isActive() {
    return active;
  }

}
