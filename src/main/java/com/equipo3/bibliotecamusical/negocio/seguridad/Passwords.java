package com.equipo3.bibliotecamusical.negocio.seguridad;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Hashing y verificacion de contrasenas con bcrypt. Las contrasenas nunca se
 * guardan en texto plano. Costo 12 (equilibrio seguridad/velocidad).
 */
public final class Passwords {

    private static final int COSTO = 12;

    private Passwords() {
    }

    public static String hash(String contrasenaPlana) {
        return BCrypt.hashpw(contrasenaPlana, BCrypt.gensalt(COSTO));
    }

    public static boolean verificar(String contrasenaPlana, String hash) {
        if (contrasenaPlana == null || hash == null || hash.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(contrasenaPlana, hash);
        } catch (IllegalArgumentException e) {
            // hash con formato invalido
            return false;
        }
    }
}
