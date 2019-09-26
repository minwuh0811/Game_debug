import java.awt.*;

public class Starter {                                      //Klassen Starter skriver enbart ut texten "Press enter to start"

    Font fnt0 = new Font("Comics", Font.BOLD, 25);          //Skapar en ny font, (comics, bold, strl=25)


    public void render(Graphics g) {                        //Ritar ut texten på spelplanen
        g.setFont(fnt0);                                    //Anger fonten
        g.setColor(Color.BLACK);                            //Sätter färgen på texten till svart
        g.drawString("Press Enter to start", 205, 220);     //Skriver ut texten på x-pos: 205, y-pos:220 (mitten av spelplanen)
    }
}
