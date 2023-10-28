package develop.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import develop.client.exceptions.ClientException;
import develop.common.models.*;
import develop.common.utils.LocalDateAdapter;
import develop.common.utils.LocalDateTimeAdapter;
import develop.common.utils.PropertiesReader;
import develop.common.utils.UuidAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static develop.common.models.Request.Type.*;
/**
 * Esta clase representa un cliente que se comunica con un servidor.
 * Proporciona metodos para enviar solicitudes al servidor y gestionar la comunicacion.
 *
 * @version 1.0
 */
public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 3000;
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(java.util.UUID.class, new UuidAdapter()).create();
    private SSLSocket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String token;

    /**
     * Metodo principal que inicia la aplicacion del cliente y si hay un error se muestra en
     * pantalla un logger con el Error dado.
     */
    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.start();
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        }
    }
    /**
     * Inicia la conexion con el servidor, configurando la comunicacion.
     * Y cargando la configuracion del cliente desde un archivo de propiedades.
     *
     * @throws IOException Si ocurre un error durante la conexion.
     */
    public void start() throws IOException {
        try {
            // Hacemos la conexion
            openConnection();

            // Enviamos el Request de Login y almacenados el Token devuelto
            token = sendRequestLogin();

            Thread.sleep(150);

            // Enviamos un Request para Obtener todos los funkos
            sendRequestGetAllFunkos(token);

            // Creamos un funko
            var funko = Funko.builder().releaseData(LocalDate.of(2022, 1,1)).cod(UUID.randomUUID()).id(91L).name("Pepe").price(9.0).model(Model.ANIME).build();

            // Enviamos el Request para guardar nuestro funko
            sendRequestPostFunko(token, funko);

            // Enviamos el Request para Obtener funko por id
            sendRequestGetFunkoById(token, "1");

            // Enviamos ahora 4 request para guardar 4 funkos
            sendRequestPostFunko(token, Funko.builder().releaseData(LocalDate.of(2022, 1,1)).cod(UUID.randomUUID()).id(92L).name("Maria").price(9.0).model(Model.OTROS).build());
            sendRequestPostFunko(token,Funko.builder().releaseData(LocalDate.of(2022, 1,1)).cod(UUID.randomUUID()).id(93L).name("Juan").price(9.0).model(Model.OTROS).build());
            sendRequestPostFunko(token, Funko.builder().releaseData(LocalDate.of(2022, 1,1)).cod(UUID.randomUUID()).id(94L).name("Carlos").price(9.0).model(Model.DISNEY).build());
            sendRequestPostFunko(token, Funko.builder().releaseData(LocalDate.now()).cod(UUID.randomUUID()).id(95L).name("Laura").price(9.0).model(Model.MARVEL).build());

            // Enviamos Request para obtener funkos por modelo "OTROS"
            sendRequestGetFunkoByModel(token, "OTROS");

            // Modificamos un funko
            funko.setName("Updated");

            // Enviamos Request para modificar el funko con los nuevos cambios
            sendRequestPutFunko(token, funko);

            // Enviamos Request para borrar el funko con id 1
            sendRequestDeleteFunko(token, "91");

            // Enviamos Request para Obtener todos los funkos de nuevo
            sendRequestGetAllFunkos(token);

            // Enviamos Request para actualizar el Funko (este nos dara error ya que hemos borrado el funko con id 91)
            sendRequestPutFunko(token, funko);

            // Enviamos Request para borrar funko con if 1 (este nos dara error ya que hemos borrado el funko con id 91)
            sendRequestDeleteFunko(token, "91");

            // Evniamos Request para obtener todos los Funkos
            sendRequestGetAllFunkos(token);

            // Enviamos Request para obtener Funkos por su Año de creacion siendo este 2023
            sendRequestGetFunkoByReleaseData(token, "2023");

            // Finalmente enviamos Request para desloguearnos
            sendRequestSalir();
        } catch (ClientException ex) {
            logger.error("Error: " + ex.getMessage());
            System.err.println("🔴 Error: " + ex.getMessage());
            closeConnection();
            System.exit(1);
        } catch (InterruptedException e) {
            logger.error("Error: " + e.getMessage());
        }
    }
    /**
     * Cierra la conexion con el servidor y los flujos de entrada/salida.
     *
     * @throws IOException Si ocurre un error al cerrar la conexion.
     */
    private void closeConnection() throws IOException {
        logger.debug("Cerrando la conexión con el servidor: " + HOST + ":" + PORT);
        System.out.println("🔵 Cerrando Cliente");
        if (in != null)
            in.close();
        if (out != null)
            out.close();
        if (socket != null)
            socket.close();
    }
    /**
     * Abre la conexion con el servidor.
     *
     * @throws IOException Si ocurre un error durante la apertura de la conexion.
     */
    private void openConnection() throws IOException {
        System.out.println("🔵 Iniciando Cliente");
        Map<String, String> myConfig = readConfigFile();

        logger.debug("Cargando fichero de propiedades");
        System.setProperty("javax.net.ssl.trustStore", myConfig.get("keyFile"));
        System.setProperty("javax.net.ssl.trustStorePassword", myConfig.get("keyPassword"));

        SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) clientFactory.createSocket(HOST, PORT);

        logger.debug("Protocolos soportados: " + Arrays.toString(socket.getSupportedProtocols()));
        socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
        socket.setEnabledProtocols(new String[]{"TLSv1.3"});

        logger.debug("Conectando al servidor: " + HOST + ":" + PORT);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("✅ Cliente conectado a " + HOST + ":" + PORT);
        infoSession(socket);

    }
    /**
     * Envia una solicitud de inicio de sesion al servidor y almacena el token devuelto.
     *
     * @return El token de autenticacion obtenido del servidor.
     * @throws ClientException Si ocurre un error durante la solicitud de inicio de sesion.
     */
    private String sendRequestLogin() throws ClientException {
        String myToken = null;
        var loginJson = gson.toJson(new Login("pepe", "pepe1234"));
        Request request = new Request(LOGIN, loginJson, null, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + LOGIN);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        try {
            Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
            }.getType());

            logger.debug("Respuesta recibida: " + response.toString());

            System.out.println("Respuesta recibida de tipo: " + response.status());

            switch (response.status()) {
                case TOKEN -> {
                    System.out.println("🟢 Mi token es: " + response.content());
                    myToken = response.content();
                }
                default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());

            }
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        }
        return myToken;
    }
    /**
     * Envia una solicitud al servidor para obtener todos los Funkos disponibles.
     *
     * @param token El token de autenticacion del cliente.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     * @throws IOException     Si ocurre un error durante la comunicacion con el servidor.
     */
    private void sendRequestGetAllFunkos(String token) throws ClientException, IOException {
        Request request = new Request(GETALL, null, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + GETALL);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> {
                List<Funko> responseContent = gson.fromJson(response.content(), new TypeToken<List<Funko>>() {
                }.getType());
                System.out.println("🟢 Los funkos son: " + responseContent);
            }
            case ERROR -> System.err.println("🔴 Error: " + response.content());
        }
    }

    /**
     * Envia una solicitud al servidor para obtener un Funko por su ID.
     *
     * @param token El token de autenticacion del cliente.
     * @param id    El ID del Funko que se desea obtener.
     * @throws IOException     Si ocurre un error durante la comunicacion con el servidor.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     */
    private void sendRequestGetFunkoById(String token, String id) throws IOException, ClientException {
        Request request = new Request(GETBYID, id, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + GETBYID);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());
        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> {
                Funko responseContent = gson.fromJson(response.content(), new TypeToken<Funko>() {
                }.getType());
                System.out.println("🟢 El funko solicitado es: " + responseContent);
            }
            case ERROR ->
                    System.err.println("🔴 Error: Funko no encontrado con id: " + id + ". " + response.content());
            default -> throw new ClientException("Error no esperado al obtener el funko");
        }
    }
    /**
     * Envia una solicitud al servidor para obtener Funkos por su modelo.
     *
     * @param token   El token de autenticacion del cliente.
     * @param modelo  El modelo de Funko por el cual se desea realizar la busqueda.
     * @throws IOException     Si ocurre un error durante la comunicacion con el servidor.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     */
    private void sendRequestGetFunkoByModel(String token, String modelo) throws IOException, ClientException {
        Request request = new Request(GETBYMODEL, modelo, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + GETBYMODEL);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());
        switch (response.status()) {
            case OK -> {
                List<Funko> responseContent = gson.fromJson(response.content(), new TypeToken<List<Funko>>() {
                }.getType());
                System.out.println("🟢 Los funkos por modelo " + modelo + " son: " + responseContent);
            }
            case ERROR -> System.err.println("🔴 Error: " + response.content());
        }
    }

    /**
     * Envia una solicitud al servidor para obtener Funkos por su ano de lanzamiento.
     *
     * @param token         El token de autenticacion del cliente.
     * @param anoLanzamiento El ano de lanzamiento por el cual se desea realizar la busqueda.
     * @throws IOException     Si ocurre un error durante la comunicacion con el servidor.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     */
    private void sendRequestGetFunkoByReleaseData(String token, String anoLanzamiento) throws IOException, ClientException {
        Request request = new Request(GETBYRELEASEDATA, anoLanzamiento, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + GETBYRELEASEDATA);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());
        switch (response.status()) {
            case OK -> {
                List<Funko> responseContent = gson.fromJson(response.content(), new TypeToken<List<Funko>>() {
                }.getType());
                System.out.println("🟢 Los funkos con ano de lanzamiento " + anoLanzamiento + " son: " + responseContent);
            }
            case ERROR -> System.err.println("🔴 Error: " + response.content());
        }
    }
    /**
     * Envia una solicitud al servidor para agregar un nuevo Funko.
     *
     * @param token El token de autenticacion del cliente.
     * @param funko El Funko que se desea agregar.
     * @throws IOException     Si ocurre un error durante la comunicacion con el servidor.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     */
    private void sendRequestPostFunko(String token, Funko funko) throws IOException, ClientException {
        var funkoJson = gson.toJson(funko);
        Request request = new Request(POST, funkoJson, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + POST);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> {
                Funko responseContent = gson.fromJson(response.content(), new TypeToken<Funko>() {
                }.getType());
                System.out.println("🟢 El funko insertado es: " + responseContent);
            }
            case ERROR ->
                    System.err.println("🔴 Error: No se ha podido insertar el funko: " + response.content()); // No se ha encontrado
            default -> throw new ClientException("Error no esperado al insertar el funko");
        }
    }
    /**
     * Envia una solicitud al servidor para actualizar un Funko existente.
     *
     * @param token El token de autenticacion del cliente.
     * @param funko El Funko con los cambios que se desean aplicar.
     * @throws IOException     Si ocurre un error durante la comunicacion con el servidor.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     */
    private void sendRequestPutFunko(String token, Funko funko) throws IOException, ClientException {
        var funkoJson = gson.toJson(funko);
        Request request = new Request(UPDATE, funkoJson, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + UPDATE);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());
        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> {
                Funko responseContent = gson.fromJson(response.content(), new TypeToken<Funko>() {
                }.getType());
                System.out.println("🟢 El Funko actualizado es: " + responseContent);
            }
            case ERROR ->
                    System.err.println("🔴 Error: No se ha podido actualizar el Funko: " + response.content());
            default -> throw new ClientException("Error no esperado al actualizar el Funko");
        }
    }

    /**
     * Envia una solicitud al servidor para eliminar un Funko por su ID.
     *
     * @param token El token de autenticacion del cliente.
     * @param id    El ID del Funko que se desea eliminar.
     * @throws IOException     Si ocurre un error durante la comunicacion con el servidor.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     */
    private void sendRequestDeleteFunko(String token, String id) throws IOException, ClientException {
        Request request = new Request(DELETE, id, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + DELETE);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> {
                Funko responseContent = gson.fromJson(response.content(), new TypeToken<Funko>() {
                }.getType());
                System.out.println("🟢 El Funko eliminado es: " + responseContent);
            }
            case ERROR ->
                    System.err.println("🔴 Error: No se ha podido eliminar el Funko con id: " + id + ". " + response.content()); // No se ha encontrado
            default -> throw new ClientException("Error no esperado al eliminar el Funko");
        }
    }

    /**
     * Envía una solicitud al servidor para cerrar la sesion del cliente.
     * Esta solicitud provoca la desconexion del cliente del servidor.
     *
     * @throws IOException     Si ocurre un error durante la comunicación con el servidor.
     * @throws ClientException Si se recibe una respuesta inesperada del servidor.
     */
    private void sendRequestSalir() throws IOException, ClientException {
        Request request = new Request(SALIR, null, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + SALIR);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case ERROR -> System.err.println("🔴 Error: " + response.content());
            case BYE -> {
                System.out.println("Vamos a cerrar la conexión " + response.content());
                closeConnection();
            }
            default -> throw new ClientException(response.content());
        }
    }
    /**
     * Lee la configuracion del cliente desde un archivo de propiedades.
     *
     * @return Un mapa que contiene las configuraciones leidas.
     */
    public Map<String, String> readConfigFile() {
        try {
            logger.debug("Leyendo el fichero de configuracion");
            PropertiesReader properties = new PropertiesReader("client.properties");

            String keyFile = properties.getProperty("keyFile");
            String keyPassword = properties.getProperty("keyPassword");

            if (keyFile.isEmpty() || keyPassword.isEmpty()) {
                throw new IllegalStateException("Hay errores al procesar el fichero de propiedades o una de ellas está vacía");
            }

            if (!Files.exists(Path.of(keyFile))) {
                throw new FileNotFoundException("No se encuentra el fichero de la clave");
            }

            Map<String, String> configMap = new HashMap<>();
            configMap.put("keyFile", keyFile);
            configMap.put("keyPassword", keyPassword);

            return configMap;
        } catch (FileNotFoundException e) {
            logger.error("Error en clave: " + e.getLocalizedMessage());
            System.exit(1);
            return null;
        } catch (IOException e) {
            logger.error("Error al leer el fichero de configuracion: " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Lee la configuracion del cliente desde un archivo de propiedades.
     *
     * @return Un mapa que contiene las configuraciones leídas.
     */
    private void infoSession(SSLSocket socket) {
        logger.debug("Información de la sesión");
        System.out.println("Información de la sesión");
        try {
            SSLSession session = socket.getSession();
            System.out.println("Servidor: " + session.getPeerHost());
            System.out.println("Cifrado: " + session.getCipherSuite());
            System.out.println("Protocolo: " + session.getProtocol());
            System.out.println("Identificador:" + new BigInteger(session.getId()));
            System.out.println("Creación de la sesión: " + session.getCreationTime());
            X509Certificate certificado = (X509Certificate) session.getPeerCertificates()[0];
            System.out.println("Propietario : " + certificado.getSubjectX500Principal());
            System.out.println("Algoritmo: " + certificado.getSigAlgName());
            System.out.println("Tipo: " + certificado.getType());
            System.out.println("Número Serie: " + certificado.getSerialNumber());
            // expiración del certificado
            System.out.println("Válido hasta: " + certificado.getNotAfter());
        } catch (SSLPeerUnverifiedException ex) {
            logger.error("Error en la sesión: " + ex.getLocalizedMessage());
        }
    }
}