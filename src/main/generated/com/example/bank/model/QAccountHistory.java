package com.example.bank.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccountHistory is a Querydsl query type for AccountHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccountHistory extends EntityPathBase<AccountHistory> {

    private static final long serialVersionUID = 1791363821L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccountHistory accountHistory = new QAccountHistory("accountHistory");

    public final QAccount account;

    public final NumberPath<Long> accountHistoryId = createNumber("accountHistoryId", Long.class);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final StringPath category = createString("category");

    public final BooleanPath fixedExpenses = createBoolean("fixedExpenses");

    public final StringPath note = createString("note");

    public final StringPath storeName = createString("storeName");

    public final DateTimePath<java.time.LocalDateTime> transactionDate = createDateTime("transactionDate", java.time.LocalDateTime.class);

    public final EnumPath<com.example.bank.model.enumSet.TransactionMeansEnum> transactionMeans = createEnum("transactionMeans", com.example.bank.model.enumSet.TransactionMeansEnum.class);

    public final StringPath transactionType = createString("transactionType");

    public QAccountHistory(String variable) {
        this(AccountHistory.class, forVariable(variable), INITS);
    }

    public QAccountHistory(Path<? extends AccountHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccountHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccountHistory(PathMetadata metadata, PathInits inits) {
        this(AccountHistory.class, metadata, inits);
    }

    public QAccountHistory(Class<? extends AccountHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new QAccount(forProperty("account")) : null;
    }

}

