import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.*;
import java.io.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

public class spaceship
{
	public static void main(String...args) throws IOException
	{
		JFrame j = new JFrame();  
		MyPanel m = new MyPanel();
		j.setSize(m.getSize());
		j.add(m); 
		j.setVisible(true); 

		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
class MyPanel extends JPanel implements ActionListener, KeyListener, MouseListener
{
	private Timer time;
	private int x,y, rxcol, bycol;
	private int add;
	private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean shieldup = false;
    private int shield = 0;
    private boolean gameOn = true;
	private ArrayList<SpaceObj> objs;
	private int playticks = 0;
	private int difficultyticks = 0;
	private int difficulty = 40;
	private boolean shieldpower = false;
	private int shieldcount = 0;
	private int deadticks = 0;
	private boolean explosion = true;
	private boolean startmusic = true;
	private int sx, sy;
	
	MyPanel()
	{
		time = new Timer(15, this);
		setSize(1600,850);
		setVisible(true); 
		time.start();
		add=7;
		x=800;
		y=425;
		objs = new ArrayList<SpaceObj>();
		addMouseListener(this);
		setFocusable(true);
		addKeyListener(this);
		
	}
	
	public void paintComponent(Graphics g)
	{
		if (gameOn)
		{
			g.setColor(Color.BLACK);
			g.fillRect(0,0,2000,1500);
			g.setColor(Color.WHITE);
			g.setFont(new Font("HELLO",1,20));
			if (shieldpower && !shieldup)
				g.drawString("SHIELD POWER: 100% Click to activate!",1,21);
			else g.drawString("SHIELD POWER: " + shield/5 + "%",1,21);
			g.drawString("Score: " + playticks,1,50);
			for (int k=0; k<40; k++){
				sx = (int) (Math.random()*1600);
				sy = (int) (Math.random()*850);
				g.drawOval(sx, sy, 2, 2);
			}
			if (shieldup && shieldpower)
			{
				drawSS(g,x,y);
				BufferedImage meme = null;
		        try {
		        	meme= ImageIO.read(new File("wowally.png"));
		        } catch (IOException ex){
		        	System.out.println("sad face");
		        }
		        g.drawImage(meme,180,625,null);
		        g.drawImage(meme,980,625,null);
				if (shield >= 1)
					shield--;
				else{
					shieldup = false;
					shieldpower = false;
					shieldcount--;
				}
			}
			else drawShip(g,x,y);
			for (SpaceObj so:objs){
				if (checkCollision() && so.isShield()){
					objs.remove(so);
					shieldpower = true;
					shield = 500;
				}
    		}
			for (SpaceObj so:objs){
        		so.draw(g, so.getX(), so.getY());
        		so.move();
        		if (so.despawn())
        			objs.remove(so);
        		if (checkCollision() && !shieldup)
    				gameOn = false;
			}
		}
		
		else{
			if (explosion){
				new Thread(new Runnable() {
		            public void run() {
		                try {
		                    Clip explosion = AudioSystem.getClip();
		                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("boomw.wav"));
		                    explosion.open(inputStream);
		                    explosion.start();
		                } catch (Exception e) {
		                    System.out.println("fizzled explosion :(");
		                }
		            }
		        }).start();
				explosion = false;
			}
			g.setColor(Color.BLACK);
			g.fillRect(0,0,2000,1500);
			g.setColor(Color.WHITE);
			for (int k=0; k<40; k++){
				sx = (int) (Math.random()*1600);
				sy = (int) (Math.random()*850);
				g.drawOval(sx, sy, 2, 2);
			}
			for (SpaceObj so:objs){ //moves all objects
        		so.draw(g, so.getX(), so.getY());
        		so.move();
        		if (so.despawn()) //gets rid of off-screen objects
        			objs.remove(so);
			}
			deadticks++;
			drawDeath(g,x,y);
			if (deadticks > 65){ //delays timing
				g.setColor(Color.WHITE);
				g.setFont(new Font("HELLO",1,100));
				g.drawString("GAME OVER MAN",400,375);
				if (deadticks > 130){
					g.setFont(new Font("HELLO",1,50));
					g.drawString("Score: " + playticks,700,250);
					g.drawString("Press R to play again",600,450);
				}
			}
		}	
	}
	public void drawShip(Graphics g, int x, int y)
	{
		g.setColor(Color.GRAY);
		g.fillRect(x+12,y+24,16,4);
		g.drawLine(x+12,y+24,x,y+30);
		g.drawLine(x+28,y+24,x+40,y+30);
		g.drawLine(x+28,y+8,x,y);
		g.drawLine(x+12,y+8,x+40,y);
		g.setColor(Color.GRAY);
		g.fillOval(x+12,y,16,16);
		g.setColor(Color.GREEN);//alien
		g.fillOval(x+16,y+4,6,6);
		g.setColor(Color.MAGENTA);
		g.fillOval(x,y+8,40,16);
		g.setColor(Color.WHITE);
		g.fillRect(x+4, y+13, 12, 1);
		g.fillRect(x+24, y+13, 12, 1);
		g.setColor(Color.ORANGE);
		g.fillArc(x+16,y+24,8,8,0,-180);
		g.setColor(Color.RED);
		g.fillArc(x+18,y+26,4,4,0,-180);
		g.setColor(Color.CYAN);
		g.fillOval(x,y+14,2,2);
		g.fillOval(x+4,y+14,2,2);
		g.fillOval(x+8,y+14,2,2);
		g.fillOval(x+16,y+14,2,2);
		g.fillOval(x+23,y+14,2,2);
		g.fillOval(x+30,y+14,2,2);
		g.fillOval(x+34,y+14,2,2);
		g.fillOval(x+38,y+14,2,2);
	}
	
	public void drawSS(Graphics g, int x, int y)
	{
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillRect(x+12,y+24,16,4);
		g.drawLine(x+12,y+24,x,y+30);
		g.drawLine(x+28,y+24,x+40,y+30);
		g.drawLine(x+28,y+8,x,y);
		g.drawLine(x+12,y+8,x+40,y);
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillOval(x+12,y,16,16);
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillOval(x+16,y+4,6,6);
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillOval(x,y+8,40,16);
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillRect(x+4, y+13, 12, 1);
		g.fillRect(x+24, y+13, 12, 1);
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillArc(x+16,y+24,8,8,0,-180);
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillArc(x+18,y+26,4,4,0,-180);
		g.setColor(new Color((int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1,(int)(Math.random() * 255) + 1));
		g.fillOval(x,y+14,2,2);
		g.fillOval(x+4,y+14,2,2);
		g.fillOval(x+8,y+14,2,2);
		g.fillOval(x+16,y+14,2,2);
		g.fillOval(x+23,y+14,2,2);
		g.fillOval(x+30,y+14,2,2);
		g.fillOval(x+34,y+14,2,2);
		g.fillOval(x+38,y+14,2,2);
	}	
	public void drawDeath(Graphics g,int x, int y)
	{
		g.setColor(new Color(255, (int)(Math.random()*256), 0));
		g.fillOval(x-300, y-300,600,600);
		g.setColor(new Color(255, (int)(Math.random()*256), 0));
		g.fillOval(x-((int)(Math.random()*100)+150),y-((int)(Math.random()*100)+150),400,400);
		for( int a=0; a<5; a++)
		{
			g.setColor(new Color(255, (int)(Math.random()*256), 0));
			g.fillOval(x-((int)(Math.random()*200)-50),y-((int)(Math.random()*200)-50),80,80);
			g.setColor(new Color(255, (int)(Math.random()*256), 0));
			g.fillOval(x-((int)(Math.random()*200)-50),y-((int)(Math.random()*200)-50),100,100);
		}
	}
	public void actionPerformed(ActionEvent e)
	{
		difficultyticks++;
		if (gameOn){
			playticks++;
			
			if (leftDirection && x > 2) {
				x -= add;
			}
			
			if (rightDirection && x < 1542) {
				x += add;
			}

			if (upDirection && y > 4) {
				y -= add;
			}

			if (downDirection && y < 778) {
				y += add;
			}
		}
        rxcol = x+40;
        bycol = y+30;
        if (difficultyticks % 100 == 0 && difficulty > 10) //increasing difficulty
        	difficulty--;
        if (playticks%difficulty == 0){ //random spawning
        	int rng = (int) (Math.random()*100);
        	if (rng < 42)  // 42/100
        		objs.add(new smallAsteroid()); 
        	if (rng > 41 && rng < 74) // 32/100
        		objs.add(new largeAsteroid());
        	if (rng > 73 && rng < 89) // 15/100
        		objs.add(new spaceJunk());
        	if (rng > 88 && rng < 99) // 10/100
        		objs.add(new Laser());
        	if (rng > 98 && shieldcount == 0){ //1/100
        		objs.add(new shield());
        		shieldcount++;
        	}	
        }
        if (gameOn && startmusic){
        	new Thread(new Runnable() {
	            public void run() {
	                try {
	                    Clip music = AudioSystem.getClip();
	                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("Darude.wav"));
	                    music.open(inputStream);
	                    music.start(); music.loop(music.LOOP_CONTINUOUSLY);
	                } catch (Exception e) {
	                    System.out.println("what song is this? :(");
	                }
	            }
	        }).start();
        	startmusic = false;
        }
        repaint();
	}
	public boolean checkCollision()
	{	
		for (SpaceObj so:objs)
			if ((so.getX() >= x && so.getX() <= rxcol || so.getrxcol() >= x && so.getrxcol() <= rxcol || so.getX() <= x && rxcol <= so.getrxcol())
				 && (so.getY() >= y && so.getY() <= bycol || so.getbycol() >= y && so.getbycol() <= bycol || so.getY() <= y && bycol <= so.getbycol()))
				return true;
		return false;
	}
	
	public void keyPressed(KeyEvent e)
	{
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            // game reset
            if (key == KeyEvent.VK_R)
            	if (!gameOn){
            		gameOn = true;
            		objs.clear();
            		x = 800; y = 425;
            		leftDirection = false;
            	    rightDirection = true;
            	    upDirection = false;
            	    downDirection = false;
            	    explosion = true;
            	    shieldpower = false;
            		playticks = 0;
            		difficultyticks = 0;
            		difficulty = 40;
            		shield = 0;
            		shieldcount = 0;
            		deadticks = 0;
            	}	

	}
	public void keyTyped(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	public void mousePressed(MouseEvent e){
		if (shieldpower)
			shieldup = true;
		repaint();
	}
	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
}
class SpaceObj{
	private int x, y, rxcol, bycol;
	
	public SpaceObj(){
	}	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;	
	}
	public int getrxcol(){
		return rxcol;
	}
	public int getbycol(){
		return bycol;
	}
	public boolean despawn(){
		return false;
	}
	public void move(){
		
	}
	public void draw(Graphics g, int x, int y){
		
	}
	public boolean isShield(){
		return false;
	}
}

class smallAsteroid extends SpaceObj{
	private int x, y, place, lifetime;
	
