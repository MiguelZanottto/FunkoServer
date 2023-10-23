package develop.server;


import develop.common.models.IdGenerator;
import develop.common.utils.PropertiesReader;
import develop.server.repositories.funkos.FunkosRepositoryImpl;
import develop.server.services.services.database.DatabaseManager;
import develop.server.services.services.funkos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    public static final String TOKEN_SECRET = "ManoTengoFe";
    public static final long TOKEN_EXPIRATION = 10000;
    private static final AtomicLong clientNumber = new AtomicLong(0);
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PUERTO = 3000;
    private static final FunkosServiceImpl funkosService = FunkosServiceImpl.getInstance(FunkosRepositoryImpl.getInstance(DatabaseManager.getInstance(), IdGenerator.getInstance()), FunkosNotificationImpl.getInstance(), FunkosStorageImpl.getInstance());

    public static void main(String[] args) {
        try {
            funkosService.getNotifications().subscribe(
                    notificacion -> {
                        switch (notificacion.getTipo()) {
                            case NEW:
                                System.out.println("🟢 Funko insertado: " + notificacion.getContenido());
                                break;
                            case UPDATED:
                                System.out.println("🟠 Funko actualizado: " + notificacion.getContenido());
                                break;
                            case DELETED:
                                System.out.println("🔴 Funko eliminado: " + notificacion.getContenido());
                                break;
                        }
                    },
                    error -> System.err.println("Se ha producido un error: " + error),
                    () -> System.out.println("Completado")
            );


            var myConfig = readConfigFile();

            logger.debug("Configurando TSL");
            System.setProperty("javax.net.ssl.keyStore", myConfig.get("keyFile"));
            System.setProperty("javax.net.ssl.keyStorePassword", myConfig.get("keyPassword"));

            SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) serverFactory.createServerSocket(PUERTO);

            logger.debug("Protocolos soportados: " + Arrays.toString(serverSocket.getSupportedProtocols()));
            serverSocket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});


            System.out.println("🚀 Servidor escuchando en el puerto 3000");

            while (true) {
                new ClientHandler(serverSocket.accept(), clientNumber.incrementAndGet(), funkosService).start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static Map<String, String> readConfigFile() {
        try {
            logger.debug("Leyendo el fichero de propiedades");
            PropertiesReader properties = new PropertiesReader("server.properties");

            String keyFile = properties.getProperty("keyFile");
            String keyPassword = properties.getProperty("keyPassword");
            String tokenSecret = properties.getProperty("tokenSecret");
            String tokenExpiration = properties.getProperty("tokenExpiration");

            if (keyFile.isEmpty() || keyPassword.isEmpty()) {
                throw new IllegalStateException("Hay errores al procesar el fichero de propiedades o una de ellas está vacía");
            }

            if (!Files.exists(Path.of(keyFile))) {
                throw new FileNotFoundException("No se encuentra el fichero de la clave");
            }

            Map<String, String> configMap = new HashMap<>();
            configMap.put("keyFile", keyFile);
            configMap.put("keyPassword", keyPassword);
            configMap.put("tokenSecret", tokenSecret);
            configMap.put("tokenExpiration", tokenExpiration);

            return configMap;
        } catch (FileNotFoundException e) {
            logger.error("Error en clave: " + e.getLocalizedMessage());
            System.exit(1);
            return null;
        } catch (IOException e) {
            logger.error("Error al leer el fichero de propiedades: " + e.getLocalizedMessage());
            return null;
        }
    }
}