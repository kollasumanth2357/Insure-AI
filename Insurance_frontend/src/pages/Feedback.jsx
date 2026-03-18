import { useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/agent.css";

export default function Feedback() {
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!message.trim()) return;

    try {
      const token = localStorage.getItem("token");
      const headers = token ? { Authorization: `Bearer ${token}` } : {};

      await axios.post(
        "http://localhost:8080/api/agent/feedback",
        { message },
        { headers }
      );

      alert("Feedback submitted. Thank you!");
      setMessage("");
    } catch (err) {
      console.error("Feedback failed", err);
      alert("Could not submit feedback");
    }
  };

  return (
    <div className="agent-page">
      <Navbar />

      <main className="agent-main">
        <div className="agent-header">
          <h1>Feedback</h1>
          <p>
            Share feedback about customer interactions or policy material to
            help improve the platform.
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="profile-input-group">
            <label>Your Feedback</label>
            <textarea
              rows={6}
              className="feedback-textarea"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Describe your experience with customers or policies..."
            />
          </div>
          <button className="security-btn" type="submit">
            Submit Feedback
          </button>
        </form>
      </main>

      <Footer />
    </div>
  );
}

