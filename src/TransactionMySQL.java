/*
 * Clase que contiene metodos y atributos para realizar transacciones conectandose
 * a una base de datos de MySQL haciendo uso de la libreria de JDBC
 */

//Librerias

import java.sql.*;
import java.io.*;
import java.util.Calendar;
import java.util.Scanner;

//Inicio clase TransactionMySQL
public class TransactionMySQL {
    //ATRIBUTOS

    //Declaracion Objeto conexion
    Connection conn = null;
    //Declaracion Enunciado(query)
    Statement stmt = null;
    //Declaracion Objeto bufferedReader para obtener la info (Equivalente a Scanf)
    BufferedReader in = null;

    //Establecer información GLOBAL de la BDs a donde se va a conectar
    //Ubicacion
    static final String URL = "jdbc:mysql://localhost/";
    //Nombre BD
    static final String BD = "agenciaponchito";
    //Credenciales Usuario
    static final String USER = Personal.user;
    static final String PASSWD = Personal.password;//Password vacio
    String[][] hotelesMtrx = new String[100][8];
    int numberHoteles = 0;
    int costoHoteles = 0;

    //+++++++++++++++++++++ INICIO CONSTRUCTOR +++++++++++++++++++++++++++++++++
    public TransactionMySQL() throws SQLException, Exception {
        //Esto se cargara en el Driver de MySQL, cada BDs tiene su propio driver
        Class.forName("com.mysql.jdbc.Driver");
        System.out.print("Conectandose a la base de datos... ");

        //Preparar la conexion con la BDs/Inicializacion de objeto Connerction
        conn = DriverManager.getConnection(URL + BD, USER, PASSWD);
        System.out.println(" Conexion exitosa!\n\n");

        //Desactivar auto commit para manejar manualmente transacciones
        conn.setAutoCommit(false);
        //Inicializar un enunciado con la conexion actual
        stmt = conn.createStatement();
        //Inicializar lector de buffer para leer los datos de un stream de entrada
        //Equivalente a Scanf
        in = new BufferedReader(new InputStreamReader(System.in));

    }//Fin constructor
    //+++++++++++++++++++++ FIN CONSTRUCTOR ++++++++++++++++++++++++++++++++++++

    //METODOS
    //--------------------------------------------------------------------------
    //Metodo que dado un query guarda todo el contenido en una matrizsi se logra
    //encontrar algo, si no se encuentra nada se regresa una matriz de tamaño 0
    //Este metodo regresa una tupla por fila con cada atributo en la columna
    //correspondiente para cada una de las tuplas
    String[][] ResultQuery(String query) throws SQLException {
        //----------------
        //System.out.println("Entro resultquery");
        //---------------
        //Ejecutar query y guardar resultado en ResultSet
        ResultSet rset = stmt.executeQuery(query);

        //Obtener los metadatos del query resultante
        ResultSetMetaData rsetmd = rset.getMetaData();

        //Determinar el maximo de coulumnas de la metadata del query resultante
        int numc = rsetmd.getColumnCount();

        //Declarar variable de matriz que se regresa
        String[][] result;

        //Determinar el numero de tuplas/filas
        //1)Posicionar el cursor en ultima fila
        rset.last();
        //Regresar numero de columna actual, como la primera columna inicia con 1
        //se tiene el numero exacto de columnas
        int numr = rset.getRow();

        //Posicionar el cursor antes de la primera fila
        rset.beforeFirst();

        //Matriz para guardar el resultado de la consulta[NumTuplas][NumColumnas]
        //Cada fila es una tupla con cada uno de sus atributos en cada posicion
        //de columna correspondiente

        //**OJO si no hay ninguna tupla en el resultado se regresa 0 para filas/rows
        ///de la matriz lo que es malo ya que se regresa una matriz sin filas
        //por ende si la matriz tiene 0 filas se inicializa con [1][1] donde
        //el unico valor sera null, todo esto para evitar erores
        if (numr != 0) {
            result = new String[numr][numc];
        } else {
            result = new String[1][1];
        }

        //---------------
        //System.out.println("Filas:"+numr+"  Columnas:"+numc);
        //---------------

        //Declarar contadores para posicon en filas y columnas del arreglo
        //para que se guarde desde 0
        int ifil = 0;
        int icol = 0;

        //Como cursor de fila ya esta antes de primera posicion hacer el guardado
        //del contenido de forma ordenada
        while (rset.next()) {
            //------------------------------------
            //System.out.println("Tupla #"+ifil+": ");
            //--------------------------------------

            //Para cada fila/Tupla guardar el contenido de cada uno de sus
            //columnas inicando por la columna 1 pero guardando desde la fila 0
            //en el arreglo
            for (int i = 1; i <= numc; i++) {
                result[ifil][icol] = rset.getString(i);
                //---------------
                //System.out.println("Guardado en:"+ifil+":"+icol);
                //System.out.print("Atributo #"+i+": ");
                //System.out.println(rset.getString(i));
                //---------------

                //Incremento de columna en 1
                icol++;

            }//Fin for 1

            //Reseteo de valores de columna(atributos) para siguiente tupla(si es que hay)
            icol = 0;

            //Incremento de fila en 1
            ifil++;

        }//Fin while 1

        //Cerrar ResultSet
        rset.close();

        return result;

    }//Fin metodo result query

    //--------------------------------------------------------------------------
    //Metodo que dado el conjunto resultante obtenido de un query hace la impresion
    //de esta informacion en consola
    public void dumpResultSet(ResultSet rset) throws SQLException {
        //Metadatos del ResultSet
        ResultSetMetaData rsetmd = rset.getMetaData();

        //Contar el numero de columnas que hubo en la metadata del conjunto
        //resultante obtenido tras un quety
        int i = rsetmd.getColumnCount();

        //Mientras halla una siguiente fila(tupla)
        //rset.next mueve el cursor una tupla/fila, inicialmente el cursor
        //es posicionado antes de la primera columna, por lo que la primera vez
        //que se llama se pociona en columna 0  y luego continua
        while (rset.next()) {
            //Para cada fila/Tupla imprimir el contenido de cada uno de sus
            //columnas inicando por la columna 1
            for (int j = 1; j <= i; j++) {
                //Imprimir contenido columna(j) en fila actual(rset.next())
                System.out.print(rset.getString(j) + "\t");

            }//Fin for 1

            //Impresion para delmimitar el fin de tupla /columna
            System.out.println();

        }//Fin while 1

    }//Fin metodo dumpResultSet

    //--------------------------------------------------------------------------
    //Metodo que ejecuta un query que es pasado como string y dado el ResultSet
    //que se obtiene este es impreso mediante la llamada del metodo dumpResuktSet
    public void query(String statement) throws SQLException {
        //Ejecutar query y guardar resultado en ResultSet
        ResultSet rset = stmt.executeQuery(statement);

        //Imprimir resultado
        dumpResultSet(rset);

        //Cerrar el ResultSet del query realizado: Esto libera inmediatamente los recursos
        //de la BDs de este objeto ResultSet y los recursos del JDBC en lugar de
        //esperarse a que esto suceda automaticamente
        rset.close();

    }//Fin metodo query

    //--------------------------------------------------------------------------
    //Metodo que cierra tanto la conexion a la base de datos como el statement
    public void close() throws SQLException {
        //******************IMPORTANTE CLOSE DE STATEMENT***************
        //Cerrar el Statement del objeto query usado en todas las consultas: Esto libera inmediatamente los recursos
        //de la BDs de este objeto Statement y los recursos del JDBC en lugar de
        //esperarse a que esto suceda automaticamente, Es una buena practica hacer esto
        //tan pronto se halla terminado con ellos para evitar el bloqueo de los recursos
        //de la BDs
        stmt.close();

        //Cerrar la Connection a la BDs que se usa: Esto libera inmediatamente los recursos
        //de la BDs de este objeto Connection y los recursos del JDBC en lugar de
        //esperarse a que esto suceda automaticamente.
        conn.close();

    }//Fin metodo close