	public smallAsteroid(){
		super();
		place = (int) (Math.random()*4+1);
		if (place == 1){
			x = -40; y = (int)(Math.random()*811);
		}
		if (place == 2){
			x = (int)(Math.random()*1601); y = -40;
		}
		if (place == 3){
			x = 1600; y = (int)(Math.random()*811);
		}
		if (place == 4){
			x = (int)(Math.random()*1601); y = 850;
		}
		lifetime = 0;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;	
	}
	public int getrxcol(){
		return x+40;
	}
	public int getbycol(){
		return y+40;
	}
	public boolean despawn(){
		return lifetime > 275;
	}	
	public void move(){
		if (place == 1)
			x+=6;
		if (place == 2)
			y+=6;
		if (place == 3)
			x-=6;
		if (place == 4)
			y-=6;
		lifetime++;
	}
	public void draw(Graphics g, int x, int y){
		g.setColor(Color.GRAY);
		g.fillOval(x,y,40,35);
		g.fillOval(x+10,y+20, 40, 30);
		g.setColor(new Color(150,150,150));
		g.fillOval(x+30, y,20,40);
		g.setColor(new Color(105,105,105));
		g.fillOval(x,y+20,40,30);
		g.setColor(new Color(140,140,140));
		g.fillOval(x+25,y+10, 15, 30);
		g.setColor(new Color(105,105,105));
		g.fillOval(x+20, y+20, 10,10);
		g.fillOval(x+35, y+10, 5, 5);
		g.fillOval(x+15, y+15, 7, 5);
		g.setColor(new Color(100,100,100));
		g.fillOval(x+14, y+30, 9, 9);
	}
}
	
class largeAsteroid extends SpaceObj{
	private int x, y, place, lifetime;
	
