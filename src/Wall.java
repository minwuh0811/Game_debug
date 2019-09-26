import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;


public class Wall extends GameObject implements Entity {                //Ärver GameObject och har Entity som interface
    private BufferedImage wall;                                         //Skapar bildobjektet
    private double velY = 1;                                            //Sätter hastigheten, initialt till 1.
    Random random;                                                      //Skapar objektet random
    int rand;                                                           //Skapar klassvariablen rand, som ska innehålla slumpvärdet

    public Wall(int x, int y) {                                         //Konstruktorn
        super(x, y);                                                    //Klassvariablerna sätts av superklassen GameObjekt

        BufferedImageLoader loader = new BufferedImageLoader();         //Skapa "bild-laddar"-objektet
        try{
            wall = loader.loadImage("/wall.png");                       //Ladda bilden som heter "wall.png"
        }
        catch (IOException e){                                          //Om filen inte hittas, kasta ett fel (varna)
            e.printStackTrace();                                        //Skriv ut felet
        }
        random = new Random();                                          //Skapa ett nytt objekt som ska hantera random
        rand = random.nextInt(9);                                      //Slumpa ett tal mellan 0-9, (det är här första hålet i väggen är)
    }

    @Override
    public void tick() {                                                //Denna metod körs vid varje klockcykel
        y = y+(int)velY;                                                //Sätter nya positionen på objektet (wall)

        if(y>=Game.HEIGHT){                                             //Om objektet har kommit längst ner i bild, starta om från början
            y=0;                                                        //Sätt koordinaten på objektet till 0 (överst i bild)
            rand = random.nextInt(9);                                  //Slumpa ett nytt tal (nytt hål i väggen)
            setVelY(velY+0.5);                                          //Öka hastigheten till nästa varv (så att spelet blir svårare och svårare)
            Counter.count++;                                            //Öka räknare med ett (håller räkningen på hur många varv vi har klarat)
        }
    }
    @Override
    public void render(Graphics g) {                                    //Denna metod ritar ut objektet på rätt position


        for(int i = 0; i < Game.FACTOR; i++ ) {                         //Rita ut objektet Game.Factor gånger (10 ggr)

            if(i != rand && i != rand+1) {                              //Rita bara ut objektet om vi inte är på den positionen där det ska vara ett hål
                g.drawImage(wall, x + 64 * i, y, null);                 //Rita ut objektet wall på rätt position
            }
        }
    }


    public void setVelY(double velY){                                   //Sätt ny hastighet
        this.velY = velY;
    }




    /*
    Vi skapar här en arraylist som vi fyller med rektanglar. Varje rektangel är en "del av väggen" (totalt 10 stycken - 2 hål)
    Genom att skapa reklanglar kan vi enkelt se om en vägg kolliderar med spelaren = Game Over, med hjälp av den
    inbyggda funktionen "intersect" som undersöker om två rektanglar korsar varandra.
     */

    private ArrayList<Rectangle> getRectangleWall(){
        ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();           //Skapar en Arraylist av typen Rectangle

        for(int i = 0; i < Game.FACTOR; i++ ) {                         //Loopar 10 ggr

            if(i != rand && i != rand+1) {                              //Lägger en ny rektangle i arraylisten om vi inte är "på hålet"
                rectangles.add(getBounds(64*i, y));                     //GetBounds returnerar en ny rektangel från superklassen GameObject
            }
        }
        return rectangles;                                              //Returnerar listan
    }



    public boolean collision(Rectangle rectanglePlayer){                //Undersöker om objektet player och wallen kolliderar med varandra = Game Over

        for(Rectangle rec : getRectangleWall()) {                       //Loopar igenom hela listan med rektanglar från getRectangleWall()
            if (rectanglePlayer.intersects(rec)) {                      //Om player och någon av de 8 wall-objekten korsar varandra, returnera true = Game Over
                return true;
            }
        }
        return false;                                                   //Om ingen av wall-objekten korsar player så returnera false
    }

}