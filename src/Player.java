import processing.core.PApplet;
import processing.core.PImage;

/**
 * The player in a Level of 2D Portal, which can move, teleport, and interact with buttons and cubes
 */
public class Player extends AbstractPhysicsObj {

  // the horizontal speed that Players move in (4 pixels per frame)
  private static final int X_SPEED = 4;
  // whether or not the Player is currently moving left
  private boolean movingLeft;
  // whether or not the Player is currently moving right
  private boolean movingRight;
  // whether or not the Player is on the ground
  private boolean grounded;

  /**
   * Constructs a new 30x30 grounded player with 0.8 friction and 0 velocity at the given
   * coordinates, which is not moving to the left, right, and uses the given Processing library
   * @param x the x position of the Player
   * @param y the y position of the Player
   * @param processing the Processing library
   */
  Player(float x, float y, PApplet processing) {
    p = processing;
    this.x = x;
    this.y = y;
    vx = 0;
    vy = 0;
    friction = 0.8f;
    width = 30;
    height = 30;
    grounded = true;
    movingLeft = false;
    movingRight = false;
  }

  /**
   * Draws this Player at its current position using the given sprite
   * @param sprite
   */
  public void draw(PImage sprite) {
    p.image(sprite, x, y, width, height);
  }

  /**
   * Sets this Player's velocities according to whether they are moving left, right, jumping, or
   * still
   */
  public void processMovement() {
    if (movingLeft) {
      vx = -X_SPEED;
    } else if (movingRight) {
      vx = X_SPEED;
    }
  }

  /**
   * Returns true if this Player is touching the given coordinates of the help node
   * @param helpX the x coordinate of the help node
   * @param helpY the y coordinate of the help node
   * @return whether or not the Player is touching the help node
   */
  public boolean touchingHelp(int helpX, int helpY) {
    return p.dist(x, y, helpX, helpY) < width;
  }


  // ***TRY TO ABSTRACT THIS:
  @Override
  public void exitPortal(float inV, float crossV, PortalOrientation orientation, float portalX,
                         float portalY) {
    switch (orientation) {
      case FROM_LEFT:
        x = portalX - width - 10;
        y = portalY - height / 2;
        vx = -inV;
        vy = crossV;
        if (movingRight) {
          movingRight = false;
        }
        break;
      case FROM_RIGHT:
        x = portalX + 10;
        y = portalY - height / 2;
        vx = inV;
        vy = -crossV;
        if (movingLeft) {
          movingLeft = false;
        }
        break;
      case FROM_TOP:
        x = portalX - width / 2;
        y = portalY - height - 10;
        vx = -crossV;
        vy = -inV;
        break;
      case FROM_BOTTOM:
        x = portalX - width / 2;
        y = portalY + 10;
        vx = crossV;
        vy = inV;
        break;
    }
  }

  /**
   * Returns true if this Player is touching the exit door with the given size and coordinates
   * @param doorX the x coordinate of the exit door
   * @param doorY the y coordinate of the exit door
   * @param doorSize the width and height of the exit door
   * @return whether or not the player is touching the exit door, and therefore leaving the level
   */
  public boolean leavingLevel(int doorX, int doorY, int doorSize) {
    return x > doorX && x + width < doorX + doorSize && y > doorY && y + height < doorY + doorSize;
  }

  /**
   * Puts the given Projectile on screen at the center of the Player sprite, moving toward where the
   * user clicked
   * @param proj the Projectile to be fired
   */
  public void fireProjectile(Projectile proj) {
    float playerCenterX = x + width / 2;
    float playerCenterY = y + height / 2;
    proj.fire(playerCenterX, playerCenterY);
  }

  /**
   * Returns true if this Player is touching the given Cube
   * @param c the Cube to be checked for collision
   * @return whether or not the Player is touching the Cube
   */
  public boolean touchingCube(Cube c) {
    return c.touchingObj(x, y, width, height);
  }

  /**
   * Sets the given Cube's position to this Player's position if they are holding it
   * @param c the Cube to be moved with the Player
   */
  public void moveCube(Cube c) {
    c.move(x, y);
  }

  /**
   * Lets go of the given Cube with the Player's velocities if it is being held, or picks it up
   * if it is not being held
   * @param c the Cube to be picked up or dropped
   */
  public void cubeInteraction(Cube c) {
    c.playerInteraction(vx, vy);
  }

  /**
   * Returns true if this Player is touching the given PedestalButton
   * @param b the PedestalButton to be checked for collision
   * @return wheher or not the Player is touching the PedestalButton
   */
  public boolean touchingPButton(PedestalButton b) {
    return b.touchingObj(x, y, width, height);
  }

  /**
   * Sets this Player to be moving left
   */
  public void moveLeft() {
    movingLeft = true;
  }

  /**
   * Sets this Player to be moving right
   */
  public void moveRight() {
    movingRight = true;
  }

  /**
   * Stops this Player's movement to the left
   */
  public void stopLeft() {
    movingLeft = false;
  }

  /**
   * Stops this Player's movement to the right
   */
  public void stopRight() {
    movingRight = false;
  }

  /**
   * Makes this Player jump if they are grounded, by giving them a high negative y-velocity
   */
  public void jump() {
    if (grounded) {
      vy = -15;
    }
  }

  /**
   * Returns true if the given Wall is a pixel below this Player
   * @param w the Wall being checked for this Player above it
   * @return true if the given Wall would be colliding with this Player if they were a pixel lower
   */
  public boolean standingOnWall(Wall w) {
    return w.touchingObj(x, y+1, width, height);
  }

  /**
   * Sets this Player's grounded status to the given boolean
   * @param grounded whether or not the player should be grounded
   */
  public void setGrounded(boolean grounded) {
    this.grounded = grounded;
  }
}
