package m5;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import IO.InputBitStream;
import m5.io.Fichier;
import m5.io.OutputBitStreams;


/**
 * 
 * @author Ludovic Fastre, Denis Marinx
 *
 */
public class Huffman {

	private String header;
	private String nbrLettres;
	int nbrL;

	public Huffman() {
	}

	/**
	 * Lis le fichier non encod? en input et le compresse dans le fichier output
	 * 
	 * @param fileNameInput
	 *            le nom du fichier input
	 * @param fileNameOutput
	 *            le nom du fichier output
	 * @throws IOException
	 *             Erreurs relatifs aux fichiers
	 */
	public void compress(String fileNameInput, String fileNameOutput) throws IOException {

		String text = readMessageUnencoded(fileNameInput);
		if (text.length() == 0) {
			throw new IllegalArgumentException("Il doit y avoir au moins un caract?re");
		}
		// On commence par cr?er un compteur de caract?res par le biais d'une
		// HashMap
		CharacterCounter cc = new CharacterCounter();
		// On ajoute la string pour remplir cette HashMap
		cc.add(text);
		// On construit l'arbre ? partir des entr?es de la HashMap
		HuffmanNode root = buildTree(cc.getEntries());
		header = Integer.toBinaryString(cc.getEntries().size());
		header(root);
		System.out.println(header);
		// On cr?e une HashMap qui va reprendre pour chaque caract?re le
		// CharCode correspondant.
		// Il faut le faire en infixe en principe
		Map<Character, String> charCodes = new HashMap<Character, String>();
		generateCode(root, charCodes, "");
		// On encode le string ? partir des charCodes g?n?r?s
		String encodedMessage = encodeMessage(charCodes, text);
		//System.out.println("Message compresse : "+encodedMessage);
		// On s?rialise le string dans un fichier
		serializeMessage(header+encodedMessage, fileNameOutput);
	}

	/**
	 * Transforme un string en chaine de bits
	 */
	private String stringToBinary(String s){
		byte[] bytes = s.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes)
		{
			int val = b;
			for (int i = 0; i < 8; i++)
			{
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		return binary.toString();
	}

	/**
	 * Contruit le header du fichier de compression
	 * @param root
	 */
	private void header(HuffmanNode root)
	{
		if(root == null) {
			return;
		}

		if(root.ch == '\0')
			header = header+"0";
		else
			header = header+"1"+stringToBinary(String.valueOf(root.ch));
		header(root.left);
		header(root.right);
	}

	/**
	 * Lis le fichier en input pour renvoyer une String de son contenu
	 * 
	 * @param fileNameInput
	 *            le nom du fichier input
	 * @return le contenu du fichier ? lire
	 */
	public String readMessageUnencoded(String fileNameInput) {
		Fichier fichierR = new Fichier();
		fichierR.ouvrir(fileNameInput, 'R');

		String sentence = "";
		String ligneInput = null;
		while ((ligneInput = fichierR.lire()) != null) {
			sentence += ligneInput;
		}
		fichierR.fermer();
		return sentence;
	}

	/**
	 * Lis le fichier encod? en input et le decompresse dans le fichier output
	 * 
	 * @param fileNameInput
	 *            le nom du fichier input
	 * @param fileNameOutput
	 *            le nom du fichier output
	 * @throws IOException
	 *             Erreurs relatifs aux fichiers
	 */
	public void decompress(String fileNameInput, String fileNameOutput) throws IOException {
		String messageUnEncoded = decodeMessage(fileNameInput);
		writeMessageUnencoded(fileNameOutput, messageUnEncoded);
	}

	public void writeMessageUnencoded(String fileNameOutput, String messageUnEncoded) {
		Fichier fichierW = new Fichier();
		fichierW.ouvrir(fileNameOutput, 'W');
		fichierW.ecrire(messageUnEncoded);
		fichierW.fermer();
	}

	/**
	 * Construit un arbre de recherche Map<Character, Integer> map Some implementation of that treeSet is passed as parameter.
	 * 
	 * @param map
	 */
	private HuffmanNode buildTree(Set<Entry<Character, Integer>> entries) {
		// On cr?er une file de priorit? de caract?re avec leur fr?quence
		final Queue<HuffmanNode> nodeQueue = new PriorityQueue<HuffmanNode>(entries.size(), new Comparator<HuffmanNode>() {
			public int compare(HuffmanNode node1, HuffmanNode node2) {
				if(node1.frequency < node2.frequency)
					return -1;
				else if(node1.frequency > node2.frequency)
					return 1;
				else{
					if(node1.ch == '\0' || node2.ch == '\0')
						return -1;
					else if((int)node1.ch < (int)node2.ch)
						return -1;
					else if((int)node1.ch > (int)node2.ch)
						return 1;
					else
						return 0;
				}
			}
		});

		// On ajoute tous les noeuds dans la priority queue
		for (Entry<Character, Integer> entry : entries) {
			nodeQueue.add(new HuffmanNode(entry.getKey(), entry.getValue(), null, null));
		}

		// Tant que l'arbre n'est pas construit, donc qu'on ait plus qu'un seul noeud "root"
		while (nodeQueue.size() > 1) {
			// On retire les 2 premiers noeuds de la priority queue
			final HuffmanNode node1 = nodeQueue.remove();
			final HuffmanNode node2 = nodeQueue.remove();
			HuffmanNode node = new HuffmanNode('\0', node1.frequency + node2.frequency, node1, node2);
			// On ajoute un noeud parent contenant les 2 retir?s
			nodeQueue.add(node);
		}
		return nodeQueue.remove();
	}

	/**
	 * 
	 * @param node
	 *            l'?lement root de l'arbre
	 * @param map
	 *            la map dans lequel on va attribuer les charCodes pour chaque char
	 * @param s
	 *            il s'agit du code binaire (charCode) en construction pour chaque noeud
	 */
	private void generateCode(HuffmanNode node, Map<Character, String> map, String s) {
		if (node.left == null && node.right == null) {
			map.put(node.ch, s);
			return;
		}
		// On descend r?cursivement dans l'arbre de gauche et de droite pour attribuer le hashcode correspondant au char
		generateCode(node.left, map, s + '0');
		generateCode(node.right, map, s + '1');
	}

	/**
	 * 
	 * @param charCode
	 *            Le code binaire correspondant ? chaque caract?re
	 * @param sentence
	 *            Le string ? encoder
	 * @return Retourne la string sous forme de code binaire.
	 */
	private String encodeMessage(Map<Character, String> charCode, String sentence) {
		final StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < sentence.length(); i++) {
			stringBuilder.append(charCode.get(sentence.charAt(i)));
		}
		return stringBuilder.toString();
	}

