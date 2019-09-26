import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Counter {                                          //Denna klass håller räkningen på hur många varv vi klarar av

    Font fnt0 = new Font("Comics", Font.BOLD, 20);              //Skapar en ny font
    public static int count = 0;                                //En statisk räknare som alla klasser kommer åt


    public void render(Graphics g) {                            //Ritar ut värdet på count
        g.setFont(fnt0);                                        //Anger fonten
        g.setColor(Color.BLACK);                                //Anger färgen
        g.drawString(Integer.toString(count), 10, 30);          //Ritar ut count på (x=10, y=30)
    }

    public void reset(){                                        //Om metoden reset anropas, nollställs räknaren
        this.count = 0;
    }
}