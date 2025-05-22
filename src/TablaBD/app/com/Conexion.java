package TablaBD.app.com;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 * La clase <code>Conexion</code> se encarga de gestionar las conexiones a bases
 * de datos. Soporta tanto MySQL como SQL Server, proporcionando métodos para
 * abrir y cerrar conexiones.
 *
 * @authors capistran y díaz
 * @version 1.0
 */
public class Conexion {

    // Atributos
    private static String baseUsed; // Nombre de la base de datos a la que se conectará
    private static String usuarioUsed; // Usuario para la autenticación en la base de datos
    private static String passwordUsed; // Contraseña para la autenticación en la base de datos
    private static int idSGBD; // Identificador para reestablecer la bd seleccionada
    private static Connection conexion = null; // Objeto Connection que representa la conexión a la base de datos
    private static DatabaseMetaData metaDataDB;
    private static final String CERTIFICADOS = "encrypt=true; trustServerCertificate=true; characterEncoding=UTF-8;"; // Configuración para la conexión a SQL Server

    /**
     * Constructor de la clase <code>Conexion</code>. Inicializa los atributos
     * necesarios para realizar la conexión a la base de datos.
     *
     * @param base El nombre de la base de datos a la que se conectará.
     * @param usuario El usuario para acceder a la base de datos.
     * @param password La contraseña del usuario para acceder a la base de
     * datos.
     */
    public Conexion(String base, String usuario, String password) {
        Conexion.baseUsed = base;
        Conexion.usuarioUsed = usuario;
        Conexion.passwordUsed = password;
        metaDataDB = null;
    }

    /**
     * Constructor vacío de la clase Conexion.
     *
     * Se deja vacío para permitir la inicialización sin configuración
     * inmediata.
     */
    public Conexion() {
    }

    /**
     * Obtiene el nombre de la base de datos utilizada en la conexión.
     *
     * @return Nombre de la base de datos configurada.
     */
    public static String getBase() {
        return baseUsed;
    }

    /**
     * Obtiene el nombre de usuario utilizado para la conexión a la base de
     * datos.
     *
     * @return Nombre de usuario de la conexión.
     */
    public static String getUsuario() {
        return usuarioUsed;
    }

    /**
     * Obtiene la contraseña utilizada para la conexión a la base de datos.
     *
     * @return Contraseña utilizada en la conexión.
     */
    public static String getPassword() {
        return passwordUsed;
    }

    /**
     * Obtiene el identificador del sistema gestor de bases de datos (SGBD).
     *
     * @return Identificador del SGBD configurado.
     */
    public static int getIdSGBD() {
        return idSGBD;
    }

    /**
     * Establece el nombre de la base de datos utilizada en la conexión.
     *
     * @param baseUsed Nombre de la base de datos a utilizar.
     */
    public static void setBaseUsed(String baseUsed) {
        Conexion.baseUsed = baseUsed;
    }

    /**
     * Establece el nombre de usuario para la conexión a la base de datos.
     *
     * @param usuarioUsed Nombre de usuario a configurar.
     */
    public static void setUsuarioUsed(String usuarioUsed) {
        Conexion.usuarioUsed = usuarioUsed;
    }

    /**
     * Establece la contraseña para la conexión a la base de datos.
     *
     * @param passwordUsed Contraseña a configurar.
     */
    public static void setPasswordUsed(String passwordUsed) {
        Conexion.passwordUsed = passwordUsed;
    }

    /**
     * Establece el identificador del sistema gestor de bases de datos (SGBD).
     *
     * @param idSGBD Identificador del SGBD a configurar.
     */
    public static void setIdSGBD(int idSGBD) {
        Conexion.idSGBD = idSGBD;
    }

