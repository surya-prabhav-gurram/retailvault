import React, { useEffect, useState } from 'react';
import { analyticsApi } from '../services/api';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer, Legend, Cell
} from 'recharts';

const COLORS = ['#3b82f6','#10b981','#f59e0b','#8b5cf6','#ef4444','#06b6d4','#f97316','#84cc16','#ec4899','#14b8a6'];
const fmt = (n) => n >= 1000000 ? `$${(n/1000000).toFixed(2)}M` : n >= 1000 ? `$${(n/1000).toFixed(1)}K` : `$${Number(n)?.toFixed(2) ?? 0}`;

export default function SalesPage() {
  const currentYear = new Date().getFullYear();
  const [year, setYear] = useState(currentYear);
  const [byStore, setByStore] = useState([]);
  const [byCategory, setByCategory] = useState([]);
  const [topProducts, setTopProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => { load(); }, [year]);

  async function load() {
    setLoading(true);
    try {
      const [storeRes, catRes, prodRes] = await Promise.all([
        analyticsApi.getSalesByStore(year),
        analyticsApi.getSalesByCategory(year),
        analyticsApi.getTopProducts(year, 10),
      ]);
      setByStore(storeRes.data || []);
      setByCategory(catRes.data || []);
      setTopProducts(prodRes.data || []);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  }

  return (
    <div>
      <div className="topbar">
        <h2>Sales Analytics</h2>
        <select className="select" value={year} onChange={e => setYear(+e.target.value)}>
          {[currentYear, currentYear-1, currentYear-2].map(y => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>
      </div>

      <div className="page-content">
        {loading ? (
          <div className="loading"><div className="spinner" /><span>Loading...</span></div>
        ) : (
          <>
            {/* Revenue by Store */}
            <div className="card" style={{ marginBottom: 16 }}>
              <div className="card-title">Revenue by Store — {year}</div>
              <ResponsiveContainer width="100%" height={260}>
                <BarChart data={byStore} margin={{ top: 5, right: 20, left: 20, bottom: 60 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                  <XAxis dataKey="storeName" tick={{ fill: 'var(--text-muted)', fontSize: 10 }} angle={-30} textAnchor="end" interval={0} />
                  <YAxis tickFormatter={fmt} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                  <Tooltip formatter={fmt} contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
                  <Bar dataKey="totalRevenue" name="Revenue" radius={[3,3,0,0]}>
                    {byStore.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="grid-2" style={{ marginBottom: 16 }}>
              {/* Revenue by Category */}
              <div className="card">
                <div className="card-title">Revenue by Category</div>
                <ResponsiveContainer width="100%" height={240}>
                  <BarChart data={byCategory} layout="vertical" margin={{ top: 5, right: 20, left: 80, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                    <XAxis type="number" tickFormatter={fmt} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                    <YAxis dataKey="category" type="category" tick={{ fill: 'var(--text-muted)', fontSize: 11 }} width={75} />
                    <Tooltip formatter={fmt} contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
                    <Bar dataKey="totalRevenue" name="Revenue" radius={[0,3,3,0]}>
                      {byCategory.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              </div>

              {/* Store Profit Comparison */}
              <div className="card">
                <div className="card-title">Store Revenue vs Profit</div>
                <ResponsiveContainer width="100%" height={240}>
                  <BarChart data={byStore.slice(0,6)} margin={{ top: 5, right: 10, left: 10, bottom: 30 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                    <XAxis dataKey="storeName" tick={{ fill: 'var(--text-muted)', fontSize: 9 }} angle={-20} textAnchor="end" interval={0} />
                    <YAxis tickFormatter={fmt} tick={{ fill: 'var(--text-muted)', fontSize: 11 }} />
                    <Tooltip formatter={fmt} contentStyle={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 6 }} />
                    <Legend />
                    <Bar dataKey="totalRevenue" name="Revenue" fill="var(--accent)" radius={[3,3,0,0]} />
                    <Bar dataKey="totalProfit" name="Profit" fill="var(--green)" radius={[3,3,0,0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>

            {/* Top Products Table */}
            <div className="card">
              <div className="card-title">Top 10 Products by Revenue</div>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Product</th>
                    <th>Category</th>
                    <th>Revenue</th>
                    <th>Units Sold</th>
                    <th>Gross Profit</th>
                    <th>Margin</th>
                  </tr>
                </thead>
                <tbody>
                  {topProducts.map((p, i) => {
                    const margin = p.totalRevenue > 0 ? ((p.totalProfit / p.totalRevenue) * 100).toFixed(1) : 0;
                    return (
                      <tr key={i}>
                        <td style={{ color: 'var(--text-muted)' }} className="mono">{i + 1}</td>
                        <td>{p.productName}</td>
                        <td><span className="badge" style={{ background: 'var(--surface2)', color: 'var(--text-muted)' }}>{p.category}</span></td>
                        <td className="mono" style={{ color: 'var(--accent)' }}>{fmt(p.totalRevenue)}</td>
                        <td className="mono">{p.totalQuantity?.toLocaleString()}</td>
                        <td className="mono num-positive">{fmt(p.totalProfit)}</td>
                        <td className="mono">{margin}%</td>
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
