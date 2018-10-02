import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A full level of 2D Portal, with all of its contents
 */
public class Level {

  // the Processing applet to use Processing functions
  PApplet p;
  // the width and height of the level's exit door
  private static final int doorSize = 50;
  // the number of this level
  private final int levelNum;
  // the sprite for the player
  private PImage playerSprite;
  // the sprite for the exit door
  private PImage doorSprite;
  // the sprite for cubes
  private PImage cubeSprite;
  // the sprite for unpressed floor buttons
  private PImage fbUnpressedSprite;
  // the sprite for pressed floor buttons
  private PImage fbPressedSprite;
  // the sprite for inactive pedestal buttons
  private PImage pbInactiveSprite;
  // the sprite for active pedestal buttons
  private PImage pbActiveSprite;
  // the x and y positions where the player starts this level
  private int startX, startY;
  // the x and y positions of the exit door
  private int doorX, doorY;
  // the x and y positions of the help node
  private int helpX, helpY;
  // the player object
  private Player player;
  // the portals that the player creates
  private Portal bluePortal, orangePortal;
  // the portal projectiles that the player shoots
  private Projectile blueProjectile, orangeProjectile;
  // the walls in this level
  private ArrayList<Wall> walls;
  // the cubes in this level
  private ArrayList<Cube> cubes;
  // the pedestal buttons in this level
  private ArrayList<PedestalButton> pButtons;
  // the floor buttons in this level
  private ArrayList<FloorButton> fButtons;
  // specific wall of this level being marked as hit by a portal projectile
  private Wall markedWall;

  /**
   * Constructs a new Level by loading the sprites, assigning variables, and putting all the
   * relevant objects in the level based on which level number this is
   *
   * @param levelNum   the number of this level
   * @param processing the Processing library
   * @throws IllegalArgumentException when attempting to make a Level with an invalid level number
   */
  public Level(int levelNum, PApplet processing) throws IllegalArgumentException {
    p = processing;
    loadImages(); // can this be moved to set up?
    this.levelNum = levelNum;
    switch (levelNum) {
      case 1:
        buildLevel1();
        break;
      case 2:
        buildLevel2();
        break;
      case 3:
        buildLevel3();
        break;
      case 4:
        buildLevel4();
        break;
      case 5:
        buildLevel5();
        break;
      case 6:
        buildLevel6();
        break;
      default:
        throw new IllegalArgumentException("That level number does not exist.");
    }
    player = new Player(startX, startY, p);
    bluePortal = new Portal(PortalColor.BLUE, p);
    orangePortal = new Portal(PortalColor.ORANGE, p);
    blueProjectile = new Projectile(PortalColor.BLUE, p);
    orangeProjectile = new Projectile(PortalColor.ORANGE, p);
    markedWall = new Wall(p);
  }

  /**
   * Loads images from the game's folder to use as sprites
   */
  public void loadImages() {
    playerSprite = p.loadImage("player.png");
    doorSprite = p.loadImage("door.png");
    cubeSprite = p.loadImage("cube.png");
    fbUnpressedSprite = p.loadImage("floorButton.png");
    fbPressedSprite = p.loadImage("floorButtonPressed.png");
    pbInactiveSprite = p.loadImage("pedestalButton.png");
    pbActiveSprite = p.loadImage("pedestalButtonPressed.png");

  }

