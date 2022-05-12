public class Servidor extends Mediator {
    
     public Servidor(int puerto){
          this.puerto = puerto;
     }

    public static void main(String[] args) {
        Servidor servidor = new Servidor(7777);
        servidor.init();
    }

}
