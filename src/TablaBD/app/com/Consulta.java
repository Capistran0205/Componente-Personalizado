/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TablaBD.app.com;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * La clase {@code Consulta} proporciona una manera de realizar consultas SQL a una base de datos
 * (MySQL o SQL Server), y obtener los resultados en un formato que se puede procesar más fácilmente.
 * Esta clase contiene un método estático para ejecutar consultas SELECT sobre cualquier tabla de la base de datos.
 */
public class Consulta {
    
    /**
     * Atributo estático para almacenar la consulta SQL a ejecutar.
     * Se establece directamente sin necesidad de instanciar la clase.
     */
    private static String query; 

    /**
     * Ejecuta una consulta SQL para obtener todos los registros de una tabla específica.
     * 
     * Este método crea una consulta SQL dinámica utilizando el nombre de la tabla proporcionado
     * y la ejecuta en la base de datos conectada a través del objeto {@code connection}.
     * 
     * @param connection El objeto {@link Connection} que maneja la conexión a la base de datos.
     * @param tableName El nombre de la tabla de la cual se quiere obtener los datos.
     * @return Un objeto {@link ResultSet} que contiene los resultados de la consulta SQL.
     * @throws SQLException Si ocurre un error durante la ejecución de la consulta.
     */
    public static ResultSet executeQuery(Connection connection, String tableName) throws SQLException {
        // Construcción de la consulta SQL
        query = "SELECT * FROM " + tableName;
        
        // Preparar la consulta
        PreparedStatement pstmt = connection.prepareStatement(query);
        
        // Ejecutar la consulta y retornar el resultado
        return pstmt.executeQuery();
    }
}