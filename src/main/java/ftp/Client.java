package src.main.java.ftp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    private Scanner console ;
    private Socket commandStream; //сокет для общения
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет
    private String serverIP;   //адрес сервера
    private boolean passiveMode;

    public Client(String ip) throws IOException
    {
        passiveMode = false;
        this.console = new Scanner(System.in);
        serverIP = ip;
        try {
            commandStream = new Socket(ip, 21);
            in = new BufferedReader(new InputStreamReader(commandStream.getInputStream())); // Буффер получения
            out = new BufferedWriter(new OutputStreamWriter(commandStream.getOutputStream())); // Буффер отправки
        }
        catch (IOException e)
        {
            System.err.println(e);
            commandStream.close();
            in.close();
            out.close();
        }
    }

    private class FTPhandler extends Thread{    //класс отправляет управляющую команду на ftp-сервер пока основной поток слушает порт

        String filename;
        String command;

        FTPhandler(String filename, String command){
            super();
            this.filename = filename;
            this.command = command;
        }
        public void run(){
            try{
                Thread.sleep(400);
                out(filename, command);
            }
            catch(InterruptedException e){
                System.out.println("Thread has been interrupted");
            }
            catch(IOException e){
                System.out.println("Bad Input");
            }
        }
    }

    public String out(String word) throws IOException // Отправка команд
    {
        out.write(word +"\n"); // отправляем сообщение на сервер
        out.flush();
        return word;
    }

    public String out(String arg, String command) throws IOException // Отправка команд
    {
        out.write(command + arg +"\n"); // отправляем сообщение на сервер
        out.flush();
        return arg;
    }

    public String get() throws IOException // Читает ОДНУ строку от сервера
    {
        String serverWord = in.readLine(); // ждём, что скажет сервер
        return serverWord;
    }

    public String authorization(String login, String password) throws IOException
    {
        StringBuilder resultCodes = new StringBuilder();
        out(login, "USER ");
        out(password, "PASS ");
        for(int i = 0; i < 3; i++){
            resultCodes.append(get().substring(0,3));
        }
        return resultCodes.toString();
    }

    public String downloadPassive(String filename)throws IOException{

        out("PASV");    //переход в пассивный режим
        int[] servResp = getSocket();   //получение от сервера адреса и порта для соединения
        out(filename, "RETR ");

        Socket dataStream = new Socket(servResp[0] + "." + servResp[1] + "." + servResp[2] + "." + servResp[3], servResp[4] * 256 + servResp[5]);
        BufferedReader dataReceived = new BufferedReader(new InputStreamReader(dataStream.getInputStream()));
        StringBuilder result = new StringBuilder();
        String str = "";
        while((str = dataReceived.readLine()) != null){
            result.append(str + "\n");
        }
        dataReceived.close();
        dataStream.close();
        get();
        get();
        return result.toString();
    }

    public String downloadActive(String filename)throws IOException{

        int[] IPandPort = generateIPandPort();
        out(IPandPort[0] + "," + IPandPort[1] + "," + IPandPort[2] + "," +IPandPort[3] + "," + IPandPort[4] + "," + IPandPort[5] ,"PORT ");
        get();

        int port = IPandPort[4] * 256 + IPandPort[5];
        ServerSocket listener = new ServerSocket(port, 1);

        new FTPhandler(filename, "RETR ").start();
        Socket dataStream = listener.accept();

        BufferedReader dataReceived = new BufferedReader(new InputStreamReader(dataStream.getInputStream()));
        StringBuilder result = new StringBuilder();
        String str = "";
        while((str = dataReceived.readLine()) != null){
            result.append(str + "\n");
        }
        dataReceived.close();
        dataStream.close();
        get();
        get();
        return result.toString();
    }

    public void uploadPassive(String filename, String dataToUpload) throws IOException{

        out("PASV");    //переход в пассивный режим
        int[] servResp = getSocket();   //получение от сервера адреса и порта для соединения
        out(filename, "STOR ");

        Socket dataStream = new Socket(servResp[0] + "." + servResp[1] + "." + servResp[2] + "." + servResp[3], servResp[4] * 256 + servResp[5]);
        BufferedWriter dataToTransmit = new BufferedWriter(new OutputStreamWriter(dataStream.getOutputStream()));
        dataToTransmit.write(dataToUpload); // отправляем сообщение на сервер
        out.flush();
        dataToTransmit.close();
        dataStream.close();
        get();
        get();
    }

    public void uploadActive(String filename, String dataToUpload) throws IOException{

        int[] IPandPort = generateIPandPort();
        out(IPandPort[0] + "," + IPandPort[1] + "," + IPandPort[2] + "," +IPandPort[3] + "," + IPandPort[4] + "," + IPandPort[5] ,"PORT ");
        get();

        int port = IPandPort[4] * 256 + IPandPort[5];
        ServerSocket listener = new ServerSocket(port, 1);

        new FTPhandler(filename, "STOR ").start();
        Socket dataStream = listener.accept();

        BufferedWriter dataToTransmit = new BufferedWriter(new OutputStreamWriter(dataStream.getOutputStream()));
        dataToTransmit.write(dataToUpload); // отправляем сообщение на сервер
        out.flush();
        dataToTransmit.close();
        dataStream.close();
        get();
        get();
    }

    public void disconnect() throws IOException {
        out("QUIT");
    }

    public int[] getSocket() throws IOException{
        String get = get();
        Scanner serverResponse = new Scanner(get);    //получение от сервера адреса и порта для соединения
        String servResp = "";
        for(int i = 0; i < 5; i++){
            servResp = serverResponse.next();
        }
        servResp = servResp.replace('(', ' ');
        servResp = servResp.replace(')', ' ');
        servResp = servResp.replace('.', ' ');
        servResp = servResp.replace(',', ' ');

        String[] numbers = servResp.split(" ");
        int[] ipAndPort = new int[6];
        for(int i = 1; i < 7; i++){
            ipAndPort[i - 1] = Integer.parseInt(numbers[i]);
        }
        return ipAndPort;
    }

    public int[] generateIPandPort(){
        int[] IPandPort = new int[6];
        String[] ipParts = serverIP.split("\\.");
        for(int i = 0; i < 4; i++){
            IPandPort[i] = Integer.parseInt(ipParts[i]);
        }
        IPandPort[4] = (int)Math.round(Math.random() * 1000 % 200 + 50);
        IPandPort[5] = (int)Math.round(Math.random() * 1000 % 200 + 50);

        return IPandPort;
    }

    public String menu(){
        StringBuilder menu = new StringBuilder();
        menu.append("Список студентов\t\t\t1\n");
        menu.append("Студент по номеру\t\t\t2\n");
        menu.append("Добавить студента\t\t\t3\n");
        menu.append("Удалить студента\t\t\t4\n");
        menu.append("Завершить работу\t\t\t5\n");
        menu.append("Переключить режим\t\t\t6\n");

        return menu.toString();
    }

    public boolean changeMode(){
        passiveMode = !passiveMode;
        return passiveMode;
    }

    public boolean isPassiveMode(){
        return passiveMode;
    }
}
