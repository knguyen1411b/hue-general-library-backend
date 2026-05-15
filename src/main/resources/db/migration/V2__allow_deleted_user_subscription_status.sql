ALTER TABLE public.tbl_user_subscription
    DROP CONSTRAINT IF EXISTS tbl_user_subscription_status_check;

ALTER TABLE public.tbl_user_subscription
    ADD CONSTRAINT tbl_user_subscription_status_check
    CHECK (
        (status)::text = ANY (
            ARRAY[
                'ACTIVE'::character varying,
                'EXPIRED'::character varying,
                'CANCELED'::character varying,
                'DELETED'::character varying
            ]::text[]
        )
    );
