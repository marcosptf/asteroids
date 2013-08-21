/*
 * Asteriod class - for polygonal asteroid shapes
 */

//package asteroids2;

import BaseVectorShape;
import java.awt.Polygon;
import java.awt.Rectangle;

public class Asteroid extends BaseVectorShape
{

    //define the asteriod polygon shape
    private int[] astX = {-20,13,0,20,22,20,12,2,-10,-22,-16};
    private int[] astY = {20,23,17,20,16,-20,-22,-14,-17,-20,-5};

    //rotation speed
    protected double rotVel;
    public double getRotationVelocity()
    {
        return rotVel;
    }
    public void setRotationVelocity(double v)
    {
        rotVel=v;
    }

    //bouding rectangle
    public Rectangle getBounds()
    {
        Rectangle r;
        r=new Rectangle(((int)getX()-20),((int)getY()-20),40,40);
        return r;
    }

    //default constructor
    Asteroid()
    {
        setShape(new Polygon(astX,astY,astX.length));
        setAlive(true);
        setRotationVelocity(0.0);
    }
}
