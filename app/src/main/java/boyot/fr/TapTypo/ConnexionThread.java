package boyot.fr.TapTypo;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


class ConnexionThread extends Thread implements Manager.SocketHandlerThread, Serializable {
    private static final String TAG = "taptypo.ConnexionT.";

    //buffers de lecture/�criture dans la socket
    BufferedReader in;
    PrintWriter out;

    //FIFOs des messages lus et � �crire
    public ConcurrentLinkedQueue<String> messageToSend = new ConcurrentLinkedQueue<String>();
    public ConcurrentLinkedQueue<String> messageReceived = new ConcurrentLinkedQueue<String>();

    //La socket est pass�e au constructeur directement apr�s un accept() ou il faut fournir
    // l'addresse IP et le num�ro de port
    Socket newSocket = null;
    private InetAddress groupOwnerAddress = null;
    private int port = 0;

    //boolean � mettre � false pour terminer les threads
    public boolean keepgoing = true;

    public ConnexionThread(Socket newSocket) {
        Log.d(TAG, "nouvelle thread de connexion cr��e par la GroupOwnerThread");
        this.newSocket = newSocket;
    }

    public ConnexionThread(InetAddress groupOwnerAddress, int port) {
        Log.d(TAG, "nouvelle thread client cr��e par le client");
        this.groupOwnerAddress = groupOwnerAddress;
        this.port = port;
    }

    @Override
    public void run() {
        Log.d(TAG, "ConnexionThread lancee, keepgoin : " + keepgoing);

        //initialisation des buffers et de la socket si ce n'a pas �t� fait par le constructeur
        try {
            if(newSocket==null){
                if(this.groupOwnerAddress!=null) {
                    newSocket = new Socket(this.groupOwnerAddress, this.port);
                } else {
                    newSocket = new Socket("localhost", this.port);
                }
            }
            Log.d(TAG, "socket : " + newSocket.toString());
            out = new PrintWriter(newSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
        }catch (UnknownHostException e) {
            e.printStackTrace();
            keepgoing = false;
            //on ferme la thread en cas de pb
        } catch (IOException e) {
            e.printStackTrace();
            keepgoing = false;
            //on ferme la thread en cas de pb
        }

        //thread de lecture de la socket
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "thread de lecture lancee, keepgoing : " + keepgoing);
                while (keepgoing) {
                    Log.v(TAG, "thread de lecture running");
                    try {
                        String message = in.readLine();
                        //methode synchrone, pas besoin de sleep
                        if(message!=null) {
                            messageReceived.add(message);
                            Log.i(TAG, "un message a été recupere de la socket et ajoutee a la FIFO messageReceived : " + message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        keepgoing=false;
                        //on ferme la thread en cas de pb
                    }
                }
                Log.d(TAG, "fin de la thread de lecture de ConnexionThread");
            }
        }.start();

        // thread �criture
        new Thread() {
            @Override
            public void run() {
                while (keepgoing) {
                    Log.v(TAG, "thread d'ecriture running, queue size : " + messageToSend.size());
                    if(messageToSend.size()>0) {
                        String message = messageToSend.poll();
                        Log.v(TAG, "thread d'ecriture running, message : " + message + " queue size : " + messageToSend.size());
                        if (message != null) {
                            Log.i(TAG, "un message a été recupere dans la FIFO messageToSend : " + message);
                            out.println(message);
                            out.flush();
                            Log.e(TAG, "sending " + message);
                        }
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        // ici keepgoin = false
        // on peut enfin fermer la socket de connexion et sortir de run()
        /*try {
            newSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Log.d(TAG, "fin de ConnexionThread");
    }

    @Override
    public void write(String message) {
        if(message!=null) {
            messageToSend.add(message);
            Log.d(TAG, "write(" + message + "), message added to messageToSend , queue size : " + messageToSend.size());
        }
    }

    @Override
    public String read() {
        Log.v(TAG, "read(), messageReceived.size() : " + messageReceived.size());
        return messageReceived.poll();
    }
}
