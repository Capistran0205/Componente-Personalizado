package TablaBD.app.com;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * Modelo de datos para representar una tabla en una interfaz gráfica. Esta
 * clase extiende <code>AbstractTableModel</code> y proporciona los datos de una
 * base de datos para ser mostrados en un <code>JTable</code>. Se encarga de
 * cargar los datos desde una base de datos (MySQL o SQL Server) y proporcionar
 * los valores de la tabla para la visualización.
 *
 * @authors capistran y díaz
 * @version 1.0
 */
public class ModeloDatos extends AbstractTableModel {

    // Atributos
    private ArrayList<Object[]> data; // Lista que contiene las filas de la tabla
    private String[] columnNames; // Nombres de las columnas
    private Conexion conexion; // Objeto que maneja las conexiones a la base de datos
    private ArrayList<String> metaData; // Lista para contener los metadatos de la BD
    private int opcionBase; // Opción que indica qué tipo de base de datos se está utilizando (1 para SQL Server, 2 para MySQL)

    /**
     * Constructor de la clase <code>ModeloDatos</code>. Inicializa los
     * atributos y carga los datos de la base de datos correspondiente.
     *
     * @param conexion El objeto que maneja la conexión con la base de datos.
     * @param opcionBase La opción que especifica qué base de datos utilizar (1
     * para SQL Server, 2 para MySQL).
     */
    public ModeloDatos(Conexion conexion, int opcionBase) {
        this.conexion = conexion;
        this.opcionBase = opcionBase;
        metaData = new ArrayList<>(); // Incializa la lista de metadatos         
    }

    /**
     * Sobrecarga del constructor cuando se específica la consulta completa o
     * una de mayor complejidad
     *
     * @param conexion
     * @param opcionBase
     * @param sqlQuery
     */
    public ModeloDatos(Conexion conexion, int opcionBase, String sqlQuery) {
        this.conexion = conexion; // Incializa la lista de metadatos
        this.opcionBase = opcionBase;
        data = new ArrayList<>(); // Inicializa el arreglo 
        loadData(sqlQuery); // Cargar el modelo de datos
    }

