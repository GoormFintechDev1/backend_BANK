package com.example.bank.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWithdrawalHistory is a Querydsl query type for WithdrawalHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWithdrawalHistory extends EntityPathBase<WithdrawalHistory> {

    private static final long serialVersionUID = -662735079L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWithdrawalHistory withdrawalHistory = new QWithdrawalHistory("withdrawalHistory");

    public final QAccount account;

    public final StringPath approvedAt = createString("approvedAt");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath currency = createString("currency");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath orderName = createString("orderName");

    public final NumberPath<Long> totalAmount = createNumber("totalAmount", Long.class);

    public final NumberPath<Long> vat = createNumber("vat", Long.class);

    public QWithdrawalHistory(String variable) {
        this(WithdrawalHistory.class, forVariable(variable), INITS);
    }

    public QWithdrawalHistory(Path<? extends WithdrawalHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWithdrawalHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWithdrawalHistory(PathMetadata metadata, PathInits inits) {
        this(WithdrawalHistory.class, metadata, inits);
    }

    public QWithdrawalHistory(Class<? extends WithdrawalHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account")) : null;
    }

}

