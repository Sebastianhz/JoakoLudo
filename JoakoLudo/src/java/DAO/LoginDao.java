/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;


import Model.Login;
import javax.persistence.EntityManager;

import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 *
 * @author Pyther
 */
@Service
public class LoginDao {
    @PersistenceContext
 private EntityManager em;
    @Transactional(rollbackFor = {SevicioException.class})
     //@Transactional
    public void create(Login dto) throws SevicioException{
    em.persist(dto);
    em.merge(dto);
    em.remove(dto);
   
    }
    
}
