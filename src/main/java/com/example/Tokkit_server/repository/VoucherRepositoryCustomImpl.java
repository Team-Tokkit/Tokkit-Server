package com.example.Tokkit_server.repository;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.apiPayload.code.status.ErrorStatus;
import com.example.Tokkit_server.apiPayload.exception.GeneralException;
import com.example.Tokkit_server.domain.QVoucher;
import com.example.Tokkit_server.domain.Voucher;
import com.example.Tokkit_server.dto.request.VoucherSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.aspectj.weaver.GeneratedReferenceTypeDelegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                throw new GeneralException(ErrorStatus.INVALID_CATEGORY);
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

            Map<String, Expression<?>> sortMap = new HashMap<>();
            sortMap.put("price", path.getNumber("price", Integer.class));
            sortMap.put("createdAt", path.getDateTime("createdAt", LocalDateTime.class));

            Expression<?> sortExpr = sortMap.getOrDefault(sort, path.getDateTime("createdAt", LocalDateTime.class));
            OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order, (Expression<? extends Comparable>) sortExpr);

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

    }

