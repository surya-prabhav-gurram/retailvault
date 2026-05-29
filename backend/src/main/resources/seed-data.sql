-- ============================================================
-- RetailVault OLTP Seed Data
-- ============================================================
USE retailvault_oltp;

-- Stores
INSERT INTO stores (store_name, city, state, region, store_type, opened_date) VALUES
('Hobby Lobby OKC North', 'Oklahoma City', 'Oklahoma', 'South Central', 'Flagship', '2010-03-15'),
('Hobby Lobby OKC South', 'Moore', 'Oklahoma', 'South Central', 'Standard', '2012-07-20'),
('Hobby Lobby Tulsa', 'Tulsa', 'Oklahoma', 'South Central', 'Standard', '2011-05-10'),
('Hobby Lobby Dallas', 'Dallas', 'Texas', 'South', 'Flagship', '2009-01-01'),
('Hobby Lobby Houston', 'Houston', 'Texas', 'South', 'Flagship', '2008-06-15'),
('Hobby Lobby Denver', 'Denver', 'Colorado', 'West', 'Standard', '2015-09-01'),
('Hobby Lobby Phoenix', 'Phoenix', 'Arizona', 'West', 'Standard', '2016-04-12'),
('Hobby Lobby Chicago', 'Chicago', 'Illinois', 'Midwest', 'Flagship', '2013-11-30'),
('Hobby Lobby Atlanta', 'Atlanta', 'Georgia', 'Southeast', 'Standard', '2014-02-14'),
('Hobby Lobby Nashville', 'Nashville', 'Tennessee', 'Southeast', 'Standard', '2017-08-22');

-- Suppliers
INSERT INTO suppliers (supplier_name, contact_name, email, phone, country) VALUES
('ArtCraft Supplies Co.', 'John Smith', 'jsmith@artcraft.com', '800-111-2222', 'USA'),
('Global Fabric Imports', 'Li Wei', 'lwei@globalfabric.com', '800-333-4444', 'China'),
('Creative Tools Ltd.', 'Emma Brown', 'ebrown@creativetools.com', '800-555-6666', 'USA'),
('Yarn & Thread World', 'Maria Garcia', 'mgarcia@yarnworld.com', '800-777-8888', 'Mexico'),
('Seasonal Decor Inc.', 'David Kim', 'dkim@seasonaldecor.com', '800-999-0000', 'USA'),
('Canvas & Frame Co.', 'Sarah Lee', 'slee@canvasframe.com', '800-222-3333', 'Canada'),
('Craft Paper Plus', 'Tom Wilson', 'twilson@craftpaper.com', '800-444-5555', 'USA'),
('Floral Wholesale LLC', 'Nancy Davis', 'ndavis@floralwholesale.com', '800-666-7777', 'USA');

-- Categories
INSERT INTO categories (category_name, parent_category) VALUES
('Painting', 'Art Supplies'),
('Drawing', 'Art Supplies'),
('Yarn & Knitting', 'Fabric & Sewing'),
('Fabric & Sewing', 'Fabric & Sewing'),
('Seasonal Decor', 'Home Decor'),
('Floral', 'Home Decor'),
('Framing', 'Home Decor'),
('Paper Crafts', 'Crafts'),
('Kids Crafts', 'Crafts'),
('Storage & Organization', 'Crafts');

-- Products
INSERT INTO products (sku, product_name, category_id, supplier_id, unit_price, cost_price) VALUES
('ART-001', 'Acrylic Paint Set 24-Pack', 1, 1, 24.99, 11.00),
('ART-002', 'Oil Paint Brush Set', 1, 1, 14.99, 6.50),
('ART-003', 'Canvas 16x20 Pack of 3', 7, 6, 19.99, 9.00),
('DRW-001', 'Colored Pencils 48-Pack', 2, 1, 12.99, 5.50),
('DRW-002', 'Sketch Pad 9x12', 2, 7, 8.99, 3.75),
('YRN-001', 'Premium Yarn Bundle 5-Pack', 3, 4, 22.99, 10.00),
('YRN-002', 'Knitting Needles Set', 3, 4, 17.99, 7.50),
('FAB-001', 'Cotton Fabric Yard - Floral', 4, 2, 6.99, 2.75),
('FAB-002', 'Sewing Thread Set 50-Pack', 4, 2, 11.99, 5.00),
('DEC-001', 'Christmas Wreath 24"', 5, 5, 39.99, 16.00),
('DEC-002', 'Fall Harvest Centerpiece', 5, 5, 29.99, 12.50),
('FLR-001', 'Silk Roses Bundle x12', 6, 8, 15.99, 6.50),
('FLR-002', 'Floral Foam Bricks 6-Pack', 6, 8, 9.99, 4.00),
('FRM-001', 'Black Photo Frame 8x10', 7, 6, 13.99, 6.00),
('PPR-001', 'Scrapbook Paper 50-Sheet Pack', 8, 7, 7.99, 3.25),
('PPR-002', 'Cardstock 100-Sheet Pack', 8, 7, 14.99, 6.25),
('KID-001', 'Kids Glitter Glue Set', 9, 3, 8.99, 3.75),
('KID-002', 'Foam Sticker Sheets 20-Pack', 9, 3, 6.99, 2.75),
('STG-001', 'Stackable Storage Bins Set of 4', 10, 3, 19.99, 8.50),
('STG-002', 'Craft Supply Organizer', 10, 3, 34.99, 15.00);

