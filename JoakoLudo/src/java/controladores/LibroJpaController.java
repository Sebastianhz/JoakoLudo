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
import Model.Categoria;
import Model.Usuario;
import Model.Intercambio;
import java.util.ArrayList;
import java.util.Collection;
import Model.Comentario;
import Model.Libro;
import controladores.exceptions.IllegalOrphanException;
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
public class LibroJpaController implements Serializable {

    public LibroJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Libro libro) throws RollbackFailureException, Exception {
        if (libro.getIntercambioCollection() == null) {
            libro.setIntercambioCollection(new ArrayList<Intercambio>());
        }
        if (libro.getIntercambioCollection1() == null) {
            libro.setIntercambioCollection1(new ArrayList<Intercambio>());
        }
        if (libro.getComentarioCollection() == null) {
            libro.setComentarioCollection(new ArrayList<Comentario>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Categoria categoriaId = libro.getCategoriaId();
            if (categoriaId != null) {
                categoriaId = em.getReference(categoriaId.getClass(), categoriaId.getId());
                libro.setCategoriaId(categoriaId);
            }
            Usuario usuarioId = libro.getUsuarioId();
            if (usuarioId != null) {
                usuarioId = em.getReference(usuarioId.getClass(), usuarioId.getId());
                libro.setUsuarioId(usuarioId);
            }
            Collection<Intercambio> attachedIntercambioCollection = new ArrayList<Intercambio>();
            for (Intercambio intercambioCollectionIntercambioToAttach : libro.getIntercambioCollection()) {
                intercambioCollectionIntercambioToAttach = em.getReference(intercambioCollectionIntercambioToAttach.getClass(), intercambioCollectionIntercambioToAttach.getId());
                attachedIntercambioCollection.add(intercambioCollectionIntercambioToAttach);
            }
            libro.setIntercambioCollection(attachedIntercambioCollection);
            Collection<Intercambio> attachedIntercambioCollection1 = new ArrayList<Intercambio>();
            for (Intercambio intercambioCollection1IntercambioToAttach : libro.getIntercambioCollection1()) {
                intercambioCollection1IntercambioToAttach = em.getReference(intercambioCollection1IntercambioToAttach.getClass(), intercambioCollection1IntercambioToAttach.getId());
                attachedIntercambioCollection1.add(intercambioCollection1IntercambioToAttach);
            }
            libro.setIntercambioCollection1(attachedIntercambioCollection1);
            Collection<Comentario> attachedComentarioCollection = new ArrayList<Comentario>();
            for (Comentario comentarioCollectionComentarioToAttach : libro.getComentarioCollection()) {
                comentarioCollectionComentarioToAttach = em.getReference(comentarioCollectionComentarioToAttach.getClass(), comentarioCollectionComentarioToAttach.getId());
                attachedComentarioCollection.add(comentarioCollectionComentarioToAttach);
            }
            libro.setComentarioCollection(attachedComentarioCollection);
            em.persist(libro);
            if (categoriaId != null) {
                categoriaId.getLibroCollection().add(libro);
                categoriaId = em.merge(categoriaId);
            }
            if (usuarioId != null) {
                usuarioId.getLibroCollection().add(libro);
                usuarioId = em.merge(usuarioId);
            }
            for (Intercambio intercambioCollectionIntercambio : libro.getIntercambioCollection()) {
                Libro oldLibro1IdOfIntercambioCollectionIntercambio = intercambioCollectionIntercambio.getLibro1Id();
                intercambioCollectionIntercambio.setLibro1Id(libro);
                intercambioCollectionIntercambio = em.merge(intercambioCollectionIntercambio);
                if (oldLibro1IdOfIntercambioCollectionIntercambio != null) {
                    oldLibro1IdOfIntercambioCollectionIntercambio.getIntercambioCollection().remove(intercambioCollectionIntercambio);
                    oldLibro1IdOfIntercambioCollectionIntercambio = em.merge(oldLibro1IdOfIntercambioCollectionIntercambio);
                }
            }
            for (Intercambio intercambioCollection1Intercambio : libro.getIntercambioCollection1()) {
                Libro oldLibro2IdOfIntercambioCollection1Intercambio = intercambioCollection1Intercambio.getLibro2Id();
                intercambioCollection1Intercambio.setLibro2Id(libro);
                intercambioCollection1Intercambio = em.merge(intercambioCollection1Intercambio);
                if (oldLibro2IdOfIntercambioCollection1Intercambio != null) {
                    oldLibro2IdOfIntercambioCollection1Intercambio.getIntercambioCollection1().remove(intercambioCollection1Intercambio);
                    oldLibro2IdOfIntercambioCollection1Intercambio = em.merge(oldLibro2IdOfIntercambioCollection1Intercambio);
                }
            }
            for (Comentario comentarioCollectionComentario : libro.getComentarioCollection()) {
                Libro oldLibroIdOfComentarioCollectionComentario = comentarioCollectionComentario.getLibroId();
                comentarioCollectionComentario.setLibroId(libro);
                comentarioCollectionComentario = em.merge(comentarioCollectionComentario);
                if (oldLibroIdOfComentarioCollectionComentario != null) {
                    oldLibroIdOfComentarioCollectionComentario.getComentarioCollection().remove(comentarioCollectionComentario);
                    oldLibroIdOfComentarioCollectionComentario = em.merge(oldLibroIdOfComentarioCollectionComentario);
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

    public void edit(Libro libro) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Libro persistentLibro = em.find(Libro.class, libro.getId());
            Categoria categoriaIdOld = persistentLibro.getCategoriaId();
            Categoria categoriaIdNew = libro.getCategoriaId();
            Usuario usuarioIdOld = persistentLibro.getUsuarioId();
            Usuario usuarioIdNew = libro.getUsuarioId();
            Collection<Intercambio> intercambioCollectionOld = persistentLibro.getIntercambioCollection();
            Collection<Intercambio> intercambioCollectionNew = libro.getIntercambioCollection();
            Collection<Intercambio> intercambioCollection1Old = persistentLibro.getIntercambioCollection1();
            Collection<Intercambio> intercambioCollection1New = libro.getIntercambioCollection1();
            Collection<Comentario> comentarioCollectionOld = persistentLibro.getComentarioCollection();
            Collection<Comentario> comentarioCollectionNew = libro.getComentarioCollection();
            List<String> illegalOrphanMessages = null;
            for (Intercambio intercambioCollectionOldIntercambio : intercambioCollectionOld) {
                if (!intercambioCollectionNew.contains(intercambioCollectionOldIntercambio)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Intercambio " + intercambioCollectionOldIntercambio + " since its libro1Id field is not nullable.");
                }
            }
            for (Intercambio intercambioCollection1OldIntercambio : intercambioCollection1Old) {
                if (!intercambioCollection1New.contains(intercambioCollection1OldIntercambio)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Intercambio " + intercambioCollection1OldIntercambio + " since its libro2Id field is not nullable.");
                }
            }
            for (Comentario comentarioCollectionOldComentario : comentarioCollectionOld) {
                if (!comentarioCollectionNew.contains(comentarioCollectionOldComentario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comentario " + comentarioCollectionOldComentario + " since its libroId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (categoriaIdNew != null) {
                categoriaIdNew = em.getReference(categoriaIdNew.getClass(), categoriaIdNew.getId());
                libro.setCategoriaId(categoriaIdNew);
            }
            if (usuarioIdNew != null) {
                usuarioIdNew = em.getReference(usuarioIdNew.getClass(), usuarioIdNew.getId());
                libro.setUsuarioId(usuarioIdNew);
            }
            Collection<Intercambio> attachedIntercambioCollectionNew = new ArrayList<Intercambio>();
            for (Intercambio intercambioCollectionNewIntercambioToAttach : intercambioCollectionNew) {
                intercambioCollectionNewIntercambioToAttach = em.getReference(intercambioCollectionNewIntercambioToAttach.getClass(), intercambioCollectionNewIntercambioToAttach.getId());
                attachedIntercambioCollectionNew.add(intercambioCollectionNewIntercambioToAttach);
            }
            intercambioCollectionNew = attachedIntercambioCollectionNew;
            libro.setIntercambioCollection(intercambioCollectionNew);
            Collection<Intercambio> attachedIntercambioCollection1New = new ArrayList<Intercambio>();
            for (Intercambio intercambioCollection1NewIntercambioToAttach : intercambioCollection1New) {
                intercambioCollection1NewIntercambioToAttach = em.getReference(intercambioCollection1NewIntercambioToAttach.getClass(), intercambioCollection1NewIntercambioToAttach.getId());
                attachedIntercambioCollection1New.add(intercambioCollection1NewIntercambioToAttach);
            }
            intercambioCollection1New = attachedIntercambioCollection1New;
            libro.setIntercambioCollection1(intercambioCollection1New);
            Collection<Comentario> attachedComentarioCollectionNew = new ArrayList<Comentario>();
            for (Comentario comentarioCollectionNewComentarioToAttach : comentarioCollectionNew) {
                comentarioCollectionNewComentarioToAttach = em.getReference(comentarioCollectionNewComentarioToAttach.getClass(), comentarioCollectionNewComentarioToAttach.getId());
                attachedComentarioCollectionNew.add(comentarioCollectionNewComentarioToAttach);
            }
            comentarioCollectionNew = attachedComentarioCollectionNew;
            libro.setComentarioCollection(comentarioCollectionNew);
            libro = em.merge(libro);
            if (categoriaIdOld != null && !categoriaIdOld.equals(categoriaIdNew)) {
                categoriaIdOld.getLibroCollection().remove(libro);
                categoriaIdOld = em.merge(categoriaIdOld);
            }
            if (categoriaIdNew != null && !categoriaIdNew.equals(categoriaIdOld)) {
                categoriaIdNew.getLibroCollection().add(libro);
                categoriaIdNew = em.merge(categoriaIdNew);
            }
            if (usuarioIdOld != null && !usuarioIdOld.equals(usuarioIdNew)) {
                usuarioIdOld.getLibroCollection().remove(libro);
                usuarioIdOld = em.merge(usuarioIdOld);
            }
            if (usuarioIdNew != null && !usuarioIdNew.equals(usuarioIdOld)) {
                usuarioIdNew.getLibroCollection().add(libro);
                usuarioIdNew = em.merge(usuarioIdNew);
            }
            for (Intercambio intercambioCollectionNewIntercambio : intercambioCollectionNew) {
                if (!intercambioCollectionOld.contains(intercambioCollectionNewIntercambio)) {
                    Libro oldLibro1IdOfIntercambioCollectionNewIntercambio = intercambioCollectionNewIntercambio.getLibro1Id();
                    intercambioCollectionNewIntercambio.setLibro1Id(libro);
                    intercambioCollectionNewIntercambio = em.merge(intercambioCollectionNewIntercambio);
                    if (oldLibro1IdOfIntercambioCollectionNewIntercambio != null && !oldLibro1IdOfIntercambioCollectionNewIntercambio.equals(libro)) {
                        oldLibro1IdOfIntercambioCollectionNewIntercambio.getIntercambioCollection().remove(intercambioCollectionNewIntercambio);
                        oldLibro1IdOfIntercambioCollectionNewIntercambio = em.merge(oldLibro1IdOfIntercambioCollectionNewIntercambio);
                    }
                }
            }
            for (Intercambio intercambioCollection1NewIntercambio : intercambioCollection1New) {
                if (!intercambioCollection1Old.contains(intercambioCollection1NewIntercambio)) {
                    Libro oldLibro2IdOfIntercambioCollection1NewIntercambio = intercambioCollection1NewIntercambio.getLibro2Id();
                    intercambioCollection1NewIntercambio.setLibro2Id(libro);
                    intercambioCollection1NewIntercambio = em.merge(intercambioCollection1NewIntercambio);
                    if (oldLibro2IdOfIntercambioCollection1NewIntercambio != null && !oldLibro2IdOfIntercambioCollection1NewIntercambio.equals(libro)) {
                        oldLibro2IdOfIntercambioCollection1NewIntercambio.getIntercambioCollection1().remove(intercambioCollection1NewIntercambio);
                        oldLibro2IdOfIntercambioCollection1NewIntercambio = em.merge(oldLibro2IdOfIntercambioCollection1NewIntercambio);
                    }
                }
            }
            for (Comentario comentarioCollectionNewComentario : comentarioCollectionNew) {
                if (!comentarioCollectionOld.contains(comentarioCollectionNewComentario)) {
                    Libro oldLibroIdOfComentarioCollectionNewComentario = comentarioCollectionNewComentario.getLibroId();
                    comentarioCollectionNewComentario.setLibroId(libro);
                    comentarioCollectionNewComentario = em.merge(comentarioCollectionNewComentario);
                    if (oldLibroIdOfComentarioCollectionNewComentario != null && !oldLibroIdOfComentarioCollectionNewComentario.equals(libro)) {
                        oldLibroIdOfComentarioCollectionNewComentario.getComentarioCollection().remove(comentarioCollectionNewComentario);
                        oldLibroIdOfComentarioCollectionNewComentario = em.merge(oldLibroIdOfComentarioCollectionNewComentario);
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
                Integer id = libro.getId();
                if (findLibro(id) == null) {
                    throw new NonexistentEntityException("The libro with id " + id + " no longer exists.");
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
            Libro libro;
            try {
                libro = em.getReference(Libro.class, id);
                libro.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libro with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Intercambio> intercambioCollectionOrphanCheck = libro.getIntercambioCollection();
            for (Intercambio intercambioCollectionOrphanCheckIntercambio : intercambioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Libro (" + libro + ") cannot be destroyed since the Intercambio " + intercambioCollectionOrphanCheckIntercambio + " in its intercambioCollection field has a non-nullable libro1Id field.");
            }
            Collection<Intercambio> intercambioCollection1OrphanCheck = libro.getIntercambioCollection1();
            for (Intercambio intercambioCollection1OrphanCheckIntercambio : intercambioCollection1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Libro (" + libro + ") cannot be destroyed since the Intercambio " + intercambioCollection1OrphanCheckIntercambio + " in its intercambioCollection1 field has a non-nullable libro2Id field.");
            }
            Collection<Comentario> comentarioCollectionOrphanCheck = libro.getComentarioCollection();
            for (Comentario comentarioCollectionOrphanCheckComentario : comentarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Libro (" + libro + ") cannot be destroyed since the Comentario " + comentarioCollectionOrphanCheckComentario + " in its comentarioCollection field has a non-nullable libroId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Categoria categoriaId = libro.getCategoriaId();
            if (categoriaId != null) {
                categoriaId.getLibroCollection().remove(libro);
                categoriaId = em.merge(categoriaId);
            }
            Usuario usuarioId = libro.getUsuarioId();
            if (usuarioId != null) {
                usuarioId.getLibroCollection().remove(libro);
                usuarioId = em.merge(usuarioId);
            }
            em.remove(libro);
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

    public List<Libro> findLibroEntities() {
        return findLibroEntities(true, -1, -1);
    }

    public List<Libro> findLibroEntities(int maxResults, int firstResult) {
        return findLibroEntities(false, maxResults, firstResult);
    }

    private List<Libro> findLibroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libro.class));
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

    public Libro findLibro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Libro.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Libro> rt = cq.from(Libro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
