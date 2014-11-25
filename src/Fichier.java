import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Fichier {
	private BufferedReader bR = null;
	private BufferedWriter bW = null;
	private char mode = '\0';
	
	/**
	 * Ouvre un fichier en lecture ou en écriture
	 * 
	 * @param nomFichier
	 * 					Le nom du fichier
	 * @param mode
	 * 					Le mode d'ouverture du fichier
	 * 
	 */
	public void ouvrir(String nomFichier, char mode){
		try {
			if(mode == 'R'){
				this.mode = 'R';
				this.bR = new BufferedReader(new FileReader(new File(nomFichier)));
			}
			else if (mode == 'W'){
				this.mode = 'W';
				this.bW = new BufferedWriter(new FileWriter(new File(nomFichier)));
			}
			else{
				return;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Ecrit une chaine de caractère dans un fichier ouvert en écriture
	 * 
	 * @pre Le fichier doit etre ouvert en ecriture
	 * 
	 * @param chaine
	 * 					La chaine à écrire
	 */
	public void ecrire(String chaine){
		if(mode != 'W'){
			return;
		}
		if(chaine != null){
			try {
				bW.write(chaine, 0, chaine.length());
				bW.newLine();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * retourne une ligne d'un fichier ouvert en lecture;
	 * 
	 * @pre Le fichier doit etre ouvert en lecture
	 * 
	 * @return la ligne lue
	 */
	public String lire(){
		if(mode != 'R'){
			return null;
		}
		String ligne = null;
		try {
			ligne = bR.readLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return ligne;
	}
	
	
	/**
	 * ferme un fichier
	 * 
	 * @pre Le fichier doit etre ouvert
	 */
	public void fermer(){
		if(this.mode == 'R'){
			try {
				bR.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		else if(this.mode == 'W'){
			try {
				bW.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		this.mode = '\0';
	}
}
