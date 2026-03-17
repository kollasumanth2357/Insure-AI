import { useState } from "react";
import api from "../api/api";

const STATUS_OPTIONS = ["CONFIRMED", "COMPLETED", "CANCELLED"];

export default function UpdateAppointmentStatus({ appointmentId, currentStatus, onUpdated }) {
  const [nextStatus, setNextStatus] = useState(
    STATUS_OPTIONS.includes(currentStatus) ? currentStatus : "CONFIRMED"
  );
  const [isSaving, setIsSaving] = useState(false);
  const isLocked = currentStatus === "COMPLETED" || currentStatus === "CANCELLED";

  const handleUpdate = () => {
    setIsSaving(true);
    api
      .put(`/api/agent/appointments/${appointmentId}/status`, { status: nextStatus })
      .then(() => onUpdated?.())
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to update status");
      })
      .finally(() => setIsSaving(false));
  };

  return (
    <div className="status-update">
      <select
        value={nextStatus}
        onChange={(e) => setNextStatus(e.target.value)}
        disabled={isLocked}
      >
        {STATUS_OPTIONS.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
      <button type="button" onClick={handleUpdate} disabled={isSaving || isLocked}>
        Update
      </button>
    </div>
  );
}
