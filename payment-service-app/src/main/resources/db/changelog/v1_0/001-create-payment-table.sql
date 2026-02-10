DO
$$
    DECLARE
        lScriptName  VARCHAR := '001-create-payment-table.sql';
        lErrorStack  TEXT;
        lErrorState  TEXT;
        lErrorMsg    TEXT;
        lErrorDetail TEXT;
        lErrorHint   TEXT;
    BEGIN
        RAISE NOTICE 'Start of % ...', lScriptName;

        CREATE TABLE payment (
            guid UUID NOT NULL PRIMARY KEY,
            inquiry_ref_id UUID NOT NULL,
            amount DECIMAL(5,2) NOT NULL,
            currency VARCHAR(3) NOT NULL,
            transaction_ref_id UUID,
            status VARCHAR(50) NOT NULL,
            note TEXT,
            created_at TIMESTAMP WITH TIME ZONE NOT NULL,
            updated_at TIMESTAMP WITH TIME ZONE NOT NULL
        );

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