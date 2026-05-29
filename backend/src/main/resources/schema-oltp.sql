-- ============================================================
-- RetailVault OLTP Source Schema (Transactional Database)
-- ============================================================

CREATE DATABASE IF NOT EXISTS retailvault_oltp;
USE retailvault_oltp;

-- Stores
CREATE TABLE IF NOT EXISTS stores (
    store_id INT AUTO_INCREMENT PRIMARY KEY,
    store_name VARCHAR(100) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(50),
    region VARCHAR(50),
    store_type VARCHAR(50),
    opened_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    country VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    parent_category VARCHAR(100)
);

-- Products
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) UNIQUE NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    category_id INT,
    supplier_id INT,
    unit_price DECIMAL(10,2),
    cost_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

-- Customers
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    store_id INT NOT NULL,
    order_date DATETIME NOT NULL,
    status VARCHAR(30) DEFAULT 'COMPLETED',
    total_amount DECIMAL(12,2),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);

-- Order Line Items
CREATE TABLE IF NOT EXISTS order_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0.00,
    line_total DECIMAL(12,2) GENERATED ALWAYS AS (quantity * unit_price * (1 - discount/100)) STORED,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Inventory Log
CREATE TABLE IF NOT EXISTS inventory_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    store_id INT NOT NULL,
    movement_type ENUM('RESTOCK', 'SALE', 'RETURN', 'ADJUSTMENT', 'TRANSFER') NOT NULL,
    quantity INT NOT NULL,
    stock_before INT,
    stock_after INT,
    reference_id INT,
    movement_date DATETIME NOT NULL,
    notes VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);

-- Current Inventory Snapshot
CREATE TABLE IF NOT EXISTS inventory_snapshot (
    snapshot_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    store_id INT NOT NULL,
    current_stock INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_product_store (product_id, store_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);
