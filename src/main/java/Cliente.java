import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Cliente extends Colega {

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
                System.out.println("Bienvenido");
                RecibirDatos();
                EscribirDatos();
            } else {
                System.out.println(mesgIn);
                cliente.close();
            }

        } catch (Exception e) {
            System.out.println("no funciono");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente("Luis", "localhost", 9000);
        cliente.init();

    }

}
