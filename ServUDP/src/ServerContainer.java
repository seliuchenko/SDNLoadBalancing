import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by mseliuch on 10.02.2017.
 */
public class ServerContainer {
    private static final Logger LOGGER = Logger.getLogger(ServerContainer.class.getName());

    private ManagementProtocol managementProtocol;
    public static final int DEFAULT_MANAGEMENT_PORT = 9999;
    private int managementPort = DEFAULT_MANAGEMENT_PORT;
    private static final String DEFAULT_HOST = "localhost";
    private ManagementConnectionHandler managementConnectionHandler;
    DataplanePacketReceiver dataplanePacketReceiver;

    public void launchManagementConnectionHadler(){
        Socket controlSocket = null;
        try{
            LOGGER.info("Listening for management connection at "+
                    InetAddress.getByName(DEFAULT_HOST)+":"+
                    String.valueOf(this.managementPort));
            controlSocket = new ServerSocket(this.managementPort).accept();
            managementProtocol = new ManagementProtocol(this);
            managementConnectionHandler = new ManagementConnectionHandler(controlSocket,
                    managementProtocol);
            managementConnectionHandler.start();
            LOGGER.info("Management connection established.");
            managementConnectionHandler.join();
        } catch (InterruptedException e) {
            LOGGER.severe("Management connection was closed. Reason: "+e.getMessage());
        }
        catch (IOException e){
            e.printStackTrace();
            LOGGER.severe("Failed to establish management connection with Admin App.");
        }
    }

    public byte[] launchDataplanePacketReceiver(){
        if(dataplanePacketReceiver != null)
            return new byte[]{ManagementProtocolMessages.CMD_RES_FAILED_TO_START};
        try {
            dataplanePacketReceiver = new DataplanePacketReceiver();
            dataplanePacketReceiver.start();
        } catch (Exception e){
            e.printStackTrace();
            LOGGER.severe("Unable to start data plane packet receiver.");
            return new byte[]{ManagementProtocolMessages.CMD_RES_FAILED_TO_START};
        }
        return new byte[]{ManagementProtocolMessages.CMD_RES_START_SUCC};
    }

    public byte[] stopDataplanePacketReceiver(){
        if(dataplanePacketReceiver == null)
            return new byte[]{ManagementProtocolMessages.CMD_RES_FAILED_TO_STOP};
        dataplanePacketReceiver.close();
        try {
            dataplanePacketReceiver.join();
            return new byte[]{ManagementProtocolMessages.CMD_RES_STOP_SUCC};
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new byte[]{ManagementProtocolMessages.CMD_RES_FAILED_TO_STOP};
        }
    }

    public DataplaneStatistics getDataplaneStatistics(){
        return this.dataplanePacketReceiver != null ?
                dataplanePacketReceiver.getDataplaneStatistics() :
                new DataplaneStatistics();
    }

}
