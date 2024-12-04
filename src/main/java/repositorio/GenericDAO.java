package repositorio;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class GenericDAO<T> {
    private Class<T> classType;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
    private EntityManager em = emf.createEntityManager();
    private EntityTransaction et = em.getTransaction();

    public GenericDAO(Class<T> classType) {
        this.classType = classType;
    }

    public void create(T entity) {
        et.begin();
        em.persist(entity);
        et.commit();
    }

    public T read (Integer id) {
        return em.find(classType, id);
    }

    public List<T> readAll() {
        return em.createQuery("SELECT e FROM " + classType.getSimpleName() + " e", classType).getResultList();
    }

    public void update (T entity) {
        et.begin();
        em.merge(entity);
        et.commit();
    }

    public void delete (T entity) {
        et.begin();
        em.remove(entity);
        et.commit();
    }
}

