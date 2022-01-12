//maanek sharma
//julio costa
//arkanoid
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;

public class Arkanoid extends JFrame implements ActionListener{
	Timer myTimer;
	GamePanel game;

    public Arkanoid() {
		super("ARKANOID");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(448,478);//frame size

		myTimer = new Timer(10, this);	 // trigger every 10 ms

		game = new GamePanel(this);
		add(game);

		setResizable(false);
		setVisible(true);
    }

	public void start(){
		myTimer.start();
	}

	public void actionPerformed(ActionEvent evt){
		game.move();
		game.paddleCollide();
		game.repaint();
		game.boundaries();
		game.loseLife();
	}

    public static void main(String[] arguments) {
		Arkanoid frame = new Arkanoid();
    }
}

class GamePanel extends JPanel implements KeyListener{
	private int paddlex,ballx,bally,ballvx,ballvy,lives,alive;//bar x coordinates and ball x,y coordianates
	private boolean []keys;
	private Image back,paddle,ball,whitebrick,gameover;
	private Arkanoid mainFrame;
	private boolean start;
	private int bricks[][];
	private brickMaker brick;
	public int score;
	public static final int XBOUNCE = 1, YBOUNCE = 2;
	Font fontLocal=null, fontSys=null;

	public GamePanel(Arkanoid m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("ArkanoidBackground.png").getImage();//background
		back = back.getScaledInstance(442,478,Image.SCALE_SMOOTH);//resize background

	 	paddle = new ImageIcon("bar.png").getImage();//main paddle
	 	paddle = paddle.getScaledInstance(40,10,Image.SCALE_SMOOTH);

		ball = new ImageIcon("ball.png").getImage();//ball
		ball = ball.getScaledInstance(7,7,Image.SCALE_SMOOTH);

		whitebrick=new ImageIcon("whitebar.png").getImage();//bricks
		whitebrick = whitebrick.getScaledInstance(32,16,Image.SCALE_SMOOTH);

		gameover=new ImageIcon("gameover.jpg").getImage();//bricks
		gameover = gameover.getScaledInstance(448,478,Image.SCALE_SMOOTH);

    	fontSys = new Font("Comic Sans MS",Font.PLAIN,32);//font for score and lives display


		mainFrame = m;

	    paddlex = 210;//paddle start x coordinate

	    brick=new brickMaker();//brick maker class

	    ballvx=0;//ball velocity along the x
	    ballvy=0;//ball velocity along the x

		lives=3;//lives

		start=true;

		setSize(448,478);//frame size
        addKeyListener(this);
	}

    public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

	public void move(){
		if(keys[KeyEvent.VK_RIGHT] ){//paddle right
			paddlex += 5;
		}
		if(keys[KeyEvent.VK_LEFT] ){//paddle left
			paddlex -= 5;
		}
		ballx+=ballvx;//ball movement
		bally+=ballvy;

		int hit = brick.brickHit(ballvx,ballvy,ballx,bally,score);

		if(hit == XBOUNCE){//bounce to x
			ballvx *= -1;
			score+=100;
		}
		else if(hit == YBOUNCE){//bounce to y
			ballvy*= -1;
			score+=100;
		}

	}

	public void paddleCollide(){//if ball hits the paddle
		Rectangle paddleRect = new Rectangle(paddlex,400,50,10);
		Rectangle ballRect=new Rectangle(ballx,bally,5,5);
   		 if(ballRect.intersects(paddleRect)){
   		 	ballvy=-ballvy;
   		 }
   	}

	public void boundaries(){
		if (paddlex>385){//so that the paddle doesnt go off right side of screen
			paddlex=385;
		}
		if (paddlex<20){//so that the paddle doesnt go off left side of screen
			paddlex=20;
		}
		if (ballx<20){//so that the ball doesnt go off left side of screen
			ballvx=-ballvx;
		}
		if (ballx>410){//so that the ball doesnt go off right side of screen
			ballvx=-ballvx;
		}
		if (bally<20){//so the ball dosent go off the top of the screen
			ballvy=-ballvy;
		}
	}

	public void loseLife(){//losing lives when the ball is lower than the paddle
		if (bally>480){
			start=true;
			lives--;
			ballx=paddlex+20;
			bally=394;
			ballvx=0;
			ballvy=0;
		}
	}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {//once the user presses space the ball starts moving
        keys[e.getKeyCode()] = true;
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
			start=false;
        	ballvx=-6;
        	ballvy=-4;
        }
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void paint(Graphics g){//painting all images
    	g.drawImage(back,0,0,null);

		g.fillRect(paddlex,400,40,10);
		g.drawImage(paddle,paddlex,400,null);
		brick.paint((Graphics)g);

		if (start==true){//when ball goes off screen restart
			g.drawImage(ball,paddlex+17,394,null);
			ballx=paddlex+17;
	    	bally=396;
		}
		else{
			g.fillRect(ballx,bally,5,5);
			g.drawImage(ball,ballx,bally,null);
		}
		if (lives==0){//if all the lives are gone
			g.drawImage(gameover,0,0,null);
		}

		g.setColor(Color.WHITE);//font for lives and score
    	g.drawString("lives: "+lives,20,440);
		g.setFont(fontLocal);
    	g.drawString("score: "+score,350,440);
  	}


}

class brickMaker{
	public int bricks[][];//bricks 2d list
	private Image whitebrick,gameover;//images
	public brickMaker(){
		bricks=new int[8][11];
		for (int i=0;i<bricks.length;i++){
			for (int j=0;j<bricks[0].length;j++){
				bricks[i][j]=1;//intial value of all bricks set to 0
			}
		}
	}
	public void paint(Graphics g){//painting the bricks
		for (int i=0;i<bricks.length;i++){
			for (int j=0;j<bricks[0].length;j++){
				if (bricks[i][j]>0){
					g.setColor(Color.black);
					g.fillRect(j*32+45,i*16+100,32,16);
					g.setColor(Color.white);
					g.fillRect(j*32+45,i*16+100,30,14);
					//g.drawImage(whitebrick,j*32+45,i*16+100,null);
				}
				if (bricks.length==0){
					g.drawImage(gameover,0,0,null);
				}
			}
		}
	}

	public int brickHit(int ballvx,int ballvy,int ballx,int bally, int score){//if the ball makes contact with a brick change the value to 0 and return a bounce
		int xhit=0,yhit=0;
		for (int i=0;i<bricks.length;i++){
			for (int j=0;j<bricks[0].length;j++){
				if(bricks[i][j]>0){

					Rectangle brickRect=new Rectangle(j*32+45,i*16+100,32,16);
					Rectangle ballRect=new Rectangle(ballx,bally,5,5);

					if (ballRect.intersects(brickRect)){
						bricks[i][j]=0;
						if(!brickRect.intersects(new Rectangle(ballx-ballvx,bally,5,5))){
							xhit++;
						}
						if(!brickRect.intersects(new Rectangle(ballx,bally-ballvy,5,5))){
							yhit++;
						}
					}
				}
			}
		}
		if(xhit>yhit){
			return GamePanel.XBOUNCE;
		}
		if(yhit>xhit){
			return GamePanel.YBOUNCE;
		}
		return 0;
	}
}
