/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TablaBD.app.com;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 * La clase <code>Conexion</code> se encarga de gestionar las conexiones a bases de datos.
 * Soporta tanto MySQL como SQL Server, proporcionando métodos para abrir y cerrar conexiones.
 * 
 * @authors capistran y díaz
 * @version 1.0
 */
public class Conexion {
    
    // Atributos
    private String base; // Nombre de la base de datos a la que se conectará
    private String usuario; // Usuario para la autenticación en la base de datos
    private String password; // Contraseña para la autenticación en la base de datos
    private Connection conexion = null; // Objeto Connection que representa la conexión a la base de datos
    private final String CERTIFICADOS = "encrypt=true; trustServerCertificate=true; characterEncoding=UTF-8;"; // Configuración para la conexión a SQL Server

    /**
     * Constructor de la clase <code>Conexion</code>.
     * Inicializa los atributos necesarios para realizar la conexión a la base de datos.
     * 
     * @param base El nombre de la base de datos a la que se conectará.
     * @param usuario El usuario para acceder a la base de datos.
     * @param password La contraseña del usuario para acceder a la base de datos.
     */
    public Conexion(String base, String usuario, String password) {
        this.base = base;
        this.usuario = usuario;
        this.password = password;
    }

    /**
     * Establece una conexión con una base de datos MySQL.
     * 
     * Este método carga el controlador JDBC para MySQL y establece una conexión con la base de datos especificada.
     * Si la conexión es exitosa, se muestra un mensaje indicando que la conexión fue abierta exitosamente.
     * En caso de error, se captura y muestra una excepción.
     * 
     * @return Un objeto {@link Connection} que representa la conexión a la base de datos MySQL.
     */
    public Connection getConexionMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carga el controlador JDBC para MySQL
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+base, usuario, password); // Establece la conexión
            JOptionPane.showMessageDialog(null, "Conexión Abierta con MySQL Exitosamente", "Mensaje de Estado de Conexión",
                    JOptionPane.INFORMATION_MESSAGE); // Mensaje de éxito
        } catch (SQLException e) { // Captura errores de conexión SQL
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException ex) { // Captura errores al no encontrar el controlador JDBC
            ex.printStackTrace();
        }
        return conexion; // Devuelve el objeto Connection para realizar consultas y operaciones en la base de datos.
    }

    /**
     * Establece una conexión con una base de datos SQL Server.
     * 
     * Este método configura la URL de conexión y establece una conexión a la base de datos SQL Server.
     * Si la conexión es exitosa, se muestra un mensaje indicando que la conexión fue abierta exitosamente.
     * En caso de error, se captura y muestra una excepción.
     * 
     * @return Un objeto {@link Connection} que representa la conexión a la base de datos SQL Server.
     */
    public Connection getConexionSQLServer() {
        try {
            // Construcción de la URL de conexión con SQL Server
            String urlConnection = "jdbc:sqlserver://localhost:1433;databaseName=" + base + ";" +
                    "user=" + usuario + ";" + "password=" + password + ";" + CERTIFICADOS;
            conexion = DriverManager.getConnection(urlConnection); // Establece la conexión
            JOptionPane.showMessageDialog(null, "Conexión Abierta con SQL Server Exitosamente", "Mensaje de Estado de Conexión",
                    JOptionPane.INFORMATION_MESSAGE); // Mensaje de éxito
            return conexion;
        } catch (SQLException ex) { // Captura errores de conexión SQL
            System.err.println("Error de conexión: " + ex.getMessage());
            return conexion;
        }
    }

    /**
     * Cierra la conexión activa con la base de datos.
     * 
     * Este método cierra la conexión establecida a la base de datos. Si la conexión se cierra con éxito,
     * se muestra un mensaje indicando que la conexión fue cerrada correctamente. Si ocurre un error al cerrarla,
     * se captura y muestra un mensaje de error.
     */
    public void cerrarConexion() {
        try {
            conexion.close(); // Cierra la conexión
            JOptionPane.showMessageDialog(null, "Conexión Cerrada Exitosamente", "Mensaje de Estado de Conexión",
                    JOptionPane.INFORMATION_MESSAGE); // Mensaje de éxito
        } catch (SQLException e) { // Captura errores al cerrar la conexión
            JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getSQLState(), "Mensaje de Error",
                    JOptionPane.ERROR_MESSAGE); // Mensaje de error
        }
    }

}
