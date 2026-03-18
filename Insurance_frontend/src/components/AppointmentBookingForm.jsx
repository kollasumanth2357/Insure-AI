import { useState } from "react";

export default function AppointmentBookingForm({
  customerId,
  pincode,
  onPincodeChange,
  agents,
  selectedAgent,
  onSelectAgent,
  onBook,
  isBooking,
}) {
  const [form, setForm] = useState({
    appointmentDate: "",
    appointmentTime: "",
    notes: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!customerId) {
      alert("Customer profile not loaded.");
      return;
    }
    if (!pincode || pincode.length < 6) {
      alert("Please enter a valid pincode.");
      return;
    }
    if (!selectedAgent?.id) {
      alert("Please select an agent.");
      return;
    }
    if (!form.appointmentDate || !form.appointmentTime) {
      alert("Please select appointment date and time.");
      return;
    }
    onBook({
      customerId,
      agentId: selectedAgent.id,
      appointmentDate: form.appointmentDate,
      appointmentTime: form.appointmentTime,
      pincode,
      notes: form.notes,
    });
  };

  return (
    <form className="appointments-form" onSubmit={handleSubmit}>
      <div>
        <label>Pincode</label>
        <input
          type="text"
          name="pincode"
          value={pincode}
          onChange={(e) => onPincodeChange?.(e.target.value)}
          placeholder="Enter pincode"
        />
      </div>
      <div>
        <label>Agent</label>
        <select
          value={selectedAgent?.id || ""}
          onChange={(e) => {
            const agent = agents.find((item) => String(item.id) === e.target.value);
            onSelectAgent?.(agent || null);
          }}
        >
          <option value="">Select an agent</option>
          {agents.map((agent) => (
            <option key={agent.id} value={agent.id}>
              {agent.name} ({agent.pincode})
            </option>
          ))}
        </select>
      </div>
      <div>
        <label>Appointment Date</label>
        <input
          type="date"
          name="appointmentDate"
          value={form.appointmentDate}
          onChange={handleChange}
        />
      </div>
      <div>
        <label>Appointment Time</label>
        <input
          type="time"
          name="appointmentTime"
          value={form.appointmentTime}
          onChange={handleChange}
        />
      </div>
      <div>
        <label>Notes</label>
        <textarea
          name="notes"
          rows={3}
          value={form.notes}
          onChange={handleChange}
        />
      </div>
      <button type="submit" disabled={isBooking}>
        {isBooking ? "Booking..." : "Book Appointment"}
      </button>
    </form>
  );
}
