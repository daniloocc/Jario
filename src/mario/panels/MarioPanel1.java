package mario.panels;


import mario.Fase;
import mario.GameEndStatus;
import mario.RocketMario;
import mario.loaders.ClipsLoader;
import mario.loaders.ImagesLoader;
import mario.managers.BricksManager;
import mario.managers.ImagesPlayer;
import mario.managers.ImagesPlayerWatcher;
import mario.managers.RibbonsManager;
import mario.sprites.CannonBallSprite;
import mario.sprites.MarioSprite;
import java.awt.image.*;
import java.awt.*;

public class MarioPanel1 extends Fase implements Runnable, ImagesPlayerWatcher {
	
	boolean colision;

	// arquivos de configuração de som, imagens e obstaculos
	private final String IMS_INFO = "imsInfo.txt";
	private final String BRICKS_INFO = "bricksInfo.txt";
	private final String SNDS_FILE = "clipsInfo.txt";

	// nomes dos sons de explosoes
	private final String[] exploNames = { "explo1", "explo2", "explo3" };
	//  numero maximo de balas de canhao
	private final short MAX_CANNONBALL = 5;
	
	// numero de vezes qeu mario pode ser atingido antes de ser game over
	private final short MAX_HITS = 5;

	// os sprites
	private MarioSprite mario; 
	private CannonBallSprite[] cannonball;
	private RibbonsManager ribsMan; // o manipulador de background
	private BricksManager bricksMan; // o manipulador de obstaculos
	
	private ClipsLoader clipsLoader;

	// relacionado a explosao
	private ImagesPlayer explosionPlayer = null;
	private boolean showExplosion = false;
	private int explWidth, explHeight; // dimensoes da imagem de explosao
	private int xExpl, yExpl; // coordenadas onde a explosoes serao desenhadas

	private int numHits = 0; // the number of times 'jack' has been hit

	public MarioPanel1(long period, RocketMario top) {
		super(top, period);
		setTheGameReady();

	} // end of JackPanel()

	/** Inicializa os elementos do game */
	private void setTheGameReady() {
		// inicialisa os loaders
		ImagesLoader imsLoader = new ImagesLoader(IMS_INFO);
		clipsLoader = new ClipsLoader(SNDS_FILE);

		// inicializa as entidades do jogo
		bricksMan = new BricksManager(PWIDTH, PHEIGHT, BRICKS_INFO, imsLoader);
		int brickMoveSize = bricksMan.getMoveSize();

		ribsMan = new RibbonsManager(PWIDTH, PHEIGHT, brickMoveSize, imsLoader);

		mario = new MarioSprite(PWIDTH, PHEIGHT, brickMoveSize, bricksMan,
				imsLoader, (int) (period / 1000000L), clipsLoader); // in ms
		cannonball = new CannonBallSprite[MAX_CANNONBALL];
		for (short i = 0; i < cannonball.length; i++)
			cannonball[i] = new CannonBallSprite(PWIDTH, PHEIGHT, imsLoader, this, mario);

		// prepare the explosion animation
		explosionPlayer = new ImagesPlayer("explosion",
				(int) (period / 1000000L), 0.5, false, imsLoader);
		BufferedImage explosionIm = imsLoader.getImage("explosion");
		explWidth = explosionIm.getWidth();
		explHeight = explosionIm.getHeight();
		explosionPlayer.setWatcher(this); // report animation's end back here

		// prepare title/help screen
		helpIm = imsLoader.getImage("title");
		isPaused = true;

		// set up message font
		msgsFont = new Font("SansSerif", Font.BOLD, 16);
		metrics = this.getFontMetrics(msgsFont);
	}

