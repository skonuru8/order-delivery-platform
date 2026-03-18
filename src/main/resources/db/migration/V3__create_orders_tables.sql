CREATE TABLE main.orders (
                             id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             user_id           UUID NOT NULL REFERENCES main.users(id),
                             status            VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                             total_amount      DECIMAL(12,2) NOT NULL,
                             shipping_address  TEXT NOT NULL,
                             shipment_id       VARCHAR(100),
                             created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
                             updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE main.order_line_items (
                                       id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       order_id        UUID NOT NULL REFERENCES main.orders(id) ON DELETE CASCADE,
                                       product_id      UUID NOT NULL REFERENCES main.products(id),
                                       quantity        INTEGER NOT NULL,
                                       unit_price      DECIMAL(10,2) NOT NULL
);

CREATE INDEX idx_orders_user ON main.orders(user_id);
CREATE INDEX idx_orders_status ON main.orders(status);
CREATE INDEX idx_order_items_order ON main.order_line_items(order_id);