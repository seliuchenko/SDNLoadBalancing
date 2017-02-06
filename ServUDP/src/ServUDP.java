import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * Created by ivan on 28.01.2017.
 */
public class ServUDP extends Thread{
    public static Socket ControlSocket = null;              //сокет упрвління через wifi
    public static int StopFlag=0;                           //флаг зупинки потоків
    public static long CounterPacket=0;                      //лічильник прийнятих пакетів
    public static int AccesFlag=0;                          //флаг дозволу зміни лічильника
    public static DatagramSocket UDPovsSock = null;         //UDP сервер сокет для прийому пакетів
    public static String IP="127.0.0.1";
    public static String name="server";
//********************MAIN********************************
    public static void main(String[] args)  {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (StopFlag!=1) {
            try {
                System.out.println("write IP admin server press 1");
                System.out.println("write name press 2");
                System.out.println("start control socket press 3");
                System.out.println("stop press 0");
                line = keyboard.readLine();

                switch (line){
                    case "1":{
                        System.out.println("write IP admin server");
                        IP = keyboard.readLine();
                        break;
                    }
                    case "2":{
                        System.out.println("write name");
                        name = keyboard.readLine();
                        break;
                    }
                    case "3":{
                        (new ContSock(IP,name)).start();
                        break;
                    }
                    case "0":{
                        StopFlag=1;
                        UDPovsSock.close();
                        ControlSocket.close();

                    }
                }

            }
            catch (Exception e){
                //System.out.println(e);
            }
        }



    }
//****************Метод зміни лічильника пакетів   ************
    public static void AddCounterPacket(){
        while (true) {
            if (AccesFlag == 0) {
                AccesFlag = 1;
                CounterPacket++;
                AccesFlag = 0;
                break;
            }
        }
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

//**************Поток UDP сокета****************
    public void run(){
        try{
            int i;
            byte[] bufer = new byte[1000];
            DatagramPacket inmessage = new DatagramPacket(bufer,bufer.length);
            UDPovsSock = new DatagramSocket(60001);                             //запуск сокета UDP
            while (StopFlag!=1){
                UDPovsSock.receive(inmessage);                                  //прийом повідомлень
                //i = Integer.parseInt(new String(inmessage.getData(),0,inmessage.getLength()));
                //if(i==0){
                    CounterPacket++;
                    //AddCounterPacket();
                //}
            }

        }
        catch (Exception e){
            //System.out.println(e);
        }

    }

}





class ContSock extends Thread {
    private String IP;
    private String name;
//конструктор
    public ContSock(String IP, String name){
        this.IP=IP;
        this.name=name;

    }

    public void run(){
        try{
            ServUDP.ControlSocket = new Socket(IP,60000);  //запуск сокета управління
            InputStream sin = ServUDP.ControlSocket.getInputStream();
            OutputStream sout = ServUDP.ControlSocket.getOutputStream();
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            out.writeUTF(name);                             //відправка імені
            out.flush();
            out.writeInt(2);
            out.flush();
            int message = 0;
            while (ServUDP.StopFlag!=1){
                message = in.readInt();
                switch (message){
                    case 0: {                               //зупинка прийому пакетів
                        try {
                            ServUDP.UDPovsSock.close();
                        }catch (Exception e){ }
                        sleep(100);
                        System.out.println("accepted stoped");
                        break;
                    }
                    case 1: {                               //запуск прийому пакетів, обнулення лічильника
                        ServUDP.CounterPacket=0;
                        (new ServUDP()).start();
                        System.out.println("accepted start");
                        break;
                    }
                    case 2: {                               //відсилка числа прийнятих пакетів
                        sleep(10);
                        out.writeUTF(String.valueOf(name+" packet accepted = "+ServUDP.GetCounterPacket()));
                        out.flush();
                        System.out.println("sended number accepted packets");
                        break;
                    }
                    case 4: {                               //зупинка приграми
                        System.out.println("bye");
                        ServUDP.StopFlag=1;
                        try {
                            ServUDP.UDPovsSock.close();
                        }catch (Exception e){ }
                        ServUDP.ControlSocket.close();
                        break;
                    }
                }

            }




        }
        catch (Exception e){
            //ServUDP.ControlSocket.close();
            ServUDP.UDPovsSock.close();
            //System.out.println(e);
        }

    }

}
