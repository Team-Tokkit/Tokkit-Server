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
        StringBuilder jpql = new StringBuilder("SELECT v FROM Voucher v WHERE 1=1");

        if (request.getCategory() != null) {
            jpql.append(" AND v.category = :category");
        }
        if (StringUtils.hasText(request.getSearchKeyword())) {
            jpql.append(" AND LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%'))");
        }



        String sort = Optional.ofNullable(request.getSort()).orElse("createdAt");
        String dir = Optional.ofNullable(request.getDirection()).orElse("desc");
        jpql.append(" ORDER BY v.").append(sort).append(" ").append(dir);

        TypedQuery<Voucher> query = em.createQuery(jpql.toString(), Voucher.class);

        if (request.getCategory() != null) {
            query.setParameter("category", request.getCategory());
        }
        if (StringUtils.hasText(request.getSearchKeyword())) {
            query.setParameter("keyword", request.getSearchKeyword());
        }

        int total = query.getResultList().size();

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