	/**
	 * 
	 * @param encodedMessage
	 *            le texte encod?
	 * @param fileNameOutput
	 *            le nom du fichier output
	 * @throws IOException
	 */
	private void serializeMessage(String encodedMessage, String fileNameOutput) throws IOException {
		final boolean[] bitSet = getBitSet(encodedMessage);
		OutputBitStreams obs = new OutputBitStreams(fileNameOutput);
		for(int i = 0 ; i<bitSet.length ; i++){
			System.out.print(bitSet[i] ? 1 : 0);
			obs.write(bitSet[i]);
		}
		obs.close();
	}

	private boolean[] getBitSet(String message) {
		boolean[] bitSet = new boolean[message.length()];
		boolean bit_1 = true;
		boolean bit_0 = false;
		int i = 0;
		for (i = 0; i < message.length(); i++) {
			boolean b = (message.charAt(i) != '0') ? bit_1 : bit_0;
			bitSet[i] = b;
		}
		return bitSet;
	}

	// A COMPLETER
	/**
	 * 
	 * @param fileNameInput
	 *            le nom du fichier encod? en input
	 * @return texte d?cod?
	 * @throws IOException
	 */
	private String decodeMessage(String fileNameInput) throws IOException {
		InputBitStream in = new InputBitStream(fileNameInput);
		String result = "";
		nbrLettres = "";
		for(int i = 0 ; i<6 ; i++){
			boolean resu = in.readBoolean();
			nbrLettres = nbrLettres+(resu ? 1 : 0);
		}
		nbrL = Integer.parseInt(nbrLettres, 2);
		HuffmanNode root = new HuffmanNode('\0', 0, null, null);
		decodeHeader(root, in);
		HuffmanNode tempRoot = root;
		try {
			// Lecture bit ? bit du flux d'entr?e et impression du bit lu
			while (true) {
				boolean resu = in.readBoolean();
				if(resu){
					tempRoot = tempRoot.right;
				}
				else{
					tempRoot = tempRoot.left;
				}
				if(tempRoot.ch != '\0'){
					result = result+tempRoot.ch;
					tempRoot = root;
				}
			}
		} catch (IOException e) { // Exception lanc?e notamment en fin de fichier
			System.out.println("");
			in.close();
		}
		
		try {
			  // Lecture bit ? bit du flux d'entr?e et impression du bit lu
			  while (true) {
			    boolean resu = in.readBoolean();
			    System.out.print (resu ? 1 : 0); 
			  }
			} catch (IOException e) { // Exception lanc?e notamment en fin de fichier
			  System.out.println("");
			  in.close();
			}

		return result;
	}

	private char readLetter(InputBitStream in) throws IOException{
		String letter = "";
		for(int i = 0 ; i < 8 ; i++){
			boolean resu = in.readBoolean();
			letter = letter+(resu ? 1 : 0);
		}
		int charCode = Integer.parseInt(letter, 2);
		return new Character((char)charCode);
	}

	private void decodeHeader(HuffmanNode root, InputBitStream in) throws IOException
	{
		if(root == null) {
			return;
		}

		boolean resu = in.readBoolean();

		if(resu){
			root.ch = readLetter(in);
		}
		else{
			root.left = new HuffmanNode('\0', 0, null, null);
			root.right = new HuffmanNode('\0', 0, null, null);
		}

		decodeHeader(root.left, in);
		decodeHeader(root.right, in);
	}

	private class HuffmanNode {
		char ch;
		int frequency;
		HuffmanNode left;
		HuffmanNode right;

		HuffmanNode(char ch, int frequency, HuffmanNode left, HuffmanNode right) {
			this.ch = ch;
			this.frequency = frequency;
			this.left = left;
			this.right = right;
		}
	}
}
