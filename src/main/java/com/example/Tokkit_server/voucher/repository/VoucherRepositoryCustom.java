package com.example.Tokkit_server.voucher.repository;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.global.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.global.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.QVoucher;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class VoucherRepositoryCustom implements VoucherRepository {

    private final JPAQueryFactory queryFactory;

    public VoucherRepositoryCustom(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    // 바우처 조회 메인 메서드
    @Override
    public Page<Voucher> searchVoucher(VoucherSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = createSearchCondition(request);
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(request);

        // 조회된 결과를 페이지 형식으로 반환
        List<Voucher> content = queryFactory
                .selectFrom(QVoucher.voucher)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long count = queryFactory
                .selectFrom(QVoucher.voucher)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    // 검색 및 카테고리 필터 조건 생성 메서드
    private BooleanBuilder createSearchCondition(VoucherSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        // 카테고리 필터
        if (request.getCategory() != null && !request.getCategory().isBlank() && !request.getCategory().equalsIgnoreCase("ALL")) {
            try {
                StoreCategory category = StoreCategory.valueOf(request.getCategory().toUpperCase());
                builder.and(QVoucher.voucher.category.eq(category));
            } catch (Exception e) {
                throw new GeneralException(ErrorStatus.INVALID_CATEGORY);
            }
        }

        // 검색어 필터
        if (request.getSearchKeyword() != null && !request.getSearchKeyword().isBlank()) {
            builder.and(QVoucher.voucher.name.containsIgnoreCase(request.getSearchKeyword()));
        }

        return builder;
    }

    // 정렬 조건을 위한 Map
    private static final Map<String, Function<PathBuilder<Voucher>, OrderSpecifier<?>>> ORDER_MAP = Map.of(
            "price", path -> new OrderSpecifier<>(Order.ASC, path.getNumber("price", Integer.class)),
            "createdAt", path -> new OrderSpecifier<>(Order.DESC, path.getDateTime("createdAt", LocalDateTime.class))
            // 추후에 인기순 정렬 추가
    );

    // 추가 필터 조건 생성 메서드
    private OrderSpecifier<?> createOrderSpecifier(VoucherSearchRequest request) {
        String sort = Optional.ofNullable(request.getSort()).orElse("createdAt");
        String direction = Optional.ofNullable(request.getDirection()).orElse("desc");
        Order order = direction.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        PathBuilder<Voucher> path = new PathBuilder<>(Voucher.class, "voucher");
        return ORDER_MAP.getOrDefault(sort, ORDER_MAP.get("createdAt")).apply(path);
    }
}
