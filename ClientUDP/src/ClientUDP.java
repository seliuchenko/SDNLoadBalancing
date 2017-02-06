import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by ivan on 30.01.17.
 */
public class ClientUDP extends Thread {
    public static Socket ControlSocket = null;              //сокет упрвління через wifi
    public static int StopFlag=0;                           //флаг зупинки потоків
    public static long CounterPacket=0;                      //лічильник прийнятих пакетів
    public static int AccesFlag=0;                          //флаг дозволу зміни лічильника
    public static DatagramSocket UDPovsSock = null;         //UDP сервер сокет для прийому пакетів
    public static long pause=100;                           //затримка між відправками пакетів
    public static int NFlow=1;
    public static String IP="127.0.0.1";
    public static String IP_UDP="127.0.0.1";
    public static String name="client";
    private DatagramSocket s;

//***************конструктор*******************************
    public ClientUDP(DatagramSocket s){
        this.s=s;

    }

    public static void main(String[] args) {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (StopFlag!=1) {
            try {
                System.out.println("write IP admin server press 1");
                System.out.println("write name press 2");
                System.out.println("write IP_UDP_server press 4");
                System.out.println("start control socket press 3");
                System.out.println("stop press 0");
                line = keyboard.readLine();

                switch (line) {
                    case "1": {
                        System.out.println("write IP admin server");
                        IP = keyboard.readLine();
                        break;
                    }
                    case "4": {
                        System.out.println("write IP_UDP_server");
                        IP_UDP = keyboard.readLine();
                        break;
                    }
                    case "2": {
                        System.out.println("write name");
                        name = keyboard.readLine();
                        break;
                    }
                    case "3": {
                        (new ContSock(IP, name)).start();
                        break;
                    }
                    case "0": {
                        StopFlag = 1;
                        ControlSocket.close();
                        UDPovsSock.close();
                    }
                }

            } catch (Exception e) {
                UDPovsSock.close();
                //System.out.println(e);
            }


        }


    }




//****************Метод зміни лічильника пакетів   ************
    public synchronized static void AddCounterPacket(){
                AccesFlag = 1;
                CounterPacket++;
                AccesFlag = 0;

    }

//***************отримання даних лічильника пакетів********
    public static long GetCounterPacket(){
        long i=0;
        while(true) {
            if (AccesFlag == 0) {
                AccesFlag = 1;
                i = CounterPacket;
                AccesFlag = 0;
                break;
            }
        }
        return i;
    }
//******поток відправки пакетів***************************
    public void run(){
        try {
            DatagramPacket dp;
            byte[]data= new byte[1000];
            //data = "1".getBytes();
            while (StopFlag!=1){
                dp = new DatagramPacket(data,data.length, InetAddress.getByName(IP_UDP),60001);
                s.send(dp);
                AddCounterPacket();
                //CounterPacket++;
                sleep(pause);

            }
            UDPovsSock.close();
        }
        catch (Exception e){
            UDPovsSock.close();
            //System.out.println(e);
        }
    }
    
}


class ContSock extends Thread {
    private String IP;
    private String name;
    public ContSock(String IP, String name){
        this.IP=IP;
        this.name=name;

    }

    public void run(){
        try{
            ClientUDP.ControlSocket = new Socket(IP,60000);                 //запуск сокета управління
            InputStream sin = ClientUDP.ControlSocket.getInputStream();
            OutputStream sout = ClientUDP.ControlSocket.getOutputStream();
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            out.writeUTF(name);                                             //відправка імені на адмін сервер
            out.flush();
            out.writeInt(1);
            out.flush();
            int message = 0;
            while (ClientUDP.StopFlag!=1){
                message = in.readInt();
                switch (message){
                    case 0: {                               //зупинка відправки пакетів
                        try {
                            ClientUDP.UDPovsSock.close();
                        }catch (Exception e){ }
                        System.out.println("sendes stoped");
                        break;
                    }
                    case 1: {                               //запуск відправки пакетів, обнулення лічильника
                        ClientUDP.pause=in.readLong();
                        ClientUDP.NFlow=in.readInt();
                        ClientUDP.UDPovsSock = new DatagramSocket();
                        ClientUDP.CounterPacket=0;
                        sleep(10);
                        while (ClientUDP.NFlow!=0){
                            (new ClientUDP(ClientUDP.UDPovsSock)).start();
                            ClientUDP.NFlow--;
                            System.out.println("started client flow");

                        }
                        break;
                    }
                    case 2: {                               //відсилка числа відправлених пакетів
                        out.writeUTF(String.valueOf(name+" packets  sended = "+ClientUDP.GetCounterPacket()));
                        out.flush();
                        System.out.println("sended number sent packets");
                        break;
                    }
                    case 4: {                               //зупинка приграми
                        System.out.println("bye");
                        ClientUDP.StopFlag=1;
                        try {
                            ClientUDP.UDPovsSock.close();
                        }catch (Exception e){ }
                        ClientUDP.ControlSocket.close();
                        break;
                    }
                }

            }




        }
        catch (Exception e){
            //ClientUDP.ControlSocket.close();
            ClientUDP.UDPovsSock.close();
            //System.out.println(e);
        }

    }

}
