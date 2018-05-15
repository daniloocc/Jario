package mario.loaders;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import mario.basics.TileImage;
import mario.basics.TileMap;


public class ImagesLoader {

	private final static String IMAGE_DIR = "../Images/";

	private HashMap<String, ArrayList<BufferedImage>> imagesMap;
	private GraphicsConfiguration gc;

	/**
	 * @param fnm o arquivo de configuração das imagens
	 * 
	 * Configura o loader com base nas informações do arquivo fnm
	 */
	public ImagesLoader(String fnm){
		initLoader();
		loadImagesFile(fnm);
	} // fim de ImagesLoader()

	
	public ImagesLoader() {
		initLoader();
	}

	/**
	 * Inicializa as estruturas de armazenamento
	 */
	private void initLoader() {
		imagesMap = new HashMap<String, ArrayList<BufferedImage>>();
		new HashMap<String, ArrayList<String>>();

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
	} // fim de initLoader()

	/**
	* @param fnm
	* 
	* Formatos:
	*   o <fnm> 						// uma unica imagem
	*   s <fnm> <number>				// an images strip
	*   t <tilesetname> <fnm> <tilesz>	// a tiled image
	* 	 ignora linhas em branco e linhas de comentarios
	*/
	private void loadImagesFile(String fnm){
		String imsFNm = IMAGE_DIR + fnm;
		System.out.println("Reading file: " + imsFNm);
		try {
			InputStream in = this.getClass().getResourceAsStream(imsFNm);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// BufferedReader br = new BufferedReader( new FileReader(imsFNm));
			String line;
			char ch;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) // blank line
					continue;
				if (line.startsWith("//")) // comment
					continue;
				ch = Character.toLowerCase(line.charAt(0));
				if (ch == 'o') // a single image
					getFileNameImage(line);
				else if (ch == 's') // an images strip
					getStripImages(line);				
				else if (ch == 't')
					getTiledImage(line);
				else
					System.out.println("Não reconheco a linha: " + line);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("Erro lendo o arquivo: " + imsFNm);
			System.exit(1);
		}
	} // fim de loadImagesFile()

	// --------- carrega uma imagem só -------------------------------

	/**
	 * @param line
	 * 
	 * formato: o <fnm>
	 */
	private void getFileNameImage(String line){
		StringTokenizer tokens = new StringTokenizer(line);

		if (tokens.countTokens() != 2)
			System.out.println("Numero errado de argumentos em " + line);
		else {
			tokens.nextToken(); // pula o token do comando
			System.out.print("Linha o: ");
			loadSingleImage(tokens.nextToken());
		}
	} // fim de  getFileNameImage()


	/**
	 * @param fnm
	 * 
	 * Carrega a imagem da estrutura de armazenamento
	 * Pode ser chamado diretamente
	 */
	public boolean loadSingleImage(String fnm){
		String name = getPrefix(fnm);

		if (imagesMap.containsKey(name)) {
			System.out.println("Erro: " + name + "ja usado");
			return false;
		}

		BufferedImage bi = loadImage(fnm);
		if (bi != null) {
			ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
			imsList.add(bi);
			imagesMap.put(name, imsList);
			System.out.println("  Stored " + name + "/" + fnm);
			return true;
		} else
			return false;
	} // fim de loadSingleImage()

	/**
	 * @param fnm
	 * @return prefix 
	 * 				recebe o nome do arquivo e retorna o valor antes do '.'
	 */
	private String getPrefix(String fnm){
		int posn;
		if ((posn = fnm.lastIndexOf(".")) == -1) {
			System.out.println("No prefix found for filename: " + fnm);
			return fnm;
		} else
			return fnm.substring(0, posn);
	} // fim de getPrefix()

	// --------- carregando a faixa de imagens -------------------------------

	/**
	 * @param line
	 * 
	 * format: s <fnm> <number>
	 */
	private void getStripImages(String line){
		
		StringTokenizer tokens = new StringTokenizer(line);

		if (tokens.countTokens() != 3)
			System.out.println("Numero errado de argumentos em " + line);
		else {
			tokens.nextToken(); // pula o token de comando
			System.out.print("Linha s: ");

			String fnm = tokens.nextToken();
			int number = -1;
			try {
				number = Integer.parseInt(tokens.nextToken());
			} catch (Exception e) {
				System.out.println("O numero esta incorreto em  " + line);
			}

			loadStripImages(fnm, number);
		}
	} // fim de getStripImages()

	/**
	 * @param fnm
	 * @param number
	 * 
	 * Can be called directly, to load a strip file, <fnm>, holding <number>
	 * images.
	 */
	public int loadStripImages(String fnm, int number)
	{
		String name = getPrefix(fnm);
		if (imagesMap.containsKey(name)) {
			System.out.println("Error: " + name + "already used");
			return 0;
		}
		// carrega as imagens em um array
		BufferedImage[] strip = loadStripImageArray(fnm, number);
		if (strip == null)
			return 0;

		ArrayList<BufferedImage> imsList = new ArrayList<BufferedImage>();
		int loadCount = 0;
		System.out.println("  Adding " + name + "/" + fnm + "... ");
		for (int i = 0; i < strip.length; i++) {
			loadCount++;
			imsList.add(strip[i]);
			System.out.print(i + " ");
		}
		System.out.println();

		if (loadCount == 0)
			System.out.println("No images loaded for " + name);
		else
			imagesMap.put(name, imsList);

		return loadCount;
	} // fim de loadStripImages()

	public BufferedImage[] loadStripImageArray(String fnm, int number)
	/*
	 * Extract the individual images from the strip image file, <fnm>. We assume
	 * the images are stored in a single row, and that there are <number> of
	 * them. The images are returned as an array of BufferedImages
	 */
	{
		if (number <= 0) {
			System.out.println("number <= 0; returning null");
			return null;
		}

		BufferedImage stripIm;
		if ((stripIm = loadImage(fnm)) == null) {
			System.out.println("Returning null");
			return null;
		}

		int imWidth = (int) stripIm.getWidth() / number;
		int height = stripIm.getHeight();
		int transparency = stripIm.getColorModel().getTransparency();

		BufferedImage[] strip = new BufferedImage[number];
		Graphics2D stripGC;

		// each BufferedImage from the strip file is stored in strip[]
		for (int i = 0; i < number; i++) {
			strip[i] = gc.createCompatibleImage(imWidth, height, transparency);

			// create a graphics context
			stripGC = strip[i].createGraphics();
			// stripGC.setComposite(AlphaComposite.Src);

			// copy image
			stripGC.drawImage(stripIm, 0, 0, imWidth, height, i * imWidth, 0,
					(i * imWidth) + imWidth, height, null);
			stripGC.dispose();
		}
		return strip;
	} // fim de loadStripImageArray()
	

	/*-----------TILE MANIPULATION----------------------------*/
	public void getTiledImage(String line) {
		StringTokenizer tok = new StringTokenizer(line);
		if (tok.countTokens() != 4) {
			System.out.println("Wrong number of arguments for a tiled image");
			return;
		}
		tok.nextToken();// skip the command
		String tsName = tok.nextToken();
		BufferedImage tileset = getImage(tsName);
		String fnm = tok.nextToken();
		short tileSize = Short.parseShort(tok.nextToken());

		if (tileset == null) {
			System.out.println("No tile set stored under: " + tsName);
			return;
		}

		TileMap tMap = new TileMap(tileset, tileSize);
		TileImage timg = new TileImage(IMAGE_DIR+fnm);
		
		System.out.println("t Line:\tStored "+fnm+"/"+ getPrefix(fnm)
				+ " from " + tsName);
		loadTiledImage(getPrefix(fnm), tMap.getImageFromTileMap(timg));

	}

	private void loadTiledImage(String nameImage, BufferedImage img) {
		if (imagesMap.containsKey(nameImage)) {
			System.out.println("Já Contem uma imagem sob o nome de "
					+ nameImage);
			return;
		}
		ArrayList<BufferedImage> i = new ArrayList<BufferedImage>();
		i.add(img);
		imagesMap.put(nameImage, i);
	}



	/*---------fim de TILE MANIPULATION-----------------------*/

	// ------------------ access methods -------------------

	public BufferedImage getImage(String name)
	/*
	 * Get the image associated with <name>. If there are several images stored
	 * under that name, return the first one in the list.
	 */
	{
		ArrayList<?> imsList = (ArrayList<?>) imagesMap.get(name);
		if (imsList == null) {
			System.out.println("No image(s) stored under " + name);
			return null;
		}

		// System.out.println("Returning image stored under " + name);
		return (BufferedImage) imsList.get(0);
	} // fim de getImage() with name input;

	/**/
	public BufferedImage getInvertedImage(String name){		
		/*se já contiver a imagem armazenada, então só retorne-a
		 * caso contrario renderize uma
		 */
		String name2 = name+"1";
		if(imagesMap.containsKey(name2)){
			return getImage(name2);
		}
				
		BufferedImage im = getImage(name);
		BufferedImage out = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
		out.createGraphics().drawImage(im, 0,0, im.getWidth(), im.getHeight(),
											im.getWidth(), 0, 0, im.getHeight(), null);
		ArrayList<BufferedImage> l = new ArrayList<BufferedImage>();
		l.add(out);
		imagesMap.put(name2, l);
		return l.get(0);
	}
	
	
	public BufferedImage getImage(String name, int posn)
	/*
	 * Get the image associated with <name> at position <posn> in its list. If
	 * <posn> is < 0 then return the first image in the list. If posn is bigger
	 * than the list's size, then calculate its value modulo the size.
	 */
	{
		ArrayList<?> imsList = (ArrayList<?>) imagesMap.get(name);
		if (imsList == null) {
			System.out.println("No image(s) stored under " + name);
			return null;
		}

		int size = imsList.size();
		if (posn < 0) {
			// System.out.println("No " + name + " image at position " + posn +
			// "; return position 0");
			return (BufferedImage) imsList.get(0); // return first image
		} else if (posn >= size) {
			// System.out.println("No " + name + " image at position " + posn);
			int newPosn = posn % size; // modulo
			// System.out.println("Return image at position " + newPosn);
			return (BufferedImage) imsList.get(newPosn);
		}

		// System.out.println("Returning " + name + " image at position " +
		// posn);
		return (BufferedImage) imsList.get(posn);
	} // fim de getImage() with posn input;

	public ArrayList<?> getImages(String name)
	// return all the BufferedImages for the given name
	{
		ArrayList<?> imsList = (ArrayList<?>) imagesMap.get(name);
		if (imsList == null) {
			System.out.println("No image(s) stored under " + name);
			return null;
		}

		System.out.println("Returning all images stored under " + name);
		return imsList;
	} // fim de getImages();

	public boolean isLoaded(String name)
	// is <name> a key in the imagesMap hashMap?
	{
		ArrayList<?> imsList = (ArrayList<?>) imagesMap.get(name);
		if (imsList == null)
			return false;
		return true;
	} // fim de isLoaded()

	public int numImages(String name)
	// how many images are stored under <name>?
	{
		ArrayList<?> imsList = (ArrayList<?>) imagesMap.get(name);
		if (imsList == null) {
			System.out.println("No image(s) stored under " + name);
			return 0;
		}
		return imsList.size();
	} // fim de numImages()

	// ------------------- Image Input ------------------

	/*
	 * There are three versions of loadImage() here! They use: ImageIO // the
	 * preferred approach ImageIcon Image We assume that the BufferedImage copy
	 * required an alpha channel in the latter two approaches.
	 */

	public BufferedImage loadImage(String fnm)
	/*
	 * Load the image from <fnm>, returning it as a BufferedImage which is
	 * compatible with the graphics device being used. Uses ImageIO.
	 */
	{
		try {
			BufferedImage im = ImageIO.read(getClass().getResource(
					IMAGE_DIR + fnm));
			// An image returned from ImageIO in J2SE <= 1.4.2 is
			// _not_ a managed image, but is after copying!

			int transparency = im.getColorModel().getTransparency();
			BufferedImage copy = gc.createCompatibleImage(im.getWidth(),
					im.getHeight(), transparency);
			// create a graphics context
			Graphics2D g2d = copy.createGraphics();
			// g2d.setComposite(AlphaComposite.Src);

			// reportTransparency(IMAGE_DIR + fnm, transparency);

			// copy image
			g2d.drawImage(im, 0, 0, null);
			g2d.dispose();
			return copy;
		} catch (IOException e) {
			System.out.println("Load Image error for " + IMAGE_DIR + "/" + fnm
					+ ":\n" + e);
			return null;
		}
	} // fim de loadImage() using ImageIO

} // fim de ImagesLoader class
