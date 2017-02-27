/**
 * Created by mseliuch on 09.02.2017.
 */
public interface ManagementProtocolMessages {
    byte CMD_REQ_START = 5;
    byte CMD_RES_START_SUCC = 6;
    byte CMD_REQ_STOP = 10;
    byte CMD_RES_STOP_SUCC = 11;
    byte CMD_REQ_LOAD = 20;
    byte CMD_RES_LOAD = 21;

    byte CMD_RES_FAILED_TO_START = 103;
    byte CMD_RES_FAILED_TO_STOP = 106;

}
