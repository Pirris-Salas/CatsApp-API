import com.google.gson.Gson;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;


public class GatosService {

    public static void verGatos() throws IOException {

        //1. Vamos a traer los datos de la API
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        String elJson = response.body().string();

        //Cortamos el primer caracter del String, ya que trae corchetes
        elJson = elJson.substring(1,elJson.length());

        //Cortamos el último corchete
        elJson = elJson.substring(0,elJson.length()-1);

        //Crear un objeto de la clase GSON
        Gson gson = new Gson();
        Gatos gatos = gson.fromJson(elJson, Gatos.class);//parseamos el Json a la clase

        //Redimensionar la imagen
        Image image = null;
        try{
            URL url = new URL(gatos.getUrl());
            //Cargamos el url a un objeto tipo imagen

            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.addRequestProperty("User-Agent", "");
            BufferedImage bufferedImage = ImageIO.read(httpcon.getInputStream());
            ImageIcon fondoGato = new ImageIcon(bufferedImage);

            if(fondoGato.getIconWidth() > 800){
                //Redimensionamos las imágenes con más de 800 px de ancho
                Image fondo = fondoGato.getImage();
                Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                fondoGato = new ImageIcon(modificada);
            }

            String menu = "Opciones: \n"
                    + "1. Ver otra imagen \n"
                    + "2. Favorito \n"
                    + "3. Volver \n";

            String[] botones = {"Ver otra imagen", "Favorito", "Volver"};
            String id_gato = String.valueOf(gatos.getId());
            String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato,
                    JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);

            int seleccion = -1;
            for (int i = 0; i< botones.length;i++){
                if(opcion.equals(botones[i])){
                    seleccion = i;
                }
            }

            switch (seleccion){
                case 0: verGatos();
                    break;
                case 1:
                    favoritoGato(gatos);
                    break;
                default:
                    break;
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void favoritoGato(Gatos gato){
        try{
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n    \"image_id\":\""+gato.getId()+"\"\r\n}");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites")
                    .method("POST", body)
                    .addHeader("x-api-key", gato.getApikey())
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void verFavoritos(String apikey) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites/")
                .method("GET", null)
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", apikey)
                .build();
        Response response = client.newCall(request).execute();

        //Guardamos el String con la lista de gatos favoritos
        String elJson = response.body().string();

        //Creamos el objeto GSON
        Gson gson = new Gson();

        GatosFav[] gatosArray = gson.fromJson(elJson, GatosFav[].class);

        if(gatosArray.length > 0){
            int min = 1;
            int max = gatosArray.length;
            int aleatorio = (int) (Math.random() * ((max-min) + 1)) + min;
            int indice = aleatorio - 1;

            GatosFav gatosFav = gatosArray[indice];

            //Redimensionar la imagen
            Image image = null;
            try{
                URL url = new URL(gatosFav.getImage().getUrl());
                //Cargamos el url a un objeto tipo imagen

                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.addRequestProperty("User-Agent", "");
                BufferedImage bufferedImage = ImageIO.read(httpcon.getInputStream());
                ImageIcon fondoGato = new ImageIcon(bufferedImage);

                if(fondoGato.getIconWidth() > 800){
                    //Redimensionamos las imágenes con más de 800 px de ancho
                    Image fondo = fondoGato.getImage();
                    Image modificada = fondo.getScaledInstance(800,600, Image.SCALE_SMOOTH);
                    fondoGato = new ImageIcon(modificada);
                }

                String menu = "Opciones: \n"
                        + "1. Ver otra imagen \n"
                        + "2. Eliminar Favorito \n"
                        + "3. Volver \n";

                String[] botones = {"Ver otra imagen", "Eliminar Favorito", "Volver"};
                String id_gato = String.valueOf(gatosFav.getId());
                String opcion = (String) JOptionPane.showInputDialog(null, menu, id_gato,
                        JOptionPane.INFORMATION_MESSAGE, fondoGato, botones, botones[0]);

                int seleccion = -1;
                for (int i = 0; i< botones.length;i++){
                    if(opcion.equals(botones[i])){
                        seleccion = i;
                    }
                }

                switch (seleccion){
                    case 0: verFavoritos(apikey);
                        break;
                    case 1:
                        borrarFavorito(gatosFav);
                        break;
                    case 2:
                        Inicio.menuPrincipal();
                    default:
                        break;
                }
            }catch (IOException e){
                System.out.println(e);
            }
        }
    }

    public static void borrarFavorito(GatosFav gatoFav){

        try{
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://api.thecatapi.com/v1/favourites/" +gatoFav.getId() +"")
                    .method("DELETE", body)
                    .addHeader("x-api-key", gatoFav.getApikey())
                    .build();
            Response response = client.newCall(request).execute();

            if(response.code() == 200) {
                JOptionPane.showMessageDialog(null, "Gato Favorito " + gatoFav.getId() + " Eliminado ");
                verFavoritos(gatoFav.getApikey());
            }else {
                JOptionPane.showMessageDialog(null, "Algo a fallado " + response.code());
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }
}
