package cr.ac.una.unaplanilla.service;

import cr.ac.una.unaplanilla.model.Empleado;
import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.model.TipoPlanilla;
import cr.ac.una.unaplanilla.model.TipoPlanillaDto;
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
import javax.persistence.TypedQuery;

/**
 *
 * @author Luvara
 */
public class TipoPlanillaService {

    EntityManager em = EntityManagerHelper.getInstance().getManager();
    private EntityTransaction et;

    public Respuesta getTipoPlanilla(Long id) {
        try {
            Query qryTipoPlanilla = em.createNamedQuery("TipoPlanilla.findById", TipoPlanilla.class);
            qryTipoPlanilla.setParameter("id", id);
            TipoPlanilla tipoPlanilla = (TipoPlanilla) qryTipoPlanilla.getSingleResult();
            TipoPlanillaDto tipoPlanillaDto = new TipoPlanillaDto(tipoPlanilla);
            for (Empleado emp : tipoPlanilla.getEmpleados()) {
                tipoPlanillaDto.getEmpleados().add(new EmpleadoDto(emp));
            }
            return new Respuesta(true, "", "", "TipoPlanilla", tipoPlanillaDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe un tipo de planilla con el código ingresado.", "getTipoPlanilla NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al consultar el tipo de planilla.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar el tipo de planilla.", "getTipoPlanilla NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo el tipo de planilla [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el tipo de planilla.", "getTipoPlanilla " + ex.getMessage());
        }
    }

    public Respuesta guardarTipoPlanilla(TipoPlanillaDto tipoPlanillaDto) {
        try {
            et = em.getTransaction();
            et.begin();
            TipoPlanilla tipoPlanilla;
            if (tipoPlanillaDto.getId() != null && tipoPlanillaDto.getId() > 0) {
                tipoPlanilla = em.find(TipoPlanilla.class, tipoPlanillaDto.getId());
                if (tipoPlanilla == null) {
                    et.rollback();
                    return new Respuesta(false, "No se encrontró el tipo de planilla a modificar.", "guardarEmpleado NoResultException");
                }
                tipoPlanilla.actualizar(tipoPlanillaDto);
                for (EmpleadoDto emp : tipoPlanillaDto.getEmpleadosEliminados()) {
                    tipoPlanilla.getEmpleados().remove(new Empleado(emp.getId()));
                }
                if (!tipoPlanillaDto.getEmpleados().isEmpty()) {
                    for (EmpleadoDto emp : tipoPlanillaDto.getEmpleados()) {
                        if (emp.getModificado()) {
                            Empleado empleado = em.find(Empleado.class, emp.getId());
                            empleado.getTiposPlanilla().add(tipoPlanilla);
                            tipoPlanilla.getEmpleados().add(empleado);
                        }
                    }
                }
                tipoPlanilla = em.merge(tipoPlanilla);
            } else {
                tipoPlanilla = new TipoPlanilla(tipoPlanillaDto);
                em.persist(tipoPlanilla);
            }
            et.commit();
            return new Respuesta(true, "", "", "TipoPlanilla", new TipoPlanillaDto(tipoPlanilla));
        } catch (Exception ex) {
            et.rollback();
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al guardar el tipo de planilla.", ex);
            return new Respuesta(false, "Ocurrio un error al guardar el tipo de planilla.", "guardarTipoPlanilla " + ex.getMessage());
        }
    }

    public Respuesta eliminarTipoPlanilla(Long id) {
        try {
            et = em.getTransaction();
            et.begin();
            TipoPlanilla tipoPlanilla;
            if (id != null && id > 0) {
                tipoPlanilla = em.find(TipoPlanilla.class, id);
                if (tipoPlanilla == null) {
                    et.rollback();
                    return new Respuesta(false, "No se encrontró el tipo de planilla a eliminar.", "eliminarTipoPlanilla NoResultException");
                }
                em.remove(tipoPlanilla);
            } else {
                et.rollback();
                return new Respuesta(false, "Debe cargar el tipo de planilla a eliminar.", "eliminarTipoPlanilla NoResultException");
            }
            et.commit();
            return new Respuesta(true, "", "");
        } catch (Exception ex) {
            et.rollback();
            if (ex.getCause() != null && ex.getCause().getCause().getClass() == SQLIntegrityConstraintViolationException.class) {
                return new Respuesta(false, "No se puede eliminar el tipo de planilla porque tiene relaciones con otros registros.", "eliminarTipoPlanilla " + ex.getMessage());
            }
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE, "Ocurrio un error al eliminar el tipo de planilla.", ex);
            return new Respuesta(false, "Ocurrio un error al eliminar el tipo de planilla.", "eliminarTipoPlanilla " + ex.getMessage());
        }
    }

    /*public Respuesta getBusqueda(String planillaPorMes, String codigo, String descripcion, String cedula) {

        try {
            TypedQuery<TipoPlanilla> qryPlanilla = em.createNamedQuery("TipoPlanillas.findByTplanillas", TipoPlanilla.class);
            qryPlanilla.setParameter("codigo", codigo);
            qryPlanilla.setParameter("planillaPorMes", planillaPorMes);
            qryPlanilla.setParameter("descripcion", descripcion);
            qryPlanilla.setParameter("cedula", cedula);

            List<TipoPlanillaDto> tipoP = new ArrayList<>();
            List<TipoPlanilla> planillas = qryPlanilla.getResultList();
            for (TipoPlanilla planilla : planillas) {
                tipoP.add(new TipoPlanillaDto(planilla));
            }
            return new Respuesta(true, "", "", "TipoPlanilla", tipoP);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe una planilla con las credenciales ingresadas.", "getBusqueda NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE, "Ocurrio un error al consultar la planilla.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar la planilla", "getBusqueda NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE, "Error obteniendo informacion de la planilla [" + planillaPorMes + " " + codigo + "" + descripcion + "]", ex);
            return new Respuesta(false, "Error obteniendo la planilla.", "getBusqueda " + ex.getMessage());
        }

    }*/
    public Respuesta getBusqueda(String planillaPorMes, String codigo, String descripcion) {

        try {
            Query qryPlanilla = em.createNamedQuery("TipoPlanillas.findByTplanillas", TipoPlanilla.class);
            qryPlanilla.setParameter("codigo", codigo);
            qryPlanilla.setParameter("planillaPorMes", planillaPorMes);
            qryPlanilla.setParameter("descripcion", descripcion);

            List<TipoPlanilla> tipoPlanillas = qryPlanilla.getResultList();
            List<TipoPlanillaDto> tipoPlanillaDtos = new ArrayList<>();

            for (int i = 0; i < tipoPlanillas.size(); i++) {
                tipoPlanillaDtos.add(new TipoPlanillaDto(tipoPlanillas.get(i)));
            }
            return new Respuesta(true, "", "", "TipoPlanilla", tipoPlanillaDtos);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existe una planilla con las credenciales ingresadas.", "getBusqueda NoResultException");
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE, "Ocurrio un error al consultar la planilla.", ex);
            return new Respuesta(false, "Ocurrio un error al consultar la planilla", "getBusqueda NonUniqueResultException");
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaService.class.getName()).log(Level.SEVERE, "Error obteniendo informacion de la planilla [" + planillaPorMes + " " + codigo + "" + descripcion + "]", ex);
            return new Respuesta(false, "Error obteniendo la planilla.", "getBusqueda " + ex.getMessage());
        }

    }

    public Respuesta getTipoPlanillasPorCedula(String cedula) {
        try {
            Query qryPlanilla = em.createNamedQuery("TipoPlanilla.findByCedula", TipoPlanilla.class);
            qryPlanilla.setParameter("cedula", cedula);

            List<TipoPlanilla> tipoPlanillas = qryPlanilla.getResultList();
            List<TipoPlanillaDto> tipoPlanillaDtos = new ArrayList<>();

            for (TipoPlanilla tipoPlanilla : tipoPlanillas) {
                tipoPlanillaDtos.add(new TipoPlanillaDto(tipoPlanilla));
            }

            return new Respuesta(true, "", "", "TipoPlanilla", tipoPlanillaDtos);
        } catch (NoResultException ex) {
            return new Respuesta(false, "No existen planillas asociadas a la cédula ingresada.", "getTipoPlanillasPorCedula NoResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo planillas.", ex);
            return new Respuesta(false, "Error obteniendo planillas.", "getTipoPlanillasPorCedula " + ex.getMessage());
        }
    }

}
