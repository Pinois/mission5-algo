package m5;
import java.io.IOException;

public class Compress {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("SYNTAX : Compress fichierNonCompress? fichierBinaire");
			return;
		}
		System.out.println("Fichier input : "+args[0]);
		System.out.println("Fichier compresse : "+args[1]);
		Huffman huffman = new Huffman();
		try {
			huffman.compress(args[0], args[1]);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
