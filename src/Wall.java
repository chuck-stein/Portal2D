import processing.core.PApplet;

/**
 * A Wall in a game level, which can stop the movement of PhysicsObjs and can host Portals if it is
 * portal-friendly
 */
public class Wall extends AbstractCollisionObj {
  // the name of this wall, to easily distinguish its location and/or function in the level
  private String name;
  // this wall's type: portal-friendly, portal-resistant, or toggleable
  private WallType type;

  /**
   * Constructs a new Wall at the given coordinates with the given name, width, height, and type,
   * which uses the given Processing libray
   *
   * @param name       the name of the Wall
   * @param x          the x coordinate of the Wall
   * @param y          the y coordinate of the Wall
   * @param width      the width of the Wall
   * @param height     the height of the Wall
   * @param type       the type of the Wall
   * @param processing the Processing library
   */
  public Wall(String name, float x, float y, int width, int height, WallType type,
              PApplet processing) {
    p = processing;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.name = name;
    this.type = type;
  }

  /**
   * Dummy constructor to create a placeholder Wall for objects that need a Wall, but which one in
   * the Level has yet to be determined. Uses the given Processing library
   *
   * @param processing the Processing library
   */
  public Wall(PApplet processing) {
    p = processing;
    this.x = -1;
    this.y = -1;
    this.width = 0;
    this.height = 0;
    this.type = WallType.PORTAL_RESISTANT;
    this.name = "PLACEHOLDER - not actually in the level";
  }

  /**
   * Draws this wall as a rectangle which is black if portal-friendly, white if portal-resistant,
   * and blue if toggleable
   */
  public void draw() {
    switch (type) {
      case PORTAL_FRIENDLY:
        p.stroke(0);
        p.fill(0);
        break;
      case PORTAL_RESISTANT:
        p.stroke(255);
        p.fill(255);
        break;
      case TOGGLEABLE:
        p.stroke(0, 0, 255);
        p.fill(0, 204, 255);
        break;
    }
    p.rect(x, y, width, height);
  }

  /**
   * Sets the X and Y coordinates of the given Projectile to be on the nearest edge of this wall
   * to the given previous coordinates of that Projectile
   * @param oldX the previous X coordinate of the Projectile to be moved
   * @param oldY the previous Y coordinate of the Projectile to be moved
   * @param proj the Projectile to be moved
   */
  public void roundProjectileToEdge(float oldX, float oldY, Projectile proj) {
    float distToTopEdge = p.dist(oldX, oldY, oldX, y);
    float distToBottomEdge = p.dist(oldX, oldY, oldX, y + height);
    float distToLeftEdge = p.dist(oldX, oldY, x, oldY);
    float distToRightEdge = p.dist(oldX, oldY, x + width, oldY);
    float minDist = Math.min(Math.min(distToTopEdge, distToBottomEdge), Math.min(distToLeftEdge,
            distToRightEdge));
    if (minDist == distToTopEdge) {
      proj.setY(y);
    } else if (minDist == distToBottomEdge) {
      proj.setY(y + height);
    } else if (minDist == distToLeftEdge) {
      proj.setX(x);
    } else if (minDist == distToRightEdge) {
      proj.setX(x + width);
    }
  }

