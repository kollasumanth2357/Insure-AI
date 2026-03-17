import { useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/login.css";
import { useNavigate } from "react-router-dom";

export default function Login() {

  const [role, setRole] = useState("CUSTOMER");
  const [showPassword, setShowPassword] = useState(false);
  const [identifier, setIdentifier] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/login",
        {
          identifier,
          password,
          role
        }
      );

      console.log("Login Success:", response.data);

      // ✅ Store token and role correctly
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("role", response.data.role);

      alert(response.data.message);

      // ✅ Redirect to Home after login
      navigate("/");

    } catch (err) {
      console.error(err);
      setError("Invalid Credentials");
    }
  };

  return (
    <>
      <Navbar />

      <div className="login-page">
        <div className="login-container">

          {/* LEFT CARD */}
          <div className="login-card">
            <h2>Customer Login</h2>
            <div className="title-underline"></div>

            <form onSubmit={handleSubmit}>
              <label>Username or Email</label>
              <input
                type="text"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                required
              />

              <label>Password</label>
              <div className="password-wrapper">
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <span
                  className="eye-icon"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? "🙈" : "👁"}
                </span>
              </div>

              <label>Role</label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
              >
                <option value="ADMIN">ADMIN</option>
                <option value="AGENT">AGENT</option>
                <option value="CUSTOMER">CUSTOMER</option>
              </select>

              {error && (
                <p style={{ color: "red", marginTop: "10px" }}>
                  {error}
                </p>
              )}

              <button type="submit" className="login-btn">
                Login
              </button>
            </form>
          </div>

          {/* RIGHT CARD */}
          <div className="assist-card">
            <h3>Need Assistance?</h3>
            <div className="assist-divider"></div>

            <p>
              If you have trouble logging in or need to register a new
              account, please contact our support team.
            </p>

            <div className="assist-item">
              <span className="icon">📍</span>
              <div>
                <b>Visit Us</b>
                <p>123 Insurance Ave, City, State</p>
              </div>
            </div>

            <div className="assist-item">
              <span className="icon">📞</span>
              <div>
                <b>Call Us</b>
                <p>+1 234 567 8900</p>
              </div>
            </div>

            <div className="assist-item">
              <span className="icon">✉️</span>
              <div>
                <b>Email Us</b>
                <p>support@onlineinsurance.com</p>
              </div>
            </div>

          </div>

        </div>
      </div>

      <Footer />
    </>
  );
}
