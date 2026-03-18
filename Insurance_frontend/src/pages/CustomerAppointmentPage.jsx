import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import api from "../api/api";
import AppointmentBookingForm from "../components/AppointmentBookingForm";
import AgentMapSelector from "../components/AgentMapSelector";
import "../styles/appointments-new.css";

export default function CustomerAppointmentPage() {
  const [customerId, setCustomerId] = useState(null);
  const [pincode, setPincode] = useState("");
  const [agents, setAgents] = useState([]);
  const [selectedAgent, setSelectedAgent] = useState(null);
  const [isBooking, setIsBooking] = useState(false);

  useEffect(() => {
    api
      .get("/api/profile")
      .then((res) => {
        setCustomerId(res.data?.id ?? null);
        if (res.data?.pincode) {
          setPincode(res.data.pincode);
        }
      })
      .catch(() => {});
  }, []);

  useEffect(() => {
    if (!selectedAgent) {
      return;
    }
    const stillAvailable = agents.find((agent) => agent.id === selectedAgent.id);
    if (!stillAvailable) {
      setSelectedAgent(null);
    }
  }, [agents, selectedAgent]);

  const handleBook = (payload) => {
    setIsBooking(true);
    api
      .post("/api/customer/appointments", payload)
      .then(() => {
        alert("Appointment booked successfully.");
      })
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to book appointment");
      })
      .finally(() => setIsBooking(false));
  };

  return (
    <div className="appointments-page">
      <Navbar />

      <section className="appointments-hero">
        <h1>Book an Appointment</h1>
        <p>
          Choose your pincode, select an available agent, and confirm your appointment
          time in a single flow.
        </p>
      </section>

      <section className="appointments-grid">
        <div className="appointments-card">
          <h2>Appointment Booking</h2>
          <AppointmentBookingForm
            customerId={customerId}
            pincode={pincode}
            onPincodeChange={setPincode}
            agents={agents}
            selectedAgent={selectedAgent}
            onSelectAgent={setSelectedAgent}
            onBook={handleBook}
            isBooking={isBooking}
          />
        </div>

        <div className="appointments-card">
          <h2>Select Agent on Map</h2>
          <AgentMapSelector
            pincode={pincode}
            selectedAgentId={selectedAgent?.id}
            onSelectAgent={setSelectedAgent}
            onAgentsLoaded={setAgents}
          />
        </div>
      </section>

      <Footer />
    </div>
  );
}
