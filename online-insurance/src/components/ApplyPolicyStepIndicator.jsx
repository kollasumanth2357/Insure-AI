export default function ApplyPolicyStepIndicator({ currentStep }) {
  const steps = [
    { id: 1, label: "Address & OTP" },
    { id: 2, label: "Documents" },
    { id: 3, label: "Payment" },
  ];

  return (
    <div className="apply-flow-steps" aria-label="Policy application steps">
      {steps.map((step, index) => (
        <div key={step.id} className="apply-flow-step-wrap">
          <div
            className={`apply-flow-step ${
              currentStep === step.id
                ? "step-active"
                : currentStep > step.id
                  ? "step-completed"
                  : ""
            }`}
          >
            <span className="apply-flow-step-number">{step.id}</span>
            <span className="apply-flow-step-label">{step.label}</span>
          </div>
          {index < steps.length - 1 && (
            <div className={`apply-flow-step-line ${currentStep > step.id ? "step-completed" : ""}`} />
          )}
        </div>
      ))}
    </div>
  );
}
