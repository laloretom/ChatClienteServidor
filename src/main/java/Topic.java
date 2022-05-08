import java.util.ArrayList;
import java.util.List;

public class Topic {

    public List<Conexion> usuarios = new ArrayList<Conexion>();
    public String topicTitle;
    public String admnUsr;
    
    public Topic(List<Conexion> usuarios, String topicTitle, String admnUsr){
        this.usuarios = usuarios;
        this.topicTitle = topicTitle;
        this.admnUsr = admnUsr;
    }
    
    public List<Conexion> getUserList() {
        return usuarios;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public void Publish(String msg, Conexion con) {

        for (int i = 0; i < usuarios.size(); i++) {
            if (i != usuarios.indexOf(con)) {
                usuarios.get(i).EnviarMensaje(msg);
            }
        }
    }
}
