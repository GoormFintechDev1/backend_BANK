package com.example.bank.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDepositHistory is a Querydsl query type for DepositHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDepositHistory extends EntityPathBase<DepositHistory> {

    private static final long serialVersionUID = -2088645284L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDepositHistory depositHistory = new QDepositHistory("depositHistory");

    public final QAccount account;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> orderDate = createDateTime("orderDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final StringPath orderStatus = createString("orderStatus");

    public final StringPath paymentStatus = createString("paymentStatus");

    public final StringPath productName = createString("productName");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final NumberPath<Integer> totalPrice = createNumber("totalPrice", Integer.class);

    public QDepositHistory(String variable) {
        this(DepositHistory.class, forVariable(variable), INITS);
    }

    public QDepositHistory(Path<? extends DepositHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDepositHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDepositHistory(PathMetadata metadata, PathInits inits) {
        this(DepositHistory.class, metadata, inits);
    }

    public QDepositHistory(Class<? extends DepositHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account")) : null;
    }

}

