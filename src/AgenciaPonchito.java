/*
 * CLASE PRINCIPAL QUE CONTIENE MAIN QUE EJECUTA EL SISTEMA DE LA AGENCIA DE
 * VIAJES PONCHITO
 *
 *Refrencia comparacion de Dates: https://stackoverflow.com/questions/14757836/java-sql-comparing-dates
 */

//Inicio Clase AgenciaPonchito

//Inicio Clase AgenciaPonchito

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.text.ParseException;
//Atributos

public class AgenciaPonchito {
    //Atributos
    //Declaracion Objeto bufferedReader para obtener la info (Equivalente a Scanf)
    BufferedReader in = null;
    //-------------- DETERMINAR FECHA DEL SISTEMA ACTUAL--------------------
    static Date CURRENTDATE = java.sql.Date.valueOf("2007-12-21"); //Fecha:2009(7)-12-21


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //Constructor que solo inizalzia el BufferedReader
    public AgenciaPonchito() {
        //Inicializar lector de buffer para leer los datos de un stream de entrada
        in = new BufferedReader(new InputStreamReader(System.in));
    }//Fin constructor
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //Metodos
    //--------------------------------------------------------------------------
    //Metodo que imprime el menu de login
    int MenuLogin() throws Exception {
        int io = -1;

        System.out.println("\t\tAGENCIA DE VIAJES PONCHITO\n");

        System.out.println("1)Ingresar (solo usuarios autorizados)");
        System.out.println("2)Registrarse");
        System.out.println("0)Salir");
        System.out.print("Opcion:");

        io = Integer.parseInt(in.readLine());

        return io;
    }//Fin MenuLogin

    //--------------------------------------------------------------------------
    //SubmenuReservaciones
    void Reservaciones(int iduser) throws Exception {
        int selected = -1;
        //Objeto trasnaccion exclusivo de la simulacion
        TransactionMySQL transm = new TransactionMySQL();

        int idsim = -1;
        int idres = -1;

        do {
            //LLAMAR MENU
            System.out.println("\t\tRESERVACION DE VIAJES\n");

            System.out.println("1)Reservar simulacion ");
            System.out.println("2)Consultar simulaciones ");
            System.out.println("3)Cosnultar reservaciones ");
            System.out.println("0)Salir");
            System.out.print("Opcion: ");

            //Obtener informacion en forma de cadena y luego convertirla como entero
            //y guardarla en variable selceted
            selected = Integer.parseInt(in.readLine());

            //Espacio despues de opcion seleccionada
            System.out.println();

            switch (selected) {
                case 0:

                    break;

                case 1:
                    //Dado numero id usuario y numero de simulacion buscar en
                    //tabla simulacion entrada correspondiente.

                    //Validar simulacion; SI la validacion pasa entonces:
                    //1)Llamar validarInfoUSuario
                    //2)Validacion no paso entonces dar a escoger entre:
                    //a)Descartar toda la simulacion
                    //b)Descartat toda la simulacion y moverse a hacer nueva simulacion

                    System.out.print("Indicar Id de simulacion: ");
                    idsim = Integer.parseInt(in.readLine());

                    System.out.println("Validando simulacion...\n");
                    //Obtener id de reservacion si se valido la simulacion
                    idres = transm.ValidarSimulacion(iduser, idsim);

                    //Saber si la validacion fue exitosa
                    if (idres == -1) {
                        System.out.println("VALIDACION DE SIMULACION FALLIDA! RESERVACION NO COMPLETADA!\n");
                    } else {
                        System.out.println("VALIDACION DE SIMULACION EXITOSA! NUMERO DE RESERVACION: " + idres + "\n");

                        //Validar info Usuario
                        ValidarInfoUsuario(iduser);

                        //VER SI INFO DE USUARIO INDICA QUE ES TRABAJADOR DE LA
                        //AGENCIA DE VIAJES PARA APLICAR DESCUENTO

                    }//Fin else

                    break;

                case 2:
                    submenuConsultaSim(iduser);
                    break;

                case 3:
                    //Submenu Reservacion
                    submenuConsultaRes(iduser);

                    break;

                default:
                    System.out.println("Opcion no valida");
                    break;

            }//Fin estructura swithc case

        } while (selected != 0); //Mientras el


    }//Fin metodo reservaciones

