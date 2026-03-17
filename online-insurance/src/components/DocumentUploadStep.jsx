export default function DocumentUploadStep({
  documents,
  selectedDocs,
  uploading,
  onUpload,
  onToggle,
  onBack,
  onNext,
}) {
  return (
    <div className="step-content">
      <h2>Step 2: Document Upload</h2>
      <p className="step-subtitle">
        Upload new documents or select from existing uploads.
      </p>

      <div className="upload-panel">
        <label className="upload-button">
          {uploading ? "Uploading..." : "Upload Document"}
          <input
            type="file"
            hidden
            disabled={uploading}
            onChange={(e) => onUpload(e.target.files[0])}
          />
        </label>
        <p className="upload-hint">PDF only. Max size 5MB.</p>
      </div>

      <div className="document-list">
        {documents.length === 0 ? (
          <div className="documents-empty">No documents uploaded yet.</div>
        ) : (
          documents.map((doc) => (
            <label key={doc.id} className="document-item">
              <input
                type="checkbox"
                checked={selectedDocs.includes(doc.id)}
                onChange={() => onToggle(doc.id)}
              />
              <span>{doc.fileName || `Document ${doc.id}`}</span>
            </label>
          ))
        )}
      </div>

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