	public void showExplosion(int x, int y)
	// called by fireball sprite when it hits jack at (x,y)
	{
		if (!showExplosion) { // only allow a single explosion at a time
			showExplosion = true;
			xExpl = x - explWidth / 2; // \ (x,y) is the center of the explosion
			yExpl = y - explHeight / 2;

			/*
			 * Play an explosion clip, but cycle through them. This adds
			 * variety, and gets round not being able to play multiple instances
			 * of a clip at the same time.
			 */
			clipsLoader.play(exploNames[numHits % exploNames.length], false);			
			numHits++;
			score = MAX_HITS-numHits;
		}
	} // end of showExplosion()

	public void sequenceEnded(String imageName)
	// called by ImagesPlayer when the explosion animation finishes
	{
		showExplosion = false;
		explosionPlayer.restartAt(0); // reset animation for next time

		if (numHits >= MAX_HITS) {
			status = GameEndStatus.GAME_OVER;			
			running = false;
			gameOver = true;
			clipsLoader.play("death", false);
		}
	} // end of sequenceEnded()
	
	protected void gameUpdate() {
		
		if (!isPaused && !gameOver) {
			
			//alualiza posições do personagem, cenario e obstaculos
			
			if (keyDown){
			}
			
			if (keyLeft) {
				mario.moveLeft();
				bricksMan.moveRight(); // bricks and ribbons move the other way
				ribsMan.moveRight();
			}		
			
			if (keyRight) {
				mario.moveRight();
				bricksMan.moveLeft();
				ribsMan.moveLeft();
			}				
			
			if(keySpace){
				mario.jump(); // jumping has no effect on the bricks/ribbons
			}			

			if (mario.willHitBrick()) { // collision checking first
				mario.stayStill(); // stop jack and scenery
				bricksMan.stayStill();
				ribsMan.stayStill();
			}			
				
			if (keyCtrl) {				
				if( keyLeft || keyRight )
					if(!mario.willHitBrick()){
						mario.updateSprite();
						ribsMan.update();
						bricksMan.update();
					}
			}
			
			if(!keyLeft && !keyRight){
				mario.stayStill();
				bricksMan.stayStill();
				ribsMan.stayStill();
			}
						
			
			ribsMan.update(); // update background and sprites
			bricksMan.update();
			mario.updateSprite();			
			for (CannonBallSprite s : cannonball)
				s.updateSprite();
		
			if (showExplosion)
				explosionPlayer.updateTick(); // update the animation
			//se atingiu o fim do mapa
			if ( mario.geInTheEndOfTheMap()){
				status = GameEndStatus.CONTINUE;
 				running = false;
 			}
		}
	} // end of gameUpdate()

	public void run(){
		super.run();
		gameOverMessage(dbg, score);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {}
		System.exit(0);
//		callParent(status, score);
	}
	
	protected void gameRender() {
		
		BufferedImage b = new BufferedImage(500, 400, BufferedImage.TYPE_4BYTE_ABGR);
		
		
		if (dbImage == null) {
			dbImage = createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) {
				System.out.println("dbImage is null");
				return;
			} else
				dbg = dbImage.getGraphics();
		}

		// draw a white background
		dbg.setColor(Color.blue);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);

		// draw the game elements: order is important
		ribsMan.display(dbg); // the background ribbons
		bricksMan.display(dbg); // the bricks
		mario.drawSprite(dbg); // the sprites
		for (int i = 0; i < cannonball.length; i++)
			cannonball[i].drawSprite(dbg);

		if (showExplosion) // draw the explosion (in front of jack)
			dbg.drawImage(explosionPlayer.getCurrentImage(), xExpl, yExpl, null);

		reportStats(dbg);

		if (gameOver){
			gameOverMessage(dbg, score);
		}

		if (isPaused) // draw the help at the very front (if switched on)
			showHelpMessage(dbg);
	} // end of gameRender()

	private void reportStats(Graphics g)
	// Report the number of hits, and time spent playing
	{																																									// secs
		g.setColor(Color.red);
		g.setFont(msgsFont);
		g.drawString("Hits: " + numHits + "/" + MAX_HITS, 15, 25);
		g.drawString("Score: " + score, 15, 50);
		g.setColor(Color.black);
	} // end of reportStats()

} // end of JackPanel class
