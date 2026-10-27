CREATE TABLE activation_request (
    id UUID NOT NULL,
    external_id VARCHAR(100) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    product_code VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_activation_request PRIMARY KEY (id),
    CONSTRAINT uk_activation_request_external_id UNIQUE (external_id)
);
