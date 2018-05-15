package mario.loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import mario.managers.ClipInfo;
import mario.managers.SoundsWatcher;

/**
 * @author Danilo 
 * @author Andrew Davison
 * @version 0.0001XD
 * @since 08/10/2012
 * 		  ClipsLoader armazena uma coleção de objetos ClipInfo em um
 *        objeto HashMap cuja chaves saõ seus nomes;
 * 
 *        O nome e o nome do arquivo são obtidos a partir de um arquivo de
 *        configuração de sons que é carregado quando o objeto ClipsLoader é
 *        criado.
 * 
 *        O arquivo de informação supostamente esta em ../sounds/
 * 
 *        ClipsLoader permite que um clip especifico seja tocado, parado,
 *        pausado, resumido ou repetido. um ouvinte de som pode ser anexado ao
 *        clip. Todas essas funcionalidades são manipuladas no objeto ClipInfo.
 * 
 *        ClipsLoader simplesmente redireciona o metodo de chamada para o certo
 *        ClipInfo
 * 
 *        É possivel que muitos clips sejam tocados ao mesmo tempo, já que cada
 *        clip é responsavel por tocar seu proprio clip
 */

public class ClipsLoader {

	private final static String SOUND_DIR = "../Sounds/";

	// A chave é o nome do clip, e o valor é o objeto clipInfo
	private HashMap<String, ClipInfo> clipsMap;

	/**
	 * @param soundsFnm
	 *            nome do arquivo de configuração onde os sons serao carrecados
	 */
	public ClipsLoader(String soundsFnm) {
		clipsMap = new HashMap<String, ClipInfo>();
		loadSoundsFile(soundsFnm);
	}

	/**
	 * Inicializa o loader vazio.
	 */
	public ClipsLoader() {
		clipsMap = new HashMap<String, ClipInfo>();
	}

	/**
	 * @param soundsFnm
	 *            nome do arquivo de som a ser lido
	 * 
	 *            The file format are lines of: <name> <filename> // a single
	 *            sound file and blank lines and comment lines.
	 */
	private void loadSoundsFile(String soundsFnm) {
		String sndsFNm = SOUND_DIR + soundsFnm;
		System.out.println("Reading file: " + sndsFNm);
		try {
			InputStream in = this.getClass().getResourceAsStream(sndsFNm);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			// BufferedReader br = new BufferedReader( new FileReader(sndsFNm));
			StringTokenizer tokens;
			String line, name, fnm;
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) // linha em branco
					continue;
				if (line.startsWith("//")) // comentario
					continue;

				tokens = new StringTokenizer(line);
				if (tokens.countTokens() != 2)
					System.out.println("Wrong no. of arguments for " + line);
				else {
					name = tokens.nextToken();
					fnm = tokens.nextToken();
					load(name, fnm);
				}
			}
			br.close();
		} catch (IOException e) {
			System.out.println("Error reading file: " + sndsFNm);
			System.exit(1);
		}
	} // fim de loadSoundsFile()

	// ----------- manipulando um vlip em particular --------

	/**
	 * @param name
	 *            chave do objeto no hashMap
	 * @param fnm
	 *            Nome no arquivo create a ClipInfo object for name and store it
	 */
	public void load(String name, String fnm) {
		if (clipsMap.containsKey(name))
			System.out.println("Error: " + name + "already stored");
		else {
			clipsMap.put(name, new ClipInfo(name, fnm));
			System.out.println("-- " + name + "/" + fnm);
		}
	} // fim de load()

	/**
	 * @param name
	 * 
	 *            fecha o clip especificado
	 */
	public void close(String name) {
		ClipInfo ci = (ClipInfo) clipsMap.get(name);
		if (ci == null)
			System.out.println("Error: " + name + "not stored");
		else
			ci.close();
	} // fim close()

	/**
	 * @param name
	 * @param toLoop
	 * 
	 *            toca o clip especificado, e repete continuamente se o toLoop
	 *            for true
	 */

	public void play(String name, boolean toLoop) {
		ClipInfo ci = (ClipInfo) clipsMap.get(name);
		if (ci == null)
			System.out.println("Error: " + name + "not stored");
		else
			ci.play(toLoop);
	} // fim de play()

	/**
	 * @param name
	 * 
	 *            para o clip especificado, resetando-o para o inicio
	 */
	public void stop(String name) {
		ClipInfo ci = (ClipInfo) clipsMap.get(name);
		if (ci == null)
			System.out.println("Error: " + name + "not stored");
		else
			ci.stop();
	} // fim de stop()

	/**
	 * @param name
	 * 
	 *            pausa o clip especificado
	 */
	public void pause(String name) {
		ClipInfo ci = (ClipInfo) clipsMap.get(name);
		if (ci == null)
			System.out.println("Error: " + name + "not stored");
		else
			ci.pause();
	} // fim de pause()

	/**
	 * @param name
	 * 
	 *            continua o clip especificado
	 */
	public void resume(String name) {
		ClipInfo ci = (ClipInfo) clipsMap.get(name);
		if (ci == null)
			System.out.println("Error: " + name + "not stored");
		else
			ci.resume();
	} // fim de resume()

	// -------------------------------------------------------

	/**
	 * @param name
	 * @param sw
	 * 
	 *            Seta um watcher para o clip. Ele sera notificado quando o clip
	 *            repete ou para.
	 */
	public void setWatcher(String name, SoundsWatcher sw) {
		ClipInfo ci = (ClipInfo) clipsMap.get(name);
		if (ci == null)
			System.out.println("Error: " + name + "not stored");
		else
			ci.setWatcher(sw);
	} // fim de setWatcher()

} // end of ClipsLoader class
