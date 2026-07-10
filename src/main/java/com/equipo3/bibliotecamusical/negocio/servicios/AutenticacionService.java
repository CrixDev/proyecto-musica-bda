package com.equipo3.bibliotecamusical.negocio.servicios;

import com.equipo3.bibliotecamusical.daos.IUsuarioDAO;
import com.equipo3.bibliotecamusical.dtos.CredencialesDTO;
import com.equipo3.bibliotecamusical.dtos.RegistroUsuarioDTO;
import com.equipo3.bibliotecamusical.dtos.UsuarioDTO;
import com.equipo3.bibliotecamusical.entidades.Usuario;
import com.equipo3.bibliotecamusical.negocio.excepciones.AutenticacionException;
import com.equipo3.bibliotecamusical.negocio.excepciones.DuplicadoException;
import com.equipo3.bibliotecamusical.negocio.excepciones.NegocioException;
import com.equipo3.bibliotecamusical.negocio.excepciones.ValidacionException;
import com.equipo3.bibliotecamusical.negocio.mapeadores.UsuarioMapper;
import com.equipo3.bibliotecamusical.negocio.seguridad.Passwords;
import com.equipo3.bibliotecamusical.negocio.seguridad.SesionActual;
import com.equipo3.bibliotecamusical.negocio.validadores.ValidadorUsuario;
import com.equipo3.bibliotecamusical.negocio.validadores.Validaciones;
import com.equipo3.bibliotecamusical.persistencia.LlaveDuplicadaException;
import com.equipo3.bibliotecamusical.persistencia.PersistenciaException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/** Registro, inicio y cierre de sesion. Nunca almacena contrasenas en texto plano. */
public class AutenticacionService {

    private final IUsuarioDAO usuarioDAO;

    /**
     * Constructor que inyecta la dependencia del DAO de usuarios.
     * 
     * @param usuarioDAO Implementación del DAO de usuarios para persistencia.
     */
    public AutenticacionService(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Autentica a un usuario en el sistema validando sus credenciales.
     * 
     * @param credenciales DTO con el nombre de usuario y contraseña textoplano.
     * @return UsuarioDTO con los datos del usuario autenticado si el proceso es exitoso.
     * @throws NegocioException Si ocurre un error en las reglas de negocio o base de datos.
     * @throws ValidacionException Si los campos de las credenciales no cumplen el formato básico.
     * @throws AutenticacionException Si las credenciales son incorrectas o el usuario no existe.
     */
    public UsuarioDTO login(CredencialesDTO credenciales) throws NegocioException {
        // 1. Validaciones estructurales
        if (credenciales == null) {
            throw new ValidacionException("Las credenciales de acceso no pueden ser nulas.");
        }

        try {
            // CORRECCIÓN 1: Se usa .usuarioOCorreo() que es el nombre exacto de tu record
            Usuario usuario = usuarioDAO.buscarPorNombreUsuarioOCorreo(credenciales.usuarioOCorreo())
                    .orElseThrow(() -> new AutenticacionException("El nombre de usuario o la contraseña son incorrectos."));

            // CORRECCIÓN 2: Se usa .contrasena() en lugar de password()
            boolean passwordCorrecto = Passwords.verificar(credenciales.contrasena(), usuario.getContrasena());

            if (!passwordCorrecto) {
                throw new AutenticacionException("El nombre de usuario o la contraseña son incorrectos.");
            }

            // 4. Mapear y registrar sesión usando tu método .iniciar
            UsuarioDTO usuarioAutenticado = UsuarioMapper.aDTO(usuario);

            // Se le pasa la entidad 'usuario' a tu clase SesionActual
            SesionActual.iniciar(usuario); 

            return usuarioAutenticado;

        } catch (AutenticacionException e) {
            throw e; 
        } catch (Exception e) {
            throw new NegocioException("Error interno del sistema al intentar iniciar sesión.", e);
        }
    }
    /**
     * Registra un nuevo usuario en el sistema (Requerido por el Runner).
     * 
     * @param dto Datos de registro recolectados.
     * @return UsuarioDTO del usuario recién creado.
     * @throws NegocioException Si ocurre un problema de duplicados o persistencia.
     */
    public UsuarioDTO registrar(RegistroUsuarioDTO dto) throws NegocioException {
        if (dto == null) {
            throw new ValidacionException("Los datos de registro no pueden ser nulos.");
        }

        try {
            // Validar si ya existen el nombre de usuario o correo en la base de datos
            if (usuarioDAO.existeNombreUsuario(dto.nombreUsuario())) {
                throw new NegocioException("El nombre de usuario ya se encuentra registrado.");
            }
            if (usuarioDAO.existeCorreo(dto.correo())) {
                throw new NegocioException("El correo electrónico ya se encuentra registrado.");
            }

            // Crear la entidad de dominio mapeando los datos del DTO
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(dto.nombreUsuario());
            nuevoUsuario.setCorreo(dto.correo());
            nuevoUsuario.setFechaRegistro(LocalDate.now());
            nuevoUsuario.setImagenPerfil(dto.imagenPerfil());

            // Hashear la contraseña antes de mandarla a MongoDB usando tu utilería Passwords
            String passwordHasheada = Passwords.hash(dto.contrasena());
            nuevoUsuario.setContrasena(passwordHasheada);

            // Guardar en MongoDB mediante el DAO
            Usuario usuarioGuardado = usuarioDAO.crear(nuevoUsuario);

            // Retornar la versión DTO segura para la presentación
            return UsuarioMapper.aDTO(usuarioGuardado);

        } catch (NegocioException e) {
            throw e;
        } catch (Exception e) {
            throw new NegocioException("Error interno al registrar el usuario.", e);
        }
    }

    /**
     * Cierra la sesión del usuario actualmente autenticado en el sistema.
     */
    public void logout() {
        SesionActual.cerrar();
    }
}