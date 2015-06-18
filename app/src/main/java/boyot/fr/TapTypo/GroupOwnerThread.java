package boyot.fr.TapTypo;

import android.util.Log;

import org.andengine.util.ThreadUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

class GroupOwnerThread extends Thread {
    private static final String TAG = "taptypo.GroupOwnerT.";

    private Manager manager;
    public boolean keepgoing = true;
    private ServerSocket serverSocket;
    public static final String LAUNCH_GAME = "launch_game";
    public static final String DICO ="dico";
    public static final String SCORE = "sending_score";
    public static final String RANK = "ranking";
    private static final String DEFAULT_NAME = "Joueur";

    int groupOwnerIndex = 0;

    ArrayList<ConnexionThread> connexionThreadList = new ArrayList<>(4);

    GroupOwnerThread(Manager manager) {
        this.manager = manager;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
            keepgoing = false;
        }
    }

    /**
     *
     * @return nombre de connexions établies
     */
    int getClientCount() {
        return connexionThreadList.size();
    }

    @Override
    public void run() {
        //thread de lecture des differentes thread de connection : envoie les messages recus a toutes les connections disponibles
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "thread de lecture lancee, keepgoing : " + keepgoing);
                while (keepgoing) {

                    Log.v(TAG, "thread de lecture running");
                    for(int i=0; i<connexionThreadList.size();i++) {
                        String message = connexionThreadList.get(i).read();
                        if (message != null) {
                            Log.d(TAG, "un message a ete recupere dans la thread de connexion " + i + " : " + message);
                            //on envoie que à l'host
                            if(message.contains(LAUNCH_GAME)){      //envoi du top départ : changement d'activité
                                groupOwnerIndex = i;    //the player who send a msg with the "LAUNCH_GAME" tag is the host
                                sendToGroupOwner(getClientCount() + "");    //send the number of players
                                try {
                                    this.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                broadcastLaunch();
                            }
                            else if(message.contains(DICO)){        //propagation du dictionnaire aux clients
                                groupOwnerIndex = i;    //the player who send a msg with the "DICO" tag is the host
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                broadcastExceptHost(message.substring(DICO.length() + 1));
                            }
                            else if(message.contains(SCORE)){       //envoi du score au "serveur"
                                sendToGroupOwner(message.substring(SCORE.length()+1));
                            }
                            else if(message.contains(RANK)){        // diffusion du classement aux clients
                                broadcast(message.substring(RANK.length()+1));    //on retire le taggage
                            }
                            else{
                                //broadcast(message);
                            }
                        }
                        else {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Log.d(TAG, "fin de la thread de lecture/ecriture de GroupOwnerThread");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        while (connexionThreadList.size()<20 && keepgoing) {
            try {
                connexionThreadList.add(new ConnexionThread(serverSocket.accept()));
                connexionThreadList.get(connexionThreadList.size()-1).start();
                Log.d(TAG, "nouvelle connection acceptee, connexionThreadList.size() : " + connexionThreadList.size());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "createGroupOwnerThread serverSocket.accept(); failed");
                keepgoing = false;
            }
        }
        Log.d(TAG, "fin de GroupOwnerThread");
    }

    /**
     * Send a message to all of the clients (including group owner)
     * @param message message to send
     */
    private void broadcast(String message) {
        for (int j = 0; j < connexionThreadList.size(); j++) {
            if(connexionThreadList.get(j).keepgoing) {
                connexionThreadList.get(j).write(message);
                Log.d(TAG, "message transmis a la thread de connexion " + j + " : " + message);
            } else {
                connexionThreadList.remove(j);
            }
        }
    }

    /**
     * Send a message to the clients by socket except the host
     * @param message message to send
     */
    private void broadcastExceptHost(String message){
        for (int j = 0; j < connexionThreadList.size(); j++) {
            if(j == this.groupOwnerIndex)
                continue;
            if(connexionThreadList.get(j).keepgoing) {
                connexionThreadList.get(j).write(message);
                Log.v(TAG, "message transmis a la thread de connexion " + j + " : " + message);
            } else {
                connexionThreadList.remove(j);
            }
        }
    }

    /**
     * Send a message only to the group owner
     * @param message message to send
     */
    private void sendToGroupOwner(String message){
        if(groupOwnerIndex >= 0 && groupOwnerIndex < connexionThreadList.size()) {
            if (connexionThreadList.get(groupOwnerIndex).keepgoing) {
                connexionThreadList.get(groupOwnerIndex).write(message);
                Log.v(TAG, "message transmis a la thread de connexion " + groupOwnerIndex + " : " + message);
            } else {
                connexionThreadList.remove(groupOwnerIndex);
            }
        }
    }

    /**
     * Broadcast to all of the clients (except Host) the game launch by sending their name
     */
    private void broadcastLaunch(){
        for (int j = 0; j < connexionThreadList.size(); j++) {
//            if(j == this.groupOwnerIndex)
//                continue;
            if(connexionThreadList.get(j).keepgoing) {
                connexionThreadList.get(j).write(LAUNCH_GAME+":"+DEFAULT_NAME+j);
                Log.v(TAG, "lancement de partie transmis a la thread de connexion " + j + " : " + DEFAULT_NAME+j);
            } else {
                connexionThreadList.remove(j);
            }
        }
    }

    public int getLocalPort() {
        if (serverSocket != null) {
            return serverSocket.getLocalPort();
        } else return 0;
    }
}
