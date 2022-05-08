import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
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
    List<Conexion> list = new ArrayList<Conexion>();
    

    public Conexion(Socket cliente, DataInputStream buffEntrada, DataOutputStream buffSalida, String username) 
    {
        cliente1 = cliente;
        this.buffEntrada = buffEntrada;
        this.buffSalida = buffSalida;
        clientesConectados.add(this);
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
    }

    @Override
    public void run() {
        try {

            Boolean done = true;
            System.out.println("Num: " + clientesConectados.size());
            
            while (done) 
            {
                String mensaje = buffEntrada.readUTF();
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
                            System.out.println("Mensaje exitoso a: " + args.get(2));
                            notificarUsuario(this, "Mensaje exitoso a: " + args.get(2));
                            topic.Publish(messageBody, this);
                        } else {
                            System.out.println("El usario o topic no existe ");
                            notificarUsuario(this, "el usuario o topic no existe");
                        }
                    } else if (args.size() == 2){
                        Topic topic = topics.stream()
                                .filter(current -> "BroadCast".equals(current.getTopicTitle()))
                                .findAny()
                                .orElse(null);
                        if (topic != null) {
                            System.out.println("Mensaje enviado a BroadCast");
                            notificarUsuario(this, "El Mensaje fue enviado al BroadCast");
                            topic.Publish(messageBody, this);
                        }
                    }

                }else if (args.get(0).equals("LIST"))
                {
                    String lista = "";
                    for (Topic topic : topics) {
                        lista += "Usuarios: " + topic.topicTitle + "\n";
                    }
                    notificarUsuario(this, "Lista:\n-----------------------------------");
                    notificarUsuario(this, lista);

                }else if (args.get(0).equals("DISCONNECT")){
                    System.out.println("["+ this.username + "]: desconectado");
                    topics.remove(this.username);
                    this.cliente1.close();

                }
                else if (args.get(0).isEmpty())
                {
                    System.out.println("Comando vacio");
                    notificarUsuario(this, "Comando vacio");
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
            buffSalida.writeUTF(mensaje);
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
