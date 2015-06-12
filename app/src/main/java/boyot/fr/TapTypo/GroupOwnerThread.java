package boyot.fr.TapTypo;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class GroupOwnerThread extends Thread {
    private static final String TAG = "taptypo.GroupOwnerT.";

    private Manager manager;
    public boolean keepgoing = true;
    private ServerSocket serverSocket;
    int nbClients = 0;

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

    int getClientNumber() {
        return nbClients;
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
                        //TODO : on peut faire un traitement specifique a un message recu ici (genre getClients)
                        if (message != null) {
                            Log.d(TAG, "un message a ete recupere dans la thread de connexion " + i + " : " + message);
                            for (int j = 0; j < connexionThreadList.size(); j++) {
                                // if(connexionThreadList.get(j).keepgoing) {
                                connexionThreadList.get(j).write(message);
                                Log.v(TAG, "message transmis a la thread de connexion " + j + " : " + message);
                                //TODO
                                // } else {
                                //   connexionThreadList.remove(j);
                                //}
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

        while (connexionThreadList.size()<5 && keepgoing) {
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

    public int getLocalPort() {
        if (serverSocket != null) {
            return serverSocket.getLocalPort();
        } else return 0;
    }
}
