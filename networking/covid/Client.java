package networking.covid;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{
    private String serverName;
    private int serverPort;
    public Client(int serverPort,String serverName){
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getByName(serverName),serverPort);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner sc = new Scanner(System.in);
            String line;
            while ((line=br.readLine())!=null){
                System.out.println("Server: " + line);
                String send = sc.nextLine();
                System.out.println("Client: "+send);
                bw.write(send+"\n");
                bw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (br != null) br.close();
                if (bw != null) bw.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Client leon = new Client(8888,"localhost");
        leon.start();
    }
}