  /**
   * Returns the orientation that a Portal at the given coordinates on this Wall would be, based on
   * which direction of those coordinates holds open space, and which three directions are blocked
   * by this Wall (since Portals can only be facing open space). The distance in each direction
   * to be checked for open space is 1 pixel
   *
   * @param portalX the x coordinate of the Portal whose orientation is being determined
   * @param portalY the y coordinate of the Portal whose orientation is being determined
   * @return the orientation of the Portal to be created on this Wall at the given coordinates
   * @throws RuntimeException if the orientation could not be determined
   */
  public PortalOrientation findPortalOrientation(int portalX, int portalY)
          throws RuntimeException {
    int checkDist = 1;
    boolean notRight = false;
    boolean notLeft = false;
    boolean notBottom = false;
    boolean notTop = false;
    // is the wall blocking the bottom of the portal?:
    if (portalX >= x && portalX <= x + width && portalY + checkDist >= y
            && portalY + checkDist <= y + height) {
      notBottom = true;
    }
    // is the wall blocking the top of the portal?:
    if (portalX >= x && portalX <= x + width && portalY - checkDist >= y
            && portalY - checkDist <= y + height) {
      notTop = true;
    }
    // is the wall blocking the portal to the right?:
    if (portalX + checkDist >= x && portalX + checkDist <= x + width
            && portalY >= y && portalY <= y + height) {
      notRight = true;
    }
    // is the wall blocking the portal to the left?:
    if (portalX - checkDist >= x && portalX - checkDist <= x + width
            && portalY >= y && portalY <= y + height) {
      notLeft = true;
    }
    if (notRight && notBottom && notTop) {
      return PortalOrientation.FROM_LEFT;
    }
    if (notLeft && notBottom && notTop) {
      return PortalOrientation.FROM_RIGHT;
    }
    if (notLeft && notRight && notTop) {
      return PortalOrientation.FROM_BOTTOM;
    }
    if (notLeft && notRight && notBottom) {
      return PortalOrientation.FROM_TOP;
    }
    throw new RuntimeException("Portal orientation could not be determined!");
  }

  /**
   * Returns true if this Wall is portal-friendly
   *
   * @return whether or not this Wall is portal-friendly
   */
  public boolean isPortalFriendly() {
    return type == WallType.PORTAL_FRIENDLY;
  }

  /**
   * Returns true if there is enough space on this Wall to fit a Portal of the given orientation and
   * coordinates. If there is enough space in one direction but not the other, then sets the given
   * Projectile's coordinates to the closest place on this Wall where there is space and returns
   * true
   *
   * @param orientation the orientation of the Portal being checked for room
   * @param originX     the x coordinate of the Portal being checked for room
   * @param originY     the y coordinate of the Portal being checked for room
   * @param proj        the Projectile which is making the Portal being checked for room
   * @return whether both edges of the Portal being checked can fit on this wall
   */
  public boolean hasPortalRoom(PortalOrientation orientation, float originX, float originY,
                               Projectile proj) {
    boolean firstEdgeSpace = false;
    boolean secondEdgeSpace = false;
    if (orientation == PortalOrientation.FROM_BOTTOM || orientation == PortalOrientation.FROM_TOP) {
      if (originX - 40 >= x && originX - 40 <= x + width && originY >= y && originY <= y + height) {
        firstEdgeSpace = true;
      }
      if (originX + 40 >= x && originX + 40 <= x + width && originY >= y && originY <= y + height) {
        secondEdgeSpace = true;
      }
    } else { //FROMLEFT or FROMRIGHT
      if (originX >= x && originX <= x + width && originY - 40 >= y && originY - 40 <= y + height) {
        firstEdgeSpace = true;
      }
      if (originX >= x && originX <= x + width && originY + 40 >= y && originY + 40 <= y + height) {
        secondEdgeSpace = true;
      }
    }
    if (firstEdgeSpace && secondEdgeSpace) {
      return true;
    } else if (firstEdgeSpace) {
      if (orientation == PortalOrientation.FROM_BOTTOM
              || orientation == PortalOrientation.FROM_TOP) {
        // move the projectile to the closest position to the left
        // where its portal won't be hanging off to the right:
        proj.setX(x + width - 40);
      } else {
        // move the projectile to the closest position above
        // where its portal won't be hanging off the bottom:
        proj.setY(y + height - 40);
      }
      return true;
    } else if (secondEdgeSpace) {
      if (orientation == PortalOrientation.FROM_BOTTOM
              || orientation == PortalOrientation.FROM_TOP) {
        // move the projectile to the closest position to the right
        // where its portal won't be hanging off to the left:
        proj.setX(x + 40);
      } else {
        // move the projectile to the closest position below
        // where its portal won't be hanging off the top:
        proj.setY(y + 40);
      }
      return true;
    }
    return false;
  }

  /**
   * Returns the name of this Wall
   *
   * @return the name of this Wall
   */
  public String getName() {
    return name;
  }

}
