package com.example.Tokkit_server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoucher is a Querydsl query type for Voucher
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoucher extends EntityPathBase<Voucher> {

    private static final long serialVersionUID = -1640053785L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoucher voucher = new QVoucher("voucher");

    public final com.example.Tokkit_server.domain.common.QBaseTimeEntity _super = new com.example.Tokkit_server.domain.common.QBaseTimeEntity(this);

    public final EnumPath<com.example.Tokkit_server.Enum.StoreCategory> category = createEnum("category", com.example.Tokkit_server.Enum.StoreCategory.class);

    public final StringPath contact = createString("contact");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final StringPath detailDescription = createString("detailDescription");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMerchant merchant;

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final StringPath refundPolicy = createString("refundPolicy");

    public final ListPath<Store, QStore> stores = this.<Store, QStore>createList("stores", Store.class, QStore.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final DateTimePath<java.time.LocalDateTime> validDate = createDateTime("validDate", java.time.LocalDateTime.class);

    public QVoucher(String variable) {
        this(Voucher.class, forVariable(variable), INITS);
    }

    public QVoucher(Path<? extends Voucher> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoucher(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoucher(PathMetadata metadata, PathInits inits) {
        this(Voucher.class, metadata, inits);
    }

    public QVoucher(Class<? extends Voucher> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.merchant = inits.isInitialized("merchant") ? new QMerchant(forProperty("merchant"), inits.get("merchant")) : null;
    }

}

