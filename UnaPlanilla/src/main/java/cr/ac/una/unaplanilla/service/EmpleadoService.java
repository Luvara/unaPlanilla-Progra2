package cr.ac.una.unaplanilla.service;

import cr.ac.una.unaplanilla.model.Empleado;
import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.util.EntityManagerHelper;
import cr.ac.una.unaplanilla.util.Respuesta;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 *
 * @author Luvara
 */
public class EmpleadoService {

    EntityManager em = EntityManagerHelper.getManager();
    private EntityTransaction et;

    public Respuesta getUsuario(String usuario, String clave) {
        try {
            Query qryEmpleado = em.createNamedQuery("Empleado.findByUsuarioClave", Empleado.class);
            qryEmpleado.setParameter("usuario", usuario);
            qryEmpleado.setParameter("clave", clave);
            EmpleadoDto empleadoDto = new EmpleadoDto((Empleado) qryEmpleado.getSingleResult());
            return new Respuesta(true, "", "", "Empleado", empleadoDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un usuario con las credenciales ingresadas.", "getUsuario NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al consultar el usuario.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar el usuario.", "getUsuario NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo el usuario [" + usuario + "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario.", "getUsuario " + ex.getMessage());
        }
    }

    public Respuesta guardarEmpleado(EmpleadoDto empleadoDto) {
        try {
            et = em.getTransaction();
            et.begin();
            Empleado empleado;
            if (empleadoDto.getId() != null && empleadoDto.getId() > 0) {
                empleado = em.find(Empleado.class, empleadoDto.getId());
                if (empleado == null) {
                    et.rollback();
                    return new Respuesta(false, "No se encontro el empleado a modificar", "guardarEmpleado NoResultException");

                }
                empleado.Actualizar(empleadoDto);
                empleado = em.merge(empleado);
            } else {
                empleado = new Empleado(empleadoDto);
                em.persist(empleado);
            }
            et.commit();
            return new Respuesta(true, "", "", "Empleado", new EmpleadoDto(empleado));
        } catch (Exception ex) {
            et.rollback();
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al guardar el empleado.", ex);
            return new Respuesta(false, "Ocurrio un error al guardar el empleado.", "guardarEmpleado" + ex.getMessage());
        }
    }

    public Respuesta getCedula(String cedula) {
        try {
            Query qryEmpleado = em.createNamedQuery("Empleado.findByCedula", Empleado.class);
            qryEmpleado.setParameter("cedula", cedula);
            EmpleadoDto empleadoDto = new EmpleadoDto((Empleado) qryEmpleado.getSingleResult());
            return new Respuesta(true, "", "", "Empleado", empleadoDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un usuario con la cedula ingresada.", "getCedula NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al consultar la cedula.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar la cedula.", "getCedula NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo la cedula [" + cedula + "]", ex);
            return new Respuesta(false, "Error obteniendo la cedula.", "getCedula " + ex.getMessage());
        }
    }
    
    public Respuesta getDatosId(Long id) {
        try {
            Query qryEmpleado = em.createNamedQuery("Empleado.findById", Empleado.class);
            qryEmpleado.setParameter("id", id);
            EmpleadoDto empleadoDto = new EmpleadoDto((Empleado) qryEmpleado.getSingleResult());
            return new Respuesta(true, "", "", "Empleado", empleadoDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un usuario con el id ingresado.", "getDatosId NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al consultar el id.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar el id.", "getDatosId NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo el id [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el id.", "getDatosId " + ex.getMessage());
        }
    }

    public Respuesta eliminarEmpleado(Long id) {
        try {
            et = em.getTransaction();
            et.begin();
            Empleado empleado;
            if (id != null && id > 0) {
                empleado = em.find(Empleado.class, id);
                if (empleado == null) {
                    et.rollback();
                    return new Respuesta(false, "No se encontro el empleado a eliminar.", "eliminarEmpleado NoResultException");
                }
                em.remove(empleado);
            } else {
                et.rollback();
                return new Respuesta(false, "Debe cargar el empleado a eliminar.", "eliminarEmpleado NoResultException");
            }
            et.commit();
            return new Respuesta(true, "", "");
        } catch (Exception ex) {
            et.rollback();
            if (ex.getCause() != null && ex.getCause().getCause().getClass() == SQLIntegrityConstraintViolationException.class) {
                return new Respuesta(false, "No se puede eliminar el empleado porque tiene relaciones con otros registros.", "eliminarEmpleado " + ex.getMessage());
            }
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "ocurrio un error al guardar el empleado", ex);
            return new Respuesta(false, "Ocurrio un error al eliminar el empleado.", "eliminarEmpleado " + ex.getMessage());
        }
    }
    
    public Respuesta getEmpleados(String cedula,String nombre,String apellido){
        
            try {
            Query qryEmpleado = em.createNamedQuery("Empleado.findByInfoEmpleado", Empleado.class);
            qryEmpleado.setParameter("cedula", cedula);
            qryEmpleado.setParameter("nombre", nombre);
            qryEmpleado.setParameter("primerApellido", apellido);
            
            List<EmpleadoDto> empleados = new ArrayList<>();
            List<Empleado> empleado= qryEmpleado.getResultList();
            for(int i=0; i<empleado.size(); i++){
                empleados.add(new EmpleadoDto(empleado.get(i)));
            }
            return new Respuesta(true, "", "", "Empleado", empleados);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un usuario con las credenciales ingresadas.", "getEmpleados NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al consultar el usuario.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar el usuario.", "getUsuario NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo informacion del empleado [" + cedula+ " " +nombre+ "" +apellido+ "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario.", "getEmpleados " + ex.getMessage());
        }
        
    }
}
