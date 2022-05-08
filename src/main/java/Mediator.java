import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class Mediator {

    public ServerSocket server;
    public int puerto = 9000;
    public List<Conexion> conexiones = new ArrayList<Conexion>();

    public void init() {
        Socket socket;

        try {
            server = new ServerSocket(puerto);
            System.out.println("Esperando peticiones por el puerto " + puerto);
            while (true) {
                socket = server.accept();
                DataInputStream buffEntrada = new DataInputStream(socket.getInputStream());
                DataOutputStream buffSalida = new DataOutputStream(socket.getOutputStream());
                String username = buffEntrada.readUTF();
                System.out.println("["+username+"]: Conectado");
                Conexion con = conexiones.stream().
                        filter(current -> username.equals(current.username))
                        .findAny()
                        .orElse(null);
                
                if (con == null) {
                    buffSalida.writeUTF("Aceptado");
                    Conexion conexion = new Conexion(socket, buffEntrada, buffSalida, username);
                    conexion.start();
                    conexiones.add(conexion);

                } else {
                    System.out.println("["+username+"] no se pudo loggear");
                    socket.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

}
