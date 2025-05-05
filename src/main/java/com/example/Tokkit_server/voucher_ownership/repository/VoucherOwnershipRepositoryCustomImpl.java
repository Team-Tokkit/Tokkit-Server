package com.example.Tokkit_server.voucher_ownership.repository;

import com.example.Tokkit_server.global.entity.StoreCategory;
import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.voucher.entity.QVoucher;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.voucher_ownership.entity.QVoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.entity.VoucherOwnership;
import com.example.Tokkit_server.voucher_ownership.dto.request.VoucherOwnershipSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class VoucherOwnershipRepositoryCustomImpl implements VoucherOwnershipRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public VoucherOwnershipRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<VoucherOwnership> searchMyVoucher(VoucherOwnershipSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = createSearchCondition(request);
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(request);

        List<VoucherOwnership> content = queryFactory
                .selectFrom(QVoucherOwnership.voucherOwnership)
                .join(QVoucherOwnership.voucherOwnership.voucher, QVoucher.voucher).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = queryFactory
                .selectFrom(QVoucherOwnership.voucherOwnership)
                .join(QVoucherOwnership.voucherOwnership.voucher, QVoucher.voucher).fetchJoin()
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanBuilder createSearchCondition(VoucherOwnershipSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(QVoucherOwnership.voucherOwnership.wallet.user.id.eq(request.getUserId()));

        if (request.getCategory() != null && !request.getCategory().isBlank() && !request.getCategory().equalsIgnoreCase("ALL")) {
            StoreCategory storeCategory = queryFactory
                    .selectFrom(QVoucher.voucher.category)
                    .where(QVoucher.voucher.category.name.eq(request.getCategory()))
                    .fetchOne();

            if (storeCategory == null) {
                throw new GeneralException(ErrorStatus.INVALID_CATEGORY);
            }

            builder.and(QVoucher.voucher.category.eq(storeCategory));
        }

        if (request.getSearchKeyword() != null && !request.getSearchKeyword().isBlank() && !request.getSearchKeyword().equalsIgnoreCase("ALL")) {
            builder.and(QVoucher.voucher.name.containsIgnoreCase(request.getSearchKeyword()));
        }

        return builder;
    }

    private static final Map<String, Function<PathBuilder<Voucher>, OrderSpecifier<?>>> ORDER_MAP = Map.of(
            "price", path -> new OrderSpecifier<>(Order.ASC, path.getNumber("price", Integer.class)),
            "expiredSoon", path -> new OrderSpecifier<>(Order.ASC, path.getDate("expiredAt", LocalDate.class)),
            "createdAt", path -> new OrderSpecifier<>(Order.DESC, path.getDateTime("createdAt", LocalDateTime.class))
    );

    private OrderSpecifier<?> createOrderSpecifier(VoucherOwnershipSearchRequest request) {
        String sort = Optional.ofNullable(request.getSort()).orElse("createdAt");
        String direction = Optional.ofNullable(request.getDirection()).orElse("desc");
        Order order = direction.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        PathBuilder<Voucher> path = new PathBuilder<>(Voucher.class, "voucher");
        return ORDER_MAP.getOrDefault(sort, ORDER_MAP.get("createdAt")).apply(path);
    }
}