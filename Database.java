import javax.xml.crypto.Data;
import java.security.spec.ECField;
import java.sql.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static java.lang.Integer.parseInt;


// Diğer Database işlemleri burada
public class Database {
    public Connection connect_to_db(String dbname, String user, String pass){
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbname,user,pass);
        }catch (Exception e){
            System.out.println(e);
        }
        return conn;
    }

    public void start_database(Connection connection) throws ParseException {
        Database db = new Database();
        db.createTable(connection, "Users"); // (name, password) unique
        db.createTable(connection,"Messages");
        String str = "11-11-1996";
        SimpleDateFormat obj = new SimpleDateFormat("dd-MM-yyyy");
        long epoch = obj.parse(str).getTime();
        //Creating java.util.Date object
        java.sql.Date date = new java.sql.Date(epoch);
        DatabaseEntry user = new UserDBE("admin123","admin123","admin123","admin",date,"M","offline");

        if(((UserDBE)user).getCount(connection,"")==0){
            ((UserDBE)user).insert_row(connection);
        }
    }
    public void createTable(Connection conn, String table_name){
        Statement statement;
        String query;
        try{
            if(table_name == "Users"){
                query="create table IF NOT EXISTS "+table_name+"(uid SERIAL,name varchar(200),lastname varchar(200),password varchar(200), role varchar(200),birthdate date,gender char, available varchar(200), primary key(uid));";
            }
            else{
                query="create table  IF NOT EXISTS "+table_name+"(m_id SERIAL,fromU int,toU int, title varchar(200),content varchar(200), createdAt Timestamp, primary key(m_id), FOREIGN KEY (fromU) REFERENCES Users(uid), FOREIGN KEY (toU) REFERENCES Users(uid));";
            }
            statement=conn.createStatement();
            statement.executeUpdate(query);
            //System.out.println("Table Created");
        }catch (Exception e){
            System.out.println(e);
        }
    }}



