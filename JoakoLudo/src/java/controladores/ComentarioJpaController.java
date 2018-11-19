/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import Model.Comentario;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Model.Libro;
import Model.Usuario;
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
public class ComentarioJpaController implements Serializable {

    public ComentarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comentario comentario) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Libro libroId = comentario.getLibroId();
            if (libroId != null) {
                libroId = em.getReference(libroId.getClass(), libroId.getId());
                comentario.setLibroId(libroId);
            }
            Usuario usuarioId = comentario.getUsuarioId();
            if (usuarioId != null) {
                usuarioId = em.getReference(usuarioId.getClass(), usuarioId.getId());
                comentario.setUsuarioId(usuarioId);
            }
            em.persist(comentario);
            if (libroId != null) {
                libroId.getComentarioCollection().add(comentario);
                libroId = em.merge(libroId);
            }
            if (usuarioId != null) {
                usuarioId.getComentarioCollection().add(comentario);
                usuarioId = em.merge(usuarioId);
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

    public void edit(Comentario comentario) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Comentario persistentComentario = em.find(Comentario.class, comentario.getId());
            Libro libroIdOld = persistentComentario.getLibroId();
            Libro libroIdNew = comentario.getLibroId();
            Usuario usuarioIdOld = persistentComentario.getUsuarioId();
            Usuario usuarioIdNew = comentario.getUsuarioId();
            if (libroIdNew != null) {
                libroIdNew = em.getReference(libroIdNew.getClass(), libroIdNew.getId());
                comentario.setLibroId(libroIdNew);
            }
            if (usuarioIdNew != null) {
                usuarioIdNew = em.getReference(usuarioIdNew.getClass(), usuarioIdNew.getId());
                comentario.setUsuarioId(usuarioIdNew);
            }
            comentario = em.merge(comentario);
            if (libroIdOld != null && !libroIdOld.equals(libroIdNew)) {
                libroIdOld.getComentarioCollection().remove(comentario);
                libroIdOld = em.merge(libroIdOld);
            }
            if (libroIdNew != null && !libroIdNew.equals(libroIdOld)) {
                libroIdNew.getComentarioCollection().add(comentario);
                libroIdNew = em.merge(libroIdNew);
            }
            if (usuarioIdOld != null && !usuarioIdOld.equals(usuarioIdNew)) {
                usuarioIdOld.getComentarioCollection().remove(comentario);
                usuarioIdOld = em.merge(usuarioIdOld);
            }
            if (usuarioIdNew != null && !usuarioIdNew.equals(usuarioIdOld)) {
                usuarioIdNew.getComentarioCollection().add(comentario);
                usuarioIdNew = em.merge(usuarioIdNew);
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
                Integer id = comentario.getId();
                if (findComentario(id) == null) {
                    throw new NonexistentEntityException("The comentario with id " + id + " no longer exists.");
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
            Comentario comentario;
            try {
                comentario = em.getReference(Comentario.class, id);
                comentario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comentario with id " + id + " no longer exists.", enfe);
            }
            Libro libroId = comentario.getLibroId();
            if (libroId != null) {
                libroId.getComentarioCollection().remove(comentario);
                libroId = em.merge(libroId);
            }
            Usuario usuarioId = comentario.getUsuarioId();
            if (usuarioId != null) {
                usuarioId.getComentarioCollection().remove(comentario);
                usuarioId = em.merge(usuarioId);
            }
            em.remove(comentario);
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

    public List<Comentario> findComentarioEntities() {
        return findComentarioEntities(true, -1, -1);
    }

    public List<Comentario> findComentarioEntities(int maxResults, int firstResult) {
        return findComentarioEntities(false, maxResults, firstResult);
    }

    private List<Comentario> findComentarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Comentario.class));
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

    public Comentario findComentario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comentario.class, id);
        } finally {
            em.close();
        }
    }

    public int getComentarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Comentario> rt = cq.from(Comentario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