    //--------------------------------------------------------------------------
    //SUBMENU PARA CONSULTAR LISTADO DE TODOS LAS RESERVACIONES QUE POSEE UN CLIENTE
    //Y OPCION PARA VER INFORMACION DETALLADE DE UNA RESERVACION
    void submenuConsultaRes(int id) throws Exception {
        int iop = -1;
        int idres = -1;

        //Objeto trasnaccion exclusivo de query de consulta de info de reservacion
        TransactionMySQL trans = new TransactionMySQL();

        do {
            System.out.println("\n\tCONSULTA DE RESERVACIONES");
            System.out.println("1)Mostrar todas mis reservaciones");
            System.out.println("2)Desglozar reservacion");
            System.out.println("0)Salir");
            System.out.print("Opcion:");
            iop = Integer.parseInt(in.readLine());

            switch (iop) {
                case 0:
                    //fin conexion
                    trans.close();
                    break;

                case 1:
                    //Listado del ID de de todas las simulaciones de un usuario
                    System.out.println("\nLas reservaciones con las que cuenta son:\n");
                    System.out.println("Id\n");
                    trans.MostrarResCliente(id);
                    break;

                case 2:
                    //Dado un Id mostrar Circuitos,Hoteles,Lugares,Ciudades,Pais

                    //Precio,Fecha salida Fecha llegada,Costo,NumPersonas

                    //Pais

                    //Ciudades a visitar

                    //Circuitos
                    //Lugares

                    //Hoteles

                    System.out.print("\nID de reservacion: ");
                    idres = Integer.parseInt(in.readLine());

                    trans.MostrarDetallesRes(idres, id);

                    System.out.println();
                    break;

            }//Fin switch


        } while (iop != 0);


    }//Fin metodo submenuConsultaSim

    //--------------------------------------------------------------------------
    //SUBMENU PARA CONSULTAR LISTADO DE TODOS LAS SIMULACIONES QUE POSEE UN USUARIO
    //Y OPCION PARA VER INFORMACION DETALLADA DE UNA SIMULACION
    void submenuConsultaSim(int id) throws Exception {
        int iop = -1;
        int idsim = -1;

        //Objeto trasnaccion exclusivo de query de consulta de info de simulacion
        TransactionMySQL trans = new TransactionMySQL();

        do {
            System.out.println("\n\tCONSULTA DE SIMULACIONES");
            System.out.println("1)Mostrar todas mis simulaciones");
            System.out.println("2)Desglozar simulacion");
            System.out.println("0)Salir");
            System.out.print("Opcion:");
            iop = Integer.parseInt(in.readLine());

            switch (iop) {
                case 0:
                    //fin conexion
                    trans.close();
                    break;

                case 1:
                    //Listado del ID de de todas las simulaciones de un usuario
                    System.out.println("\nLas simulaciones con las que cuenta son:\n");
                    System.out.println("Id\n");
                    trans.MostrarSimsUsuario(id);
                    break;

                case 2:
                    //Dado un Id mostrar Circuitos,Hoteles,Lugares,Ciudades,Pais

                    //Precio,Fecha salida Fecha llegada,Costo,NumPersonas

                    //Pais

                    //Ciudades a visitar

                    //Circuitos
                    //Lugares

                    //Hoteles
                    System.out.print("\nID de simulacion: ");
                    idsim = Integer.parseInt(in.readLine());

                    trans.MostrarDetallesSim(idsim, id);

                    System.out.println();
                    break;

            }//Fin switch


        } while (iop != 0);


    }//Fin metodo submenuConsultaSim

