package mario.basics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @author Danilo
 * @author Andrew Davison
 * @version 0.0001XD
 * @since 08/10/2012
 * 
 *        Uma ribbon gerencia uma imagem que é mais larga que o painel de jogo
 *        width: width >= pWidth
 * 
 *        Quando um sprite é instruido a mover esquerda ou direita, o sprite não
 *        move realmente, ao inves disso, a faixa move na direção
 *        contraria(right or left). A quantidade de movimento é especificado em
 *        moveSize.
 * 
 *        A imagem é amarrada junto com sua cauda, então em um dado momento a
 *        cauda da imagem, seguida por sua cabeca podera ser visivel no painel
 * 
 *        Uma coleção de faixa sao gerenciada por um objego RibbonManager
 */

public class Ribbon {

	private BufferedImage im;

	private int width; // a largura da imagem (>= pWidth)
	private int pWidth, pHeight; // dimensões do painel

	private int moveSize; // tamanho do movimento de imagem(in pixels)
	private boolean isMovingRight;// flags de movimento
	private boolean isMovingLeft;

	// A coordenada x no painel onde o inicio da imagem
	// (sua head) deve ser desenhado.
	// ela pode variar de -width ate width (exclusivo).
	//
	// Enquanto xImHead varia, a faixa na tela sera geralmente
	// uma combinação de sua cauda seguido por sua cabeça
	private int xImHead;

	/**
	 * @param w
	 *            largura do painel
	 * @param h
	 *            altura do painel
	 * @param im
	 *            imagem da faixa (que sera maior que o panel;
	 * @param moveSz
	 *            quantidade de movimento que a faixa fara em cada atualização
	 * 
	 *            A faixa sera geralmente usada para representar o estado do
	 *            background. Ela se movera dependento da direção qeu ela devera
	 *            tomar.
	 */
	public Ribbon(int w, int h, BufferedImage im, int moveSz) {
		pWidth = w;
		pHeight = h;

		this.im = im;
		width = im.getWidth(); // sem nescessidade de armazenar a altura
		if (width < pWidth)
			System.out.println("Ribbon width < panel width");

		moveSize = moveSz;
		isMovingRight = false; // sem movimento no inicio
		isMovingLeft = false;
		xImHead = 0;
	} // fim do construtor de Ribbon()

	/**
	 * seta o objeto para mover a faixa para a direita na proxima atualização
	 */
	public void moveRight() {
		isMovingRight = true;
		isMovingLeft = false;
	}

	/**
	 * seta o objeto para mover a faixa para a esquerda na proxima atualização
	 */
	public void moveLeft() {
		isMovingRight = false;
		isMovingLeft = true;
	}

	/**
	 * seta o objeto para não mover a faixa na proxima atualização
	 */
	public void stayStill() {
		isMovingRight = false;
		isMovingLeft = false;
	}

	/**
	 * Incrementa o valor de xImHead dependendo da flag de movimento. Pode
	 * variar entre -width to width (exclusivo), que é a largura da imagem
	 */
	public void update() {

		if (isMovingRight)
			xImHead = (xImHead + moveSize) % width;
		else if (isMovingLeft)
			xImHead = (xImHead - moveSize) % width;

	} // fim de update()

	/**
	 * @param g
	 *            contexto grafico onde a faixa vai ser impressa Consider 3
	 *            cases: quando xImHead == 0, desenhe somente a cabeca da im
	 *            quando xImHead > 0, desenhe a cauda da im e a cabeca, ou
	 *            somente desenhe a cauda. quando xImHead < 0, desenhe a cauda
	 *            da imagem, ou a cauda e a cabeca
	 * 
	 *            xImHead pode variar entre -width to width (exclusivo)
	 */
	public void display(Graphics g) {
		if (xImHead == 0) // desenha a cabeca da imagem em (0,0)
			draw(g, im, 0, pWidth, 0, pWidth);

		else if ((xImHead > 0) && (xImHead < pWidth)) {// desenha a cauda de im
														// em (0,0) e a cabeca
														// de im em (xImHead,0)

			draw(g, im, 0, xImHead, width - xImHead, width); // cauda de im
			draw(g, im, xImHead, pWidth, 0, pWidth - xImHead); // head im

		} else if (xImHead >= pWidth) // somente desenha a cauda de im em (0,0)
			draw(g, im, 0, pWidth, width - xImHead, width - xImHead + pWidth); // cauda
																				// im

		else if ((xImHead < 0) && (xImHead >= pWidth - width))
			draw(g, im, 0, pWidth, -xImHead, pWidth - xImHead); // corpo de im

		else if (xImHead < pWidth - width) {// desenha a cauda da im em (0,0) e
											// a cabeça em (width+xImHead,0)
			draw(g, im, 0, width + xImHead, -xImHead, width); // cauda de im
			draw(g, im, width + xImHead, pWidth, 0, pWidth - width - xImHead); // cabeca
																				// de
																				// im
		}
	} // fim de display()

	/**
	 * @param g
	 *            contexto grafico onde a faixa sera impressa
	 * @param im
	 *            imagem que sera impressa no contexto
	 * @param scrX1
	 *            coordenadas x inicial tela onde a imagem vai ser impressa
	 * @param scrX2
	 *            coordenadas x final tela onde a imagem vai ser impressa
	 * @param imX1
	 *            coordenadas x inicial da imagem a ser impressa
	 * @param imX2
	 *            coordenadas x final da imagem a ser impressa
	 * 
	 *            A coordenadas y das images sempre comecao no 0 e terminam em
	 *            pHeight (A altura do painel).
	 */
	private void draw(Graphics g, BufferedImage im, int scrX1, int scrX2,
			int imX1, int imX2) {
		g.drawImage(im, scrX1, 0, scrX2, pHeight, imX1, 0, imX2, pHeight, null);
	}
} // end of Ribbon
