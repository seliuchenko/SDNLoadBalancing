import java.io.Serializable;

/**
 * Created by mseliuch on 10.02.2017.
 */
public class DataplaneStatistics implements Serializable {
    //****************Метод зміни лічильника пакетів   ************
    private long receivedPacketCount = 0L;

    protected synchronized void increasePacketCounter(){
        receivedPacketCount++;
    }
    //***************отримання даних лічильника пакетів********
    protected synchronized long getPacketCounter(){
        return receivedPacketCount;
    }

    protected synchronized long resetCounter(){
        long count = receivedPacketCount;
        receivedPacketCount = 0;
        return count;

    }
}
