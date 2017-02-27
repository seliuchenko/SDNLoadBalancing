import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mseliuch on 09.02.2017.
 */
public class ManagementConnectionHandler extends Thread {


    private boolean stopFlag = false;
    private ManagementProtocol managementProtocol;
    private Socket controlSocket;
    //конструктор
    public ManagementConnectionHandler(Socket socket, ManagementProtocol managementProtocol){
        this.managementProtocol = managementProtocol;
        this.controlSocket = socket;
    }

    public void run(){
        try{
            DataInputStream in = new DataInputStream(controlSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(controlSocket.getOutputStream());
            while(!stopFlag){
                int messageSize = in.readInt();
                byte[] response;
                if(messageSize > 0 ) {
                    byte[] incomingData = new byte[messageSize];
                    in.readFully(incomingData);
                    response = managementProtocol.processIncomingData(incomingData);
                } else {
                    response = managementProtocol.incorrectCommand();
                }
                int outgoingDataSize = response.length;
                out.writeInt(outgoingDataSize);
                out.write(response);
            }
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Closing management connection handler.");
            this.close();
        }
    }

    public void close() {
        this.stopFlag = true;
        if(this.controlSocket != null){
            try{
                this.controlSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.controlSocket = null;
        }
    }
}
