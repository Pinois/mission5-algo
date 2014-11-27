package m5;
import java.io.IOException;

public class Decompress {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("SYNTAX : Decompress fichierBinaire fichierNonCompress?");
			return;
		}
		Huffman huffman = new Huffman();
		try {
			huffman.decompress(args[0], args[1]);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
