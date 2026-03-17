export default function PlanCard({ policy, onApply }) {
  const label = policy?.mainCategory || policy?.name || "Plan";

  const renderIcon = () => {
    const key = label.toLowerCase();
    if (key.includes("life")) {
      return (
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path
            d="M12 21c-3.6-3.2-6.9-5.9-8.4-8.5-1.3-2.3-.4-5 1.8-6.2 1.7-.9 3.8-.4 5 1.1 1.2-1.5 3.4-2 5-1.1 2.2 1.2 3.1 3.9 1.8 6.2-1.5 2.6-4.8 5.3-8.2 8.5z"
            fill="currentColor"
          />
        </svg>
      );
    }
    if (key.includes("health") || key.includes("medical")) {
      return (
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path
            d="M10 4h4v6h6v4h-6v6h-4v-6H4v-4h6z"
            fill="currentColor"
          />
        </svg>
      );
    }
    if (key.includes("vehicle") || key.includes("car") || key.includes("bike")) {
      return (
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path
            d="M5 13l2-5h10l2 5v5h-2a2 2 0 01-4 0H9a2 2 0 01-4 0H3v-5h2zm3.5 4a1 1 0 100-2 1 1 0 000 2zm9 0a1 1 0 100-2 1 1 0 000 2z"
            fill="currentColor"
          />
        </svg>
      );
    }
    if (key.includes("travel")) {
      return (
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path
            d="M2 12l20-6-6 20-3.5-7L2 12z"
            fill="currentColor"
          />
        </svg>
      );
    }
    return (
      <svg viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="12" cy="12" r="10" fill="currentColor" />
      </svg>
    );
  };

  return (
    <div className="plan-card">
      <div className="plan-card-header">
        <div className="plan-icon">{renderIcon()}</div>
        <span className="plan-tag">{policy?.mainCategory || "Insurance"}</span>
      </div>
      <h3>{policy?.name}</h3>
      <p className="plan-description">
        {policy?.description || "Comprehensive coverage tailored for your needs."}
      </p>
      <div className="plan-metrics">
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
      </div>
      <button
        style={{
          backgroundColor: "#d4af37",
          color: "white",
          padding: "8px 16px",
          border: "none",
          borderRadius: "6px",
          cursor: "pointer",
          marginTop: "10px",
        }}
        onClick={() => {
          console.log("Apply clicked", policy);
          alert("Apply clicked for " + (policy.name || "policy"));
        }}
      >
        Apply Policy
      </button>
    </div>
  );
}
