/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import Model.Categoria;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Model.Libro;
import java.util.ArrayList;
import java.util.Collection;
import Model.Usuario;
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
public class CategoriaJpaController implements Serializable {

    public CategoriaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categoria categoria) throws RollbackFailureException, Exception {
        if (categoria.getLibroCollection() == null) {
            categoria.setLibroCollection(new ArrayList<Libro>());
        }
        if (categoria.getUsuarioCollection() == null) {
            categoria.setUsuarioCollection(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Libro> attachedLibroCollection = new ArrayList<Libro>();
            for (Libro libroCollectionLibroToAttach : categoria.getLibroCollection()) {
                libroCollectionLibroToAttach = em.getReference(libroCollectionLibroToAttach.getClass(), libroCollectionLibroToAttach.getId());
                attachedLibroCollection.add(libroCollectionLibroToAttach);
            }
            categoria.setLibroCollection(attachedLibroCollection);
            Collection<Usuario> attachedUsuarioCollection = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionUsuarioToAttach : categoria.getUsuarioCollection()) {
                usuarioCollectionUsuarioToAttach = em.getReference(usuarioCollectionUsuarioToAttach.getClass(), usuarioCollectionUsuarioToAttach.getId());
                attachedUsuarioCollection.add(usuarioCollectionUsuarioToAttach);
            }
            categoria.setUsuarioCollection(attachedUsuarioCollection);
            em.persist(categoria);
            for (Libro libroCollectionLibro : categoria.getLibroCollection()) {
                Categoria oldCategoriaIdOfLibroCollectionLibro = libroCollectionLibro.getCategoriaId();
                libroCollectionLibro.setCategoriaId(categoria);
                libroCollectionLibro = em.merge(libroCollectionLibro);
                if (oldCategoriaIdOfLibroCollectionLibro != null) {
                    oldCategoriaIdOfLibroCollectionLibro.getLibroCollection().remove(libroCollectionLibro);
                    oldCategoriaIdOfLibroCollectionLibro = em.merge(oldCategoriaIdOfLibroCollectionLibro);
                }
            }
            for (Usuario usuarioCollectionUsuario : categoria.getUsuarioCollection()) {
                Categoria oldCategoriaPreferenciaIdOfUsuarioCollectionUsuario = usuarioCollectionUsuario.getCategoriaPreferenciaId();
                usuarioCollectionUsuario.setCategoriaPreferenciaId(categoria);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
                if (oldCategoriaPreferenciaIdOfUsuarioCollectionUsuario != null) {
                    oldCategoriaPreferenciaIdOfUsuarioCollectionUsuario.getUsuarioCollection().remove(usuarioCollectionUsuario);
                    oldCategoriaPreferenciaIdOfUsuarioCollectionUsuario = em.merge(oldCategoriaPreferenciaIdOfUsuarioCollectionUsuario);
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

    public void edit(Categoria categoria) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getId());
            Collection<Libro> libroCollectionOld = persistentCategoria.getLibroCollection();
            Collection<Libro> libroCollectionNew = categoria.getLibroCollection();
            Collection<Usuario> usuarioCollectionOld = persistentCategoria.getUsuarioCollection();
            Collection<Usuario> usuarioCollectionNew = categoria.getUsuarioCollection();
            List<String> illegalOrphanMessages = null;
            for (Libro libroCollectionOldLibro : libroCollectionOld) {
                if (!libroCollectionNew.contains(libroCollectionOldLibro)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Libro " + libroCollectionOldLibro + " since its categoriaId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Libro> attachedLibroCollectionNew = new ArrayList<Libro>();
            for (Libro libroCollectionNewLibroToAttach : libroCollectionNew) {
                libroCollectionNewLibroToAttach = em.getReference(libroCollectionNewLibroToAttach.getClass(), libroCollectionNewLibroToAttach.getId());
                attachedLibroCollectionNew.add(libroCollectionNewLibroToAttach);
            }
            libroCollectionNew = attachedLibroCollectionNew;
            categoria.setLibroCollection(libroCollectionNew);
            Collection<Usuario> attachedUsuarioCollectionNew = new ArrayList<Usuario>();
            for (Usuario usuarioCollectionNewUsuarioToAttach : usuarioCollectionNew) {
                usuarioCollectionNewUsuarioToAttach = em.getReference(usuarioCollectionNewUsuarioToAttach.getClass(), usuarioCollectionNewUsuarioToAttach.getId());
                attachedUsuarioCollectionNew.add(usuarioCollectionNewUsuarioToAttach);
            }
            usuarioCollectionNew = attachedUsuarioCollectionNew;
            categoria.setUsuarioCollection(usuarioCollectionNew);
            categoria = em.merge(categoria);
            for (Libro libroCollectionNewLibro : libroCollectionNew) {
                if (!libroCollectionOld.contains(libroCollectionNewLibro)) {
                    Categoria oldCategoriaIdOfLibroCollectionNewLibro = libroCollectionNewLibro.getCategoriaId();
                    libroCollectionNewLibro.setCategoriaId(categoria);
                    libroCollectionNewLibro = em.merge(libroCollectionNewLibro);
                    if (oldCategoriaIdOfLibroCollectionNewLibro != null && !oldCategoriaIdOfLibroCollectionNewLibro.equals(categoria)) {
                        oldCategoriaIdOfLibroCollectionNewLibro.getLibroCollection().remove(libroCollectionNewLibro);
                        oldCategoriaIdOfLibroCollectionNewLibro = em.merge(oldCategoriaIdOfLibroCollectionNewLibro);
                    }
                }
            }
            for (Usuario usuarioCollectionOldUsuario : usuarioCollectionOld) {
                if (!usuarioCollectionNew.contains(usuarioCollectionOldUsuario)) {
                    usuarioCollectionOldUsuario.setCategoriaPreferenciaId(null);
                    usuarioCollectionOldUsuario = em.merge(usuarioCollectionOldUsuario);
                }
            }
            for (Usuario usuarioCollectionNewUsuario : usuarioCollectionNew) {
                if (!usuarioCollectionOld.contains(usuarioCollectionNewUsuario)) {
                    Categoria oldCategoriaPreferenciaIdOfUsuarioCollectionNewUsuario = usuarioCollectionNewUsuario.getCategoriaPreferenciaId();
                    usuarioCollectionNewUsuario.setCategoriaPreferenciaId(categoria);
                    usuarioCollectionNewUsuario = em.merge(usuarioCollectionNewUsuario);
                    if (oldCategoriaPreferenciaIdOfUsuarioCollectionNewUsuario != null && !oldCategoriaPreferenciaIdOfUsuarioCollectionNewUsuario.equals(categoria)) {
                        oldCategoriaPreferenciaIdOfUsuarioCollectionNewUsuario.getUsuarioCollection().remove(usuarioCollectionNewUsuario);
                        oldCategoriaPreferenciaIdOfUsuarioCollectionNewUsuario = em.merge(oldCategoriaPreferenciaIdOfUsuarioCollectionNewUsuario);
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
                Integer id = categoria.getId();
                if (findCategoria(id) == null) {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
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
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Libro> libroCollectionOrphanCheck = categoria.getLibroCollection();
            for (Libro libroCollectionOrphanCheckLibro : libroCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Categoria (" + categoria + ") cannot be destroyed since the Libro " + libroCollectionOrphanCheckLibro + " in its libroCollection field has a non-nullable categoriaId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Usuario> usuarioCollection = categoria.getUsuarioCollection();
            for (Usuario usuarioCollectionUsuario : usuarioCollection) {
                usuarioCollectionUsuario.setCategoriaPreferenciaId(null);
                usuarioCollectionUsuario = em.merge(usuarioCollectionUsuario);
            }
            em.remove(categoria);
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

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
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

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
