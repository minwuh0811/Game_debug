import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
Huvudklassen Game ärver från den inbyggda klassen Canvas som innehåller användbara funktioner för att skapa spelet,
som exempelvis en keylistener som kopplar tangenttryckningarna till spelet och funktioner för att skapa spelfönstret.
Game implementerar även Runnable som gör det möjligt att starta och stoppa threads.
 */
public class Game extends Canvas implements Runnable {

    public static final int SQUARE = 64;                                                        //Delar in spelplanen i rutor, varje ruta är 64 pixlar
    public static final int FACTOR = 10;                                                        //Spelplanen är 10 rutor bred
    public static final int WIDTH = SQUARE*FACTOR;                                              //Längden på spelplanen är antal rutor*längden på varje ruta
    public static final int HEIGHT = SQUARE*FACTOR;                                             //Samma höjd som bredd
    public static final int SCALE = 1;                                                          //Scale om man vill förstora eller minska spelet proportionellt
    public final String TITLE = "ProgrammeraJava";                                              //Namnet på spelet

    private boolean running = false;                                                            //En boolean om tråden ska köras eller ej
    private Thread thread;                                                                      //Skapar en ny tråd

    //private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); //RedGreenBlue
    private BufferedImage background;

    //Skapa alla objekt som ska samverka med varandra
    private Player player;                                                                      //Skapar ett nytt objekt från klassen Player
    private Wall wall;                                                                          //Skapar ett nytt objekt från klassen Wall
    private Starter start;                                                                      //Skapar ett nytt objekt från klassen Starter
    private Counter counter;                                                                    //Skapar ett nytt objekt från klassen Counter

    public static enum STATE{                                                                   //Skapar två enum.
        GAME,                                                                                   //Game är aktivt när spelet körs
        RESTART,                                                                                //Game är aktivt när man väntar på att starta spelet ("press enter to start")
    };
    public static STATE state = STATE.RESTART;                                                  //Börja spelet med i läget "press enter to start"

    public void init(){                                                                         //Skapar alla initialtillstånd
        requestFocus();                                                                         //Gör så att spelet är aktivt när kompilatorn skapar spelplanen (ärvd från Canvas)
        BufferedImageLoader loader = new BufferedImageLoader();                                 //Skapar en ny bildladdar-objekt
        try{
            background = loader.loadImage("/background.png");                                   //Försöker ladda in bakgrunden
        }
        catch (IOException e){                                                                  //Om fel inträffar, släng ett felmeddelande
            e.printStackTrace();                                                                //Skriver ut felmeddelande
        }

        this.addKeyListener(new KeyInput(this));                                                //Kopplar tangenttryckningarna till spelet

        player = new Player(getWidth()/2-32,getHeight());                                       //Skapar ett nytt playerobjekt och ger koordinaterna till objektet
        wall = new Wall(0,0);                                                                   //Skapar ett nytt wallobjekt och ger koordinaterna till objektet
        counter = new Counter();                                                                //Skapar ett nytt objekt av typen Counter
        start = new Starter();                                                                  //Skapar ett nytt objekt av typen Starter
    }


    private synchronized void start(){                                                          //Metod för att starta tråden (starta spelet)
        if(running){                                                                            //Om spelet redan är startat, return (för att inte starta tråden flera gånger)
            return;
        }
        running = true;                                                                         //Sätter boolean running till true, spelet är startat
        thread = new Thread(this);                                                              //Skapar en tråd och kopplar spelet till tråden
        thread.start();                                                                         //Starta tråden
    }

    private synchronized void stop(){                                                           //Motsvarande som start fast för att stänga tråden
        if(!running) {                                                                          //Kör endast metoden om spelet är igång
            return;
        }

        running = false;
        try {
            thread.join();                                                                      //Slutför tråden
        }
        catch (InterruptedException e){                                                         //Om något får fel, kasta felmeddelande
            e.printStackTrace();
        }
        System.exit(1);                                                                         //Stänger ner spelet
    }



    public void run(){                                                                          //Metoden som körs när spelet är igång. Hanterar tick och render
        init();                                                                                 //Skapar alla objekten
        long lastTime = System.nanoTime();                                                      //Nuvarande tiden i nanosekunder sen
        final double amountOfTicks = 60.0;                                                      //Hastighet på uppdateringarna
        double ns = 1000000000 / amountOfTicks;                                                 //Antal uppdateringar per sekund
        double delta = 0;                                                                       //Skillnad på nuvarande tid och senaste uppdatering (tick)
        int updates = 0;                                                                        //Antal uppdateringar per sekund
        int frames = 0;
        long timer = System.currentTimeMillis();                                                //Antal millisekunder sen 1 Januari, 1970 (klassiskt startdatum i programmering)


        while(running){                                                                         //Kör spelet till spelet stängs av
            long now = System.nanoTime();                                                       //Nuvarande tid
            delta += (now - lastTime) / ns;                                                     //Beräknar skillnaden
            lastTime = now;                                                                     //Nytt tidsteg
            if(delta >=1){                                                                      //Om antal steg är större än 1, uppdatera
                tick();                                                                         //Kör metoden tick
                updates++;                                                                      //Uppdatera
                delta--;                                                                        //Nollställ Delta
            }
            render();                                                                           //Rita ut alla objekten
            frames++;                                                                           //Räkna hur många frames som ritas ut

            //If-satsen behövs inte för spelet men ger mer förståelse över hur många frames som skrivs ut
            if(System.currentTimeMillis() - timer > 1000){                                      //Skriv ut hur många "frame per seconds" som har ritas ut
                timer += 1000;                                                                  //Uppdatera med en sekund
                //System.out.println(updates + " Ticks, Fps " + frames);                        //Skriv ut antal frames per sekund
                updates = 0;                                                                    //Nollställ
                frames = 0;
            }
        }
        stop();                                                                                 //Om spelet stängs av, stäng av tråden
    }

