package boyot.fr.TapTypo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;


import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.util.debug.Debug;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

public class TapTypoActivity extends SimpleBaseGameActivity implements
        OnClickListener {

    private static final int NB_MAX_LETTERS = 10;

    private int CAMERA_WIDTH = 400;
    private int CAMERA_HEIGHT=615;

    final private int GRID_WIDTH = 10;
    final private int GRID_HEIGHT = 3;

    final private int STROKE_WIDTH = 4;

    private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
    private ArrayList<ITextureRegion> lettreNormal = new ArrayList<ITextureRegion>();
    private ArrayList<ITextureRegion> lettrePressee = new ArrayList<ITextureRegion>();
    private ArrayList<Text> tableauLettreAffiche = new ArrayList<Text>();
    private Font mFont;

    private ButtonSprite[][] gridSprite = new ButtonSprite[GRID_WIDTH][GRID_HEIGHT];
    private String alphabet = "AZERTYUIOPQSDFGHJKLMWXCVBN";
    private String mot;
    private Hashtable<ButtonSprite, Character> tableauLettrePosition = new Hashtable<ButtonSprite, Character>();
    public static InputStream reader;
    private Game game;

    @Override
    public void onClick(final ButtonSprite pButtonSprite,
                        float pTouchAreaLocalX, float pTouchAreaLocalY) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Recupération de la lettre tapée
                char lettre = tableauLettrePosition.get(pButtonSprite);

                // Si on est entre le mot 0 et le mot 9 : mots horizontaux
                if(game.getCursorWord()<10)
                {
                    if (game.getCurrentWord().checkLetter(lettre))
                        colorCorrectLetter(game.getCurrentWord());
                    else
                        colorWrongLetter(game.getCurrentWord());
                }

                // si on est entre le mot 10 et le mot 20 : mots verticaux
                else if(game.getCursorWord()>9 && game.getCursorWord()<20)
                {

                    if (game.getCurrentWord().checkLetter(lettre))
                        colorCorrectLetter(game.getCurrentWord(), true);
                    else
                        colorWrongLetter(game.getCurrentWord(), true);
                }
                //dernier mot
                else {

                    if (game.getCurrentWord().checkLetter(lettre)) {
                        colorCorrectLetter(game.getCurrentWord());
                    } else {
                        colorWrongLetter(game.getCurrentWord());
                        //on remet le mot en transparent au bout de 0.1s
                        pause(100);
                        for (int i = 0; i < mot.length(); i++) {
                            tableauLettreAffiche.get(i).setColor(1,1,1,0);
                        }
                    }

                }
                if (!game.checkGameEnd())
                {
                    //Check fin du mot
                    if (game.getCurrentWord().checkWordEnd()) {
                        game.updateScore();
                        Log.d("score", game.getStatistics().getScore()+"");
                        Log.d("current ", game.getStatistics().getCurrentStreak()+"");
                        Log.d("bonus", ""+game.getStatistics().getBonus());
                        mot = game.newWord();
                        resetColorText();
                        if (game.getCursorWord() <= 9) {
                            showCurrentWord(game.getCurrentWord());
                            colorCurrentLetter(game.getCurrentWord(), false);
                        }
                        else if (game.getCursorWord()> 9 && game.getCursorWord() < 20)
                        {
                            showCurrentWord(game.getCurrentWord(), true);
                            colorCurrentLetter(game.getCurrentWord(), true);
                        }
                        else {
                            showCurrentWord(game.getCurrentWord());
                            //faire disparaitre le mot
                            pause(100);
                            for (int i = 0; i < mot.length(); i++) {
                                tableauLettreAffiche.get(i).setColor(1,1,1,0);
                            }

                        }

                    }
                }
                else
                {
                    game.endGame();
                    //AlertDialog.Builder ABDbuiler = new AlertDialog.Builder(TapTypoActivity.this);
                    //ABDbuiler.setMessage("Vous avez mis "+ m_time_total+" secondes.").show();
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("result", game.getStatistics());
                    finish();
                    startActivity(intent);
                }

            }
        });

    }

    /**
     * Colore la bonne lettre en vert et met le curseur sur la lettre suivante
     * @param word le mot courant
     */
    protected void colorCorrectLetter(Word word){
        colorCorrectLetter(word, false);
    }

    /**
     * Cololre la bonne lettre en vert et met le curseur sur la lettre suivante
     * @param word le mot courant
     * @param vertical indique si le mot est vertical
     */
    protected void colorCorrectLetter(Word word, boolean vertical){
        int cursor = word.getCursorLetter();
        if(vertical)
            cursor += NB_MAX_LETTERS;

        tableauLettreAffiche.get(cursor).setColor(0, 1, 0, 1);
        word.nextCursor();
        if(!word.checkWordEnd())
            colorCurrentLetter(word, vertical);
    }

    /**
     * Colore le mot entier en rouge puis remet le mot à blanc et le curseur recommence à zéro
     * enregistre les erreurs dans le modèle
     * @param word le mot courant
     */
    protected void colorWrongLetter(Word word){
        colorWrongLetter(word, false);
    }
    /**
     * Colore le mot entier en rouge puis remet le mot à blanc et le curseur recommence à zéro
     * enregistre les erreurs dans le modèle
     * @param word le mot courant
     */
    protected void colorWrongLetter(Word word, boolean vertical){
        //colore en rouge la lettre sur laquelle on a fauté
        int cursor = word.getCursorLetter();
        if(vertical)
            cursor += NB_MAX_LETTERS;
        tableauLettreAffiche.get(cursor).setColor(1, 0, 0, 1);
        pause(200);
        //on indique qu'il y a un faute
        game.errorOnWord();

        //remet le curseur au début et colore la première lettre
        int bounds = (vertical) ? NB_MAX_LETTERS : 0;
        for (int i = 0; i < mot.length(); i++) {
            tableauLettreAffiche.get(i+bounds).setColor(1, 1, 1, 1);
        }
        word.resetCursor();
        colorCurrentLetter(word, vertical);
    }

    /**
     * Colore en bleu la lettre courante
     * @param word le mot courant
     * @param vertical indique si le mot est vertical
     */
    protected void colorCurrentLetter(Word word, boolean vertical){
        int cursor = word.getCursorLetter();
        if(vertical)
            cursor += NB_MAX_LETTERS;
        tableauLettreAffiche.get(cursor).setColor(0, 0, 1);
    }

    /**
     * Affiche le mot sur la fenetre et colore la première lettre
     * @param word le mot à afficher
     */
    protected void showCurrentWord(Word word){
        showCurrentWord(word, false);
    }
    /**
     * Affiche le mot sur la fenetre et colore la première lettre
     * @param word le mot à afficher
     * @param vertical si le mot est en vertical
     */
    protected void showCurrentWord(Word word, boolean vertical){
        int bounds = (vertical) ? NB_MAX_LETTERS : 0;
        for (int i = 0; i < mot.length(); i++) {
            tableauLettreAffiche.get(i + bounds).setText(mot.charAt(i) + "");
        }
    }

    @Override
    protected void onCreateResources() {

        reader = getResources().openRawResource(R.raw.dico);
        game = new Game();

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 512);
        for(int i=0; i < alphabet.length(); i++)
        {
            this.lettreNormal.add(i,
                    BitmapTextureAtlasTextureRegionFactory.createFromAsset
                            (this.mBitmapTextureAtlas, this, "lettre"+alphabet.charAt(i)+".png"));
            this.lettrePressee.add(i,
                    BitmapTextureAtlasTextureRegionFactory.createFromAsset
                            (this.mBitmapTextureAtlas, this, "lettrePressee" + alphabet.charAt(i) + ".png"));
        }

        try {
            this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
            this.mBitmapTextureAtlas.load();
        } catch (ITextureAtlasBuilder.TextureAtlasBuilderException e) {
            Debug.e(e);
        }


        mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.WHITE);
        mFont.load();

        game.startChrono();
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final Scene scene = new Scene();
        final VertexBufferObjectManager VBOManager = this.getVertexBufferObjectManager();

        float lineX[] = new float[GRID_WIDTH];
        float lineY[] = new float[GRID_HEIGHT];

        float touchX[] = new float[GRID_WIDTH];
        float touchY[] = new float[GRID_HEIGHT];

        float midTouchX = CAMERA_WIDTH / GRID_WIDTH / 2;
        float midTouchY = 200 / GRID_HEIGHT / 2;

        float halfTouchX = 40 / 2;
        float halfTouchY = 66 / 2;

        float paddingX = midTouchX - halfTouchX;
        float paddingY = midTouchY - halfTouchY;

        for (int i = 0; i < GRID_WIDTH; i++) {
            lineX[i] = CAMERA_WIDTH / GRID_WIDTH * i;
            touchX[i] = lineX[i] + 2;
        }

        lineY[0] = CAMERA_HEIGHT-66;
        touchY[0] = lineY[0] + 2;
        lineY[1] = CAMERA_HEIGHT-132;
        touchY[1] = lineY[1] + 2;
        lineY[2] = CAMERA_HEIGHT-198;
        touchY[2] = lineY[2] + 2;

        //Draw the Grid lines

        for (int i = 1; i < GRID_WIDTH; i++) {
            final Line line = new Line(lineX[i], 400, lineX[i], 600, STROKE_WIDTH, VBOManager);
            line.setColor(0.85f, 0.85f, 0.85f);
            scene.attachChild(line);

        }
        for (int i = 0; i < GRID_HEIGHT; i++) {
            final Line line = new Line(0, lineY[i], CAMERA_WIDTH, lineY[i], STROKE_WIDTH, VBOManager);
            line.setColor(0.85f, 0.85f, 0.85f);
            scene.attachChild(line);

        }

        scene.setBackground(new Background(0.85f, 0.85f, 0.85f));

        //Lay Out the buttonSprites
        int compteurLettre = 25;
        for (int i = 0; i < GRID_HEIGHT ; i++)
            for (int j = GRID_WIDTH-1; j >=0  ; j--) {
                if((i == 0 && j==0)||(i == 0 && j==1)||(i == 0 && j==8)||(i == 0 && j==9))
                {
                    continue;
                }
                else

                {
                    final  ButtonSprite button = new ButtonSprite(touchX[j], touchY[i], this.lettreNormal.get(compteurLettre), this.lettrePressee.get(compteurLettre), this.lettrePressee.get(compteurLettre), VBOManager, this);
                    scene.registerTouchArea(button);
                    scene.attachChild(button);
                    gridSprite[j][i] = button;
                    tableauLettrePosition.put(button,this.alphabet.charAt(compteurLettre));
                    compteurLettre--;

                }


            }

        //crée les zones de textes horizontales
        for(int i = 0 ; i < NB_MAX_LETTERS ; i++)
            createLetterTextZone((60 + 30*i), 200, scene);
        //crée les zones de textes verticales
        for(int i = 0 ; i < NB_MAX_LETTERS ; i++)
            createLetterTextZone(180, (50+30*i), scene);

        mot = game.getCurrentWord().toString();
        showCurrentWord(game.getCurrentWord());

        scene.setTouchAreaBindingOnActionDownEnabled(true);

        return scene;

    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED
                , new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    private void resetColorText()
    {
        for(int i=0; i<tableauLettreAffiche.size();i++){
            tableauLettreAffiche.get(i).setColor(1,1,1);
            tableauLettreAffiche.get(i).setText("");
        }
    }

    /**
     * met le thread en pause
     * @param ms la durée en ms
     */
    protected void pause(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée un élément Text à des positions données et l'ajouter dans le tableau des lettres et à la scène
     * @param x coordonnée x
     * @param y coordonnée y
     */
    protected void createLetterTextZone(int x, int y, Scene scene){
        final Text txt = new Text(x, y, this.mFont, "",1, this.getVertexBufferObjectManager());
        tableauLettreAffiche.add(txt);
        scene.attachChild(txt);
    }
}