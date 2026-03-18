import { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import PolicyStatusToggle from "../components/PolicyStatusToggle";
import "../styles/agent.css";

export default function AdminPlans() {
  const mainCategories = [
    "Online Insurance",
    "Health Insurance",
    "Vehicle Insurance",
    "Home Insurance",
    "Business Insurance",
    "Life Insurance",
  ];

  const subCategoryOptions = {
    "Online Insurance": [
      "Instant Policy Purchase",
      "Digital Policy Management",
      "Paperless Insurance Plans",
      "Online Claim Processing",
      "Multi-Policy Online Bundle",
    ],
    "Health Insurance": [
      "Basic Health Insurance Policies",
      "Premium Health Insurance Policies",
      "Family Health Insurance Policies",
      "Senior Citizen Health Plans",
      "Wellness & Preventive Plans",
    ],
    "Vehicle Insurance": [
      "Basic Vehicle Insurance Policies",
      "Premium Vehicle Insurance Policies",
      "Family Multi-Vehicle Insurance Plans",
      "Commercial Vehicle Insurance Plans",
      "Electric Vehicle Insurance Plans",
      "Roadside Assistance & Emergency Plans",
      "Usage-Based Vehicle Insurance Plans",
    ],
    "Home Insurance": [
      "Basic Home Insurance Policies",
      "Premium Home Insurance Policies",
      "Family Home Protection Plans",
      "Rental Property Insurance Plans",
      "Apartment & Condo Insurance Plans",
      "Natural Disaster Protection Plans",
      "Smart Home & Security Insurance Plans",
    ],
    "Business Insurance": [
      "Basic Business Insurance Policies",
      "Premium Business Insurance Policies",
      "Small Business Protection Plans",
      "Commercial Property Insurance Plans",
      "Employee & Workforce Insurance Plans",
      "Cyber & Technology Business Insurance",
      "Business Interruption & Disaster Insurance",
    ],
    "Life Insurance": [
      "Basic Life Insurance Policies",
      "Premium Life Insurance Policies",
      "Family Life Protection Plans",
      "Child Education & Future Plans",
      "Retirement & Pension Life Plans",
      "Investment Linked Life Insurance Plans",
    ],
  };

  const [policies, setPolicies] = useState([]);
  const [form, setForm] = useState({
    name: "",
    description: "",
    premiumAmount: "",
    active: true,
    mainCategory: "Online Insurance",
    subCategory: "Instant Policy Purchase",
    coverageAmount: "",
    billingCycle: "Monthly",
  });

  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const fetchPolicies = () => {
    axios
      .get("http://localhost:8080/api/admin/policies", { headers })
      .then((res) => setPolicies(res.data || []))
      .catch(() => setPolicies([]));
  };

  useEffect(() => {
    fetchPolicies();
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    if (name === "mainCategory") {
      const nextSubCategories = subCategoryOptions[value] || [];
      setForm({
        ...form,
        mainCategory: value,
        subCategory: nextSubCategories[0] || "",
      });
      return;
    }
    setForm({ ...form, [name]: type === "checkbox" ? checked : value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    axios
      .post("http://localhost:8080/api/admin/policies", form, { headers })
      .then(() => {
        setForm({
          name: "",
          description: "",
          premiumAmount: "",
          active: true,
          mainCategory: "Online Insurance",
          subCategory: "Instant Policy Purchase",
          coverageAmount: "",
          billingCycle: "Monthly",
        });
        fetchPolicies();
      })
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to create policy");
      });
  };

  return (
    <div className="agent-page">
      <Navbar />
      <main className="agent-main">
        <div className="agent-header">
          <h1>Policy Management</h1>
          <p>Create and manage insurance policies for all customers and agents.</p>
        </div>

        <div className="policy-card" style={{ marginBottom: "24px" }}>
          <h3>Create Policy</h3>
          <form onSubmit={handleSubmit} className="admin-form">
            <div className="two-column">
              <div className="profile-input-group">
                <label>Name</label>
                <input name="name" value={form.name} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Premium Amount</label>
                <input
                  name="premiumAmount"
                  value={form.premiumAmount}
                  onChange={handleChange}
                />
              </div>
              <div className="profile-input-group">
                <label>Coverage Amount</label>
                <input
                  name="coverageAmount"
                  value={form.coverageAmount}
                  onChange={handleChange}
                />
              </div>
              <div className="profile-input-group">
                <label>Active</label>
                <input
                  type="checkbox"
                  name="active"
                  checked={form.active}
                  onChange={handleChange}
                />
              </div>
              <div className="profile-input-group">
                <label>Billing Cycle</label>
                <select name="billingCycle" value={form.billingCycle} onChange={handleChange}>
                  <option value="Monthly">Monthly</option>
                  <option value="Yearly">Yearly</option>
                </select>
              </div>
              <div className="profile-input-group">
                <label>Main Category</label>
                <select name="mainCategory" value={form.mainCategory} onChange={handleChange}>
                  {mainCategories.map((category) => (
                    <option key={category} value={category}>
                      {category}
                    </option>
                  ))}
                </select>
              </div>
              <div className="profile-input-group">
                <label>Sub Category</label>
                <select name="subCategory" value={form.subCategory} onChange={handleChange}>
                  {(subCategoryOptions[form.mainCategory] || []).map((subcategory) => (
                    <option key={subcategory} value={subcategory}>
                      {subcategory}
                    </option>
                  ))}
                </select>
              </div>
              <div className="profile-input-group" style={{ gridColumn: "1 / -1" }}>
                <label>Description</label>
                <textarea
                  name="description"
                  value={form.description}
                  onChange={handleChange}
                  rows={4}
                />
              </div>
            </div>
            <button type="submit">Create Policy</button>
          </form>
        </div>

        <div className="policy-card">
          <h3>All Policies</h3>
          <table className="appointments-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Main Category</th>
                <th>Premium</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {policies.map((p) => (
                <tr key={p.id}>
                  <td>{p.name}</td>
                  <td>{p.mainCategory || "N/A"}</td>
                  <td>₹{p.premiumAmount}</td>
                  <td>
                    {p.status === "ACTIVE" || (p.status == null && p.active) ? (
                      <span className="status-active">Active</span>
                    ) : (
                      <span className="status-inactive">Inactive</span>
                    )}
                  </td>
                  <td>
                    <PolicyStatusToggle policy={p} onUpdated={fetchPolicies} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
      <Footer />
    </div>
  );
}
