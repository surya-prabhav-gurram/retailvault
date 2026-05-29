import React, { useEffect, useState, useRef } from 'react';
import { analyticsApi, etlApi, demoApi } from '../services/api';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend
} from 'recharts';
import { RefreshCw, AlertTriangle, Zap, ShoppingBag, RotateCcw, Clock } from 'lucide-react';

const COLORS = ['#3b82f6','#10b981','#f59e0b','#8b5cf6','#ef4444','#06b6d4','#f97316','#84cc16'];
const fmt = (n) => n >= 1000000 ? `$${(n/1000000).toFixed(1)}M` : n >= 1000 ? `$${(n/1000).toFixed(1)}K` : `$${n?.toFixed(0) ?? 0}`;
const fmtNum = (n) => n >= 1000000 ? `${(n/1000000).toFixed(1)}M` : n >= 1000 ? `${(n/1000).toFixed(0)}K` : `${n ?? 0}`;
const fmtDt = (dt) => { if (!dt) return '—'; const d = new Date(dt + 'Z'); return d.toLocaleString('en-US', { month: 'numeric', day: 'numeric', hour: 'numeric', minute: '2-digit', hour12: true }); };

const SCENARIOS = [
  { key: 'NORMAL', label: '🛒 Generate Orders', desc: 'Add random orders to OLTP', color: '#3b82f6', count: 20 },
  { key: 'BLACK_FRIDAY', label: '🔥 Black Friday', desc: 'Surge: 80 high-volume orders', color: '#ef4444', count: 80 },
  { key: 'RESTOCK', label: '📦 Restock Inventory', desc: 'Replenish low-stock items', color: '#10b981', count: 0 },
];

