import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
/**
 * Created by ivan on 31.01.17.
 */
public class ManagementPanel extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ManagementPanel.class.getName());

    public static final Byte MANAGEMENT_CONNECTION_TO_SERVER = 1;
    public static final Byte MANAGEMENT_CONNECTION_TO_CLIENT = 2;
    private boolean stopFlag = false;

    Interpreter interpreter;
    ManagementProtocol managementProtocol;
    ManagementConnectionHandler serverManagementConnectionHandler;
    ManagementConnectionHandler clientManagementConnectionHandler;


    public ManagementPanel() {
        managementProtocol = new ManagementProtocol(this);
        interpreter = new Interpreter(this, managementProtocol);
    }

    public void run() {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        while (!stopFlag) {
            try {
                interpreter.processUserCommand(userInput.readLine());
            } catch (IOException e) {
                LOGGER.severe("Error readin user input. "+e.getMessage());
            }
        }

    }


    public void connectToOverManagementChannel(String address, int port, byte type){
        if( (type == MANAGEMENT_CONNECTION_TO_SERVER && serverManagementConnectionHandler != null) ||
                (type == MANAGEMENT_CONNECTION_TO_SERVER && serverManagementConnectionHandler != null)) {
            LOGGER.severe("Can't establish new connection while another connection.");
            return;
        }
        Socket socket;
        try {
            socket = new Socket(address, port);
            ManagementConnectionHandler connection =  new ManagementConnectionHandler(managementProtocol);
            connection.bindToSocket(socket);
            if(type == MANAGEMENT_CONNECTION_TO_SERVER){
                serverManagementConnectionHandler = connection;
            } else if (type == MANAGEMENT_CONNECTION_TO_CLIENT){
                clientManagementConnectionHandler = connection;
            }
            LOGGER.info("Management connection established with "+address+":"+String.valueOf(port));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.severe("Failed to established management connection with "+address+":"+String.valueOf(port));
        }
    }

    public void startSimulation(){
        if(serverManagementConnectionHandler != null){
            serverManagementConnectionHandler.sendCommand(new byte[]{ManagementProtocolMessages.CMD_REQ_START});
        }
        //send command over management connection to server]
        //wait for success of server start
        //send command to launch client
    }

    public void stopSimulation(){
        if(serverManagementConnectionHandler != null){
            byte[] d = serverManagementConnectionHandler.sendCommand(new byte[]{ManagementProtocolMessages.CMD_REQ_STOP});
            System.out.print(d);
        }
        //send command for clients to stop simulation
        //receive ack from clients
        //send command to server to stop processing
    }

    public byte[] getSimulationStatistics(){
        if(serverManagementConnectionHandler != null){
            serverManagementConnectionHandler.sendCommand(new byte[]{ManagementProtocolMessages.CMD_REQ_LOAD});
        }
        //get statistics from client
        //get statistics from server
        return null;
    }

    public void setServerManagementConnectionHandler(ManagementConnectionHandler serverManagementConnectionHandler) {
        this.serverManagementConnectionHandler = serverManagementConnectionHandler;
    }

    public void setClientManagementConnectionHandler(ManagementConnectionHandler clientManagementConnectionHandler) {
        this.clientManagementConnectionHandler = clientManagementConnectionHandler;
    }

    public ManagementConnectionHandler getServerManagementConnectionHandler() {
        return serverManagementConnectionHandler;
    }

    public ManagementConnectionHandler getClientManagementConnectionHandler() {
        return clientManagementConnectionHandler;
    }
}
