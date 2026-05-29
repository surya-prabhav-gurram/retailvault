import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import Dashboard from './pages/Dashboard';
import SalesPage from './pages/SalesPage';
import InventoryPage from './pages/InventoryPage';
import EtlPage from './pages/EtlPage';
import './index.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="sales" element={<SalesPage />} />
          <Route path="inventory" element={<InventoryPage />} />
          <Route path="etl" element={<EtlPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
