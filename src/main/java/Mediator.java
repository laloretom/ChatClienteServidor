import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class Mediator {

    public ServerSocket server;
    public int puerto = 7777;
    //public List<Conexion> conexiones = new ArrayList<Conexion>();

    public void init() {
        Socket socket;
        Conexion con = null;
        try {
            server = new ServerSocket(puerto);
            System.out.println("Esperando peticiones por el puerto " + puerto);

            while (true) {
                socket = server.accept();
                DataInputStream buffEntrada = new DataInputStream(socket.getInputStream());
                DataOutputStream buffSalida = new DataOutputStream(socket.getOutputStream());
                String username = buffEntrada.readUTF();
                System.out.println("[NOTIFY]: ["+username+"] CONNECTED");
                con = Conexion.clientesConectados.stream().
                        filter(current -> username.equals(current.username))
                        .findAny()
                        .orElse(null);

                if (con == null) {
                    buffSalida.writeUTF("Aceptado");
                    Conexion conexion = new Conexion(socket, buffEntrada, buffSalida, username);
                    conexion.start();
                    Conexion.clientesConectados.add(conexion);
                    //conexiones.add(conexion);

                } else {
                    //conexiones.remove(con);
                    System.out.println("[ERROR]: ["+username+"] COULD NOT LOG IN");
                    socket.close();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        };
    }

}
