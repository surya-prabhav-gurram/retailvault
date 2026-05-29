-- ============================================================
-- RetailVault Data Warehouse Schema (Star Schema)
-- ============================================================

CREATE DATABASE IF NOT EXISTS retailvault_warehouse;
USE retailvault_warehouse;

-- ============================================================
-- DIMENSION TABLES
-- ============================================================

-- Dim Date
CREATE TABLE IF NOT EXISTS dim_date (
    date_key INT PRIMARY KEY,
    full_date DATE NOT NULL,
    day_of_week TINYINT,
    day_name VARCHAR(10),
    day_of_month TINYINT,
    day_of_year SMALLINT,
    week_of_year TINYINT,
    month_num TINYINT,
    month_name VARCHAR(10),
    quarter TINYINT,
    year SMALLINT,
    is_weekend BOOLEAN,
    is_holiday BOOLEAN DEFAULT FALSE
);

-- Dim Store
CREATE TABLE IF NOT EXISTS dim_store (
    store_key INT AUTO_INCREMENT PRIMARY KEY,
    store_id INT NOT NULL,
    store_name VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    region VARCHAR(50),
    store_type VARCHAR(50),
    opened_date DATE,
    effective_date DATE,
    expiry_date DATE,
    is_current BOOLEAN DEFAULT TRUE
);

-- Dim Product
CREATE TABLE IF NOT EXISTS dim_product (
    product_key INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    sku VARCHAR(50),
    product_name VARCHAR(200),
    category_name VARCHAR(100),
    parent_category VARCHAR(100),
    supplier_name VARCHAR(100),
    supplier_country VARCHAR(50),
    unit_price DECIMAL(10,2),
    cost_price DECIMAL(10,2),
    effective_date DATE,
    expiry_date DATE,
    is_current BOOLEAN DEFAULT TRUE
);

-- Dim Supplier
CREATE TABLE IF NOT EXISTS dim_supplier (
    supplier_key INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    supplier_name VARCHAR(100),
    contact_name VARCHAR(100),
    country VARCHAR(50),
    is_current BOOLEAN DEFAULT TRUE
);

-- Dim Customer
CREATE TABLE IF NOT EXISTS dim_customer (
    customer_key INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    full_name VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(50),
    is_current BOOLEAN DEFAULT TRUE
);

-- ============================================================
-- FACT TABLES
-- ============================================================

-- Fact Sales
CREATE TABLE IF NOT EXISTS fact_sales (
    sales_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_key INT NOT NULL,
    store_key INT NOT NULL,
    product_key INT NOT NULL,
    customer_key INT,
    order_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    discount_pct DECIMAL(5,2),
    gross_revenue DECIMAL(12,2),
    discount_amount DECIMAL(12,2),
    net_revenue DECIMAL(12,2),
    cost_of_goods DECIMAL(12,2),
    gross_profit DECIMAL(12,2),
    FOREIGN KEY (date_key) REFERENCES dim_date(date_key),
    FOREIGN KEY (store_key) REFERENCES dim_store(store_key),
    FOREIGN KEY (product_key) REFERENCES dim_product(product_key),
    FOREIGN KEY (customer_key) REFERENCES dim_customer(customer_key),
    INDEX idx_date (date_key),
    INDEX idx_store (store_key),
    INDEX idx_product (product_key)
);

-- Fact Inventory
CREATE TABLE IF NOT EXISTS fact_inventory (
    inventory_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_key INT NOT NULL,
    store_key INT NOT NULL,
    product_key INT NOT NULL,
    supplier_key INT,
    movement_type VARCHAR(20),
    quantity_moved INT,
    stock_before INT,
    stock_after INT,
    reorder_level INT,
    is_below_reorder BOOLEAN,
    FOREIGN KEY (date_key) REFERENCES dim_date(date_key),
    FOREIGN KEY (store_key) REFERENCES dim_store(store_key),
    FOREIGN KEY (product_key) REFERENCES dim_product(product_key),
    INDEX idx_date (date_key),
    INDEX idx_store (store_key),
    INDEX idx_product (product_key)
);

-- ============================================================
-- ETL AUDIT TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS etl_run_log (
    run_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100),
    status ENUM('RUNNING','SUCCESS','FAILED') DEFAULT 'RUNNING',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    rows_extracted INT DEFAULT 0,
    rows_loaded INT DEFAULT 0,
    error_message TEXT,
    triggered_by VARCHAR(50) DEFAULT 'SCHEDULER'
);
