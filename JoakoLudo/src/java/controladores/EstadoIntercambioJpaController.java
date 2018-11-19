/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import Model.EstadoIntercambio;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Model.Intercambio;
import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Pyther
 */
public class EstadoIntercambioJpaController implements Serializable {

    public EstadoIntercambioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(EstadoIntercambio estadoIntercambio) throws RollbackFailureException, Exception {
        if (estadoIntercambio.getIntercambioCollection() == null) {
            estadoIntercambio.setIntercambioCollection(new ArrayList<Intercambio>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Intercambio> attachedIntercambioCollection = new ArrayList<Intercambio>();
            for (Intercambio intercambioCollectionIntercambioToAttach : estadoIntercambio.getIntercambioCollection()) {
                intercambioCollectionIntercambioToAttach = em.getReference(intercambioCollectionIntercambioToAttach.getClass(), intercambioCollectionIntercambioToAttach.getId());
                attachedIntercambioCollection.add(intercambioCollectionIntercambioToAttach);
            }
            estadoIntercambio.setIntercambioCollection(attachedIntercambioCollection);
            em.persist(estadoIntercambio);
            for (Intercambio intercambioCollectionIntercambio : estadoIntercambio.getIntercambioCollection()) {
                EstadoIntercambio oldEstadoIntercambioIdOfIntercambioCollectionIntercambio = intercambioCollectionIntercambio.getEstadoIntercambioId();
                intercambioCollectionIntercambio.setEstadoIntercambioId(estadoIntercambio);
                intercambioCollectionIntercambio = em.merge(intercambioCollectionIntercambio);
                if (oldEstadoIntercambioIdOfIntercambioCollectionIntercambio != null) {
                    oldEstadoIntercambioIdOfIntercambioCollectionIntercambio.getIntercambioCollection().remove(intercambioCollectionIntercambio);
                    oldEstadoIntercambioIdOfIntercambioCollectionIntercambio = em.merge(oldEstadoIntercambioIdOfIntercambioCollectionIntercambio);
                }
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

    public void edit(EstadoIntercambio estadoIntercambio) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            EstadoIntercambio persistentEstadoIntercambio = em.find(EstadoIntercambio.class, estadoIntercambio.getId());
            Collection<Intercambio> intercambioCollectionOld = persistentEstadoIntercambio.getIntercambioCollection();
            Collection<Intercambio> intercambioCollectionNew = estadoIntercambio.getIntercambioCollection();
            List<String> illegalOrphanMessages = null;
            for (Intercambio intercambioCollectionOldIntercambio : intercambioCollectionOld) {
                if (!intercambioCollectionNew.contains(intercambioCollectionOldIntercambio)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Intercambio " + intercambioCollectionOldIntercambio + " since its estadoIntercambioId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Intercambio> attachedIntercambioCollectionNew = new ArrayList<Intercambio>();
            for (Intercambio intercambioCollectionNewIntercambioToAttach : intercambioCollectionNew) {
                intercambioCollectionNewIntercambioToAttach = em.getReference(intercambioCollectionNewIntercambioToAttach.getClass(), intercambioCollectionNewIntercambioToAttach.getId());
                attachedIntercambioCollectionNew.add(intercambioCollectionNewIntercambioToAttach);
            }
            intercambioCollectionNew = attachedIntercambioCollectionNew;
            estadoIntercambio.setIntercambioCollection(intercambioCollectionNew);
            estadoIntercambio = em.merge(estadoIntercambio);
            for (Intercambio intercambioCollectionNewIntercambio : intercambioCollectionNew) {
                if (!intercambioCollectionOld.contains(intercambioCollectionNewIntercambio)) {
                    EstadoIntercambio oldEstadoIntercambioIdOfIntercambioCollectionNewIntercambio = intercambioCollectionNewIntercambio.getEstadoIntercambioId();
                    intercambioCollectionNewIntercambio.setEstadoIntercambioId(estadoIntercambio);
                    intercambioCollectionNewIntercambio = em.merge(intercambioCollectionNewIntercambio);
                    if (oldEstadoIntercambioIdOfIntercambioCollectionNewIntercambio != null && !oldEstadoIntercambioIdOfIntercambioCollectionNewIntercambio.equals(estadoIntercambio)) {
                        oldEstadoIntercambioIdOfIntercambioCollectionNewIntercambio.getIntercambioCollection().remove(intercambioCollectionNewIntercambio);
                        oldEstadoIntercambioIdOfIntercambioCollectionNewIntercambio = em.merge(oldEstadoIntercambioIdOfIntercambioCollectionNewIntercambio);
                    }
                }
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
                Integer id = estadoIntercambio.getId();
                if (findEstadoIntercambio(id) == null) {
                    throw new NonexistentEntityException("The estadoIntercambio with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            EstadoIntercambio estadoIntercambio;
            try {
                estadoIntercambio = em.getReference(EstadoIntercambio.class, id);
                estadoIntercambio.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estadoIntercambio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Intercambio> intercambioCollectionOrphanCheck = estadoIntercambio.getIntercambioCollection();
            for (Intercambio intercambioCollectionOrphanCheckIntercambio : intercambioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This EstadoIntercambio (" + estadoIntercambio + ") cannot be destroyed since the Intercambio " + intercambioCollectionOrphanCheckIntercambio + " in its intercambioCollection field has a non-nullable estadoIntercambioId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(estadoIntercambio);
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

    public List<EstadoIntercambio> findEstadoIntercambioEntities() {
        return findEstadoIntercambioEntities(true, -1, -1);
    }

    public List<EstadoIntercambio> findEstadoIntercambioEntities(int maxResults, int firstResult) {
        return findEstadoIntercambioEntities(false, maxResults, firstResult);
    }

    private List<EstadoIntercambio> findEstadoIntercambioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(EstadoIntercambio.class));
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

    public EstadoIntercambio findEstadoIntercambio(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(EstadoIntercambio.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadoIntercambioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<EstadoIntercambio> rt = cq.from(EstadoIntercambio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
