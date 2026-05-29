import React, { useEffect, useState, useRef } from 'react';
import { etlApi } from '../services/api';
import { Play, RefreshCw, Clock, CheckCircle, XCircle, AlertCircle, Database, Layers, ArrowRight } from 'lucide-react';

function StatusIcon({ status }) {
  if (status === 'SUCCESS') return <CheckCircle size={14} style={{ color: 'var(--green)' }} />;
  if (status === 'FAILED') return <XCircle size={14} style={{ color: 'var(--red)' }} />;
  if (status === 'RUNNING') return <div className="spinner" style={{ width: 14, height: 14 }} />;
  return <AlertCircle size={14} style={{ color: 'var(--text-muted)' }} />;
}

function fmtDuration(secs) {
  if (!secs) return '—';
  if (secs < 60) return `${secs}s`;
  return `${Math.floor(secs/60)}m ${secs%60}s`;
}

function fmtDt(dt) {
  if (!dt) return '—';
  return new Date(dt).toLocaleString();
}

const PIPELINE_STEPS = [
  { id: 1, name: 'Extract OLTP', desc: 'Read orders, inventory logs from source MySQL DB', icon: Database },
  { id: 2, name: 'Load dim_date', desc: 'Populate date dimension (2yr history + 1yr future)', icon: Clock },
  { id: 3, name: 'Load Dimensions', desc: 'SCD Type 2 load for stores, products, suppliers, customers', icon: Layers },
  { id: 4, name: 'Load fact_sales', desc: 'Transform order items into star schema facts with revenue metrics', icon: ArrowRight },
  { id: 5, name: 'Load fact_inventory', desc: 'Transform inventory movements with reorder flag logic', icon: ArrowRight },
];

