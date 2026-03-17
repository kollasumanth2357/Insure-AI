const ApplyPolicyModal = ({ policyId, onClose }) => {
  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h2>Apply Policy</h2>
        <p>Policy ID: {policyId}</p>

        <button className="apply-btn" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
};

export default ApplyPolicyModal;
