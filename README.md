# RetailVault — Data Warehouse & ETL Pipeline

A full-stack retail analytics platform built on a star-schema data warehouse.  
Transactional data flows from an OLTP MySQL database through a nightly ETL pipeline into a warehouse, then surfaced in a React dashboard.

---

## Architecture

```
┌─────────────────┐     ETL Pipeline     ┌──────────────────────┐
│  OLTP Database  │ ──────────────────▶  │  Warehouse Database  │
│ retailvault_oltp│   (Spring Batch /     │retailvault_warehouse │
│                 │    Scheduler)         │                      │
│ • stores        │                       │  Dimensions          │
│ • products      │                       │  • dim_date          │
│ • customers     │                       │  • dim_store         │
│ • orders        │                       │  • dim_product       │
│ • order_items   │                       │  • dim_customer      │
│ • inventory_log │                       │  • dim_supplier      │
└─────────────────┘                       │                      │
                                          │  Facts               │
                                          │  • fact_sales        │
                                          │  • fact_inventory    │
                                          │  • etl_run_log       │
                                          └──────────────────────┘
                                                    │
                                          Spring Boot REST API
                                                    │
                                          React Dashboard (port 3000)
```

### Tech Stack

| Layer     | Technology                               |
|-----------|------------------------------------------|
| Backend   | Java 17, Spring Boot 3.2, Spring Batch   |
| Database  | MySQL 8.0 (dual datasource)              |
| Frontend  | React 18, Recharts, Axios, React Router  |
| Container | Docker, Docker Compose, nginx            |

---

## Quick Start (Docker — recommended)

```bash
git clone <your-repo-url>
cd retailvault
docker compose up --build
```

- **Frontend**: http://localhost:3000  
- **Backend API**: http://localhost:8080/api  
- **MySQL**: localhost:3306

The init scripts run automatically on first boot: schemas are created and sample data is seeded.

---

## Local Development (no Docker)

### Prerequisites
- Java 17+, Maven 3.9+
- Node 20+, npm
- MySQL 8.0 running on `localhost:3306`

### 1 — Database setup

```bash
mysql -u root -p < backend/src/main/resources/schema-oltp.sql
mysql -u root -p < backend/src/main/resources/schema-warehouse.sql
mysql -u root -p < backend/src/main/resources/seed-data.sql
```

### 2 — Backend

```bash
cd backend
mvn spring-boot:run
# API available at http://localhost:8080
```

### 3 — Frontend

```bash
cd frontend
npm install
npm start
# App available at http://localhost:3000
```

---

## ETL Pipeline

The pipeline runs automatically at **2:00 AM** every day (configurable in `application.properties`):

```properties
etl.schedule.cron=0 0 2 * * *
etl.schedule.enabled=true
```

You can also trigger it manually from the **ETL Pipeline** page in the UI, or via API:

```bash
curl -X POST http://localhost:8080/api/etl/trigger \
  -H "Content-Type: application/json" \
  -d '{"triggeredBy": "MANUAL_API"}'
```

### ETL steps (in order)

1. **Populate `dim_date`** — generates date dimension rows for ±2 years
2. **Load dimensions** — upserts stores, products, suppliers, customers (SCD Type 2)
3. **Load `fact_sales`** — transforms order items → measures (gross revenue, discount, net revenue, COGS, gross profit)
4. **Load `fact_inventory`** — transforms inventory log → movement facts with reorder-level flag

---

## REST API Reference

| Method | Endpoint                          | Description                       |
|--------|-----------------------------------|-----------------------------------|
| GET    | `/api/analytics/kpi?year=`        | KPI summary (revenue, profit, ...) |
| GET    | `/api/analytics/sales/by-store`   | Revenue & profit by store         |
| GET    | `/api/analytics/sales/by-category`| Revenue by product category       |
| GET    | `/api/analytics/sales/monthly`    | Month-by-month sales trend        |
| GET    | `/api/analytics/sales/top-products`| Top N products by revenue        |
| GET    | `/api/analytics/sales/by-region`  | Sales by geographic region        |
| GET    | `/api/analytics/inventory/turnover`| Inventory turnover by product    |
| GET    | `/api/analytics/inventory/low-stock`| Products below reorder level    |
| GET    | `/api/analytics/inventory/movements`| Movement summary by type        |
| POST   | `/api/etl/trigger`                | Trigger full ETL manually         |
| GET    | `/api/etl/history`                | Last 20 ETL run logs              |
| GET    | `/api/etl/status/latest`          | Most recent ETL run status        |

---

## Project Structure

```
retailvault/
├── backend/                          # Spring Boot application
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/retailvault/
│       │   ├── RetailVaultApplication.java
│       │   ├── config/               # DataSource, CORS, JPA repo config
│       │   ├── controller/           # REST controllers (analytics + ETL)
│       │   ├── dto/                  # Response DTOs
│       │   ├── entity/
│       │   │   ├── oltp/             # OLTP JPA entities
│       │   │   └── warehouse/        # Warehouse JPA entities
│       │   ├── etl/                  # ETL pipeline + scheduler
│       │   ├── repository/
│       │   │   ├── oltp/             # OLTP Spring Data repos
│       │   │   └── warehouse/        # Warehouse Spring Data repos
│       │   └── service/              # AnalyticsService
│       └── resources/
│           ├── application.properties
│           ├── schema-oltp.sql
│           ├── schema-warehouse.sql
│           └── seed-data.sql
│
├── frontend/                         # React application
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
│       ├── App.js
│       ├── index.js
│       ├── index.css
│       ├── components/layout/        # Sidebar layout
│       ├── pages/                    # Dashboard, Sales, Inventory, ETL
│       └── services/api.js           # Axios API client
│
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## Configuration

Edit `backend/src/main/resources/application.properties` to change:

- Database credentials (`spring.datasource.oltp.*`, `spring.datasource.warehouse.*`)
- ETL schedule (`etl.schedule.cron`)
- CORS origins (`cors.allowed-origins`)

---

## License

MIT
