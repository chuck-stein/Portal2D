/**
 * Represents portals and portal projectiles
 */
public abstract class AbstractPortalObj extends AbstractCollisionObj{
  // whether or not this PortalObj is currently present/should be displayed
  protected boolean onScreen;
  // the color of this PortalObj, blue or orange
  protected PortalColor color;

  /**
   * Draws this PortalObj as an ellipse of its color at its coordinates if it is onScreen
   */
  public void draw() {
    if (onScreen) {
      p.stroke(0);
      if (color == PortalColor.BLUE) {
        p.fill(0, 204, 255);
      } else {
        p.fill(255, 153, 0);
      }
      p.ellipse(x, y, width, height);
    }
  }

}
