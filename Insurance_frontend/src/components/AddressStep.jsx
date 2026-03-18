export default function AddressStep({ address, onChange, onSave, saving }) {
  return (
    <div className="step-content">
      <h2>Step 1: Address Confirmation</h2>
      <p className="step-subtitle">
        Confirm or update your address details before moving ahead.
      </p>

      <div className="step-grid">
        <div className="step-field">
          <label>Full Name</label>
          <input
            value={address.fullName}
            onChange={(e) => onChange("fullName", e.target.value)}
          />
        </div>
        <div className="step-field">
          <label>Phone</label>
          <input
            value={address.phone}
            onChange={(e) => onChange("phone", e.target.value)}
          />
        </div>
        <div className="step-field">
          <label>Street</label>
          <input
            value={address.street}
            onChange={(e) => onChange("street", e.target.value)}
          />
        </div>
        <div className="step-field">
          <label>City</label>
          <input
            value={address.city}
            onChange={(e) => onChange("city", e.target.value)}
          />
        </div>
        <div className="step-field">
          <label>State</label>
          <input
            value={address.state}
            onChange={(e) => onChange("state", e.target.value)}
          />
        </div>
        <div className="step-field">
          <label>Pincode</label>
          <input
            value={address.pincode}
            onChange={(e) => onChange("pincode", e.target.value)}
          />
        </div>
        <div className="step-field">
          <label>Country</label>
          <input value={address.country} readOnly />
        </div>
      </div>

      <div className="step-actions">
        <button className="primary-btn" type="button" onClick={onSave} disabled={saving}>
          {saving ? "Saving..." : "Save & Continue"}
        </button>
      </div>
    </div>
  );
}
