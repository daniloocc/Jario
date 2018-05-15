package mario;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * @author Danilo
 * @author Andrew Davison
 * @version 0.0001XD
 * @since 08/10/2012
 * 
 * Essa classe sera responsavel pelo gerenciamento de renderização e controle do tempo.
 * Questoes como controle de colisões e update de coordenadas deve ser
 * geridas no metodo update que sera sobreposta na classe que herdar de fase.
 */

@SuppressWarnings("serial")
public abstract class Fase extends JPanel implements Runnable{
	
	protected long period; // periodo entre os desenhos em _nanosecs_
	private long gameStartTime;   // when the game started
	
	protected static final int PWIDTH = 800; // tamanho do painel
	protected static final int PHEIGHT = 640;
	protected boolean keyLeft, keyRight, keyDown, keyUp, keyCtrl, keySpace;//update related

	protected Thread animator; // A thread usada para animações 
	protected boolean running = false; // usado para parar a thread de animação
												
	protected volatile boolean isPaused = false;

	// usado na finalização do jogo
	protected volatile boolean gameOver = false;
	protected int score = 0;

	// para a exibição de mensagens
	protected Font msgsFont;
	protected FontMetrics metrics;

	// usada na renderização da imagem na tela
	protected Graphics dbg;
	protected Image dbImage = null;

	// exibição da imagem de ajuda
	protected BufferedImage helpIm;
	
	//um ponteiro para a classe onde esse painel sera adicionado
	//quando a fase termnar ele mandara uma mensagem para o pai 
	//indicando o fim.
	private RocketMario top;
	
	public GameEndStatus status = GameEndStatus.PLAYING;
	

