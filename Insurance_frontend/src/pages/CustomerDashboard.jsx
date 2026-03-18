import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import CustomerAppointmentsList from "../components/CustomerAppointmentsList";
import "../styles/appointments-new.css";

export default function CustomerDashboard() {

  const [message, setMessage] = useState("");
  const [appointments, setAppointments] = useState([]);
  const [policies, setPolicies] = useState([]);
  const [policyCatalog, setPolicyCatalog] = useState([]);
  const [selectedPolicyId, setSelectedPolicyId] = useState(null);
  const [customerId, setCustomerId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    api.get("/api/customers/dashboard")
      .then((res) => setMessage(res.data))
      .catch(() => navigate("/login"));

    api.get("/api/profile")
      .then((res) => setCustomerId(res.data?.id ?? null))
      .catch(() => {});
  }, []);

  useEffect(() => {
    if (!customerId) {
      return;
    }
    api
      .get(`/api/customer/appointments/${customerId}`)
      .then((res) => setAppointments(res.data || []))
      .catch(() => setAppointments([]));

    api
      .get("/api/customers/policies/purchased")
      .then((res) => setPolicies(Array.isArray(res.data) ? res.data : []))
      .catch(() => setPolicies([]));

    api
      .get("/api/policies")
      .then((res) => setPolicyCatalog(Array.isArray(res.data) ? res.data : []))
      .catch(() => setPolicyCatalog([]));
  }, [customerId]);

  const handleDownloadPolicy = (policy, policyMeta) => {
    const payload = [
      "Policy Summary",
      `Policy Name: ${policy?.policyName || policyMeta?.name || "N/A"}`,
      `Coverage: ${policyMeta?.coverageAmount != null ? `₹${policyMeta.coverageAmount}` : "N/A"}`,
      `Premium: ${policyMeta?.premiumAmount != null ? `₹${policyMeta.premiumAmount}` : "N/A"}`,
      `Status: ${policy?.status || "ACTIVE"}`,
    ].join("\n");
    const blob = new Blob([payload], { type: "text/plain" });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "policy-summary.txt";
    link.click();
    window.URL.revokeObjectURL(url);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    navigate("/login");
  };

  return (
    <div className="appointments-page" style={{ padding: "24px" }}>
      <h1>{message}</h1>
      <div className="appointments-card" style={{ marginTop: "16px" }}>
        <h2>My Appointments</h2>
        <CustomerAppointmentsList appointments={appointments} />
      </div>
      <div className="appointments-card" style={{ marginTop: "16px" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h2>My Policies</h2>
          <button onClick={() => navigate("/customer/plans")}>Browse Plans</button>
        </div>
        {policies.length === 0 ? (
          <p style={{ marginTop: "12px", color: "#64748b" }}>No policies purchased yet.</p>
        ) : (
          <table className="appointments-table" style={{ marginTop: "12px" }}>
            <thead>
              <tr>
                <th>Policy Name</th>
                <th>Coverage</th>
                <th>Premium</th>
                <th>Start Date</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {policies.map((policy) => {
                const policyMeta = policyCatalog.find(
                  (item) => String(item.id) === String(policy.policyId)
                );
                const startDate = policy.purchaseDate
                  ? new Date(policy.purchaseDate).toLocaleDateString()
                  : "N/A";
                return (
                  <tr key={policy.id}>
                    <td>{policy.policyName || policyMeta?.name || "N/A"}</td>
                    <td>
                      {policyMeta?.coverageAmount != null
                        ? `₹${policyMeta.coverageAmount}`
                        : "N/A"}
                    </td>
                    <td>
                      {policyMeta?.premiumAmount != null
                        ? `₹${policyMeta.premiumAmount}`
                        : "N/A"}
                    </td>
                    <td>{startDate}</td>
                    <td>{policy.status}</td>
                    <td>
                      <button type="button" onClick={() => setSelectedPolicyId(policy.id)}>
                        View Policy
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDownloadPolicy(policy, policyMeta)}
                      >
                        Download Policy
                      </button>
                      <button
                        type="button"
                        onClick={() => navigate(`/customer/apply/${policy.policyId}`)}
                      >
                        Renew Policy
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}

        {selectedPolicyId && (
          <div style={{ marginTop: "16px" }}>
            {(() => {
              const policy = policies.find((item) => item.id === selectedPolicyId);
              const policyMeta = policyCatalog.find(
                (item) => String(item.id) === String(policy?.policyId)
              );
              return (
                <div>
                  <h3>Policy Details</h3>
                  <p>Policy: {policy?.policyName || policyMeta?.name || "N/A"}</p>
                  <p>
                    Coverage:{" "}
                    {policyMeta?.coverageAmount != null
                      ? `₹${policyMeta.coverageAmount}`
                      : "N/A"}
                  </p>
                  <p>
                    Premium:{" "}
                    {policyMeta?.premiumAmount != null
                      ? `₹${policyMeta.premiumAmount}`
                      : "N/A"}
                  </p>
                  <button type="button" onClick={() => setSelectedPolicyId(null)}>
                    Close
                  </button>
                </div>
              );
            })()}
          </div>
        )}
      </div>
      <button onClick={handleLogout} style={{ marginTop: "16px" }}>
        Logout
      </button>
    </div>
  );
}
