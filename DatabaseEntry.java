import javax.xml.crypto.Data;
import java.sql.*;

public abstract class DatabaseEntry {
    String table_name;

    public abstract void insert_row(Connection conn);
    public abstract void delete_row_by_id(Connection conn,int id);


}

class UserDBE extends DatabaseEntry{
    int uid;
    String name;
    String lastname;
    String password;
    String role;
    Date birthdate;
    String gender;
    String available;

    public UserDBE(String n, String l, String p,String r, Date b, String g, String a){//users database işlemleri
        uid = 0;
        name = n;
        lastname = l;
        password = p;
        role = r;
        birthdate = b;
        gender = g;
        table_name = "Users";
        available = a;
    }

    public UserDBE() {

    }
    public int is_admin(Connection conn, String table_name,String name, String password){
        Statement statement;
        ResultSet rs=null;
        int result = 0;
        try {
            String query=String.format("select * from %s where name= '%s' and password= '%s'",table_name,name,password);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);

            while (rs.next()) {
                if(rs.getString("role").equals("admin")){
                    result = 1;
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
    public boolean user_exists(Connection conn,String name, String password){
        Statement statement;
        ResultSet rs=null;
        boolean result = false;
        try {
            String query=String.format("select * from Users where name= '%s' and password= '%s'",name,password);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            if(rs.next()){
                result = true;
            }
            while (rs.next()) {
                System.out.println("                        Welcome "+rs.getString("name")+" "+rs.getString("lastname") + "\n");

            }
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
    public int getCount(Connection conn, String available){
        Statement statement;
        ResultSet rs = null;
        int count = 0;
        try{
            String query="";
            if(available.equals("online")){
                query=String.format("SELECT COUNT(*) as c from Users where available=%s;",available);
            }
            else{
                query=String.format("SELECT COUNT(*) as c from Users;");
            }
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            rs.next();
            count = rs.getInt("c");
            rs.close();
        }catch (Exception e){
            System.out.println(e);
        }
        return count;
    }
    public DatabaseEntry getCurrentUser(Connection conn, String name, String password){
        Statement statement;
        ResultSet rs=null;
        String last="", role="", gender=""  ;
        Date birthdate = null;
        try {
            String query=String.format("select * from Users where name= '%s' and password= '%s'",name,password);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while (rs.next()) {
                last = rs.getString("lastname");
           //     System.out.println("inWhilelast"+last);
                password = rs.getString("password");
                role = rs.getString("role");
                birthdate = rs.getDate("birthdate");
                gender = rs.getString("gender");
                System.out.println("                        Welcome "+rs.getString("name")+" "+rs.getString("lastname") + "\n");

            }
        }catch (Exception e){
            System.out.println(e);
        }
        //System.out.println("notin"+last);
        return new UserDBE(name,last,password,role,birthdate,gender,"online");
    }
    public DatabaseEntry getUserbyId(Connection conn,int id, String available){
        Statement statement;
        ResultSet rs=null;
        boolean not_exist = true;
        String last="", role="", gender="", a="", n="", p=""  ;
        Date birthdate = null;
        try {
            String query = "";
            if(available.equals("online")){
                query=String.format("select * from Users where uid = %d and available='%s'",id,available);
            }
            else{
                query=String.format("select * from Users where uid = %d",id);
            }
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while (rs.next()) {
                not_exist = false;
                n = rs.getString("name");
                last = rs.getString("lastname");
           //     System.out.println("inWhilelast"+last);
                p = rs.getString("password");
                role = rs.getString("role");
                birthdate = rs.getDate("birthdate");
                gender = rs.getString("gender");
                a = rs.getString("available");
            }
        }catch (Exception e){
            System.out.println(e);
        }
        //System.out.println("notin"+last);
        if(not_exist){
            System.out.println("USER NOT FOUND for "+id);
            return null;
        }
        return new UserDBE(n,last,p,role,birthdate,gender, a );
    }
    public void insert_row(Connection conn){ // function overloading //// (User user) şeklinde geç
        Statement statement;
        ResultSet rs=null;

        try {
            String query=String.format("insert into " +
                    "%s(name,lastname,password,role,birthdate,gender,available) " +
                    "values('%s','%s','%s','%s','%s','%s','%s');",table_name,name,lastname,password,role,birthdate,gender,available);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row Inserted");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void delete_row_by_id(Connection conn, int id){
        Statement statement;
        if( getUserbyId(conn,id,"") == null){
            System.out.println("NOT VALID, back to menu");
            return;
        }
        try{
            String query=String.format("delete from Users where uid= %d",id);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public int getId(Connection conn,String name, String password){
        Statement statement;
        ResultSet rs=null;
        int result = 0;
        try {
            String query=String.format("select * from %s where name= '%s' and password= '%s'","Users",name,password);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);

            while (rs.next()) {
                result = rs.getInt("uid");
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
    public String getUsers(Connection conn){
        Statement statement;
        ResultSet rs=null;
        String result="";
        try {
            String query=String.format("select * from %s;","Users");
            statement=conn.createStatement();
            rs=statement.executeQuery(query);

            while (rs.next()) {
                result+=rs.getString("uid")+" "+rs.getString("name")+" "+rs.getString("lastname") + "\n";
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
    public String getOnlineUsers(Connection conn){
        Statement statement;
        ResultSet rs=null;
        String result="";
        try {
            String query=String.format("select * from Users where available = '%s';","online");
            statement=conn.createStatement();
            rs=statement.executeQuery(query);

            while (rs.next()) {
                result+=rs.getString("uid")+" "+rs.getString("name")+" "+rs.getString("lastname")+"\n";
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
    public void update_name(Connection conn,int id,String new_name){
        Statement statement;
        if( getUserbyId(conn,id,"") == null){
            System.out.println("NOT VALID, back to menu");
            return;
        }
        try {
            String query=String.format("update Users set name='%s' where uid=%d",new_name, id);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void update_lastname(Connection conn,int id,String new_lastname){
        Statement statement;
        if( getUserbyId(conn,id,"") == null){
            System.out.println("NOT VALID, back to menu");
            return;
        }        try {
            String query=String.format("update Users set lastname='%s' where uid=%d",new_lastname, id);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void update_role(Connection conn,int id,String new_role){
        Statement statement;
        if( getUserbyId(conn,id,"") == null){
            System.out.println("NOT VALID, back to menu");
            return;
        }        try {
            String query=String.format("update Users set role='%s' where uid=%d",new_role, id);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void update_available(Connection conn,int id,String available){
        Statement statement;
        if( getUserbyId(conn,id,"") == null){
            System.out.println("NOT VALID, back to menu");
            return;
        }        try {
            String query=String.format("update Users set available='%s' where uid=%d",available, id);
            statement=conn.createStatement();
            statement.executeUpdate(query);
           // System.out.println("Data Updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
}

class MessageDBE extends DatabaseEntry{//message database işlemleri
    //User from_user;
    //User to_user;
    int from;
    int to;
    String title;
    String content;

    public MessageDBE(int f,int t,String ttl,String c){
      //  from_user = ufrom;
      //  to_user = uto;
        from = f;
        to = t;
        title = ttl;
        content = c;
        table_name = "Messages";
    }

    public MessageDBE() {

    }
    public String get_last_ten(Connection conn, int id){
        Statement statement;
        ResultSet rs=null;
        DatabaseEntry dbe = new UserDBE();
        String result = "";
        try {
            String query=String.format("select * from Messages where fromu= %d or tou = %d order by createdat desc limit 10",id,id);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            result +="From        To        title          content          createdat\n";
            System.out.println("From        To        title          content          createdat\n");
            while (rs.next()){
                DatabaseEntry fromUser = ((UserDBE) dbe).getUserbyId(conn,rs.getInt("fromu"),"");
                DatabaseEntry toUser = ((UserDBE) dbe).getUserbyId(conn,rs.getInt("tou"),"");
                if(toUser==null && fromUser!=null){
                    result += ((UserDBE)fromUser).name + " " + ((UserDBE)fromUser).lastname + " "+
                            "kullanıcı yok" + " "+
                            rs.getString("title") + " " +
                            rs.getString("content") + " " +
                            rs.getString("createdat") + "\n";
                }
                else if(toUser!=null && fromUser==null){
                    result += "kullanıcı yok" + ((UserDBE)toUser).name + " " +
                            ((UserDBE)toUser).lastname + " "+
                            " "+
                            rs.getString("title") + " " +
                            rs.getString("content") + " " +
                            rs.getString("createdat") + "\n";
                }
                else{
                    result += ((UserDBE)fromUser).name + " " + ((UserDBE)fromUser).lastname + " "+
                            ((UserDBE)toUser).name + " " + ((UserDBE)toUser).lastname + " "+
                            rs.getString("title") + " " +
                            rs.getString("content") + " " +
                            rs.getString("createdat") + "\n";
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
    public String get_last_ten_with_specified(Connection conn, int id, int id2){
        Statement statement;
        ResultSet rs=null;
        DatabaseEntry dbe = new UserDBE();
        String result = "";
        if( ((UserDBE)dbe).getUserbyId(conn,id2,"") == null){
            System.out.println("NOT VALID, back to menu");
            return "NOT OK";
        }
        try {
            String query=String.format("select * from Messages where (fromu= %d and tou = %d) or (tou = %d and fromu = %d) order by createdat desc limit 10",id,id2,id,id2);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            result += "From        To        title          content          createdat\n";
            //System.out.println("From        To        title          content          createdat\n");
            while (rs.next()){
                DatabaseEntry fromUser = ((UserDBE) dbe).getUserbyId(conn,rs.getInt("fromu"),"");
                DatabaseEntry toUser = ((UserDBE) dbe).getUserbyId(conn,rs.getInt("tou"),"");
                if(toUser==null){
                    result += ((UserDBE)fromUser).name + " " + ((UserDBE)fromUser).lastname + " "+
                            "kullanıcı yok" + " "+
                            rs.getString("title") + " " +
                            rs.getString("content") + " " +
                            rs.getString("createdat") + "\n";
                }else{
                    result += ((UserDBE)fromUser).name + " " + ((UserDBE)fromUser).lastname + " "+
                            ((UserDBE)toUser).name + " " + ((UserDBE)toUser).lastname + " "+
                            rs.getString("title") + " " +
                            rs.getString("content") + " " +
                            rs.getString("createdat") + "\n";
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return result;
    }
    public void insert_row(Connection conn){ // function overloading yap
        Statement statement;
        //java.sql.Date createdAt = new java.sql.Date(java.util.Calendar.getInstance().getTime().getTime());
        long now = System.currentTimeMillis();
        Timestamp sqlTimestamp = new Timestamp(now);
        try {
            String query=String.format("insert into " +
                    "%s(fromu,tou,title,content,createdAt) " +
                    "values('%s','%s','%s','%s','%s');",table_name,from,to,title,content, sqlTimestamp);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Row Inserted");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void delete_row_by_id(Connection conn,int id){
        Statement statement;
        try{
            String query=String.format("delete from %s where uid= %s",table_name,id);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

}

