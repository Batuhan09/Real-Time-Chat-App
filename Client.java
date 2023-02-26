// Java implementation for multithreaded chat client
// Save file as Client.java

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class Client
{
    static int currId = -1;
    static DatabaseEntry currentUser = new UserDBE();
    final static int ServerPort = 1234;
    static int is_admin;
    public static void task2(DataOutputStream dos) throws IOException {

        dos.writeUTF("getmsg#$1#$ ");
    }
    public static void task3(DataOutputStream dos) throws IOException {
        dos.writeUTF("view#$all");
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
        System.out.println("id:");
        int id2= sc.nextInt();
        dos.writeUTF("getmsg#$"+currId+"#$"+id2);
    }
    public static void task4(DataOutputStream dos) throws IOException {
        dos.writeUTF("view#$all");
    }
    public static void task5(DataOutputStream dos) throws IOException {
        dos.writeUTF("view#online");
    }
    public static void task6(DataOutputStream dos) throws ParseException, IOException {
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
        String userName = "";
        String lastname = "";
        String password = "";
        String role = "";
        String gender = "";
        while(userName.equals("")){
            System.out.println("First Name:");
            userName= sc.nextLine();
        }
        while(lastname.equals("")){
            System.out.println("Last Name:");
            lastname= sc.nextLine();
        }
        while(password.equals("")){
            System.out.println("password:");
            password= sc.nextLine();
        }
        while(!role.equals("admin") && !role.equals("regular")){
            System.out.println("role(type admin or regular):");
            role= sc.nextLine();
        }
        System.out.println("Enter user birthdate day(0-31)");
        String birthdate_day= sc.nextLine();
        System.out.println("Enter user birthdate month(0-12)");
        String birthdate_month= sc.nextLine();
        System.out.println("Enter user birthdate year(e.g 1995)");
        String birthdate_year= sc.nextLine();
        while(gender.equals("")){
            System.out.println("gender(type F for female, M for male):");
            gender= sc.nextLine();
        }
        String available = "offline";
        dos.writeUTF("add#$"+userName+"#$"+lastname+"#$"+password+"#$"+role+"#$"+birthdate_day+"#$"+birthdate_month+"#$"+birthdate_year+"#$"+available+"#$"+gender);
    }
    public static void task7(DataOutputStream dos) throws IOException {
        task4(dos);
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
        System.out.println("id:");
        int id= sc.nextInt();
        dos.writeUTF("delete#$"+id);
    }
    public static void task8(DataOutputStream dos) throws IOException {
        task4(dos);
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
        System.out.println("Choose user with id:");
        int id= sc.nextInt();
        System.out.println("choose \n1-username\n2-lastname\n3-role");
        int selected = sc.nextInt();
        Scanner sc2= new Scanner(System.in);
        if(selected==1){
            String new_name = "";
            while(new_name.equals("")){
                System.out.println("new Username:");
                new_name = sc2.nextLine();
            }
            dos.writeUTF("update#$1#$"+id+"#$"+new_name);
        }
        else if(selected==2){
            String new_lastname="";
            while (new_lastname.equals("")){
                System.out.println("new Lastname:");
                new_lastname = sc2.nextLine();
            }
            dos.writeUTF("update#$2#$"+id+"#$"+new_lastname);
        }
        else if(selected==3){
            String new_role="";
            while(!(new_role.equals("admin") || new_role.equals("regular"))){
                System.out.println("role(type admin or regular):");
                new_role= sc2.nextLine();
            }
            dos.writeUTF("update#$$3#$"+id+"#$"+new_role);
        }
        else{
            System.out.println("NOT VALID");
        }
    }

    public static void task9(DataOutputStream dos) throws IOException {
        task4(dos);
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
        System.out.println("id:");
        int id= sc.nextInt();
        dos.writeUTF("getuserinfo#$"+id);
    }


    public static void select_from_list(int selection, DataOutputStream dos) throws ParseException, IOException {
        switch (selection){
            case 1:
                break;
            case 2:
                task2(dos);
                break;
            case 3:
                task3(dos);
                break;
            case 4:
                task4(dos);
                break;
            case 5:
                task5(dos);
                break;
            case 6:
                task6(dos);
                break;
            case 7:
                task7(dos);
                break;
            case 8:
                task8(dos);
                break;
            case 9:
                task9(dos);
                break;
        }
    }

    public static void sign_in(DataOutputStream dos, DataInputStream dis) throws IOException {

        String received = "";

        String userName = "";
        String password = "";
        while (true){
            System.out.println("Sign-in page");
            Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
            System.out.println("First name");
            userName= sc.nextLine();
            System.out.println("Password");
            password = sc.nextLine();
            dos.writeUTF("login#$"+userName+"#$"+password);
            received = dis.readUTF();
            if(received.equals("SUCCESFULL!")) break;
        }
        while(true){
            System.out.println("received "+received);
            received = dis.readUTF();
            if(received.charAt(0) == '?'){
                currId = parseInt(received.substring(1,received.length()));
            } else if (received.charAt(0) == '*') {
                is_admin = parseInt(received.substring(1,2));
                break;
            }
        }
    }
    public static void sign_up(DataOutputStream dos, DataInputStream dis) throws ParseException, IOException {
        Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
        String userName = "";
        String lastname = "";
        String password = "";
        String gender = "";
        while(userName.equals("")){
            System.out.println("First Name:");
            userName= sc.nextLine();
        }
        while(lastname.equals("")){
            System.out.println("Last Name:");
            lastname= sc.nextLine();
        }
        while(password.equals("")) {
            System.out.println("password:");
            password = sc.nextLine();
        }
        System.out.println("Enter user birthdate day(0-31)");
        String birthdate_day= sc.nextLine();
        System.out.println("Enter user birthdate month(0-12)");
        String birthdate_month= sc.nextLine();
        System.out.println("Enter user birthdate year(e.g 1995)");
        String birthdate_year= sc.nextLine();
        while(gender.equals("")){
            System.out.println("gender(type F for female, M for male):");
            gender= sc.nextLine();
        }
        String available = "offline";
        dos.writeUTF("add#$"+userName+"#$"+lastname+"#$"+password+"#$"+"regular"+"#$"+birthdate_day+"#$"+birthdate_month+"#$"+birthdate_year+"#$"+available+"#$"+gender);
        System.out.println("OK. can login");
        System.out.println("You are redirected to login page");
        String a = "";
        while(!a.equals("wait")){
            a = dis.readUTF();
        }
        sign_in(dos,dis);
    }
    public static void signInUp(DataOutputStream dos, DataInputStream dis) throws ParseException, IOException {
        while (true){
            Scanner sc= new Scanner(System.in); //System.in is a standard input stream.
            System.out.print("  CHAT APP \n1- sign-in \n2- sign-up");
            int a= sc.nextInt();
            if(a==1){
                sign_in(dos,dis);//check inputs return
                break;
            }
            else if(a==2){
                sign_up(dos,dis);//check inputs and save user to database then return
                break;
            }
            else{
                System.out.println("Please type valid entrance number");
            }
        }
    }
    public static boolean input_valid(String msg){
        String[] valid_inputs = {};

        if(is_admin==1){
            valid_inputs = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        }
        else{
            valid_inputs = new String[]{"1", "2", "3", "4", "5"};
        }
        for (String element : valid_inputs) {
//            System.out.println(element+" "+msg);
//            System.out.println(element.equals(msg));
            if (element.equals(msg)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String args[]) throws UnknownHostException, IOException, ParseException {
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);
        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        signInUp(dos, dis);

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {
                    String msg = "";
                    // read the message to deliver.
                    while(true){
                        msg = scn.nextLine();
                        if(!input_valid(msg)){
                            if(msg.equals("logout")){
                                try {
                                    dos.writeUTF("logout");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("Logging out");
                                System.exit(0);
                                }else if(msg.equals("help")){
                                try {
                                    dos.writeUTF("help");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else if(!msg.equals("1")){
                            try {
                                select_from_list(parseInt(msg),dos);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else if (msg.equals("1")) {
                            System.out.println("To(Choose user with id):");  // Connection established and 7yi degistir
                            try {
                                dos.writeUTF("view#$all");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            int to  = scn.nextInt();
                            Scanner scn2 = new Scanner(System.in);
                            System.out.println("Title:");
                            String title = scn2.nextLine();
                            System.out.println("Message:");
                            String message = scn2.nextLine();
                            String prot = "from:" + "null" + " " +"null" + "#$" + "to:"+Integer.toString(to)+"#$"+"title:"+title+"#$"+"message:"+message;
                            try {
                                dos.writeUTF(prot);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                String msg = "";
                while (true) {
                    try {
                        // read the message sent to this client
                        msg = dis.readUTF();
                        if(msg.equals("deleted")){
                            System.exit(0);
                        } else if (!msg.equals("wait")) {
                            System.out.println(msg);
                            if (msg.equals("admin1")) is_admin = 1;
                            else if (msg.equals("admin0")) is_admin = 0;

                        }
                        //System.out.println("You have a new message,\n"+msg);
                    } catch (EOFException e){
                        System.out.println("Logging out");
                        System.exit(0);
                    } catch(IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }

}
