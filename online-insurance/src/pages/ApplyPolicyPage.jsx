import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import AddressStep from "../components/AddressStep";
import DocumentUploadStep from "../components/DocumentUploadStep";
import PaymentStep from "../components/PaymentStep";
import PolicyConfirmation from "../components/PolicyConfirmation";
import "../styles/apply-policy.css";

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
  country: "India",
};

export default function ApplyPolicyPage() {
  const { policyId } = useParams();
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [policy, setPolicy] = useState(null);
  const [address, setAddress] = useState(defaultAddress);
  const [savingAddress, setSavingAddress] = useState(false);
  const [documents, setDocuments] = useState([]);
  const [selectedDocs, setSelectedDocs] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("CREDIT_CARD");
  const [paying, setPaying] = useState(false);
  const [purchase, setPurchase] = useState(null);

  useEffect(() => {
    api
      .get("/api/policies")
      .then((res) => {
        const list = Array.isArray(res.data) ? res.data : [];
        const found = list.find((item) => String(item.id) === String(policyId));
        setPolicy(found || null);
      })
      .catch(() => setPolicy(null));
  }, [policyId]);

  useEffect(() => {
    api
      .get("/api/profile")
      .then((res) => {
        const profile = res.data || {};
        setAddress((prev) => ({
          ...prev,
          fullName: profile.fullName || "",
          phone: profile.phone || "",
          doorNo: profile.doorNo || "",
          buildingName: profile.buildingName || "",
          street: profile.street || "",
          area: profile.area || "",
          city: profile.city || "",
          district: profile.district || "",
          state: profile.state || "",
          pincode: profile.pincode || "",
          country: "India",
        }));
      })
      .catch(() => {});
  }, []);

  useEffect(() => {
    api
      .get("/api/profile/documents")
      .then((res) => {
        setDocuments(Array.isArray(res.data) ? res.data : []);
      })
      .catch(() => setDocuments([]));
  }, []);

  const handleAddressChange = (field, value) => {
    setAddress((prev) => ({ ...prev, [field]: value }));
  };

  const handleSaveAddress = () => {
    setSavingAddress(true);
    const payload = {
      fullName: address.fullName,
      phone: address.phone,
      doorNo: address.doorNo,
      buildingName: address.buildingName,
      street: address.street,
      area: address.area,
      city: address.city,
      district: address.district,
      state: address.state,
      pincode: address.pincode,
    };
    api
      .put("/api/profile", payload)
      .then(() => {
        setStep(2);
      })
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to save address");
      })
      .finally(() => setSavingAddress(false));
  };

  const handleToggleDoc = (id) => {
    setSelectedDocs((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleUploadDoc = (file) => {
    if (!file) return;
    setUploading(true);
    const data = new FormData();
    data.append("file", file);
    api
      .post("/api/profile/upload-document", data, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then(() => api.get("/api/profile/documents"))
      .then((res) => {
        setDocuments(Array.isArray(res.data) ? res.data : []);
      })
      .catch(() => {
        alert("Document upload failed");
      })
      .finally(() => setUploading(false));
  };

  const handlePayment = () => {
    if (!policy?.id) {
      alert("Policy not found");
      return;
    }
    setPaying(true);
    api
      .post("/api/customers/policies/purchase", {
        policyId: policy.id,
        amount: policy.premiumAmount,
      })
      .then((res) => {
        setPurchase(res.data || null);
        setStep(4);
      })
      .catch((err) => {
        alert(err.response?.data?.error || "Payment failed");
      })
      .finally(() => setPaying(false));
  };

  const computedDates = useMemo(() => {
    if (!purchase?.purchaseDate) {
      return { start: null, end: null };
    }
    const start = new Date(purchase.purchaseDate);
    let end = null;
    const cycle = (policy?.billingCycle || "").toUpperCase();
    if (cycle.includes("MONTH")) {
      end = new Date(start);
      end.setMonth(end.getMonth() + 1);
    } else if (cycle.includes("QUARTER")) {
      end = new Date(start);
      end.setMonth(end.getMonth() + 3);
    } else if (cycle.includes("HALF")) {
      end = new Date(start);
      end.setMonth(end.getMonth() + 6);
    } else if (cycle.includes("YEAR")) {
      end = new Date(start);
      end.setFullYear(end.getFullYear() + 1);
    }
    return { start, end };
  }, [purchase, policy]);

  return (
    <div className="apply-policy-page">
      <Navbar />

      <section className="apply-hero">
        <h1>Apply for Plan</h1>
        <p>Complete the steps below to activate your insurance policy.</p>
      </section>

      <section className="apply-steps">
        <div className={`step-item ${step >= 1 ? "active" : ""}`}>1</div>
        <div className={`step-item ${step >= 2 ? "active" : ""}`}>2</div>
        <div className={`step-item ${step >= 3 ? "active" : ""}`}>3</div>
      </section>

      <section className="apply-card">
        {step === 1 && (
          <AddressStep
            address={address}
            onChange={handleAddressChange}
            onSave={handleSaveAddress}
            saving={savingAddress}
          />
        )}
        {step === 2 && (
          <DocumentUploadStep
            documents={documents}
            selectedDocs={selectedDocs}
            uploading={uploading}
            onBack={() => setStep(1)}
            onNext={() => setStep(3)}
            onToggle={handleToggleDoc}
            onUpload={handleUploadDoc}
          />
        )}
        {step === 3 && (
          <PaymentStep
            policy={policy}
            paymentMethod={paymentMethod}
            onPaymentMethodChange={setPaymentMethod}
            onBack={() => setStep(2)}
            onPay={handlePayment}
            paying={paying}
          />
        )}
        {step === 4 && (
          <PolicyConfirmation
            policy={policy}
            purchase={purchase}
            dates={computedDates}
            onViewPolicies={() => navigate("/customer-dashboard")}
          />
        )}
      </section>

      <Footer />
    </div>
  );
}
