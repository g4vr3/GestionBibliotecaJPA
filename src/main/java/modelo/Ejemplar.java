package modelo;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Ejemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "isbn", nullable = false)
    private modelo.Libro isbn;

    @ColumnDefault("'Disponible'")
    @Lob
    @Column(name = "estado")
    private String estado;

    @OneToMany(mappedBy = "ejemplar")
    private Set<modelo.Prestamo> prestamos = new LinkedHashSet<>();

    public Ejemplar(Libro libro, String estado) {
        this.isbn = libro;
        this.estado = estado;
    }

    public Ejemplar() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public modelo.Libro getIsbn() {
        return isbn;
    }

    public void setIsbn(modelo.Libro isbn) {
        this.isbn = isbn;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Set<modelo.Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Set<modelo.Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    @Override
    public String toString() {
        return "Ejemplar{" +
                "id=" + id +
                ", isbn=" + isbn +
                ", estado='" + estado + '\'' +
                ", prestamos=" + prestamos +
                '}';
    }
}