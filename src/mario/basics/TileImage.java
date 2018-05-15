package mario.basics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author Danilo
 * @version 0.0001XD
 * @since 08/10/2012
 * 
 *        Tile image não guarda imagem em si, Ela apenas guarda coordenadas com
 *        indices de imagens de tile, para serem renderizadas e retornadas
 *        quando é solicitado.
 */
public class TileImage {

	private short[][] tileMap;

	/**
	 * @param tileMap
	 *            matriz com a configuração da imagem
	 * 
	 *            Instancia a imagem de tile com base na matriz de configuração
	 *            que é passado no construtor
	 */
	public TileImage(short[][] tileMap) {
		this.tileMap = tileMap;
		decreaseIndex();
	}

	/**
	 * @param fnm
	 *            nome do arquivo de texto com a configuração da imagem
	 * @exception IOException
	 *                pode gerar um erro durante a leitura do arquivo
	 * 
	 *                Instancia a imagem de tile com base na configuração que é
	 *                passado no construtor
	 */
	public TileImage(String fnm) {
		// le o arquivo passado como parametro
		InputStream in = getClass().getResourceAsStream(fnm);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		ArrayList<Short[]> lista = new ArrayList<Short[]>();
		try {
			System.out.println("Reading file " + fnm);
			while ((line = br.readLine()) != null) {
				if (line.startsWith("//"))
					continue;
				if (!Character.isDigit(line.charAt(0)))
					continue;
				StringTokenizer tok = new StringTokenizer(line, ",");
				Short[] shortline = new Short[tok.countTokens()];

				int i = 0;
				while (tok.hasMoreTokens())
					shortline[i++] = Short.parseShort(tok.nextToken());

				lista.add(shortline);
			}
			br.close();
			in.close();
		} catch (IOException e) {
			System.out.println("Algo errado aconteceu");
			System.out.println("Não pude ler " + fnm);
		}

		short[][] out = new short[lista.size()][lista.get(0).length];

		/* casting de ArrayList para short[][] */
		for (int i = 0; i < lista.size(); i++)
			for (int j = 0; j < lista.get(i).length; j++)
				out[i][j] = lista.get(i)[j];

		this.tileMap = out;
		decreaseIndex();
	}

	/**
	 * @return tileMap a matriz com os indices das imagens
	 * 
	 *         Deve retornar um tile map da fase recomendo o uso do programa
	 *         Mappy
	 */
	public final short[][] getTileMap() {
		return tileMap;
	}// fim de getTileMap()

	/**
	 * Devido a uma limitação do programa mappy, que insere uma nova imagem no
	 * inicio do tile set e arromba com a logica de busca de frame, crio o
	 * metodo decreaseIndex que decrementa todos os index da matriz timeMap[][],
	 * removendo o "elemento 0" que foi posto no conjunto do tile. Por
	 * definição, o mappy define o tile "0" como vazio, logo, depois da
	 * decrementação o indice "0" sera "-1". Ohhh!!!
	 */
	private final void decreaseIndex() {
		for (int i = 0; i < tileMap.length; i++) {
			for (int j = 0; j < tileMap[i].length; j++) {
				tileMap[i][j]--;
			}
		}
	}// fim de decreaseIndex()
}
