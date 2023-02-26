// Java implementation of  Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;

import static java.lang.Integer.parseInt;

// Server class
public class Server
{

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    // counter for clients
    static int i = 0;

    public static void main(String[] args) throws IOException, ParseException {
        DatabaseEntry mdbe = new MessageDBE();
        DatabaseEntry dbe = new UserDBE();
        Database db = new Database();
        Connection connection = db.connect_to_db("Users","postgres","Baaymuan09.");
        db.start_database(connection);
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        // running infinite loop for getting
        // client request
        while (true)
        {
            // Accept the incoming request
            s = ss.accept();

            System.out.println("New client request received : " + s);

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            System.out.println("Adding this client to active client list");

            // add this client to active clients list
            ar.add(mtch);

            // start the thread.
            t.start();

            i++;

        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    DatabaseEntry currentUser = new UserDBE();

    String admin_help = "                    Admin Documentation(You can type help to read doc.)     \n" +
            "You can type the numbers below to apply corresponding functionalities\n" +
            "1 - SEND MESSAGE\n" +
            "2 - VIEW(GET) LAST 10 MESSAGES\n" +
            "3 - VIEW(GET) LAST 10 MESSAGES WITH THE SPECIFIC USER\n" +
            "4 - VIEW ALL USERS\n"+
            "5 - VIEW ONLINE USERS\n"+
            "6 - ADD USER\n"+
            "7 - DELETE USER\n"+
            "8 - UPDATE USER\n"+
            "9 - GET USER INFO\n"+
            "To logout from the app, type logout";
    String regular_help = "                  User Documentation(You can type help to read doc.)    \n" +
            "You can type the numbers below to apply corresponding functionalities\n" +
            "1 - SEND MESSAGE\n" +
            "2 - VIEW LAST 10 MESSAGES in inbox\n" +
            "3 - VIEW LAST 10 MESSAGES WITH THE SPECIFIC USER\n" +
            "4 - VIEW ALL USERS\n"+
            "5 - VIEW ONLINE USERS\n"+
            "To logout from the app type logout";
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;
    DatabaseEntry mdbe = new MessageDBE();
    DatabaseEntry dbe = new UserDBE();
    String password;

    Database db = new Database();
    Connection connection = db.connect_to_db("Users","postgres","Baaymuan09.");
    String userName = "";

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) throws IOException {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    public static Date return_birthday(String day, String month, String year) throws ParseException {
        String str = day+"-"+month+"-"+year;
        SimpleDateFormat obj = new SimpleDateFormat("dd-MM-yyyy");
        long epoch = obj.parse(str).getTime();
        //Creating java.util.Date object
        java.sql.Date date = new java.sql.Date(epoch);
        return date;
    }

    public void send_message(String received) throws IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String from = st.nextToken();
        String recipient = st.nextToken().substring(3);
        System.out.println("recipient"+recipient);
        String title = st.nextToken();
        String MsgToSend = st.nextToken();
        DatabaseEntry new_dbe = new UserDBE();
        new_dbe = ((UserDBE)dbe).getUserbyId(connection,parseInt(recipient),"");
        if(new_dbe !=null) {
            for (ClientHandler mc : Server.ar) {
                System.out.println(mc.name);
                // if the recipient is found, write on its
                // output stream
                if (mc.name.equals(recipient)) {
                    System.out.println("here");
                    try {
                        mc.dos.writeUTF("You have a new message!\nfrom: " + ((UserDBE) currentUser).name + " " + ((UserDBE) currentUser).lastname + "\n" + title + "\n" + MsgToSend );
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
            DatabaseEntry new_mdbe = new MessageDBE(parseInt(name),parseInt(recipient),title.substring(6,title.length()),MsgToSend.substring(8,MsgToSend.length()));
            ((MessageDBE)new_mdbe).insert_row(connection);
        }else{
            dos.writeUTF("INVALID USER");
        }

    }
    public void get_message(String received) throws IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String type = st.nextToken();
        String fromId = st.nextToken();
        String toId = st.nextToken();
        if(toId.equals(" ")){
            System.out.println(parseInt(name));
            String result =((MessageDBE)mdbe).get_last_ten(connection,parseInt(name));
            dos.writeUTF(result);
        }else{
            System.out.println("name "+parseInt(name));
            dos.writeUTF(((MessageDBE)mdbe).get_last_ten_with_specified(connection,parseInt(name),parseInt(toId)));
            //getLasttenmessageswithspecificUser
        }
    }
    public void view_users(String received) throws IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String type = st.nextToken();
        String availability = st.nextToken();
        if(availability.equals("all")){//get all users
            String result =((UserDBE)dbe).getUsers(connection);
            System.out.println(result);
            dos.writeUTF(result);
        }else{
            String result = ((UserDBE)dbe).getOnlineUsers(connection);
            dos.writeUTF(result);
            //getOnlineUsers
        }
    }
    public void delete_user(String received) throws IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String type = st.nextToken();
        String deletedId = st.nextToken();
        if(((UserDBE)dbe).getUserbyId(connection,parseInt(deletedId),"")!=null){//eger silinecek user varsa
            ((UserDBE)dbe).delete_row_by_id(connection,parseInt(deletedId));
            for (ClientHandler mc : Server.ar) {
                System.out.println(mc.name);
                // if the recipient is found, write on its
                // output stream
                if (mc.name.equals(deletedId)) {
                    System.out.println("here");
                    //System.out.println(MsgToSend);
                    //mc.dos.writeUTF(this.name+" : "+MsgToSend);
                    try {
                        mc.dos.writeUTF("deleted");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
            dos.writeUTF("DELETED");}
        else dos.writeUTF("NOT VALID");
        //deleteUser
    }
    public void update_user(String received) throws IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String type = st.nextToken();
        String column = st.nextToken();
        String updatedId = st.nextToken();
        String new_column = st.nextToken();
        if(((UserDBE)dbe).getUserbyId(connection,parseInt(updatedId),"")!=null) {//eger silinecek user varsa
            if (column.equals("1")) {
                ((UserDBE) dbe).update_name(connection, parseInt(updatedId), new_column);
                //update name
            } else if (column.equals("2")) {
                ((UserDBE) dbe).update_lastname(connection, parseInt(updatedId), new_column);
                //update lastname
            } else if (column.equals("3")) {
                ((UserDBE) dbe).update_role(connection, parseInt(updatedId), new_column);
                for (ClientHandler mc : Server.ar) {
                    System.out.println(mc.name);
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(updatedId)) {
                        System.out.println("here");
                        //System.out.println(MsgToSend);
                        //mc.dos.writeUTF(this.name+" : "+MsgToSend);
                        try {
                            if(new_column.equals("admin")) mc.dos.writeUTF("admin1");
                            else mc.dos.writeUTF("admin0");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    }
                }

                //update role
            }
        }else System.out.println("NOT VALID");
    }
    public void add_user(String received) throws ParseException, IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String type = st.nextToken();
        String name = st.nextToken();
        String lastname = st.nextToken();
        String addedpassword = st.nextToken();
        String role = st.nextToken();
        String day = st.nextToken();
        String month = st.nextToken();
        String year = st.nextToken();
        Date date = return_birthday(day,month,year);
        String available =st.nextToken();
        String gender = st.nextToken();
        DatabaseEntry new_dbe = new UserDBE(name, lastname,addedpassword,role,date,gender,available);//parameters USER CLASS
        ((UserDBE)new_dbe).insert_row(connection);
        dos.writeUTF("ADDED");
        dos.writeUTF("wait");
    }
    public void login(String received) throws IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String type = st.nextToken();
        String name = st.nextToken();
        password = st.nextToken();
        userName = name;
        DatabaseEntry user = new UserDBE();
        //System.out.println(((UserDBE)currentUser).name);
        if(((UserDBE)dbe).user_exists(connection,name,password)){
            currentUser = ((UserDBE)user).getCurrentUser(connection,name,password);
            dos.writeUTF("SUCCESFULL!");
            int id = ((UserDBE)dbe).getId(connection,name,password);
            int is_admin = ((UserDBE)dbe).is_admin(connection,"Users",name,password);
            ((UserDBE)dbe).update_available(connection,id,"online");
            this.name = Integer.toString(id);
            dos.writeUTF("?"+id);
            dos.writeUTF("*"+Integer.toString(is_admin));
            if(is_admin == 1){
                dos.writeUTF(admin_help);
            }else{
                dos.writeUTF(regular_help);
            }
        }else{
            dos.writeUTF("INVALID");
        }
    }
    public void get_user_info(String received) throws IOException {
        StringTokenizer st = new StringTokenizer(received, "#$");
        String type = st.nextToken();
        String id = st.nextToken();
        DatabaseEntry user =((UserDBE)dbe).getUserbyId(connection,parseInt(id),"");
        if(user!=null) {
            dos.writeUTF("name:" + ((UserDBE) user).name + "\n" +
                    "lastname:" + ((UserDBE) user).lastname + "\n" +
                    "birthdate:" + ((UserDBE) user).birthdate + "\n" +
                    "role:" + ((UserDBE) user).role + "\n" +
                    "gender:" + ((UserDBE) user).gender
            );
        }else dos.writeUTF("INVALID USER");
    }
    @Override
    public void run() {


        String received = null;
        String receivedid = null;
        String MsgToSend = "";
        String from = "";
        String to = "";
        String title = "";
        String recipient="";
        while (true)
        {
            try
            {
                // receive the string
                received = dis.readUTF();
                System.out.println(received);
                if(received.charAt(0) == '?'){
                    name = received.substring(1);
                    //received = dis.readUTF();
                }
                else if(received.equals("logout")){
                    ((UserDBE)dbe).update_available(connection,parseInt(name),"offline");
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }
                else if(received.length()>=4 && received.substring(0,4).equals("from")){
                    send_message(received);
                } else if (received.length()>=6 && received.substring(0,6).equals("getmsg")) {
                    get_message(received);
                } else if (received.length()>=4 && received.substring(0,4).equals("view")) {
                    view_users(received);
                } else if (received.length()>=6 && received.substring(0,6).equals("delete")) {
                    delete_user(received);
                } else if (received.length()>=6 && received.substring(0,6).equals("update")) {
                    update_user(received);
                } else if (received.length()>=3 && received.substring(0,3).equals("add")) {
                    //add user
                    add_user(received);
                } else if (received.length()>=5 && received.substring(0,5).equals("login")) {
                    login(received);
                } else if (received.equals("help")) {
                    String n = ((UserDBE)((UserDBE)dbe).getUserbyId(connection,parseInt(name),"")).name;
                    if(((UserDBE)dbe).is_admin(connection,"Users",((UserDBE)((UserDBE)dbe).getUserbyId(connection,parseInt(name),"")).name,password)==1){
                        dos.writeUTF(admin_help);
                    }else{
                        dos.writeUTF(regular_help);
                    }
                } else if (received.length()>=12 && received.substring(0,11).equals("getuserinfo")) {
                    get_user_info(received);
                }
                //deleteUser
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users

            }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
