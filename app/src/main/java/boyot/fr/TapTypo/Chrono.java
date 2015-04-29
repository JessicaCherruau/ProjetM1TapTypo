package boyot.fr.TapTypo;

import java.util.Date;

public class Chrono
{
    private long TimeChrono;
    private long timelapse;
    private long timeStart;
    private String stateChrono;

//    private Handler customHandler;

    /**
     * Constructeur par défaut
     */
    public Chrono()
    {
//        this.customHandler = new Handler();
        this.timeStart = 0;
        this.TimeChrono = 0;
        this.timelapse = 0;
        this.stateChrono = "INIT";
    }

    /**
     * Initialise et active le chronomètre
     */
    public void startChrono()
    {
        this.stateChrono="RUN";
        timeStart = new Date().getTime();
//        customHandler.post(updateTimerThread);
    }

    /**
     * Suspend le chronomètre
     */
    public void stopChrono()
    {
//        customHandler.removeCallbacks(updateTimerThread);
        long current = new Date().getTime();
        timelapse = current - timeStart;
        this.stateChrono = "STOP";
    }

    /**
     * retourne le délai enregistrée, ou si le chrono est en cours, le délai entre l'instant t et le début du chrono
     * @return le délai en ms
     */
    public long getTimelapse()
    {
        if(stateChrono.equals("INIT"))
            return 0;
        else if(stateChrono.equals("STOP"))
            return timelapse;
        else{
            long current = new Date().getTime();
            return current - timeStart;
        }
    }

    /**
     * Reactive le chronomètre si le chrono est arreté
     */
    public void reprendreChrono()
    {
//        if(stateChrono.equals("STOP"))
//        {
//            customHandler.post(updateTimerThread);
//        }
        this.stateChrono="RUN";
    }

    /**
     * Creation du thread qui gere le chronomètre
     */
//    private Runnable updateTimerThread = new Runnable()
//    {
//        public void run()
//        {
//            TimeChrono = SystemClock.uptimeMillis() - timeStart;
//            customHandler.post(this);
//        }
//    };


    public String getChronoString()
    {
        int secs = (int) (this.getUpdatedTime() / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        String chronoString = String.format("%02d", mins) + ":" + String.format("%02d", secs);
        return chronoString;
    }


    /**
     * retourne le temps du chronomètre en miliseconde
     */
    public long getUpdatedTime()
    {
        return this.TimeChrono;
    }

    public void setUpdatedTime(long updatedTime)
    {
        this.TimeChrono = updatedTime;
    }

    public long getTimeStart()
    {
        return this.timeStart;
    }

    public void setTimeStart(long timeStart)
    {
        this.timeStart = timeStart;
    }

    public String getStateChrono()
    {
        return this.stateChrono;
    }

    public void setStateChrono(String stateChrono)
    {
        this.stateChrono = stateChrono;
    }

//    public Handler getCustomHandler()
//    {
//        return this.customHandler;
//    }

//    public void setCustomHandler(Handler customHandler)
//    {
//        this.customHandler = customHandler;
//    }
}
