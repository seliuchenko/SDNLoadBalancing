import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.io.*;

/**
 * Created by mseliuch on 09.02.2017.
 */
public class ManagementProtocol {

    //Link to the Whole SERVER
    private ServerContainer serverContainer;
    private static final byte[] SERIALIZATION_ERROR = {102};
    private static final byte[] SERVER_ERROR = {104};
    private static final byte[] INCORRECT_COMMAND = {101};


    public ManagementProtocol(ServerContainer serverContainer){
        this.serverContainer = serverContainer;
    }

    public byte[] incorrectCommand(){
        return this.INCORRECT_COMMAND;
    }

    public byte[] processIncomingData(byte[] incomingData){
        if(incomingData.length<0){
            return this.INCORRECT_COMMAND;
        }
        byte command = incomingData[0];
        try {
            switch (command) {
                case ManagementProtocolMessages.CMD_REQ_START: {
                    byte[] status = serverContainer.launchDataplanePacketReceiver();
                    return status;
                }
                case ManagementProtocolMessages.CMD_REQ_STOP: {
                    byte[] status = serverContainer.stopDataplanePacketReceiver();
                    return status;
                }
                case ManagementProtocolMessages.CMD_REQ_LOAD: {
                    DataplaneStatistics statistics = serverContainer.getDataplaneStatistics();
                    return seriazlize(serverContainer.getDataplaneStatistics());
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Serialization Exception for Data Statistics response");
            return this.SERIALIZATION_ERROR;
        }
//        switch (message){
//            case 0: {                               //зупинка прийому пакетів
//                try {
//                    controlSocket.close();
//                }catch (Exception e){ }
//                sleep(100);
//                System.out.println("accepted stoped");
//                break;
//            }
//            case 1: {                               //запуск прийому пакетів, обнулення лічильника
//                ServUDP.CounterPacket=0;
//                (new ServUDP()).start();
//                System.out.println("accepted start");
//                break;
//            }
//            case 2: {                               //відсилка числа прийнятих пакетів
//                sleep(10);
//                out.writeUTF(String.valueOf(name+" packet accepted = "+ServUDP.GetCounterPacket()));
//                out.flush();
//                System.out.println("sended number accepted packets");
//                break;
//            }
//            case 4: {                               //зупинка приграми
//                System.out.println("bye");
//                ServUDP.StopFlag=1;
//                try {
//                    ServUDP.UDPovsSock.close();
//                }catch (Exception e){ }
//                controlSocket.close();
//                break;
//            }
//        }
        return null;
    }

    private byte[] seriazlize(Object object) throws IOException{
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)){
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        }
    }

    private <T> T deserialize(byte[] data, int offset, Class<T> clazz) throws ClassNotFoundException, IOException {
        try(ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(data, offset, data.length-offset);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayOutputStream)){
            return (T) objectInputStream.readObject();
        }
    }

}
