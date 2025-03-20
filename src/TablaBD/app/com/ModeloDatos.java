/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TablaBD.app.com;
import javax.swing.table.AbstractTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 * Modelo de datos para representar una tabla en una interfaz gráfica.
 * Esta clase extiende <code>AbstractTableModel</code> y proporciona los datos de una base de datos
 * para ser mostrados en un <code>JTable</code>. Se encarga de cargar los datos desde una base de datos
 * (MySQL o SQL Server) y proporcionar los valores de la tabla para la visualización.
 * 
 * @authors capistran y díaz
 * @version 1.0
 */
public class ModeloDatos extends AbstractTableModel {
    
    // Atributos
    private ArrayList<Object[]> data; // Lista que contiene las filas de la tabla
    private String[] columnNames; // Nombres de las columnas
    private Conexion conexion; // Objeto que maneja las conexiones a la base de datos
    private int opcionBase; // Opción que indica qué tipo de base de datos se está utilizando (1 para SQL Server, 2 para MySQL)

    /**
     * Constructor de la clase <code>ModeloDatos</code>.
     * Inicializa los atributos y carga los datos de la base de datos correspondiente.
     * 
     * @param conexion El objeto que maneja la conexión con la base de datos.
     * @param opcionBase La opción que especifica qué base de datos utilizar (1 para SQL Server, 2 para MySQL).
     */
    public ModeloDatos(Conexion conexion, int opcionBase) {
        this.conexion = conexion;
        this.opcionBase = opcionBase;
        data = new ArrayList<>(); // Inicializa la lista de datos (filas)
        loadData(); // Carga los datos de la base de datos
    }

    /**
     * Carga los datos desde la base de datos especificada.
     * Solicita al usuario el nombre de la tabla y realiza una consulta SQL para obtener
     * los datos y cargarlos en el modelo de datos.
     * 
     * El método maneja tanto la conexión a MySQL como a SQL Server, dependiendo de la opción seleccionada.
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
        switch (opcionBase) {
            case 1:
                conn = conexion.getConexionSQLServer(); // Conexión con SQL Server
                break;
            case 2:
                conn = conexion.getConexionMySQL(); // Conexión con MySQL
                break;
            default:
                JOptionPane.showMessageDialog(null, "Opción no disponible", "Validación de BD",
                        JOptionPane.WARNING_MESSAGE); // Muestra un mensaje si la opción no es válida
                return;
        }

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
                e.printStackTrace(); // Muestra el error en consola en caso de una excepción SQL
            } finally {
                conexion.cerrarConexion(); // Cierra la conexión a la base de datos
            }
        } else {
            System.err.println("Error al establecer la conexión"); // Muestra un mensaje de error si la conexión no se pudo establecer
        }
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
     * Devuelve el valor de una celda en la tabla, especificada por su fila y columna.
     * 
     * @param rowIndex El índice de la fila (comienza en 0).
     * @param columnIndex El índice de la columna (comienza en 0).
     * @return El valor en la celda correspondiente a la fila y columna especificadas.
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
