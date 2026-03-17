export default function CustomerAppointmentsList({ appointments }) {
  if (!appointments || appointments.length === 0) {
    return <p style={{ color: "#64748b" }}>No appointments booked yet.</p>;
  }

  return (
    <table className="appointment-table">
      <thead>
        <tr>
          <th>Agent Name</th>
          <th>Date</th>
          <th>Time</th>
          <th>Pincode</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        {appointments.map((appointment) => (
          <tr key={appointment.id}>
            <td>{appointment.agentName}</td>
            <td>{appointment.appointmentDate || "N/A"}</td>
            <td>{appointment.appointmentTime || "N/A"}</td>
            <td>{appointment.pincode || "N/A"}</td>
            <td>
              <span className="status-pill">{appointment.status}</span>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
