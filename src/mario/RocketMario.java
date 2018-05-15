package mario;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import mario.loaders.MidisLoader;
import mario.panels.MarioPanel1;
import mario.panels.MarioPanel2;
import mario.panels.PanelInicial;

 
@SuppressWarnings("serial")
public class RocketMario extends JFrame implements WindowListener {
	public static int DEFAULT_FPS = 30; // 40 is too fast!

	private PanelInicial pi; // tela de apresentação
	private MarioPanel1 mp; // primeira fase
	private MarioPanel2 mp2; // segunda fase

	private MidisLoader midisLoader;
	private int currentStage;

	private long period = -1;

	public RocketMario(long period) {
		super("RocketMario");
		this.period = period;

		// carrega a sequencia de fundo MIDI
		midisLoader = new MidisLoader();
		midisLoader.load("rm", "theme.midi");
		midisLoader.play("rm", true); // toca-o repetidamente

		updateStage(GameEndStatus.START, 0);

	} // fim do construtor de JumpingJack()

	private void clearFrame() {
		if (getComponentCount() > 1 )
			removeAll();
	}

	private void setUp(int fase, long score) {		
		dispose();
		clearFrame();
		Container c = getContentPane(); // default BorderLayout usado

		switch (fase) {
		case 0:
			pi = new PanelInicial(this, period);
			c.add(pi, "Center");
			break;
		case 1:
			mp = new MarioPanel1(period, this);
			c.add(mp, "Center");
			break;
		case 2:
			mp2 = new MarioPanel2(period, this);
			c.add(mp2, "Center");
			break;
		}

		addWindowListener(this);
		setUndecorated(true);
		pack();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 *	@param status 	GameEndStatus que mostrara a classe como agir
	 *	@param score 	Placar do jogador
	 *	Diz ao JFrame como reagir ao fim da ultima fase,
	 *	seja essa reação, voltar ao inicio, avançar pra proxima fase.
	 */
	public void updateStage(GameEndStatus status, long score) {
		switch (status) {
		case GAME_OVER:// volta pra tela inicial
			currentStage = 0;
			setUp(currentStage, 0);
			break;
		case RESTART:// usa a mesma fase que esta crrendo
			setUp(currentStage, 0);
			break;
		case CONTINUE:// avanca uma fase
			setUp(++currentStage, 0);
			break;
		case START:// comeca a primeira fase, que é a de indice 1
			currentStage = 1;
			setUp(currentStage , 0);
			break;
		default:
			break;
		}
	}

	// ----------------- window listener methods -------------

	public void windowActivated(WindowEvent e) {

		switch (currentStage) {
		case 0:
			pi.resumeGame();
			break;
		case 1:
			mp.resumeGame();
			break;
		case 2:
			mp2.resumeGame();
			break;
		}
	}

	public void windowDeactivated(WindowEvent e) {

		switch (currentStage) {
		case 0:
			pi.pauseGame();
			break;
		case 1:
			mp.pauseGame();
			break;
		case 2:
			mp2.pauseGame();
			break;
		}
	}

	public void windowDeiconified(WindowEvent e) {

		switch (currentStage) {
		case 0:
			pi.resumeGame();
			break;
		case 1:
			mp.resumeGame();
			break;
		case 2:
			mp2.resumeGame();
			break;
		}
	}

	public void windowIconified(WindowEvent e) {

		switch (currentStage) {
		case 0:
			pi.pauseGame();
			break;
		case 1:
			mp.pauseGame();
			break;
		case 2:
			mp2.pauseGame();
			break;
		}
	}

	public void windowClosing(WindowEvent e) {

		switch (currentStage) {
		case 0:
			pi.stopGame();
			break;
		case 1:
			mp.stopGame();
			break;
		case 2:
			mp2.stopGame();
			break;
		}		
		// midisLoader.close(); // não obrigatorio
	}

	public void windowClosed(WindowEvent e) {}

	public void windowOpened(WindowEvent e) {}

	// ----------------------------------------------------

	public static void main(String args[]) {
		long period = (long) 1000.0 / DEFAULT_FPS;
		new RocketMario(period * 1000000L); // ms --> nanosecs
	}

} // fim da classe RocketMario

