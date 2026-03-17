import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import { useNavigate } from "react-router-dom";
import "../styles/home.css";

export default function Home() {

  const navigate = useNavigate();
  const token = localStorage.getItem("token"); // ✅ Check login

  return (
    <>
      <Navbar />

      {/* HERO SECTION */}
      <section className="hero">
        <div className="overlay">
          <div className="hero-content">
            <h1>Protect What Matters Most</h1>
            <p>
              Comprehensive insurance solutions tailored to your unique needs.
              Secure your future with our reliable coverage plans.
            </p>

            <div className="hero-buttons">

              {/* ✅ Show Get Started only if NOT logged in */}
              {!token && (
                <button
                  className="btn-primary"
                  onClick={() => navigate("/register")}
                >
                  Get Started
                </button>
              )}

              <button className="btn-outline" onClick={() => navigate("/plans")}>
                View Plans
              </button>

            </div>
          </div>
        </div>
      </section>

      {/* WELCOME SECTION */}
      <section className="welcome-section">
        <h2>Welcome to Online Insurance</h2>
        <div className="underline"></div>
        <p>
          We are dedicated to providing you with the best insurance policies
          that cater to your specific requirements. Whether it's life, health,
          car, or home insurance, we have a plan for you.
        </p>
      </section>

      {/* FEATURES SECTION */}
      <section className="features">
        <div className="feature-card">
          <div className="icon">🛡️</div>
          <h3>Reliable Coverage</h3>
          <p>Trustworthy plans backed by industry leaders.</p>
        </div>

        <div className="feature-card">
          <div className="icon">👥</div>
          <h3>Expert Support</h3>
          <p>24/7 assistance from our dedicated team.</p>
        </div>

        <div className="feature-card">
          <div className="icon">⏰</div>
          <h3>Fast Claims</h3>
          <p>Quick and hassle-free claim settlement process.</p>
        </div>
      </section>

      <Footer />
    </>
  );
}
