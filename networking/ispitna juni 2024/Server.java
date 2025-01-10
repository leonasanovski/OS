package networking.junska;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server extends Thread{
    public static File txt_file;
    public ServerSocket server_socket;
    public static ArrayList<String> words;
    int port;
    public static Lock lock;

    public Server(String file_path, int port) {
        this.port = port;
        lock = new ReentrantLock();
        words = new ArrayList<>();
        txt_file = new File(file_path);

        try {
            this.server_socket = new ServerSocket(7391);//so ova go imam serverot
            System.out.println("Server initiated ...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        Socket client_socker = null;
        while (true){
            try {
                client_socker = server_socket.accept();
                System.out.println("New client has connected to the server...");
                Worker worker_for_client = new Worker(txt_file,client_socker);
                worker_for_client.start();
                //
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static int checkWordOccurance(String word){
        int dane;
        lock.lock();
        if(words.contains(word)) {
            dane = 1;
        }else{
            dane = 0;
            words.add(word);
        }
        lock.unlock();
        return dane;//ova ke bide 1 ili 0 - ima ili nema zbor vo nizata
    }

    public static void main(String[] args) {
        String serverPort = System.getenv("SERVER_PORT");
        if (serverPort == null || serverPort.isEmpty()){
            throw new RuntimeException("Please add env with port number 7389");
        }
        Server server = new Server(System.getenv("log_file_path"), Integer.parseInt(serverPort));
    }
}
