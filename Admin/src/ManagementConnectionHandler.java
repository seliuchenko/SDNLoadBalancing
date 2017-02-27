import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by mseliuch on 09.02.2017.
 */
public class ManagementConnectionHandler{


    private boolean stopFlag = false;
    private ManagementProtocol managementProtocol;
    private Socket controlSocket;
    DataOutputStream out;
    DataInputStream in;

    //конструктор
    public ManagementConnectionHandler(ManagementProtocol managementProtocol) {
        this.managementProtocol = managementProtocol;
    }

    public void bindToSocket(Socket socket) throws IOException {
        this.controlSocket = socket;
        in = new DataInputStream(controlSocket.getInputStream());
        out = new DataOutputStream(controlSocket.getOutputStream());
    }

    public byte[] sendCommand(byte[] command) {
        try {
            int outgoingDataSize = command.length;
            out.writeInt(outgoingDataSize);
            out.write(command);

            int messageSize = in.readInt();
            byte[] response;
            if (messageSize > 0) {
                byte[] incomingData = new byte[messageSize];
                in.readFully(incomingData);
                return managementProtocol.processIncomingData(incomingData);
            } else {
                return managementProtocol.incorrectCommand();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Closing management connection handler.");
            this.close();
            return null;
        }
    }

    public void close() {
        this.stopFlag = true;
        if (this.controlSocket != null) {
            try {
                this.controlSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.controlSocket = null;
        }
    }
}
