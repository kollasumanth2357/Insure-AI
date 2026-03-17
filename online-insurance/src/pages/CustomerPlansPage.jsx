import { useEffect, useState } from "react";
import api from "../api/api";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PlanCard from "../components/PlanCard";
import ApplyPolicyModal from "../components/ApplyPolicyModal";
import "../styles/customer-plans.css";

export default function CustomerPlansPage() {
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [selectedPolicyId, setSelectedPolicyId] = useState(null);

  const handleApply = (policyId) => {
    setSelectedPolicyId(policyId);
    setShowApplyModal(true);
  };

  useEffect(() => {
    api
      .get("/api/policies")
      .then((res) => {
        const nextPlans = Array.isArray(res.data) ? res.data : [];
        setPlans(nextPlans);
      })
      .catch(() => setPlans([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="customer-plans-page">
      <Navbar />

      <section className="plans-hero">
        <h1>Insurance Plans</h1>
        <p>Choose a plan that fits your lifestyle and protect what matters most.</p>
      </section>

      <section className="plans-grid">
        {loading ? (
          <div className="plans-empty">Loading plans...</div>
        ) : plans.length === 0 ? (
          <div className="plans-empty">No insurance plans available</div>
        ) : (
          plans.map((policy) => (
            <PlanCard
              key={policy.id}
              policy={policy}
              onApply={() => handleApply(policy.id)}
            />
          ))
        )}
      </section>

      {showApplyModal && (
        <ApplyPolicyModal
          policyId={selectedPolicyId}
          onClose={() => setShowApplyModal(false)}
        />
      )}

      <Footer />
    </div>
  );
}
