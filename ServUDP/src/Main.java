import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by mseliuch on 09.02.2017.
 */
public class Main {
    public static void main(String...args){
        new ServerContainer().launchManagementConnectionHadler();


        //            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
//            String line;
//            while (StopFlag!=1) {
//                try {
//                    System.out.println("write IP admin server press 1");
//                    System.out.println("write name press 2");
//                    System.out.println("start control socket press 3");
//                    System.out.println("stop press 0");
//                    line = keyboard.readLine();
//
//                    switch (line){
//                        case "1":{
//                            System.out.println("write IP admin server");
//                            IP = keyboard.readLine();
//                            break;
//                        }
//                        case "2":{
//                            System.out.println("write name");
//                            name = keyboard.readLine();
//                            break;
//                        }
//                        case "3":{
//                            (new ContSock(IP,name)).start();
//                            break;
//                        }
//                        case "0":{
//                            StopFlag=1;
//                            UDPovsSock.close();
//                            ControlSocket.close();
//
//                        }
//                    }
//
//                }
//                catch (Exception e){
//                    //System.out.println(e);
//                }
//            }



    }
}
