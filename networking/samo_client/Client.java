package networking.samo_client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread{
    private String serverName;
    private int port;
    private File file;
    public Client(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;
        file = new File("data.txt");
    }

    @Override
    public void run() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        BufferedWriter put_in_file = null;

        Socket socket = null;
        try {
            socket = new Socket(InetAddress.getByName(serverName),port);
            System.out.println("Client initiated ...");
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner sc = new Scanner(System.in);
            String index = sc.nextLine();
            bw.write("hello:"+index+"\n"); //ova treba da e hello:indeks
            bw.flush();
            String line;
            line = br.readLine();
            String [] parts = line.split(":");
            if(parts.length != 2 || !parts[0].equals(index) || !parts[1].equals("hello")){
                throw new RuntimeException("The client has not sent the propper format for login (hello:index) ");
            }
//            line = sc.nextLine();//baranje vo forma index:recieve

            bw.write(index+":recieve\n");
            bw.flush();

            line = br.readLine();
            parts = line.split(":");
            if(parts.length != 3 || !parts[0].equals(index) || !parts[1].equals("send") || !parts[2].contains(".txt")){
                throw new RuntimeException("The client has not sent the propper format for login (hello:index) ");
            }
            put_in_file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
            while ((line = br.readLine()) != null && !line.equals(index + ":over")) {
                put_in_file.write(line + "\n");
                put_in_file.flush();
            }

            long file_size = file.length();
            bw.write(String.format("%s:size:%s\n",index,file_size));
            bw.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if (put_in_file != null)    put_in_file.close();
                if (bw != null)     bw.close();
                if (br != null)     br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("194.149.135.49",9357);
        client.start();
    }
}
