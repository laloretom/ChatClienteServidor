import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cliente extends Colega {
    static final String CLASS_NAME = Cliente.class.getSimpleName();
    static final Logger LOG = Logger.getLogger(CLASS_NAME);

    public Cliente(String nombre, String ip, int puerto) {
        this.puerto = puerto;
        this.nombre = nombre;
        this.ip = ip;
    }

    public void init() {

        try {
            cliente = new Socket(ip, puerto);
            buffSalida = new DataOutputStream(cliente.getOutputStream());
            buffEntrada = new DataInputStream(cliente.getInputStream());
            teclado = new DataInputStream(System.in);

            buffSalida.writeUTF(nombre);
            String mesgIn = buffEntrada.readUTF();
            if (mesgIn.equals("Aceptado")) {
                System.out.println("[NOTIFY]: WELCOME ["+nombre+"]");
                RecibirDatos();
                EscribirDatos();

            } else {
                System.out.println(mesgIn);
                cliente.close();
                //System.out.println("BANDERA CLIENTE");
            }

        } catch (Exception e) {
            System.out.println("COULD NOT LOG IN");
            LOG.severe(e.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2){
            //LOG.severe("No command to process. Usage is:" + "\njava DeptSalesReport <keyword>");
            return;
        }
        if (args[0].equals("CONNECT")){
            Cliente cliente = new Cliente(args[1], "localhost", 7777);
            cliente.init();
        }



    }

}
