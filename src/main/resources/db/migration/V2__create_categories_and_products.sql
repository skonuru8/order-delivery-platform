CREATE TABLE main.categories (
                                 id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 name            VARCHAR(255) NOT NULL UNIQUE,
                                 description     TEXT
);

CREATE TABLE main.products (
                               id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               name            VARCHAR(255) NOT NULL,
                               sku             VARCHAR(100) NOT NULL UNIQUE,
                               description     TEXT,
                               price           DECIMAL(10,2) NOT NULL,
                               stock_quantity  INTEGER NOT NULL DEFAULT 0,
                               image_url       VARCHAR(500),
                               category_id     UUID REFERENCES main.categories(id),
                               created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_products_category ON main.products(category_id);
CREATE INDEX idx_products_sku ON main.products(sku);