    private void tick(){                                                                        //Körs varje uppdatering
        if(state==STATE.GAME) {                                                                 //Uppdatera bara om vi är i "spelläge"
            player.tick();                                                                      //Uppdatera player
            wall.tick();                                                                        //Uppdatera wall
            if(wall.collision(player.getBounds(player.getX(), player.getY()))){                 //Om player och wall kolliderar med varandra, ändra spelläge till "start-läge"
                state = STATE.RESTART;                                                          //Ändra till startläge
            }

        }
    }

    private void render(){                                                                      //Ritar ut objekten
        BufferStrategy bs = this.getBufferStrategy();                                           //BufferedStrategy gör det möjligt att ladda flera frames samtidigt, ger ett snabbare spel
        if (bs == null){
            createBufferStrategy(3);                                                            //Buffrar tre lager åt gången
            return;
        }

        Graphics g = bs.getDrawGraphics();                                                      //Gör det möjligt att rita ut objekten på spelplanen
        g.drawImage(background, 0, 0, null);                                                    //Ritar ut bakgrunden


        if(state == STATE.GAME) {                                                               //Uppdatera bara om vi är i "spelläge"
            player.render(g);                                                                   //Rita ut objektet player
            wall.render(g);                                                                     //Rita ut objektet wall
            counter.render(g);                                                                  //Rita ut objektet counter

        }
        if(state == STATE.RESTART){                                                             //Om spelet är i "start-läge"
            start.render(g);                                                                    //rita ut objektet starter

        }


        ////// ends here
        g.dispose();                                                                            //Uppdaterar föregående ritning
        bs.show();                                                                              //Gör alla objekt synliga
    }

    //Följande metoder gör det möjligt att hantera tangenttryck

    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();                                                               //Nytt tangenttryck

        if(state == STATE.GAME) {                                                               //Piltangenterna fungerar bara när vi är i "spel-läge"
            if (key == KeyEvent.VK_RIGHT) {                                                     //Om vi trycker höger, öka hastigheten på player åt höger
                player.setVelX(9);
            } else if (key == KeyEvent.VK_LEFT) {                                               //Om vi trycker vänster, öka hastigheten på player åt vänster
                player.setVelX(-9);
            } else if (key == KeyEvent.VK_DOWN) {                                               //Om vi trycker neråt, öka hastigheten på player neråt
                player.setVelY(9);
            } else if (key == KeyEvent.VK_UP) {                                                 //Om vi trycker upp, öka hastigheten på player uppåt
                player.setVelY(-9);
            }
        }
        else if (state == STATE.RESTART){                                                       //Om vi är i "start-läge"
            if(key == KeyEvent.VK_ENTER){                                                       //Om vi trycker enter
                state = STATE.GAME;                                                             //Ändra till "spel-läge"
                player.setX(getWidth()/2-32);                                                   //Sätt ny x-koordinat på player
                player.setY(getHeight());                                                       //Sätt ny y-koordinat på player
                wall.y = 0;                                                                     //Sätt ny y-koordinat på wall
                wall.setVelY(1);                                                                //Sätt starthastighet på wall
                counter.reset();                                                                //Nollställ räknaren
            }

        }
    }

    public void keyReleased(KeyEvent e){                                                        //När en tangent släpps
        int key = e.getKeyCode();                                                               //Ny tangent har släppts


        if(key == KeyEvent.VK_RIGHT){                                                           //Om vi släpper högertangenten, sätt hastigheten åt höger till noll
            player.setVelX(0);
        }
        else if(key == KeyEvent.VK_LEFT){                                                       //Om vi släpper vänstertangenten, sätt hastigheten åt vänster till noll
            player.setVelX(0);
        }
        else if(key == KeyEvent.VK_DOWN){                                                       //Om vi släpper nertangenten, sätt hastigheten neråt till noll
            player.setVelY(0);
        }
        else if(key == KeyEvent.VK_UP){                                                         //Om vi släpper upptangenten, sätt hastigheten uppåt till noll
            player.setVelY(0);
        }
    }


    //Mainmetoden - körs först i programmet
    public static void main (String args[]){
        Game game = new Game();                                                                 //Skapar ett nytt game

        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));                    //Skapar dimensionerna för spelet enligt vad som är angivet i klassvariablerna
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

        JFrame frame = new JFrame(game.TITLE);                                                  // Skapar objektet frame och ger titeln på spelet
        frame.add(game);                                                                        // Kopplar objektet game till GUI:t
        frame.pack();                                                                           //Gör GUI:t i rätt storlek (samma storlek som spelet)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                                   //Om man stänger fönstret, stäng av programmet
        frame.setResizable(false);                                                              //Gör så att man inte kan ändra storlek på fönstret
        frame.setLocationRelativeTo(null);                                                      //Placerar GUI:t mitt på skärmen
        frame.setVisible(true);                                                                 //Gör fönstret synligt för användaren
        game.start();                                                                           //Starta spelet

    }
}


