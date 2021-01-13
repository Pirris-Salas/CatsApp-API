import javax.swing.*;
import java.io.IOException;

public class Inicio {

    public static void main(String[] args) throws IOException {
        menuPrincipal();
    }

    public static void menuPrincipal() throws IOException {
        int opcion_menu = -1;
        String [] botones = {"1. Ver gatos","2. Ver Favoritos", "3. Salir"};

        do {
            //Menu Principal
            String opcion = (String) JOptionPane.showInputDialog(null,"Gatos Java", "Menu " +
                    "Principal", JOptionPane.INFORMATION_MESSAGE, null, botones, botones[0]);

            //Validamos que opcion selecciona el usuario
            for(int i=0; i< botones.length; i++){
                if(opcion.equals(botones[i])){
                    opcion_menu = i;
                }
            }

            switch (opcion_menu){
                case 0: GatosService.verGatos();
                    break;
                case 1:
                    Gatos  gatos = new Gatos();
                    GatosService.verFavoritos(gatos.getApikey());
                default:
                    break;
                case 2:
                    System.exit(1);
                    break;
            }
        }while (opcion_menu != 1);
    }
    }