    /**
     * Establece una conexión con una base de datos MySQL.
     *
     * Este método carga el controlador JDBC para MySQL y establece una conexión
     * con la base de datos especificada. Si la conexión es exitosa, se muestra
     * un mensaje indicando que la conexión fue abierta exitosamente. En caso de
     * error, se captura y muestra una excepción.
     *
     * @return Un objeto {@link Connection} que representa la conexión a la base
     * de datos MySQL.
     */
    public static Connection getConexionMySQL() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carga el controlador JDBC para MySQL
            if(baseUsed != null)
                conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+baseUsed, usuarioUsed, passwordUsed); // Establece la conexión
            else
               conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/?", usuarioUsed, passwordUsed); // Establece la conexión
            JOptionPane.showMessageDialog(null, "Conexión Abierta con MySQL Exitosamente", "Mensaje de Estado de Conexión",
                    JOptionPane.INFORMATION_MESSAGE); // Mensaje de éxito
        } catch (SQLException | ClassNotFoundException e) { // Captura errores de conexión SQL
            System.err.println(e.getMessage());
        }
        // Captura errores al no encontrar el controlador JDBC
        return conexion; // Devuelve el objeto Connection para realizar consultas y operaciones en la base de datos.
    }

    /**
     * Establece una conexión con una base de datos SQL Server.
     *
     * Este método configura la URL de conexión y establece una conexión a la
     * base de datos SQL Server. Si la conexión es exitosa, se muestra un
     * mensaje indicando que la conexión fue abierta exitosamente. En caso de
     * error, se captura y muestra una excepción.
     *
     * @return Un objeto {@link Connection} que representa la conexión a la base
     * de datos SQL Server.
     */
    public static Connection getConexionSQLServer() {
        try {
            // Construcción de la URL de conexión con SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // Carga el controlador JDBC para SQL Server
            if(baseUsed != null)
                Conexion.conexion = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;"+"database="+ baseUsed +";"
                        +"user=" + usuarioUsed + ";" + "password=" + passwordUsed + ";" + CERTIFICADOS); // Establece la conexión
            else
                Conexion.conexion = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;"
                        +"user=" + usuarioUsed + ";" + "password=" + passwordUsed + ";" + CERTIFICADOS); // Establece la conexión
            JOptionPane.showMessageDialog(null, "Conexión Abierta con SQL Server Exitosamente", "Mensaje de Estado de Conexión",
                    JOptionPane.INFORMATION_MESSAGE); // Mensaje de éxito
        } catch (SQLException | ClassNotFoundException e) { // Captura errores de conexión SQL
            System.err.println(e.getMessage());
        }
        return conexion; // Devuelve el objeto Connection para realizar consultas y operaciones en la base de datos.
    }

    /**
     * Obtiene una conexión a la base de datos según el identificador del SGBD
     * proporcionado.
     *
     * @param idSGBD Identificador del sistema gestor de bases de datos. - `1`:
     * SQL Server - `2`: SQL Server (parece duplicado, verifica si deberías
     * manejar otro SGBD)
     * @return La conexión establecida con el sistema gestor de bases de datos
     * especificado, o `null` si el identificador no es válido.
     */
    public static Connection getConexion(int idSGBD) {
        if (idSGBD == 1) {
            return Conexion.getConexionSQLServer();
        }
        if (idSGBD == 2) {
            return Conexion.getConexionMySQL();
        }
        return null;
    }

    /**
     * Obtiene la conexión actual establecida.
     *
     * @return La conexión activa con la base de datos.
     */

    /**
     * Cierra la conexión activa con la base de datos.
     *
     * Este método cierra la conexión establecida a la base de datos. Si la
     * conexión se cierra con éxito, se muestra un mensaje indicando que la
     * conexión fue cerrada correctamente. Si ocurre un error al cerrarla, se
     * captura y muestra un mensaje de error.
     */
    public static void cerrarConexion() {
        try {
            conexion.close(); // Cierra la conexión
            JOptionPane.showMessageDialog(null, "Conexión Cerrada Exitosamente", "Mensaje de Estado de Conexión",
                    JOptionPane.INFORMATION_MESSAGE); // Mensaje de éxito
        } catch (SQLException e) { // Captura errores al cerrar la conexión
            JOptionPane.showMessageDialog(null, "Ocurrio un error: " + e.getMessage(), "Mensaje de Error",
                    JOptionPane.ERROR_MESSAGE); // Mensaje de error
        }
    }

    /**
     * Estado de la conexión Este método trata de obtener el estado de la
     * conexión mediante el método isClosed() el cual retorna true si está
     * cerrada la conexión y false si está abierta. En caso de que la conexión
     * mande una posible excepción se establece un try-catch que indica el
     * mensaje de error
     *
     * @return Un booleano {
     * @Boolean} el cual indica si la conexión se encuentra abierta o cerrada
     */
    public static boolean getStateConnection() {
        try {
            if (Conexion.conexion != null && !Conexion.conexion.isClosed()) {
                return true; // Estado de la conexión abierto
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar el estado de la conexión", "Error" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
        return false; // Estado de la conexión cerrado
    }

    /**
     * Obtiene el conjunto de tablas definidas por el usuario en la base de
     * datos actual a través de los metadatos de la conexión.
     * <p>
     * Este método utiliza `DatabaseMetaData.getTables()` para consultar todas
     * las tablas del esquema `"dbo"` en la base de datos especificada por
     * `baseUsed`, siempre que la conexión esté activa.
     * </p>
     *
     * @return Un {@link ResultSet} que contiene información sobre las tablas
     * encontradas (nombre, tipo, esquema, etc.), o {@code null} si no hay
     * conexión activa o ocurre un error durante la consulta.
     */
    public ResultSet getTablesMetaData() {
        ResultSet tables = null;

        // Verifica que la conexión esté activa antes de continuar
        if (getStateConnection()) {
            try {
                // Obtiene los metadatos de la base de datos desde la conexión actual
                metaDataDB = conexion.getMetaData();

                // Recupera las tablas del esquema "dbo" de la base de datos especificada
                tables = metaDataDB.getTables(baseUsed, "dbo", "%", new String[]{"TABLE"});

            } catch (SQLException e) {
                // Muestra un mensaje de error si la consulta falla
                JOptionPane.showMessageDialog(null,
                        "Error al consultar las tablas de la BD",
                        "Error" + e.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return tables;
    }

}
