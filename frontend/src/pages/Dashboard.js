import React, { useEffect, useState } from 'react';
import { analyticsApi, etlApi } from '../services/api';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend
} from 'recharts';
import { DollarSign, ShoppingCart, TrendingUp, Package, RefreshCw, AlertTriangle } from 'lucide-react';

const COLORS = ['#3b82f6','#10b981','#f59e0b','#8b5cf6','#ef4444','#06b6d4','#f97316','#84cc16'];
const fmt = (n) => n >= 1000000 ? `$${(n/1000000).toFixed(1)}M` : n >= 1000 ? `$${(n/1000).toFixed(1)}K` : `$${n?.toFixed(0) ?? 0}`;
const fmtNum = (n) => n >= 1000000 ? `${(n/1000000).toFixed(1)}M` : n >= 1000 ? `${(n/1000).toFixed(0)}K` : `${n ?? 0}`;

export default function Dashboard() {
  const year = new Date().getFullYear();
  const [kpi, setKpi] = useState(null);
  const [monthly, setMonthly] = useState([]);
  const [byCategory, setByCategory] = useState([]);
  const [byRegion, setByRegion] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [etlStatus, setEtlStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [triggering, setTriggering] = useState(false);

  useEffect(() => { loadAll(); }, []);

  async function loadAll() {
    setLoading(true);
    try {
      const [kpiRes, monthlyRes, catRes, regionRes, lowRes, etlRes] = await Promise.all([
        analyticsApi.getKpi(year),
        analyticsApi.getMonthlySales(year),
        analyticsApi.getSalesByCategory(year),
        analyticsApi.getSalesByRegion(year),
        analyticsApi.getLowStockAlerts(),
        etlApi.getLatestStatus(),
      ]);
      setKpi(kpiRes.data);
      setMonthly((monthlyRes.data || []).map(m => ({
        month: m.month?.substring(0,3),
        Revenue: parseFloat(m.totalRevenue) || 0,
        Profit: parseFloat(m.totalProfit) || 0,
      })));
      setByCategory(catRes.data || []);
      setByRegion(regionRes.data || []);
      setLowStock((lowRes.data || []).slice(0, 5));
      setEtlStatus(etlRes.data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  }

  async function triggerEtl() {
    setTriggering(true);
    try {
      await etlApi.trigger('DASHBOARD_UI');
      setTimeout(() => { loadAll(); setTriggering(false); }, 3000);
    } catch (e) {
      setTriggering(false);
    }
  }

  if (loading) return (
    <div style={{ padding: 32 }}>
      <div className="topbar"><h2>Dashboard</h2></div>
      <div className="loading"><div className="spinner" /><span>Loading warehouse data...</span></div>
    </div>
  );

  return (
    <div>
      <div className="topbar">
        <div>
          <h2>Dashboard</h2>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
            Retail Data Warehouse — FY {year}
          </div>
        </div>
        <div className="topbar-right">
          <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>
            ETL: <span className={etlStatus?.status === 'SUCCESS' ? 'num-positive' : ''}>{etlStatus?.status ?? 'N/A'}</span>
          </span>
          <button className="btn btn-primary" onClick={triggerEtl} disabled={triggering}>
            <RefreshCw size={13} className={triggering ? 'spinner' : ''} />
            {triggering ? 'Running ETL...' : 'Run ETL'}
          </button>
        </div>
      </div>

      <div className="page-content">
        {/* KPI Cards */}
        <div className="kpi-grid">
          <div className="kpi-card">
            <div className="kpi-label">Total Revenue</div>
            <div className="kpi-value" style={{ color: 'var(--accent)' }}>{fmt(kpi?.totalRevenue)}</div>
            <div className="kpi-sub">FY {year}</div>
          </div>
          <div className="kpi-card">
            <div className="kpi-label">Gross Profit</div>
            <div className="kpi-value" style={{ color: 'var(--green)' }}>{fmt(kpi?.totalProfit)}</div>
            <div className="kpi-sub">Margin: {kpi?.profitMargin?.toFixed(1)}%</div>
          </div>
          <div className="kpi-card">
            <div className="kpi-label">Total Orders</div>
            <div className="kpi-value">{fmtNum(kpi?.totalOrders)}</div>
            <div className="kpi-sub">Transactions</div>
          </div>
          <div className="kpi-card">
            <div className="kpi-label">Units Sold</div>
            <div className="kpi-value">{fmtNum(kpi?.totalUnits)}</div>
            <div className="kpi-sub">Line items</div>
          </div>
        </div>

        {/* Monthly Revenue */}
        <div className="grid-2" style={{ marginBottom: 16 }}>
          <div className="card col-span-2">
            <div className="card-title">Monthly Revenue & Profit — {year}</div>
            <ResponsiveContainer width="100%" height={220}>
              <LineChart data={monthly} margin={{ top: 5, right: 20, left: 10, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="month" tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                <YAxis tickFormatter={v => fmt(v)} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                <Tooltip formatter={(v) => fmt(v)} contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
                <Legend />
                <Line type="monotone" dataKey="Revenue" stroke="var(--accent)" strokeWidth={2} dot={false} />
                <Line type="monotone" dataKey="Profit" stroke="var(--green)" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="grid-2" style={{ marginBottom: 16 }}>
          {/* Sales by Category */}
          <div className="card">
            <div className="card-title">Revenue by Category</div>
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie data={byCategory} dataKey="totalRevenue" nameKey="category" cx="50%" cy="50%" outerRadius={80} label={({ category, percent }) => `${category?.split(' ')[0]} ${(percent*100).toFixed(0)}%`} labelLine={false} fontSize={10}>
                  {byCategory.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Tooltip formatter={v => fmt(v)} contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Sales by Region */}
          <div className="card">
            <div className="card-title">Revenue by Region</div>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={byRegion} margin={{ top: 5, right: 10, left: 10, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="region" tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                <YAxis tickFormatter={v => fmt(v)} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                <Tooltip formatter={v => fmt(v)} contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
                <Bar dataKey="totalRevenue" fill="var(--purple)" radius={[3,3,0,0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Low Stock Alerts */}
        {lowStock.length > 0 && (
          <div className="card">
            <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <AlertTriangle size={13} style={{ color: 'var(--yellow)' }} />
              Low Stock Alerts
            </div>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Store</th>
                  <th>Current Stock</th>
                  <th>Reorder Level</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {lowStock.map((item, i) => (
                  <tr key={i}>
                    <td>{item.productName}</td>
                    <td style={{ color: 'var(--text-muted)' }}>{item.storeName}</td>
                    <td className="mono num-negative">{item.currentStock}</td>
                    <td className="mono">{item.reorderLevel}</td>
                    <td><span className="badge badge-warning">Reorder</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
