package com.example.Tokkit_server.voucher_ownership.repository;

import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
public class VoucherOwnershipRepositoryImpl implements VoucherOwnershipRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<VoucherOwnership> searchMyVoucher(VoucherOwnershipSearchRequest request, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT vo FROM VoucherOwnership vo JOIN FETCH vo.voucher v WHERE vo.wallet.user.id = :userId");

        if (request.getStoreCategory() != null ) {
            jpql.append(" AND v.category = :category");
        }

        if (StringUtils.hasText(request.getSearchKeyword()) && !"ALL".equalsIgnoreCase(request.getSearchKeyword())) {
            jpql.append(" AND LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%'))");
        }

        String sort = Optional.ofNullable(request.getSort()).orElse("createdAt");
        String dir = Optional.ofNullable(request.getDirection()).orElse("desc");
        jpql.append(" ORDER BY v.").append(sort).append(" ").append(dir);

        TypedQuery<VoucherOwnership> query = em.createQuery(jpql.toString(), VoucherOwnership.class);
        query.setParameter("userId", request.getUserId());

        if (request.getStoreCategory() != null) {
            query.setParameter("category", request.getStoreCategory());
        }

        if (StringUtils.hasText(request.getSearchKeyword()) && !"ALL".equalsIgnoreCase(request.getSearchKeyword())) {
            query.setParameter("keyword", request.getSearchKeyword());
        }

        int total = query.getResultList().size();

        List<VoucherOwnership> result = query.setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(result, pageable, total);
    }
}
