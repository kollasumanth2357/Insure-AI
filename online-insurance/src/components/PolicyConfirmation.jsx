export default function PolicyConfirmation({ policy, purchase, dates, onViewPolicies }) {
  const start = dates?.start ? new Date(dates.start).toLocaleDateString() : "N/A";
  const end = dates?.end ? new Date(dates.end).toLocaleDateString() : "N/A";

  const handleDownload = () => {
    const payload = [
      "Policy Purchased Successfully",
      `Policy Name: ${policy?.name || "N/A"}`,
      `Coverage: ${policy?.coverageAmount != null ? `₹${policy.coverageAmount}` : "N/A"}`,
      `Premium: ${policy?.premiumAmount != null ? `₹${policy.premiumAmount}` : "N/A"}`,
      `Start Date: ${start}`,
      `End Date: ${end}`,
      `Status: ${purchase?.status || "ACTIVE"}`,
    ].join("\n");
    const blob = new Blob([payload], { type: "text/plain" });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "policy-confirmation.txt";
    link.click();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div className="step-content">
      <h2>Policy Purchased Successfully</h2>
      <p className="step-subtitle">
        Your policy is now active. Keep this confirmation for your records.
      </p>

      <div className="confirmation-card">
        <div>
          <span>Policy Name</span>
          <strong>{policy?.name || "N/A"}</strong>
        </div>
        <div>
          <span>Coverage</span>
          <strong>
            {policy?.coverageAmount != null ? `₹${policy.coverageAmount}` : "N/A"}
          </strong>
        </div>
        <div>
          <span>Premium</span>
          <strong>
            {policy?.premiumAmount != null ? `₹${policy.premiumAmount}` : "N/A"}
          </strong>
        </div>
        <div>
          <span>Start Date</span>
          <strong>{start}</strong>
        </div>
        <div>
          <span>End Date</span>
          <strong>{end}</strong>
        </div>
      </div>

      <div className="step-actions">
        <button className="ghost-btn" type="button" onClick={handleDownload}>
          Download Policy
        </button>
        <button className="primary-btn" type="button" onClick={onViewPolicies}>
          View Policies
        </button>
      </div>
    </div>
  );
}
