import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Player extends GameObject implements Entity {
    private BufferedImage hero;
    private double velX = 0;
    private double velY = 0;

    public Player(int x, int y) {
        super(x, y);

        BufferedImageLoader loader = new BufferedImageLoader();
        try{
            hero = loader.loadImage("/hero.png");                 
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        x+=velX;
        y+=velY;

        if(x <=0){
            x=0;
        }
        if(x >=Game.WIDTH-64){
            x=640-64;
        }
        if(y <=0){
            y=0;
        }
        if(y >=Game.HEIGHT-64){
            y=Game.HEIGHT-64;
        }

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(hero, x, y, null);
    }

    //Velocity
    public void setVelX(double velX){
        this.velX = velX;
    }

    public void setVelY(double velY){
        this.velY = velY;
    }
}
