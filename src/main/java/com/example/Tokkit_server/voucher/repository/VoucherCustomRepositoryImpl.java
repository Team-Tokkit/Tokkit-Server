package com.example.Tokkit_server.voucher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.Tokkit_server.store.dto.response.StoreResponse;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import com.example.Tokkit_server.voucher.entity.Voucher;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class VoucherCustomRepositoryImpl implements VoucherCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Voucher> searchVouchers(VoucherSearchRequest request, Pageable pageable) {
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(v) FROM Voucher v WHERE 1=1");

        StringBuilder jpql = new StringBuilder("SELECT v FROM Voucher v LEFT JOIN FETCH v.image WHERE 1=1");

        if (request.getStoreCategory() != null) {
            countJpql.append(" AND v.storeCategory = :category");
            jpql.append(" AND v.storeCategory = :category");
        }
        if (StringUtils.hasText(request.getSearchKeyword())) {
            countJpql.append(" AND LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%'))");
            jpql.append(" AND LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%'))");
        }

        String sort = Optional.ofNullable(request.getSort()).orElse("createdAt");
        String dir = Optional.ofNullable(request.getDirection()).orElse("desc");
        jpql.append(" ORDER BY v.").append(sort).append(" ").append(dir);

        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        if (request.getStoreCategory() != null) {
            countQuery.setParameter("category", request.getStoreCategory());
        }
        if (StringUtils.hasText(request.getSearchKeyword())) {
            countQuery.setParameter("keyword", request.getSearchKeyword());
        }
        long total = countQuery.getSingleResult();

        TypedQuery<Voucher> query = em.createQuery(jpql.toString(), Voucher.class);
        if (request.getStoreCategory() != null) {
            query.setParameter("category", request.getStoreCategory());
        }
        if (StringUtils.hasText(request.getSearchKeyword())) {
            query.setParameter("keyword", request.getSearchKeyword());
        }

        List<Voucher> result = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<StoreResponse> findStoresByVoucherId(Long voucherId, Pageable pageable) {
        TypedQuery<StoreResponse> query = em.createQuery(
            "SELECT new com.example.Tokkit_server.store.dto.response.StoreResponse(s) " +
            "FROM VoucherStore vs JOIN vs.store s WHERE vs.voucher.id = :voucherId",
            StoreResponse.class);
        query.setParameter("voucherId", voucherId);

        int total = query.getResultList().size();

        List<StoreResponse> results = query
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        return new PageImpl<>(results, pageable, total);
    }
}