  /**
   * Plays this level (runs every frame due to being called from Processing's draw() method).
   * Updates the state of every floor button, displays the level, moves the player, moves the cubes,
   * displays the level tip if the player is touching the help node, teleports the player and cubes
   * if they are touching a portal, moves projectiles and turns them into portals if they hit a
   * valid wall
   */
  public void play() {
    //CHECK FLOOR BUTTONS:
    for (FloorButton b : fButtons) {
      b.checkIfPressed(player, cubes);
    }

    // DISPLAY LEVEL:
    updateWalls();
    drawLevel();

    // MOVE PLAYER:
    player.processMovement();
    animate(player);

    // UPDATE WHETHER PLAYER IS GROUNDED OR FALLING:
    if (wallBelowPlayer() && (!player.hittingPortal(bluePortal) || !orangePortal.isOnscreen())
            && (!player.hittingPortal(orangePortal) || !bluePortal.isOnscreen())) {
      player.setGrounded(true);
    } else {
      player.setGrounded(false);
    }

    //MOVE CUBES:
    for (Cube c : cubes) {
      animate(c);
      player.moveCube(c);
    }

    //GET TIP:
    if (player.touchingHelp(helpX, helpY)) {
      displayTip();
    }

    //TELEPORT PLAYER:
    if (player.hittingPortal(bluePortal) && orangePortal.isOnscreen()) {
      player.teleport(bluePortal, orangePortal);
    } else if (player.hittingPortal(orangePortal) && bluePortal.isOnscreen()) {
      player.teleport(orangePortal, bluePortal);
    }

    //TELEPORT CUBES:
    for (Cube c : cubes) {
      if (!c.isHeld() && c.hittingPortal(bluePortal) && orangePortal.isOnscreen()) {
        c.teleport(bluePortal, orangePortal);
      } else if (!c.isHeld() && c.hittingPortal(orangePortal) && bluePortal.isOnscreen()) {
        c.teleport(orangePortal, bluePortal);
      }
    }

    //CONTROL PROJECTILES:
    //move and check collision once for every pixel the projectiles will move this tick:
    updateProjectile(blueProjectile, bluePortal, orangePortal);
    updateProjectile(orangeProjectile, orangePortal, bluePortal);

  }

  /**
   * Turns the level's toggle walls on or off depending on the state of their corresponding buttons
   */
  public void updateWalls() {
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    switch (levelNum) {
      case 4:
        i1 = indexOfWall("toggle wall 1");
        i2 = indexOfWall("toggle wall 2");

        // if pedestal button is active and its corresponding wall is on, turn it off:
        if (pButtons.get(0).isActive() && i1 != -1) {
          walls.remove(i1);
        }
        // if pedestal button is inactive and its corresponding wall is off, turn it on:
        else if (!pButtons.get(0).isActive() && i1 == -1) {
          walls.add(new Wall("toggle wall 1", 600, 380, 30, 190, WallType.TOGGLEABLE, p));
        }

        // if floor button is pressed and its corresponding wall is on, turn it off:
        if (fButtons.get(0).isPressed() && i2 != -1) {
          walls.remove(i2);
        }
        // if floor button is unpressed and its corresponding wall is off, turn it on:
        else if (!fButtons.get(0).isPressed() && i2 == -1) {
          walls.add(new Wall("toggle wall 2", 720, 380, 30, 190, WallType.TOGGLEABLE, p));
        }
        break;
      case 5:
        i1 = indexOfWall("left toggle wall");
        i2 = indexOfWall("middle toggle wall");
        i3 = indexOfWall("right toggle wall");
        i4 = indexOfWall("door access toggle wall");
        i5 = indexOfWall("cube toggle wall");

        // if left floor button is pressed and its corresponding wall is on, turn it off:
        if (fButtons.get(0).isPressed() && i1 != -1) {
          walls.remove(i1);
        }
        // if left floor button is unpressed and its corresponding wall is off, turn it on:
        else if (!fButtons.get(0).isPressed() && i1 == -1) {
          walls.add(new Wall("left toggle wall", 130, 30, 30, 120, WallType.TOGGLEABLE, p));
        }

        // if middle floor button is pressed and its corresponding wall is on, turn it off:
        if (fButtons.get(1).isPressed() && i2 != -1) {
          walls.remove(i2);
        }
        // if middle floor button is unpressed and its corresponding wall is off, turn it on:
        else if (!fButtons.get(1).isPressed() && i2 == -1) {
          walls.add(new Wall("middle toggle wall", 260, 30, 30, 120, WallType.TOGGLEABLE, p));
        }

        // if right floor button is pressed and its corresponding wall is on, turn it off:
        if (fButtons.get(2).isPressed() && i3 != -1) {
          walls.remove(i3);
        }
        // if right floor button is unpressed and its corresponding wall is off, turn it on:
        else if (!fButtons.get(2).isPressed() && i3 == -1) {
          walls.add(new Wall("right toggle wall", 390, 30, 30, 120, WallType.TOGGLEABLE, p));
        }

        // if left pedestal button is active and its corresponding wall is off, turn it on:
        if (pButtons.get(0).isActive() && i4 == -1) {
          walls.add(new Wall("door access toggle wall", 300, 480, 30, 89, WallType.TOGGLEABLE, p));
        }
        // if left pedestal button is inactive and its corresponding wall is on, turn it off:
        else if (!pButtons.get(0).isActive() && i4 != -1) {
          walls.remove(i4);
        }

        // if right pedestal button is active and its corresponding wall is on, turn it off:
        if (pButtons.get(1).isActive() && i5 != -1) {
          walls.remove(i5);
        }
        // if left pedestal button is inactive and its corresponding wall is off, turn it on:
        else if (!pButtons.get(1).isActive() && i5 == -1) {
          walls.add(new Wall("cube toggle wall", 620, 350, 349, 30, WallType.TOGGLEABLE, p));
        }
        break;
      case 6:
        i1 = indexOfWall("left toggle wall");
        i2 = indexOfWall("right toggle wall");

        // if floor button is pressed and its corresponding wall is on, turn it off:
        if (fButtons.get(0).isPressed() && i1 != -1) {
          walls.remove(i1);
        }
        // if floor button is unpressed and its corresponding wall is off, turn it on:
        else if (!fButtons.get(0).isPressed() && i1 == -1) {
          walls.add(new Wall("left toggle wall", 120, 431, 30, 138, WallType.TOGGLEABLE, p));
        }

        // if pedestal button is active and its corresponding wall is on, turn it off:
        if (pButtons.get(0).isActive() && i2 != -1) {
          walls.remove(i2);
        }
        // if pedestal button is inactive and its corresponding wall is off, turn it on:
        else if (!pButtons.get(0).isActive() && i2 == -1) {
          walls.add(new Wall("right toggle wall", 850, 31, 30, 118, WallType.TOGGLEABLE, p));
        }

    }
  }

