DO
$$
    DECLARE
        lScriptName  VARCHAR := 'db.changelog-test-data.sql';
        lErrorStack  TEXT;
        lErrorState  TEXT;
        lErrorMsg    TEXT;
        lErrorDetail TEXT;
        lErrorHint   TEXT;
    BEGIN
        RAISE NOTICE 'Start of % ...', lScriptName;

        INSERT INTO payment (guid, inquiry_ref_id, amount, currency, transaction_ref_id, status, note, created_at, updated_at)
        VALUES ('00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 99.99, 'USD', '20000000-0000-0000-0000-000000000001', 'RECEIVED', 'Test payment 1', '2025-01-01 10:00:00', '2025-01-01 10:00:00'),
               ('00000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 50.00, 'EUR', '20000000-0000-0000-0000-000000000002', 'APPROVED', 'Test payment 2', '2025-01-02 10:00:00', '2025-01-02 10:00:00'),
               ('00000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 10.50, 'CZK', '20000000-0000-0000-0000-000000000003', 'DECLINED', 'Test payment 3', '2025-01-03 10:00:00', '2025-01-03 10:00:00')
        ON CONFLICT (guid) DO NOTHING;

        RAISE NOTICE 'Execution of % is completed', lScriptName;
    EXCEPTION
        WHEN OTHERS
            THEN
                GET STACKED DIAGNOSTICS
                    lErrorState = RETURNED_SQLSTATE,
                    lErrorMsg = MESSAGE_TEXT,
                    lErrorDetail = PG_EXCEPTION_DETAIL,
                    lErrorHint = PG_EXCEPTION_HINT,
                    lErrorStack = PG_EXCEPTION_CONTEXT;
                RAISE EXCEPTION ' in script during executing.
  code        : %
  message     : %
  description : %
  hint        : %
  context     : %', lErrorState, lErrorMsg, lErrorDetail, lErrorHint, lErrorStack;
    END
$$