    //--------------------------------------------------------------------------
    //Se hace la verificacion de que sea un usuario conocido; si no es se pide
    //registro de datos faltantes; si es un usario conocido se le muestra la
    //informacion y despues se pregunta si desea actualizar algun atributo
    void ValidarInfoUsuario(int id) throws Exception {
        String tipo, dir, pago, emple;
        int iop = -1;
        String[][] infactual;//contenedro de info actual del usuario

        //Objeto transaccion exclusivo para verificacionCliente
        TransactionMySQL tran = new TransactionMySQL();
        if (tran.VerificarCliente(id) != -1) {
            System.out.println("Informacion actual de cliente conocido: \n");

            //Hacer print de todos los datos tabla USUARIO/Cliente
            System.out.println("Nombre\tTipoCliente\tAnioRegistro\tDireccion\tPago\tEs empleado");
            tran.MostrarDatosCliente(id);


            System.out.println("\n¿Desea actualizar algun dato?");
            System.out.println("1)Si");
            System.out.println("2)No");
            System.out.print("Opcion: ");

            if ((Integer.parseInt(in.readLine())) == 1) {
                //Obtener Info actual CLiente conocido y asignarsela a variables
                //para que aquellas opciones que no se van a cambiar no pierdan
                //datos
                infactual = tran.qryGetClientData(id);

                tipo = infactual[0][0];
                dir = infactual[0][1];
                pago = infactual[0][2];
                emple = infactual[0][3];

                do {
                    System.out.println("\nIndique campo que desea actualizar:\n");
                    System.out.println("1)Tipo Cliente");
                    System.out.println("2)Direccion");
                    System.out.println("3)Forma de pago");
                    System.out.println("4)Es empleado de la Agencia");
                    System.out.println("0)Salir y guardar cambios");
                    System.out.print("Opcion: ");
                    iop = Integer.parseInt(in.readLine());

                    switch (iop) {
                        case 0:
                            break;

                        case 1:
                            System.out.print("Tipo cliente(Individual/Compania/Grupo): ");
                            tipo = in.readLine();
                            break;

                        case 2:
                            System.out.print("Direccion: ");
                            dir = in.readLine();
                            break;

                        case 3:
                            System.out.print("Forma de pago (Efectivo/Tarjeta): ");
                            pago = in.readLine();
                            break;

                        case 4:
                            System.out.print("Es empleado de Agencias Ponchito(Si/No): ");
                            emple = in.readLine();
                            break;

                        default:
                            System.out.println("Opcion no valida!");
                            break;

                    }//Fin estructura switch

                } while (iop != 0);

                //Llamar metodo de actualizacion de info de CLIENTE CONOCIDO
                tran.UpdateDatosCliente(id, tipo, dir, pago, emple);


            }//Fin if 2


        }//Fin if 1
        else {
            System.out.println("Cliente no conocido!\n");

            System.out.println("Completar informacion requerida:");

            System.out.print("Tipo cliente(Individual/Compania/Grupo): ");
            tipo = in.readLine();

            System.out.print("Direccion: ");
            dir = in.readLine();

            System.out.print("Forma de pago (Efectivo/Tarjeta): ");
            pago = in.readLine();

            System.out.print("Es empleado de Agencias Ponchito(Si/No): ");
            emple = in.readLine();

            tran.InsertarCliente(id, tipo, CURRENTDATE, dir, pago, emple);

        }//Fin else 1

    }//Fin metodo validar usuario

    //--------------------------------------------------------------------------
    //Metodo que imprime el menu principal
    int MenuPrincipal(int iduser) throws Exception {
        int selected = -1;
        //Objeto trasnaccion exclusivo de la simulacion
        TransactionMySQL transm = new TransactionMySQL();

        //Eliminar todos los clientes que no tengan al menos una Reservacion

        do {
            //LLAMAR MENU
            System.out.println("\t\tAGENCIA DE VIAJES PONCHITO\n");

            System.out.println("1)Consultar Folleto ");
            System.out.println("2)Simulacion ");
            System.out.println("3)Reservacion ");
            System.out.println("0)Salir");
            System.out.print("Opcion: ");

            //Obtener informacion en forma de cadena y luego convertirla como entero
            //y guardarla en variable selceted
            selected = Integer.parseInt(in.readLine());

            //Espacio despues de opcion seleccionada
            System.out.println();

            switch (selected) {
                case 0:
                    //Final de Transaccion, Entonces cerrar todo
                    //Cerrar statement y la conexion (Los resultset usados se cierran
                    //respectivamente al usar los metodos query y ReturnQuery)
                    transm.close();
                    break;

                case 1:
                    //Submenu Consultar Folleto
                    MenuFolleto();
                    break;

                case 2:
                    //Submenu Simulacion
                    transm.Simulacion(CURRENTDATE, iduser);
                    break;

                case 3:
                    //Submenu Reservacion
                    Reservaciones(iduser);
                    break;

                default:
                    System.out.println("Opcion no valida");
                    break;

            }//Fin estructura swithc case

        } while (selected != 0); //Mientras el

        return selected;
    }//Fin metodo menu principal

