export default function ApplyPolicyDocumentsStep({
  requiredDocuments,
  profileDocuments,
  selectedDocuments,
  uploadingKey,
  onUploadFile,
  onUseProfileDocument,
  onBack,
  onNext,
  stepError,
}) {
  return (
    <div className="apply-flow-panel">
      <div className="apply-flow-section-head">
        <h2>Step 2: Document Upload & Verification</h2>
        <p>Upload from your system or reuse profile documents for each required item.</p>
      </div>

      <div className="apply-flow-doc-list">
        {requiredDocuments.map((doc) => {
          const selected = selectedDocuments[doc.key];
          return (
            <div key={doc.key} className="apply-flow-doc-card">
              <div className="apply-flow-doc-head">
                <div>
                  <h3>{doc.label}</h3>
                  <p>{doc.description}</p>
                </div>
                <span className={`apply-flow-status status-${(selected?.status || "PENDING").toLowerCase()}`}>
                  {selected?.status || "Pending"}
                </span>
              </div>

              <p className="apply-flow-muted">Accepted: {doc.acceptedTypes}</p>
              {selected?.reason ? <p className="apply-flow-error">{selected.reason}</p> : null}

              {selected ? (
                <div className="apply-flow-selected">
                  <span>{selected.fileName || "Selected document"}</span>
                  <span>{selected.source === "PROFILE" ? "Profile Documents" : "New Upload"}</span>
                </div>
              ) : null}

              <div className="apply-flow-inline">
                <label className="upload-button">
                  {uploadingKey === doc.key ? "Uploading..." : "Upload from Device"}
                  <input
                    type="file"
                    hidden
                    disabled={uploadingKey === doc.key}
                    accept=".pdf,image/png,image/jpeg"
                    onChange={(e) => onUploadFile(doc, e.target.files?.[0] || null)}
                  />
                </label>

                <select className="apply-flow-select" defaultValue="" onChange={(e) => onUseProfileDocument(doc, e.target.value)}>
                  <option value="">Choose from Profile Documents</option>
                  {profileDocuments.map((profileDoc) => (
                    <option key={profileDoc.id} value={profileDoc.id}>
                      {profileDoc.fileName}
                    </option>
                  ))}
                </select>
              </div>

              {selected?.fileUrl ? (
                <a className="apply-flow-link" href={selected.fileUrl} target="_blank" rel="noreferrer">
                  Preview document
                </a>
              ) : null}
            </div>
          );
        })}
      </div>

      {stepError ? <p className="apply-flow-error">{stepError}</p> : null}

      <div className="step-actions">
        <button className="ghost-btn" type="button" onClick={onBack}>
          Back
        </button>
        <button className="primary-btn" type="button" onClick={onNext}>
          Continue to Payment
        </button>
      </div>
    </div>
  );
}
