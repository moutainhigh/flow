package com.daoeee.test.xmlrcp;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

  
public class DBTools {  
  
    private java.sql.Connection connection;  
    private java.sql.PreparedStatement preparedStatement;  
  
    public DBTools() {  
        try {  
            connection = DriverManager.getConnection(  
                    "jdbc:mysql://localhost:3306/taskflow_test", "root", "123456");  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }  
  
    static {  
        try {  
            Class.forName("com.mysql.jdbc.Driver");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public void close() {  
        if (connection != null) {  
            try {  
                connection.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
    public ResultSet executeQuery(String sql, LinkedList<Object> params) {  
        try {  
            preparedStatement = connection.prepareStatement(sql);  
            if (params != null) {  
                int i = 1;  
                for (Object p : params) {  
                    preparedStatement.setObject(i++, p);  
                }  
            }  
            return preparedStatement.executeQuery();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return null;  
    } 
    
    public int executeUpdate(String sql, LinkedList<Object> params) {  
        try {  
            preparedStatement = connection.prepareStatement(sql);  
            if (params != null) {  
                int i = 1;  
                for (Object p : params) {  
                    preparedStatement.setObject(i++, p);  
                }  
            }  
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return 0;  
    }
    
    

    
    public int executeUpdate(String sql) {  
        try {  
            Statement statement = connection.createStatement();  
            int result = statement.executeUpdate(sql);
            System.out.println("sql=" + sql);
            System.out.println("result=" + result);
            return result;
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        return 0;  
    }
    
    
    public static void main2(String[] args) {  
        LinkedList<Object> params = new LinkedList<Object>();  
        params.add("5");  
        DBTools tool = new DBTools();  
        ResultSet rs = tool.executeQuery("SELECT * FROM RPT_TASKFLOW_TEMPLATE where ID_TASKFLOW= ? order by ID_TASKFLOW limit 100", params);  
        try {
        	   int cloumnCount = rs.getMetaData().getColumnCount();
     
            while (rs.next()) {
            		for (int i = 1; i<= cloumnCount; i++) {
                		System.out.print(rs.getString(i)+ " | ") ;
            		}
            		System.out.println();
            }
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
        tool.close();  
    } 
    
    public static void main(String[] args) {  
        DBTools tool = new DBTools();
        tool.executeUpdate("DELETE FROM RPT_LINK WHERE ID_LINK IN (51)");  
        tool.executeUpdate("DELETE FROM RPT_TASK_ATTRIBUTE WHERE ID_TASK IN (8, 9)");
        tool.executeUpdate("DELETE FROM RPT_TASK_TEMPLATE WHERE ID_TASK IN (8, 9)");
        tool.executeUpdate("DELETE FROM RPT_TASK WHERE ID_TASK IN (8, 9)");
        tool.executeUpdate("DELETE FROM RPT_TASKFLOW_TEMPLATE WHERE ID_TASKFLOW = 5");
        tool.executeUpdate("DELETE FROM RPT_TASKFLOW WHERE ID_TASKFLOW = 5");
        tool.close();  
    }  
}  
