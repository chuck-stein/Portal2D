/**
 * Represents physics-enabled objects in the game, with some general fields and implementations of
 * their methods
 */
public abstract class AbstractPhysicsObj extends AbstractCollisionObj implements PhysicsObj {

  // the maximum speed any PhysicsObj can reach in freefall
  private static final int TERMINAL_VELOCITY = 30;
  // the x and y velocities of the PhysicsObj
  protected float vx, vy;
  // the friction multiplier for the PhysicsObj,
  // a.k.a how much it glides after initial movement is stopped
  protected float friction;

  @Override
  public void updateX() {
    x += vx;
    vx *= friction;
    if (Math.abs(vx) < 0.5) vx = 0; // so that velocity doesn't get infinitely close to 0 forever
  }

  @Override
  public void updateY() {

    //falling = true;

    y += vy;
    vy += 1; // this accounts for acceleration while in the air
    if (vy > TERMINAL_VELOCITY) vy = TERMINAL_VELOCITY;
  }

  @Override
  public void backUpX() {
    if (vx < 0) {
      x += 1;
    } else if (vx > 0) {
      x -= 1;
    }
  }

  @Override
  public void backUpY() {
    //p.println("Y: " + y);
    if (vy < 0) {
      y += 1;
    } else if (vy > 0) {
      y -= 1;
    }
  }

  @Override
  public void resetVX() {
    vx = 0;
  }

  @Override
  public void resetVY() {
    // if positive vy then falling is false expression used to be here
    vy = 0;
  }

  @Override
  public boolean hittingWall(Wall w) {
    return w.touchingObj(x, y, width, height);
  }

  @Override
  public boolean hittingPortal(Portal portal) {
    return portal.objEntering(x, y, width, height);
  }

  @Override
  public void teleport(Portal pIn, Portal pOut) {
    pIn.transport(this, pOut, vx, vy);
  }


}
