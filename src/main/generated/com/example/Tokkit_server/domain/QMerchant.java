package com.example.Tokkit_server.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMerchant is a Querydsl query type for Merchant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMerchant extends EntityPathBase<Merchant> {

    private static final long serialVersionUID = 1821522863L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMerchant merchant = new QMerchant("merchant");

    public final com.example.Tokkit_server.domain.common.QBaseTimeEntity _super = new com.example.Tokkit_server.domain.common.QBaseTimeEntity(this);

    public final StringPath businessNumber = createString("businessNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDormant = createBoolean("isDormant");

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Integer> simplePassword = createNumber("simplePassword", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final QWallet wallet;

    public QMerchant(String variable) {
        this(Merchant.class, forVariable(variable), INITS);
    }

    public QMerchant(Path<? extends Merchant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMerchant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMerchant(PathMetadata metadata, PathInits inits) {
        this(Merchant.class, metadata, inits);
    }

    public QMerchant(Class<? extends Merchant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.wallet = inits.isInitialized("wallet") ? new QWallet(forProperty("wallet"), inits.get("wallet")) : null;
    }

}

