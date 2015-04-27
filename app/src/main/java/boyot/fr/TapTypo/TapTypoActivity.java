package boyot.fr.TapTypo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;


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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class TapTypoActivity extends SimpleBaseGameActivity implements
        OnClickListener {

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
    private BitmapTextureAtlas mFontTexture;

    private ButtonSprite[][] gridSprite = new ButtonSprite[GRID_WIDTH][GRID_HEIGHT];
    private String alphabet = "AZERTYUIOPQSDFGHJKLMWXCVBN";
    private String mot;
    private Hashtable<ButtonSprite, Character> tableauLettrePosition = new Hashtable<ButtonSprite, Character>();
    public static InputStream reader;
    private Game game;
    long m_time_debut;
    long m_time_total;


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
                    if (game.getWords().get(game.getCursorWord()).checkLetter(lettre))
                        colorCorrectLetter(game.getWords().get(game.getCursorWord()));
                    else
                        colorWrongLetter(game.getWords().get(game.getCursorWord()));
                }

                // si on est entre le mot 10 et le mot 20 : mots verticaux
                else if(game.getCursorWord()>9 && game.getCursorWord()<20)
                {

                    if (game.getWords().get(game.getCursorWord()).checkLetter(lettre))
                        colorCorrectLetter(game.getWords().get(game.getCursorWord()), true);
                    else
                        colorWrongLetter(game.getWords().get(game.getCursorWord()), true);
                }
                //dernier mot
                else {

                    if (game.getWords().get(game.getCursorWord()).checkLetter(lettre)) {
                        tableauLettreAffiche.get(game.getWords().get(game.getCursorWord()).getCursorLetter()).setColor(0, 1, 0, 1);
                        game.getWords().get(game.getCursorWord()).nextCursor();
                    } else {
                        tableauLettreAffiche.get(game.getWords().get(game.getCursorWord()).getCursorLetter()).setColor(1, 0, 0, 1);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < mot.length(); i++) {
                            tableauLettreAffiche.get(i).setColor(1, 1, 1,1);
                        }
                        game.getWords().get(game.getCursorWord()).resetCursor();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < mot.length(); i++) {
                            tableauLettreAffiche.get(i).setColor(1,1,1,0);
                        }

                    }

                }
                if (!game.checkGameEnd())
                {
                    //Check fin du mot
                    if (game.getWords().get(game.getCursorWord()).checkWordEnd()) {
                        game.newWord();
                        mot = game.getWords().get(game.getCursorWord()).getWord();
                        resetColorText();
                        if (game.getCursorWord() <= 9) {

                            for (int i = 0; i < mot.length(); i++) {
                                tableauLettreAffiche.get(i).setText(mot.charAt(i) + "");
                            }
                            tableauLettreAffiche.get(game.getWords().get(game.getCursorWord()).getCursorLetter()).setColor(0, 0, 1);

                        }
                        else if (game.getCursorWord()> 9 && game.getCursorWord() < 20)
                        {
                            for (int i = 0; i < mot.length(); i++) {
                                int j = i + 10;
                                tableauLettreAffiche.get(j).setText(mot.charAt(i) + "");
                            }
                            tableauLettreAffiche.get(game.getWords().get(game.getCursorWord()).getCursorLetter()+10).setColor(0, 0, 1);

                        }
                        else {
                            for (int i = 0; i < mot.length(); i++) {
                                tableauLettreAffiche.get(i).setText(mot.charAt(i) + "");
                            }
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            for (int i = 0; i < mot.length(); i++) {
                                tableauLettreAffiche.get(i).setColor(1,1,1,0);
                            }

                        }

                    }
                }
                else
                {
                    m_time_total =  new Date().getTime() - m_time_debut;
                    m_time_total =  (m_time_total / 1000);
                    //AlertDialog.Builder ABDbuiler = new AlertDialog.Builder(TapTypoActivity.this);
                    //ABDbuiler.setMessage("Vous avez mis "+ m_time_total+" secondes.").show();
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("result", m_time_total+"");
                    finish();
                    startActivity(intent);
                }


            }
        });

    }

    /**
     * Cololre la bonne lettre en vert et met le curseur sur la lettre suivante
     * @param word
     */
    protected void colorCorrectLetter(Word word){
        colorCorrectLetter(word, false);
    }

    /**
     * Cololre la bonne lettre en vert et met le curseur sur la lettre suivante
     * @param word
     * @param vertical indique si le mot est vertical
     */
    protected void colorCorrectLetter(Word word, boolean vertical){
        int cursor = word.getCursorLetter();
        if(vertical)
            cursor += 10;

        tableauLettreAffiche.get(cursor).setColor(0, 1, 0);
        word.nextCursor();
        if(!word.checkWordEnd())
            colorCurrentLetter(word, vertical);
    }

    /**
     * Colore le mot entier en rouge puis remet le mot à blanc et le curseur recommence à zéro
     * @param word le mot courant
     */
    protected void colorWrongLetter(Word word){
        colorWrongLetter(word, false);
    }
    /**
     * Colore le mot entier en rouge puis remet le mot à blanc et le curseur recommence à zéro
     * @param word le mot courant
     */
    protected void colorWrongLetter(Word word, boolean vertical){
        int cursor = word.getCursorLetter();
        if(vertical)
            cursor += 10;
        tableauLettreAffiche.get(cursor).setColor(1, 0, 0);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int bounds = (vertical) ? 10 : 0;
        for (int i = 0; i < mot.length(); i++) {
            tableauLettreAffiche.get(i+bounds).setColor(1, 1, 1);
        }
        word.resetCursor();
        colorCurrentLetter(word, vertical);
    }

    /**
     * Colore en bleu la lettre courante
     * @param word
     */
    protected void colorCurrentLetter(Word word){
        colorCurrentLetter(word, false);
    }
    /**
     * Colore en bleu la lettre courante
     * @param word
     * @param vertical indique si le mot est vertical
     */
    protected void colorCurrentLetter(Word word, boolean vertical){
        int cursor = word.getCursorLetter();
        if(vertical)
            cursor += 10;
        tableauLettreAffiche.get(cursor).setColor(0, 0, 1);
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
        m_time_debut = new Date().getTime();







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

        final Text H0 = new Text(60, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H1 = new Text(90, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H2 = new Text(120, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H3 = new Text(150, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H4 = new Text(180, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H5 = new Text(210, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H6 = new Text(240, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H7 = new Text(270, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H8 = new Text(300, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text H9 = new Text(330, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V0 = new Text(180, 50, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V1 = new Text(180, 80, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V2 = new Text(180, 110, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V3 = new Text(180, 140, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V4 = new Text(180, 170, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V5 = new Text(180, 200, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V6 = new Text(180, 230, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V7 = new Text(180, 260, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V8 = new Text(180, 290, this.mFont, "",1, this.getVertexBufferObjectManager());
        final Text V9 = new Text(180, 320, this.mFont, "",1, this.getVertexBufferObjectManager());
        tableauLettreAffiche.add(0,H0);
        tableauLettreAffiche.add(1,H1);
        tableauLettreAffiche.add(2,H2);
        tableauLettreAffiche.add(3,H3);
        tableauLettreAffiche.add(4,H4);
        tableauLettreAffiche.add(5,H5);
        tableauLettreAffiche.add(6,H6);
        tableauLettreAffiche.add(7,H7);
        tableauLettreAffiche.add(8,H8);
        tableauLettreAffiche.add(9,H9);
        tableauLettreAffiche.add(10,V0);
        tableauLettreAffiche.add(11,V1);
        tableauLettreAffiche.add(12,V2);
        tableauLettreAffiche.add(13,V3);
        tableauLettreAffiche.add(14,V4);
        tableauLettreAffiche.add(15,V5);
        tableauLettreAffiche.add(16,V6);
        tableauLettreAffiche.add(17,V7);
        tableauLettreAffiche.add(18,V8);
        tableauLettreAffiche.add(19,V9);


        scene.attachChild(H1);
        scene.attachChild(H0);
        scene.attachChild(H2);
        scene.attachChild(H3);
        scene.attachChild(H4);
        scene.attachChild(H5);
        scene.attachChild(H6);
        scene.attachChild(H7);
        scene.attachChild(H8);
        scene.attachChild(H9);
        scene.attachChild(V1);
        scene.attachChild(V2);
        scene.attachChild(V3);
        scene.attachChild(V5);
        scene.attachChild(V6);
        scene.attachChild(V7);
        scene.attachChild(V8);
        scene.attachChild(V0);
        scene.attachChild(V9);
        scene.attachChild(V4);


        mot = game.getWords().get(game.getCursorWord()).getWord();

        for(int i=0;i< mot.length(); i++ )
        {
            tableauLettreAffiche.get(i).setText(mot.charAt(i)+"");

        }
        tableauLettreAffiche.get(game.getWords().get(game.getCursorWord()).getCursorLetter()).setColor(0, 0, 1);


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
}