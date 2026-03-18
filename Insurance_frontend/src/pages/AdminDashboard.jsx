import { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/agent.css";

export default function AdminDashboard() {
  const [kpis, setKpis] = useState({
    revenueThisMonth: 0,
    conversionRate: 0,
    cancellationRate: 0,
    agentProductivity: 0,
  });
  const [stats, setStats] = useState({
    totalCustomers: 0,
    totalAgents: 0,
    activePolicies: 0,
    totalAppointments: 0,
    completedAppointments: 0,
    pendingAppointments: 0,
  });

  useEffect(() => {
    const token = localStorage.getItem("token");
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    axios
      .get("http://localhost:8080/api/admin/dashboard/kpis", { headers })
      .then((res) => setKpis(res.data || kpis))
      .catch(() => {});

    axios
      .get("http://localhost:8080/api/admin/dashboard/stats", { headers })
      .then((res) => setStats(res.data || stats))
      .catch(() => {});
  }, []);

  return (
    <div className="agent-page">
      <Navbar />
      <main className="agent-main">
        <div className="agent-header">
          <h1>Admin Dashboard</h1>
          <p>Key performance indicators across policies, revenue, and agents.</p>
        </div>

        <div className="policy-grid" style={{ marginBottom: "24px" }}>
          <div className="policy-card">
            <div className="policy-category">Total Customers</div>
            <h3>{stats.totalCustomers}</h3>
          </div>
          <div className="policy-card">
            <div className="policy-category">Total Agents</div>
            <h3>{stats.totalAgents}</h3>
          </div>
          <div className="policy-card">
            <div className="policy-category">Active Policies</div>
            <h3>{stats.activePolicies}</h3>
          </div>
          <div className="policy-card">
            <div className="policy-category">Total Appointments</div>
            <h3>{stats.totalAppointments}</h3>
          </div>
          <div className="policy-card">
            <div className="policy-category">Completed Appointments</div>
            <h3>{stats.completedAppointments}</h3>
          </div>
          <div className="policy-card">
            <div className="policy-category">Pending Appointments</div>
            <h3>{stats.pendingAppointments}</h3>
          </div>
        </div>

        <div className="policy-grid">
          <div className="policy-card">
            <div className="policy-category">Revenue This Month</div>
            <h3>₹{kpis.revenueThisMonth}</h3>
            <p className="policy-coverage">Total successful payments</p>
          </div>
          <div className="policy-card">
            <div className="policy-category">Conversion Rate</div>
            <h3>{kpis.conversionRate}%</h3>
            <p className="policy-coverage">Policies purchased vs viewed</p>
          </div>
          <div className="policy-card">
            <div className="policy-category">Cancellation Rate</div>
            <h3>{kpis.cancellationRate}%</h3>
            <p className="policy-coverage">Cancelled appointments ratio</p>
          </div>
          <div className="policy-card">
            <div className="policy-category">Agent Productivity</div>
            <h3>{kpis.agentProductivity}%</h3>
            <p className="policy-coverage">Completed vs assigned</p>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
