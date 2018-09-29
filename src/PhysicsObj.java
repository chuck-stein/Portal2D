/**
 * Represents physics-enabled objects in the game, meaning they can move, teleport,
 * and be stopped by walls
 */
public interface PhysicsObj extends CollisionObj {

  /**
   * Updates this PhysicsObj's x value according to its x velocity, applying friction to smooth
   * the movement
   */
  void updateX();

  /**
   * Updates this PhysicsObj's y value according to its y velocity, and handles vertical
   * acceleration while in the air
   */
  void updateY();

  /**
   * Moves this PhysicObj one pixel in the opposite direction of its x velocity
   */
  void backUpX();

  /**
   * Moves this PhysicObj one pixel in the opposite direction of its y velocity
   */
  void backUpY();

  /**
   * Sets this PhysicsObj's x velocity to zero
   */
  void resetVX();

  /**
   * Sets this PhysicsObj's y velocity to zero
   */
  void resetVY();

  /**
   * Returns true if this PhysicsObj is touching the given Wall
   * @param w the wall to be checked
   * @return whether or not the PhysicsObj is touching the wall
   */
  boolean hittingWall(Wall w);

  /**
   * Returns true if this PhysicsObj is touching the given Portal
   * @param portal the portal to be checked
   * @return whether or not the PhysicsObj is touching the portal
   */
  boolean hittingPortal(Portal portal);

  /**
   * Teleports this PhysicsObject from the given entrance portal to the given exit portal
   * @param pIn the portal being entered
   * @param pOut the portal where the object will exit
   */
  void teleport(Portal pIn, Portal pOut);

  /**
   * Sets this PhysicsObj to be leaving a portal at the given coordinates, by placing it slightly
   * outside the portal and modifying its x and y velocities based on the given velocities that it
   * entered the other portal with
   * @param inV the velocity the object had in the same direction the entrance portal was facing
   * @param crossV the velocity the object had in the direction perpendicular to the direction
   *               the entrance portal was facing
   * @param orientation the orientation of the portal that the object is exiting from
   * @param portalX the x coordinate of the portal that the object is leaving from
   * @param portalY the y coordinate of the portal that the object is leaving from
   */
  void exitPortal(float inV, float crossV, PortalOrientation orientation, float portalX,
                  float portalY);

}
