package DAO;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDao implements AutoCloseable{
Connection con = null;

    public Connection getConnection() throws IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            con =  DriverManager.getConnection("jdbc:mysql://192.168.1.105:3306/bdcinema?user=root&password=Andreluis87");
            if(con != null) 
                System.out.println("Conexao estabelecida");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return con;
        
    }

    @Override
    public void close() throws Exception {
        System.out.println("CDAO CLOSED");
        
    }
    
}
