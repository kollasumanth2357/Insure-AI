import { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/agent.css";

export default function AgentDashboard() {
  const [activeTab, setActiveTab] = useState("policies");
  const [policies, setPolicies] = useState([]);
  const [appointments, setAppointments] = useState([]);

  useEffect(() => {
    const token = localStorage.getItem("token");

    const headers = {
      Authorization: `Bearer ${token}`,
    };

    axios
      .get("http://localhost:8080/api/agent/policies", { headers })
      .then((res) => setPolicies(res.data || []))
      .catch(() => setPolicies([]));

    axios
      .get("http://localhost:8080/api/agent/appointments", { headers })
      .then((res) => setAppointments(res.data || []))
      .catch(() => setAppointments([]));
  }, []);

  const categories = [
    "Online Insurance",
    "Health Insurance",
    "Vehicle Insurance",
    "Home Insurance",
    "Business Insurance",
    "Life Insurance",
  ];

  return (
    <div className="agent-page">
      <Navbar />

      <main className="agent-main">
        <div className="agent-header">
          <h1>Agent Workspace</h1>
          <p>
            View active policies and manage customer appointments assigned to
            you.
          </p>

          <div className="agent-tabs">
            <button
              className={`agent-tab ${
                activeTab === "policies" ? "active" : ""
              }`}
              onClick={() => setActiveTab("policies")}
            >
              Policies
            </button>
            <button
              className={`agent-tab ${
                activeTab === "appointments" ? "active" : ""
              }`}
              onClick={() => setActiveTab("appointments")}
            >
              Appointments
            </button>
          </div>
        </div>

        {activeTab === "policies" && (
          <section>
            {categories.map((cat) => {
              const list = policies.filter((p) => p.mainCategory === cat);
              if (list.length === 0) return null;

              return (
                <div key={cat} style={{ marginBottom: "26px" }}>
                  <h2 style={{ marginBottom: "10px", fontSize: "18px" }}>
                    {cat} Policies
                  </h2>
                  <div className="policy-grid">
                    {list.map((p) => (
                    <div key={p.id} className="policy-card">
                      <div className="policy-category">{p.mainCategory}</div>
                      <h3>{p.name}</h3>
                      <p className="policy-coverage">{p.description}</p>
                      <p className="policy-premium">Premium: ₹{p.premiumAmount}</p>
                    </div>
                  ))}
                </div>
              </div>
            );
            })}
          </section>
        )}

        {activeTab === "appointments" && (
          <section>
            <h2 style={{ marginBottom: "12px", fontSize: "18px" }}>
              Upcoming Customer Appointments
            </h2>
            {appointments.length === 0 ? (
              <p style={{ color: "#9ca3af" }}>No appointments assigned yet.</p>
            ) : (
              <table className="appointments-table">
                <thead>
                  <tr>
                    <th>Customer</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Date &amp; Time</th>
                    <th>Notes</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map((a) => (
                    <tr key={a.id}>
                      <td>{a.customerName}</td>
                      <td>{a.customerEmail}</td>
                      <td>{a.customerPhone}</td>
                      <td>{new Date(a.appointmentTime).toLocaleString()}</td>
                      <td>{a.notes || "N/A"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </section>
        )}
      </main>

      <Footer />
    </div>
  );
}
