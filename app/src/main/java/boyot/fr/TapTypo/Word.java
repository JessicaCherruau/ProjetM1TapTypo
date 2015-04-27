package boyot.fr.TapTypo;

public class Word {

    private int cursorLetter;
    private String word;
    private boolean reverse;

    public Word(String word)
    {
        this.cursorLetter = 0;
        this.word = word;
        this.reverse = false;
    }
    public Word(String word, int i)
    {
        this.cursorLetter = i;
        this.word = word;
        this.reverse = true;
    }

    public boolean checkLetter(char a)
    {
        if ( !( this.checkWordEnd() ) )
        {
            if ( word.charAt(cursorLetter) == a )
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

    public void resetCursor()
    {
        cursorLetter=0;
    }

    public void nextCursor()
    {
        if ( !( this.checkWordEnd() ) )
        {
            cursorLetter++;
        }
        else
        {
            System.err.println("no more letter in this word " + this.getWord() );
        }
    }

    public boolean checkWordEnd()
    {
        if ( word.length() == cursorLetter && reverse == false || cursorLetter == -1 && reverse  )
        {
            return true;
        }
        return false;
    }

    public int getCursorLetter() {
        return cursorLetter;
    }

    public void setCursorLetter(int cursorLetter) {
        if (this.word.length() >= cursorLetter)
        {
            this.cursorLetter = cursorLetter;
        }
        else
        {
            System.err.println("imposible to insert a cursor higher than the length of the word ");
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

}