import api from "../api/api";

export default function PolicyStatusToggle({ policy, onUpdated }) {
  const resolvedStatus =
    policy?.status ||
    (policy?.active === true ? "ACTIVE" : policy?.active === false ? "INACTIVE" : "ACTIVE");
  const isActive = resolvedStatus === "ACTIVE";

  const handleToggle = () => {
    const endpoint = isActive
      ? `/api/admin/policies/${policy.id}/deactivate`
      : `/api/admin/policies/${policy.id}/activate`;

    api
      .put(endpoint)
      .then(() => onUpdated?.())
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to update policy status");
      });
  };

  return (
    <button
      type="button"
      className={isActive ? "danger-btn" : "success-btn"}
      onClick={handleToggle}
    >
      {isActive ? "Deactivate" : "Activate"}
    </button>
  );
}
