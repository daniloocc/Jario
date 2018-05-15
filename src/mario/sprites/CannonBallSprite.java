package mario.sprites;


/* 
*/

import java.awt.*;

import mario.basics.Sprite;
import mario.loaders.ImagesLoader;
import mario.panels.MarioPanel1;



public class CannonBallSprite extends Sprite
{
  // the ball's x- and y- step values are STEP +/- STEP_OFFSET
  private static final int STEP = -10;   // moving left
  private static final int STEP_OFFSET = 2;

  private MarioPanel1 mp;    // tell JackPanel about colliding with jack
  private MarioSprite mario;


  public CannonBallSprite(int w, int h, ImagesLoader imsLd,
                               MarioPanel1 mp, MarioSprite m) 
  { super( w, h/2, w, h, imsLd, "cannonball");  
        // the ball is positioned in the middle at the panel's rhs
    this.mp = mp;
    mario = m;
    initPosition();
  } // end of FireBallSprite()


  private void initPosition()
  // adjust the fireball's position and its movement left
  {
    int h = ((int)(getPHeight() * Math.random()));
                     // along the lower half of the rhs edge
    if (h + getHeight() > getPHeight())
      h -= getHeight();    // so all on screen

    setPosition(getPWidth(), h);   
    setStep(STEP + getRandRange(STEP_OFFSET), 0);   // move left
  } // end of initPosition()


  private int getRandRange(int x) 
  // random number generator between -x and x
  {   return ((int)(2 * x * Math.random())) - x;  }



  public void updateSprite() 
  { hasHitJack();
    goneOffScreen();
	super.updateSprite();
  }

  private void hasHitJack()
  /* If the ball has hit jack, tell JackPanel (which will
     display an explosion and play a clip), and begin again.
  */
  { 
    Rectangle marioBox = mario.getMyRectangle();
    marioBox.grow(-marioBox.width/3, 0);   // make jack's bounded box thinner

    if (marioBox.intersects( getMyRectangle() )) {    // jack collision?
      mp.showExplosion(locx, locy+getHeight()/2);  
             // tell JackPanel, supplying it with a hit coordinate
      initPosition();
    }
  } // end of hasHitJack()


  private void goneOffScreen()
  // when the ball has gone off the lhs, start it again.
  {
    if (((locx+getWidth()) <= 0) && (dx < 0)) // off left and moving left
      initPosition();   // start the ball in a new position
  }  // end of goneOffScreen()


}  // end of FireBallSprite class
