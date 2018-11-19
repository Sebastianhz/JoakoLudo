/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pyther
 */
@Entity
@Table(name = "intercambio")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Intercambio.findAll", query = "SELECT i FROM Intercambio i")
    , @NamedQuery(name = "Intercambio.findById", query = "SELECT i FROM Intercambio i WHERE i.id = :id")})
public class Intercambio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "estado_intercambio_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private EstadoIntercambio estadoIntercambioId;
    @JoinColumn(name = "libro_1_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Libro libro1Id;
    @JoinColumn(name = "libro_2_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Libro libro2Id;

    public Intercambio() {
    }

    public Intercambio(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EstadoIntercambio getEstadoIntercambioId() {
        return estadoIntercambioId;
    }

    public void setEstadoIntercambioId(EstadoIntercambio estadoIntercambioId) {
        this.estadoIntercambioId = estadoIntercambioId;
    }

    public Libro getLibro1Id() {
        return libro1Id;
    }

    public void setLibro1Id(Libro libro1Id) {
        this.libro1Id = libro1Id;
    }

    public Libro getLibro2Id() {
        return libro2Id;
    }

    public void setLibro2Id(Libro libro2Id) {
        this.libro2Id = libro2Id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Intercambio)) {
            return false;
        }
        Intercambio other = (Intercambio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "JPA.Intercambio[ id=" + id + " ]";
    }
    
}