	public largeAsteroid(){
		super();
		place = (int) (Math.random()*4+1);
		if (place == 1){
			x = -60; y = (int)(Math.random()*791);
		}
		if (place == 2){
			x = (int)(Math.random()*1601); y = -60;
		}
		if (place == 3){
			x = 1600; y = (int)(Math.random()*791);
		}
		if (place == 4){
			x = (int)(Math.random()*1601); y = 850;
		}
		lifetime = 0;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;	
	}
	public int getrxcol(){
		return x+60;
	}
	public int getbycol(){
		return y+60;
	}
	public boolean despawn(){
		return lifetime > 580; //rip blaze
	}	
	public void move(){
		if (place == 1)
			x+=3;
		if (place == 2)
			y+=3;
		if (place == 3)
			x-=3;
		if (place == 4)
			y-=3;
		lifetime++;
	}
	public void draw(Graphics g, int x, int y){
		g.setColor(Color.GRAY);
		g.fillOval(x,y,60,45);
		g.fillOval(x+15,y+30, 60, 45);
		g.setColor(new Color(150,150,150));
		g.fillOval(x+45, y,30,60);
		g.setColor(new Color(105,105,105));
		g.fillOval(x,y+30,60,45);
		g.setColor(new Color(140,140,140));
		g.fillOval(x+30,y+15, 22, 45);
		g.setColor(new Color(105,105,105));
		g.fillOval(x+30, y+30, 15,15);
		g.fillOval(x+50, y+15, 7, 7);
		g.fillOval(x+22, y+22, 8, 7);
		g.setColor(new Color(100,100,100));
		g.fillOval(x+21, y+45, 11, 11);
	}
}
class spaceJunk extends SpaceObj{
	private int x, y, place, lifetime;
	
