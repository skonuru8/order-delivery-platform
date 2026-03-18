INSERT INTO main.categories (id, name, description) VALUES
                                                        ('a1111111-1111-1111-1111-111111111111', 'Electronics', 'Gadgets and devices'),
                                                        ('a2222222-2222-2222-2222-222222222222', 'Accessories', 'Cables, cases, peripherals');

INSERT INTO main.products (name, sku, description, price, stock_quantity, category_id) VALUES
                                                                                           ('Wireless Keyboard', 'KB-001', 'Bluetooth mechanical keyboard', 79.99, 50, 'a1111111-1111-1111-1111-111111111111'),
                                                                                           ('USB-C Hub', 'HUB-001', '7-in-1 USB-C adapter', 49.99, 100, 'a2222222-2222-2222-2222-222222222222'),
                                                                                           ('27" Monitor', 'MON-001', '4K IPS display', 349.99, 25, 'a1111111-1111-1111-1111-111111111111'),
                                                                                           ('Laptop Stand', 'STD-001', 'Adjustable aluminum stand', 39.99, 75, 'a2222222-2222-2222-2222-222222222222'),
                                                                                           ('Webcam HD', 'CAM-001', '1080p webcam with mic', 59.99, 40, 'a1111111-1111-1111-1111-111111111111');