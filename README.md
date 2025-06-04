# TablasBD

## Descripción del Proyecto
TablasBD es un proyecto en Java diseñado para ser utilizado como una librería (JAR) en otras aplicaciones. Su propósito es simplificar la interacción con bases de datos relacionales (específicamente MySQL y SQL Server), permitiendo la gestión de conexiones, la ejecución de consultas y la preparación de datos para visualización en una interfaz gráfica. Es ideal para proyectos donde se necesita integrar funcionalidades de base de datos de manera modular.

## Tecnologías Utilizadas
Este proyecto fue desarrollado utilizando el Java Development Kit (JDK) 8+ y se apoya en las siguientes librerías:

* **API JDBC (`java.sql.*`)**: Esencial para toda la comunicación con las bases de datos.
* **Swing (`javax.swing.*`)**: Para la creación de componentes de interfaz gráfica (aunque el JAR no genera una GUI por sí mismo, sus modelos de datos están pensados para Swing).
* **API de Fecha y Hora (`java.time.LocalDate`)**: Para el manejo moderno de fechas (su uso puede estar limitado o en desarrollo).
* **Clases de Utilidad (`java.util.ArrayList`, `java.util.Arrays`)**: Para la gestión eficiente de colecciones de datos.

---

## Controladores de Base de Datos (Drivers JDBC)
Para que TablasBD funcione correctamente al ser incluido en otro proyecto, el proyecto consumidor debe tener los siguientes archivos JAR (drivers JDBC) en su classpath:

* Para **SQL Server**: `mssql-jdbc-12.8.1.jre11.jar`
* Para **MySQL**: `mysql-connector-j-8.4.0.jar`

> Estos drivers no están incluidos en el JAR de TablasBD; deben ser gestionados por el proyecto que lo utilice.

## Estructura del Proyecto
El proyecto se organiza en el paquete principal `TablaBD.app.com` y consta de tres clases principales:

* `Conexion.java`: Gestiona el establecimiento y cierre de la conexión con la base de datos.
* `Consulta.java`: Se encarga de ejecutar operaciones SQL, como consultas de lectura (SELECT) y la obtención de metadatos de bases de datos y tablas.
* `ModeloDatos.java`: Adapta los datos recuperados de la base de datos para que puedan ser mostrados fácilmente en componentes de interfaz gráfica como `JTable` (al implementar `AbstractTableModel`).

## Cómo Usar TablasBD en Otros Proyectos
Dado que TablasBD se espera como una librería JAR, el proceso de uso es el siguiente:

1. **Compilar el Proyecto**:
   * Abre este proyecto en NetBeans.
   * Limpia y construye el proyecto (**Clean and Build Project**). NetBeans generará un archivo `.jar` (por ejemplo, `TablasBD.jar`) en la carpeta `dist/` del proyecto.

2. **Añadir el JAR a tu Proyecto**:
   * En tu otro proyecto de NetBeans, haz clic derecho en la carpeta **"Libraries"** (Librerías).
   * Selecciona **"Add JAR/Folder..."** (Añadir JAR/Carpeta...).
   * Navega hasta la ubicación del `TablasBD.jar` (dentro de la carpeta `dist/` de este proyecto) y selecciónalo.

3. **Incluir Drivers JDBC**:
   * Asegúrate de que tu proyecto también tenga los drivers `mssql-jdbc-12.8.1.jre11.jar` y/o `mysql-connector-j-8.4.0.jar` añadidos a sus librerías.

4. **Utilizar las Clases**:
   * Ya puedes importar y utilizar las clases `Conexion`, `Consulta` y `ModeloDatos` desde el paquete `TablaBD.app.com` en tu nuevo proyecto.
