export default function ApplyPolicyOtpStep({
  address,
  editable,
  onToggleEdit,
  onAddressChange,
  mobileNumber,
  onMobileNumberChange,
  otp,
  onOtpChange,
  onSendOtp,
  onVerifyOtp,
  onContinue,
  sendingOtp,
  verifyingOtp,
  savingAddress,
  otpSent,
  otpVerified,
  otpTimer,
  demoOtp,
  stepError,
}) {
  return (
    <div className="apply-flow-panel">
      <div className="apply-flow-section-head">
        <h2>Step 1: Address + OTP Verification</h2>
        <p>Confirm your address, verify your mobile number, then continue.</p>
      </div>

      <div className="apply-flow-card-grid">
        <div className="apply-flow-card">
          <div className="apply-flow-card-title">
            <h3>Address Details</h3>
            <button type="button" className="ghost-btn" onClick={onToggleEdit}>
              {editable ? "Lock Address" : "Edit Address"}
            </button>
          </div>

          <div className="step-grid">
            <div className="step-field">
              <label>Full Name</label>
              <input value={address.fullName} disabled={!editable} onChange={(e) => onAddressChange("fullName", e.target.value)} />
            </div>
            <div className="step-field">
              <label>Phone</label>
              <input value={address.phone} disabled={!editable} onChange={(e) => onAddressChange("phone", e.target.value)} />
            </div>
            <div className="step-field">
              <label>Door No</label>
              <input value={address.doorNo} disabled={!editable} onChange={(e) => onAddressChange("doorNo", e.target.value)} />
            </div>
            <div className="step-field">
              <label>Building Name</label>
              <input value={address.buildingName} disabled={!editable} onChange={(e) => onAddressChange("buildingName", e.target.value)} />
            </div>
            <div className="step-field">
              <label>Street</label>
              <input value={address.street} disabled={!editable} onChange={(e) => onAddressChange("street", e.target.value)} />
            </div>
            <div className="step-field">
              <label>Area</label>
              <input value={address.area} disabled={!editable} onChange={(e) => onAddressChange("area", e.target.value)} />
            </div>
            <div className="step-field">
              <label>City</label>
              <input value={address.city} disabled={!editable} onChange={(e) => onAddressChange("city", e.target.value)} />
            </div>
            <div className="step-field">
              <label>District</label>
              <input value={address.district} disabled={!editable} onChange={(e) => onAddressChange("district", e.target.value)} />
            </div>
            <div className="step-field">
              <label>State</label>
              <input value={address.state} disabled={!editable} onChange={(e) => onAddressChange("state", e.target.value)} />
            </div>
            <div className="step-field">
              <label>Pincode</label>
              <input value={address.pincode} disabled={!editable} onChange={(e) => onAddressChange("pincode", e.target.value)} />
            </div>
          </div>
        </div>

        <div className="apply-flow-card">
          <div className="apply-flow-card-title">
            <h3>Mobile OTP</h3>
          </div>

          <div className="step-field">
            <label>Mobile Number</label>
            <input value={mobileNumber} onChange={(e) => onMobileNumberChange(e.target.value)} />
          </div>

          <div className="apply-flow-inline">
            <button type="button" className="apply-btn" onClick={onSendOtp} disabled={sendingOtp || otpTimer > 0}>
              {sendingOtp ? "Sending..." : otpSent ? "OTP Sent" : "Send OTP"}
            </button>
            <span className="apply-flow-muted">
              {otpTimer > 0 ? `Resend in ${otpTimer}s` : "You can resend OTP now"}
            </span>
          </div>

          {demoOtp ? <p className="apply-flow-note">Demo OTP: <strong>{demoOtp}</strong></p> : null}

          <div className="step-field">
            <label>Enter OTP</label>
            <input value={otp} onChange={(e) => onOtpChange(e.target.value)} maxLength={6} />
          </div>

          <div className="apply-flow-inline">
            <button type="button" className="ghost-btn" onClick={onVerifyOtp} disabled={verifyingOtp || !otp}>
              {verifyingOtp ? "Verifying..." : otpVerified ? "Verified" : "Verify OTP"}
            </button>
            <button type="button" className="ghost-btn" onClick={onSendOtp} disabled={sendingOtp || otpTimer > 0}>
              Resend OTP
            </button>
          </div>

          {otpVerified ? <p className="apply-flow-success">Mobile number verified successfully.</p> : null}
        </div>
      </div>

      {stepError ? <p className="apply-flow-error">{stepError}</p> : null}

      <div className="step-actions">
        <button className="primary-btn" type="button" onClick={onContinue} disabled={savingAddress || !otpVerified}>
          {savingAddress ? "Saving..." : "Continue to Documents"}
        </button>
      </div>
    </div>
  );
}
