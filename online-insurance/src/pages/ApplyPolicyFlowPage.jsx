import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import ApplyPolicyStepIndicator from "../components/ApplyPolicyStepIndicator";
import ApplyPolicyOtpStep from "../components/ApplyPolicyOtpStep";
import ApplyPolicyDocumentsStep from "../components/ApplyPolicyDocumentsStep";
import ApplyPolicyPaymentStep from "../components/ApplyPolicyPaymentStep";
import ApplyPolicySuccessCard from "../components/ApplyPolicySuccessCard";
import "../styles/apply-policy-flow.css";

const defaultAddress = {
  fullName: "",
  phone: "",
  doorNo: "",
  buildingName: "",
  street: "",
  area: "",
  city: "",
  district: "",
  state: "",
  pincode: "",
};

export default function ApplyPolicyFlowPage() {
  const { policyId } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [step, setStep] = useState(1);
  const [policyData, setPolicyData] = useState(null);
  const [requiredDocuments, setRequiredDocuments] = useState([]);
  const [profileDocuments, setProfileDocuments] = useState([]);
  const [address, setAddress] = useState(defaultAddress);
  const [editableAddress, setEditableAddress] = useState(false);
  const [mobileNumber, setMobileNumber] = useState("");
  const [otp, setOtp] = useState("");
  const [otpSent, setOtpSent] = useState(false);
  const [otpVerified, setOtpVerified] = useState(false);
  const [otpTimer, setOtpTimer] = useState(0);
  const [demoOtp, setDemoOtp] = useState("");
  const [sendingOtp, setSendingOtp] = useState(false);
  const [verifyingOtp, setVerifyingOtp] = useState(false);
  const [savingAddress, setSavingAddress] = useState(false);
  const [uploadingKey, setUploadingKey] = useState("");
  const [selectedDocuments, setSelectedDocuments] = useState({});
  const [paymentMethod, setPaymentMethod] = useState("UPI");
  const [paying, setPaying] = useState(false);
  const [receipt, setReceipt] = useState(null);
  const [stepError, setStepError] = useState("");

  useEffect(() => {
    setLoading(true);
    Promise.all([
      api.post("/api/policy/apply/start", { policyId }),
      api.get(`/api/policy/documents/${policyId}`),
      api.get("/api/profile/documents"),
    ])
      .then(([startRes, docsRes, profileDocsRes]) => {
        const nextPolicyData = startRes.data || null;
        setPolicyData(nextPolicyData);
        setRequiredDocuments(Array.isArray(docsRes.data) ? docsRes.data : []);
        setProfileDocuments(Array.isArray(profileDocsRes.data) ? profileDocsRes.data : []);
        setAddress({
          fullName: nextPolicyData?.customerName || "",
          phone: nextPolicyData?.phone || "",
          doorNo: nextPolicyData?.doorNo || "",
          buildingName: nextPolicyData?.buildingName || "",
          street: nextPolicyData?.street || "",
          area: nextPolicyData?.area || "",
          city: nextPolicyData?.city || "",
          district: nextPolicyData?.district || "",
          state: nextPolicyData?.state || "",
          pincode: nextPolicyData?.pincode || "",
        });
        setMobileNumber(nextPolicyData?.phone || "");
      })
      .catch(() => {
        setPolicyData(null);
        setRequiredDocuments([]);
        setProfileDocuments([]);
        setStepError("Unable to load policy application details.");
      })
      .finally(() => setLoading(false));
  }, [policyId]);

  useEffect(() => {
    if (otpTimer <= 0) {
      return undefined;
    }
    const timer = window.setInterval(() => {
      setOtpTimer((current) => (current > 0 ? current - 1 : 0));
    }, 1000);
    return () => window.clearInterval(timer);
  }, [otpTimer]);

  const readyForPayment = useMemo(
    () =>
      requiredDocuments.length > 0 &&
      requiredDocuments.every((doc) => {
        const selected = selectedDocuments[doc.key];
        return selected && ["PENDING", "VERIFIED"].includes((selected.status || "").toUpperCase());
      }),
    [requiredDocuments, selectedDocuments]
  );

  const handleAddressChange = (field, value) => {
    setAddress((prev) => ({ ...prev, [field]: value }));
  };

  const handleSendOtp = () => {
    setStepError("");
    setSendingOtp(true);
    api
      .post("/api/policy/apply/send-otp", { phone: mobileNumber })
      .then((res) => {
        setOtpSent(true);
        setOtpVerified(false);
        setOtp("");
        setDemoOtp(res.data?.otp || "");
        setOtpTimer(Number(res.data?.expiresInSeconds || 45));
      })
      .catch((err) => {
        setStepError(err.response?.data?.error || "Failed to send OTP.");
      })
      .finally(() => setSendingOtp(false));
  };

  const handleVerifyOtp = () => {
    setStepError("");
    setVerifyingOtp(true);
    api
      .post("/api/policy/apply/verify-otp", { phone: mobileNumber, otp })
      .then(() => {
        setOtpVerified(true);
      })
      .catch((err) => {
        setOtpVerified(false);
        setStepError(err.response?.data?.error || "OTP verification failed.");
      })
      .finally(() => setVerifyingOtp(false));
  };

  const handleStepOneContinue = () => {
    setStepError("");
    if (!otpVerified) {
      setStepError("Please verify your mobile number before continuing.");
      return;
    }
    setSavingAddress(true);
    api
      .put("/api/profile", {
        fullName: address.fullName,
        phone: mobileNumber,
        doorNo: address.doorNo,
        buildingName: address.buildingName,
        street: address.street,
        area: address.area,
        city: address.city,
        district: address.district,
        state: address.state,
        pincode: address.pincode,
      })
      .then(() => {
        setStep(2);
        setEditableAddress(false);
        setPolicyData((prev) => (prev ? { ...prev, phone: mobileNumber } : prev));
      })
      .catch((err) => {
        setStepError(err.response?.data?.error || "Failed to save address details.");
      })
      .finally(() => setSavingAddress(false));
  };

  const handleUploadFile = (requiredDoc, file) => {
    if (!file) {
      return;
    }
    setStepError("");
    const validTypes = ["application/pdf", "image/png", "image/jpeg"];
    if (!validTypes.includes(file.type)) {
      setSelectedDocuments((prev) => ({
        ...prev,
        [requiredDoc.key]: {
          ...(prev[requiredDoc.key] || {}),
          documentKey: requiredDoc.key,
          label: requiredDoc.label,
          status: "REJECTED",
          reason: "Only PDF, JPG, and PNG files are allowed.",
        },
      }));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setSelectedDocuments((prev) => ({
        ...prev,
        [requiredDoc.key]: {
          ...(prev[requiredDoc.key] || {}),
          documentKey: requiredDoc.key,
          label: requiredDoc.label,
          status: "REJECTED",
          reason: "File size must be less than 5MB.",
        },
      }));
      return;
    }

    setUploadingKey(requiredDoc.key);
    const formData = new FormData();
    formData.append("policyId", policyId);
    formData.append("documentKey", requiredDoc.key);
    formData.append("file", file);

    api
      .post("/api/policy/upload-doc", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((res) => {
        const previewUrl = URL.createObjectURL(file);
        setSelectedDocuments((prev) => ({
          ...prev,
          [requiredDoc.key]: { ...res.data, fileUrl: res.data?.fileUrl || previewUrl },
        }));
        return api.get("/api/profile/documents");
      })
      .then((res) => {
        setProfileDocuments(Array.isArray(res.data) ? res.data : []);
      })
      .catch((err) => {
        setSelectedDocuments((prev) => ({
          ...prev,
          [requiredDoc.key]: {
            ...(prev[requiredDoc.key] || {}),
            documentKey: requiredDoc.key,
            label: requiredDoc.label,
            status: "REJECTED",
            reason: err.response?.data?.error || "Document upload failed.",
          },
        }));
      })
      .finally(() => setUploadingKey(""));
  };

  const handleUseProfileDocument = (requiredDoc, documentId) => {
    if (!documentId) {
      return;
    }
    setStepError("");
    setUploadingKey(requiredDoc.key);
    const formData = new FormData();
    formData.append("policyId", policyId);
    formData.append("documentKey", requiredDoc.key);
    formData.append("existingDocumentId", documentId);

    api
      .post("/api/policy/upload-doc", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((res) => {
        setSelectedDocuments((prev) => ({
          ...prev,
          [requiredDoc.key]: res.data,
        }));
      })
      .catch((err) => {
        setSelectedDocuments((prev) => ({
          ...prev,
          [requiredDoc.key]: {
            ...(prev[requiredDoc.key] || {}),
            documentKey: requiredDoc.key,
            label: requiredDoc.label,
            status: "REJECTED",
            reason: err.response?.data?.error || "Failed to use profile document.",
          },
        }));
      })
      .finally(() => setUploadingKey(""));
  };

  const handleStepTwoContinue = () => {
    setStepError("");
    if (!readyForPayment) {
      setStepError("Please complete all required documents before continuing.");
      return;
    }
    setStep(3);
  };

  const handlePayment = () => {
    setStepError("");
    if (!policyData?.policyId) {
      setStepError("Policy details are unavailable.");
      return;
    }
    setPaying(true);
    api
      .post("/api/policy/payment", {
        policyId: policyData.policyId,
        amount: policyData.premiumAmount,
        paymentMethod,
        selectedDocuments: Object.values(selectedDocuments).map(
          (doc) => `${doc.label || doc.documentKey}: ${doc.fileName || "Selected"}`
        ),
      })
      .then((res) => {
        setReceipt(res.data || null);
        setStep(4);
      })
      .catch((err) => {
        setStepError(err.response?.data?.error || "Payment failed.");
      })
      .finally(() => setPaying(false));
  };

  return (
    <div className="apply-flow-page">
      <Navbar />

      <section className="apply-flow-hero">
        <h1>Apply Policy</h1>
        <p>Complete the 3-step flow to submit your policy application safely.</p>
      </section>

      <section className="apply-flow-shell">
        <ApplyPolicyStepIndicator currentStep={step > 3 ? 3 : step} />

        {loading ? (
          <div className="apply-flow-loading">Loading application details...</div>
        ) : step === 1 ? (
          <ApplyPolicyOtpStep
            address={address}
            editable={editableAddress}
            onToggleEdit={() => setEditableAddress((prev) => !prev)}
            onAddressChange={handleAddressChange}
            mobileNumber={mobileNumber}
            onMobileNumberChange={setMobileNumber}
            otp={otp}
            onOtpChange={setOtp}
            onSendOtp={handleSendOtp}
            onVerifyOtp={handleVerifyOtp}
            onContinue={handleStepOneContinue}
            sendingOtp={sendingOtp}
            verifyingOtp={verifyingOtp}
            savingAddress={savingAddress}
            otpSent={otpSent}
            otpVerified={otpVerified}
            otpTimer={otpTimer}
            demoOtp={demoOtp}
            stepError={stepError}
          />
        ) : step === 2 ? (
          <ApplyPolicyDocumentsStep
            requiredDocuments={requiredDocuments}
            profileDocuments={profileDocuments}
            selectedDocuments={selectedDocuments}
            uploadingKey={uploadingKey}
            onUploadFile={handleUploadFile}
            onUseProfileDocument={handleUseProfileDocument}
            onBack={() => setStep(1)}
            onNext={handleStepTwoContinue}
            stepError={stepError}
          />
        ) : step === 3 ? (
          <ApplyPolicyPaymentStep
            policy={policyData}
            selectedDocuments={selectedDocuments}
            paymentMethod={paymentMethod}
            onPaymentMethodChange={setPaymentMethod}
            onBack={() => setStep(2)}
            onPay={handlePayment}
            paying={paying}
            stepError={stepError}
          />
        ) : (
          <ApplyPolicySuccessCard receipt={receipt} onDone={() => navigate("/customer-dashboard")} />
        )}
      </section>

      <Footer />
    </div>
  );
}
