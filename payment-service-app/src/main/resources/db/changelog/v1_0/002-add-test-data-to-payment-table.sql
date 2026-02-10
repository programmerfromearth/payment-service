DO
$$
    DECLARE
        lScriptName  VARCHAR := '002-add-test-data-to-payment-table.sql';
        lErrorStack  TEXT;
        lErrorState  TEXT;
        lErrorMsg    TEXT;
        lErrorDetail TEXT;
        lErrorHint   TEXT;
    BEGIN
        RAISE NOTICE 'Start of % ...', lScriptName;

        INSERT INTO payment (guid, inquiry_ref_id, amount, currency, transaction_ref_id, status, note, created_at, updated_at)
        VALUES
          ('550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', 100.50, 'USD', '550e8400-e29b-41d4-a716-446655440002', 'RECEIVED', 'Payment for online course', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
          ('550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440004', 250.00, 'USD', '550e8400-e29b-41d4-a716-446655440005', 'RECEIVED', 'Monthly subscription', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
          ('550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440007', 75.25, 'USD', '550e8400-e29b-41d4-a716-446655440008', 'RECEIVED', 'Book purchase', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
          ('550e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-44665544000a', 300.00, 'EUR', '550e8400-e29b-41d4-a716-44665544000b', 'PENDING', 'Conference registration', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
          ('550e8400-e29b-41d4-a716-44665544000c', '550e8400-e29b-41d4-a716-44665544000d', 89.99, 'GBP', '550e8400-e29b-41d4-a716-44665544000e', 'PENDING', 'Software license', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
          ('550e8400-e29b-41d4-a716-44665544000f', '550e8400-e29b-41d4-a716-446655440010', 500.75, 'USD', '550e8400-e29b-41d4-a716-446655440011', 'DECLINED', 'Hotel booking', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
          ('550e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440013', 120.00, 'EUR', '550e8400-e29b-41d4-a716-446655440014', 'DECLINED', 'Flight tickets', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
          ('550e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440016', 65.50, 'USD', '550e8400-e29b-41d4-a716-446655440017', 'APPROVED', 'Insufficient funds', NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours'),
          ('550e8400-e29b-41d4-a716-446655440018', '550e8400-e29b-41d4-a716-446655440019', 45.00, 'USD', '550e8400-e29b-41d4-a716-446655440018', 'APPROVED', 'Customer request', NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours')
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