/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.Login;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Pyther
 */

@Service
public class LoginDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional(rollbackFor = {ServicioException.class})
    //
    
    public void create(Login dto) throws ServicioException {

        em.persist(dto);
        //em.merge(dto); actualizar
        //em.remove(dto); borrar
        //em.find(Login.class, "111"); buscar pk

    }

    public Login readByUsuario(String usuario) {
        return em.find(Login.class, usuario);

    }

    public Login readByJPQL(String usuario) {
        String sql = "select a from Login a where a.usuario =:usuario";

        Query q = em.createQuery(sql);
        q.setParameter("usuario", usuario);
        try {
            return (Login) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }
    //consulta multiple jpql
    
   
}
