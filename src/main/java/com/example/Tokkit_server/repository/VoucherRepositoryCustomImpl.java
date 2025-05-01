package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.QVoucher;
import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.dto.request.VoucherSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public class VoucherRepositoryCustomImpl implements VoucherRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public VoucherRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Voucher> searchVoucher(VoucherSearchRequest request, Pageable pageable) {
        QVoucher voucher = QVoucher.voucher;
        BooleanBuilder builder = new BooleanBuilder();

        // 카테고리 필터
        if (request.getCategory() != null && !request.getCategory().isBlank() && !request.getCategory().equalsIgnoreCase("ALL")) {
            try {
                StoreCategory category = StoreCategory.valueOf(request.getCategory().toUpperCase());
                builder.and(voucher.category.eq(category));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid category: " + request.getCategory());
            }
        }

        // 검색어 필터
        if (request.getSearchKeyword() != null && !request.getSearchKeyword().isBlank()) {
            builder.and(voucher.name.containsIgnoreCase(request.getSearchKeyword()));
        }

        // 정렬 처리
        String sort = request.getSort() != null ? request.getSort() : "createdAt";
        String direction = request.getDirection() != null ? request.getDirection() : "desc";
        Order order = direction.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        PathBuilder<Voucher> path = new PathBuilder<>(Voucher.class, "voucher");
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sort, order, path);

        // 결과 조회
        List<Voucher> content = queryFactory
                .selectFrom(voucher)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        long count = queryFactory
                .selectFrom(voucher)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, Order order, PathBuilder<Voucher> path) {
        if ("price".equals(sort)) {
            return new OrderSpecifier<>(order, path.getNumber("price", Integer.class));
        } else if ("createdAt".equals(sort)) {
            return new OrderSpecifier<>(order, path.getDateTime("createdAt", LocalDateTime.class));
        } else {
            return new OrderSpecifier<>(Order.DESC, path.getDateTime("createdAt", LocalDateTime.class));
        }
    }
}
