package mario.basics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @author Danilo
 * @author Andrew Davison
 * @version 0.0001XD
 * @since 08/10/2012
 * 
 *        O obstaculo base do nosso jogo.
 * 
 *        (mapX, mapY) sao indices no mapa de blocos, que inicia no (0,0) com a
 *        primeira fila de blocos e aumenta para a esquerda e para baixo and
 *        down.
 * 
 *        locY é a coordenada y na faixa de blocos, que inicia no (0,0) no topo
 *        esquerdo do painel, e conticua para baixo e direita O bloco é
 *        representado por uma imagem estatica
 */

public class Brick {
	private int mapX, mapY; // indices do mapa de blocos
	// the ID corresponde ao index da imagem de bloco na imagem de blocos.
	private int imageID;

	// informações da imagem de bloco
	private BufferedImage image;
	private int height;

	// the y- coordinate of the brick in the bricks ribbon
	private int locY;

	/**
	 * @param id
	 *            id da imagem da imagem de blocos
	 * @param x
	 *            indice x do bloco no mapa de blocos
	 * @param y
	 *            indice y do bloco no mapa de blocos
	 * 
	 *            Cria um bloco com id da imagem e suas coordenadas no mapa de
	 *            blocos.
	 */
	public Brick(int id, int x, int y) {
		mapX = x;
		mapY = y;
		imageID = id;
	}

	/**
	 * @return mapX
	 */
	public int getMapX() {
		return mapX;
	}

	/**
	 * @return mapY
	 */
	public int getMapY() {
		return mapY;
	}

	/**
	 * @return locY retorna a posicao na tela onde o bloco podera ser desenhado
	 */
	public int getImageID() {
		return imageID;
	}

	/**
	 * @param im
	 *            imagem do bloco Seta a imagem do bloco
	 */
	public void setImage(BufferedImage im) {
		image = im;
		height = im.getHeight();
	} // fim de setImage()

	/**
	 * @param pHeight
	 *            altura do painel
	 * @param maxYBricks
	 *            numero de blocos na maior coluna Converte a coordenadas do
	 *            bloco para as coordenadas na tela
	 */
	public void setLocY(int pHeight, int maxYBricks) {
		locY = pHeight - ((maxYBricks - mapY) * height);
	}

	/**
	 * @return locY retorna a posicao na tela onde o bloco podera ser desenhado
	 */
	public int getLocY() {
		return locY;
	}

	/**
	 * @param g
	 *            contexto grafico onde o bloco sera exibido
	 * @param xScr
	 *            posicao x na tela Desenha o bloco na tela em sua posicao
	 *            correta.
	 */
	public void display(Graphics g, int xScr) {
		g.drawImage(image, xScr, locY, null);
	}
} // fim da classe Brick
