package com.example.Tokkit_server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoucherOwnership is a Querydsl query type for VoucherOwnership
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoucherOwnership extends EntityPathBase<VoucherOwnership> {

    private static final long serialVersionUID = 1602161480L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoucherOwnership voucherOwnership = new QVoucherOwnership("voucherOwnership");

    public final com.example.Tokkit_server.domain.common.QBaseTimeEntity _super = new com.example.Tokkit_server.domain.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isUsed = createBoolean("isUsed");

    public final NumberPath<Long> remainingAmount = createNumber("remainingAmount", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QVoucher voucher;

    public final QWallet wallet;

    public QVoucherOwnership(String variable) {
        this(VoucherOwnership.class, forVariable(variable), INITS);
    }

    public QVoucherOwnership(Path<? extends VoucherOwnership> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoucherOwnership(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoucherOwnership(PathMetadata metadata, PathInits inits) {
        this(VoucherOwnership.class, metadata, inits);
    }

    public QVoucherOwnership(Class<? extends VoucherOwnership> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.voucher = inits.isInitialized("voucher") ? new QVoucher(forProperty("voucher"), inits.get("voucher")) : null;
        this.wallet = inits.isInitialized("wallet") ? new QWallet(forProperty("wallet"), inits.get("wallet")) : null;
    }

}

