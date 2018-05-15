package mario.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mario.Fase;
import mario.GameEndStatus;
import mario.RocketMario;


public class PanelInicial extends Fase{
	private final String IMG_DIR = "../Images/";
	//pode assumir valores 0 e 1
	private boolean selectedButton;
	private Image background = new ImageIcon(
			getClass().getResource(IMG_DIR+"title.gif")).getImage();

	
	public PanelInicial(RocketMario rm, long period){
		super(rm, period);	
	}
	
	@Override
	protected void gameUpdate() {
		if (keyCtrl) {
			status = GameEndStatus.CONTINUE;
			running = false;
		}
	}
	
}