	private static final int NO_DELAYS_PER_YIELD = 16;
	/* Number of frames with a delay of 0 ms before the animation thread yields
	     to other running threads. */
	private static final int MAX_FRAME_SKIPS = 5;
	    // no. of frames that can be skipped in any one animation loop
	    // i.e the games state is updated but not rendered
	
	
	/**
	 * @param RocketMario
	 * @param period
	 * @author Danilo
	 * Construtor padrao da Classe fase.
	 * Essa classe sera responsavel pelo gerenciamento de renderização e controle do tempo.
	 * Questoes como controle de colisões e update de coordenadas deve ser
	 * geridas no metodo update que sera sobreposta na classe que herdar de fase.
	 */
	public Fase(RocketMario frameGame, long period) {
		top = frameGame;
		this.period = period; // period between drawing in _nanosecs_
		setDoubleBuffered(false);
		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		setFocusable(true);
		requestFocus(); // the JPanel now has focus, so receives key events

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				processKey(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					keySpace = false;
					break;
				case KeyEvent.VK_DOWN:
					keyDown = false;
					break;
				case KeyEvent.VK_RIGHT:
					keyRight = false;
					break;
				case KeyEvent.VK_LEFT:
					keyLeft = false;
					break;
				case KeyEvent.VK_CONTROL:
					keyCtrl = false;
					break;			
				}
			}
		});		
	}
	
	/**
	 *  espera o JPanel ser adicionado ao JFrame para começar a animação
	 */
	public void addNotify()	{
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}
	
	/**
	 * Inicializa e starta a thread de animação
	 */
	private void startGame(){
		if (animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // fim de startGame()
	// ----------------------------------------------

	/**
	 * @exception InterruptedException
	 * 
	 * Responsavel pelo loop de animação.
	 * Esse metodo é responsavel pelo ciclo do jogo.
	 * Lanca uma interruptedException caso o ciclo
	 * seja interrompido.
	 */
	  public void run()
	  /* The frames of the animation are drawn inside the while loop. */
	  {
	    long beforeTime, afterTime, timeDiff, sleepTime;
	    long overSleepTime = 0L;
	    int noDelays = 0;
	    long excess = 0L;

	    gameStartTime = System.nanoTime();
	    beforeTime = gameStartTime;

		running = true;

		while(running) {
			  gameUpdate();
		      gameRender();
		      paintScreen();

		      afterTime = System.nanoTime();
		      timeDiff = afterTime - beforeTime;
		      sleepTime = (period - timeDiff) - overSleepTime;  

		      if (sleepTime > 0) {   // some time left in this cycle
		        try {
		          Thread.sleep(sleepTime/1000000L);  // nano -> ms
		        }
		        catch(InterruptedException ex){}
		        overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
		      }
		      else {    // sleepTime <= 0; the frame took longer than the period
		        excess -= sleepTime;  // store excess time value
		        overSleepTime = 0L;

		        if (++noDelays >= NO_DELAYS_PER_YIELD) {
		          Thread.yield();   // give another thread a chance to run
		          noDelays = 0;
		        }
		      }

		      beforeTime = System.nanoTime();

		      /* If frame animation is taking too long, update the game state
		         without rendering it, to get the updates/sec nearer to
		         the required FPS. */
		      int skips = 0;
		      while((excess > period) && (skips < MAX_FRAME_SKIPS)) {
		        excess -= period;
			    gameUpdate();    // update state but don't render
		        skips++;
		      }
			}
		    System.exit(0);   // so window disappears
	} // fim run()
	
	
	/**
	 * @param KeyEvent 
	 * Manipula teclas de terminação, pausa e teclas de jogo
	 */
	private void processKey(KeyEvent e)
	{
		int keyCode = e.getKeyCode();

		// teclas de terminação
		// ouve as teclas esc, q, end, ctrl-c no jpanel
		if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q)
				|| (keyCode == KeyEvent.VK_END)
				|| ((keyCode == KeyEvent.VK_C) && e.isControlDown())){
			status = GameEndStatus.GAME_OVER;
			running = false;
		}
		
		// teclasl de ajuda
		if (keyCode == KeyEvent.VK_H) {
			isPaused = !isPaused;
		}

		// teclas de jogo
		if (!isPaused && !gameOver) {
			//as teclas de jogo sao setadas como verdadeiras caso sejam]
			//pressionadas
			if (keyCode == KeyEvent.VK_LEFT)
				keyLeft = true;
			if (keyCode == KeyEvent.VK_RIGHT)
				keyRight = true;
			if (keyCode == KeyEvent.VK_SPACE)
				keySpace = true;
			if (keyCode == KeyEvent.VK_DOWN)
				keyDown = true;
			if (keyCode == KeyEvent.VK_CONTROL)
				keyCtrl = true;
		}
	} // fim de processKey()
	
	// --- metodos do ciclo de vida do jogo --------
	// chamado pela janela ouvinte do jframe
	// chamado quando o JFrame é ativado
	
	/**
	 *  Retorna a execução do ciclo do game
	 */
	public void resumeGame(){
		if (!isPaused) // CHANGED
			isPaused = false;
	}

	// called when the JFrame is deactivated / iconified
	/**
	 *  Pausa a execução do ciclo do game
	 */
	public void pauseGame(){
		isPaused = true;
	}
	
	// chamado quando o jframe esta fechando
	/**
	 *  Para a execução do ciclo do game
	 */
	public void stopGame(){
		running = false;
	}
	
	/**
	 * Esse metodo deve ser resposavel pela implementação da 
	 * atualizações de coordenadas, checagem de colisão e outras.
	 * Deve ser implementadas na classe que herda dessa classe.
	 */
	protected void gameUpdate(){}
	
	/**
	 * Esse metodo deve ser resposavel pela renderização da 
	 * imagem que vai ser exibida na tela.
	 * Deve ser implementadas na classe que herda dessa classe.	  
	 */
	protected void gameRender(){}
	
	/**
	 * Exibe o contexto grafico na tela
	 * Usa renderização ativa para por o bufferedImage na tela
	 */
	protected final void paintScreen()
	{
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))
				g.drawImage(dbImage, 0, 0, null);
			g.dispose();
		} catch (Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	} // fim de paintScreen()
	
	
	/**
	 *	@param Graphics	contexto graphico do jogo onde a mensagem sera exibida
	 *	@param score	pontuação do jogador
	 *
	 *	Exibe a mensagem que indica que o jogo acabou
	 */
	protected void gameOverMessage(Graphics g, int score){
		String msg = "Game Over. Your score: " + score;

		int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
		int y = (PHEIGHT - metrics.getHeight()) / 2;		
		g.setFont(msgsFont);
		g.setColor(Color.white);
		g.drawString(msg, x+1, y+1);
		g.setColor(Color.black);
		g.drawString(msg, x, y);
	} // fim de gameOverMessage()
	
	/**
	 * @param Graphics	contexto graphico do jogo onde a imagem sera exibida
	 * Mostra a imagem de pausa
	 */
	protected void showHelpMessage(Graphics dbg) {
		dbg.drawImage(helpIm, (PWIDTH - helpIm.getWidth()) / 2,
				(PHEIGHT - helpIm.getHeight()) / 2, null);		
	}

	/**
	 * @param e	status do jogo atuamente
	 * @param score indica o placar do jogador
	 * 
	 * Esse methodo é um flag para o jframe container.
	 * O status sera passado pra classe pai e lá ele decidira
	 * o que fazer
	 */
	public void callParent(GameEndStatus e, long score){
		stopGame();
		top.updateStage(e, score);
	}
}