    /**
     * Carga los datos desde la base de datos especificada. Solicita al usuario
     * el nombre de la tabla y realiza una consulta SQL para obtener los datos y
     * cargarlos en el modelo de datos.
     *
     * El método maneja tanto la conexión a MySQL como a SQL Server, dependiendo
     * de la opción seleccionada.
     */
    private void loadData() {
        String tableName = JOptionPane.showInputDialog(null, "Ingrese el nombre de la tabla:", "Nombre de la Tabla",
                JOptionPane.QUESTION_MESSAGE); // Solicita el nombre de la tabla al usuario
        Connection conn = null;

        // Verifica que el nombre de la tabla no esté vacío
        if (tableName == null || tableName.isEmpty()) {
            System.err.println("Nombre de la tabla no ingresado");
            return; // Sale si no se ingresa un nombre de tabla
        }

        // Establece la conexión según la base de datos seleccionada
        conn = conexion.getConexion(Conexion.getIdSGBD());

        // Si la conexión fue exitosa, realiza la consulta y carga los datos
        if (conn != null) {
            try {
                // Ejecuta la consulta para obtener los datos de la tabla
                ResultSet rs = Consulta.executeQuery(conn, tableName);

                // Obtiene los nombres de las columnas de la tabla
                int columnCount = rs.getMetaData().getColumnCount();
                columnNames = new String[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = rs.getMetaData().getColumnName(i + 1); // Asigna los nombres de las columnas
                }

                // Carga las filas de la tabla
                while (rs.next()) {
                    Object[] row = new Object[columnCount]; // Crea un arreglo para cada fila
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getObject(i + 1); // Asigna el valor de cada columna en la fila
                    }
                    System.out.println("Fila cargada: " + Arrays.toString(row)); // Muestra la fila cargada en consola
                    data.add(row); // Añade la fila a la lista de datos
                }
                rs.close(); // Cierra el ResultSet
                fireTableDataChanged(); // Notifica que los datos han cambiado para actualizar la vista de la tabla
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Ocurrio un error en el modelo de datos" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra el error en consola en caso de una excepción SQL
            } finally {
                conexion.cerrarConexion(); // Cierra la conexión a la base de datos
            }
        } else {
            System.err.println("Error al establecer la conexión"); // Muestra un mensaje de error si la conexión no se pudo establecer
        }
    }

    /**
     * Carga datos desde una consulta SQL y almacena la información en
     * estructuras dinámicas.
     *
     * @param sqlQuery La consulta SQL que se utilizará para obtener los datos.
     */
    private void loadData(String sqlQuery) {
        Connection conn = null;

        // Verifica que la consulta no sea nula o vacía
        if (sqlQuery == null || sqlQuery.isEmpty()) {
            System.err.println("La query no fue definida");
            return; // Sale si no se proporciona una consulta válida
        }

        // Establece la conexión según la base de datos seleccionada
        conn = conexion.getConexion(Conexion.getIdSGBD());

        // Si la conexión fue exitosa, ejecuta la consulta y procesa los resultados
        if (conn != null) {
            try {
                // Ejecuta la consulta para obtener los datos de la tabla
                ResultSet rs = Consulta.executeQueryRead(conn, sqlQuery);

                // Obtiene la cantidad de columnas en la tabla
                int columnCount = rs.getMetaData().getColumnCount();
                columnNames = new String[columnCount];

                // Recupera los nombres de las columnas
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = rs.getMetaData().getColumnName(i + 1);
                }

                // Carga los datos de las filas en la lista
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    System.out.println("Fila cargada: " + Arrays.toString(row));
                    data.add(row);
                }

                rs.close(); // Cierra el ResultSet
                fireTableDataChanged(); // Notifica que los datos han cambiado para actualizar la vista
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error en el modelo de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                conexion.cerrarConexion(); // Cierra la conexión a la base de datos
            }
        } else {
            System.err.println("Error al establecer la conexión");
        }
    }

    /**
     * Obtiene los nombres de las tablas en la base de datos seleccionada.
     *
     * @return Una lista con los nombres de las tablas disponibles en la base de datos.
     */
    public ArrayList<String> getLoadMetaData() {
        Connection conn = null;

        // Establece la conexión según la base de datos seleccionada
        conn = conexion.getConexion(Conexion.getIdSGBD());
        
        // Verifica el estado de la conexión antes de proceder
        if (conexion.getStateConnection()) {
            try {
                ResultSet tables = Consulta.getTablesMetaData(conn);

                // Almacena los nombres de las tablas en la lista metaData
                while (tables.next()) {
                    metaData.add(tables.getString("TABLE_NAME"));
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error en el modelo de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                conexion.cerrarConexion(); // Cierra la conexión a la base de datos
            }
        }
        return metaData;
    }

    /**
     * Devuelve el número de filas en la tabla.
     *
     * @return El número de filas en la tabla de datos.
     */
    @Override
    public int getRowCount() {
        return data.size(); // Devuelve el tamaño de la lista de datos (número de filas)
    }

    /**
     * Devuelve el número de columnas en la tabla.
     *
     * @return El número de columnas en la tabla de datos.
     */
    @Override
    public int getColumnCount() {
        return columnNames.length; // Devuelve la longitud del arreglo de nombres de columnas
    }

    /**
     * Devuelve el valor de una celda en la tabla, especificada por su fila y
     * columna.
     *
     * @param rowIndex El índice de la fila (comienza en 0).
     * @param columnIndex El índice de la columna (comienza en 0).
     * @return El valor en la celda correspondiente a la fila y columna
     * especificadas.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex]; // Devuelve el valor de la celda especificada
    }

    /**
     * Devuelve el nombre de una columna de la tabla.
     *
     * @param column El índice de la columna (comienza en 0).
     * @return El nombre de la columna especificada.
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column]; // Devuelve el nombre de la columna especificada
    }
}
