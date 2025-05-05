package com.example.Tokkit_server.voucher.repository;

import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher.dto.request.VoucherSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.example.Tokkit_server.voucher.entity.QVoucher;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class VoucherRepositoryCustomImpl implements VoucherRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public VoucherRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Voucher> searchVoucher(VoucherSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = createSearchCondition(request);
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(request);

        List<Voucher> content = queryFactory
                .selectFrom(QVoucher.voucher)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = queryFactory
                .selectFrom(QVoucher.voucher)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanBuilder createSearchCondition(VoucherSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (request.getCategory() != null && !request.getCategory().isBlank() && !request.getCategory().equalsIgnoreCase("ALL")) {
            builder.and(QVoucher.voucher.category.name.eq(request.getCategory()));
        }

        if (request.getSearchKeyword() != null && !request.getSearchKeyword().isBlank()) {
            builder.and(QVoucher.voucher.name.containsIgnoreCase(request.getSearchKeyword()));
        }

        return builder;
    }

    private static final Map<String, Function<PathBuilder<Voucher>, OrderSpecifier<?>>> ORDER_MAP = Map.of(
            "price", path -> new OrderSpecifier<>(Order.ASC, path.getNumber("price", Integer.class)),
            "createdAt", path -> new OrderSpecifier<>(Order.DESC, path.getDateTime("createdAt", LocalDateTime.class))
    );

    private OrderSpecifier<?> createOrderSpecifier(VoucherSearchRequest request) {
        String sort = Optional.ofNullable(request.getSort()).orElse("createdAt");
        String direction = Optional.ofNullable(request.getDirection()).orElse("desc");
        Order order = direction.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        PathBuilder<Voucher> path = new PathBuilder<>(Voucher.class, "voucher");
        return ORDER_MAP.getOrDefault(sort, ORDER_MAP.get("createdAt")).apply(path);
    }
}
