package networking.junska;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

public class Worker extends Thread {
    private File file;
    private Socket socket;

    public Worker(File file, Socket socket) {
        this.file = file;
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = "";
            line = br.readLine();
            if(!line.equals("Login")){
                throw new RuntimeException("must make handshake with \"Login\"");
            }
            //posle ova znaci deka imame uspesno handshake
            bw.write("Logged In " + socket.getLocalAddress().getHostAddress() + "\n");
            bw.flush();
            //sega posle ova znaci deka serverot  i klientot imaat handshake
            while ((line = br.readLine()) != null){
                if (line.equals("STOP")){
                    bw.write("LOGGED OUT\n");
                    bw.flush();
                    socket.close();
                    break;
                }
                if(line.trim().split(" ").length != 1){
                    throw new RuntimeException("The message that is sent must be one word, not a phrase or sentence");
                }
                int check = Server.checkWordOccurance(line);
                if(check == 1){
                    bw.write("IMA\n");
                    bw.flush();
                }else {
                    writeInTxtFile(line);
                    bw.write("NEMA\n");
                    bw.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void writeInTxtFile(String word) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
            bufferedWriter.append(String.format("%s - %s - %s\n",word, LocalDateTime.now(),socket.getInetAddress().getHostAddress()));
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            if (bufferedWriter != null) bufferedWriter.close();
        }
    }
}
