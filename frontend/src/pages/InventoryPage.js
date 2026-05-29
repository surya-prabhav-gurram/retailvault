import React, { useEffect, useState } from 'react';
import { analyticsApi } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Cell
} from 'recharts';
import { AlertTriangle } from 'lucide-react';

const COLORS = ['#3b82f6','#10b981','#f59e0b','#8b5cf6','#ef4444'];

export default function InventoryPage() {
  const year = new Date().getFullYear();
  const [turnover, setTurnover] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [movements, setMovements] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { load(); }, []);

  async function load() {
    setLoading(true);
    try {
      const [turnRes, lowRes, movRes] = await Promise.all([
        analyticsApi.getInventoryTurnover(),
        analyticsApi.getLowStockAlerts(),
        analyticsApi.getInventoryMovements(year),
      ]);
      setTurnover(turnRes.data || []);
      setLowStock(lowRes.data || []);
      setMovements(movRes.data || []);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  }

  const topTurnover = turnover.slice(0, 10);

  return (
    <div>
      <div className="topbar">
        <h2>Inventory Analytics</h2>
        <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>
          <span className="num-negative">{lowStock.length}</span> items below reorder level
        </div>
      </div>

      <div className="page-content">
        {loading ? (
          <div className="loading"><div className="spinner" /><span>Loading...</span></div>
        ) : (
          <>
            {/* KPI row */}
            <div className="kpi-grid" style={{ marginBottom: 20 }}>
              <div className="kpi-card">
                <div className="kpi-label">Low Stock Alerts</div>
                <div className="kpi-value num-negative">{lowStock.length}</div>
                <div className="kpi-sub">Items below reorder</div>
              </div>
              <div className="kpi-card">
                <div className="kpi-label">Products Tracked</div>
                <div className="kpi-value">{turnover.length}</div>
                <div className="kpi-sub">Across all stores</div>
              </div>
              <div className="kpi-card">
                <div className="kpi-label">Movement Types</div>
                <div className="kpi-value">{movements.length}</div>
                <div className="kpi-sub">Distinct types in {year}</div>
              </div>
              <div className="kpi-card">
                <div className="kpi-label">Total Movements</div>
                <div className="kpi-value">{movements.reduce((s, m) => s + (m.eventCount || 0), 0)}</div>
                <div className="kpi-sub">Events in {year}</div>
              </div>
            </div>

            <div className="grid-2" style={{ marginBottom: 16 }}>
              {/* Inventory Movement Summary */}
              <div className="card">
                <div className="card-title">Inventory Movements — {year}</div>
                <ResponsiveContainer width="100%" height={220}>
                  <BarChart data={movements} margin={{ top: 5, right: 10, left: 10, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                    <XAxis dataKey="movementType" tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                    <YAxis tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                    <Tooltip contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
                    <Bar dataKey="totalQuantity" name="Total Qty" radius={[3,3,0,0]}>
                      {movements.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>

              {/* Top Turnover Products */}
              <div className="card">
                <div className="card-title">Top 10 Products by Units Sold</div>
                <ResponsiveContainer width="100%" height={220}>
                  <BarChart data={topTurnover} layout="vertical" margin={{ top: 5, right: 10, left: 100, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                    <XAxis type="number" tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                    <YAxis dataKey="productName" type="category" tick={{ fill: 'var(--text-muted)', fontSize: 9 }} width={95}
                      tickFormatter={v => v?.length > 14 ? v.substring(0,13)+'…' : v} />
                    <Tooltip contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
                    <Bar dataKey="totalSold" name="Units Sold" fill="var(--purple)" radius={[0,3,3,0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>

            {/* Low Stock Alerts Table */}
            <div className="card" style={{ marginBottom: 16 }}>
              <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                <AlertTriangle size={13} style={{ color: 'var(--yellow)' }} />
                Low Stock Alerts — Requires Replenishment
              </div>
              {lowStock.length === 0 ? (
                <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>No low stock items found.</div>
              ) : (
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Product</th>
                      <th>Store</th>
                      <th>Current Stock</th>
                      <th>Reorder Level</th>
                      <th>Deficit</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {lowStock.map((item, i) => (
                      <tr key={i}>
                        <td>{item.productName}</td>
                        <td style={{ color: 'var(--text-muted)' }}>{item.storeName}</td>
                        <td className="mono num-negative">{item.currentStock}</td>
                        <td className="mono">{item.reorderLevel}</td>
                        <td className="mono num-negative">-{Math.max(0, item.reorderLevel - item.currentStock)}</td>
                        <td><span className="badge badge-warning">Reorder Now</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>

            {/* Full Turnover Table */}
            <div className="card">
              <div className="card-title">Inventory Turnover Report</div>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Store</th>
                    <th>Units Sold</th>
                    <th>Avg Stock</th>
                    <th>Min Stock</th>
                    <th>Turnover Rate</th>
                  </tr>
                </thead>
                <tbody>
                  {turnover.map((item, i) => {
                    const rate = item.avgStock > 0 ? (item.totalSold / item.avgStock).toFixed(2) : 'N/A';
                    return (
                      <tr key={i}>
                        <td>{item.productName}</td>
                        <td style={{ color: 'var(--text-muted)' }}>{item.storeName}</td>
                        <td className="mono">{item.totalSold}</td>
                        <td className="mono">{item.avgStock?.toFixed(0)}</td>
                        <td className="mono" style={{ color: item.minStock < 15 ? 'var(--red)' : 'inherit' }}>{item.minStock}</td>
                        <td className="mono" style={{ color: 'var(--accent)' }}>{rate}x</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
