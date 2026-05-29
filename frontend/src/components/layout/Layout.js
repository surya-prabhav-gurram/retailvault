import React from 'react';
import { Outlet, NavLink } from 'react-router-dom';
import {
  LayoutDashboard, TrendingUp, Package, Database, Settings
} from 'lucide-react';

const navItems = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/sales', icon: TrendingUp, label: 'Sales Analytics' },
  { to: '/inventory', icon: Package, label: 'Inventory' },
  { to: '/etl', icon: Database, label: 'ETL Pipeline' },
];

export default function Layout() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <h1>RetailVault</h1>
          <span>Data Warehouse</span>
        </div>
        <nav className="sidebar-nav">
          <div className="nav-section">Analytics</div>
          {navItems.slice(0, 3).map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
            >
              <Icon size={15} />
              {label}
            </NavLink>
          ))}
          <div className="nav-section" style={{ marginTop: 12 }}>Pipeline</div>
          {navItems.slice(3).map(({ to, icon: Icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
            >
              <Icon size={15} />
              {label}
            </NavLink>
          ))}
        </nav>
        <div style={{ padding: '16px 20px', borderTop: '1px solid var(--border)', fontSize: 11, color: 'var(--text-muted)' }}>
          <div style={{ fontFamily: 'IBM Plex Mono', marginBottom: 2 }}>v1.0.0</div>
          <div>Spring Boot + MySQL</div>
        </div>
      </aside>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
