package com.retailvault.repository.warehouse;

import com.retailvault.entity.warehouse.FactSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactSalesRepository extends JpaRepository<FactSales, Long> {

    @Query(value = """
        SELECT ds.store_name, SUM(fs.net_revenue),
               SUM(fs.quantity), SUM(fs.gross_profit)
        FROM fact_sales fs
        JOIN dim_store ds ON fs.store_key = ds.store_key
        JOIN dim_date dd ON fs.date_key = dd.date_key
        WHERE dd.year = :year
        GROUP BY ds.store_name
        ORDER BY SUM(fs.net_revenue) DESC
    """, nativeQuery = true)
    List<Object[]> getSalesByStore(Integer year);

    @Query(value = """
        SELECT dp.category_name, SUM(fs.net_revenue),
               SUM(fs.quantity)
        FROM fact_sales fs
        JOIN dim_product dp ON fs.product_key = dp.product_key
        JOIN dim_date dd ON fs.date_key = dd.date_key
        WHERE dd.year = :year
        GROUP BY dp.category_name
        ORDER BY SUM(fs.net_revenue) DESC
    """, nativeQuery = true)
    List<Object[]> getSalesByCategory(Integer year);

    @Query(value = """
        SELECT dd.month_name, dd.month_num,
               SUM(fs.net_revenue), SUM(fs.gross_profit)
        FROM fact_sales fs
        JOIN dim_date dd ON fs.date_key = dd.date_key
        WHERE dd.year = :year
        GROUP BY dd.month_name, dd.month_num
        ORDER BY dd.month_num
    """, nativeQuery = true)
    List<Object[]> getMonthlySales(Integer year);

    @Query(value = """
        SELECT dp.product_name, dp.category_name,
               SUM(fs.net_revenue), SUM(fs.quantity),
               SUM(fs.gross_profit)
        FROM fact_sales fs
        JOIN dim_product dp ON fs.product_key = dp.product_key
        JOIN dim_date dd ON fs.date_key = dd.date_key
        WHERE dd.year = :year
        GROUP BY dp.product_name, dp.category_name
        ORDER BY SUM(fs.net_revenue) DESC
        LIMIT :topN
    """, nativeQuery = true)
    List<Object[]> getTopProducts(Integer year, Integer topN);

    @Query(value = """
        SELECT ds.region, SUM(fs.net_revenue),
               COUNT(DISTINCT fs.order_id)
        FROM fact_sales fs
        JOIN dim_store ds ON fs.store_key = ds.store_key
        JOIN dim_date dd ON fs.date_key = dd.date_key
        WHERE dd.year = :year
        GROUP BY ds.region
        ORDER BY SUM(fs.net_revenue) DESC
    """, nativeQuery = true)
    List<Object[]> getSalesByRegion(Integer year);

    @Query(value = """
        SELECT COALESCE(SUM(net_revenue), 0),
               COALESCE(SUM(gross_profit), 0),
               COALESCE(SUM(quantity), 0),
               COUNT(DISTINCT order_id)
        FROM fact_sales fs
        JOIN dim_date dd ON fs.date_key = dd.date_key
        WHERE dd.year = :year
    """, nativeQuery = true)
    List<Object[]> getKpiSummary(Integer year);
}
