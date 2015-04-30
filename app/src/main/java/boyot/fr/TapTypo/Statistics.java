package boyot.fr.TapTypo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Cette classe contient les statistiques d'un joueur au cours de sa partie
 * Elle est Parcelable car on l'utilise pour passer les détails du score à la fenetre de résultat
 * Created by Jessie on 29/04/2015.
 */
public class Statistics implements Parcelable{

    private int nbErrors;                   //nombre d'erreurs total
    private int longestStreak;              //la plus longue série de mots sans erreur
    private int currentStreak;              //série de mots sans erreur en cours
    private double timelapse;               // durée de la partie
    private long score;                     // score calculé à partir des autres paramètres

    public Statistics(){
        this.nbErrors = 0;
        this.longestStreak = 0;
        this.currentStreak = 0;
        this.timelapse = 0.0;
    }

    /**
     * Use when reconstructing User object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    public Statistics(Parcel in) {
        this.nbErrors = in.readInt();
        this.longestStreak = in.readInt();
        this.currentStreak = in.readInt();
        this.timelapse = in.readDouble();
        this.score = in.readLong();
    }

    /**
     *
     * @return le nombre d'erreurs total
     */
    public int getNbErrors(){ return nbErrors; }

    /**
     *
     * @return la série de mots sans erreur la plus longue
     */
    public int getLongestStreak(){ return longestStreak; }

    /**
     *
     * @return la durée en secondes
     */
    public double getTimelapse(){ return timelapse; }

    /**
     *
     * @return le score
     */
    public long getScore(){ return score; }

    /**
     * Setter pour la durée
     * @param ms durée en ms
     */
    public void setTimelapse(long ms){ timelapse = ((double)ms) / 1000; }

    /**
     * Enregistre une erreur dans le compte
     */
    public void incrementError(){
        nbErrors++;
        currentStreak = 0;
    }

    /**
     * Augmente le nombre de séries (ou remet à zéro) en fonction du nombre d'erreur dans le mot
     * @param errors nombre d'erreur dans le mot
     */
    public void editStreaks(int errors){
        if(errors == 0){
            currentStreak++;
            longestStreak = (currentStreak > longestStreak) ? longestStreak + 1 : longestStreak;
        }
        else{
            currentStreak = 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Actual object serialization happens here, Write object content
     * to parcel one by one, reading should be done according to this write order
     * @param dest parcel
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(nbErrors);
        dest.writeInt(longestStreak);
        dest.writeInt(currentStreak);
        dest.writeDouble(timelapse);
        dest.writeLong(score);
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     *
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Statistics> CREATOR = new Parcelable.Creator<Statistics>() {

        public Statistics createFromParcel(Parcel in) {
            return new Statistics(in);
        }

        public Statistics[] newArray(int size) {
            return new Statistics[size];
        }
    };
}
