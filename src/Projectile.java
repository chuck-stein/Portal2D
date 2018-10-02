import processing.core.PApplet;

/**
 * A Projectile fired by the Player, which carries the ability to create a portal of corresponding
 * color if it collides with a valid Wall
 */
public class Projectile extends AbstractPortalObj {

  // the speed of all Projectiles (15 pixels per frame)
  private static final int SPEED = 15;
  // the x and y velocities of this Projectile
  private float vx, vy;
  // the x and y coordinates of where the user clicked to fire this Projectile
  private int targetX, targetY;

  /**
   * Creates a new 14x14 offscreen Projectile at (-1, -1) of the given color with no velocity and no
   * target, which uses the given Processing library
   *
   * @param color      the color of the Projectile: blue or orange
   * @param processing the Processing library
   */
  public Projectile(PortalColor color, PApplet processing) {
    p = processing;
    x = -1;
    y = -1;
    width = 14;
    height = 14;
    vx = 0;
    vy = 0;
    targetX = 0;
    targetY = 0;
    onScreen = false;
    this.color = color;
  }

  /**
   * Returns the constant speed of all Projectiles
   *
   * @return the constant Projectile speed
   */
  public static int getSpeed() {
    return SPEED;
  }

  /**
   * Fires this Projectile from the given coordinates if it is not already onScreen, setting its
   * target coordinates to where the user clicked and its velocities to the direction of a straight
   * line to the target
   *
   * @param startX the x coordinate to fire this Projectile from
   * @param startY the y coordinate to fire this Projectile from
   */
  public void fire(float startX, float startY) {
    if (!onScreen) {
      x = startX;
      y = startY;
      targetX = p.mouseX;
      targetY = p.mouseY;
      double distToTarget = p.dist(x, y, targetX, targetY);
      vx = (float) ((targetX - x) / distToTarget * SPEED);
      vy = (float) ((targetY - y) / distToTarget * SPEED);
      onScreen = true;
    }
  }

  /**
   * Moves this Projectile according to its x- and y-velocities if it is onScreen, 1/15th of the
   * distance that it should travel in one frame (because this method is called 15 times per frame)
   */
  public void move() {
    if (onScreen) {
      x += vx / SPEED;
      y += vy / SPEED;
    }
  }

  /**
   * Returns true if this Projectile is onScreen and touching the given Wall
   *
   * @param w the Wall to be checked for collision
   * @return whether or not the Projectile is touching the Wall
   */
  public boolean hitsWall(Wall w) {
    if (onScreen) {
      return w.touchingObj(x, y, 0, 0);
    }
    return false;
  }

  /**
   * Moves this Projectile to the nearest edge of the given Wall
   * @param w the Wall whose edge this Projectile is to be moved to
   */
  public void moveToWallEdge(Wall w) {
    w.roundProjectileToEdge(x, y, this);
  }

  /**
   * Returns a Portal of this Projectile's color at its current coordinates on the given Wall
   *
   * @param w the Wall on which the Portal will be created
   * @return the Portal that this Projectile creates
   * @throws RuntimeException if the prospective Portal's orientation could not be determined
   */
  public Portal createPortal(Wall w) throws RuntimeException {
    PortalOrientation orientation = w.findPortalOrientation((int)x, (int)y);
    return new Portal(color, x, y, orientation, w, p);
  }

  /**
   * Stops this Projectile's movement and sets it to be offscreen
   */
  public void cancelShot() {
    onScreen = false;
    vx = 0;
    vy = 0;
  }

  /**
   * Returns true if this Projectile can make a Portal on the given Wall. It cannot if any of the
   * following conditions are met: the given Wall is portal-resistant, there is not enough room to
   * fit a Portal on the given Wall, or the Projectile is touching the Portal of opposite color
   * (because you cannot make Portals on top of each other)
   *
   * @param w           the Wall on which this Projectile would make a Portal if it can
   * @param otherPortal the Portal of opposite color to this Projectile
   * @return whether or not this Projectile can make a Portal on the given Wall
   * @throws RuntimeException if the prospective Portal's orientation could not be determined
   */
  public boolean canMakePortal(Wall w, Portal otherPortal) throws RuntimeException {
    if (!w.isPortalFriendly()) {
      return false;
    }
    if (otherPortal.touchingObj(x, y, width, height)) {
      return false;
    }
    PortalOrientation orientation = w.findPortalOrientation((int)x, (int)y);
    return w.hasPortalRoom(orientation, x, y, this);
  }

  /**
   * Sets this Projectile's x position to the given one
   *
   * @param newX the new x value
   */
  public void setX(float newX) {
    x = newX;
  }

  /**
   * Sets this Projectile's y position to the given one
   *
   * @param newY the new y value
   */
  public void setY(float newY) {
    y = newY;
  }

}