-- Customers
INSERT INTO customers (first_name, last_name, email, city, state) VALUES
('Alice', 'Johnson', 'alice.j@email.com', 'Oklahoma City', 'Oklahoma'),
('Bob', 'Martinez', 'bob.m@email.com', 'Tulsa', 'Oklahoma'),
('Carol', 'Williams', 'carol.w@email.com', 'Dallas', 'Texas'),
('Dan', 'Brown', 'dan.b@email.com', 'Houston', 'Texas'),
('Eve', 'Davis', 'eve.d@email.com', 'Denver', 'Colorado'),
('Frank', 'Wilson', 'frank.w@email.com', 'Chicago', 'Illinois'),
('Grace', 'Lee', 'grace.l@email.com', 'Atlanta', 'Georgia'),
('Henry', 'Taylor', 'henry.t@email.com', 'Nashville', 'Tennessee'),
('Iris', 'Anderson', 'iris.a@email.com', 'Phoenix', 'Arizona'),
('Jack', 'Thomas', 'jack.t@email.com', 'Moore', 'Oklahoma');

-- Stored Procedure to generate orders
DELIMITER $$

DROP PROCEDURE IF EXISTS generate_sample_orders$$
CREATE PROCEDURE generate_sample_orders()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE v_order_id INT;
    DECLARE v_store_id INT;
    DECLARE v_customer_id INT;
    DECLARE v_product_id INT;
    DECLARE v_qty INT;
    DECLARE v_price DECIMAL(10,2);
    DECLARE v_discount DECIMAL(5,2);
    DECLARE v_date DATETIME;

    WHILE i <= 500 DO
        SET v_store_id = FLOOR(1 + RAND() * 10);
        SET v_customer_id = FLOOR(1 + RAND() * 10);
        SET v_date = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY);

        INSERT INTO orders (customer_id, store_id, order_date, status, total_amount)
        VALUES (v_customer_id, v_store_id, v_date, 'COMPLETED', 0);
        SET v_order_id = LAST_INSERT_ID();

        -- 1-4 items per order
        SET @items = FLOOR(1 + RAND() * 4);
        SET @j = 1;
        WHILE @j <= @items DO
            SET v_product_id = FLOOR(1 + RAND() * 20);
            SET v_qty = FLOOR(1 + RAND() * 5);
            SELECT unit_price INTO v_price FROM products WHERE product_id = v_product_id;
            SET v_discount = ROUND(RAND() * 15, 2);

            INSERT INTO order_items (order_id, product_id, quantity, unit_price, discount)
            VALUES (v_order_id, v_product_id, v_qty, v_price, v_discount);

            SET @j = @j + 1;
        END WHILE;

        -- Update order total
        UPDATE orders o
        SET total_amount = (SELECT SUM(line_total) FROM order_items WHERE order_id = v_order_id)
        WHERE order_id = v_order_id;

        SET i = i + 1;
    END WHILE;
END$$

DROP PROCEDURE IF EXISTS generate_inventory_data$$
CREATE PROCEDURE generate_inventory_data()
BEGIN
    DECLARE v_product_id INT DEFAULT 1;
    DECLARE v_store_id INT;
    DECLARE v_stock INT;

    -- Initialize snapshot
    WHILE v_product_id <= 20 DO
        SET v_store_id = 1;
        WHILE v_store_id <= 10 DO
            SET v_stock = FLOOR(20 + RAND() * 200);
            INSERT INTO inventory_snapshot (product_id, store_id, current_stock, reorder_level)
            VALUES (v_product_id, v_store_id, v_stock, FLOOR(10 + RAND() * 20))
            ON DUPLICATE KEY UPDATE current_stock = v_stock;

            -- Generate movement logs
            INSERT INTO inventory_log (product_id, store_id, movement_type, quantity, stock_before, stock_after, movement_date)
            VALUES
            (v_product_id, v_store_id, 'RESTOCK', FLOOR(50 + RAND() * 100), 0, FLOOR(50 + RAND() * 100), DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 180) DAY)),
            (v_product_id, v_store_id, 'SALE', FLOOR(1 + RAND() * 30), v_stock, v_stock - FLOOR(1 + RAND() * 30), DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY)),
            (v_product_id, v_store_id, 'RESTOCK', FLOOR(20 + RAND() * 80), v_stock, v_stock + FLOOR(20 + RAND() * 80), DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY));

            SET v_store_id = v_store_id + 1;
        END WHILE;
        SET v_product_id = v_product_id + 1;
    END WHILE;
END$$

DELIMITER ;

CALL generate_sample_orders();
CALL generate_inventory_data();
