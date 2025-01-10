package networking.covid;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Worker extends Thread{
    private Socket socket;
    private File file;

    public Worker(Socket socket, File file) {
        this.socket = socket;
        this.file = file;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));// se sto e prateno preku soketot
            int flag = 0;
            bw.write(String.format("HELLO %s\n",socket.getLocalAddress().getHostAddress()));
            bw.flush();
            while (true){
                String recieved_message = br.readLine();
                System.out.println("test - " + recieved_message);
                if(flag == 0){
                    String [] parts = recieved_message.split(" ");
                    if (!parts[0].equals("HELLO") || parts.length != 2){
                        throw new RuntimeException("FIRST MESSAGE MUST BE 'HELLO <local_port>' ");
                    }
                    int porta = Integer.parseInt(parts[1]);
                    bw.write("SEND DAILY DATA\n");
                    bw.flush();
                }
                if(flag == 1){
                    String [] parts = recieved_message.split(", ");
                    if(parts.length != 4){
                        throw new RuntimeException("at this stage, client must send date, no.new cases, no.hospitalized patients,no.recovered patients");
                    }
                    try {//da vidam dali prviot argument e datum
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        sdf.setLenient(false);
                        Date date = sdf.parse(parts[0]);
                    } catch (ParseException e) {
                        throw new RuntimeException("Date must be the first argument in format \"MM/dd/yyyy\" ");
                    }
                    //HERE IS THE WRITING IN CSV
                    writeInCsv(recieved_message);
                    bw.write("OK\n");
                    bw.flush();
                }
                if(flag > 1){
                    if(!recieved_message.equals("QUIT")){
                        throw new RuntimeException("After sending the daily data, must be QUIT");
                    }
                    break;
                }
                flag++;
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
    public synchronized void writeInCsv(String parts) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
            bufferedWriter.append(parts).append("\n");
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            if(bufferedWriter != null)  bufferedWriter.close();
        }
    }

}

