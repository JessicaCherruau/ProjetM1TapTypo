package boyot.fr.TapTypo;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

public class Chrono
{
    private long TimeChrono;
    private long timeStart;
    private String stateChrono;

    private Handler customHandler;

    /**
     * Constructeur par défaut
     */
    public Chrono()
    {
        this.customHandler = new Handler();
        this.timeStart = 0;
        this.TimeChrono = 0;
        this.stateChrono = "INIT";
    }

    /**
     * Initialise et active le chronomètre
     */
    public void StartChrono()
    {
        this.stateChrono="RUN";
        timeStart = SystemClock.uptimeMillis();
        customHandler.post(updateTimerThread);
    }

    /**
     * Suspent le chronomètre
     */
    public void StopChrono()
    {
        customHandler.removeCallbacks(updateTimerThread);
        this.stateChrono = "STOP";
    }

    /**
     * Reactive le chronomètre si le chrono est arreté
     */
    public void ReprendreChrono()
    {
        if (stateChrono == "STOP")
        {
            customHandler.post(updateTimerThread);
        }
        this.stateChrono="RUN";
    }

    /**
     * Creation du thread qui gere le chronomètre
     */
    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            TimeChrono = SystemClock.uptimeMillis() - timeStart;
            customHandler.post(this);
        }
    };


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

    public Handler getCustomHandler()
    {
        return this.customHandler;
    }

    public void setCustomHandler(Handler customHandler)
    {
        this.customHandler = customHandler;
    }
}