export default function EtlPage() {
  const [history, setHistory] = useState([]);
  const [triggering, setTriggering] = useState(false);
  const [loading, setLoading] = useState(true);
  const [activeStep, setActiveStep] = useState(null);
  const pollRef = useRef(null);

  useEffect(() => { loadHistory(); return () => clearInterval(pollRef.current); }, []);

  async function loadHistory() {
    setLoading(true);
    try {
      const res = await etlApi.getHistory();
      setHistory(res.data || []);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  }

  async function triggerEtl() {
    setTriggering(true);
    setActiveStep(1);
    try {
      await etlApi.trigger('MANUAL_UI');

      // Animate through steps
      let step = 1;
      const interval = setInterval(() => {
        step++;
        if (step <= PIPELINE_STEPS.length) {
          setActiveStep(step);
        } else {
          clearInterval(interval);
          setActiveStep(null);
          setTriggering(false);
          loadHistory();
        }
      }, 1800);
      pollRef.current = interval;
    } catch (e) {
      setTriggering(false);
      setActiveStep(null);
    }
  }

  const latest = history[0];

  return (
    <div>
      <div className="topbar">
        <div>
          <h2>ETL Pipeline</h2>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
            Extract → Transform → Load — OLTP to Data Warehouse
          </div>
        </div>
        <button className="btn btn-primary" onClick={triggerEtl} disabled={triggering}>
          {triggering ? <><div className="spinner" style={{width:13,height:13}} /> Running Pipeline...</> : <><Play size={13} /> Run ETL Now</>}
        </button>
      </div>

      <div className="page-content">
        {/* Pipeline Visualization */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-title">Pipeline Architecture</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 0, overflowX: 'auto', paddingBottom: 4 }}>
            {/* Source */}
            <div style={{ textAlign: 'center', minWidth: 110 }}>
              <div style={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 8, padding: '12px 16px' }}>
                <Database size={20} style={{ color: 'var(--yellow)', margin: '0 auto 6px' }} />
                <div style={{ fontSize: 12, fontWeight: 600 }}>OLTP Source</div>
                <div style={{ fontSize: 10, color: 'var(--text-muted)', marginTop: 2 }}>retailvault_oltp</div>
                <div style={{ fontSize: 10, color: 'var(--text-muted)' }}>MySQL</div>
              </div>
              <div style={{ marginTop: 6, fontSize: 10, color: 'var(--text-muted)' }}>orders, inventory_log</div>
            </div>

            <ArrowRight size={20} style={{ color: 'var(--border)', flexShrink: 0, margin: '0 8px' }} />

            {/* ETL Steps */}
            {PIPELINE_STEPS.slice(0, 3).map((step, i) => {
              const isActive = activeStep === step.id;
              const isDone = activeStep > step.id;
              return (
                <React.Fragment key={step.id}>
                  <div style={{ textAlign: 'center', minWidth: 120 }}>
                    <div style={{
                      background: isActive ? 'var(--accent-glow)' : isDone ? 'rgba(16,185,129,0.1)' : 'var(--surface2)',
                      border: `1px solid ${isActive ? 'var(--accent)' : isDone ? 'var(--green)' : 'var(--border)'}`,
                      borderRadius: 8, padding: '12px 10px',
                      transition: 'all 0.3s'
                    }}>
                      {isActive ? <div className="spinner" style={{ width: 18, height: 18, margin: '0 auto 6px' }} /> :
                        isDone ? <CheckCircle size={18} style={{ color: 'var(--green)', margin: '0 auto 6px' }} /> :
                        <step.icon size={18} style={{ color: 'var(--text-muted)', margin: '0 auto 6px' }} />}
                      <div style={{ fontSize: 11, fontWeight: 600, color: isActive ? 'var(--accent)' : 'inherit' }}>{step.name}</div>
                    </div>
                    <div style={{ marginTop: 6, fontSize: 10, color: 'var(--text-muted)', maxWidth: 110 }}>{step.desc}</div>
                  </div>
                  {i < 2 && <ArrowRight size={16} style={{ color: 'var(--border)', flexShrink: 0, margin: '0 4px' }} />}
                </React.Fragment>
              );
            })}

            <ArrowRight size={20} style={{ color: 'var(--border)', flexShrink: 0, margin: '0 8px' }} />

            {/* Warehouse */}
            <div style={{ textAlign: 'center', minWidth: 120 }}>
              <div style={{ background: 'var(--surface2)', border: '1px solid var(--border)', borderRadius: 8, padding: '12px 14px' }}>
                <Database size={20} style={{ color: 'var(--accent)', margin: '0 auto 6px' }} />
                <div style={{ fontSize: 12, fontWeight: 600 }}>Warehouse</div>
                <div style={{ fontSize: 10, color: 'var(--text-muted)', marginTop: 2 }}>retailvault_wh</div>
                <div style={{ fontSize: 10, color: 'var(--text-muted)' }}>Star Schema</div>
              </div>
              <div style={{ marginTop: 6, fontSize: 10, color: 'var(--text-muted)' }}>fact_sales, fact_inventory</div>
            </div>
          </div>
        </div>

        {/* Latest Run Status */}
        {latest && (
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-title">Latest Run</div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: 16 }}>
              <div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Status</div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <StatusIcon status={latest.status} />
                  <span className={`badge badge-${latest.status?.toLowerCase() === 'success' ? 'success' : latest.status?.toLowerCase() === 'failed' ? 'error' : 'running'}`}>
                    {latest.status}
                  </span>
                </div>
              </div>
              <div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Started</div>
                <div style={{ fontSize: 12 }}>{fmtDt(latest.startedAt)}</div>
              </div>
              <div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Duration</div>
                <div className="mono">{fmtDuration(latest.durationSeconds)}</div>
              </div>
              <div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Rows Extracted</div>
                <div className="mono">{latest.rowsExtracted?.toLocaleString()}</div>
              </div>
              <div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Rows Loaded</div>
                <div className="mono num-positive">{latest.rowsLoaded?.toLocaleString()}</div>
              </div>
            </div>
            {latest.errorMessage && (
              <div style={{ marginTop: 12, padding: '10px 14px', background: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.3)', borderRadius: 6, fontSize: 12, color: 'var(--red)', fontFamily: 'IBM Plex Mono' }}>
                {latest.errorMessage}
              </div>
            )}
          </div>
        )}

        {/* Schedule Info */}
        <div className="card" style={{ marginBottom: 16 }}>
          <div className="card-title">Schedule Configuration</div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16 }}>
            <div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Cron Expression</div>
              <div className="mono" style={{ color: 'var(--accent)' }}>0 0 2 * * *</div>
            </div>
            <div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Schedule</div>
              <div style={{ fontSize: 13 }}>Every day at 2:00 AM</div>
            </div>
            <div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 4 }}>Mode</div>
              <span className="badge badge-success">Active</span>
            </div>
          </div>
        </div>

        {/* History Table */}
        <div className="card">
          <div className="card-title" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span>Run History</span>
            <button className="btn btn-outline" onClick={loadHistory} style={{ padding: '4px 10px', fontSize: 11 }}>
              <RefreshCw size={11} /> Refresh
            </button>
          </div>
          {loading ? (
            <div className="loading"><div className="spinner" /></div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Run ID</th>
                  <th>Job</th>
                  <th>Status</th>
                  <th>Started</th>
                  <th>Duration</th>
                  <th>Extracted</th>
                  <th>Loaded</th>
                  <th>Triggered By</th>
                </tr>
              </thead>
              <tbody>
                {history.length === 0 ? (
                  <tr><td colSpan={8} style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '24px 0' }}>No runs yet. Click "Run ETL Now" to start.</td></tr>
                ) : history.map((run) => (
                  <tr key={run.runId}>
                    <td className="mono" style={{ color: 'var(--text-muted)' }}>#{run.runId}</td>
                    <td>{run.jobName}</td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
                        <StatusIcon status={run.status} />
                        <span className={`badge badge-${run.status?.toLowerCase() === 'success' ? 'success' : run.status?.toLowerCase() === 'failed' ? 'error' : 'running'}`}>
                          {run.status}
                        </span>
                      </div>
                    </td>
                    <td style={{ fontSize: 12 }}>{fmtDt(run.startedAt)}</td>
                    <td className="mono">{fmtDuration(run.durationSeconds)}</td>
                    <td className="mono">{run.rowsExtracted?.toLocaleString()}</td>
                    <td className="mono num-positive">{run.rowsLoaded?.toLocaleString()}</td>
                    <td><span className="badge" style={{ background: 'var(--surface2)', color: 'var(--text-muted)' }}>{run.triggeredBy}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
