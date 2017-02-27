import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by ivan on 28.01.2017.
 */
public class DataplanePacketReceiver extends Thread{

//    private Socket ControlSocket = null;              //сокет упрвління через wifi
    private int StopFlag=0;                           //флаг зупинки потоків
//    private long CounterPacket=0;                      //лічильник прийнятих пакетів
//    private int AccesFlag=0;                          //флаг дозволу зміни лічильника
    private DatagramSocket UDPovsSock = null;         //UDP сервер сокет для прийому пакетів
//    private String IP="127.0.0.1";
//    private String name="server";
    private DataplaneStatistics dataplaneStatistics;
    private final int MAX_INCOMING_PACKET_SIZE = 1000;
    private final int DEFAULT_PORT_TO_LISTEN = 60001;
    private int port = DEFAULT_PORT_TO_LISTEN;
//    public static final byte[] FAILED_TO_START = {103};
//    public static final byte[] FAILED_TO_STOP = {106};
//    public static final byte[] START_SUCC = {100};
//    public static final byte[] STOP_SUCC = {105};

//********************MAIN********************************

    public DataplanePacketReceiver(){
        dataplaneStatistics = new DataplaneStatistics();
    }

//**************Поток UDP сокета****************
    public void run(){
        try{
            byte[] buffer = new byte[MAX_INCOMING_PACKET_SIZE]; // why 1000 ? what happens if packet has bigger size? 1300 ?
            DatagramPacket inMessage = new DatagramPacket(buffer,buffer.length);
            UDPovsSock = new DatagramSocket(this.port);                             //запуск сокета UDP
            while (StopFlag!=1){
                UDPovsSock.receive(inMessage);                                  //прийом повідомлень
                dataplaneStatistics.increasePacketCounter();
            }
        }
        catch (Exception e){
            //System.out.println(e);
        }

    }

    public DataplaneStatistics getDataplaneStatistics(){
        return dataplaneStatistics;
    }

    public void close(){
        if(UDPovsSock != null){
            this.UDPovsSock.close();
        }
    }

}

