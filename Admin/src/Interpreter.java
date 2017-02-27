/**
 * Created by mseliuch on 10.02.2017.
 */
public class Interpreter {

//    ManagementProtocol managementProtocol;
    private static final int MAX_FLOW_SPEED = 100; //Mbps
    private static final int MIN_FLOW_SPEED = 1; //Mbps
    private ManagementProtocol managementProtocol;
    private ManagementPanel managementPanel;


    public Interpreter(ManagementPanel panel, ManagementProtocol protocol){
        this.managementPanel = panel;
        this.managementProtocol = protocol;
    }

    protected boolean isFlowSpeedValid(int flowSpeed){
        return (flowSpeed>MIN_FLOW_SPEED && flowSpeed<MAX_FLOW_SPEED);
    }

    /**
     * There are the following commands available for interpretation
     * simulation start --clients <number of clients> --speed <speed of flow of each client>
     * simulation stop
     * simulation statistics
     * connect --ip <ip address> --port <port number> --type <[server,client]></></></>
     * connect --ip 127.0.0.1 --port 9999 --type server
     * @param userCommand
     */

    public void processUserCommand(String userCommand){
        String[] commandParts = userCommand.split("--");
//        if(commandParts.length > 1){
            switch (commandParts[0].trim()) {
                case "simulation start": {
                    int clientsCount = 0;
                    int flowSpeed = 0;
                    if(commandParts[1].trim().contains("clients")){
                        clientsCount = Integer.parseInt(commandParts[1].split(" ")[1]);
                    }
                    if(commandParts[2].trim().contains("speed")){
                        flowSpeed = Integer.parseInt(commandParts[2].split(" ")[1]);
                    }
                    if(clientsCount>0 && isFlowSpeedValid(flowSpeed)){
                        this.managementPanel.startSimulation();
                    }
                    break;
                }
                case "simulation stop": {
                    this.managementPanel.stopSimulation();
                    break;
                }
                case "simulation status": {
                    this.managementPanel.getSimulationStatistics();
                    break;
                }
                case "connect":{
                    String address = null;
                    int port = 0;
                    byte type = ManagementPanel.MANAGEMENT_CONNECTION_TO_SERVER;
                    if(commandParts[1].trim().contains("ip")){
                        address = commandParts[1].split(" ")[1];
                    }
                    if(commandParts[2].trim().contains("port")){
                        port = Integer.parseInt(commandParts[2].split(" ")[1]);
                    }
                    if(commandParts[3].trim().contains("type")){
                        if(commandParts[3].split(" ")[1].equals("server")){
                            type = ManagementPanel.MANAGEMENT_CONNECTION_TO_SERVER;
                        } else if (commandParts[3].split(" ")[1].equals("client")){
                            type = ManagementPanel.MANAGEMENT_CONNECTION_TO_CLIENT;
                        }
                    }
                    this.managementPanel.connectToOverManagementChannel(address, port, type);
                    break;
                }
                default: {
                    System.out.println("Unknown command.");
                }
            }


//        }
        System.out.println("Unknown command.");

    }
}
