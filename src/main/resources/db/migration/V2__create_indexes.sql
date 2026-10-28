CREATE INDEX idx_activation_request_status_created_at ON activation_request (status, created_at);
CREATE INDEX idx_activation_request_customer_created_at ON activation_request (customer_id, created_at);
