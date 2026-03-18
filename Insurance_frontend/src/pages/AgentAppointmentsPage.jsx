import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import api from "../api/api";
import UpdateAppointmentStatus from "../components/UpdateAppointmentStatus";
import "../styles/appointments-new.css";

export default function AgentAppointmentsPage() {
  const [agentId, setAgentId] = useState(null);
  const [appointments, setAppointments] = useState([]);

  useEffect(() => {
    api
      .get("/api/profile")
      .then((res) => setAgentId(res.data?.id ?? null))
      .catch(() => {});
  }, []);

  const fetchAppointments = () => {
    if (!agentId) {
      return;
    }
    api
      .get(`/api/agent/appointments/${agentId}`)
      .then((res) => setAppointments(res.data || []))
      .catch(() => setAppointments([]));
  };

  useEffect(() => {
    fetchAppointments();
  }, [agentId]);

  return (
    <div className="appointments-page">
      <Navbar />
      <div className="agent-appointments">
        <h1>My Appointments</h1>
        {appointments.length === 0 ? (
          <p style={{ color: "#64748b" }}>No appointments assigned yet.</p>
        ) : (
          <table className="appointment-table">
            <thead>
              <tr>
                <th>Customer</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Date</th>
                <th>Time</th>
                <th>Pincode</th>
                <th>Status</th>
                <th>Update</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((appointment) => (
                <tr key={appointment.id}>
                  <td>{appointment.customerName}</td>
                  <td>{appointment.customerEmail}</td>
                  <td>{appointment.customerPhone}</td>
                  <td>{appointment.appointmentDate || "N/A"}</td>
                  <td>{appointment.appointmentTime || "N/A"}</td>
                  <td>{appointment.pincode || "N/A"}</td>
                  <td>
                    <span className="status-pill">{appointment.status}</span>
                  </td>
                  <td>
                    <UpdateAppointmentStatus
                      appointmentId={appointment.id}
                      currentStatus={appointment.status}
                      onUpdated={fetchAppointments}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
      <Footer />
    </div>
  );
}
