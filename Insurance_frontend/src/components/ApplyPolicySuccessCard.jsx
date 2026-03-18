export default function ApplyPolicySuccessCard({ receipt, onDone }) {
  return (
    <div className="apply-flow-panel">
      <div className="apply-flow-success-card">
        <h2>Policy Application Successful</h2>
        <p>Your payment is complete and your receipt is ready.</p>

        <div className="confirmation-card">
          <div>
            <span>Receipt Number</span>
            <strong>{receipt?.receiptNumber || "Pending"}</strong>
          </div>
          <div>
            <span>Plan Name</span>
            <strong>{receipt?.policyName || "N/A"}</strong>
          </div>
          <div>
            <span>Amount Paid</span>
            <strong>{receipt?.amount != null ? `Rs. ${receipt.amount}` : "N/A"}</strong>
          </div>
          <div>
            <span>Payment Method</span>
            <strong>{receipt?.paymentMethod || "N/A"}</strong>
          </div>
        </div>

        <p className="apply-flow-note">Receipt generation is available in UI for this flow.</p>

        <div className="step-actions">
          <button className="primary-btn" type="button" onClick={onDone}>
            View Dashboard
          </button>
        </div>
      </div>
    </div>
  );
}
