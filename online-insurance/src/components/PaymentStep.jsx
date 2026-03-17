export default function PaymentStep({
  policy,
  paymentMethod,
  onPaymentMethodChange,
  onBack,
  onPay,
  paying,
}) {
  return (
    <div className="step-content">
      <h2>Step 3: Payment</h2>
      <p className="step-subtitle">Choose a payment method and confirm.</p>

      <div className="payment-summary">
        <div>
          <span>Plan</span>
          <strong>{policy?.name || "Selected Plan"}</strong>
        </div>
        <div>
          <span>Premium</span>
          <strong>
            {policy?.premiumAmount != null ? `₹${policy.premiumAmount}` : "N/A"}
          </strong>
        </div>
      </div>

      <div className="payment-methods">
        {[
          { value: "CREDIT_CARD", label: "Credit Card" },
          { value: "DEBIT_CARD", label: "Debit Card" },
          { value: "NET_BANKING", label: "Net Banking" },
          { value: "UPI", label: "UPI" },
        ].map((method) => (
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

      <div className="step-actions">
        <button className="ghost-btn" type="button" onClick={onBack}>
          Back
        </button>
        <button className="primary-btn" type="button" onClick={onPay} disabled={paying}>
          {paying ? "Processing..." : "Pay & Activate"}
        </button>
      </div>
    </div>
  );
}