  /**
   * Returns the index of the wall in the level with the given name, if it is present. NOTE: all
   * wall names are unique in every distinct level
   *
   * @param name the name being searched for
   * @return the index of the found wall, or -1 if it was not found
   */
  public int indexOfWall(String name) {
    for (int i = 0; i < walls.size(); i++) {
      if (walls.get(i).getName().equals(name)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Displays this level for the user. Draws the walls, door, floor buttons, player, cubes, pedestal
   * buttons, portals, help node, then portal projectiles, in that order.
   */
  public void drawLevel() {
    // WALLS:
    for (Wall w : walls) {
      w.draw();
    }

    // DOOR:
    p.stroke(0);
    p.image(doorSprite, doorX, doorY, doorSize, doorSize);

    // FLOOR BUTTONS:
    for (FloorButton b : fButtons) {
      b.draw(fbUnpressedSprite, fbPressedSprite);
    }

    // PLAYER:
    player.draw(playerSprite);

    // CUBES:
    for (Cube c : cubes) {
      c.draw(cubeSprite);
    }

    // PEDESTAL BUTTONS:
    for (PedestalButton b : pButtons) {
      b.draw(pbInactiveSprite, pbActiveSprite);
    }

    // PORTALS:
    bluePortal.draw();
    orangePortal.draw();

    // HELP NODE:
    p.fill(255, 0, 0);
    p.textSize(50);
    p.text('?', helpX, helpY);

    //PORTAL PROJECTILES:
    blueProjectile.draw();
    orangeProjectile.draw();
  }

  /**
   * Animates the given object by updating their x and y values while preventing them from
   * penetrating any of this level's walls
   *
   * @param obj the physics-enabled object being animated
   */
  public void animate(PhysicsObj obj) {
    // HORIZONTAL ANIMATION:
    obj.updateX();
    if (hittingWall(obj)) {
      while (hittingWall(obj)) {
        obj.backUpX();
      }
      obj.resetVX();
    }

    // VERTICAL ANIMATION:
    obj.updateY();

    if (hittingWall(obj)) {
      while (hittingWall(obj)) {
        obj.backUpY();
      }
      //obj.ground();
      obj.resetVY();
    }
  }

  /**
   * Returns true if the given object is touching any of this level's walls
   *
   * @param obj the physics-enabled object being tested
   * @return whether or not the object is touching any walls
   */
  public boolean hittingWall(PhysicsObj obj) {
    if (bluePortal.isOnscreen() && orangePortal.isOnscreen()
            && (obj.hittingPortal(bluePortal) || obj.hittingPortal(orangePortal))) {
      return false;
    }

    for (Wall w : walls) {
      if (obj.hittingWall(w)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true when there is a Wall below the Player in this Level
   * @return true if the Player of this Level is standing on any of the Walls in this Level
   */
  public boolean wallBelowPlayer() {
    for (Wall w : walls) {
      if (player.standingOnWall(w)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Displays this level's help box which contains a gameplay tip
   */
  public void displayTip() {
    // DRAW BOX FOR TEXT:
    p.stroke(255, 153, 0);
    p.fill(50, 200);
    p.rectMode(p.CENTER);
    switch (levelNum) {
      case 1:
        p.rect(helpX, helpY - 70, 300, 100);
        break;
      case 2:
        p.rect(helpX, helpY - 70, 220, 100);
        break;
      case 3:
        p.rect(helpX, helpY - 100, 270, 120);
        break;
      case 4:
        p.rect(helpX, helpY - 70, 270, 100);
        break;
      case 5:
        p.rect(helpX, helpY - 70, 250, 90);
        break;
      case 6:
        p.rect(helpX, helpY - 100, 270, 120);
    }

    // DISPLAY HELP TEXT:
    p.fill(255);
    p.stroke(0);
    p.textSize(14);
    switch (levelNum) {
      case 1:
        p.text("LMB to shoot blue portal, RMB to shoot yellow portal." +
                        "Get to the exit door to advance to the next level!",
                helpX, helpY - 70, 300, 100);
        break;
      case 2:
        p.text("You can shoot portals onto all black walls." +
                        "You cannot make any portals on white walls!",
                helpX, helpY - 70, 220, 100);
        break;
      case 3:
        p.text("Your momentum and acceleration are retained through portals. " +
                        "Use this to your advantage to launch yourself further and solve puzzles!",
                helpX, helpY - 100, 250, 130);
        break;

      case 4:
        // ideally divide this level into four different question marks
        // (tips for cubes, floor buttons, pedestal buttons, and blue walls)
        p.text("MMB or 'E' key to pickup/drop the cube, or to activate pedestal buttons." +
                        "Floor buttons must be held down to retain their effect.",
                helpX, helpY - 70, 260, 100);
        break;
      case 5:
        p.text("If you are stuck on a level, press 'Backspace' to return to the main menu.",
                helpX, helpY - 70, 260, 100);
        break;
      case 6:
        p.text("Don't forget that sometimes you need to enter a portal with as much acceleration " +
                        "as possible in order to really fly coming out the other end.",
                helpX, helpY - 100, 250, 130);
    }

    //RESET RECTANGLE MODE:
    p.rectMode(p.CORNER);
  }

  /**
   * Moves the given projectile. If it hits a wall, then turn it into a portal if the wall is portal
   * friendly and has enough room.
   *
   * @param proj        the projectile to be updated
   * @param portal      the portal of the same color as the given projectile
   * @param otherPortal the portal of different color than the given projectile
   */
  public void updateProjectile(Projectile proj, Portal portal, Portal otherPortal) {
    for (int i = 0; i < Projectile.getSpeed(); i++) {
      proj.move();
      if (projectileHitsWall(proj)) {
        proj.moveToWallEdge(markedWall);
        try {
          if (proj.canMakePortal(markedWall, otherPortal)) {
            if (portal.getColor() == PortalColor.BLUE) {
              bluePortal = proj.createPortal(markedWall);
              proj.cancelShot();
            } else {
              orangePortal = proj.createPortal(markedWall);
              proj.cancelShot();
            }
          } else {
            proj.cancelShot();
          }
          break; // so that projectile does not keep moving this frame after hitting a wall
        }
        // if portal orientation cannot be determined (caused by a shot colliding with a Wall's
        // corner pixel), cancel the shot:
        catch (RuntimeException noOrientationFound){
          proj.cancelShot();
        }
      }
    }

  }

  /**
   * Returns true if the given projectile is touching a wall in this level, and stores that wall in
   * markedWall
   *
   * @param proj the projectile being tested
   * @return whether or not the projectile is hitting a wall
   */
  public boolean projectileHitsWall(Projectile proj) {
    //p.println("projectile hit wall!");
    for (Wall w : walls) {
      if (proj.hitsWall(w)) {
        markedWall = w;
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if the Player is at the exit door
   *
   * @return whether or not the Player is at the exit door
   */
  public boolean wonLevel() {
    return player.leavingLevel(doorX, doorY, doorSize);
  }

  /**
   * Returns true if the Player lost the Level
   * @return whether or not the Player is falling out of the Level
   */
  public boolean lostLevel() {
    return player.fallingOffLevel();
  }

  /**
   * Stops the player when they reach the exit door, and opens the door
   */
  public void exitDoor() {
    p.fill(0);
    p.rect(doorX, doorY, doorSize, doorSize);
    player.resetVX();
    player.resetVY();
    player.stopLeft();
    player.stopRight();
  }

  /**
   * Moves the player to the left or right, or jumps, depending on the given MovementType
   *
   * @param type the type of movement to be performed: left, right, or jump
   */
  public void movePlayer(MovementType type) {
    switch (type) {
      case LEFT:
        player.moveLeft();
        break;
      case RIGHT:
        player.moveRight();
        break;
      case JUMP:
        player.jump();
    }
  }

  /**
   * Stops the player from moving left or right, depending on the given MovementType
   *
   * @param type the type of movement to be stopped: left or right
   */
  public void stopPlayer(MovementType type) {
    switch (type) {
      case LEFT:
        player.stopLeft();
        break;
      case RIGHT:
        player.stopRight();
    }
  }

  /**
   * Fires a portal projectile -- blue if the left mouse button is being pressed and orange if the
   * right mouse button is being pressed.
   */
  public void fireProjectile() {
    if (p.mouseButton == p.LEFT) {
      player.fireProjectile(blueProjectile);
    }
    if (p.mouseButton == p.RIGHT) {
      player.fireProjectile(orangeProjectile);
    }
  }

  /**
   * Performs the player's action button operations -- picks up any cubes they are touching or
   * activates any pedestal buttons they are touching
   */
  public void actionButton() {
    for (Cube c : cubes) {
      if (player.touchingCube(c)) {
        player.cubeInteraction(c);
      }
    }
    for (PedestalButton b : pButtons) {
      if (player.touchingPButton(b)) {
        b.toggle();
      }
    }
  }

  /**
   * Returns the number of this Level
   *
   * @return the level number
   */
  public int getLevelNum() {
    return levelNum;
  }

  /**
   * Builds this Level according to level 1's design, by creating all of its walls, buttons, and
   * cubes, and setting the player's start position as well as the positions of the exit door and
   * help node
   */
  public void buildLevel1() {
    startX = 150;
    startY = 539;
    doorX = 800;
    doorY = 100;
    helpX = 350;
    helpY = 540;
    walls = new ArrayList<Wall>();
    walls.add(new Wall("floor", 0, 570, 1000, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("ceiling", 0, 0, 1000, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left wall", 0, 0, 30, 600, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("right wall", 970, 0, 30, 600, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("bottom left platform", 0, 350, 600, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("bottom right platform", 700, 350, 300, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("top left platform", 0, 150, 250, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("top right platform", 350, 150, 700, 30, WallType.PORTAL_FRIENDLY, p));
    cubes = new ArrayList<Cube>();
    pButtons = new ArrayList<PedestalButton>();
    fButtons = new ArrayList<FloorButton>();
  }

  /**
   * Builds this Level according to level 2's design, by creating all of its walls, buttons, and
   * cubes, and setting the player's start position as well as the positions of the exit door and
   * help node
   */
  public void buildLevel2() {
    startX = 100;
    startY = 539;
    doorX = 100;
    doorY = 150;
    helpX = 412;
    helpY = 540;
    walls = new ArrayList<Wall>();
    walls.add(new Wall("floor", 0, 570, 1000, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("ceiling", 0, 0, 800, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("good ceiling", 800, 0, 200, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left wall", 0, 0, 30, 600, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("right wall", 970, 0, 30, 600, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("door platform", 0, 200, 400, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("stalagmite 1", 200, 400, 30, 250, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("stalagmite 2", 550, 400, 30, 250, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("black stalactite", 400, 200, 30, 200, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("white stalactite 1", 400, 0, 30, 160, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("hovering wall", 700, 200, 30, 200, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("white stalactite 2", 700, 0, 30, 160, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("small right platform", 900, 200, 70, 30, WallType.PORTAL_FRIENDLY, p));
    cubes = new ArrayList<Cube>();
    pButtons = new ArrayList<PedestalButton>();
    fButtons = new ArrayList<FloorButton>();
  }

  /**
   * Builds this Level according to level 3's design, by creating all of its walls, buttons, and
   * cubes, and setting the player's start position as well as the positions of the exit door and
   * help node
   */
  public void buildLevel3() {
    startX = 400;
    startY = 539;
    doorX = 270;
    doorY = 150;
    helpX = 200;
    helpY = 540;
    walls = new ArrayList<Wall>();
    walls.add(new Wall("left floor", 0, 570, 550, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("right floor", 550, 570, 450, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling", 0, 0, 750, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("right ceiling", 750, 0, 250, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("left wall", 0, 31, 30, 538, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("right wall", 970, 31, 30, 600, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("pedestal 1", 550, 500, 30, 200, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("pedestal 2", 650, 400, 30, 300, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("pedestal 3", 750, 275, 30, 400, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("pedestal 4", 850, 160, 30, 500, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("door platform", 210, 200, 150, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("white barricade", 600, 101, 30, 220, WallType.PORTAL_RESISTANT, p));
    cubes = new ArrayList<Cube>();
    pButtons = new ArrayList<PedestalButton>();
    fButtons = new ArrayList<FloorButton>();
  }

  /**
   * Builds this Level according to level 4's design, by creating all of its walls, buttons, and
   * cubes, and setting the player's start position as well as the positions of the exit door and
   * help node
   */
  public void buildLevel4() {
    startX = 120;
    startY = 539;
    doorX = 850;
    doorY = 520;
    helpX = 360;
    helpY = 540;
    walls = new ArrayList<Wall>();
    walls.add(new Wall("floor", 0, 570, 1000, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling", 0, 0, 400, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("right ceiling", 400, 0, 600, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("left wall", 0, 0, 30, 600, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("right wall", 970, 0, 30, 600, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("central divider", 485, 150, 30, 230, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("top left platform", 0, 150, 250, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("bottom left platform", 0, 350, 250, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("top right platform", 516, 150, 310, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("bottom right platform", 516, 350, 500, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("toggle wall 1", 600, 380, 30, 190, WallType.TOGGLEABLE, p));
    walls.add(new Wall("toggle wall 2", 720, 380, 30, 190, WallType.TOGGLEABLE, p));
    cubes = new ArrayList<Cube>();
    cubes.add(new Cube(100, 324, p));
    pButtons = new ArrayList<PedestalButton>();
    pButtons.add(new PedestalButton(560, 320, p));
    fButtons = new ArrayList<FloorButton>();
    fButtons.add(new FloorButton(600, 140, p));
  }

  /**
   * Builds this Level according to level 5's design, by creating all of its walls, buttons, and
   * cubes, and setting the player's start position as well as the positions of the exit door and
   * help node
   */
  public void buildLevel5() {
    startX = 797;
    startY = 539;
    doorX = 130;
    doorY = 520;
    helpX = 500;
    helpY = 540;
    walls = new ArrayList<Wall>();
    walls.add(new Wall("left floor", 0, 570, 300, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("right floor", 300, 570, 700, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling segment 1", 0, 0, 130, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling segment 2", 130, 0, 30, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling segment 3", 160, 0, 100, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling segment 4", 260, 0, 30, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling segment 5", 290, 0, 100, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling segment 6", 390, 0, 30, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left ceiling segment 7", 420, 0, 80, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("right ceiling", 500, 0, 500, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("upper left wall", 0, 0, 30, 150, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("lower left wall", 0, 150, 30, 450, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("right wall", 970, 0, 30, 569, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("left toggle wall", 130, 30, 30, 120, WallType.TOGGLEABLE, p));
    walls.add(new Wall("middle toggle wall", 260, 30, 30, 120, WallType.TOGGLEABLE, p));
    walls.add(new Wall("right toggle wall", 390, 30, 30, 120, WallType.TOGGLEABLE, p));
    walls.add(new Wall("left platform", 0, 150, 420, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("right platform", 650, 260, 319, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("cube toggle wall", 620, 350, 349, 30, WallType.TOGGLEABLE, p));
    walls.add(new Wall("cube barricade", 620, 260, 30, 90, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("center platform", 490, 320, 70, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("door barricade", 270, 400, 30, 169, WallType.PORTAL_RESISTANT, p));
    cubes = new ArrayList<Cube>();
    cubes.add(new Cube(800, 324, p)); // right cube
    cubes.add(new Cube(330, 124, p)); // left cube
    pButtons = new ArrayList<PedestalButton>();
    pButtons.add(new PedestalButton(80, 120, p)); // left pb
    pButtons.add(new PedestalButton(210, 120, p)); // right pb
    fButtons = new ArrayList<FloorButton>();
    fButtons.add(new FloorButton(700, 250, p)); // left
    fButtons.add(new FloorButton(800, 250, p)); // middle
    fButtons.add(new FloorButton(900, 250, p)); // right
  }

  /**
   * Builds this Level according to level 6's design, by creating all of its walls, buttons, and
   * cubes, and setting the player's start position as well as the positions of the exit door and
   * help node
   */
  public void buildLevel6() {
    startX = 900;
    startY = 369;
    doorX = 50;
    doorY = 520;
    helpX = 700;
    helpY = 530;
    walls = new ArrayList<Wall>();
    walls.add(new Wall("floor", 0, 570, 1000, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("ceiling", 0, 0, 1000, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("upper left wall", 0, 0, 30, 180, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("lower left wall", 0, 181, 30, 420, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("upper right wall", 970, 0, 30, 180, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("lower right wall", 970, 181, 30, 420, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("upper left platform", 30, 150, 120, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("upper right platform", 850, 150, 120, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("lower left platform", 30, 400, 120, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("lower right platform", 850, 400, 120, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left toggle wall", 120, 431, 30, 138, WallType.TOGGLEABLE, p));
    walls.add(new Wall("right toggle wall", 850, 31, 30, 118, WallType.TOGGLEABLE, p));
    walls.add(new Wall("top center platform", 460, 100, 80, 30, WallType.PORTAL_RESISTANT, p));
    walls.add(new Wall("bottom center platform", 460, 131, 80, 30, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("left stalactite", 300, 31, 30, 100, WallType.PORTAL_FRIENDLY, p));
    walls.add(new Wall("right stalactite", 670, 31, 30, 100, WallType.PORTAL_FRIENDLY, p));
    cubes = new ArrayList<Cube>();
    cubes.add(new Cube(915, 124, p));
    pButtons = new ArrayList<PedestalButton>();
    pButtons.add(new PedestalButton(80, 120, p));
    fButtons = new ArrayList<FloorButton>();
    fButtons.add(new FloorButton(475, 90, p));
  }
}
