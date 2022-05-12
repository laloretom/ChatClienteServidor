import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public abstract class Colega {

    public Socket cliente;
    public DataOutputStream buffSalida;
    public DataInputStream buffEntrada;
    public DataInputStream teclado;
    public String nombre;
    public String ip;
    public int puerto;

    public void RecibirDatos() {

        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String mesgIn = buffEntrada.readUTF();
                        System.out.println(mesgIn);
                    }
                } catch (Exception e) {
                    System.out.print("[NOTIFY]: BYE");
                }
            }
        });
        hilo.start();
    }

    public void EnviarDatos(String msg) {
        try {
            buffSalida.writeUTF("[" + nombre + "]: " + msg);
            buffSalida.flush();
        } catch (Exception e) {
        };
    }

    public void EscribirDatos() {
        try {
            String line = "";
            while (!line.equals("DISCONNECT")) {
                System.out.println("");
                System.out.print("["+nombre+"]: ");
                line = teclado.readLine();
                buffSalida.writeUTF(line);
                buffSalida.flush();
            }
        } catch (Exception e) {
        }
    }



}
