import processing.core.PApplet;

/**
 * Represents objects in the game which can be touching one another, with some general fields and
 * implementations of their methods
 */
public abstract class AbstractCollisionObj implements CollisionObj {
  // the Processing applet to use Processing functions
  protected PApplet p;
  // the x and y values of the CollisionObj
  protected float x, y;
  // the width and height of the CollisionObj
  protected int width, height;

  @Override
  public boolean touchingObj(float objX, float objY, int objWidth, int objHeight) {
    boolean rightPastLeft = objX + objWidth >= x;
    boolean leftPastRight = objX <= x + width;
    boolean bottomPastTop = objY + objHeight >= y;
    boolean topPastBottom = objY <= y + height;
    return rightPastLeft && leftPastRight && bottomPastTop && topPastBottom;
  }

}
