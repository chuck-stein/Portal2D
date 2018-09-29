import processing.core.PApplet;

/**
 * An orange or blue portal on a Wall which can teleport physics-enabled objects
 */
public class Portal extends AbstractPortalObj {

  // the position in which this Portal is oriented on its Wall
  private PortalOrientation orientation;
  // the wall which this Portal is on
  private Wall wall;

  /**
   * Constructs a new bottom-oriented 0x0 offscreen Portal at (-1, -1) of the given color on a
   * placeholder Wall, which uses the  Processing library
   * @param color the color of the Portal, blue or orange
   * @param processing the Processing library
   */
  public Portal(PortalColor color, PApplet processing) {
    p = processing;
    x = -1;
    y = -1;
    width = 0;
    height = 0;
    onScreen = false;
    this.color = color;
    orientation = PortalOrientation.FROM_BOTTOM;
    wall = new Wall(p);
  }

  /**
   * Constructs a new Portal of the given color and orientation at the given coordinates on the
   * given Wall, which uses the given Processing library. Sets its width and height to 80x10 if it
   * is bottom- or top-oriented, or to 10x80 if it is left- or right-oriented
   * @param color the color of the Portal, blue or orange
   * @param x the x coordinate of the Portal
   * @param y the y coordinate of the Portal
   * @param orientation the orientation of the Portal
   * @param w the Wall which the Portal is on
   * @param processing the Processing library
   */
  public Portal(PortalColor color, float x, float y, PortalOrientation orientation, Wall w,
                PApplet processing) {
    p = processing;
    this.x = x;
    this.y = y;
    if (orientation == PortalOrientation.FROM_BOTTOM || orientation == PortalOrientation.FROM_TOP) {
      width = 80;
      height = 10;
    } else {
      width = 10;
      height = 80;
    }
    this.orientation = orientation;
    wall = w;
    onScreen = true;
    this.color = color;
  }

  /**
   * Returns true if the space indicated by the given x, y, width, and height is
   * fully within this Portal lengthwise, as well as touching this Portal
   * @param objX the x value of the object testing for entrance
   * @param objY the y value of the object testing for entrance
   * @param objWidth the width of the object testing for entrance
   * @param objHeight the height of the object testing for entrance
   * @return whether or not this Portal is being entered by an object of the given x, y, width, and
   * height
   */
  public boolean objEntering(float objX, float objY, int objWidth, int objHeight) {
    if (orientation == PortalOrientation.FROM_LEFT || orientation == PortalOrientation.FROM_RIGHT) {
      return objX + objWidth >= x - width / 2 && objX <= x + width / 2
              && objY + objHeight <= y + height/2 && objY >= y - height/2;
    }
    //OTHERWISE FROM_TOP or FROM_BOTTOM:
    return objX + objWidth <= x + width/2 && objX >= x - width/2
            && objY + objHeight >= y - height / 2 && objY <= y + height / 2;
  }

  /**
   * Transports the given PhysicsObj to the given exit Portal, with an in-velocity and
   * cross-velocity set according to this Portal's orientation and PhysicsObj's given velocities
   * @param obj the PhysicsObj to be transported
   * @param exit the Portal to send the PhysicsObj to
   * @param vx the x velocity of the PhysicsObj to be transported
   * @param vy the y velocity of the PhysicsObj to be transported
   */
  public void transport(PhysicsObj obj, Portal exit, float vx, float vy) {
    float inV = 0; // velocity directed into the portal
    float crossV = 0; // velocity directed perpindicular to the portal
    switch (orientation) {
      case FROM_LEFT:
        inV = vx;
        crossV = -vy;
        break;
      case FROM_RIGHT:
        inV = -vx;
        crossV = vy;
        break;
      case FROM_TOP:
        inV = vy;
        crossV = vx;
        break;
      case FROM_BOTTOM:
        inV = -vy;
        crossV = -vx;
        break;
    }

    exit.receive(obj, inV, crossV);

  }

  /**
   * Receives the given PhysicsObj by moving it to this Portal, maintaining its relative velocities
   * @param obj the PhysicsObj to be received
   * @param inV the velocity of the PhysicsObj in the direction facing the Portal which
   *            transported it to this one, at the time that it entered that Portal
   * @param crossV the velocity of the PhysicsObj in the direction perpendicular to which
   *               the Portal that transported the object was facing, at the time that it entered
   *               that Portal
   */
  public void receive(PhysicsObj obj, float inV, float crossV) {
    obj.exitPortal(inV, crossV, orientation, x, y);
  }

  /**
   * Returns the color of this Portal: blue or orange
   * @return the color of this Portal
   */
  public PortalColor getColor() {
    return color;
  }

  /**
   * Returns true if this Portal is currently onScreen
   * @return whether or not the Portal is onScreen
   */
  public boolean isOnscreen() {
    return onScreen;
  }

}
