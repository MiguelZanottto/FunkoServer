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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static develop.common.models.Request.Type.*;

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


    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.start();
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    public void start() throws IOException {
        try {

            openConnection();

            token = sendRequestLogin();

            Thread.sleep(150);


            sedRequestGetAllFunkos(token);

            var funko = Funko.builder().id(1L).name("Pepe").price(9.0).build();

            sendRequestPostFunko(token, funko);

            sendRequestGetFunkoById(token, "1");


            sendRequestPostFunko(token, Funko.builder().id(0L).name("Ana").price(8.0).build());
            sendRequestPostFunko(token, Funko.builder().id(0L).name("Luis").price(4.0).build());
            sendRequestPostFunko(token, Funko.builder().id(0L).name("Pedro").price(7.0).build());
            sendRequestPostFunko(token, Funko.builder().id(0L).name("Sara").price(9.0).build());

            sendRequestGetFunkoByModel(token, "OTRO");

            funko = Funko.builder().id(1L).name("Updated").price(10.0).build();

            sendRequestPutFunko(token, funko);

            sendRequestDeleteFunko(token, "1");

            sedRequestGetAllFunkos(token);

            sendRequestPutFunko(token, funko);
            sendRequestDeleteFunko(token, "1");

            sedRequestGetAllFunkos(token);

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

    private void openConnection() throws IOException {
        System.out.println("🔵 Iniciando Cliente");
        Map<String, String> myConfig = readConfigFile();

        logger.debug("Cargando fichero de propiedades");
        System.setProperty("javax.net.ssl.trustStore", myConfig.get("keyFile"));
        System.setProperty("javax.net.ssl.trustStorePassword", myConfig.get("keyPassword"));

        SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) clientFactory.createSocket(HOST, PORT);

        logger.debug("Protocolos soportados: " + Arrays.toString(socket.getSupportedProtocols()));
        socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
        socket.setEnabledProtocols(new String[]{"TLSv1.3"});

        logger.debug("Conectando al servidor: " + HOST + ":" + PORT);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("✅ Cliente conectado a " + HOST + ":" + PORT);

        infoSession(socket);

    }

    private String sendRequestLogin() throws ClientException {
        String myToken = null;
        var loginJson = gson.toJson(new Login("pepe", "pepe1234"));
        Request request = new Request(LOGIN, loginJson, null, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + LOGIN);
        logger.debug("Petición enviada: " + request);
        // Enviamos la petición
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

    private void sedRequestGetAllFunkos(String token) throws ClientException, IOException {
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
                Funko responseContent = gson.fromJson(response.content(), new TypeToken<Funko>() {
                }.getType());
                System.out.println("🟢 Los funkos solicitado por modelo "+ modelo + " son: "  + responseContent);
            }
            case ERROR ->
                    System.err.println("🔴 Error: Funko con modelo: " + modelo + ". " + response.content());
            default -> throw new ClientException("Error no esperado al obtener funkos por modelo"  + modelo);
        }
    }

    private void sendRequestGetFunkoByReleaseData(String token, String anoLanzamiento) throws IOException, ClientException {
        Request request = new Request(GETBYRELEASEDATA, anoLanzamiento, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + GETBYMODEL);
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
                System.out.println("🟢 Los funkos solicitado por ano de lanzamiento "+ anoLanzamiento + " son: "  + responseContent);
            }
            case ERROR ->
                    System.err.println("🔴 Error: Funko con ano de lanzamiento: " + anoLanzamiento + ". " + response.content());
            default -> throw new ClientException("Error no esperado al obtener funkos por ano de lanzamiento "  + anoLanzamiento);
        }
    }

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