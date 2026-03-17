import { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/agent.css";

export default function Appointments() {
  const [appointments, setAppointments] = useState([]);
  const [locationModal, setLocationModal] = useState(null);
  const role = localStorage.getItem("role");

  const fetchAppointments = () => {
    const token = localStorage.getItem("token");
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    axios
      .get(
        role === "AGENT"
          ? "http://localhost:8080/api/agent/appointments"
          : role === "ADMIN"
            ? "http://localhost:8080/api/admin/appointments"
            : "http://localhost:8080/api/customers/appointments",
        { headers }
      )
      .then((res) => setAppointments(res.data || []))
      .catch(() => setAppointments([]));
  };

  useEffect(() => {
    fetchAppointments();
  }, [role]);

  const handleStatusUpdate = (id, action) => {
    const token = localStorage.getItem("token");
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    axios
      .put(`http://localhost:8080/api/agent/appointments/${id}/${action}`, {}, { headers })
      .then(() => fetchAppointments())
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to update appointment");
      });
  };

  return (
    <div className="agent-page">
      <Navbar />

      <main className="agent-main">
        <div className="agent-header">
          <h1>Appointments</h1>
          <p>
            {role === "AGENT"
              ? "Customers who have selected you for more details and policy clarifications will appear here."
              : role === "ADMIN"
                ? "Monitor all appointments booked across customers and agents."
                : "Track your scheduled appointments with agents and policy discussions here."}
          </p>
        </div>

        {appointments.length === 0 ? (
          <p style={{ color: "#9ca3af" }}>No appointments assigned yet.</p>
        ) : (
          <table className="appointments-table">
            <thead>
              <tr>
                {role === "AGENT" ? (
                  <>
                    <th>Customer</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Date &amp; Time</th>
                    <th>Notes</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </>
                ) : role === "ADMIN" ? (
                  <>
                    <th>Customer</th>
                    <th>Agent</th>
                    <th>Date &amp; Time</th>
                    <th>Status</th>
                  </>
                ) : (
                  <>
                    <th>Agent Name</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Pincode</th>
                    <th>Status</th>
                  </>
                )}
              </tr>
            </thead>
            <tbody>
              {appointments.map((a) => (
                <tr key={a.id}>
                  {role === "AGENT" ? (
                    <>
                      <td>{a.customerName}</td>
                      <td>{a.customerEmail}</td>
                      <td>{a.customerPhone}</td>
                      <td>{new Date(a.appointmentTime).toLocaleString()}</td>
                      <td>{a.notes || "N/A"}</td>
                      <td>
                        <span className="status-pill">{a.status}</span>
                      </td>
                      <td>
                        <button
                          type="button"
                          className="security-btn"
                          onClick={() =>
                            setLocationModal({
                              latitude: a.customerLatitude,
                              longitude: a.customerLongitude,
                              name: a.customerName,
                            })
                          }
                        >
                          View Location
                        </button>
                        {(a.status ?? "").toUpperCase() !== "COMPLETED" && (
                          <>
                            <button
                              type="button"
                              className="success-btn"
                              onClick={() => handleStatusUpdate(a.id, "start")}
                              disabled={a.status !== "PENDING"}
                              style={{ marginLeft: "8px" }}
                            >
                              Start Meeting
                            </button>
                            <button
                              type="button"
                              className="danger-btn"
                              onClick={() => handleStatusUpdate(a.id, "complete")}
                              disabled={a.status === "COMPLETED"}
                              style={{ marginLeft: "8px" }}
                            >
                              Mark Completed
                            </button>
                          </>
                        )}
                      </td>
                    </>
                  ) : role === "ADMIN" ? (
                    <>
                      <td>{a.customerName}</td>
                      <td>{a.agentName}</td>
                      <td>{new Date(a.appointmentTime).toLocaleString()}</td>
                      <td>
                        <span className="status-pill">{a.status}</span>
                      </td>
                    </>
                  ) : (
                    <>
                      <td>{a.agentName}</td>
                      <td>
                        {a.appointmentTime
                          ? new Date(a.appointmentTime).toLocaleDateString()
                          : "N/A"}
                      </td>
                      <td>
                        {a.appointmentTime
                          ? new Date(a.appointmentTime).toLocaleTimeString([], {
                              hour: "2-digit",
                              minute: "2-digit",
                            })
                          : "N/A"}
                      </td>
                      <td>{a.pincode || "N/A"}</td>
                      <td>
                        <span className="status-pill">{a.status}</span>
                      </td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>

      <Footer />

      {locationModal && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(15, 23, 42, 0.7)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 50,
          }}
        >
          <div
            style={{
              background: "#0f172a",
              borderRadius: "12px",
              padding: "20px",
              width: "90%",
              maxWidth: "640px",
            }}
          >
            <h3 style={{ marginBottom: "12px" }}>
              Customer Location {locationModal.name ? `- ${locationModal.name}` : ""}
            </h3>
            {locationModal.latitude != null && locationModal.longitude != null ? (
              <>
                <iframe
                  title="Customer Location"
                  width="100%"
                  height="320"
                  style={{ border: 0, borderRadius: "10px" }}
                  loading="lazy"
                  src={`https://maps.google.com/maps?q=${locationModal.latitude},${locationModal.longitude}&z=15&output=embed`}
                />
                <div style={{ marginTop: "12px", display: "flex", gap: "10px" }}>
                  <button
                    type="button"
                    className="security-btn"
                    onClick={() =>
                      window.open(
                        `https://www.google.com/maps/dir/?api=1&destination=${locationModal.latitude},${locationModal.longitude}`,
                        "_blank"
                      )
                    }
                  >
                    Navigate
                  </button>
                  <button
                    type="button"
                    className="danger-btn"
                    onClick={() => setLocationModal(null)}
                  >
                    Close
                  </button>
                </div>
              </>
            ) : (
              <>
                <p>Customer location not available.</p>
                <button
                  type="button"
                  className="danger-btn"
                  onClick={() => setLocationModal(null)}
                >
                  Close
                </button>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

