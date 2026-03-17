import { useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/agent.css";

export default function AdminAgents() {
  const [agents, setAgents] = useState([]);
  const [form, setForm] = useState({
    fullName: "",
    username: "",
    email: "",
    phone: "",
    password: "",
    experienceYears: "",
    specialization: "Online Insurance",
    serviceAreas: "",
    availabilityStatus: "AVAILABLE",
    pincode: "",
    latitude: null,
    longitude: null,
  });

  const token = localStorage.getItem("token");
  const headers = token ? { Authorization: `Bearer ${token}` } : {};

  const fetchAgents = () => {
    axios
      .get("http://localhost:8080/api/admin/agents", { headers })
      .then((res) => setAgents(res.data || []))
      .catch(() => setAgents([]));
  };

  useEffect(() => {
    fetchAgents();
  }, []);

  useEffect(() => {
    if (!navigator.geolocation) {
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setForm((prev) => ({
          ...prev,
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        }));
      },
      () => {}
    );
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    axios
      .post("http://localhost:8080/api/admin/agents", form, { headers })
      .then(() => {
        setForm((prev) => ({
          fullName: "",
          username: "",
          email: "",
          phone: "",
          password: "",
          experienceYears: "",
          specialization: "Online Insurance",
          serviceAreas: "",
          availabilityStatus: "AVAILABLE",
          pincode: "",
          latitude: prev.latitude,
          longitude: prev.longitude,
        }));
        fetchAgents();
      })
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to create agent");
      });
  };

  const handleDelete = (id) => {
    axios
      .put(`http://localhost:8080/agents/${id}/deactivate`, {}, { headers })
      .then(() => fetchAgents())
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to deactivate agent");
      });
  };

  const handleActivate = (id) => {
    axios
      .put(`http://localhost:8080/agents/${id}/activate`, {}, { headers })
      .then(() => fetchAgents())
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to activate agent");
      });
  };

  return (
    <div className="agent-page">
      <Navbar />
      <main className="agent-main">
        <div className="agent-header">
          <h1>Agents</h1>
          <p>Create and manage agent profiles. Agents cannot self-register.</p>
        </div>

        <div className="policy-card" style={{ marginBottom: "24px" }}>
          <h3>Create Agent</h3>
          <form onSubmit={handleSubmit} className="admin-form">
            <div className="two-column">
              <div className="profile-input-group">
                <label>Full Name</label>
                <input name="fullName" value={form.fullName} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Username</label>
                <input name="username" value={form.username} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Email</label>
                <input name="email" value={form.email} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Phone</label>
                <input name="phone" value={form.phone} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Password</label>
                <input name="password" type="password" value={form.password} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Experience Years</label>
                <input name="experienceYears" value={form.experienceYears} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Specialization</label>
                <select name="specialization" value={form.specialization} onChange={handleChange}>
                  <option value="Online Insurance">Online Insurance</option>
                  <option value="Health Insurance">Health Insurance</option>
                  <option value="Vehicle Insurance">Vehicle Insurance</option>
                  <option value="Home Insurance">Home Insurance</option>
                  <option value="Business Insurance">Business Insurance</option>
                  <option value="Life Insurance">Life Insurance</option>
                </select>
              </div>
              <div className="profile-input-group">
                <label>Availability</label>
                <select name="availabilityStatus" value={form.availabilityStatus} onChange={handleChange}>
                  <option value="AVAILABLE">AVAILABLE</option>
                  <option value="BUSY">BUSY</option>
                  <option value="UNAVAILABLE">UNAVAILABLE</option>
                </select>
              </div>
              <div className="profile-input-group">
                <label>Service Areas</label>
                <input name="serviceAreas" value={form.serviceAreas} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Pincode</label>
                <input name="pincode" value={form.pincode} onChange={handleChange} />
              </div>
              <div className="profile-input-group">
                <label>Latitude</label>
                <input name="latitude" value={form.latitude ?? ""} readOnly />
              </div>
              <div className="profile-input-group">
                <label>Longitude</label>
                <input name="longitude" value={form.longitude ?? ""} readOnly />
              </div>
            </div>
            <button type="submit">Create Agent</button>
          </form>
        </div>

        <div className="policy-card">
          <h3>Agent Directory</h3>
          <table className="appointments-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Username</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Specialization</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {agents.map((agent) => {
                const status = agent.status ?? "ACTIVE";
                const isInactive = status === "INACTIVE";
                const hasProfileId = Boolean(agent.id);
                return (
                  <tr key={agent.userId ?? agent.id ?? agent.username}>
                    <td>{agent.fullName}</td>
                    <td>{agent.username}</td>
                    <td>{agent.email}</td>
                    <td>{agent.phone}</td>
                    <td>
                      {agent.specialization
                        ? agent.specialization.replaceAll("_", " ")
                        : "Not Assigned"}
                    </td>
                    <td>{isInactive ? "Inactive" : "Active"}</td>
                    <td>
                      {isInactive ? (
                        <button
                          type="button"
                          className="success-btn"
                          disabled={!hasProfileId}
                          onClick={() => hasProfileId && handleActivate(agent.id)}
                        >
                          Activate
                        </button>
                      ) : (
                        <button
                          type="button"
                          className="danger-btn"
                          disabled={!hasProfileId}
                          onClick={() => hasProfileId && handleDelete(agent.id)}
                        >
                          Deactivate
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </main>
      <Footer />
    </div>
  );
}