    //--------------------------------------------------------------------------
    //Metodo que imprime y maneja la ejecucuion de los metodos correspondientes
    //de acuerdo a las opciones ofrecidas para realizar las consultas de la informacion
    //de los viajes ofrecidos mientras se navega en folleto
    public void MenuFolleto() throws Exception {
        int select = -1;
        int iop = 0;
        //Objeto para transaccion de MySql
        TransactionMySQL transac = new TransactionMySQL();

        do {
            System.out.println("\tCONSULTA DE FOLLETO\n");

            System.out.println("1)Paises");
            System.out.println("2)Ciudades");
            System.out.println("3)Lugares");
            System.out.println("4)Fechas");
            System.out.println("5)Circuitos sugeridos");
            System.out.println("6)Hoteles");
            System.out.println("0)Regresar al menu principal ");
            System.out.print("Opcion: ");

            //Obtener informacion en forma de cadena y luego convertirla como entero
            //y guardarla en variable selceted
            select = Integer.parseInt(in.readLine());

            //Espacio despues de opcion seleccionada
            System.out.println();

            switch (select) {
                case 0:
                    //Salirse, Entonces cerrar todo
                    //Cerrar statement y la conexion
                    transac.close();
                    break;

                case 1:
                    System.out.println("1)Todos los paises disponibles");
                    System.out.println("2)Buscar un pais especifico");
                    System.out.print("Opcion: ");
                    iop = Integer.parseInt(in.readLine());

                    if (iop == 1) {
                        System.out.println("\nResultado de la consulta: ");
                        transac.qryAllPaises();
                    }//Fin if 1

                    if (iop == 2) {
                        String pais = "";
                        pais = transac.queryPais();

                        if (pais.length() == 0) {
                            System.out.println("\nPais no disponible o inexistente");
                        }
                        ;//Fin if 2.1

                    }//Fin if 2

                    //Separador de respuesta
                    System.out.println();
                    break;

                case 2:
                    //TOdas la ciudades de todos los paises * *
                    //Todas las ciudades de un pais especifico * Nombre
                    //Buscar una ciudad especifica Pais Nombre
                    break;

                case 3:
                    break;

                case 4:
                    break;

                case 5:
                    break;

                case 6:
                    break;

                default:
                    System.out.println("Opcion de consulta no valida");
                    break;
            }//Fin estructura switch


        } while (select != 0);

    }//Fin metodo MenuFolleto

    //--------------------------------------------------------------------------
    //Metodo que hace validacion de Login y si pasa el login llama al MenuPrincipal
    //Pero si falla indica que no hay usuario registrado
    public void validarLogin() throws Exception {
        //Objeto para transaccion de MySql
        TransactionMySQL transac = new TransactionMySQL();
        int idobt = transac.Login();

        if (idobt != -1) {
            //Paso el login, entonces ir a MenuPrincipal pasando id usuario actual
            System.out.println();
            MenuPrincipal(idobt);
        }//Fin if 1
        else {
            //No paso el login, porque se regreso Id -1
            System.out.println("Usuario inexistente!\n");

        }//Fin else 1

        //Cerrar el statement y la conexion a la base de datos (Fin transaccion)
        transac.close();

    }//Fin validar Login

    //--------------------------------------------------------------------------
    //Metodo que realiza registro de usuario y una vez que lo efectua lleva al
    //menu principal
    public void RegistrarUsuario() throws Exception {
        //Objeto para la transaccion
        TransactionMySQL trans = new TransactionMySQL();
        int idass = -1;

        //Obtener id asignado
        idass = trans.userRegistration();

        if (idass != -1) {
            //Ir al menu principal pasando le el id del usuario actual
            MenuPrincipal(idass);
        }//FIn if 1

        //Cerrar el statement y la conexion a la base de datos (Fin transaccion)
        trans.close();

    }//Fin metodo RegistrarUsuario

    //-------------------------------------------------------------------------
    //Inicio del programa/main
    public static void main(String[] args) throws Exception {
        //Declaracion de variables
        //Opcion seleccionada menuPrincipal
        int iop = -1;

        //Objeto clase  transaccion para eliminar simulaciones vencidas
        TransactionMySQL tr = new TransactionMySQL();

        //Objeto clase agencia ponchito que inicializa el Buffered Reader
        //Usado por otros metodos!! Es importante hacer esta instanciacion por eso
        AgenciaPonchito agencia = new AgenciaPonchito();

        //Datos de entrada

        //Procesos
        //Eliminar automaticamente todas las simulaciones cuya fecha de validacion
        //haya vencido
        tr.ElimExpiredSims(CURRENTDATE);

        //Solo puede haber usuarios con reservaciones

        //Verificación de derechos de acceso

        do {
            //LLAMAR MENU
            iop = agencia.MenuLogin();
            switch (iop) {
                case 0:
                    break;

                case 1:
                    //Verificar Login
                    agencia.validarLogin();
                    break;

                case 2:
                    //Hace registro
                    agencia.RegistrarUsuario();
                    break;

                default:
                    System.out.println("Opcion no valida");
                    break;

            }//Fin estructura swithc case

        } while (iop != 0); //Mientras el

        //Datos de salida

        //Fin del programa
        System.out.println("Fin del programa v28");

    }//Fin main


}//Fin Clase AgenciaPonchito