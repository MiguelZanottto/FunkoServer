package develop.server.services.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import develop.common.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
/**
 * Clase que proporciona servicios relacionados con la creacion y verificacion de tokens (JSON).
 */
public class TokenService {
    private static TokenService INSTANCE = null;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private TokenService() {
    }
    /**
     * Obtiene una instancia unica de la clase TokenService.
     *
     * @return La instancia unica de TokenService.
     */
    public synchronized static TokenService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TokenService();
        }
        return INSTANCE;
    }
    /**
     * Crea un token JWT con los datos del usuario, el secreto y el tiempo de expiracion especificados.
     *
     * @param user           El usuario para el que se crea el token.
     * @param tokenSecret    El secreto para firmar el token.
     * @param tokenExpiration El tiempo de expiracion del token en milisegundos.
     * @return El token JWT creado.
     */
    public String createToken(User user, String tokenSecret, long tokenExpiration) {
        logger.debug("Creando token");
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        return JWT.create()
                .withClaim("userid", user.id())
                .withClaim("username", user.username())
                .withClaim("rol", user.role().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenExpiration))
                .sign(algorithm);
    }
    /**
     * Crea un token JWT con los datos del usuario, el secreto y el tiempo de expiracion especificados.
     *
     * @param user           El usuario para el que se crea el token.
     * @param tokenSecret    El secreto para firmar el token.
     * tokenExpiration El tiempo de expiración del token en milisegundos.
     * @return El token JWT creado.
     */
    public boolean verifyToken(String token, String tokenSecret, User user) {
        logger.debug("Verificando token");
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); // Creamos el verificador
            DecodedJWT decodedJWT = verifier.verify(token);
            logger.debug("Token verificado");
            return decodedJWT.getClaim("userid").asLong() == user.id() &&
                    decodedJWT.getClaim("username").asString().equals(user.username()) &&
                    decodedJWT.getClaim("rol").asString().equals(user.role().toString());
        } catch (Exception e) {
            logger.error("Error al verificar el token: " + e.getMessage());
            return false;
        }
    }
    /**
     * Verifica la validez de un token JWT con respecto al secreto proporcionado.
     *
     * @param token       El token JWT a verificar.
     * @param tokenSecret El secreto para verificar la firma del token.
     * @return true si el token es valido con el secreto, de lo contrario, false.
     */
    public boolean verifyToken(String token, String tokenSecret) {
        logger.debug("Verificando token");
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            logger.debug("Token verificado");
            return true;
        } catch (Exception e) {
            logger.error("Error al verificar el token: " + e.getMessage());
            return false;
        }
    }
    /**
     * Obtiene los claims contenidos en un token JWT, verificando su validez con respecto al secreto proporcionado.
     *
     * @param token       El token JWT del que se obtienen las reclamaciones.
     * @param tokenSecret El secreto para verificar la firma del token.
     * @return Un mapa de reclamaciones del token o null si el token no es valido.
     */
    public java.util.Map<String, com.auth0.jwt.interfaces.Claim> getClaims(String token, String tokenSecret) {
        logger.debug("Verificando token");
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            logger.debug("Token verificado");
            return decodedJWT.getClaims();
        } catch (Exception e) {
            logger.error("Error al verificar el token: " + e.getMessage());
            return null;
        }
    }
}