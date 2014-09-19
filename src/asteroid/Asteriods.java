/**
 *@author:marcosptf
 *@since:18/06/10
 *@desc:primary class for the game
 *Este código foi escrito por marcosptf@yahoo.com.br e caso
 * for utilizar para alguma finalidade favor colocar os devidos créditos ao
 * autor.
 *@copyright:Jonathan S. Harbour.
 */

package asteroid;

import asteroid.Asteroid;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;

public class Asteriods extends Applet implements Runnable,KeyListener
{

    //the main thread becomes the game loop
    Thread gameLoop;

    //use this as a double buffer
    BufferedImage backBuffer;

    //the main drawing object for back buffer
    Graphics2D g2d;

    //toggle for drawing bounding boxes
    boolean showBounds = false;

    int ASTEROIDS = 20;
    Asteroid[] ast = new Asteroid[ASTEROIDS];

    //create the bullet array
    int BULLETS = 10;
    Bullet[] bullet = new Bullet[BULLETS];
    int currentBullet=0;

    //the player ship
    Ship ship = new Ship();

    //create the identity tranform(0,0)
    AffineTransform identity = new AffineTransform();

    //create a random number generador
    Random rand = new Random();


    /***************************************************************************
     * applet init event
     * *************************************************************************/
    @Override
     public void init(){
        //create the back buffer for smooth graphics
//        backBuffer = new BufferedImage(640,480,BufferedImage.TYPE_INT_RGB);
        this.setSize(800,600);
        backBuffer = new BufferedImage(800,600,BufferedImage.TYPE_INT_RGB);
        g2d = backBuffer.createGraphics();

        //set up the ship
        ship.setX(320);
        ship.setY(240);

        //set up the bukkets
        for(int n=0;n<BULLETS;n++){
            bullet[n]=new Bullet();
        }

        //create the asteroids
        for(int n=0;n<ASTEROIDS;n++){
            ast[n]=new Asteroid();
            ast[n].setRotationVelocity(rand.nextInt(3+1));
            ast[n].setX((double)rand.nextInt(600)+20);
            ast[n].setY((double)rand.nextInt(440)+20);
            ast[n].setMoveAngle(rand.nextInt(360));
            double ang = (ast[n].getMoveAngle()-90);
            ast[n].setVelX(calcAngleMoveX(ang));
            ast[n].setVelY(calcAngleMoveX(ang));
        }

        //start the user input listener
        addKeyListener(this);
     }


    /***************************************************************************
     * applet update event to redraw the screen
     * *************************************************************************/
    @Override
    public void update(Graphics g)
    {
        //start off transforms at identy
        g2d.setTransform(identity);

        //erase the background
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, getSize().width,getSize().height);

        //print some status information
        g2d.setColor(Color.WHITE);
        g2d.drawString("Ship: "+Math.round(ship.getX())+","+Math.round(ship.getY()),5,10);
        g2d.drawString("MoveAngle:"+Math.round(ship.getMoveAngle())+90,5,25);
        g2d.drawString("Face angle:"+Math.round(ship.getFaceAngle()),5,40);
        g2d.drawString("Developer:marcosptf@yahoo.com.br",5,55);
        g2d.drawString("http://javascripterninja.com.br",5,70);

        //paint the game graphics
        drawShip();
        drawBullets();
        drawAsteroids();

