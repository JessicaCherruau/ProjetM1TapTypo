package boyot.fr.TapTypo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class WordGenerator {
    /*****************************************************************************************
     * Attributs *****************************************************************************
     */
    /**
     * DICTIONNARY_PATH contient le chemin vers le fichier texte contenant le dictionnaire
     * Il s'agit d'une liste de mots, chaque mot est séparé d'un retour à la ligne
     */
    private final static String DICTIONNARY_PATH = "raw/dico.txt";
    /**
     * ALPHABET est une chaine de caractères contenant les lettres de l'alphabet
     * utile pour choisir une lettre au hasard
     */
    private final static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    /**
     * Une référence vers un unique objet WordGenerator, entre dans l'implémentation du pattern Singleton
     */
    private static WordGenerator wg = null;
    /**
     * ArrayList contenant la liste des mots du fichier
     */
    private ArrayList<String> dictionnary;

    /*****************************************************************************************
     * Méthodes ******************************************************************************
     *****************************************************************************************/
    /**
     * Constructeur privé
     * Instancie la array list dictionnary à partir des mots d'un fichier
     */
    private WordGenerator(boolean multi)
    {
        this.dictionnary = new ArrayList<String>();
        BufferedReader is = null;
        // lecture du fichier, on insère tous les mots dans la liste
        if(multi)
            is = new BufferedReader(new InputStreamReader(TapTypoHostActivity.reader));
        else
            is = new BufferedReader(new InputStreamReader(TapTypoActivity.reader));

        String str = "";
        try
        {
            while((str = is.readLine()) != null) // tant que le fichier n'est pas vide
            {
                this.dictionnary.add(str.toUpperCase());
            }
        } catch (IOException e) {
            System.err.println("Erreur entrée/sortie : "+e.getMessage());
        }
    }
    /**
     * Permet d'accéder à l'unique instance de WordGenerator
     * Entre dans l'implémentation du pattern Singleton
     * @return l'objet WordGenerator
     */
    public static WordGenerator getInstance(boolean multi)
    {
        // si l'objet n'a pas encore été instancié, on le crée
        if(WordGenerator.wg == null)
            WordGenerator.wg = new WordGenerator(multi);
        return WordGenerator.wg;
    }
    /**
     * Génère une liste de mots aléatoires
     * @param length longueur de la liste souhaitée
     * @return ArrayList<String> la liste
     */
    public ArrayList<String> generateWordList(int length)
    {
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0 ; i < length ; i++)
        {
            String r = this.getRandomWord(list);
            list.add(r);
        }
        return list;
    }
    /**
     * Choisit un mot au hasard dans le dictionnaire
     * @return String un mot
     */
    public String getRandomWord()
    {
        return this.getRandomWord(new ArrayList<String>());
    }
    /**
     * Choisi un mot au hasard dans le dictionnaire n'étant pas dans la liste passée en paramètre
     * @param alreadyInserted liste des mots que l'on ne veut pas retourner
     * @return String un mot
     */
    public String getRandomWord(ArrayList<String> alreadyInserted)
    {
        String random = "";
        Random r = new Random();
        int low = 0;
        int high = this.dictionnary.size();
        do
        {
            int randomIndex = r.nextInt(high-low) + low;
            random = this.dictionnary.get(randomIndex);
            // on sort du while seulement si le mot n'est pas vide ou si le mot n'existe pas déjà dans la liste
        } while(random.equals("") || alreadyInserted.contains(random));
        return random;
    }
    /**
     * Retourne les lettres d'un mot : la dernière lettre devient la première , etc
     * par exemple : exemple devient elpmexe
     * @param s le mot
     * @return le mot retourné
     */
    public String reverseWord(String s)
    {
        String reversed = "";
        // parcours à l'envers de la chaine passée en paramètre
        for(int i = s.length() - 1 ; i >= 0 ; i--)
        {
            reversed = reversed + s.charAt(i);
        }
        return reversed;
    }
    /**
     * Remplace une lettre aléatoire du mot par une autre lettre aléatoire
     * @param s le mot auquel il faut donner un malus
     * @return le mot "malusé"
     */
    public String addMalusToWord(String s)
    {
        char[] malused = s.toCharArray();
        Random rand = new Random();
        char randomLetter;
        int randomIndex = rand.nextInt(s.length()); // l'indice de la lettre à remplacer dans le mot
        // une lettre random va être changée par une autre, choisie aléatoirement dans l'alphabet
        // il ne faut pas que la lettre qui remplace soit la même que celle a remplacé
        do
        {
            randomLetter = WordGenerator.ALPHABET.charAt(rand.nextInt(WordGenerator.ALPHABET.length()));
        } while (randomLetter == malused[randomIndex]);
        malused[randomIndex] = randomLetter;
        return new String(malused);
    }
}
