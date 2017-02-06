import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ivan on 31.01.17.
 */
public class Admin extends Thread {

    public static ServerSocket AdminSocket;
    public static int StopFlag=0;
    public static int AccessFlag=0;
    public static int ReadFlag=0;
    public static int Nflow=1;
    public static long pause=100;
    public static int order;


    public static void main(String[] args) {

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        String line;
        (new Admin()).start();

        try {
            while (StopFlag != 1) {

                System.out.println("press 1 to write pause");
                System.out.println("press 2 to write number flow client");
                System.out.println("press 3 to stop simulation");
                System.out.println("press 4 to start simulation");
                System.out.println("press 5 to read number send/accepted packet");
                System.out.println("press 6 to close connection");
                System.out.println("press 9 to write bandwidth");
                System.out.println("press 0 to exit");
                line = keyboard.readLine();
                switch (line) {
                    case "1": {                     //задаємо інтервал між пакетами
                        System.out.println("pause");
                        pause =Long.valueOf(keyboard.readLine());
                        break;
                    }
                    case "2": {                     //задаємо кількість потоків
                        System.out.println("NFlov");
                        Nflow = Integer.valueOf(keyboard.readLine());
                        break;
                    }
                    case "9": {                     //задаємо пропускну
                        System.out.println("Bandwidth");
                        int bandwidth=0;
                        while (true){
                            bandwidth=Integer.valueOf(keyboard.readLine());
                            if((bandwidth!=0)&&(bandwidth<=100))
                                break;
                        }
                        pause=35;
                        Nflow=bandwidth*5;
                        System.out.println("pause = "+pause);
                        System.out.println("number flov = "+Nflow);
                        break;
                    }
                    case "3": {                     //зупинка
                        order = 0;
                        AccessFlag = 1;
                        break;
                    }
                    case "4": {                     //запуск
                        order = 1;
                        AccessFlag = 1;
                        break;
                    }
                    case "5": {                     //отримання результатів
                        order = 2;
                        AccessFlag = 1;
                        break;
                    }
                    case "6": {                     //відключення клієнтів/серверів
                        order = 4;
                        AccessFlag = 1;
                        break;
                    }
                    case "0": {                     //закриття програми
                        order = 4;
                        AccessFlag = 1;
                        sleep(100);
                        StopFlag = 1;
                        break;
                    }
                    default:{
                        System.out.println("error");
                    }

                }
                sleep(200);
                AccessFlag = 0;


            }
            AdminSocket.close();
        }catch (Exception e){

            System.out.println("Main"+e);

        }



    }

//*********поток для встановлення з'єднань****************
    public void run(){
        try{
            AdminSocket = new ServerSocket(60000);
            System.out.println("socket started");
            while(StopFlag!=1) {
                Socket socket = AdminSocket.accept();
                (new connection(socket)).start();
                //sleep(100);
            }
        }catch (Exception e){

            System.out.println("Admin.run"+e);

        }
    }

}



class connection extends Thread{
    private int flag;
    private Socket socket;
    private String name;
    private int Nflow;
    private long pause;
    private int order;
    private int stop=0;
//*********конструктор********************************
    public connection(Socket s){
        socket = s;
    }



    public void run(){
        try {
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);
            name = in.readUTF();
            System.out.println(name+" conected");
            flag=in.readInt();
            while((Admin.StopFlag!=1)&&(stop!=1)){
                if (Admin.AccessFlag==1){
                    if(Admin.ReadFlag==0){
                        Admin.ReadFlag=1;                       //читаєм дані для
                        Nflow = Admin.Nflow;                    //відправки запитів
                        pause = Admin.pause;                    //до клієнта/сервера
                        order = Admin.order;
                        Admin.ReadFlag=0;
                        sleep(100);
                        switch (order){
                            case 0:{                            //зупинка відправки/отримання пакетів
                                out.writeInt(0);
                                out.flush();
                                break;
                            }
                            case 1:{                            //старт відправки/отримання пакетів
                                out.writeInt(1);
                                out.flush();
                                if (flag==1){
                                    //System.out.println("client");
                                    out.writeLong(pause);
                                    out.flush();
                                    out.writeInt(Nflow);
                                    out.flush();
                                }
                                break;
                            }
                            case 2:{                            //зупинка відправки/отримання пакетів
                                out.writeInt(0);
                                out.flush();
                                sleep(200);
                                out.writeInt(2);                //запит на кількість пакетів
                                out.flush();
                                System.out.println(in.readUTF());
                                break;
                            }
                            case 4:{                            //відключення
                                out.writeInt(4);
                                out.flush();
                                stop=1;
                            }

                        }



                    }
                }
                sleep(100);
            }
            System.out.println("connection closed");
            socket.close();

        }catch (Exception e){
            try{
                socket.close();
            }catch (Exception b){

            }
        }
    }

}
