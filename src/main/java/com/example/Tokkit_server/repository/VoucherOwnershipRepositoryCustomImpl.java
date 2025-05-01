package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.QVoucher;
import com.example.Tokkit_server.domain.QVoucherOwnership;
import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.domain.VoucherOwnership;
import com.example.Tokkit_server.dto.request.VoucherOwnershipSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class VoucherOwnershipRepositoryCustomImpl implements VoucherOwnershipRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public VoucherOwnershipRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<VoucherOwnership> searchMyVoucher(VoucherOwnershipSearchRequest request, Pageable pageable) {
        QVoucherOwnership voucherOwnership = QVoucherOwnership.voucherOwnership;
        QVoucher voucher = QVoucher.voucher;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(voucherOwnership.wallet.user.id.eq(request.getUserId()));

        if (request.getCategory() != null && !request.getCategory().isBlank() && !request.getCategory().equalsIgnoreCase("ALL")) {
            try {
                StoreCategory storeCategory = StoreCategory.valueOf(request.getCategory().toUpperCase());
                builder.and(voucher.category.eq(storeCategory));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid category: " + request.getCategory());
            }
        }

        if (request.getSearchKeyword() != null && !request.getSearchKeyword().isBlank() && !request.getSearchKeyword().equalsIgnoreCase("ALL")) {
            builder.and(voucher.name.containsIgnoreCase(request.getSearchKeyword()));
        }

        // 정렬과 정렬 방향 처리 (sort, direction)
        String sort = request.getSort() != null ? request.getSort() : "createdAt";
        String direction = request.getDirection() != null ? request.getDirection() : "desc";
        Order order = direction.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        PathBuilder<Voucher> path = new PathBuilder<>(Voucher.class, "voucher");
        OrderSpecifier<?> orderSpecifier;

        switch (sort) {
            case "price":
                orderSpecifier = new OrderSpecifier<>(order, path.getNumber(sort, Integer.class));
                break;
            case "expiredSoon":
                orderSpecifier = new OrderSpecifier<>(Order.ASC, path.getDate("expiredAt", java.time.LocalDate.class));
                break;
            case "createdAt":
                orderSpecifier = new OrderSpecifier<>(order, path.getDateTime("createdAt", java.time.LocalDateTime.class));
                break;
            default:
                orderSpecifier = new OrderSpecifier<>(Order.DESC, path.getDateTime("createdAt", java.time.LocalDateTime.class));
        }

        // 정렬 처리
        List<VoucherOwnership> content = queryFactory
                .selectFrom(voucherOwnership)
                .join(voucherOwnership.voucher, voucher).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = queryFactory
                .selectFrom(voucherOwnership)
                .join(voucherOwnership.voucher, voucher).fetchJoin()
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }
}
