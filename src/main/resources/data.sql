# 은행 고유 번호
INSERT INTO bank_code (bank_code, bank_name, bank_alias) VALUES
                                                             ('001', 'KB국민은행', '국민은행'),
                                                             ('002', 'KDB산업은행', '산업은행'),
                                                             ('003', 'IBK기업은행', '기업은행'),
                                                             ('004', '신한은행', '신한'),
                                                             ('007', '수협은행', '수협'),
                                                             ('008', '수출입은행', '수출입은행'),
                                                             ('011', 'NH농협은행', '농협'),
                                                             ('020', '우리은행', '우리'),
                                                             ('023', 'SC제일은행', 'SC제일'),
                                                             ('027', '한국씨티은행', '씨티은행'),
                                                             ('031', '대구은행', '대구'),
                                                             ('032', '부산은행', '부산'),
                                                             ('034', '광주은행', '광주'),
                                                             ('035', '제주은행', '제주'),
                                                             ('037', '전북은행', '전북'),
                                                             ('039', '경남은행', '경남'),
                                                             ('045', '새마을금고', '새마을'),
                                                             ('050', '저축은행중앙회', '저축은행');

# account
INSERT INTO account (
    cntr_account_num,
    account_holder_name,
    bank_code,
    balance_amt,
    account_status,
    last_transaction_time,
    fintech_use_num
) VALUES
    ('110123456789', '이민수', '001', 1500000, 'ACTIVE', '20241120120000', '00112345678901234567890123');