    //--------------------------------------------------------------------------
    //Metodo que se conecta a la base de datos y busca a un usuario con el nombre
    //que le es dado, si no lo encuentra regresa -1 si no regresa su ID
    int Login() throws SQLException, IOException {
        int idusr = -1;
        String[][] resultado = new String[1][1];

        //Nivel de aislamiento solo para lecturas
        conn.setTransactionIsolation(1);

        System.out.print("Nombre de usuario: ");

        try {
            //Query
            resultado = ResultQuery("select Id,Nombre from USUARIO where Nombre = '" + in.readLine() + "';");
            //Si no hay error hacer commit ()
            conn.commit();  //Fin transaccion e inicio de otra transaccion
        } catch (SQLException sqle) {
            //En caso de error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }
        //--------------------
        //System.out.println("Tamaño resultado: "+);
        //--------------------

        //Ver si regreso vacio el primer atributo de la primera tupla
        if (resultado[0][0] == null) {
            //Dejar con -1 el id
        } else {
            //No regreso vacia; entonces regresar el id del usuario encontrado
            idusr = Integer.parseInt(resultado[0][0]);

            System.out.println("\nBienvenido: " + resultado[0][1]);

        }//Fin else

        return idusr;
    }//Fin login

    //--------------------------------------------------------------------------
    //Metodo que hace el registro de todos los Usuarios (Potenciales y conocidos)
    //asignando siempre un id consecutivo, para lo que primero obtiene cual
    //es el id mas grande de todos los usuarios y le suma 1 para el nuevo usuario
    //que se registra; mismo ID que se regresa al final de este metodo
    int userRegistration() throws SQLException, IOException {
        int idassigned = -1;
        String res[][];
        String name = "";

        System.out.println("\tRegistro de nuevo Usuario");
        System.out.print("Nombre de usuario: ");
        name = in.readLine();


        //Debido a que al dar de alta un usuario se toma como referencia el valor
        //Más alto de la tabla, pero no se toma un valor especifico entonces
        //hay que evitar que dos registros sucedan al mismo tiempo por lo que
        //se debe usar el nivel mas alto de asilamiento para este proceso, evitando
        //que dos registros sucedan al mismo tiempo (SERIALIZABLE)
        conn.setTransactionIsolation(8);

        //Inicio transaccion
        try {
            res = ResultQuery("select max(id) from USUARIO");

            //--------------
            //System.out.println("Num col:"+res.length);
            //System.out.println("NumFilas:"+res[0].length);
            //--------------

            //Verificar si no hay ni una tupla; si no hay nada se regresa null a
            //pesar de que este el metodo resultset.getString(NumColumna)
            if (res[0][0] == null) {
                //Asignar 1 para el primer usuario
                idassigned = 1;
            }//Fin if 1
            else {
                //Sumarle 1 al ID del usuario mas alto
                idassigned = Integer.parseInt(res[0][0]) + 1;
            }//Fin else 1

            //Hacer la actualizacion
            stmt.executeUpdate("insert into USUARIO values ('" + name + "'," + idassigned + ");");

            //Si no hay error hacer commit
            conn.commit();      //Fin transaccion e inicio de otra transaccion

        } catch (SQLException sqle) {
            //Si hay error cancelar la transaccion
            conn.rollback();    //Fin transaccion e inicio de otra transaccion

            System.out.println("Error de registro. Cliente ya exitente con ese nombre!");
            //Asiganr -1 para que no entre a la BDs
            idassigned = -1;
            sqle.printStackTrace();
        }//Fin catch


        return idassigned;

    }//Fin userRegistration

    //--------------------------------------------------------------------------
    //Metodo que dado el nombre de un pais busca en la tabla CIUDADES si hay
    //alguno, si lo encuentra lo regresa como String si no lo enecuntra regresa
    //""
    String queryPais() throws SQLException, IOException {
        String paisdes[][];
        String paisres = "";
        //Nivel de islamiento solo para lecturas
        conn.setTransactionIsolation(1);

        System.out.print("Nombre de pais deseado: ");


        //Por defincion todas las operaciones de una transaccion deben acabar
        //con un commit o rollback para dar inicio a la siguiente transaccion
        //No obstante en esta operacion de una consulata con bajo nivel de
        //aislamiento es poco probable que cause diferencia ya que los datos
        //no son bloqueados para otras operaciones
        try {
            //Hacer query
            paisdes = ResultQuery("select distinct(Pais) from CIUDAD where Pais = '" + in.readLine() + "';");
            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion

            //Verificar que se haya encontrado un pais, si no regresar vacio
            if (paisdes[0][0] == null) {
                paisres = "";
            }//Fin if
            else {
                //Regresa rnombre pais encontrado
                paisres = paisdes[0][0];
                System.out.println("Pais "+paisres+" encontrado");
            }//Fin else

        }//Fin try
        catch (SQLException sqle) {
            //Si hay error cancelar todo con rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

        return paisres;

    }//Fin metodo query pais

    //--------------------------------------------------------------------------
    //Metodo que imprime en pantalla todos los paises en la BDs
    public void qryAllPaises() throws SQLException, IOException {
        //Nivel de islamiento solo para lecturas
        conn.setTransactionIsolation(1);

        try {
            //Hacer query
            query("select distinct(Pais) from CIUDAD ;");
            //Si no hay error hacer commit
            conn.commit();  //Fin transaccion e inicio de otra transaccion
        } catch (SQLException sqle) {
            //Si hay error cancelar todo con rollback
            conn.rollback();    //Fin transaccion e inicio de otra transaccion

        }//Fin catch

    }//Fin metodo todos qryAllPaises

    //--------------------------------------------------------------------------
    //Metodo que hace transaccion para buscar Una ciudad en un pais especifico
    //Si encuentra la ciudad regresa el Atributo nombre, si no se encuentra
    //regresa ""
    public String queryCiudad(String pais) throws SQLException, IOException {
        //CIudad resultante
        String cityfound = "";
        //Resultado del quey
        String[][] qryres;

        //Nivel de aislamiento que hace lecturas sobre contenido que puede
        //que todavia no este en disco
        conn.setTransactionIsolation(1);

        System.out.print("Nombre ciudad: ");

        try {
            qryres = ResultQuery("select Nombre from CIUDAD where "
                    + "Pais = '" + pais + "' and Nombre = '" + in.readLine() + "';");
            //Si no hay error hacer el commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion

            //Verificar que se haya encontrado una ciudad
            if (qryres[0][0] == null) {
                //cityfound sigue siendo ""

            }//Fin if 1
            else {
                cityfound = qryres[0][0];
            }//Fin else 1

        }//Fi try
        catch (SQLException sql) {
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion

        }//Fin catch

        return cityfound;

    }//Fin queryCiudad

    //--------------------------------------------------------------------------
    //Metodo que imprime en pantalla todas las ciudades de un pais
    public void qryAllCiudades(String pais) throws SQLException, IOException {
        //Nivel de aislamiento que hace lecturas sobre contenido que puede
        //que todavia no este en disco
        conn.setTransactionIsolation(1);

        try {
            query("select distinct(Nombre) from CIUDAD where Pais = '" + pais + "';");
            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion
        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

    }//Fin qryAllCiudades

    public void queryAllLugares(String ciudad) throws SQLException, IOException {
        //Nivel de aislamiento que hace lecturas sobre contenido que puede
        //que todavia no este en disco
        conn.setTransactionIsolation(1);

        try {
            query("select nombre from LUGARAVISITAR where Ciudad = '" + ciudad + "';");
            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion
        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

    }//Fin queryAllLugares

    public void queryAllLugaresP(String pais) throws SQLException, IOException {
        //Nivel de aislamiento que hace lecturas sobre contenido que puede
        //que todavia no este en disco
        conn.setTransactionIsolation(1);

        try {
            query("select nombre, ciudad from LUGARAVISITAR where Pais = '" + pais + "';");
            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion
        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

    }//Fin queryAllLugaresP

    //--------------------------------------------------------------------------
    //Metodo que imprime en pantalla todas las ciudades disponibles
    public void qryAllDistnCiudades() throws SQLException, IOException {
        //Nivel de aislamiento que hace lecturas sobre contenido que puede
        //que todavia no este en disco
        conn.setTransactionIsolation(1);

        try {
            query("select distinct(Nombre) from CIUDAD;");
            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion
        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

    }//Fin qryAllDistnCiudades

    //--------------------------------------------------------------------------
    public void queryAllLugares() throws SQLException, IOException{
        conn.setTransactionIsolation(1);
        try{
            query("select Nombre, Ciudad from LUGARAVISITAR;");
            conn.commit();
        }//Fin try
        catch(SQLException sqle){
            conn.rollback();
        }//end catch
    }//end queryAllLugares
    //Fin

    //--------------------------------------------------------------------------
    //Metodo que regresa un lugar especifico en forma String[1][2] {Nombre,Ciudad}] { {Nombre,Ciudad} }
    //y si no encuentra el lugar especificado regresa String[1][2] {{null,null}}
    public String[][] queryLugar(String pais) throws SQLException, IOException {
        //Lugar resultante
        String[][] resplace = new String[1][2];
        //Resultado del query
        String[][] qryres;
        //Especificacion de Ciudad
        String city = "";

        System.out.print("Nombre ciudad: ");
        city = in.readLine();

        System.out.print("Nombre lugar: ");

        try {
            qryres = ResultQuery("select Nombre,Ciudad from LUGARAVISITAR where "
                    + "Nombre = '" + in.readLine() + "' and Ciudad = '" + city + "' and Pais = '" + pais + "';");

            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion

            //Verificar que se haya encontrado un lugar
            if (qryres[0][0] == null) {
                //No se encontro el lugar
                //Poner en "" los valores
                resplace[0][0] = "";
                resplace[0][1] = "";
            }//Fin if 1
            else {
                //Regresar lugar encontrado
                resplace = qryres;
            }//Fin else 1

        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

        return resplace;

    }//Fin metodo queryLugar

    //--------------------------------------------------------------------------
    //Metodo que dado un pais y ciudad imprime todos los lugares que tiene
    //Ademas del precio
    public void qryAllLugares(String pais, String ciudad) throws SQLException, IOException {
        //Nivel de aislamiento que hace lecturas sobre contenido  que puede que
        //todavia no haya bajado a disco
        conn.setTransactionIsolation(1);

        try {
            query("select distinct(Nombre),Ciudad,Precio from LUGARAVISITAR where "
                    + "Ciudad = '" + ciudad + "' and Pais = '" + pais + "';");
            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion

        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

    }//Fin metodo qryAllLugares

    //--------------------------------------------------------------------------
    //Dado un nombreLugar Pais y Ciudad busca en la tabla Etapa las etapas que
    //contengan a ese lugar y regresa todos los Identificadores y fechas de Circuitos
    //diferentes que tienen salida a partir del dia actual o  de dias posteriores
    //(No regresa circuitos con fechas de salida que ya hayan pasado)
    //RESUMEN: SE OBTIENEN LAS FECHACIRCUITO validas posibles con base en un lugar y la fecha del sistema
    public String[][] qryFechaCircuitos(String pais, String ciudad, String place, Date dat) throws SQLException, IOException {
        //Regresa{{Identificador,Date},{Identificador2,Date2},...,{Identificadorn,Daten}}
        String rescir[][];

        //Nivel de aislamiento que hace lecturas sobre contenido que puede que
        //todavia no haya bajado a disco
        conn.setTransactionIsolation(1);

        try {
            rescir = ResultQuery("select ETAPA.Identificador,FechaSalida "
                    + "from ETAPA,FECHACIRCUITO "
                    + "where ETAPA.NombreLugar = '" + place + "' and ETAPA.Pais = '" + pais + "' and ETAPA.Ciudad = '" + ciudad + "' and "
                    + "FechaSalida >= '" + dat.toString() + "' and ETAPA.Identificador = FECHACIRCUITO.Identificador;");
            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra transaccion

        }//Fin try
        catch (SQLException sqle) {
            //Si hay error regresar resultado inicializado con valores nulos
            rescir = new String[1][2];

            sqle.printStackTrace();
            //Si hay error hacer rollback
            conn.rollback(); //Fin transaccion e inicio de otra transaccion
        }//Fin catch

        return rescir;

    }//Fin metodo qryInfoCircuitos

    //--------------------------------------------------------------------------
    //Dado el Identificador y FechaSlaida busca en la tabla FechaCircuitro el CIrucito
    //que este en la fehca indicada
    //y con base en este se muesta la Fecha de Salida (TABLA FECHACIRCUITO)
    //y Duracion(CIRCUITO completo),Precio (Circuito COMPLETO),Ciudad Salida y Ciudad Llegada(TABLA CIRCUITO)
    public void qryInfoAllFechaCircuitos(String id, String fecha) throws SQLException, IOException {
        //Nivel de aislamiento que hace lecturas sobre contenido  que puede que
        //todavia no haya bajado a disco
        conn.setTransactionIsolation(1);

        try {
            query("select FECHACIRCUITO.Identificador,FechaSalida,CIRCUITO.Duracion,CiudadSalida,CiudadLlegada,Precio "
                    + "from FECHACIRCUITO, CIRCUITO "
                    + "where FECHACIRCUITO.Identificador = '" + id + "' and FechaSalida = '" + fecha + "' and "
                    + "FECHACIRCUITO.Identificador = CIRCUITO.Identificador;");

            //Si no hay error hacer commit
            conn.commit();      //Fin transaccion e inicio de otra transaccion

        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            sqle.printStackTrace();
            conn.rollback();    //Fin transaccion e inicio de otra transaccion
        }//Fin catch

    }//Fin metodo qryInfoAllCircuitos

    //--------------------------------------------------------------------------
    //Dado un identificador de circuito desglozar la infromacion relacionada
    //conc ada etapa de manera ordenada
    public void qryInfoEtapas(String id) throws SQLException, IOException {
        //Nivel de asilameinto que hace lectura sobre contenido que puede que
        //todavia no haya bajado a disco
        try {
            query("select NombreLugar, Ciudad, Duracion "
                    + "from ETAPA "
                    + "where Identificador = '" + id + "' "
                    + "order by Orden;");

            //Si no hay error hacer commit
            conn.commit();  //Fin transaccion e inicio de otra transaccion

        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            sqle.printStackTrace();
            conn.rollback();    //Fin transaccion e inicio de otra transaccion
        }//Fin catch

    }//Fin metodo qryInfodesCircuito

    //--------------------------------------------------------------------------
    //Dado el identificador de Circuito y fecha de salida regresar la cadena
    //que calcula la fecha de Finalizacion de un circuito
    public String qryGetFechaLlegada(String id, String fecha) throws SQLException, IOException {
        //Obtener resultado de ResultQuery {FechaLlegada}
        String[][] result;
        String fechallegada = "";

        //Nivel de aislamiento que hace lecturas sobre contenido que puede
        //que todavia no haya bajado a disco
        conn.setTransactionIsolation(1);

        try {
            //A la fecha de salida pasada como aprametro sumarle la Duracion del Ciruito
            result = ResultQuery("select DATE_ADD('" + fecha + "', INTERVAL Duracion day) as FechaLlegada "
                    + "from CIRCUITO "
                    + "where Identificador = '" + id + "';");

            //Guardar la fecha de llegada como solo una cadena
            //Si regresa null significa que el idnetificador es icnorrecto
            if (result[0][0] == null) {
                //No guardar  y dejar  que fecha llegada siga siendo ""
            }//Fin if 1
            else {   //Se ingreo un identificador valido
                fechallegada = result[0][0];
            }//Fin else 1
            //Si no hay error hacer commit
            conn.commit();  //Fin transaccion e inicio de otra

        }//Fin try
        catch (SQLException sqle) {
            //Si hay error hacer rollback
            sqle.printStackTrace();
            conn.rollback(); //Fin transaccion e inicio de otra

        }//Fin catch

        return fechallegada;

    }//FIn metodo qryGetFechaLlegada

    //--------------------------------------------------------------------------
    //Metodo que hace elimiancion de todas las simualciones cuya fecha de expiracion
    //es menor a la fecha del sistema actual. Nota fecha de expiracion = FechaEmisionSimulacion+2dias
    //El valor de vigencia ya esta calculado
    public void ElimExpiredSims(Date fecha) throws SQLException {
        //Usar nivel de aislamiento 8 para transacciones que se ejcuten hasta
        //despues de esta
        conn.setTransactionIsolation(8);

        try {
            //ELIMINAR SIMULACION YA QUE EXPIRO;
            //ELMINADO TODA LA INFROMACION RELACIONADA
            //TANTO EN SIMULACIONCIRUCITO, SIMULACIONHOTEL COMO RESERVACION
            stmt.executeUpdate("delete from SIMULACION where Vigencia < '" + fecha.toString() + "' ;");


            //Si no hay error hacer commit
            conn.commit(); //Fin transaccion e inicio de otra

        }//Fin try
        catch (SQLException sqle) {
            sqle.printStackTrace();
            //Si hay error hacer rollaback

            conn.rollback(); //Fin transaccion e inicio de otra

        }//Fin catch

        //System.out.println("FIN LIMPIEZA");

    }//Fin metodo ElimExpiredSims

    //--------------------------------------------------------------------------
    //Metodo que recibe todos los datos para armar una simulacion y una vez que
    //finaliza de armarse es validad, si se valida exitosamente regresa el
    //numero de la simulacion en caso de que no sea validada se hace un rollback
    //de toda la informacion para asegurar la coherencia de la BDs, regresa -1
    //si hubo algun error
    int Simulacion(Date fecha, int Idusr) throws SQLException, IOException {
        //Maximo de ciudades/Lugares(lugarAvisitar)/circuitos/hoteles
        //que se pueden seleccionar en cada simulacion son 100 para cada uno
        //no 100 en total
        int MAXCIT = 100;

        //Id de simulacion(valor que regresa este metodo) 0 = no se completo
        //con exito la simulacion / 0 < = se completo con exito la simulacion
        int idsim;

        //Numero asignado de simulacion
        int numsim = -1;
        //Opcion seleccionada do while/pais
        int iop = -1;
        //Opcion seleconada do wile/ciudad
        int ioc = -1;
        //Opcion seleccionada do while/lugares
        int iol = -1;
        //Opcion seleccionada do while/circuitos(etapas)
        int ioe = -1;
        //--Pais seleccionado--
        String pais = "";
        //--Ciudades seleccionadas--
        String[] ciudades = new String[MAXCIT];
        String cittemp = "";
        //--Lugarse seleccionados--
        String[][] lugares = new String[MAXCIT][2]; //Hasta 100 lugares [100filas] [2atributos{Nombre,Ciudad}]
        String[][] lugtemp = new String[1][2]; //Un lugar [1fila][2atributos{NombreLugar,Ciudad}]
        //-- Circuitos posibles con base en lugares selccionados y fecha actual del sistema--
        String[][] poscir = new String[MAXCIT][2]; //Hasta 100 circuitos [100 filas] [2atributos{Identificador,FechaSalida}]
        //--Circuitos seleccionados
        String[][] circuits = new String[MAXCIT][2]; //Hasta 100 circuitos [100 filas] [2atributos{Identificador,FechaSalida}]
        String[][] tempcir = new String[MAXCIT][2]; //Temporal para preever los casos de null cuando no se hayo algun circuito
        //Indice de ciudades (selccionadas)
        int ncit = 0;
        //Indice de lugares (seleccionados)
        int nlug = 0;
        //Indice de circuitos Posibles
        int ncir = 0;
        //Indice de circuitos (seleccionadso)
        int ncirsel = 0;

        //Variable del tipo Date que es inicializada con la fecha actual del sistema
        //y que depsues es actualizada con la fecha de llegada del circuito seleccionado
        //En la parte de seleccion de circuitos
        Date dateCircuit = fecha;

        //Variable que indica que se ha completado de forma correcta la navegacion
        //de la simulacion = 0; -1 = no se completo correctamente entonces
        //no se guardan datos de simulacion en diversas tablas
        int correctsim = -1;

        //Numero de personas para la simulacion/viaje
        int numper = 0;


        System.out.println("\tSIMULACION DE UNA RESERVACION\n");

        System.out.print("Ingrese el numero de personas en el viaje: ");
        numper = Integer.parseInt(in.readLine());

        do {
            System.out.println();
            System.out.println("1)Seleccionar País");
            System.out.println("2)Mostrar paises disponibles");
            System.out.println("0)Salir de simulacion");
            System.out.print("Opcion: ");
            iop = Integer.parseInt(in.readLine());

            if (iop == 2) {
                //Mostrar todos los paises disponibles
                System.out.println("\nResultado de la consulta: ");
                qryAllPaises();
            }//Fin if 1
            else {//ELSE 1
                if (iop == 1) {//IF 2
                    pais = queryPais();

                    if (pais.length() == 0) {//IF 3
                        System.out.println("Pais inexistente, seleccionar un pais valido!");
                        iop = 1;
                    }//Fin if 3
                    else {
                        do {
                            System.out.println();
                            System.out.println("SIMULACION: SELECCION DE CIUDADES\n");
                            System.out.println("1)Mostrar ciudades disponibles en " + pais);
                            System.out.println("2)Seleccionar ciudad");
                            System.out.println("3)Remover ciudad");
                            System.out.println("4)Mostrar ciudades seleccionadas");
                            System.out.println("5)Guardar y continuar");
                            System.out.println("0)Regresar a seleccion de pais");
                            System.out.print("Opcion: ");
                            ioc = Integer.parseInt(in.readLine());

                            switch (ioc) {
                                case 0:
                                    //Resetear Las ciudades poniendo el inidce de
                                    //ciudades en 0
                                    ncit = 0;
                                    System.out.println("\nCiudades seleccionadas descartadas!\n");
                                    break;

                                case 1:
                                    System.out.println("\nResultado de la consulta: ");
                                    qryAllCiudades(pais);
                                    break;

                                case 2:
                                    //Prevenir que no se exceda el maximo de
                                    //ciudades que puede tener una simulacion
                                    if (ncit < MAXCIT) {
                                        cittemp = queryCiudad(pais);

                                        //Agregar ciudad a arreglo de ciudades seleccionadas
                                        //solamente si se regreso una ciudad existente
                                        if (cittemp.length() != 0) {
                                            //ciudades[ncit] = cittemp;
                                            //Agregar ciudades verificando que no se agreguen ciudades duplicadas
                                            ciudades = Insertararray(ciudades, ncit, cittemp);

                                            //Verificar si hubo insercion de ciduad
                                            if (ciudades[ncit] == null) {
                                                //No hubo insercion porque la ciudad ya estaba seleccionada
                                            }//Fin if 5.1
                                            else {
                                                //Si se agreego ciudad
                                                //Incremento contador de indice de ciudades usado en arreglo
                                                ncit++;
                                            }//Fin else 5.2

                                        }//Fin if 5
                                        else {
                                            System.out.println("\nSolo se pueden seleccionar ciudades validas!\n");
                                        }//Fin else 5

                                    }//Fin if 4
                                    else {
                                        System.out.println("Numero de ciudades excedido\n");
                                    }//Fin else 4
                                    break;

                                case 3:
                                    System.out.print("Ciudad que desea remover: ");
                                    //Actualizar el contenido de las ciudades
                                    ciudades = Eliminararray(ciudades, ncit, in.readLine());
                                    break;

                                case 4:
                                    System.out.println("\nCiudades selecciondas:");
                                    Mostrararray(ciudades, ncit);
                                    break;

                                case 5:
                                    //Listar todos los lugares que hay en ciudades
                                    //selecicionadas

                                    do {
                                        System.out.println();
                                        System.out.println("SIMULACION: SELECCION DE LUGARES\n");
                                        System.out.println("1)Mostrar lugares disponibles");
                                        System.out.println("2)Seleccionar lugar");
                                        System.out.println("3)Remover lugar");
                                        System.out.println("4)Mostrar lugares seleccionados");
                                        System.out.println("5)Guardar y continuar");
                                        System.out.println("0)Regresar a seleccion de ciudades");
                                        System.out.print("Opcion: ");
                                        iol = Integer.parseInt(in.readLine());

                                        switch (iol) {
                                            case 0:
                                                //Resetear los lugares poniendo en 0
                                                //indice de lugares
                                                nlug = 0;
                                                System.out.println("\nLugares seleccionados descartados!\n");
                                                break;

                                            case 1:
                                                System.out.println("\nResultado de la consulta: ");
                                                System.out.println("Nombre\tCiudad\tPrecio\n");
                                                //Hacer consulta para cada una de las ciudades
                                                for (int i = 0; i < ncit; i++) {
                                                    qryAllLugares(pais, ciudades[i]);
                                                }//Fin for 1
                                                break;

                                            case 2:
                                                //Prevenir que no se exceda el maximo de lugares que puede tener
                                                //una simulcion
                                                if (nlug < MAXCIT) {//If 5

                                                    lugtemp = queryLugar(pais);

                                                    //--------------------------
                                                    //System.out.println("que se selcciono?");
                                                    //System.out.print("Lugar:"+lugtemp[0][0]);
                                                    //System.out.println(" Ciudad:"+lugtemp[0][1]);
                                                    ///-------------------------

                                                    //Solo seleccionar lugares validos
                                                    //Un lugar no es valido si qry regresa Nombre y Ciudad con longitud 0
                                                    if (lugtemp[0][0].length() != 0 && lugtemp[0][1].length() != 0) {//If 6

                                                        //Guardar en cada fila/tupla llaves de lugar encontrado
                                                        //Omitiendo solamente el atributo de pais pero ese ya esta
                                                        //almacenado en variable pais (Esto se omite en queryLugar() )
                                                        //lugares[nlug] = lugtemp[0];
                                                        lugares = Insertarmatriz(lugares, nlug, lugtemp[0]);

                                                        //Verificar si hubo insercion de lugar para saber se
                                                        //hace aumento del contador de lugares seleccioandos
                                                        if (lugares[nlug][0] == null) {
                                                            //No hubo insercion entonces  no aumentar el contador
                                                        }//Fin if 6.2
                                                        else {
                                                            //Si hubo insercion entonces aumentar el contador
                                                            //Aumentar contador de lugares seleccionados
                                                            nlug++;

                                                        }//Fin else 6.2

                                                    }//Fin if 6
                                                    else {
                                                        System.out.println("\nSolo se pueden seleccionar lugares validos!\n");
                                                    }//Fin else 6

                                                }//Fin if 5
                                                else {
                                                    System.out.println("Numero de lugares excedido!");
                                                }//Fin else  5
                                                break;

                                            case 3:
                                                String name = "";
                                                String npla = "";
                                                System.out.print("\nNombre del lugar que desea remover: ");
                                                name = in.readLine();
                                                System.out.print("Nombre de la ciudad del lugar que desea remover: ");
                                                npla = in.readLine();

                                                //Valores que debe poseer una tupla para ser elimiandos
                                                String[] claves = {name, npla};

                                                //Obtener nueva matriz con elementos ya elimiandos
                                                lugares = Eliminarmatriz(lugares, nlug, claves);
                                                break;

                                            case 4:
                                                System.out.println("\nLugares seleccionados:\n");
                                                //Imprimir el numero lugares seleccionados, el 2
                                                //hace referencia a impimir solo 2 atributos por
                                                //tupla("Nombre(lugar), Ciudad")
                                                Mostrarmatriz(lugares, nlug, 2);
                                                break;

                                            case 5:

                                                do {
                                                    System.out.println();
                                                    System.out.println("SIMULACION: SELECCION DE CIRCUITOS\n");
                                                    System.out.println("1)Mostrar circuitos disponibles");
                                                    System.out.println("2)Desplegar etapas de circuito");
                                                    System.out.println("3)Seleccionar circuito");
                                                    System.out.println("4)Remover circuito");
                                                    System.out.println("5)Mostrar circuitos seleccionados");
                                                    System.out.println("6)Guardar y continuar");
                                                    System.out.println("0)Regresar a seleccion de lugares");
                                                    System.out.print("Opcion: ");
                                                    ioe = Integer.parseInt(in.readLine());

                                                    switch (ioe) {
                                                        case 0:
                                                            //AL SALIR DE SUBMENU DE SELECICON DE CIRCUITOS:
                                                            //---Resetear Matriz con FechasCircuitos Posibles--
                                                            ncir = 0;
                                                            //--FIn reseteo de matriz con FechasCircuitos Posibles--

                                                            //--- Resetear Matriz de Circuitos con FechasCircuitos seleccionados:
                                                            ncirsel = 0;
                                                            System.out.println("\nCircuitos seleccionados descartados!\n");

                                                            break;

                                                        case 1:

                                                            //Cada vez que se consulte los cirucitos que son posibles resetear
                                                            //matriz con FechaCircuitos Posibles para asi siempre obtener
                                                            //los circuitos posibles mas actualizados con base eb fecha que se pase
                                                            ncir = 0;
                                                            //Volver a inicializar en 0s matriz para asi garantizar el reseteo,
                                                            //si no se incluye esto ocurre problemas debido a la logica para saber
                                                            //si se hizo una nueva isnercion en tempcir ya que hace comparacion
                                                            //con valroes null y si no se restea pueden qeudar valore que causan error
                                                            poscir = new String[MAXCIT][2];

                                                            //Checar para cada una de los lugares los circuitos que son posibles
                                                            //con base en fecha
                                                            for (int i = 0; i < nlug; i++) {
                                                                //No hacer consulta para lugares eleiminados
                                                                if (lugares[i][0].equals("") || lugares[i][1].equals("")) {
                                                                    //No buscar en tabla porque no es valido ni el nombre
                                                                    //del lugar ni el de la ciudad
                                                                }//Fin if 7
                                                                else {
                                                                    //Buscar Idemmtificadores y fechas de salidas; pero como los lugares pueden
                                                                    //estar en mas de un circuito solamente guardar valores difernetes. Nota:
                                                                    //Si no se encuentra una Fecha e IDentificador para un lugar dado se va a
                                                                    //regresar un resultado con null entonces hay que preever ese caso
                                                                    //luagres[N][C]:NomLugar,Ciudad}
                                                                    //(Pais,Ciudad,Place,FechaSalida)
                                                                    tempcir = qryFechaCircuitos(pais, lugares[i][1], lugares[i][0], dateCircuit);

                                                                    //--------------------------------------------------------
                                                                    //System.out.println("Resultado de Query Circuitos");
                                                                    //Mostrarmatriz(tempcir,tempcir.length,2);  //<- Este print causa errores cuando
                                                                    //tempcir no encuentra Fecha de Circuitos en line anterior
                                                                    //--------------------------------------------------------

                                                                    //Ver que haya al menos una Fecha de Circuito valido
                                                                    if (tempcir[0][0] == null || tempcir[0][1] == null) {
                                                                        //No hubo FECHASCIRCUITO validas para ese lugar con base en la fecha actual
                                                                        //del sistema; Entonces no hacer nada
                                                                    }//Fin if 8
                                                                    else {
                                                                        //SI hubo FECHAS CIRCUITO Valida, agregar verificando que no este
                                                                        //considerada esa TUPLA DE FECHACIRCUITO

                                                                        //Determinar cuantas tuplas psibles se encontraron
                                                                        int numt = tempcir.length;

                                                                        //VERIFICAR PARA CADA TUPLA SI ESTA YA ESTAB EN MATRIZ DE FECHACIRCUITOS disponibles
                                                                        for (int j = 0; j < numt; j++) {
                                                                            //--------------------
                                                                            //System.out.println("Agregar sin repetir#"+j);
                                                                            //--------------------
                                                                            //(Matriz con Circuitos Posibles, numero tuplas en Matriz, Arreglo de tupla que
                                                                            //desea verificarse si ya esta en el arreglo de la matriz)
                                                                            poscir = Insertarmatriz(poscir, ncir, tempcir[j]);

                                                                            //Verificar si hubo insercion para saber si se aumenta el contador de tuplas
                                                                            //de Circuitos posibles
                                                                            //-------------------------------
                                                                            //System.out.println("Aumentar#CircuitosPsobles?Ver si hubo insercion");
                                                                            //System.out.println(poscir[ncir][0]+"== null?");
                                                                            //-------------------------------
                                                                            if (poscir[ncir][0] == null) {
                                                                                //No hubo insercion porque el valor ya estaba en la matriz
                                                                            }//Fin if 9
                                                                            else {
                                                                                //Si hubo insercion entonces auemntar el contador de Circuitos posibles
                                                                                ncir++;

                                                                                //---------
                                                                                //System.out.println("Num de Cirucitos posibles+1/Ahora="+ncir);
                                                                                //---------

                                                                            }//Fin else 10

                                                                        }//Fin for 2

                                                                    }//FIn else 8


                                                                }//Fin else 7

                                                            }//Fin for 1 Case 1 Circuitos

                                                            //Con base en la matriz de FechasCircuitos posbiles hacer query que muestre en
                                                            //pantalla la info de esta matriz
                                                            System.out.println("\nCircuitos disponibles para lugares seleccionados:");
                                                            System.out.println("Fecha actual: " + fecha.toString());
                                                            System.out.println("Fecha de llegada ultimo circuito seleccionado: " + dateCircuit.toString() + "\n");

                                                            //Ver si hubo al menos una FechaCircuito compatible con lugares y fecha
                                                            if (ncir > 0) {
                                                                //Si hubo al menos una FechaCircuito
                                                                System.out.println("\nID\tFecha Salida\tDias\tSalida\tLlegada\tPrecio");

                                                                //Para cada elemento de los CIrcuitos Posibles
                                                                for (int i = 0; i < ncir; i++) {

                                                                    //Poscir:{Identificador,FechaSalida} <-Posibles Circuitos
                                                                    //qryInfoAllFechaCircuitos(Indentificador,Fecha)
                                                                    qryInfoAllFechaCircuitos(poscir[i][0], poscir[i][1]);
                                                                    //---PARA CADA CIRCUITO HACER DESPLIEGUE DE TODAS LAS ETAPAS QUE TIENE--
                                                                    //Aqui o en otra opcion del menu<-mejor
                                                                }//Fin for 3

                                                            }//Fin if 11
                                                            else {
                                                                System.out.println("No se encontraron circuitos disponibles!");
                                                            }//Fin else 11
                                                            break;

                                                        case 2:
                                                            //Dado el identificador de un Circuito hacer despliegue detallado
                                                            //de las etapas y del precio de cada elemento para ayudar a tomar
                                                            //decision de que circuito(s) desea agregar el usuario
                                                            String temp = "";

                                                            System.out.print("Identificador de circuito: ");
                                                            temp = in.readLine();

                                                            System.out.println("Etapas de circuito mostradas en orden de recorrido");
                                                            System.out.println("\nNombre\tCiudad\tDias\n");
                                                            qryInfoEtapas(temp);

                                                            break;

                                                        case 3:
                                                            //Cuando se seleccione un circuito dependiendo de la duracion que tenga
                                                            //hacer que para la proxima vez que se consulte los circuitos disponibles
                                                            //los que se empalmen o sean de fechas posteriores ya no aparezcan
                                                            String ident = "";
                                                            String datsal = "";
                                                            String datlle = "";

                                                            System.out.print("Identificador de circuito: ");
                                                            ident = in.readLine();

                                                            //NOTA: si el usuario se equivoca al escoger la fecha de salida
                                                            //no va a haber nada que pueda avisarle que se equivoco
                                                            System.out.print("Fecha de salida(AAAA-MM-DD): ");
                                                            datsal = in.readLine();

                                                            //Llave a aguardar para saber que CIrcuito y en que fecha de salida
                                                            //escogio el usuario
                                                            String[] tupla = {ident, datsal};

                                                            //Ver que numero maximo de circuitos seleccionados por simulacion
                                                            //no se haya excedido
                                                            if (ncirsel < MAXCIT) {
                                                                Insertarmatriz(circuits, ncirsel, tupla);
                                                                //Aumentar contador de cirucitos seleccionados
                                                                ncirsel++;
                                                            }//Fin if 12
                                                            else {
                                                                System.out.println("Numero maximo de circuitos por simulacion excedido!");
                                                            }//Fine lse 12

                                                            //------ Eliminar de la matriz de circuitos posibles todos aquellos que ya no
                                                            //sean posibles por la fecha de llegada (FechaSalida+Duracion)
                                                            //de los nuevos circuitos que han sido seleccionados --------

                                                            datlle = qryGetFechaLlegada(ident, datsal);

                                                            //Ver que se haya seleccionado correctamente un identificador
                                                            if (datlle.equals("")) {
                                                                System.out.println("Identificador de circuito inexistente!");
                                                            }//Fin if 13
                                                            else {
                                                                //Asiganr la fecha de llegada de este circuito seleccionado como fecha para buscar
                                                                //cirucitos posibles.
                                                                dateCircuit = java.sql.Date.valueOf(datlle);

                                                            }//Fin else 13

                                                            break;

                                                        case 4:
                                                            //Eliminar circuito y fecha seleccionado de forma normal de
                                                            //la matriz de circuitos seleccionados-> circuits
                                                            String id = "";
                                                            String fsal = "";
                                                            String fsaltemp = "";

                                                            System.out.print("Identificador de circuito: ");
                                                            id = in.readLine();

                                                            System.out.print("Fecha de salida(AAAA-MM-DD): ");
                                                            fsal = in.readLine();

                                                            //Poner valores a ser eliminados en forma de tupla
                                                            String[] ttodel = {id, fsal};

                                                            //Obtener nuva matriz de Circuitos seleccionadaos despues
                                                            //de la eliminacio. Nota: si se dan valores para los atributos
                                                            //incorrectos no se hara la eliminacion
                                                            circuits = Eliminarmatriz(circuits, ncirsel, ttodel);

                                                            //Despues: asignar a dateCircuit la fecha de llegada
                                                            //mayor que haya quedado despues de la eliminacion y si se
                                                            //eliminan todos los circuitos seleccionados volver a asignarle fecha actual del sistema

                                                            //Por default asignar fecha actual de sistema como new dateCircuit
                                                            dateCircuit = fecha;

                                                            //Para cada cicuito seleccionado en la amtriz circuits que todavia
                                                            //no este eliminado hacer query para determinar fecha de llegada
                                                            //y si este es mayor que el datecircuit actual asignarlo como nuevo
                                                            //datecircuit
                                                            for (int k = 0; k < ncirsel; k++) {
                                                                if (!circuits[k][0].equals("")) {    //No tiene un Identificador vacio -> No ha sido eliminado
                                                                    fsaltemp = qryGetFechaLlegada(circuits[k][0], circuits[k][1]);

                                                                    //Si La fecha de salida obtenida del query esta despues que la dateCircuit actual
                                                                    if (java.sql.Date.valueOf(fsaltemp).after(dateCircuit)) {
                                                                        //Se ha encontrado una nueva dateCircuit
                                                                        dateCircuit = java.sql.Date.valueOf(fsaltemp);

                                                                    }//FIn if 15

                                                                }//Fin if 14

                                                            }//Fin for k

                                                            break;

                                                        case 5:
                                                            //Print ncirsel
                                                            System.out.print("\nCircuitos seleccionados:");
                                                            System.out.println("ID\tFecha de Salida\n");
                                                            //Imprimir el numero de lugares seleccionados, el 2
                                                            //hace referencia a impimir solo 2 atributos por
                                                            //tupla("Identificador, FechaSalida")
                                                            Mostrarmatriz(circuits, ncirsel, 2);
                                                            break;

                                                        case 6:
                                                            //Antes de avanzar a hoteles y cuartos, ver si en tabla
                                                            //RESERVACIONCIRCUITO hay todavia cupo para ese circuito
                                                            //en esa fecha(esto se hace hasta reservacion)
                                                            int index = 0;
                                                            for (int r = 0; r < ncirsel; r++) {
                                                                String[][] etapas = ResultQuery("SELECT fechacircuito.Identificador, FechaSalida, nbPersonas, Orden, Ciudad, Pais, Duracion FROM" +
                                                                        " fechacircuito, etapa WHERE fechacircuito.Identificador = " + circuits[r][0] + " AND etapa.Identificador = " + circuits[r][0] +
                                                                        " AND fechacircuito.FechaSalida = '" + circuits[r][1] + "'");

                                                                //Solo calcular la sim de hotel para lugares que si tengan valore validos
                                                                //si el ResultQuery no regresa nada etapas[0][0] entonces no llamara metodo que simula
                                                                //un hotel por lugar
                                                                if (etapas[r][0] != null) {
                                                                    index = simHotel(etapas[r][1], etapas, 0, index);
                                                                }
                                                            }//Fin for

                                                            //En casso de que etapas[0][0] = null se haria una simulacion pero no se tendrian hoteles seleccionados
                                                            //por lo que para el id de simulacion correspodneinte no habria ninguna tupla en tabla SIMULACIONHOTEL
                                                            Mostrarmatriz(hotelesMtrx, hotelesMtrx.length, 6);
                                                            //Indicar que simulacion ha acabado correctamente
                                                            correctsim = 0;

                                                            //Salir de tods los do while
                                                            ioe = 0;
                                                            iol = 0;
                                                            ioc = 0;
                                                            iop = 0;

                                                            break;

                                                        default:
                                                            System.out.println("Opcion no valida");
                                                            break;

                                                    }//Fin swithc 3 Circuito(Etapas)

                                                } while (ioe != 0); //Fin do while #3 (Circuitos)

                                                break;

                                            default:
                                                System.out.println("Opcion no valida");
                                                break;

                                        }//Fin switch 2 Lugares(LugarAVisitar)

                                    }
                                    while (iol != 0); //Fin do while 2 Lugares(lugarAvisitar)

                                    break;

                                default:
                                    System.out.println("Opcion no valida");
                                    break;

                            }//Fin switch 1 Ciudades


                        } while (ioc != 0); //Fin do while #2

                    }//Fin else 3

                }//Fin if 2

                if (iop == 0) {
                    if (correctsim != 0) {
                        //Hacer aqui un rollback de todo lo que se haya hecho
                        System.out.println("\nSimulacion descartada!\n");
                    }//Fin if

                }//Fin if 3

            }//FIn else 1

        } while (iop != 0);//Fin do while #2

        //Si correctsim sale con -1 entonces sabemos que nada debe
        //ser guardado en tonces todo lo que viene a continucacion se brica
        //y solo se hace un rollback por si las dudas

        if (correctsim == 0) {//If guardar simulacion en tablas

            //Precio total de todos los circuitos
            int totalcircui = 0;
            //Precio total hotles
            int totalhot = 0;
            //Precio total
            int preciotot = 0;
            //ResultQuery precio circuito;
            String[][] precirc;
            //ResultSet para guardar Metadatos de insert into en tabla Simulacion
            //Id de Simulacion generado por el autoincrement
            ResultSet idgen;
            //Id de Simulacion
            idsim = 0;

            //------------------------------------------------------------------
            //Variable que contiene fechaSalida global de la Simulacion y esta
            //inicializado con la fecha de llegada actual de dateCircuit
            Date fsalglobal = dateCircuit;

            //Obtener FechaSalida de toda la simulacion calculando la fechaDeSalida
            //que sea la primera de todos los cirucitos seleccionados por el usuario
            for (int idx = 0; idx < ncirsel; idx++) {
                if (circuits[idx][1].equals("")) {
                    //No comparar fecha salida porque esta vacia
                }//Fin if 20
                else {
                    //Si la fecha de salida que se esta leyendo esta antes
                    //que la fsalglobal actual setablecerla como nueva fsalglobal
                    if (java.sql.Date.valueOf(circuits[idx][1]).before(fsalglobal)) {
                        fsalglobal = java.sql.Date.valueOf(circuits[idx][1]);
                    }//Fin if 21

                }//FIn else 20

            }//Fin for FechaSalida global de Simulacion (20)
            //------------------------------------------------------------------

            //Hacer lecturas sobre informacion a la que ya se le ha hecho
            //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
            //Si puede causar lecturas fantasmas si algun atributo es añadido en
            //alguna tabla
            conn.setTransactionIsolation(4);

            try {
                //------ Calcular costo total de precio de todos los circuitos -----
                //Calcular costo de cada circuito multiplicado por numero de personas
                for (int idc = 0; idc < ncirsel; idc++) {
                    if (circuits[idc][0].equals("")) {
                        //No hacer nada porque no es un circuito existente
                    }//Fin if 21
                    else {
                        precirc = ResultQuery("select Precio "
                                + "from CIRCUITO "
                                + "where Identificador = '" + circuits[idc][0] + "'; ");

                        //Convertir Result query a int y preever que no se regrese null
                        if (!(precirc[0][0] == null)) {
                            //Si se regreso un numero sumarlo al total de ciruci
                            totalcircui = totalcircui + Integer.parseInt(precirc[0][0]);

                        }//Fin if

                    }//Fin else 21

                }//Fin for 21
                //El precio total de circuitos multiplicarlo por el numero
                //de personas
                totalcircui = totalcircui * numper;
                //------- Fin costo total precio de todos los circuitos ---------

                //------- Calcular costo total de hoteles ----------------------
                //Costo hotel por dia = PrecioCuarto + (PrecioDesayuno*numPerosonas)
                for (int indexCostos = 0; indexCostos < numberHoteles; indexCostos++) {
                    totalhot = totalhot + Integer.parseInt(hotelesMtrx[indexCostos][6]) + (Integer.parseInt(hotelesMtrx[indexCostos][7]) * numper);
                }
                //System.out.println("\t\t" + totalhot);
                //------- FIN calculo costo de hoteels--------------------------

                //CALCULAR PRECIO TOTAL
                preciotot = totalcircui + totalhot;
                //------- Fin calcular costo total-------------------

                //-----INICIAR EL GUARDADO DE DATOS DE SIMULACION EN TABLAS CORRESPONDIENTES----------

                //-------- BAJAR TODO TABLA SIMULACION -------------------------
                //y obtener el id de la simulacion generado por el autoincrement
                stmt.executeUpdate("Insert into SIMULACION (Id,FechaSalida,FechaLlegada,Costo,Vigencia,NumPersonas,Pais) "
                                + "values (" + Idusr + ",'" + fsalglobal.toString() + "','" + dateCircuit.toString() + "'," + preciotot + ",DATE_ADD('" + fecha + "',INTERVAL 2 day)," + numper + ",'" + pais + "') ",
                        Statement.RETURN_GENERATED_KEYS);

                idgen = stmt.getGeneratedKeys();

                //Obtener de los metadatos la llave de la simulacion
                while (idgen.next()) {
                    //Guardar como Integer el valor del atributo/COLUMNA 1
                    idsim = idgen.getInt(1);
                }//Fin while 1
                //------- FIN BAJAR TODA TABLA SIMULACION ----------------------

                //---BAJAR CIRCUITOS SELECICONADOS EN TABLA RESERVACIONCIRCUITO----

                //Para Cada circuito seleccionado agregarlo a la tabla DE SIMULACION
                for (int i = 0; i < ncirsel; i++) {
                    if (circuits[i][0].equals("")) {

                    }//Fin if 22
                    else {
                        //Guardar este circuito en tabla
                        stmt.executeUpdate("Insert into SIMULACIONCIRCUITO (NumSim,Identificador,FechaSal) values (" + idsim + ",'" + circuits[i][0] + "','" + circuits[i][1] + "');");

                    }//FIn else 22

                }//Fin for 1
                //--FIN BAJAR CIRCUITOS SELECCIONADOS EN TABLA RESERVACIONCIRCUITO

                //--- BAJAR CUARTOS DE HOTEL SELECCIOANDOS EN TABLA RESERVACIONHOTEL

                for (int p = 0; p < numberHoteles; p++) {
                    stmt.executeUpdate("INSERT INTO simulacionhotel (NumSim, NomHotel, NomCiudad, Pais, NumCuarto, FechaInicio, FechaFinal, NumOcupantes) " + "VALUES (" +
                            " " + idsim + ",'" + hotelesMtrx[p][0] + "','" + hotelesMtrx[p][1] + "','" + hotelesMtrx[p][2] + "'," + hotelesMtrx[p][3] + ",'" + hotelesMtrx[p][4] + "','" + hotelesMtrx[p][5] + "'," + numper + ")");
                }
                //--- FIN  BAJAR CUARTOS DE HOTEL EN RESERVACIONHOTEL

                System.out.println("\nSimulacion calculada exitosamente!\n");
                System.out.println("ID de simulacion: " + idsim + "\n");
                System.out.println("Costo total boletos de circuitos: " + totalcircui);
                System.out.println("Costo total cuartos de hotel y desayunos: " + totalhot);
                System.out.println("Costo total simulacion para " + numper + " personas: $ " + preciotot);
                System.out.println("Consultar desglose detallado de simulacion en: 3)Reservacion-> 2)Consultar simulaciones");
                System.out.println();
                //Todo se realizo correctamente hacer commit

                conn.commit();  //Fin e inicio de nueva transaccion

            }//FIn try
            catch (SQLException sqle) {
                sqle.printStackTrace();
                //Hubo algun error hacer rollback
                System.out.println("\nDatos seleccionados incorrectos. Simulacion Descartada!");

                conn.rollback(); //Fin e inicio de nueva transaccion

            }//Fin catch


        }//Fin if para guardar info simulacion en tablas


        //Reinicar valores de simulacion hotel para futuras simulacioens consecutivas
        numberHoteles = 0;
        hotelesMtrx = new String[100][8];


        //Final de Transaccion, Entonces cerrar todo
        //Cerrar statement y la conexion (Los resultset usados se cierran
        //respectivamente al usar los metodos query y ReturnQuery)
        //close();

        //SIMULACION SIEMPRE REGRESA -1; VER SI ES CONVENIENTE QUE REGRESE EL
        //ID de Simulacion (idsim)
        return numsim;

    }//Fin metodo Simulacion

    //--------------------------------------------------------------------------
    //Metodo que dado un ID de cliente e ID de Simulacion busca en tabla SIMULACION
    //QUE EXISTA; si no existe fin; SI EXISTE: validar cupo en fecha para ese
    //circuito y disponibilidad de hoteles en fechas escogidas; Regresa -1 si
    //simulacion no se realiza o regresa num > 0 si se realiza la validacion
    public int ValidarSimulacion(int idu, int idsim) throws SQLException {
        String[][] ressim, rescir, reshot;
        //Matriz extra para query de reservacion hotel que verifica que la
        //capacidad del cuarto sea suficinte para personas en la simulacion
        String[][] rescap;

        int simobtained = -1;
        //Metadatos de Id de RESERVACION generado por el autoincrement
        ResultSet mdresgen;
        //Matriz que se llena una vez que se valido la simulacion para saber
        //cuantos circuitos y cuales circuitos hay que pasar a RESERVACIONCIRCUITO
        String[][] totcirs;

        //mATRIZ QUE SE LLENA una vez que se valido simulacion para saber
        //cuantos cuartos de hotel y caules hay que pasar a RESERVACIONHOTEL
        String[][] tothot;

        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo
        conn.setTransactionIsolation(4);

        try {
            ressim = ResultQuery("select Id from SIMULACION where Id = " + idu + " and NumSim = " + idsim + ";");

            //Si se encontro una simulacion
            if (ressim[0][0] != null) {
                //++++++INICIO VALIDACION TODOS LOS CIRCUITOS+++++
                //1) Obtener fecha y el id de cada circuito selected
                //rescir = ResultQuery("select Id from ")
                //2) Obtener para cada Fecha circuito el total de ocupantes ya
                //reservados
                //Si atriubto vigencia = null significa que la simulacion ya fue
                //validada como reservacion
                //3) Si el total de ocupantes ya reservados PARA CADA UNA DE LAS Fecha circuito
                //es menor o igual validar SIMULACION CIRCUITOS; SI PARA ALGUNA NO SE CUMPLE
                //FIN SIMULACION

                /*CONSULTA QUE REGRESA AQUELLOS FECHAS E IDENTIFICADORES DE CIRCUITOS
                PARA UNA SIMULACION ESPECIFICA EN LA CUAL YA NO ES POSIBLE HACER
                RESERVACION PORQUE EL CUPO YA SE LLENO*/

                String consulta =
                        "select R1.Id, R1.Fs " +

                                "from " +
                                "(select SC.Identificador Id, SC.FechaSal Fs, FC.nbPersonas Limite , SM.NumPersonas nper " +
                                "from SIMULACIONCIRCUITO SC, FECHACIRCUITO FC , SIMULACION SM " +
                                "where SC.Identificador = FC.Identificador and SC.FechaSal = FC.FechaSalida and SC.NumSim = " + idsim + " and SM.Numsim = SC.Numsim) R1, " +

                                "(select SUM(NumPersonas) Actual, RC.Identificador Id ,RC.FechaSal Fs " +
                                "from RESERVACIONCIRCUITO RC, FECHACIRCUITO FC, RESERVACION R " +
                                "where RC.Identificador = FC.Identificador and RC.FechaSal = FC.FechaSalida and RC.NumRes = R.NumRes " +
                                "group by RC.FechaSal, RC.Identificador) R2 " +

                                "where R1.Id = R2.Id and R1.Fs = R2.Fs and R1.Limite < R2.Actual+R1.nper";

                rescir = ResultQuery(consulta);

                if (rescir[0][0] != null) {
                    System.out.println("Reservacion no completada! No hay cupo en los siguientes circuitos en las fechas seleccionadas: ");
                    //Matriz/NumTuplas/2Atributos[Identifiacador,FechaSalida]
                    Mostrarmatriz(rescir, rescir.length, 2);
                    System.out.println("\nRealizar una simulacion con un circuito y/o fecha de salida diferentes!");

                    System.out.println();

                }//Fin if 2
                else {
                    //Si se regresa vacio quiere decir que los circuitos son validos
                    //y se puede proceder a copiar datos de SIMULACIONCUIRCUITO EN RESERVACIONCIRCUITO Y a
                    //una validacion de los hoteles
                    //++++++FIN VALIDACION TODOS LOS CIRCUITOS++++++++

                    //++++++INICIO VALIDACION TODOS LOS HOTELES+++++++
                    //QUERY 1: SABER SI LA RESERVACION DE ALGUNO DE LOS CUARTOS SE EMPALMA
                    //CON UNA RESERVACION QUE YA FUE VALIDADA
                    String consulhotel = "select SH.NomHotel, SH.NomCiudad, SH.NumCuarto, SH.FechaInicio, SH.FechaFinal " +
                            "from SIMULACIONHOTEL SH, RESERVACIONHOTEL RH " +
                            "where SH.NumSim = " + idsim + " and SH.NomHotel = RH.NomHotel and SH.NomCiudad = RH.NomCiudad " +
                            "and SH.Pais = RH.Pais and SH.NumCuarto = RH.NumCuarto and (SH.FechaInicio between " +
                            "RH.FechaInicio and RH.FechaFinal or SH.FechaFinal between RH.FechaInicio and RH.FechaFinal);";

                    reshot = ResultQuery(consulhotel);

                    if (reshot[0][0] != null) {
                        System.out.println("Reservacion no completada! Los siguientes cuartos ya han sido reservados en las fechas seleccionadas: ");
                        System.out.println("\nNombre\tCiudad\t#Cuarto\tInicio\tFin");
                        Mostrarmatriz(reshot, reshot.length, 5);

                        System.out.println("\nRealizar una simulacion con cuarto y/o hotel diferentes!");
                        System.out.println();

                    }//Fin if a
                    else {
                        //Se regreso vacio entonces no hubo ninguna reservacion
                        //que impidiera la reservacion de esta  POR FALTA DE DISPONIBILDIAD

                        //QUERY 2: SABER SI LA CAPACIDAD DEL CUARTO SELECCIONADO ES SUFICIENTE
                        //PARA ALMACENAR A LAS PERSONAS CONSIDERADAS PARA LA SIMULACION
                        String consulcapcuar = "select SH.NomHotel, SH.NomCiudad, SH.NumCuarto, C.Capacidad " +
                                "from SIMULACIONHOTEL SH, CUARTO C " +
                                "where NumSim = " + idsim + " and SH.NomHotel = C.Nombre and SH.NomCiudad = C.Ciudad and " +
                                "SH.NumCuarto = C.NumCuarto and Capacidad < NumOcupantes;";


                        rescap = ResultQuery(consulcapcuar);

                        if (rescap[0][0] != null) {
                            System.out.println("Reservacion no completada! Los siguientes cuartos tienen una capacidad menor a la que solicito: ");
                            System.out.println("\nNombre\tCiudad\t#Cuarto\tCapacidad");
                            Mostrarmatriz(rescap, rescap.length, 4);

                            System.out.println("\nRealizar una simulacion con un cuarto con mayor capacidad");

                            System.out.println();

                        }//Fin if b
                        else {
                            //Se regreso vacio entonces todos las reservaciones de cuartos
                            //han sido validadas. PROCEDER CON MIGRRACION DE DATOS DE TABLAS
                            //DE SIMULACION A RESERVACION

                            //Si los hoteles tambien se pueden reservar proceder a:
                            //1)Crear nueva tupla en RESERVACION con copia de info DE SIMULACION y obtener el ID de
                            //RESERVACION generado por el autoincremento(NumRes)
                            consulta = "insert into RESERVACION (Id,FechaSalida,FechaLlegada,Costo,NumPersonas,Pais) "
                                    + "select Id, FechaSalida, FechaLlegada, Costo, NumPersonas, Pais "
                                    + "from SIMULACION "
                                    + "where NumSim = " + idsim + ";";

                            stmt.executeUpdate(consulta, Statement.RETURN_GENERATED_KEYS);

                            //GUARDAR LOS METADATOS DE LA llave autogenerada
                            mdresgen = stmt.getGeneratedKeys();

                            //Obtener de los metadatos la llave de la RESERVACION
                            while (mdresgen.next()) {
                                //Guardar como Integer el valor del atributo/COLUMNA 1
                                //Se guarda el ID de la Simulacion que se ha obtenido
                                simobtained = mdresgen.getInt(1);
                            }//Fin while 1

                            //2)Copiar info de SIMULACIONCUIRCUITO EN RESERVACIONCIRCUITO para la SIMULACION CORRESPONDIENTE
                            consulta = "select Identificador, FechaSal "
                                    + "from SIMULACIONCIRCUITO "
                                    + "where NumSim = " + idsim + "; ";

                            totcirs = ResultQuery(consulta);

                            if (totcirs[0][0] == null) {
                                System.out.println("Reservacion sin circuitos. Reservacion Cancelada!\n");
                            }//fin if 3
                            else {
                                //----------
                                //System.out.println("Consultar circuitos de simulacion a ser guardados en reservacion");
                                //Matriz/NumTuplas/2Atributos[Identifiacador,FechaSalida]
                                //Mostrarmatriz(totcirs,totcirs.length,2);
                                //----------
                                //Insertar en tabla RESERVACIONCIRCUITO cada Circuito
                                //que conforma esta reservacion
                                for (int i = 0; i < totcirs.length; i++) {
                                    stmt.executeUpdate("insert into RESERVACIONCIRCUITO(NumRes,Identificador,FechaSal) "
                                            + "values (" + simobtained + ",'" + totcirs[i][0] + "','" + totcirs[i][1] + "');");

                                }//Fin for 1

                                //3) Copiar info de SIMULACIONHOTEL EN RESERVACIONHOTEL PARA CADA HOTEL CORRESPONDIENTE++++
                                tothot = ResultQuery("select NomHotel, NomCiudad, Pais, NumCuarto, FechaInicio, FechaFinal, NumOcupantes " +
                                        "from SIMULACIONHOTEL " +
                                        "where NumSim = " + idsim + "; ");

                                if (tothot[0][0] == null) {
                                    System.out.println("Reservacion sin cuartos. Reservacion cancelada!\n");
                                }//Fin if 4
                                else {
                                    //Insertar en tabla RESERVACIONHOTEL cada Cuarto de hotel
                                    //que conforma esta reservacion

                                    for (int j = 0; j < tothot.length; j++) {
                                        stmt.executeUpdate("insert into RESERVACIONHOTEL(NumRes, NomHotel, NomCiudad, Pais, NumCuarto, FechaInicio, FechaFinal, NumOcupantes) "
                                                + "values (" + simobtained + ",'" + tothot[j][0] + "','" + tothot[j][1] + "','" + tothot[j][2] + "', " + tothot[j][3] + " ,'" + tothot[j][4] + "'"
                                                + ",'" + tothot[j][5] + "', " + tothot[j][6] + " ); ");

                                    }//Fin for 2


                                    //4)***ELIMINAR SIMULACION YA QUE FUE RESERVADO; ELMINADO TODA LA INFROMACION RELACIONADA
                                    //TANTO EN SIMULACIONCIRUCITO, SIMULACIONHOTEL COMO RESERVACION
                                    stmt.executeUpdate("delete from SIMULACION where NumSim = " + idsim + ";");

                                }//Fin else 4

                            }//Fin else 3

                        }//Fin else b

                    }//Fin else a
                    //++++++FIN VALIDACION TODOS LOS HOTELES++++++++++

                    //Si no hay cupo en hotles; Indicar que no se completa reservacion


                }//Fin else 2

            }//Fin if
            else {
                System.out.println("Simulacion no valida. Consulte sus simulaciones realizadas!\n");
            }//Fin else  1

            //No hubo error hacer commit
            conn.commit();
            numberHoteles = 0;
            hotelesMtrx = new String[100][8];

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();

            //Hubo algun error hacer rollback
            conn.rollback(); //Fin e inicio de nueva transaccion

        }//Fin catch


        return simobtained;
    }//Fin metodo validarSimulacion

    //--------------------------------------------------------------------------
    //Metodo que checa Clientes conocidos en la base de datos Cliente y ve si ya hay una entrada
    //para el Usuario con el ID actual regresa -1 en caso de que el usuario
    //todavia no este registrado como cliente
    public int VerificarCliente(int iduser) throws SQLException {
        String[][] result;
        int idobt = -1;

        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo
        conn.setTransactionIsolation(4);

        try {
            result = ResultQuery("select Id from CLIENTE where Id = '" + iduser + "';");

            //Si se encontro un ID
            if (result[0][0] != null) {
                idobt = Integer.parseInt(result[0][0]);
            }//Fin if

            //No hubo error hacer commit
            conn.commit();

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();

            //Hubo algun error hacer rollback
            conn.rollback(); //Fin e inicio de nueva transaccion

        }//Fin catch

        return idobt;

    }//Fin metodo VerificarCliente

    //--------------------------------------------------------------------------
    //Query que despliega toda las reservaciones de un cliente
    public void MostrarResCliente(int id) throws SQLException {
        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo
        conn.setTransactionIsolation(4);

        try {
            query("select NumRes "
                    + "from RESERVACION "
                    + "where Id = " + id + ";");

            //No hubo error, hacer commit
            conn.commit(); //Fin e inicio de nueva transaccion

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();

            //Hubo algun error hacer rollback
            conn.rollback(); //Fin e inicio de nueva transaccion

        }//Fin carch

    }//Fin metodo MostrarSimCliente

    //--------------------------------------------------------------------------
    //Query que despliega toda las simulaciones de un usuario
    public void MostrarSimsUsuario(int id) throws SQLException {
        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo
        conn.setTransactionIsolation(4);

        try {
            query("select NumSim "
                    + "from SIMULACION "
                    + "where Id = " + id + ";");

            //No hubo error, hacer commit
            conn.commit(); //Fin e inicio de nueva transaccion

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();

            //Hubo algun error hacer rollback
            conn.rollback(); //Fin e inicio de nueva transaccion

        }//Fin carch

    }//Fin metodo MostrarSimUsuario

    //--------------------------------------------------------------------------
    //Metodo que despliega la informacion de todo el contenido de una Simulacion
    //Primero verificadno que la simulacion sea del usuario correspondiente
    public void MostrarDetallesSim(int idsim, int idusr) throws SQLException {
        String[][] ressim;
        String[][] circuitos;

        String qryhot = ""; //Cadena para poner query de cuartos de hotel

        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo p/e:tupla o columna
        conn.setTransactionIsolation(4);

        try {

            ressim = ResultQuery("select Id from SIMULACION where Id = " + idusr + " and NumSim = " + idsim + ";");

            //Si se obtuvo una simulacion valida par el usuario
            if (ressim[0][0] != null) {
                //Hacer el despliegue de la info
                System.out.println("$Total\tSalida\t\tLlegada\t   Personas\tPais\n");
                query("select Costo,FechaSalida,FechaLlegada,NumPersonas,Pais from SIMULACION where NumSim = " + idsim + ";");

                circuitos = ResultQuery("select Identificador from SIMULACIONCIRCUITO where NumSim = " + idsim + " order by FechaSal asc;");

                //Para cada cirucito mostrar su info asegurandose que haya circuitos en la simulacion
                System.out.println("\nCircuitos en la simulacion: \n");
                System.out.println("-----------------------------------------------");

                for (int i = 0; i < circuitos.length; i++) {
                    if (circuitos[i][0] != null) {
                        System.out.println("Circuito:" + circuitos[i][0] + "\n");
                        System.out.println("C.Salida\tC.Lleagada\tDias\tPrecio(P/persona)");
                        query("select CiudadSalida,CiudadLlegada,Duracion,Precio from CIRCUITO "
                                + "where Identificador = '" + circuitos[i][0] + "';");

                        System.out.println();

                        System.out.println("Etapas del circuito: \n");
                        System.out.println("Nombre\tCiudad\tDuracion\t$");
                        //Mostrar todas las etapas del cirucito
                        query("select NombreLugar,E.Ciudad,Duracion,Precio "
                                + "from ETAPA E, LUGARAVISITAR L "
                                + "where Identificador = '" + circuitos[i][0] + "' and E.NombreLugar = L.Nombre "
                                + "and E.Ciudad = L.Ciudad and E.Pais = L.Pais "
                                + "order by Orden;");

                        System.out.println("-----------------------------------------------");
                    }//Fin if 1

                }//Fin for 1

                //Hacer despliegue de la informacion de los hotles
                System.out.println("\nHoteles considerados para la simulacion: \n");
                System.out.println("Hotel\t\tCiudad\t#Cuarto\tInicio\t\tFinal\t\t$Cuarto\t$Desayuno(P/persona)\n");

                qryhot = "select SH.NomHotel, SH.NomCiudad, SH.NumCuarto, FechaInicio, FechaFinal, PrecioCuarto, PrecioDesayuno "
                        + "from SIMULACIONHOTEL SH, HOTEL H "
                        + "where SH.NumSim = " + idsim + " and SH.NomHotel = H.Nombre and SH.NomCiudad = H.Ciudad and "
                        + "SH.Pais = H.Pais "
                        + "order by FechaInicio asc, FechaFinal asc; ";

                query(qryhot);

                System.out.println();
            }//FIn if 1
            else {
                System.out.println("Simulacion no valida. Consulte sus simulaciones realizadas!\n");
            }//Fin else 1

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();
            //HUBO ERROR HACER ROLLBACK
            conn.rollback(); //Fin e inicio transaccion
        }//Fin catch

    }//Fin metodo MostrarDetalleSim

    //--------------------------------------------------------------------------
    //Metodo que despliega la informacion de todo el contenido de una RESERVACION
    //Primero verificadno que la RESERVACION sea del usuario correspondiente
    //METODO copia de MostrarDetallesSim que adicionamlmente hace verificacion
    //del decuento de trabajador desucento del 15%
    public void MostrarDetallesRes(int idres, int idusr) throws SQLException {
        String[][] ressim;
        String[][] circuitos;

        //Contenedor de id de cliente para saber si el usuario es cliente conocido
        String[][] cliente;

        String qryhot = ""; //Cadena para poner query de cuartos de hotel

        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo p/e:tupla o columna
        conn.setTransactionIsolation(4);

        try {

            ressim = ResultQuery("select Id from RESERVACION where Id = " + idusr + " and NumRes = " + idres + ";");

            //Si se obtuvo una simulacion valida par el usuario
            if (ressim[0][0] != null) {
                //Hacer el despliegue de la info
                System.out.println("$Total\tSalida\t\tLlegada\t   Personas\tPais\n");
                query("select Costo,FechaSalida,FechaLlegada,NumPersonas,Pais from RESERVACION where NumRes = " + idres + ";");

                circuitos = ResultQuery("select Identificador from RESERVACIONCIRCUITO where NumRes= " + idres + " order by FechaSal asc;");

                //Para cada cirucito mostrar su info asegurandose que haya circuitos en la simulacion
                System.out.println("\nCircuitos en la reservacion: \n");
                System.out.println("-----------------------------------------------");

                for (int i = 0; i < circuitos.length; i++) {
                    if (circuitos[i][0] != null) {
                        System.out.println("Circuito:" + circuitos[i][0] + "\n");
                        System.out.println("C.Salida\tC.Lleagada\tDias\tPrecio(P/persona)");
                        query("select CiudadSalida,CiudadLlegada,Duracion,Precio from CIRCUITO "
                                + "where Identificador = '" + circuitos[i][0] + "';");

                        System.out.println();

                        System.out.println("Etapas del circuito: \n");
                        System.out.println("Nombre\tCiudad\tDuracion\t$");
                        //Mostrar todas las etapas del cirucito
                        query("select NombreLugar,E.Ciudad,Duracion,Precio "
                                + "from ETAPA E, LUGARAVISITAR L "
                                + "where Identificador = '" + circuitos[i][0] + "' and E.NombreLugar = L.Nombre "
                                + "and E.Ciudad = L.Ciudad and E.Pais = L.Pais "
                                + "order by Orden;");

                        System.out.println("-----------------------------------------------");
                    }//Fin if 1

                }//Fin for 1

                //Hacer despliegue de la informacion de los hotles
                System.out.println("\nHoteles considerados para la reservacion: \n");
                System.out.println("Hotel\t\tCiudad\t#Cuarto\tInicio\t\tFinal\t\t$Cuarto\t$Desayuno(P/persona)\n");

                qryhot = "select RH.NomHotel, RH.NomCiudad, RH.NumCuarto, FechaInicio, FechaFinal, PrecioCuarto, PrecioDesayuno "
                        + "from RESERVACIONHOTEL RH, HOTEL H "
                        + "where RH.NumRes = " + idres + " and RH.NomHotel = H.Nombre and RH.NomCiudad = H.Ciudad and "
                        + "RH.Pais = H.Pais "
                        + "order by FechaInicio asc, FechaFinal asc; ";

                query(qryhot);

                System.out.println();

                //Verificar si el usuario es cliente y después ver si es empleado
                //para mostrar descuento
                cliente = ResultQuery("select Id,Empleado from CLIENTE where Id = " + idusr + ";");
                if (cliente[0][0] != null) {
                    if (cliente[0][1].equals("Si")) {
                        System.out.println("Empleado de AgenciaPonchito encontrado!");
                        System.out.print("Descuento preferncial para trabajador: $");

                        query("select Costo-(Costo*.15) from RESERVACION where Id = " + idusr + " and "
                                + "NumRes = " + idres + ";");

                        System.out.println();

                    }//Fin if 3

                }//Fin if 2

            }//FIn if 1
            else {
                System.out.println("Reservacion no valida. Consulte sus reservaciones realizadas!\n");
            }//Fin else 1

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();
            //HUBO ERROR HACER ROLLBACK
            conn.rollback(); //Fin e inicio transaccion
        }//Fin catch

    }//Fin metodo MostrarDetalleSim

    //--------------------------------------------------------------------------
    //Metodo que obtiene los datos actuales del cliente de los siguientes atributos
    //en este orden:Tipo,Direccion/FormaPago/Esempleado
    public String[][] qryGetClientData(int iduser) throws SQLException {
        String[][] result;

        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo
        conn.setTransactionIsolation(4);

        try {

            result = ResultQuery("select Tipo,Dirc,Pago,Empleado from CLIENTE where Id = " + iduser + ";");
            //No hubo error hacer commit
            conn.commit(); //Fin e inicio de transaccion

        }//Fin try
        catch (SQLException sqle) {
            sqle.printStackTrace();
            //Hubo error hacer rollback
            conn.rollback(); //Fin e inicio de transaccion
            System.out.println("Error obteniendo datos del Cliente\n");
            //NULL
            result = new String[0][0];
        }//Fin catch

        return result;

    }//Fin metodo qryGetClientData

    //--------------------------------------------------------------------------
    //Metodo que hace actualizacion de los atributos Tipo,Dic/FormaPago/Empleado
    //para un CLIENTE;
    public void UpdateDatosCliente(int idusr, String tipo, String dir, String pago, String emple) throws SQLException {
        //Como se va a hacer update pero de tuplas que ya hay para esta transaccion
        //es idoneo el nivel de aisalmiento 4 que solo permite lecturas fanrasmas
        //pero como aqui no va a haber isnerciones parece que no habria conflcito
        //entre actualizaciones
        conn.setTransactionIsolation(4);

        try {
            stmt.executeUpdate("update CLIENTE "
                    + "set Tipo = '" + tipo + "', Dirc = '" + dir + "', Pago = '" + pago + "', Empleado = '" + emple + "' "
                    + "where Id = " + idusr + ";");

            //No hubo error hacer commit
            conn.commit(); //Fin e inicio de transaccion

        }//FIn try
        catch (SQLException sqle) {
            sqle.printStackTrace();
            //Hubo error hacer rollback
            conn.rollback(); //Fin e inicio de transaccion
        }//FIn catch

    }//Fin metodo UpdateDatosCLiente

    //--------------------------------------------------------------------------
    //Query que despliega toda la informacion de un cliente conocido de tabla CLIENTE
    //y que tambien despliega el nombre contenido en tabla USUARIO
    public void MostrarDatosCliente(int id) throws SQLException {
        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo
        conn.setTransactionIsolation(4);

        try {
            query("select Nombre, Tipo,AnioRegistro,Dirc,Pago,Empleado "
                    + "from USUARIO, CLIENTE "
                    + "where USUARIO.Id = CLIENTE.Id and USUARIO.Id = " + id + ";");

            //No hubo error, hacer commit
            conn.commit(); //Fin e inicio de nueva transaccion

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();

            //Hubo algun error hacer rollback
            conn.rollback(); //Fin e inicio de nueva transaccion

        }//Fin carch

    }//Fin metodo MostrarDatosCliente

    //--------------------------------------------------------------------------
    //A partir de un usuario crear Cliente conocido con info requerida
    public void InsertarCliente(int id, String tipo, Date fecha, String dir, String pago, String emp) throws SQLException {
        //Obtener solo el año de la fecha que recibe como argumento
        Calendar cal = Calendar.getInstance();
        //Pasar datos fecha de tipo Date a objeto de clase Clanedar
        cal.setTime(fecha);
        int year = cal.get(Calendar.YEAR);

        //Hacer lecturas sobre informacion a la que ya se le ha hecho
        //commit y sobre la cual no se pueden hacer modificaciones(sin embargo)
        //Si puede causar lecturas fantasmas si algun atributo es añadido en
        //la tabla USUARIo
        conn.setTransactionIsolation(4);

        try {
            stmt.executeUpdate("insert into CLIENTE(Id,Tipo,AnioRegistro,Dirc,Pago,Empleado) values ("
                    + "" + id + ",'" + tipo + "'," + year + ",'" + dir + "','" + pago + "','" + emp + "') ; ");

            //No hubo error hacer commit
            conn.commit();

        }//Fin try
        catch (SQLException sql) {
            sql.printStackTrace();

            //Hubo algun error hacer rollback
            conn.rollback(); //Fin e inicio de nueva transaccion
        }//Finc catch

    }//Fin metodo InsertarCliente

    //--------------------------------------------------------------------------
    //Metodo que imprime todos los elementos de un arreglo de string a excepcion
    //de aquellos que tienen ""; EL metodo recibe el arreglo y posicion limite hasta
    //donde quiere que se revice el contenido la posicion limite no es incluida
    public void Mostrararray(String[] array, int limite) {
        for (int i = 0; i < limite; i++) {
            if (array[i].equals("")) {
                //No imprimir nada
            } else {
                System.out.println(array[i]);
            }//Fin else

        }//Fin for 1

        //Espacio despues de listado
        System.out.println();

    }//Fin metodo Mostrararray

    //--------------------------------------------------------------------------
    //Metodo que dado una string hace reccorrido en el arreglo en busca del
    //valor especificado y si lo encuentra lo sustituye con un ""; EL metodo recibe el arreglo y posicion limite hasta
    //donde quiere que se revice el contenido la posicion limite no es incluida
    // Esta implementacion sustituye a todos los elementos con ese valor
    String[] Eliminararray(String[] array, int limite, String elemento) {
        for (int i = 0; i < limite; i++) {
            if (array[i].equals(elemento)) {
                //baja del elelmetno
                array[i] = "";
            }//Fin if 1

        }//Fin for 1

        return array;

    }//Fin metodo Elimianrarray

    //--------------------------------------------------------------------------
    //Metodo que dado un arreglo de strings solo agrega aquellos elementos que
    //no esten duplicados en el arreglo, si ya esta dentro del arreglo no lo
    //agrega. Parametros: Arreglo strings, numero de elementos, String a insertar
    //Finalmente el metodo regresa el arreglo tras las modificaciones si es que hubo
    String[] Insertararray(String[] array, int lim, String elem) {
        int duplicado = -1; //-1=no duplicados 1= si hay un duplicado

        //Verificar si el dato ya esta en el arreglo
        for (int i = 0; i < lim; i++) {
            if (array[i].equals(elem)) {
                //Elemento duplicado
                duplicado = 1;

                //Finalizar busqueda de duplicados
                i = lim;
            }//Fin if 1

        }//Fin for 1

        //No hubo duplicado hacer insercion en la ultima posicion
        if (duplicado == -1) {
            array[lim] = elem;
        }//Fin if 1

        return array;

    }//Fin metodo Insertararray

    //--------------------------------------------------------------------------
    //Metodo que dado una matriz de strings solo agrega aquellos elementos que
    //no esten duplicados en la matriz, si ya esta dentro de esta no lo agrega
    //Parametrosos: matriz string, limite de rows(Numero de elementos en la matriz),
    //String elemento[] (arreglo de atributos de tupla que se desea insertar)
    public String[][] Insertarmatriz(String[][] mat, int rowl, String tupla[]) {
        //Contador de numero de atributos donde hubo match con los valores
        //que desean ser eliminados
        int countdel = 0;

        //Para cada fila/tupla de la matriz
        for (int i = 0; i < rowl; i++) {
            //Checar cada atributo de la tupla
            for (int j = 0; j < tupla.length; j++) {
                //--------------------------------------------
                System.out.println(mat[i][j] + "==" + tupla[j]);
                //--------------------------------------------

                if (mat[i][j].equals(tupla[j])) {
                    //Aumentar el contador de coincidencias
                    countdel++;
                }//FIn if 1

            }//Fin for 2

            //Si el numero de coincidencias fue igual al numero de atributos de algun elemento
            //pasar a siguiente tupla
            //----------------
            System.out.println("Num coincidencias");
            System.out.println(countdel + "==" + tupla.length + "?");
            //----------------
            if (countdel != tupla.length) {
                //Solo agregar en matriz si ya se recorreiron todas las tuplas:
                //En otras palabras, se llego a la ultima tupla dela rreglo con un contador de  coincidencias
                //menor al de numero de los atributos de la tupla
                if (i == rowl - 1) {
                    //Como la tupla no fue completamente igual que ninguna que ya estaba
                    //en la matriz, se puede agregar en la posicion siguiente (osea la posicion del limite)
                    //cada uno de los atributos de la tupla
                    for (int k = 0; k < tupla.length; k++) {
                        //----------------
                        System.out.print("Agregar en ultima posicion");
                        System.out.println("[" + rowl + "] [" + k + "]");
                        //----------------
                        mat[rowl][k] = tupla[k];
                    }//Fin for 3

                }//Fin if 3

            }//Fin if 2
            else {
                //IMPORTANTE:
                //Como la tupla a isnertar ya fue completamente igual para una tupla
                //de la matriz; entonces acabar proceso  ya que definitivamente
                //no se puede insertar

                //Poner que ya se recorrieron todas las tuplas para salir del for 1
                i = rowl;

            }//Fin else 2

            //Restear el contador de coincidencias
            countdel = 0;

        }//Fin for 1

        //Caso especial: cuando el rowl viene en 0 porque es la primera insercion
        //no hay duplicados entonces isnertar la tupla
        if (rowl == 0) {
            for (int k = 0; k < tupla.length; k++) {
                mat[rowl][k] = tupla[k];
            }//Fin for 4

        }//Fin if 3

        return mat;
    }//Fin metodo Insertarmatriz

    //--------------------------------------------------------------------------
    //Metodo que dado una matriz de strings muestra el contenido, recibe la matriz
    //numero de tuplas/filas limite sobre las que se hara el recorrido y numero
    //de atributos que se deben imprimir sobre la misma linea
    public void Mostrarmatriz(String[][] mat, int rowslim, int atributos) {
        //------------------------
        //System.out.println("Limite de tuplas"+rowslim);
        //System.out.println("Num tuplas en matriz"+mat.length);
        //------------------------

        //Recorrido sobre filas
        for (int i = 0; i < rowslim; i++) {
            //Si el primer atributo de la fila no es igual a "" o tampoco es null, entonces imprimir
            //el contenido de todos sus atributos

            if (mat[i][0] == null) {
                //No hacer nada en estafila porque la tupla es nulo
                //System.out.println("NULO");
            }//fiN IF 0
            else {
                if (!mat[i][0].equals("")) {
                    //Recorrido sobre Atributos
                    for (int j = 0; j < atributos; j++) {

                        System.out.print(mat[i][j] + "\t");
                    }//Fin for 2

                    //Imprimir nueva linea que delimita comienzo de nuva fila/tupla
                    System.out.println();

                }//Fin if 1
                else {
                    //El prier atributo esta vacio entonces checar la siguiente fila
                }//Fin else 1

            }//Fin else 0

        }//Fin for 1

    }//Fin metodo mostrarmatriz

    //--------------------------------------------------------------------------
    //Metodo que elimina aquellas tuplas de la matriz que tengan los mismos valores
    //que se le pasan en un arreglo para todos los atributos y regresa la matriz
    //con los elementos eliminados como "", recibe la matriz, el numero de filas/tuplas
    //hasta donde debe verificar y los atributos que debe tener de forma exacta
    String[][] Eliminarmatriz(String[][] mat, int rowlimite, String valores[]) {
        //Contador de numero de atributos donde hubo match con los valores
        //que desean ser eliminados
        int countdel = 0;

        //Para cada fila/tupla de la matriz
        for (int i = 0; i < rowlimite; i++) {
            //Checar cada atributo de la tupla
            for (int j = 0; j < valores.length; j++) {
                //--------------------------------------------
                //System.out.println(mat[i][j]+"=="+valores[j]);
                //--------------------------------------------

                if (mat[i][j].equals(valores[j])) {
                    //Aumentar el contador de coincidencias
                    countdel++;
                }//FIn if 1

            }//Fin for 2

            //Si el numero de coincidencias fue igual al numero de atributos
            //poner en "" todos los atributos de esa fila
            //----------------
            //System.out.println("Num coincidencias");
            //System.out.println(countdel +"=="+ valores.length+"?");
            //----------------
            if (countdel == valores.length) {
                //Hacer el borrado de cada atributo/columna
                for (int k = 0; k < valores.length; k++) {
                    //----------------
                    //System.out.print("Borrar");
                    //System.out.println("["+i+"] ["+k+"]");
                    //----------------
                    mat[i][k] = "";
                }//Fin for 3
            }//Fin if 2

            //Restear el contador de coincidencias
            countdel = 0;

        }//Fin for 1

        return mat;
    }//Fin metodo eliminar matriz

    //--------------------------------------------------------------------------
    //Metodo que por forma recursiva dadas las etapas que le son pasadas
    //pregunta al usuario que seleccione un cuarto de hotel en la fecha correspodniente
    //que este en la ciudad en la que el usuario va a pasar ese dia. Bajo hipotesis:
    //1)El tiempo minimo de cada etapa es un dia y la duracion de las etapas solo
    //pueden ser dias completos
    //2)Para el numero de personas que se considera para la simulacion se debe encontrar
    //un cuarto que pueda almacenar a todas las personas ya que para una simulacion
    //no se pueden asignar 2 o más cuartos en una misma fecha
    public int simHotel(String fecha, String[][] etapaFecha, int count, int index) throws SQLException, IOException {

        if (count == etapaFecha.length) return count;
        numberHoteles++;
        //---------------------- POSIBLE MEJORA:
        //Hacer buscar este query que aqui se muestren
        //tambien los precios del cuarto y de desayuno para que el usuario lo considere
        //al seleccionar cuarto
        String queryHot = "SELECT nombre,PrecioCuarto,PrecioDesayuno FROM hotel WHERE ciudad = '" + etapaFecha[count][4] + "'";
        String queryCuar;
        String hotel;
        String[][] res = null;
        try {
            res = ResultQuery(queryHot);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
        }
        Scanner sc = new Scanner(System.in);

        //Se hace la muestra de loshoteles disponibles en x ciudad

        System.out.println();
        System.out.println("SIMULACION: SELECCION DE HOTELES\n");
        System.out.println("Los hoteles disponibles en, " + etapaFecha[count][4] + " son:\n");
        System.out.println("Nombre\t\t$Cuarto\t$Desayuno(P/persona)");

        Mostrarmatriz(res, res.length, 3);
        System.out.print("\nSeleccione uno de los hoteles anteriores: ");
        hotel = sc.nextLine();

        String[][] costos = ResultQuery("SELECT PrecioCuarto, PrecioDesayuno FROM hotel WHERE nombre = '" + hotel + "' AND ciudad = '"
                + etapaFecha[count][4] + "'");


        //Creación fecha final

        int fechaInicialY = 0;
        int fechaInicialM = 0;
        int fechaInicialD = 0;

        int fechaFinalY;
        int fechaFinalM;
        int fechaFinalD;

        fechaInicialY = Integer.parseInt(fecha.substring(0, 4));
        fechaInicialM = Integer.parseInt(fecha.substring(5, 7));
        fechaInicialD = Integer.parseInt(fecha.substring(8, 10));

        if (fechaInicialM == 1 || fechaInicialM == 3 || fechaInicialM == 5 || fechaInicialM == 7 || fechaInicialM == 8 ||
                fechaInicialM == 10 || fechaInicialM == 12) {
            if (fechaInicialD + Integer.parseInt(etapaFecha[count][6]) > 31 && fechaInicialM == 12) {
                fechaFinalY = fechaInicialY + 1;
                fechaFinalM = 1;
                fechaFinalD = fechaInicialD + Integer.parseInt(etapaFecha[count][6]) - 32;
            }
            if (fechaInicialD + Integer.parseInt(etapaFecha[count][6]) > 31 && fechaInicialM != 12) {
                fechaFinalY = fechaInicialY;
                fechaFinalM = fechaInicialM + 1;
                fechaFinalD = fechaInicialD + Integer.parseInt(etapaFecha[count][6]) - 32;
            }
            if (fechaInicialD + Integer.parseInt(etapaFecha[count][6]) <= 31) {
                fechaFinalY = fechaInicialY;
                fechaFinalM = fechaInicialM;
                fechaFinalD = fechaInicialD + Integer.parseInt(etapaFecha[count][6]);
            }
        }
        if (fechaInicialM == 2) {
            if (fechaInicialD + Integer.parseInt(etapaFecha[count][6]) > 28) {
                fechaFinalY = fechaInicialY;
                fechaFinalM = fechaInicialM + 1;
                fechaFinalD = fechaInicialD + Integer.parseInt(etapaFecha[count][6]) - 29;
            } else {
                fechaFinalY = fechaInicialY;
                fechaFinalM = fechaInicialM;
                fechaFinalD = fechaInicialD + Integer.parseInt(etapaFecha[count][6]);
            }
        } else {
            if (fechaInicialD + Integer.parseInt(etapaFecha[count][6]) > 30) {
                fechaFinalY = fechaInicialY;
                fechaFinalM = fechaInicialM + 1;
                fechaFinalD = fechaInicialD + Integer.parseInt(etapaFecha[count][6]) - 31;
            } else {
                fechaFinalY = fechaInicialY;
                fechaFinalM = fechaInicialM;
                fechaFinalD = fechaInicialD + Integer.parseInt(etapaFecha[count][6]);
            }
        }
        String strFechaInicial = String.valueOf(fechaInicialY);
        String strFechaFinal = String.valueOf(fechaFinalY);
        if (fechaInicialM < 10 || fechaInicialD < 10) {
            if (fechaInicialM < 10) {
                strFechaInicial = strFechaInicial + "-0" + String.valueOf(fechaInicialM) + "-";
            } else {
                strFechaInicial = strFechaInicial + "-" + String.valueOf(fechaInicialM) + "-";
            }
            if (fechaInicialD < 10) {
                strFechaInicial = strFechaInicial + "0" + String.valueOf(fechaInicialD);
            } else {
                strFechaInicial = strFechaInicial + String.valueOf(fechaInicialD);
            }
        } else {
            strFechaInicial = strFechaInicial + "-" + String.valueOf(fechaInicialM) + "-" + String.valueOf(fechaInicialD);
        }

        if (fechaFinalM < 10 || fechaFinalD < 10) {
            if (fechaFinalM < 10) {
                strFechaFinal = strFechaFinal + "-0" + String.valueOf(fechaFinalM) + "-";
            } else {
                strFechaFinal = strFechaFinal + "-" + String.valueOf(fechaFinalM) + "-";
            }
            if (fechaFinalD < 10) {
                strFechaFinal = strFechaFinal + "0" + String.valueOf(fechaFinalD);
            } else {
                strFechaFinal = strFechaFinal + String.valueOf(fechaFinalD);
            }
        } else {
            strFechaFinal = strFechaFinal + "-" + String.valueOf(fechaFinalM) + "-" + String.valueOf(fechaFinalD);
        }

        //Fin fecha final

        String queryCuartos = "SELECT Nombre, NumCuarto, Capacidad FROM cuarto WHERE Nombre LIKE '" + hotel + "'" +
                "AND Ciudad = '" + etapaFecha[count][4] + "'";
        String[][] resCuartos = null;

        try {
            resCuartos = ResultQuery(queryCuartos);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
        }

        System.out.println("\nEscoga un numero de cuarto de acuerdo al número de ocupantes");
        System.out.println("Nombre\t\tNumero\tCapacidad");
        Mostrarmatriz(resCuartos, resCuartos.length, 3);
        int i = 0;
        int cuarto;
        System.out.print("Numero de cuarto: ");
        cuarto = sc.nextInt();

        System.out.println("\nRealizando simulación");
        hotelesMtrx[index][0] = hotel;
        hotelesMtrx[index][1] = etapaFecha[count][4];
        hotelesMtrx[index][2] = etapaFecha[count][5];
        hotelesMtrx[index][3] = String.valueOf(cuarto);
        hotelesMtrx[index][4] = strFechaInicial;
        hotelesMtrx[index][5] = strFechaFinal;
        hotelesMtrx[index][6] = costos[0][0];
        hotelesMtrx[index][7] = costos[0][1];



        /*try {
            stmt.executeUpdate("INSERT INTO simulacionhotel (NumSim, NomHotel, NomCiudad, Pais, NumCuarto, FechaInicio, FechaFinal, NumOcupantes) " + "VALUES (" +
                    " " + numSim + ",'" + hotel + "','" + etapaFecha[count][4]+ "','"+ etapaFecha[count][5] + "'," + cuarto + ",'" + strFechaInicial + "','" + strFechaFinal+ "'," + personas + ")"  );
            conn.commit();
            System.out.println("Realizada");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println(e);
        }
        conn.commit();*/

        return simHotel(strFechaFinal, etapaFecha, count + 1, index + 1);
        //queryCuar = "SELECT "

    }


}//Fin clase TransactionMySQL