        //repaint the applet window
        paint(g);
    }


    /***************************************************************************
     * drawSphip called by applet update event
     * *************************************************************************/
    public void drawShip()
    {
        g2d.setTransform(identity);
        g2d.translate(ship.getX(),ship.getY());
        g2d.rotate(Math.toRadians(ship.getFaceAngle()));
        g2d.setColor(Color.ORANGE);
        g2d.fill(ship.getShape());
    }

    /***************************************************************************
     * drawBullets called by applet update event
     * *************************************************************************/
    public void drawBullets()
    {
        //iterate throungh the arrray of bullets
        for(int n=0;n<BULLETS;n++)
        {
            //is this bulets current in use?
            if(bullet[n].isAlive())
            {
                //draw the bullet
                g2d.setTransform(identity);
                g2d.translate(bullet[n].getX(),bullet[n].getY());
                g2d.setColor(Color.MAGENTA);
                g2d.draw(bullet[n].getShape());
            }
        }
    }

    /***************************************************************************
     * drawAsteriods clalled by applet update event
     * *************************************************************************/
    public void drawAsteroids()
    {
        //iterate through the asteroids array
        for(int n=0;n<ASTEROIDS;n++)
        {
            //is this asteroid being used?
            if(ast[n].isAlive())
            {
                //draw the asteroid
                g2d.setTransform(identity);
                g2d.translate(ast[n].getX(),ast[n].getY());
                g2d.rotate(Math.toRadians(ast[n].getMoveAngle()));
                g2d.setColor(Color.DARK_GRAY);
                g2d.fill(ast[n].getShape());
            }
        }
    }

    /***************************************************************************
     * applet window repaint event -  draw the back buffer
     * *************************************************************************/
    @Override
    public void paint(Graphics g)
    {
        //draw the back buffer onto the applet window
        g.drawImage(backBuffer,0,0,this);
    }

    /***************************************************************************
     * thread start event - start the game loop running
     * *************************************************************************/
    @Override
    public void start()
    {
        //create the gameloop thread for real time updates
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    /***************************************************************************
     * thread run event gameloop
     * *************************************************************************/
    @Override
    public void run()
    {
        //acquire the current thread
        Thread t =  Thread.currentThread();

        //keep going as long as the thread is alive
        while(t==gameLoop)
        {
            try
            {
                //update the game loop
                gameUpdate();

                //target framerate is 50 fps
                Thread.sleep(20);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            repaint();
        }

    }

    /***************************************************************************
     * thread stop event
     * *************************************************************************/
    @Override
    public void stop()
    {
        //kill the gameLoop thread
        gameLoop=null;
    }

    /***************************************************************************
     * Move and animate the objects in the game
     * *************************************************************************/
    private void gameUpdate()
    {
        updateShip();
        updateBullets();
        updateAsteroids();
        checkCollisions();
    }

    /***************************************************************************
     * Update the ship position based on velocity
     * *************************************************************************/
    public void updateShip()
    {
        //update ship X position
        ship.incX(ship.getVelX());

        //wrap around left/rigth
        if(ship.getX() <-10)
        {
            ship.setX(getSize().width+10);
        }
        else if(ship.getX()>getSize().width+10)
        {
            ship.setX(-10);
        }

        //update ship Y position
        ship.incY(ship.getVelY());

        //wrap aound top/botton
        if(ship.getY()<-10)
        {
            ship.setY(getSize().height+10);
        }
        else if(ship.getY()>getSize().height+10)
        {
            ship.setY(-10);
        }
    }

    /***************************************************************************
     * update the bullets based on velocity
     * *************************************************************************/
    public void updateBullets()
    {
        //move each of the bullets
        for(int n=0;n<BULLETS;n++)
        {
            //is this bullets being used?
            if(bullet[n].isAlive())
            {
                //update bullet X position
                bullet[n].incX(bullet[n].getVelX());

                //bullet disappears at left/right edge
                if((bullet[n].getX()<0) || (bullet[n].getX() > getSize().width))
                {
                    bullet[n].setAlive(false);
                }

                //update bullets Y position
                bullet[n].incY(bullet[n].getVelY());

                //bullet disappears at top/button edge
                if((bullet[n].getY()<0) || (bullet[n].getY()>getSize().height))
                {
                    bullet[n].setAlive(false);
                }
            }
        }
    }

    /***************************************************************************
     * update the asteriods based on velocity
     * *************************************************************************/
    public void updateAsteroids()
    {
        //move and rotate the asteroids
        for(int n=0;n<ASTEROIDS;n++)
        {
            //is this asteroids being used?
            if(ast[n].isAlive())
            {
                //update the asteroid X value
                ast[n].incX(ast[n].getVelX());

                //warp the asteroid at screed edges
                if(ast[n].getX()<-20)
                {
                    ast[n].setX(getSize().width+20);
                }
                else if(ast[n].getX()>getSize().width+20)
                {
                    ast[n].setX(-20);
                }

                //update the asteroid Y value
                ast[n].incY(ast[n].getVelY());

                //warp the asteroid at screen edges
                if(ast[n].getY()<-20)
                {
                    ast[n].setY(getSize().height+20);
                }
                else if(ast[n].getY()>getSize().height+20)
                {
                    ast[n].setY(-20);
                }

                //update the asteroid rotation
                ast[n].incMoveAngle(ast[n].getRotationVelocity());

                //keep the angle within 0-359 degrees
                if(ast[n].getMoveAngle()<0)
                {
                    ast[n].setMoveAngle(360-ast[n].getRotationVelocity());
                }
                else if(ast[n].getMoveAngle()>360)
                {
                    ast[n].setMoveAngle(ast[n].getRotationVelocity());
                }
            }
        }
    }


    /***************************************************************************
     * test asteroids for colisions with ship or bullets
     * *************************************************************************/
     public void checkCollisions()
     {
        //iterate through the asteroids array
         for(int m=0;m<ASTEROIDS;m++)
         {
             //is this asteriods being used
             if(ast[m].isAlive())
             {
                 /**
                  *check for collision with bullet
                  */
                 for(int n=0;n<BULLETS;n++)
                 {
                     //is this bullet used?
                     if(bullet[n].isAlive())
                     {
                         //perform the collision test
                         if(ast[m].getBounds().contains(bullet[n].getX(),bullet[n].getY()))
                         {
                            bullet[n].setAlive(false);
                            ast[m].setAlive(false);
                            continue;
                         }
                     }
                 }

                 /**
                  *check for collision with ship
                  */
                 if(ast[m].getBounds().intersects(ship.getBounds()))
                 {
                    ast[m].setAlive(false);
                    ship.setX(320);
                    ship.setY(320);
                    ship.setFaceAngle(0);
                    ship.setVelX(0);
                    ship.setVelY(0);
                    continue;
                 }
             }
         }
     }

     /**
      *key listener events
      */
     public void keyReleased(KeyEvent k){}

     public void keyTyped(KeyEvent k){}

     public void keyPressed(KeyEvent k)
     {
         int keyCode = k.getKeyCode();

         switch(keyCode)
         {
             case KeyEvent.VK_LEFT:
                 //left arrow rotates ship left 5 degres
                 ship.incFaceAngle(-5);
                 if(ship.getFaceAngle()<0){
                     ship.setFaceAngle(360-5);
                 }
             break;

             case KeyEvent.VK_RIGHT:
                 //left arrow rotates ship left 5 degres
                 ship.incFaceAngle(5);
                 if(ship.getFaceAngle()>360){
                     ship.setFaceAngle(360+5);
                 }
             break;

             case KeyEvent.VK_UP:
                 //up arrow adds thust to ship (1/10 normal speed)
                 ship.setMoveAngle(ship.getFaceAngle()-90);
                 ship.incVelX(calcAngleMoveX(ship.getMoveAngle())*(0.1));
                 ship.incVelY(calcAngleMoveY(ship.getMoveAngle())*(0.1));
             break;

             case KeyEvent.VK_DOWN:
                 //up arrow adds thust to ship (1/10 normal speed)
                 ship.setMoveAngle(ship.getFaceAngle()+90);
                 ship.incVelX(calcAngleMoveX(ship.getMoveAngle())*(0.1));
                 ship.incVelY(calcAngleMoveY(ship.getMoveAngle())*(0.1));
             break;

             //ctrl/enter/space can be used to fire weapon
             case KeyEvent.VK_CONTROL:
             case KeyEvent.VK_ENTER:
             case KeyEvent.VK_SPACE:
                 //fire a bullet
                 currentBullet++;
                 if(currentBullet>BULLETS-1)
                 {
                     currentBullet=0;
                 }
                 bullet[currentBullet].setAlive(true);

                 //point bullet in same direction ship is facing
                 bullet[currentBullet].setX(ship.getX());
                 bullet[currentBullet].setY(ship.getY());
                 bullet[currentBullet].setMoveAngle(ship.getFaceAngle()-90);

                 //fire bullet at angle of the ship
                 double angle = bullet[currentBullet].getMoveAngle();
                 double svX = ship.getVelX();
                 double svY = ship.getVelY();
                 bullet[currentBullet].setVelX(svX+calcAngleMoveX(angle)*(2));
                 bullet[currentBullet].setVelY(svY+calcAngleMoveY(angle)*(2));
            break;
         }
     }

     /**
      *calculate X ovement value based on direction angle
      */
     public double calcAngleMoveX(double angle)
     {
         return (double) (Math.cos((angle)*(Math.PI/180)));
     }

     /**
      *calculate Y ovement value based on direction angle
      */
     public double calcAngleMoveY(double angle)
     {
         return (double) (Math.sin((angle)*(Math.PI/180)));
     }

}
