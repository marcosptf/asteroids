/*
 * Bullet class - polygonal shape of a bullet
 */

package asteroid;

import java.awt.*;
import java.awt.Rectangle;

public class Bullet extends BaseVectorShape
{

    //bouding rectangle
    public Rectangle getBounds()
    {
        Rectangle r;
        r = new Rectangle((int)getX(),(int)getY(),1,1);
        return r;
    }

    Bullet()
    {
        //create the bullet shape
        setShape(new Rectangle(0,0,1,1));
        setAlive(false);
    }
}