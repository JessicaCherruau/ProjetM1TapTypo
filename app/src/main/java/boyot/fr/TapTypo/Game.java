package boyot.fr.TapTypo;

import java.util.ArrayList;
import java.util.Iterator;

public class Game
{
    private static final int NB_WORDS = 21; //nombre de mots dans une partie
    private int cursorWord;
    private ArrayList<Word> words;
    private Chrono chrono;
    private Statistics stats;

    public Game()
    {
        this.cursorWord = 0;
        this.stats = new Statistics();

        WordGenerator wg = WordGenerator.getInstance();
        ArrayList<String> partie = wg.generateWordList(NB_WORDS);
        this.words = new ArrayList<Word>();

        for (int i=0; i<partie.size() ;i++) // initialisation de l'arraylist
        {
            if (i<5 || ( i>9 && i<15 ) || i==20 )
            {
                this.words.add( new Word( partie.get(i) ) );
            }
            else
            {
                this.words.add( new Word( reverseWord(partie.get(i) ), partie.get(i).length()-1 ) );
            }

        }
        this.chrono = new Chrono();

    }

    /**
     * Arrête le chronomètre du jeu
     */
    public void endGame(){
        if(checkGameEnd()) {
            chrono.stopChrono();
            //si le mot est sans erreur, on augmente les séries sans erreur
            stats.editStreaks(words.get(cursorWord).faultWord());
            this.stats.setTimelapse(chrono.getTimelapse());
        }
    }

    /**
     * enregistre une erreur sur un mot
     */
    public void errorOnWord(){
        words.get(cursorWord).newFault();
        stats.incrementError();
    }

    /**
     * Débute le chronomètre du jeu
     */
    public void startChrono(){
        chrono.startChrono();
    }

    /**
     *
     * @return les statistiques de la partie
     */
    public Statistics getStatistics(){
        return this.stats;
    }
    /**
     * Retourne les lettres d'un mot : la dernière lettre devient la première , etc
     * par exemple : exemple devient elpmexe
     * @param s le mot
     * @return le mot retourné
     */
    public static String reverseWord(String s)
    {
        String reversed = "";
        // parcours à l'envers de la chaine passée en paramètre
        for(int i = s.length() - 1 ; i >= 0 ; i--)
        {
            reversed = reversed + s.charAt(i);
        }
        return reversed;
    }


    public Boolean checkGameEnd()
    {
        if ( ( words.size()-1 == cursorWord ) && ( words.get(cursorWord).checkWordEnd() ) ) // verification que l'on se trouve bien a la fin du dernier mot
        {
            return true;
        }
        return false;
    }

    /**
     * Passe au mot suivant dans la liste de mots de la partie
     * @return string avec le mot suivant
     */
    public String newWord()
    {
        if ( ( cursorWord < words.size() ) )
        {
            if ( (words.get(cursorWord).checkWordEnd() ) ) // verification que le joueur a bien trouver le mot prececent
            {
                //si le mot est sans erreur, on augmente les séries sans erreur
                stats.editStreaks(words.get(cursorWord).faultWord());
                cursorWord++;
            }
            else
            {
                System.out.println("word not finish");
            }
        }
        else
        {
            System.err.println("not more word in this game");
        }
        return words.get(cursorWord).toString();
    }

    public int getCursorWord() {
        return cursorWord;
    }

    public void setCursorWord(int cursorWord)
    {
        if (this.words.size() >= cursorWord)
        {
            this.cursorWord = cursorWord;
        }
        else
        {
            System.err.println("imposible to insert a cursor higher than the length of the Arraylist of words ");
        }
    }

    public ArrayList<Word> getWords()
    {
        return words;
    }

    public void setWords(ArrayList<Word> words)
    {
        this.words = words;
    }

    /**
     *
     * @return le mot courant de la partie
     */
    public Word getCurrentWord(){
        return this.getWords().get(this.getCursorWord());
    }
}