package networking.covid;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    public static File csv_file;//tuka ke zapisuvame
    public ServerSocket serverSocket;
    public Server(String csv_file_path) throws IOException {
        csv_file = new File(csv_file_path);
        try {
            this.serverSocket = new ServerSocket(8888);
            System.out.println("-->Server started on port 8888<--");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        Socket socket = null;
        //inicijaliziram promenliva socket na koja podocna ke otvaram novi klienti
        while(true){
            try {
                socket = serverSocket.accept();//so ova prifakjam na ovoj socket nekoj klient na stranata na serverot
                System.out.println("New client has been connected");
                Worker worker = new Worker(socket,csv_file);
                worker.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server("C:\\Users\\User\\Desktop\\Java\\operativni\\src\\networking\\goch_git\\data.csv");
        server.start();
    }
}
