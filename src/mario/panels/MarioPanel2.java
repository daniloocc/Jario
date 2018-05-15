package mario.panels;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mario.Fase;
import mario.RocketMario;


public class MarioPanel2 extends Fase{

	public MarioPanel2(long period, RocketMario frameGame ) {
		super(frameGame, period);
		JOptionPane.showMessageDialog(null, "fase 2");

	}

}
