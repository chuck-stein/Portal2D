/**
 * Represents objects in the game which can be touching one another
 */
public interface CollisionObj {

  /**
   * Returns true if any part of this CollisionObj is within the space indicated by the given x,
   * y, width, and height
   * @param objX the x value of the object testing for collision with this one
   * @param objY the y value of the object testing for collision with this one
   * @param objWidth the width of the object testing for collision with this one
   * @param objHeight the height of the object testing for collision with this one
   * @return whether or not this object is touching an object of the given x, y, width, and height
   */
  boolean touchingObj(float objX, float objY, int objWidth, int objHeight);

 // boolean touchingObj(CollisionObj obj);

}
