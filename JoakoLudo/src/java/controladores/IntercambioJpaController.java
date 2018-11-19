/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Model.EstadoIntercambio;
import Model.Intercambio;
import Model.Libro;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.RollbackFailureException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Pyther
 */
public class IntercambioJpaController implements Serializable {

    public IntercambioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Intercambio intercambio) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            EstadoIntercambio estadoIntercambioId = intercambio.getEstadoIntercambioId();
            if (estadoIntercambioId != null) {
                estadoIntercambioId = em.getReference(estadoIntercambioId.getClass(), estadoIntercambioId.getId());
                intercambio.setEstadoIntercambioId(estadoIntercambioId);
            }
            Libro libro1Id = intercambio.getLibro1Id();
            if (libro1Id != null) {
                libro1Id = em.getReference(libro1Id.getClass(), libro1Id.getId());
                intercambio.setLibro1Id(libro1Id);
            }
            Libro libro2Id = intercambio.getLibro2Id();
            if (libro2Id != null) {
                libro2Id = em.getReference(libro2Id.getClass(), libro2Id.getId());
                intercambio.setLibro2Id(libro2Id);
            }
            em.persist(intercambio);
            if (estadoIntercambioId != null) {
                estadoIntercambioId.getIntercambioCollection().add(intercambio);
                estadoIntercambioId = em.merge(estadoIntercambioId);
            }
            if (libro1Id != null) {
                libro1Id.getIntercambioCollection().add(intercambio);
                libro1Id = em.merge(libro1Id);
            }
            if (libro2Id != null) {
                libro2Id.getIntercambioCollection().add(intercambio);
                libro2Id = em.merge(libro2Id);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Intercambio intercambio) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Intercambio persistentIntercambio = em.find(Intercambio.class, intercambio.getId());
            EstadoIntercambio estadoIntercambioIdOld = persistentIntercambio.getEstadoIntercambioId();
            EstadoIntercambio estadoIntercambioIdNew = intercambio.getEstadoIntercambioId();
            Libro libro1IdOld = persistentIntercambio.getLibro1Id();
            Libro libro1IdNew = intercambio.getLibro1Id();
            Libro libro2IdOld = persistentIntercambio.getLibro2Id();
            Libro libro2IdNew = intercambio.getLibro2Id();
            if (estadoIntercambioIdNew != null) {
                estadoIntercambioIdNew = em.getReference(estadoIntercambioIdNew.getClass(), estadoIntercambioIdNew.getId());
                intercambio.setEstadoIntercambioId(estadoIntercambioIdNew);
            }
            if (libro1IdNew != null) {
                libro1IdNew = em.getReference(libro1IdNew.getClass(), libro1IdNew.getId());
                intercambio.setLibro1Id(libro1IdNew);
            }
            if (libro2IdNew != null) {
                libro2IdNew = em.getReference(libro2IdNew.getClass(), libro2IdNew.getId());
                intercambio.setLibro2Id(libro2IdNew);
            }
            intercambio = em.merge(intercambio);
            if (estadoIntercambioIdOld != null && !estadoIntercambioIdOld.equals(estadoIntercambioIdNew)) {
                estadoIntercambioIdOld.getIntercambioCollection().remove(intercambio);
                estadoIntercambioIdOld = em.merge(estadoIntercambioIdOld);
            }
            if (estadoIntercambioIdNew != null && !estadoIntercambioIdNew.equals(estadoIntercambioIdOld)) {
                estadoIntercambioIdNew.getIntercambioCollection().add(intercambio);
                estadoIntercambioIdNew = em.merge(estadoIntercambioIdNew);
            }
            if (libro1IdOld != null && !libro1IdOld.equals(libro1IdNew)) {
                libro1IdOld.getIntercambioCollection().remove(intercambio);
                libro1IdOld = em.merge(libro1IdOld);
            }
            if (libro1IdNew != null && !libro1IdNew.equals(libro1IdOld)) {
                libro1IdNew.getIntercambioCollection().add(intercambio);
                libro1IdNew = em.merge(libro1IdNew);
            }
            if (libro2IdOld != null && !libro2IdOld.equals(libro2IdNew)) {
                libro2IdOld.getIntercambioCollection().remove(intercambio);
                libro2IdOld = em.merge(libro2IdOld);
            }
            if (libro2IdNew != null && !libro2IdNew.equals(libro2IdOld)) {
                libro2IdNew.getIntercambioCollection().add(intercambio);
                libro2IdNew = em.merge(libro2IdNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = intercambio.getId();
                if (findIntercambio(id) == null) {
                    throw new NonexistentEntityException("The intercambio with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Intercambio intercambio;
            try {
                intercambio = em.getReference(Intercambio.class, id);
                intercambio.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The intercambio with id " + id + " no longer exists.", enfe);
            }
            EstadoIntercambio estadoIntercambioId = intercambio.getEstadoIntercambioId();
            if (estadoIntercambioId != null) {
                estadoIntercambioId.getIntercambioCollection().remove(intercambio);
                estadoIntercambioId = em.merge(estadoIntercambioId);
            }
            Libro libro1Id = intercambio.getLibro1Id();
            if (libro1Id != null) {
                libro1Id.getIntercambioCollection().remove(intercambio);
                libro1Id = em.merge(libro1Id);
            }
            Libro libro2Id = intercambio.getLibro2Id();
            if (libro2Id != null) {
                libro2Id.getIntercambioCollection().remove(intercambio);
                libro2Id = em.merge(libro2Id);
            }
            em.remove(intercambio);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Intercambio> findIntercambioEntities() {
        return findIntercambioEntities(true, -1, -1);
    }

    public List<Intercambio> findIntercambioEntities(int maxResults, int firstResult) {
        return findIntercambioEntities(false, maxResults, firstResult);
    }

    private List<Intercambio> findIntercambioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Intercambio.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Intercambio findIntercambio(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Intercambio.class, id);
        } finally {
            em.close();
        }
    }

    public int getIntercambioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Intercambio> rt = cq.from(Intercambio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
