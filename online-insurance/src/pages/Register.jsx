import { useState, useEffect } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/register.css";

export default function Register() {

  const [formData, setFormData] = useState({
    fullName: "",
    username: "",
    email: "",
    phone: "",
    password: "",
    confirmPassword: ""
  });

  const [message, setMessage] = useState("");
  const [usernameAvailable, setUsernameAvailable] = useState(null);
  const [checkingUsername, setCheckingUsername] = useState(false);

  const [showPassword, setShowPassword] = useState(false);

  const [strengthText, setStrengthText] = useState("");
  const [strengthWidth, setStrengthWidth] = useState("0%");
  const [strengthColor, setStrengthColor] = useState("red");

  // ================= PASSWORD STRENGTH =================
  const checkPasswordStrength = (password) => {

    let score = 0;

    if (password.length >= 8) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/[a-z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[@$!%*?&]/.test(password)) score++;

    if (score <= 2) {
      setStrengthText("Weak");
      setStrengthWidth("33%");
      setStrengthColor("red");
    } else if (score <= 4) {
      setStrengthText("Medium");
      setStrengthWidth("66%");
      setStrengthColor("orange");
    } else {
      setStrengthText("Strong");
      setStrengthWidth("100%");
      setStrengthColor("green");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    if (name === "password") {
      checkPasswordStrength(value);
    }
  };

  // ================= LIVE USERNAME CHECK =================
  useEffect(() => {

    if (!formData.username.trim()) {
      setUsernameAvailable(null);
      return;
    }

    const delay = setTimeout(async () => {

      try {
        setCheckingUsername(true);

        const res = await axios.get(
          `http://localhost:8080/api/auth/check-username/${formData.username}`
        );

        setUsernameAvailable(!res.data.exists);

      } catch {
        setUsernameAvailable(null);
      } finally {
        setCheckingUsername(false);
      }

    }, 600);

    return () => clearTimeout(delay);

  }, [formData.username]);

  // ================= VALIDATIONS =================
  const passwordsMatch =
    formData.password &&
    formData.password === formData.confirmPassword;

  const allFieldsFilled =
    formData.fullName.trim() !== "" &&
    formData.username.trim() !== "" &&
    formData.email.trim() !== "" &&
    formData.phone.trim() !== "" &&
    formData.password.trim() !== "" &&
    formData.confirmPassword.trim() !== "";

  const passwordStrong =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/.test(formData.password);

  const canRegister =
    allFieldsFilled &&
    passwordStrong &&
    passwordsMatch &&
    usernameAvailable === true;

  const handleSubmit = async () => {

    if (!canRegister) return;

    setMessage("");

    try {

      const { confirmPassword, ...dataToSend } = formData;

      const res = await axios.post(
        "http://localhost:8080/api/customers/register",
        dataToSend
      );

      setMessage(res.data.message);

    } catch (err) {
      setMessage(err.response?.data || "Registration failed");
    }
  };

  return (
    <>
      <Navbar />

      <div className="register-page">
        <div className="register-container">

          <div className="register-card">
            <h2>Customer Registration</h2>
            <div className="title-underline"></div>

            <div className="grid-2">
              <div>
                <label>Full Name</label>
                <input
                  name="fullName"
                  value={formData.fullName}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label>Username</label>
                <input
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  style={{
                    borderColor:
                      usernameAvailable === null
                        ? "#e6b800"
                        : usernameAvailable
                          ? "green"
                          : "red"
                  }}
                />

                {formData.username && (
                  <p
                    style={{
                      fontSize: "12px",
                      marginTop: "4px",
                      color:
                        usernameAvailable === null
                          ? "gray"
                          : usernameAvailable
                            ? "green"
                            : "red"
                    }}
                  >
                    {checkingUsername
                      ? "Checking..."
                      : usernameAvailable === null
                        ? ""
                        : usernameAvailable
                          ? "Username available"
                          : "Username already exists"}
                  </p>
                )}
              </div>
            </div>

            <div className="grid-2">
              <div>
                <label>Email</label>
                <input
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label>Phone</label>
                <input
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                />
              </div>
            </div>

            <label>Password</label>
            <div style={{ position: "relative" }}>
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                value={formData.password}
                onChange={handleChange}
              />
              <span
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: "absolute",
                  right: "10px",
                  top: "8px",
                  cursor: "pointer"
                }}
              >
                {showPassword ? "🙈" : "👁"}
              </span>
            </div>

            {formData.password && (
              <>
                <div className="strength-bar-container">
                  <div
                    className="strength-bar"
                    style={{
                      width: strengthWidth,
                      backgroundColor: strengthColor
                    }}
                  ></div>
                </div>

                <p
                  style={{
                    color: strengthColor,
                    fontWeight: "600",
                    marginTop: "5px"
                  }}
                >
                  {strengthText} Password
                </p>
              </>
            )}

            <label>Confirm Password</label>
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
            />

            {!passwordsMatch && formData.confirmPassword && (
              <p style={{ color: "red", fontSize: "12px" }}>
                Passwords do not match
              </p>
            )}

            {message && (
              <p
                style={{
                  marginTop: "10px",
                  fontWeight: "600",
                  color: message.includes("exists") ? "red" : "green"
                }}
              >
                {message}
              </p>
            )}

            <button
              className="register-btn"
              onClick={handleSubmit}
              disabled={!canRegister}
              style={{
                opacity: canRegister ? 1 : 0.6,
                cursor: canRegister ? "pointer" : "not-allowed"
              }}
            >
              Register Now
            </button>

          </div>

          <div className="contact-card">
            <h3>Contact Information</h3>
            <div className="contact-item">
              <span className="icon">📍</span>
              <span>123 Insurance Ave, Business District, City, State 12345</span>
            </div>
            <div className="contact-item">
              <span className="icon">📞</span>
              <span>+1 234 567 8900</span>
            </div>
            <div className="contact-item">
              <span className="icon">✉️</span>
              <span>support@onlineinsurance.com</span>
            </div>
            <div className="divider"></div>
            <p className="follow-text">
              Follow us on social media for updates and offers.
            </p>
            <div className="socials">
              <div>FB</div>
              <div>TW</div>
              <div>IG</div>
            </div>
          </div>

        </div>
      </div>

      <Footer />
    </>
  );
}
