package TablaBD.app.com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * La clase {@code Consulta} proporciona una manera de realizar consultas SQL a
 * una base de datos (MySQL o SQL Server), y obtener los resultados en un
 * formato que se puede procesar más fácilmente. Esta clase contiene un método
 * estático para ejecutar consultas SELECT sobre cualquier tabla de la base de
 * datos.
 */
public class Consulta {

    /**
     * Atributo estático para almacenar la consulta SQL a ejecutar. Se establece
     * directamente sin necesidad de instanciar la clase.
     */
    private static String queryRead;
    private static StringBuilder queryWrite;
    private static DatabaseMetaData metaDataDB;
    private static ResultSet tableMetaData;
    private static String tableSelect;
    private static ArrayList<String> columnsDescrip;
    private static ArrayList<String> columnsId;
    private static ArrayList<Integer> columnsType;
    private static ArrayList<String> dataBases;
    private static int countColumns;
    private static int queryWtriteType;

    /**
     * Inserta dinámicamente un registro en la tabla especificada utilizando una
     * conexión a la base de datos.
     *
     * <p>
     * Este método construye y ejecuta una consulta de tipo INSERT INTO
     * parametrizada para evitar inyecciones SQL. Los tipos de datos de las
     * columnas son determinados previamente por el método
     * {@code setCommonColumnsTable} y se utilizan para asignar los valores
     * adecuados al {@link PreparedStatement}.</p>
     *
     * <p>
     * Si la inserción es exitosa, se muestra un mensaje de confirmación.</p>
     *
     * @param connection Conexión activa a la base de datos.
     * @param tableName Nombre de la tabla en la cual se insertará el registro.
     * @param base Nombre de la base de datos a la que pertenece la tabla.
     * @param infoRegis Lista de valores que se insertarán, en el orden
     * correspondiente a las columnas.
     */
    public static void insert(Connection connection, String tableName, String base, ArrayList<Object> infoRegis) {
        setCommonColumnsTable(connection, tableName, base);
        try {
            // Se construye la consulta SQL
            String query = queryInsert(tableName);
            PreparedStatement pstm = connection.prepareStatement(query);

            // Asignación de parámetros según su tipo
            for (int i = 0; i < infoRegis.size(); i++) {
                int type = columnsType.get(i);
                Object valor = infoRegis.get(i);
                switch (type) {
                    case Types.VARCHAR, Types.CHAR ->
                        pstm.setString(i + 1, valor.toString());
                    case Types.INTEGER ->
                        pstm.setInt(i + 1, (Integer) valor);
                    case Types.DOUBLE, Types.DECIMAL ->
                        pstm.setDouble(i + 1, (Double) valor);
                    case Types.DATE -> {
                        LocalDate localDate = (LocalDate) valor;
                        pstm.setDate(i + 1, java.sql.Date.valueOf(localDate));
                    }
                    default ->
                        pstm.setObject(i + 1, valor); // Fallback genérico
                }
            }

            // Ejecución de la consulta
            if (pstm.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Registro concluido con éxito",
                        "Consulta de Escritura: Insert", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error en el registro: " + e.getMessage(),
                    "Error al Registrar", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Genera dinámicamente una consulta SQL de tipo INSERT INTO para una tabla
     * específica.
     *
     * <p>
     * La consulta se construye utilizando las columnas descritas previamente en
     * la lista {@code columnsDescrip}. Se agregan los valores correspondientes
     * como signos de interrogación (?) para permitir la parametrización
     * mediante {@link PreparedStatement}.</p>
     *
     * @param tableName Nombre de la tabla para la cual se construirá la
     * consulta.
     * @return Cadena SQL con la instrucción INSERT INTO preparada para su
     * ejecución.
     */
    public static String queryInsert(String tableName) {
        queryWrite = new StringBuilder();
        queryWrite.append("INSERT INTO ").append(tableName).append("(");

        // Agregar nombres de columnas
        for (int j = 0; j <= countColumns - 1; j++) {
            queryWrite.append(columnsDescrip.get(j));
            if (j < countColumns - 1) {
                queryWrite.append(", ");
            }
        }

        // Agregar placeholders para los valores
        queryWrite.append(") VALUES (");
        for (int i = 0; i <= countColumns - 1; i++) {
            queryWrite.append("?");
            if (i < countColumns - 1) {
                queryWrite.append(", ");
            }
        }
        queryWrite.append(")");

        return queryWrite.toString();
    }

    /**
     * Realiza una operación de actualización (UPDATE) en la base de datos.
     *
     * <p>
     * Este método construye dinámicamente una consulta SQL para actualizar
     * registros en la tabla especificada, utilizando los valores
     * proporcionados. La consulta se genera en base al nombre de la tabla y la
     * información almacenada sobre las columnas afectadas y sus tipos de
     * datos.</p>
     *
     * @param connection Objeto {@link Connection} activo hacia la base de
     * datos.
     * @param tableName Nombre de la tabla en la que se desea realizar la
     * actualización.
     * @param base Nombre de la base de datos a la que pertenece la tabla.
     * @param infoRegisAndCond Lista de objetos que contiene los nuevos valores
     * a actualizar, seguido por el valor de la condición (e.g., ID).
     */
    public static void update(Connection connection, String tableName, String base, ArrayList<Object> infoRegisAndCond) {
        // Se asignan las claves primarias
        setPrimaryColumnsTable(connection, tableName, base);
        try {
            // Se construye la consulta UPDATE dinámica
            String query = queryUpdate(tableName);
            PreparedStatement pstm = connection.prepareStatement(query);

            for (int i = 0; i < infoRegisAndCond.size(); i++) {
                int type = columnsType.get(i);
                Object valor = infoRegisAndCond.get(i);
                // Asigna el valor según el tipo SQL correspondiente
                switch (type) {
                    case Types.VARCHAR, Types.CHAR ->
                        pstm.setString(i + 1, valor.toString());
                    case Types.INTEGER ->
                        pstm.setInt(i + 1, (Integer) valor);
                    case Types.DOUBLE, Types.DECIMAL ->
                        pstm.setDouble(i + 1, (Double) valor);
                    case Types.DATE -> {
                        LocalDate localDate = (LocalDate) valor;
                        pstm.setDate(i + 1, java.sql.Date.valueOf(localDate));
                    }
                    default ->
                        pstm.setObject(i + 1, valor); // Respaldo genérico
                }
            }

            // Ejecuta la consulta
            if (pstm.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Actualización concluida con éxito", "Consulta de Escritura: Update", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar actualizar: " + e.getMessage(), "Error al Actualizar", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Genera dinámicamente la sentencia SQL de actualización (UPDATE) para una
     * tabla específica.
     *
     * <p>
     * La estructura de la sentencia incluye la cláusula <code>SET</code> para
     * asignar nuevos valores y una condición <code>WHERE</code> basada en la
     * clave primaria.</p>
     *
     * @param tableName Nombre de la tabla sobre la cual se generará la
     * sentencia UPDATE.
     * @return Cadena de texto que representa la sentencia SQL UPDATE
     * construida.
     */
    public static String queryUpdate(String tableName) {
        queryWrite = new StringBuilder();
        queryWrite.append("UPDATE ").append(tableName).append(" SET ");

        for (int j = 0; j < countColumns; j++) {
            queryWrite.append(columnsDescrip.get(j)).append(" = ?");
            if (j < countColumns - 1) {
                queryWrite.append(", ");
            }
        }

        // Condición WHERE usando clave primaria
        if (!columnsId.isEmpty()) {
            queryWrite.append(" WHERE ").append(columnsId.get(0)).append(" = ?");
        }

        return queryWrite.toString();
    }

    /**
     * Genera dinámicamente una sentencia SQL de inserción (INSERT INTO) para
     * una tabla específica.
     *
     * <p>
     * Construye la parte de columnas y los correspondientes signos de
     * interrogación para los valores que serán introducidos en la base de
     * datos.</p>
     *
     * @param tableName Nombre de la tabla destino de la inserción.
     * @return Cadena de texto que representa la sentencia SQL INSERT
     * construida.
     */
    public static String queryDelete(String tableName) {
        queryWrite = new StringBuilder();
        queryWrite.append("INSERT INTO ").append(tableName).append(" (");

        for (int j = 0; j < countColumns; j++) {
            queryWrite.append(columnsDescrip.get(j));
            if (j < countColumns - 1) {
                queryWrite.append(", ");
            }
        }

        queryWrite.append(") VALUES (");

        for (int i = 0; i < countColumns; i++) {
            queryWrite.append("?");
            if (i < countColumns - 1) {
                queryWrite.append(", ");
            }
        }

        queryWrite.append(")");
        return queryWrite.toString();
    }

    /**
     * Obtiene los metadatos de una tabla en la base de datos y almacena las
     * descripciones y tipos de datos de sus columnas, excluyendo aquellas que
     * sean autoincrementables.
     *
     * @param connection La conexión activa con la base de datos.
     * @param tableName El nombre de la tabla de la cual se desean obtener las
     * columnas.
     * @param base El nombre de la base de datos donde se encuentra la tabla.
     */
    public static void setCommonColumnsTable(Connection connection, String tableName, String base) {

        try {
            // Obtención de los metadatos de la conexión con la base de datos
            Consulta.metaDataDB = connection.getMetaData();

            // Se recuperan los metadatos de las columnas de la tabla especificada
            Consulta.tableMetaData = metaDataDB.getColumns(base, "dbo", tableName, "%");

            // Inicialización de las listas dinámicas para almacenar la información de las columnas
            Consulta.columnsDescrip = new ArrayList<>();
            Consulta.columnsType = new ArrayList<>();

            // Inicialización del contador de columnas
            Consulta.countColumns = 0;

            // Iteración sobre los metadatos de las columnas
            while (tableMetaData.next()) {
                // Obtención de la propiedad de autoincremento de la columna
                String isAutoInc = tableMetaData.getString("IS_AUTOINCREMENT");
                // Obtención del nombre al valor asociado de la columna
                String typeColumn = tableMetaData.getString("TYPE_NAME");

                // Filtra aquellas columnas que no sean autoincrementables o de encriptación 
                if (!"YES".equals(isAutoInc) && !(typeColumn.equalsIgnoreCase("BLOB") || typeColumn.equalsIgnoreCase("VARBINARY"))) {
                    // Agrega el nombre de la columna a la lista correspondiente
                    Consulta.columnsDescrip.add(tableMetaData.getString("COLUMN_NAME"));

                    // Agrega el tipo de dato de la columna a la lista correspondiente
                    Consulta.columnsType.add(tableMetaData.getInt("DATA_TYPE"));

                    // Incrementa el contador de columnas
                    Consulta.countColumns++;
                }
            }
        } catch (SQLException ex) {
            // Manejo de errores: muestra un mensaje en caso de fallo al obtener las columnas de la tabla
            JOptionPane.showMessageDialog(null, "Ocurrió un error al obtener las columnas de la tabla: "
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conexion.cerrarConexion();
        }
    }

    /**
     * Establece las columnas que actúan como claves primarias (Primary Keys) en
     * una tabla específica de una base de datos.
     *
     * <p>
     * Este método accede a los metadatos de la base de datos a través de la
     * conexión provista y extrae información sobre las columnas que son claves
     * primarias. Además, inicializa estructuras necesarias para el
     * procesamiento posterior de datos de la tabla, como el tipo de dato de las
     * columnas y su conteo.</p>
     *
     * <p>
     * Al finalizar, también invoca
     * {@link #setCommonColumnsTable(Connection, String, String)} para
     * complementar la estructura con columnas comunes.</p>
     *
     * @param connection Conexión activa a la base de datos.
     * @param tableName Nombre de la tabla desde la cual se obtendrán las claves
     * primarias.
     * @param base Nombre de la base de datos a la que pertenece la tabla.
     */
    public static void setPrimaryColumnsTable(Connection connection, String tableName, String base) {
        try {
            // Obtención de metadatos del esquema de la base de datos
            Consulta.metaDataDB = connection.getMetaData();

            // Obtiene los metadatos relacionados con las claves primarias de la tabla
            Consulta.tableMetaData = metaDataDB.getPrimaryKeys(base, "dbo", tableName);

            // Inicialización de listas y contador
            Consulta.columnsId = new ArrayList<>();
            Consulta.columnsType = new ArrayList<>();
            Consulta.countColumns = 0;

            // Iteración sobre los resultados para almacenar los nombres y tipos de columnas clave primaria
            while (tableMetaData.next()) {
                columnsId.add(tableMetaData.getString("COLUMN_NAME"));
                columnsType.add(tableMetaData.getInt("DATA_TYPE"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error al obtener las columnas de la tabla: "
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Conexion.cerrarConexion();
        }

        // Agrega columnas comunes para completar la descripción de la tabla
        setCommonColumnsTable(connection, tableName, base);
    }

    /**
     * Recupera la lista de nombres de todas las bases de datos disponibles
     * según el motor de base de datos activo.
     *
     * <p>
     * Soporta SQL Server y MySQL. Si el motor de base de datos no es compatible
     * o no hay conexión activa, se muestra un mensaje de error.</p>
     *
     * @param connection Conexión activa a la base de datos.
     * @return Lista de nombres de bases de datos disponibles. Devuelve una
     * lista vacía si ocurre un error o no hay conexión.
     */
    public static ArrayList<String> getDataBases(Connection connection) {
        dataBases = new ArrayList<>();
        String consulta = "";

        // Define la consulta de acuerdo al tipo de SGBD
        switch (Conexion.getIdSGBD()) {
            case 1 -> // SQL Server
                consulta = "SELECT name FROM sys.databases";
            case 2 -> // MySQL
                consulta = "SHOW DATABASES";
            default -> {
                JOptionPane.showMessageDialog(null, "SGBD no soportado", "Error", JOptionPane.ERROR_MESSAGE);
                return dataBases;
            }
        }

        // Verifica que la conexión esté activa antes de ejecutar la consulta
        if (Conexion.getStateConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(consulta); ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    dataBases.add(rs.getString(1));
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al obtener las bases de datos: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                Conexion.cerrarConexion();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No hay conexión activa", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return dataBases;
    }

    /**
     * Ejecuta una consulta SQL para obtener todos los registros de una tabla
     * específica.
     *
     * Este método crea una consulta SQL dinámica utilizando el nombre de la
     * tabla proporcionado y la ejecuta en la base de datos conectada a través
     * del objeto {@code connection}.
     *
     * @param connection El objeto {@link Connection} que maneja la conexión a
     * la base de datos.
     * @param tableName El nombre de la tabla de la cual se quiere obtener los
     * datos.
     * @return Un objeto {@link ResultSet} que contiene los resultados de la
     * consulta SQL.
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * consulta.
     */
    public static ResultSet executeQuery(Connection connection, String tableName) throws SQLException {
        // Construcción de la consulta SQL
        queryRead = "SELECT * FROM " + tableName;

        // Preparar la consulta
        PreparedStatement pstmt = connection.prepareStatement(queryRead);

        // Ejecutar la consulta y retornar el ResultSet
        return pstmt.executeQuery();
    }

    /**
     * Ejecuta una consulta SQL completa y devuelve el resultado como un
     * ResultSet.
     *
     * @param connection La conexión activa con la base de datos.
     * @param sqlQuery La consulta SQL a ejecutar.
     * @return Un ResultSet con los resultados de la consulta, o null si la
     * consulta es inválida.
     * @throws SQLException Si ocurre un error al ejecutar la consulta.
     */
    public static ResultSet executeQueryRead(Connection connection, String sqlQuery) throws SQLException {
        // Validar que la cadena no se envíe vacía o nula
        if (sqlQuery != null && !sqlQuery.isBlank()) {
            PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
            return pstmt.executeQuery();
        }
        return null;
    }
    
    public static String executeQueryWrite(Connection connection, String sqlQuery)throws SQLException{        
        if (sqlQuery != null && !sqlQuery.isBlank() && Conexion.getStateConnection()){
            PreparedStatement pstmt = connection.prepareStatement(sqlQuery);
            if(pstmt.executeUpdate() > 0){
                Conexion.cerrarConexion();
                return "Consulta executada exitosamente";  
            }
        }
        return "Consulta no executada, ocurrio un error:";
    }    
    /**
     * Establece el tipo de escritura de la consulta.
     *
     * @param queryWriteType El tipo de operación de escritura (insert, update,
     * delete).
     */
    public static void setActionQuery(int queryWriteType) {
        Consulta.queryWtriteType = queryWriteType;
    }

    /**
     * Obtiene la descripción de las columnas de la tabla.
     *
     * @return Una lista con los nombres de las columnas de la tabla.
     */
    public static ArrayList<String> getColumnsDescrip() {
        return columnsDescrip;
    }

    /**
     * Obtiene el tipo de acción de escritura configurado.
     *
     * @return El tipo de acción de escritura actual.
     */
    public static int getActionQuery() {
        return Consulta.queryWtriteType;
    }

    /**
     * Obtiene el nombre de la tabla seleccionada.
     *
     * @return El nombre de la tabla actualmente seleccionada.
     */
    public static String getTableSelect() {
        return tableSelect;
    }

    /**
     * Establece el nombre de la tabla seleccionada.
     *
     * @param tableSelect El nombre de la tabla a seleccionar.
     */
    public static void setTableSelect(String tableSelect) {
        Consulta.tableSelect = tableSelect;
    }
}
