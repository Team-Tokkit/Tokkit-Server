package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
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

    // 내 바우처 조회 메인 메서드
    @Override
    public Page<VoucherOwnership> searchMyVoucher(VoucherOwnershipSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = createSearchCondition(request);
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(request);

        // 조회된 결과를 페이지 형식으로 반환
        List<VoucherOwnership> content = queryFactory
                .selectFrom(QVoucherOwnership.voucherOwnership)
                .join(QVoucherOwnership.voucherOwnership.voucher, QVoucher.voucher).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long count = queryFactory
                .selectFrom(QVoucherOwnership.voucherOwnership)
                .join(QVoucherOwnership.voucherOwnership.voucher, QVoucher.voucher).fetchJoin()
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    // 검색 및 카테고리 필터 조건 생성 메서드
    private BooleanBuilder createSearchCondition(VoucherOwnershipSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        // 사용자 ID로 소유한 바우처 필터링
        builder.and(QVoucherOwnership.voucherOwnership.wallet.user.id.eq(request.getUserId()));

        // 카테고리 필터
        if (request.getCategory() != null && !request.getCategory().isBlank() && !request.getCategory().equalsIgnoreCase("ALL")) {
            try {
                StoreCategory storeCategory = StoreCategory.valueOf(request.getCategory().toUpperCase());
                builder.and(QVoucher.voucher.category.eq(storeCategory));
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.INVALID_CATEGORY);
            }
        }

        // 검색어 필터
        if (request.getSearchKeyword() != null && !request.getSearchKeyword().isBlank() && !request.getSearchKeyword().equalsIgnoreCase("ALL")) {
            builder.and(QVoucher.voucher.name.containsIgnoreCase(request.getSearchKeyword()));
        }

        return builder;
    }

    // 정렬 조건을 위한 Map
    private static final Map<String, Function<PathBuilder<Voucher>, OrderSpecifier<?>>> ORDER_MAP = Map.of(
            "price", path -> new OrderSpecifier<>(Order.ASC, path.getNumber("price", Integer.class)),
            "expiredSoon", path -> new OrderSpecifier<>(Order.ASC, path.getDate("expiredAt", LocalDate.class)),
            "createdAt", path -> new OrderSpecifier<>(Order.DESC, path.getDateTime("createdAt", LocalDateTime.class))
    );

    // 추가 필터 조건 생성 메서드
    private OrderSpecifier<?> createOrderSpecifier(VoucherOwnershipSearchRequest request) {
        String sort = Optional.ofNullable(request.getSort()).orElse("createdAt");
        String direction = Optional.ofNullable(request.getDirection()).orElse("desc");
        Order order = direction.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        PathBuilder<Voucher> path = new PathBuilder<>(Voucher.class, "voucher");
        return ORDER_MAP.getOrDefault(sort, ORDER_MAP.get("createdAt")).apply(path);
    }
}
