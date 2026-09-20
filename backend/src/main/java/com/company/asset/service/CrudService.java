package com.company.asset.service;

import com.company.asset.common.BusinessException;
import com.company.asset.entity.BaseEntity;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 简洁的通用 CRUD 服务，所有系统设置类接口均复用此实现。
 */
@Service
public class CrudService {
    @PersistenceContext
    private EntityManager em;

    public <T> List<T> list(Class<T> type) {
        String entityName = em.getMetamodel().entity(type).getName();
        return em.createQuery("from " + entityName + " e order by e.id desc", type).getResultList();
    }

    public <T> T get(Class<T> type, Long id) {
        T data = em.find(type, id);
        if (data == null) throw new BusinessException("记录不存在");
        return data;
    }

    @Transactional
    public <T extends BaseEntity> T save(T data) {
        if (data.getId() == null) em.persist(data);
        else data = em.merge(data);
        return data;
    }

    @Transactional
    public <T> void delete(Class<T> type, Long id) {
        em.remove(get(type, id));
    }

    public long count(Class<?> type) {
        return em.createQuery("select count(x) from " + type.getSimpleName() + " x", Long.class).getSingleResult();
    }
}
