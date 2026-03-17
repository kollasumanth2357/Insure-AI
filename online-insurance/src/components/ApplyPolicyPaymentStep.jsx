const paymentOptions = [
  { value: "UPI", label: "UPI" },
  { value: "DEBIT_CARD", label: "Debit Card" },
  { value: "CREDIT_CARD", label: "Credit Card" },
  { value: "NET_BANKING", label: "Net Banking" },
];

export default function ApplyPolicyPaymentStep({
  policy,
  selectedDocuments,
  paymentMethod,
  onPaymentMethodChange,
  onBack,
  onPay,
  paying,
  stepError,
}) {
  const selectedList = Object.values(selectedDocuments);

  return (
    <div className="apply-flow-panel">
      <div className="apply-flow-section-head">
        <h2>Step 3: Payment</h2>
        <p>Review your policy details and complete payment securely.</p>
      </div>

      <div className="apply-flow-card-grid">
        <div className="apply-flow-card">
          <h3>Policy Summary</h3>
          <div className="payment-summary">
            <div>
              <span>Plan Name</span>
              <strong>{policy?.policyName || "N/A"}</strong>
            </div>
            <div>
              <span>Premium</span>
              <strong>{policy?.premiumAmount != null ? `Rs. ${policy.premiumAmount}` : "N/A"}</strong>
            </div>
            <div>
              <span>Billing Cycle</span>
              <strong>{policy?.billingCycle || "N/A"}</strong>
            </div>
          </div>

          <div className="apply-flow-doc-summary">
            <h4>Selected Documents</h4>
            {selectedList.length === 0 ? (
              <p className="apply-flow-muted">No verified documents selected.</p>
            ) : (
              selectedList.map((doc) => (
                <div key={`${doc.documentKey}-${doc.documentId || doc.fileName}`} className="apply-flow-selected">
                  <span>{doc.label}</span>
                  <span>{doc.fileName}</span>
                </div>
              ))
            )}
          </div>
        </div>

        <div className="apply-flow-card">
          <h3>Choose Payment Method</h3>
          <div className="payment-methods">
            {paymentOptions.map((method) => (
              <label key={method.value} className="payment-option">
                <input
                  type="radio"
                  value={method.value}
                  checked={paymentMethod === method.value}
                  onChange={() => onPaymentMethodChange(method.value)}
                />
                <span>{method.label}</span>
              </label>
            ))}
          </div>
        </div>
      </div>

      {stepError ? <p className="apply-flow-error">{stepError}</p> : null}

      <div className="step-actions">
        <button className="ghost-btn" type="button" onClick={onBack}>
          Back
        </button>
        <button className="primary-btn" type="button" onClick={onPay} disabled={paying}>
          {paying ? "Processing..." : "Pay & Complete"}
        </button>
      </div>
    </div>
  );
}
