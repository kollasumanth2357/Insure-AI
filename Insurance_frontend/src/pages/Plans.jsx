import { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/agent.css";
import { useNavigate } from "react-router-dom";

export default function Plans() {
  const [policies, setPolicies] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [step, setStep] = useState(1);
  const [selectedPolicy, setSelectedPolicy] = useState(null);
  const [userProfile, setUserProfile] = useState(null);
  const [address, setAddress] = useState({
    fullName: "",
    doorNo: "",
    buildingName: "",
    street: "",
    area: "",
    city: "",
    district: "",
    state: "",
    pincode: "",
  });
  const [mobile, setMobile] = useState("");
  const [otp, setOtp] = useState("");
  const [otpVerified, setOtpVerified] = useState(false);
  const [otpSending, setOtpSending] = useState(false);
  const [otpVerifying, setOtpVerifying] = useState(false);
  const [otpMessage, setOtpMessage] = useState("");
  const [requiredDocuments, setRequiredDocuments] = useState([]);
  const [profileDocuments, setProfileDocuments] = useState([]);
  const [uploadedDocuments, setUploadedDocuments] = useState([]);
  const [uploadingType, setUploadingType] = useState("");
  const [loadingModal, setLoadingModal] = useState(false);
  const [saveMessage, setSaveMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [paymentLoading, setPaymentLoading] = useState(false);
  const role = localStorage.getItem("role");
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const overlayStyle = {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    backgroundColor: "rgba(0,0,0,0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
    padding: "20px"
  };

  const modalStyle = {
    backgroundColor: "white",
    padding: "20px",
    borderRadius: "10px",
    width: "100%",
    maxWidth: "560px",
    textAlign: "center",
    maxHeight: "90vh",
    overflowY: "auto"
  };

  const inputStyle = {
    width: "90%",
    padding: "8px",
    margin: "5px",
    borderRadius: "5px",
    border: "1px solid #ccc"
  };

  const goldBtn = {
    backgroundColor: "#d4af37",
    color: "white",
    padding: "8px 14px",
    border: "none",
    borderRadius: "6px",
    margin: "5px",
    cursor: "pointer"
  };

  const plainBtn = {
    padding: "8px 14px",
    border: "1px solid #d1d5db",
    borderRadius: "6px",
    margin: "5px",
    cursor: "pointer",
    backgroundColor: "#fff"
  };

  useEffect(() => {
    if (role === "ADMIN") {
      navigate("/admin/plans");
      return;
    }
    if (!token) {
      setPolicies([]);
      return;
    }

    const headers = { Authorization: `Bearer ${token}` };

    const endpoint =
      role === "AGENT"
        ? "http://localhost:8080/api/agent/policies"
        : "http://localhost:8080/api/customers/policies";

    axios
      .get(endpoint, { headers })
      .then((res) => setPolicies(res.data || []))
      .catch(() => setPolicies([]));
  }, [role, navigate, token]);

  useEffect(() => {
    if (!showModal || !selectedPolicy || !token) {
      return;
    }

    const headers = { Authorization: `Bearer ${token}` };
    setLoadingModal(true);
    setErrorMessage("");
    setSaveMessage("");

    Promise.all([
      axios.get("http://localhost:8080/api/profile", { headers }),
      axios.get(`http://localhost:8080/api/policies/${selectedPolicy.id}/documents`, { headers }),
      axios.get("http://localhost:8080/api/user/documents", { headers }),
    ])
      .then(async ([profileRes, requirementsRes, documentsRes]) => {
        const profile = profileRes.data || {};
        setUserProfile(profile);
        setAddress({
          fullName: profile.fullName || "",
          doorNo: profile.doorNo || "",
          buildingName: profile.buildingName || "",
          street: profile.street || "",
          area: profile.area || "",
          city: profile.city || "",
          district: profile.district || "",
          state: profile.state || "",
          pincode: profile.pincode || "",
        });
        setMobile(profile.phone || "");
        setRequiredDocuments(Array.isArray(requirementsRes.data) ? requirementsRes.data : []);
        setProfileDocuments(Array.isArray(documentsRes.data) ? documentsRes.data : []);
        setUploadedDocuments([]);
        setOtp("");
        setOtpVerified(false);
        setOtpMessage("");
        setStep(1);

        if (profile.id) {
          try {
            const resumeRes = await axios.get(
              `http://localhost:8080/api/policy/resume/${profile.id}?policyId=${selectedPolicy.id}`,
              { headers }
            );
            const resume = resumeRes.data;
            if (resume && String(resume.policyId) === String(selectedPolicy.id)) {
              setStep(resume.step || 1);
              setAddress((prev) => ({ ...prev, ...(resume.address || {}) }));
              setUploadedDocuments(Array.isArray(resume.uploadedDocuments) ? resume.uploadedDocuments : []);
              setOtpVerified(Boolean(resume.otpVerified));
              if (resume.paymentStatus === "SUCCESS") {
                setSaveMessage("Saved progress found. Payment already completed.");
              } else {
                setSaveMessage("Saved progress restored.");
              }
            }
          } catch {
            // No saved progress available.
          }
        }
      })
      .catch((err) => {
        setErrorMessage(err.response?.data?.error || "Unable to load application details.");
      })
      .finally(() => setLoadingModal(false));
  }, [showModal, selectedPolicy, token]);

  useEffect(() => {
    if (!showModal || !selectedPolicy || !userProfile?.id || !token) {
      return;
    }
    const timeout = window.setTimeout(() => {
      axios
        .post(
          "http://localhost:8080/api/policy/save-progress",
          {
            policyId: selectedPolicy.id,
            step,
            address,
            uploadedDocuments,
            otpVerified,
            paymentStatus: null,
          },
          { headers: { Authorization: `Bearer ${token}` } }
        )
        .then(() => setSaveMessage("Progress saved"))
        .catch(() => {});
    }, 500);

    return () => window.clearTimeout(timeout);
  }, [showModal, selectedPolicy, userProfile, token, step, address, uploadedDocuments, otpVerified]);

  const categories = [
    "Online Insurance",
    "Health Insurance",
    "Vehicle Insurance",
    "Home Insurance",
    "Business Insurance",
    "Life Insurance",
  ];

  const handleOpenModal = (policy) => {
    setSelectedPolicy(policy);
    setStep(1);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setErrorMessage("");
    setSaveMessage("");
    setOtpMessage("");
  };

  const handleAddressChange = (field, value) => {
    setAddress((prev) => ({ ...prev, [field]: value }));
  };

  const handleSendOtp = () => {
    setOtpSending(true);
    setErrorMessage("");
    setOtpMessage("");
    axios
      .post(
        "http://localhost:8080/api/otp/send",
        { mobile },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      .then((res) => {
        setOtpVerified(false);
        setOtpMessage(res.data?.otp ? `Demo OTP: ${res.data.otp}` : "OTP sent");
      })
      .catch((err) => {
        setErrorMessage(err.response?.data?.error || "Failed to send OTP");
      })
      .finally(() => setOtpSending(false));
  };

  const handleVerifyOtp = () => {
    setOtpVerifying(true);
    setErrorMessage("");
    axios
      .post(
        "http://localhost:8080/api/otp/verify",
        { mobile, otp },
        { headers: { Authorization: `Bearer ${token}` } }
      )
      .then((res) => {
        setOtpVerified(Boolean(res.data?.verified));
        setOtpMessage(res.data?.message || "OTP verified");
      })
      .catch((err) => {
        setOtpVerified(false);
        setErrorMessage(err.response?.data?.error || "OTP verification failed");
      })
      .finally(() => setOtpVerifying(false));
  };

  const validateUploadFile = (file) => {
    const allowedTypes = ["application/pdf", "image/jpeg", "image/jpg", "image/png"];
    if (!file) {
      return "Please choose a file";
    }
    if (!allowedTypes.includes(file.type)) {
      return "Only PDF and JPG/PNG files are allowed";
    }
    return null;
  };

  const upsertUploadedDocument = (doc) => {
    setUploadedDocuments((prev) => {
      const next = prev.filter((item) => item.type !== doc.type);
      next.push(doc);
      return next;
    });
  };

  const handleLocalUpload = (docType, file) => {
    const validationMessage = validateUploadFile(file);
    if (validationMessage) {
      setErrorMessage(validationMessage);
      return;
    }
    setUploadingType(docType);
    setErrorMessage("");
    const formData = new FormData();
    formData.append("file", file);

    axios
      .post("http://localhost:8080/api/documents/upload", formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      })
      .then((res) => {
        const payload = res.data || {};
        upsertUploadedDocument({
          type: docType,
          name: requiredDocuments.find((item) => item.type === docType)?.name || docType,
          source: "LOCAL",
          id: payload.id,
          fileName: payload.fileName,
          fileType: payload.fileType,
          fileUrl: payload.fileUrl ? `http://localhost:8080${payload.fileUrl}` : "",
          status: payload.status || "UPLOADED",
        });
        return axios.get("http://localhost:8080/api/user/documents", {
          headers: { Authorization: `Bearer ${token}` },
        });
      })
      .then((res) => setProfileDocuments(Array.isArray(res.data) ? res.data : []))
      .catch((err) => {
        setErrorMessage(err.response?.data?.error || "Upload failed. Please retry.");
      })
      .finally(() => setUploadingType(""));
  };

  const handleReuseProfileDocument = (docType, documentId) => {
    const selectedDocument = profileDocuments.find((doc) => String(doc.id) === String(documentId));
    if (!selectedDocument) {
      return;
    }
    upsertUploadedDocument({
      type: docType,
      name: requiredDocuments.find((item) => item.type === docType)?.name || docType,
      source: "PROFILE",
      id: selectedDocument.id,
      fileName: selectedDocument.fileName,
      fileType: selectedDocument.fileType,
      fileUrl: selectedDocument.fileUrl ? `http://localhost:8080${selectedDocument.fileUrl}` : "",
      status: "REUSED",
    });
  };

  const isStepOneValid = Boolean(
    address.street && address.city && address.state && address.pincode && mobile && otpVerified
  );

  const isStepTwoValid =
    requiredDocuments.length > 0 &&
    requiredDocuments.every((doc) => uploadedDocuments.some((item) => item.type === doc.type));

  const loadRazorpayScript = () =>
    new Promise((resolve) => {
      if (window.Razorpay) {
        resolve(true);
        return;
      }
      const script = document.createElement("script");
      script.src = "https://checkout.razorpay.com/v1/checkout.js";
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });

  const handlePayment = async (methodLabel) => {
    if (!selectedPolicy) {
      return;
    }

    setPaymentLoading(true);
    setErrorMessage("");

    try {
      const scriptLoaded = await loadRazorpayScript();
      if (!scriptLoaded || !window.Razorpay) {
        throw new Error("Unable to load Razorpay checkout. Please retry.");
      }

      const orderRes = await axios.post(
        "http://localhost:8080/api/payment/create-order",
        {
          policyId: selectedPolicy.id,
          amount: selectedPolicy.premiumAmount,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const order = orderRes.data || {};
      const options = {
        key: order.key,
        amount: Number(selectedPolicy.premiumAmount || 0) * 100,
        currency: order.currency || "INR",
        name: selectedPolicy.name,
        description: `${methodLabel} Payment`,
        order_id: order.orderId,
        handler: async function (response) {
          try {
            await axios.post(
              "http://localhost:8080/api/payment/confirm",
              {
                policyId: selectedPolicy.id,
                amount: selectedPolicy.premiumAmount,
                orderId: order.orderId,
                paymentId: response.razorpay_payment_id || "mock_payment_id",
              },
              { headers: { Authorization: `Bearer ${token}` } }
            );

            await axios.post(
              "http://localhost:8080/api/policy/save-progress",
              {
                policyId: selectedPolicy.id,
                step: 3,
                address,
                uploadedDocuments,
                otpVerified,
                paymentStatus: "SUCCESS",
              },
              { headers: { Authorization: `Bearer ${token}` } }
            );

            setSaveMessage("Payment successful and policy purchased.");
          } catch (err) {
            setErrorMessage(err.response?.data?.error || "Payment confirmation failed.");
          } finally {
            setPaymentLoading(false);
          }
        },
        modal: {
          ondismiss: function () {
            setPaymentLoading(false);
            setErrorMessage("Payment cancelled. You can retry.");
          },
        },
        prefill: {
          name: address.fullName,
          contact: mobile,
          email: userProfile?.email || "",
        },
        theme: {
          color: "#d4af37",
        },
      };

      const paymentObject = new window.Razorpay(options);
      paymentObject.open();
    } catch (err) {
      setPaymentLoading(false);
      setErrorMessage(err.response?.data?.error || err.message || "Payment failed.");
    }
  };

  return (
    <div className="agent-page">
      <Navbar />

      <main className="agent-main">
        <div className="agent-header">
          <h1>Available Insurance Plans</h1>
          <p>
            Browse active policies across the main insurance categories.
          </p>
        </div>

        {!token && (
          <div className="policy-card">
            <h3>Please login to view available plans.</h3>
          </div>
        )}

        {token && categories.map((cat) => {
          const list = policies.filter((p) => p.mainCategory === cat);
          if (list.length === 0) return null;

          return (
            <div key={cat} style={{ marginBottom: "26px" }}>
              <h2 style={{ marginBottom: "10px", fontSize: "18px" }}>
                {cat} Policies
              </h2>
              <div className="policy-grid">
                {list.map((p) => (
                  <div key={p.id} className="policy-card">
                    <div className="policy-category">{p.mainCategory}</div>
                    <h3>{p.name}</h3>
                    <p className="policy-coverage">{p.description}</p>
                    <p className="policy-premium">
                      Premium: Rs. {p.premiumAmount}
                    </p>
                    <button
                      style={{
                        backgroundColor: "#d4af37",
                        color: "white",
                        padding: "10px 18px",
                        border: "none",
                        borderRadius: "8px",
                        cursor: "pointer",
                        marginTop: "15px"
                      }}
                      onClick={() => handleOpenModal(p)}
                    >
                      Apply Policy
                    </button>
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </main>

      {showModal && (
        <div style={overlayStyle}>
          <div style={modalStyle}>
            <h2>Apply Policy</h2>
            {selectedPolicy ? <p style={{ marginBottom: "8px" }}>{selectedPolicy.name}</p> : null}
            <p style={{ color: "#d4af37", fontWeight: "bold" }}>
              Step {step} of 3
            </p>

            {loadingModal ? <p>Loading application details...</p> : null}
            {saveMessage ? <p style={{ color: "#047857" }}>{saveMessage}</p> : null}
            {errorMessage ? <p style={{ color: "#b91c1c" }}>{errorMessage}</p> : null}
            {otpMessage ? <p style={{ color: "#1d4ed8" }}>{otpMessage}</p> : null}

            {!loadingModal && step === 1 && (
              <>
                <p>Address & OTP Verification</p>
                <input
                  placeholder="Full Name"
                  style={inputStyle}
                  value={address.fullName}
                  onChange={(e) => handleAddressChange("fullName", e.target.value)}
                />
                <input
                  placeholder="Door No"
                  style={inputStyle}
                  value={address.doorNo}
                  onChange={(e) => handleAddressChange("doorNo", e.target.value)}
                />
                <input
                  placeholder="Street"
                  style={inputStyle}
                  value={address.street}
                  onChange={(e) => handleAddressChange("street", e.target.value)}
                />
                <input
                  placeholder="City"
                  style={inputStyle}
                  value={address.city}
                  onChange={(e) => handleAddressChange("city", e.target.value)}
                />
                <input
                  placeholder="State"
                  style={inputStyle}
                  value={address.state}
                  onChange={(e) => handleAddressChange("state", e.target.value)}
                />
                <input
                  placeholder="Pincode"
                  style={inputStyle}
                  value={address.pincode}
                  onChange={(e) => handleAddressChange("pincode", e.target.value)}
                />
                <input
                  placeholder="Enter Mobile Number"
                  style={inputStyle}
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                />
                <button style={goldBtn} onClick={handleSendOtp} disabled={otpSending}>
                  {otpSending ? "Sending..." : "Send OTP"}
                </button>
                <input
                  placeholder="Enter OTP"
                  style={inputStyle}
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                />
                <button style={goldBtn} onClick={handleVerifyOtp} disabled={otpVerifying || !otp}>
                  {otpVerifying ? "Verifying..." : "Verify OTP"}
                </button>
              </>
            )}

            {!loadingModal && step === 2 && (
              <>
                <p>Upload Documents</p>
                {requiredDocuments.map((doc) => {
                  const selected = uploadedDocuments.find((item) => item.type === doc.type);
                  return (
                    <div
                      key={doc.type}
                      style={{
                        border: "1px solid #e5e7eb",
                        borderRadius: "8px",
                        padding: "12px",
                        marginBottom: "12px",
                        textAlign: "left",
                      }}
                    >
                      <strong>{doc.name}</strong>
                      <p style={{ margin: "6px 0", color: "#6b7280" }}>{doc.type}</p>
                      <input
                        type="file"
                        onChange={(e) => handleLocalUpload(doc.type, e.target.files?.[0] || null)}
                      />
                      <div style={{ marginTop: "10px" }}>
                        <select
                          style={{ ...inputStyle, width: "100%", margin: 0 }}
                          defaultValue=""
                          onChange={(e) => handleReuseProfileDocument(doc.type, e.target.value)}
                        >
                          <option value="">Reuse from profile documents</option>
                          {profileDocuments.map((profileDoc) => (
                            <option key={profileDoc.id} value={profileDoc.id}>
                              {profileDoc.fileName}
                            </option>
                          ))}
                        </select>
                      </div>
                      {uploadingType === doc.type ? <p>Uploading...</p> : null}
                      {selected ? (
                        <div style={{ marginTop: "10px" }}>
                          <p>Status: {selected.status}</p>
                          <p>Source: {selected.source}</p>
                          {selected.fileUrl ? (
                            <a href={selected.fileUrl} target="_blank" rel="noreferrer">
                              Preview
                            </a>
                          ) : null}
                        </div>
                      ) : null}
                    </div>
                  );
                })}
              </>
            )}

            {!loadingModal && step === 3 && (
              <>
                <p>Payment</p>
                <p style={{ marginBottom: "10px" }}>
                  Premium Amount: Rs. {selectedPolicy?.premiumAmount || 0}
                </p>
                <button style={goldBtn} onClick={() => handlePayment("UPI")} disabled={paymentLoading}>
                  {paymentLoading ? "Processing..." : "Pay with UPI"}
                </button>
                <button style={goldBtn} onClick={() => handlePayment("Card")} disabled={paymentLoading}>
                  {paymentLoading ? "Processing..." : "Card Payment"}
                </button>
              </>
            )}

            <div style={{ marginTop: "20px" }}>
              {step > 1 && (
                <button style={plainBtn} onClick={() => setStep(step - 1)}>
                  Back
                </button>
              )}

              {step < 3 ? (
                <button
                  style={{
                    ...goldBtn,
                    opacity: (step === 1 && !isStepOneValid) || (step === 2 && !isStepTwoValid) ? 0.6 : 1
                  }}
                  disabled={(step === 1 && !isStepOneValid) || (step === 2 && !isStepTwoValid)}
                  onClick={() => setStep(step + 1)}
                >
                  Next
                </button>
              ) : (
                <button style={goldBtn} disabled>
                  Finish
                </button>
              )}

              <button style={plainBtn} onClick={handleCloseModal}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      <Footer />
    </div>
  );
}
