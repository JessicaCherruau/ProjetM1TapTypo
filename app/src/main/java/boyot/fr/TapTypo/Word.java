package boyot.fr.TapTypo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Word {

    private int cursorLetter;
    /*
     * l'arraylist est composé d'object keyValue. Ces keyValue represente la lettre du mot associé au nombre de fautes realisé
    **/
    private ArrayList<KeyValue<Character, Integer>> word;
    private boolean reverse;

    public Word(String word)
    {
        this.cursorLetter = 0;
        this.reverse = false;
        this.word = new ArrayList<KeyValue<Character,Integer>>();
        for (int i=0; i<word.length() ; i++) // on parcours les lettrse du mot passé en paramettre
        {
            this.word.add(new KeyValue<Character, Integer>(word.charAt(i), 0));
        }
    }
    public Word(String word, int i)
    {
        this.cursorLetter = i;
        this.reverse = true;
        this.word = new ArrayList<KeyValue<Character,Integer>>();
        for (int j=0; j<word.length() ; j++) // on parcours les lettrse du mot passé en paramettre
        {
            this.word.add(
                    new KeyValue<Character, Integer>
                            (
                                    new Character( word.charAt(j) ), new Integer(0)
                            )
            );
        }
    }

    public boolean checkLetter(char a)
    {
        if ( !( this.checkWordEnd() ) )
        {
            if ( this.word.get(cursorLetter).getKey() == a )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public void newFault()
    {
        //on intremente le nombre de fautes associé a la lettre affiché
        this.word.get(cursorLetter).setValue( this.word.get(cursorLetter).getValue() + 1 );
    }

    /**
     * remet le curseur à sa position initiale
     */
    public void resetCursor()
    {
        if(!this.reverse)
            cursorLetter=0;
        else
            cursorLetter = word.size() - 1;
    }

    /**
     * met le curseur à la position suivante (ou précedent selon si le word est reverse)
     */
    public void nextCursor()
    {
        if ( !( this.checkWordEnd() ) )
        {
            if(!this.reverse)
                cursorLetter++;
            else
                cursorLetter--;
        }
        else
        {
            System.err.println("no more letter in this word " + this.getWord() );
        }
    }

    public boolean checkWordEnd()
    {
        if ( word.size() == cursorLetter && !(reverse) || cursorLetter == -1 && reverse  )
        {
            return true;
        }
        return false;
    }

    public int getCursorLetter()
    {
        return cursorLetter;
    }

    public void setCursorLetter(int cursorLetter)
    {
        if (this.word.size() >= cursorLetter)
        {
            this.cursorLetter = cursorLetter;
        }
        else
        {
            System.err.println("imposible to insert a cursor higher than the length of the word ");
        }
    }

    public boolean getReverse()
    {
        return reverse;
    }
    public void setReverse(boolean reverse)
    {
        this.reverse = reverse;
    }
    public ArrayList<KeyValue<Character, Integer>> getWord()
    {
        return word;
    }
    public void setWord(ArrayList<KeyValue<Character, Integer>> word)
    {
        this.word = word;
    }

    public String stringWord()
    {
        String returnWord = "";
        for (int i=0; i<word.size(); i++)
        {
            returnWord += this.word.get(i).getKey();
        }
        return returnWord;
    }

    public int faultWord()
    {
        int returnFault =0;
        for (int i=0; i<word.size(); i++)
        {
            returnFault += this.word.get(i).getValue();
        }
        return returnFault;
    }

    public Character getLetter()
    {
        return word.get(cursorLetter).getKey();
    }

    public Integer getLetterFault()
    {
        return word.get(cursorLetter).getValue();
    }
	/*
	public void setLetter(int i)
	{
		word.get(cursorLetter).setValue(value)();
	}
	
	public void setLetterFault(int i)
	{
		word.get(cursorLetter).getValue();
	}*/


}