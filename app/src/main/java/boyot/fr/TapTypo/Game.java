package boyot.fr.TapTypo;

import java.util.ArrayList;

public class Game
{
    private int cursorWord;
    private ArrayList<Word> words;


    public Game()
    {
        this.cursorWord = 0;
        int i;

        WordGenerator wg = WordGenerator.getInstance();
        ArrayList<String> partie = wg.generateWordList(21);
        this.words = new ArrayList<Word>();

        for (i=0; i<partie.size() ;i++) // initialisation de l'arraylist
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

    public void newWord()
    {
        if ( ( cursorWord < words.size() ) )
        {
            if ( (words.get(cursorWord).checkWordEnd() ) ) // verification que le joueur a bien trouver le mot prececent
            {
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


}

