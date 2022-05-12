import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Conexion extends Thread {
    Socket cliente1 = null;
    DataInputStream buffEntrada;
    DataOutputStream buffSalida;
    DataInputStream teclado;
    String username;
    
    public static Vector<Conexion> clientesConectados = new Vector();
    public static List<Topic> topics = new ArrayList<Topic>();


    public Conexion(Socket cliente, DataInputStream buffEntrada, DataOutputStream buffSalida, String username) 
    {
        cliente1 = cliente;
        this.buffEntrada = buffEntrada;
        this.buffSalida = buffSalida;
        //clientesConectados.add(this);
        this.username = username;

        Topic brTopic = topics.stream().
                filter(current -> "BroadCast".equals(current.getTopicTitle()))
                .findAny()
                .orElse(null);
        Topic usrTopic = topics.stream().
                filter(current -> username.equals(current.getTopicTitle()))
                .findAny()
                .orElse(null);
        if (brTopic == null)
        {
            List<Conexion> list = new ArrayList<Conexion>();
            brTopic = new Topic(list, "BroadCast", " ");
            topics.add(brTopic);
        }
        if (usrTopic == null) 
        {
            List<Conexion> list = new ArrayList<Conexion>();
            usrTopic = new Topic(list, username, username);
            topics.add(usrTopic);
        }
        
        brTopic.getUserList().add(this);
        usrTopic.getUserList().add(this);

        brTopic.Publish("[NOTIFY]: ["+this.username+"] CONNECTED", this);

    }

    @Override
    public void run() {
        try {

            Boolean done = true;

            while (done)
            {
                String mensaje = buffEntrada.readUTF();
                //Patron para entrada (SEND "MENSAJE" "Usuario Destino") o (SEND 'MENSAJE' 'Usuario Destino')
                Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
                Matcher regexMatcher = regex.matcher(mensaje);
                ArrayList<String> args = new ArrayList<String>();
                int index = 0;
               
                while (regexMatcher.find()) 
                {
                    if (regexMatcher.group(1) != null)
                    {
                        args.add(regexMatcher.group(1));
                    }
                        else if (regexMatcher.group(2) != null)
                    {
                        args.add(regexMatcher.group(2));
                    }
                        else
                    {
                        args.add(regexMatcher.group());
                    }
                }

                String messageBody;

                if (args.get(0).equals("SEND")) {
                    messageBody = args.get(1);

                    if (args.size() == 3) {
                        Topic topic = topics.stream()
                                .filter(current -> args.get(2).equals(current.getTopicTitle()))
                                .findAny()
                                .orElse(null);
                        if (topic != null) {
                            System.out.println("[SUCCESS]: SENT TO: " + args.get(2));
                            notificarUsuario(this, "[SUCCESS]: SENT TO: " + args.get(2));
                            topic.Publish("BY ["+this.username + "]: "+messageBody, this);
                        } else {
                            System.out.println("[ERROR]: USERNAME DOES NOT EXIST");
                            notificarUsuario(this, "[ERROR]: USERNAME DOES NOT EXIST");
                        }
                    } else if (args.size() == 2){
                        Topic topic = topics.stream()
                                .filter(current -> "BroadCast".equals(current.getTopicTitle()))
                                .findAny()
                                .orElse(null);
                        if (topic != null) {
                            System.out.println("[SUCCESS]: SENT TO BROADCAST BY [" +this.username+"]");
                            notificarUsuario(this, "[SUCCESS]: SENT TO BROADCAST");
                            topic.Publish("["+this.username + "] [BROADCAST]: "+messageBody, this);
                        }
                    }

                }else if (args.get(0).equals("LIST"))
                {
                    String lista = "";
                    for (Conexion cliente : clientesConectados) {
                        lista += "Usuarios: " + cliente.username + "\n";
                    }
                    notificarUsuario(this, "USERS ONLINE:");
                    notificarUsuario(this, lista);
                    notificarUsuario(this, "-----------------------------------");

                }else if (args.get(0).equals("DISCONNECT")){
                    Topic topic = topics.stream()
                            .filter(current -> "BroadCast".equals(current.getTopicTitle()))
                            .findAny()
                            .orElse(null);
                    if (topic != null) {
                        System.out.println("["+this.username+"] HAS DISCONNECTED");
                        topic.Publish("["+this.username+"] HAS DISCONNECTED" , this);
                    }
                    this.cliente1.close();
                    clientesConectados.remove(this);
                    topics.remove(this.username);
                }
                else
                {
                System.out.println("Comando Invalido");
                done = !mensaje.equals("exit");
                }
            }
        } 
        catch (Exception e) {};
    }

    public void EnviarMensaje(String mensaje) 
    {
        try 
        {
            buffSalida.writeUTF("\n"+mensaje+"\n");
        } 
        catch (Exception e) {};
    }

    public void notificarUsuario(Conexion usuario, String mensaje) 
    {
        for (int i = 0; i < clientesConectados.size(); i++) 
        {
            if (i == clientesConectados.indexOf(usuario)) 
            {
                clientesConectados.get(i).EnviarMensaje(mensaje);
            }
        }
    }

}