export default function Dashboard() {
  const year = new Date().getFullYear();
  const [kpi, setKpi] = useState(null);
  const [monthly, setMonthly] = useState([]);
  const [byCategory, setByCategory] = useState([]);
  const [byRegion, setByRegion] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [recentOrders, setRecentOrders] = useState([]);
  const [etlStatus, setEtlStatus] = useState(null);
  const [loading, setLoading] = useState(true);
  const [triggering, setTriggering] = useState(false);
  const [demoLoading, setDemoLoading] = useState(null);
  const [toast, setToast] = useState(null);
  const [orderCount, setOrderCount] = useState(20);
  const [goalRevenue, setGoalRevenue] = useState(50000);
  const pollRef = useRef(null);

  useEffect(() => { loadAll(); }, []);

  // Auto-refresh recent orders every 30s
  useEffect(() => {
    const iv = setInterval(() => { loadRecentOrders(); }, 30000);
    return () => clearInterval(iv);
  }, []);

  function showToast(msg, type = 'success') {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 4000);
  }

  async function loadRecentOrders() {
    try {
      const res = await analyticsApi.getRecentOrders();
      setRecentOrders(res.data || []);
    } catch (e) {}
  }

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
      loadRecentOrders();
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  }

  async function triggerEtl() {
    setTriggering(true);
    try {
      await etlApi.trigger('DASHBOARD_UI');
      showToast('ETL pipeline started! Dashboard will refresh in ~6 minutes.', 'info');
      setTimeout(() => { loadAll(); setTriggering(false); }, 8000);
    } catch (e) { setTriggering(false); showToast('ETL trigger failed', 'error'); }
  }

  async function runScenario(scenario) {
    setDemoLoading(scenario.key);
    try {
      if (scenario.key === 'RESTOCK') {
        await demoApi.restockInventory();
        showToast('Inventory restocked! Run ETL to update the dashboard.', 'success');
      } else {
        const count = scenario.key === 'BLACK_FRIDAY' ? scenario.count : orderCount;
        await demoApi.generateOrders(count, scenario.key);
        showToast(`${count} orders generated! Click "Run ETL" to see them in the analytics.`, 'success');
        loadRecentOrders();
      }
    } catch (e) { showToast('Action failed: ' + e, 'error'); }
    finally { setDemoLoading(null); }
  }

  const goalPct = kpi ? Math.min(100, (kpi.totalRevenue / goalRevenue) * 100).toFixed(1) : 0;

  if (loading) return (
    <div style={{ padding: 32 }}>
      <div className="topbar"><h2>Dashboard</h2></div>
      <div className="loading"><div className="spinner" /><span>Loading warehouse data...</span></div>
    </div>
  );

  return (
    <div>
      {/* Toast */}
      {toast && (
        <div style={{
          position: 'fixed', top: 16, right: 16, zIndex: 1000,
          background: toast.type === 'error' ? '#ef4444' : toast.type === 'info' ? '#3b82f6' : '#10b981',
          color: '#fff', padding: '10px 16px', borderRadius: 8, fontSize: 13,
          boxShadow: '0 4px 12px rgba(0,0,0,0.3)', maxWidth: 340
        }}>
          {toast.msg}
        </div>
      )}

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

        {/* Welcome Banner */}
        <div style={{
          background: 'linear-gradient(135deg, rgba(59,130,246,0.12) 0%, rgba(16,185,129,0.08) 100%)',
          border: '1px solid rgba(59,130,246,0.25)',
          borderRadius: 10, padding: '16px 20px', marginBottom: 16,
          display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12
        }}>
          <div>
            <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--accent)', marginBottom: 4 }}>
              👋 Welcome to RetailVault
            </div>
            <div style={{ fontSize: 12, color: 'var(--text-muted)', maxWidth: 620, lineHeight: 1.6 }}>
              A full-stack retail data warehousing platform. Transactional data from <strong style={{color:'var(--text)'}}>MySQL OLTP</strong> is
              extracted via <strong style={{color:'var(--text)'}}>Spring Batch ETL</strong> into a <strong style={{color:'var(--text)'}}>star schema warehouse</strong> — powering real-time analytics.
              Use the <strong style={{color:'var(--text)'}}>Demo Playground</strong> below to generate live orders and watch the pipeline in action.
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', justifyContent: 'flex-end' }}>
            {[
              { label: 'Spring Boot', color: '#10b981' },
              { label: 'Spring Batch ETL', color: '#3b82f6' },
              { label: 'Star Schema', color: '#8b5cf6' },
              { label: 'SCD Type 2', color: '#f59e0b' },
              { label: 'React', color: '#06b6d4' },
              { label: 'MySQL', color: '#10b981' },
            ].map(t => (
              <span key={t.label} style={{
                fontSize: 10, fontWeight: 600, padding: '3px 8px', borderRadius: 4,
                border: `1px solid ${t.color}66`, color: t.color, background: `${t.color}18`
              }}>{t.label}</span>
            ))}
          </div>
        </div>

        {/* Demo Playground */}
        <div className="card" style={{ marginBottom: 16, border: '1px solid rgba(245,158,11,0.3)', background: 'rgba(245,158,11,0.04)' }}>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <Zap size={13} style={{ color: '#f59e0b' }} />
            Demo Playground — Try the ETL Pipeline Live
          </div>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', marginBottom: 14 }}>
            Generate orders into the OLTP database, then click <strong style={{color:'var(--text)'}}>Run ETL</strong> in the top-right to move data into the warehouse and watch the charts update.
          </div>
          <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'flex-end' }}>
            {/* Order count slider */}
            <div style={{ minWidth: 180 }}>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 6 }}>Order Count: <strong style={{color:'var(--text)'}}>{orderCount}</strong></div>
              <input type="range" min={5} max={100} value={orderCount} onChange={e => setOrderCount(+e.target.value)}
                style={{ width: '100%', accentColor: '#3b82f6' }} />
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 10, color: 'var(--text-muted)' }}>
                <span>5</span><span>100</span>
              </div>
            </div>

            {/* Scenario buttons */}
            {SCENARIOS.map(s => (
              <button key={s.key} onClick={() => runScenario(s)} disabled={!!demoLoading}
                style={{
                  padding: '10px 16px', borderRadius: 8, border: `1px solid ${s.color}55`,
                  background: demoLoading === s.key ? `${s.color}22` : `${s.color}11`,
                  color: s.color, cursor: demoLoading ? 'not-allowed' : 'pointer',
                  fontSize: 13, fontWeight: 600, display: 'flex', flexDirection: 'column', gap: 2, minWidth: 160
                }}>
                <span>{demoLoading === s.key ? '⏳ Working...' : s.label}</span>
                <span style={{ fontSize: 10, fontWeight: 400, opacity: 0.8 }}>{s.desc}</span>
              </button>
            ))}
          </div>
        </div>

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

        {/* Revenue Goal Tracker */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>Revenue Goal Tracker</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>Goal: $</span>
              <input type="number" value={goalRevenue} onChange={e => setGoalRevenue(+e.target.value)}
                style={{ width: 90, padding: '3px 8px', background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 4, color: 'var(--text)', fontSize: 12 }} />
            </div>
          </div>
          <div style={{ marginBottom: 8, display: 'flex', justifyContent: 'space-between', fontSize: 12 }}>
            <span style={{ color: 'var(--text-muted)' }}>FY {year} Progress</span>
            <span style={{ color: goalPct >= 100 ? 'var(--green)' : 'var(--accent)', fontWeight: 600 }}>
              {fmt(kpi?.totalRevenue)} / {fmt(goalRevenue)} ({goalPct}%)
            </span>
          </div>
          <div style={{ background: 'var(--surface2)', borderRadius: 8, height: 14, overflow: 'hidden' }}>
            <div style={{
              height: '100%', borderRadius: 8, transition: 'width 0.8s ease',
              width: `${goalPct}%`,
              background: goalPct >= 100 ? 'linear-gradient(90deg,#10b981,#34d399)' : goalPct >= 70 ? 'linear-gradient(90deg,#f59e0b,#fbbf24)' : 'linear-gradient(90deg,#3b82f6,#60a5fa)'
            }} />
          </div>
          <div style={{ marginTop: 6, fontSize: 11, color: 'var(--text-muted)' }}>
            {goalPct >= 100 ? '🎉 Goal achieved!' : `${fmt(goalRevenue - (kpi?.totalRevenue || 0))} remaining to goal`}
          </div>
        </div>

        {/* Monthly Revenue + Live Order Feed */}
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 16, marginBottom: 16 }}>
          <div className="card">
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

          {/* Live Order Feed */}
          <div className="card">
            <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
                <span style={{ width: 7, height: 7, borderRadius: '50%', background: '#10b981', display: 'inline-block', animation: 'pulse 2s infinite' }} />
                Live Order Feed
              </span>
              <button onClick={loadRecentOrders} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', fontSize: 11 }}>↻</button>
            </div>
            <div style={{ fontSize: 10, color: 'var(--text-muted)', marginBottom: 8 }}>OLTP — refreshes every 30s</div>
            {recentOrders.length === 0 ? (
              <div style={{ color: 'var(--text-muted)', fontSize: 12, textAlign: 'center', padding: '20px 0' }}>No recent orders</div>
            ) : recentOrders.slice(0, 6).map((o, i) => (
              <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom: i < 5 ? '1px solid var(--border)' : 'none' }}>
                <div>
                  <div style={{ fontSize: 11, fontWeight: 600 }}>{o.storeName}</div>
                  <div style={{ fontSize: 10, color: 'var(--text-muted)' }}>{o.customerName} · {fmtDt(o.orderDate)}</div>
                </div>
                <div style={{ fontSize: 12, color: 'var(--accent)', fontWeight: 600 }}>{fmt(o.totalAmount)}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="grid-2" style={{ marginBottom: 16 }}>
          {/* Sales by Category */}
          <div className="card">
            <div className="card-title">Revenue by Category</div>
            <ResponsiveContainer width="100%" height={220}>
              <PieChart>
                <Pie data={byCategory} dataKey="totalRevenue" nameKey="category" cx="50%" cy="50%" outerRadius={80}
                  label={({ category, percent }) => `${category?.split(' ')[0]} ${(percent*100).toFixed(0)}%`} labelLine={false} fontSize={10}>
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
            <div className="card-title" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                <AlertTriangle size={13} style={{ color: 'var(--yellow)' }} />
                Low Stock Alerts
              </span>
              <button onClick={() => runScenario(SCENARIOS[2])} disabled={!!demoLoading}
                style={{ padding: '4px 10px', borderRadius: 6, border: '1px solid #10b98155', background: '#10b98111', color: '#10b981', cursor: 'pointer', fontSize: 11, fontWeight: 600 }}>
                📦 Restock All
              </button>
            </div>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Product</th><th>Store</th><th>Current Stock</th><th>Reorder Level</th><th>Status</th>
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
