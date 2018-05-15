package mario.basics;

//import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * @author Danilo
 * @version 0.0001XD
 * @since 08/10/2012
 * 
 *        TileMap gurda um tileset, e com esse tileset ela podera receber um
 *        objeto tileImages e gerar um bufferedImage a partir dele.
 */

public class TileMap {

	// numero de colunas e fila que o arquivo de tile set contem
	private short nCols, nRows;

	// guardara o tamanho de um tile, que é um quadrado,
	// logo altura e comprimento sao iguais
	private short tileSize;

	// o proprio tile set
	private BufferedImage tileSet;

	// FATOR_REDIMENSIONAMENTO
	private final short FR = 2;

	/**
	 * @param tileSet
	 *            guarda as imagens usadas no jogo
	 * @param tileSize
	 *            tamanho base do tile
	 * 
	 *            Recebe o tile set que permitira realizar as operações de tile
	 */
	public TileMap(BufferedImage tileSet, short tileSize) {
		setTileSet(tileSet);
		this.tileSize = tileSize;
		this.nCols = (short) ((short) (tileSet.getWidth() / tileSize));
		this.nRows = (short) ((short) (tileSet.getHeight() / tileSize));
	}

	/**
	 * @param tiledMap
	 *            objeto a ser lido para obtenção da imagem renderizada
	 * @return b imagem renderizada a partir da configuração encontrada em
	 *         timeImage
	 * 
	 *         Vai renderizar a bufferedimage a partir da configuração
	 *         encontrada no objeto
	 */
	public BufferedImage getImageFromTileMap(TileImage tiledMap) {
		short[][] tileMap = tiledMap.getTileMap();
		BufferedImage b = new BufferedImage(nCols * (tileSize * FR), nRows
				* (tileSize * FR), BufferedImage.TYPE_INT_ARGB);

		for (short i = 0; i < tileMap.length; i++) {
			for (short j = 0; j < tileMap[i].length; j++) {
				BufferedImage bf = getImageById(tileMap[i][j]);
				/*
				 * //antialiasing
				 * bf.createGraphics().setRenderingHint(RenderingHints
				 * .KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //
				 * smoother (and slower) image transforms (e.g., for resizing)
				 * bf.createGraphics().setRenderingHint(RenderingHints.
				 * KEY_INTERPOLATION,
				 * RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				 * 
				 * bf.createGraphics().setRenderingHint(RenderingHints.
				 * KEY_ALPHA_INTERPOLATION,
				 * RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY); //
				 */
				b.createGraphics().drawImage(bf, tileSize * FR * j,
						tileSize * FR * i, tileSize * FR, tileSize * FR, null);
			}
		}
		return b;
	}

	/**
	 * @param id
	 *            identificador do tile
	 * 
	 *            Recebe o identificador do tile e retorna a imagem recortada do
	 *            tile set
	 */
	private BufferedImage getImageById(short id) {
		// calculo para achar as coordenadas do frame no frameSet
		// Segundo Jonatan S. Harbour, Programação de Games com java, Cengage
		// Learning, Pg 158
		int fx = (id % nCols) * tileSize;
		int fy = (id / nCols) * tileSize;

		BufferedImage bf = new BufferedImage(tileSize, tileSize,
				BufferedImage.TYPE_INT_ARGB);

		/*
		 * //antialiasing
		 * bf.createGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		 * RenderingHints.VALUE_ANTIALIAS_ON); // smoother (and slower) image
		 * transforms (e.g., for resizing)
		 * bf.createGraphics().setRenderingHint(RenderingHints
		 * .KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		 * 
		 * bf.createGraphics().setRenderingHint(RenderingHints.
		 * KEY_ALPHA_INTERPOLATION,
		 * RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY); //
		 */
		// o indice -1 significa que deve ser impresso uma imagem vazia
		if (id == -1)
			return bf;
		bf.createGraphics().drawImage(tileSet, 0, 0, tileSize, tileSize, fx,
				fy, fx + tileSize, fy + tileSize, null);
		return bf;
	}

	/**
	 * @return nCols Retorna o numero de colunas
	 */
	public short getnCols() {
		return nCols;
	}

	/**
	 * @return nRows Retorna o numero de filas
	 */
	public short getnRows() {
		return nRows;
	}

	/**
	 * @return tileSize Retorna a altura ou a largura da imagem de tile
	 */
	public short getTileSize() {
		return tileSize;
	}

	/**
	 * @return tileSet Retorna o tileSet
	 */
	public BufferedImage getTileSet() {
		return tileSet;
	}

	/**
	 * @return numerodeTile Retorna a quantidade de tiles que a imagem possui
	 */
	public short getNTile() {
		return (short) (getnRows() * getnCols());
	}

	/**
	 * @param im
	 *            imagem que sera o tileSet Valida se a imagem é nula e se não
	 *            for a seta como o tileSet do jogo
	 */
	private void setTileSet(BufferedImage im) {
		if (im == null) {
			System.out.println("Imagem tileset null");
			System.exit(0);
		}
		this.tileSet = im;
	}
}// fim de tile map