	public spaceJunk(){
		super();
		place = (int) (Math.random()*2+1);
		if (place == 1){
			x = (int)(Math.random()*1571); y = -40;
		}
		if (place == 2){
			x = (int)(Math.random()*1571); y = 850;
		}
		lifetime = 0;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;	
	}
	public int getrxcol(){
		return x+30;
	}
	public int getbycol(){
		return y+45;
	}
	public boolean despawn(){
		return lifetime > 185;
	}	
	public void move(){
		if (place == 1)
			y+=9;
		if (place == 2)
			y-=9;
		lifetime++;
	}
	public void draw(Graphics g, int x, int y){
		g.setColor(Color.GRAY);
		g.drawRect(x+10,y,10,2);
		g.setColor(new Color(169,169,169));
		g.fillRect(x,y+2,15,5);
		g.setColor(Color.GRAY);
		g.fillRect(x+15,y+2,15,5);
		g.setColor(new Color(192,192,192));
		g.fillRect(x+2,y+7,14,28);
		g.setColor(new Color(169,169,169));
		g.fillRect(x+6,y+7,10,28);
		g.setColor(new Color(192,192,192));
		g.fillRect(x+10,y+7,6,28);
		g.setColor(Color.GRAY);
		g.fillRect(x+14,y+7,14,28);
		g.setColor(new Color(169,169,169));
		g.fillRect(x+18,y+7,10,28);
		g.setColor(Color.GRAY);
		g.fillRect(x+22,y+7,6,28);
		g.setColor(new Color(169,169,169));
		g.fillRect(x+26,y+7,2,28);
	}
}
class Laser extends SpaceObj{
	private int x, y, lifetime;
	
	public Laser(){
		super();
		y = (int)(Math.random()*811);
		lifetime = 0;
	}
	public int getX(){
		return 0;
	}
	public int getY(){
		return y;	
	}
	public int getrxcol(){
		if (lifetime < 90)
			return 0;
		else return 1600;
	}
	public int getbycol(){
		if (lifetime < 90)
			return 0;
		else return y + 40;
	}
	public boolean despawn(){
		return lifetime > 180;
	}	
	public void move(){
		lifetime++;
	}
	public void draw(Graphics g, int x, int y){
		if (lifetime < 90){
			if (lifetime % 10 > 4){}
			else {	
				g.setColor(Color.RED);
				g.fillRect(5,y,5,30);
				g.fillRect(5,y+35,5,5);
				g.fillRect(15,y,5,30);
				g.fillRect(15,y+35,5,5);
				g.fillRect(1570,y,5,30); //1590
				g.fillRect(1570,y+35,5,5);
				g.fillRect(1560,y,5,30); //1580
				g.fillRect(1560,y+35,5,5);
			}
		}
		else {
		g.setColor(new Color((int)(Math.random() * 255) + 1,255,255));
		g.fillRect(0,y,1600,40);
		g.setColor(Color.WHITE);
		g.fillRect(0,y+5,1600,30);
		g.setColor(new Color((int)(Math.random() * 255) + 1,200,255));
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		g.fillRect((int)(Math.random()*1600),y+5+(int)(Math.random()*27),(int)(Math.random()*60)+10,4);
		}
	}
}

class shield extends SpaceObj{
	private int x, y;
	
	public shield(){
		super();
		x = (int)(Math.random()*1601);
		y = (int)(Math.random()*811);
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;	
	}
	public int getrxcol(){
		return x+40;
	}
	public int getbycol(){
		return y+40;
	}
	public boolean isShield(){
		return true;
	}
	public void draw(Graphics g, int x, int y){
		g.setColor(Color.CYAN);
		g.fillOval(x,y,40,40);
		g.setColor(Color.WHITE);
		g.fillOval(x+10, y+10, 20, 20);
	}
}