import React, { useState, useEffect } from "react";
import axios from "axios";
import "../styles/profile.css";
import { useTheme } from "../context/ThemeContext";

export default function Profile() {

  const [activeTab, setActiveTab] = useState("personal");
  const [profilePic, setProfilePic] = useState(null);
  const [documents, setDocuments] = useState([]);

  const [settings] = useState({
    emailNotifications: true,
    smsAlerts: true,
    policyRenewals: true,
    claimUpdates: true,
    hidePhone: false,
    hideEmail: false,
    theme: "Dark Theme"
  });

  const [formData, setFormData] = useState({
    fullName: "",
    username: "",
    email: "",
    phone: "",
    profileImage: "",
    doorNo: "",
    buildingName: "",
    street: "",
    area: "",
    city: "",
    district: "",
    state: "",
    pincode: ""
  });

  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: ""
  });

  const [bankForm, setBankForm] = useState({
    bankName: "",
    accountNumber: "",
    ifscCode: ""
  });
  const [bankOtp, setBankOtp] = useState("");
  const [bankStep, setBankStep] = useState("form"); // form | otp
  const [linkedAccount, setLinkedAccount] = useState(null);

  const [passwordStrength, setPasswordStrength] = useState("");
  const { theme, toggleTheme } = useTheme();
  const userRole = localStorage.getItem("role");
  const hasProfileImage = Boolean(profilePic || formData.profileImage);

  // ================= FETCH PROFILE =================
  const fetchProfile = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await axios.get(
        "http://localhost:8080/api/profile",
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const user = res.data;

      setFormData({
        fullName: user.fullName || "",
        username: user.username || "",
        email: user.email || "",
        phone: user.phone || "",
        profileImage: user.profileImage || "",
        doorNo: user.doorNo || "",
        buildingName: user.buildingName || "",
        street: user.street || "",
        area: user.area || "",
        city: user.city || "",
        district: user.district || "",
        state: user.state || "",
        pincode: user.pincode || ""
      });

      if (user.profileImage) {
        setProfilePic(
          `http://localhost:8080/profile-images/${user.profileImage}`
        );
      }

    } catch (err) {
      console.error("Profile fetch error:", err);
    }
  };

  useEffect(() => {
  fetchProfile();
  fetchDocuments();
  fetchBankAccount();
}, []);

  // ================= FETCH DOCUMENTS =================
  const fetchDocuments = async () => {
  try {
    const token = localStorage.getItem("token");

    const res = await axios.get(
      "http://localhost:8080/api/profile/documents",
      { headers: { Authorization: `Bearer ${token}` } }
    );

    if (Array.isArray(res.data)) {
      setDocuments(res.data);
    } else {
      setDocuments([]);
    }

  } catch (err) {
    console.error("Fetch documents failed:", err);
    setDocuments([]);
  }
};

  // ================= FETCH BANK ACCOUNT =================
  const fetchBankAccount = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await axios.get(
        "http://localhost:8080/api/profile/bank-account",
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (res.data) {
        setLinkedAccount(res.data);
      } else {
        setLinkedAccount(null);
      }
    } catch {
      setLinkedAccount(null);
    }
  };

  // ================= UPDATE PROFILE =================
  const handleSave = async () => {
    try {
      const token = localStorage.getItem("token");

      await axios.put(
        "http://localhost:8080/api/profile",
        formData,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      await fetchProfile();
      alert("Profile updated successfully");

    } catch (err) {
      console.error("Update failed:", err);
      alert("Update failed");
    }
  };

  // ================= IMAGE UPLOAD =================
  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setProfilePic(URL.createObjectURL(file));

    const token = localStorage.getItem("token");
    const data = new FormData();
    data.append("file", file);

    try {
      await axios.post(
        "http://localhost:8080/api/profile/upload-image",
        data,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data"
          }
        }
      );

      await fetchProfile();
    } catch (err) {
      console.error("Image upload failed:", err);
    }
  };

  // ================= DOCUMENT UPLOAD =================
  const handleDocumentUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const token = localStorage.getItem("token");
    const data = new FormData();
    data.append("file", file);

    try {
      await axios.post(
        "http://localhost:8080/api/profile/upload-document",
        data,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data"
          }
        }
      );

      alert("Document uploaded successfully");
      fetchDocuments();

    } catch (err) {
      console.error("Document upload failed:", err);
      alert("Upload failed");
    }
  };

  // ================= DELETE DOCUMENT =================
  const handleDeleteDocument = async (id) => {
    try {
      const token = localStorage.getItem("token");

      await axios.delete(
        `http://localhost:8080/api/profile/documents/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      alert("Document deleted successfully");
      fetchDocuments();

    } catch (err) {
      console.error("Delete failed:", err);
      alert("Delete failed");
    }
  };

  // ================= VIEW / DOWNLOAD DOCUMENT =================
  const fetchDocumentBlob = async (id) => {
    const token = localStorage.getItem("token");

    const response = await axios.get(
      `http://localhost:8080/api/profile/documents/${id}/download`,
      {
        headers: {
          Authorization: `Bearer ${token}`
        },
        responseType: "blob"
      }
    );

    return response.data;
  };

  const handleDownloadDocument = async (id, fileName) => {
    try {
      const data = await fetchDocumentBlob(id);

      const url = window.URL.createObjectURL(new Blob([data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", fileName);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("Download failed:", error);
      alert("Download failed");
    }
  };

  const handleViewDocument = async (id) => {
    try {
      const data = await fetchDocumentBlob(id);
      const url = window.URL.createObjectURL(new Blob([data]));
      window.open(url, "_blank");
    } catch (error) {
      console.error("View failed:", error);
      alert("View failed");
    }
  };

  // ================= PASSWORD =================
  const checkStrength = (password) => {
    const strongRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/;

    if (strongRegex.test(password)) setPasswordStrength("Strong");
    else if (password.length >= 6) setPasswordStrength("Medium");
    else if (password.length > 0) setPasswordStrength("Weak");
    else setPasswordStrength("");
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData({ ...passwordData, [name]: value });
    if (name === "newPassword") checkStrength(value);
  };

  const handlePasswordSubmit = async () => {
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    try {
      const token = localStorage.getItem("token");

      await axios.put(
        "http://localhost:8080/api/profile/change-password",
        passwordData,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      alert("Password updated successfully");

      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: ""
      });

      setPasswordStrength("");

    } catch (err) {
      alert("Password update failed");
    }
  };

  const completion = () => {
    const values = Object.values(formData);
    const filled = values.filter(v => v !== "").length;
    return Math.round((filled / values.length) * 100);
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleBankChange = (e) => {
    setBankForm({ ...bankForm, [e.target.name]: e.target.value });
  };

  const handleSendBankOtp = async () => {
    if (!bankForm.bankName || !bankForm.accountNumber || !bankForm.ifscCode) {
      alert("Please fill all bank details");
      return;
    }

    try {
      const token = localStorage.getItem("token");

      const res = await axios.post(
        "http://localhost:8080/api/profile/bank/send-otp",
        bankForm,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setBankStep("otp");
      alert(
        res.data?.message +
          (res.data?.otp ? ` (Demo OTP: ${res.data.otp})` : "")
      );
    } catch (err) {
      console.error("Send OTP failed:", err);
      alert("Failed to send OTP");
    }
  };

  const handleVerifyBankOtp = async () => {
    if (!bankOtp) {
      alert("Please enter OTP");
      return;
    }

    try {
      const token = localStorage.getItem("token");

      const res = await axios.post(
        "http://localhost:8080/api/profile/bank/verify-otp",
        { otp: bankOtp },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setLinkedAccount(res.data);
      setBankOtp("");
      setBankStep("form");
      setBankForm({ bankName: "", accountNumber: "", ifscCode: "" });
      alert("Bank account verified and added");
    } catch (err) {
      console.error("Verify OTP failed:", err);
      alert("Invalid or expired OTP");
    }
  };

  return (
    <div className="profile-page">

      {/* HEADER */}
      <div className="profile-header">
        <div className="profile-cover"></div>
        <div className="profile-info-wrapper">
          <div className="profile-avatar-container">
            <div className="avatar-glow"></div>

            {hasProfileImage ? (
              <img
                src={
                  profilePic
                    ? profilePic
                    : `http://localhost:8080/profile-images/${formData.profileImage}`
                }
                alt="profile"
                className="profile-avatar"
              />
            ) : (
              <div
                className="profile-avatar"
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  textAlign: "center",
                  fontSize: "12px",
                  color: "#cbd5f5",
                  padding: "10px"
                }}
              >
                Upload Profile Photo
              </div>
            )}

            <label className="avatar-upload-icon">
              <svg width="16" height="16" viewBox="0 0 24 24"
                fill="none" stroke="currentColor"
                strokeWidth="2" strokeLinecap="round"
                strokeLinejoin="round">
                <path d="M12 3v12"></path>
                <path d="M7 8l5-5 5 5"></path>
                <path d="M5 21h14"></path>
              </svg>
              <input type="file" hidden onChange={handleImageUpload} />
            </label>
          </div>

          <div className="profile-user-info">
            <h2>{formData.username}</h2>
            <p>{formData.email}</p>
            <div style={{ marginTop: "10px" }}>
              <span className="badge">Member since Feb 2026</span>
              <span className="badge">NY</span>
            </div>
          </div>

          <div className="profile-completion-card">
            <p>Profile Completion</p>
            <div className="profile-progress-bar">
              <span style={{ width: completion() + "%" }}></span>
            </div>
            <p>{completion()}%</p>
          </div>
        </div>
      </div>

      {/* STATS */}
      {userRole !== "ADMIN" && (
        <div className="profile-stats">
          <div className="stat-card"><p>ACTIVE POLICIES</p><h3>3</h3></div>
          <div className="stat-card"><p>PREMIUM PAID</p><h3>$12,500</h3></div>
          <div className="stat-card"><p>CLAIMS</p><h3>1</h3></div>
          <div className="stat-card"><p>TOTAL POLICIES</p><h3>5</h3></div>
        </div>
      )}

      {/* TABS */}
      <div className="profile-tabs">
        {["personal", "financial", "security", "documents", "settings"]
          .filter((tab) => !(userRole === "ADMIN" && tab === "documents"))
          .map(tab => (
          <button
            key={tab}
            className={`profile-tab ${activeTab === tab ? "active" : ""}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab === "personal" ? "Personal Info" :
              tab.charAt(0).toUpperCase() + tab.slice(1)}
          </button>
        ))}
      </div>

      {/* PERSONAL TAB */}
      {activeTab === "personal" && (
        <div className="profile-card">
          <h3>Personal Information</h3>
          <div className="two-column">
            {Object.keys(formData).map((key) => (
              key !== "profileImage" && (
                <div className="profile-input-group" key={key}>
                  <label>{key}</label>
                  <input
                    name={key}
                    value={formData[key]}
                    onChange={handleChange}
                  />
                </div>
              )
            ))}
          </div>
          <button className="profile-save-btn" onClick={handleSave}>
            Save Changes
          </button>
        </div>
      )}

      {/* FINANCIAL TAB */}
      {activeTab === "financial" && (
        <div className="profile-card financial-card">
          <h3 className="financial-title">Bank Accounts</h3>
          <p className="financial-subtitle">
            Link your primary bank account to receive refunds and payouts.
          </p>

          {linkedAccount && (
            <div
              style={{
                marginBottom: "24px",
                padding: "16px 18px",
                borderRadius: "12px",
                border: "1px solid hsl(220 15% 25%)",
                background: "hsl(220 18% 12%)"
              }}
            >
              <p style={{ marginBottom: "4px", fontWeight: 600 }}>
                Linked Bank Account
              </p>
              <p>Bank: {linkedAccount.bankName}</p>
              <p>Account: {linkedAccount.maskedAccountNumber}</p>
              <p>IFSC: {linkedAccount.ifscCode}</p>
              <p>Status: {linkedAccount.verified ? "Verified" : "Pending"}</p>
            </div>
          )}

          <div className="two-column">
            <div className="profile-input-group">
              <label>Bank Name</label>
              <select
                name="bankName"
                value={bankForm.bankName}
                onChange={handleBankChange}
                disabled={bankStep === "otp"}
              >
                <option value="">Select bank</option>
                <option value="HDFC Bank">HDFC Bank</option>
                <option value="ICICI Bank">ICICI Bank</option>
                <option value="State Bank of India">State Bank of India</option>
                <option value="Axis Bank">Axis Bank</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div className="profile-input-group">
              <label>Account Number</label>
              <input
                name="accountNumber"
                value={bankForm.accountNumber}
                onChange={handleBankChange}
                disabled={bankStep === "otp"}
              />
            </div>

            <div className="profile-input-group">
              <label>IFSC Code</label>
              <input
                name="ifscCode"
                value={bankForm.ifscCode}
                onChange={handleBankChange}
                disabled={bankStep === "otp"}
              />
            </div>
          </div>

          {bankStep === "form" && (
            <button className="security-btn" onClick={handleSendBankOtp}>
              Get OTP
            </button>
          )}

          {bankStep === "otp" && (
            <div style={{ marginTop: "20px" }}>
              <div className="profile-input-group">
                <label>Enter OTP sent to your registered mobile</label>
                <input
                  value={bankOtp}
                  onChange={(e) => setBankOtp(e.target.value)}
                />
              </div>
              <button className="security-btn" onClick={handleVerifyBankOtp}>
                Verify & Add Account
              </button>
            </div>
          )}
        </div>
      )}

      {/* SECURITY TAB */}
      {activeTab === "security" && (
        <div className="profile-card security-card">
          <h3 className="security-title">Security Settings</h3>

          <div className="profile-input-group security-input">
            <label>Current Password</label>
            <input
              type="password"
              name="currentPassword"
              value={passwordData.currentPassword}
              onChange={handlePasswordChange}
            />
          </div>

          <div className="profile-input-group security-input">
            <label>New Password</label>
            <input
              type="password"
              name="newPassword"
              value={passwordData.newPassword}
              onChange={handlePasswordChange}
            />
            {passwordStrength && (
              <small>Strength: {passwordStrength}</small>
            )}
          </div>

          <div className="profile-input-group security-input">
            <label>Confirm Password</label>
            <input
              type="password"
              name="confirmPassword"
              value={passwordData.confirmPassword}
              onChange={handlePasswordChange}
            />
          </div>

          <button className="security-btn" onClick={handlePasswordSubmit}>
            Update Password
          </button>
        </div>
      )}

      {/* DOCUMENTS TAB */}
      {userRole !== "ADMIN" && activeTab === "documents" && (
        <div className="profile-card">
          <h3 className="doc-title">Documents</h3>
          <p className="doc-subtitle">
            KYC and Policy related documents.
          </p>

          <div className="doc-upload-box">
            <div className="doc-icon-circle">⬆</div>
            <h4 className="doc-upload-text">Upload Document</h4>
            <p className="doc-upload-subtext">
              Drag & drop or click to browse
            </p>
            <label className="doc-select-btn">
              Select File
              <input type="file" hidden onChange={handleDocumentUpload} />
            </label>
          </div>

          {documents.length > 0 && (
            <div style={{ marginTop: "30px" }}>
              {documents.map((doc) => (
                <div
                  key={doc.id}
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    padding: "10px 0",
                    borderBottom: "1px solid #333"
                  }}
                >
                  <span>{doc.fileName}</span>
                  <div style={{ display: "flex", gap: "10px" }}>
                    <button
                      className="doc-select-btn"
                      onClick={() => handleViewDocument(doc.id)}
                    >
                      View
                    </button>
                    <button
                      className="doc-select-btn"
                      onClick={() => handleDownloadDocument(doc.id, doc.fileName)}
                    >
                      Download
                    </button>
                    <button
                      className="security-btn"
                      onClick={() => handleDeleteDocument(doc.id)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

        </div>
      )}

      {/* SETTINGS TAB */}
      {activeTab === "settings" && (
        <div className="settings-wrapper">
          <div className="settings-card">
            <h3 className="settings-title">
              <span className="settings-icon">🔔</span>
              Notification Preferences
            </h3>
            <p className="settings-subtitle">
              Choose how you want to be notified.
            </p>
          </div>

          <div className="settings-card">
            <h3 className="settings-title">
              <span className="settings-icon">👤</span>
              Privacy Settings
            </h3>
            <p className="settings-subtitle">
              Control who sees your contact info.
            </p>
          </div>

          <div className="settings-card">
            <h3 className="settings-title">
              <span className="settings-icon">✨</span>
              Theme
            </h3>
            <p className="settings-subtitle">
              Current: {theme === "dark" ? "Dark Mode" : "Light Mode"}
            </p>
            <button className="security-btn" onClick={toggleTheme}>
              Toggle Theme
            </button>
          </div>
        </div>
      )}

    </div>
  );
}
