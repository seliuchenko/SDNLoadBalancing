/**
 * Created by mseliuch on 10.02.2017.
 */
public class ManagementProtocol {
    private ManagementPanel managementPanel;
    public ManagementProtocol(ManagementPanel managementPanel){
        this.managementPanel = managementPanel;
    }

    public byte[] processIncomingData(byte[] incomingData) {
        if(incomingData[0] == ManagementProtocolMessages.CMD_RES_FAILED_TO_STOP){
            return new byte[]{ManagementProtocolMessages.CMD_RES_FAILED_TO_STOP};
        }
        return null;
    }

    public byte[] incorrectCommand() {
        return null;
    }
}
