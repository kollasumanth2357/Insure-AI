import { useEffect, useRef, useState } from "react";
import axios from "axios";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../styles/contact.css";

export default function Contact() {
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);
  const agentMarkersRef = useRef([]);
  const customerMarkerRef = useRef(null);
  const selectedAgentMarkerRef = useRef(null);
  const streetLayerRef = useRef(null);
  const satelliteLayerRef = useRef(null);
  const [mapView, setMapView] = useState("street");
  const [coords, setCoords] = useState(null);
  const [agents, setAgents] = useState([]);
  const [selectedAgentId, setSelectedAgentId] = useState(null);
  const [selectedAgent, setSelectedAgent] = useState(null);
  const [selectionMessage, setSelectionMessage] = useState("");
  const [customerId, setCustomerId] = useState(null);
  const [appointmentForm, setAppointmentForm] = useState({
    customerName: "",
    customerEmail: "",
    customerPhone: "",
    agentId: "",
    agentName: "",
    specialization: "",
    appointmentDate: "",
    notes: "",
  });

  useEffect(() => {
    if (!navigator.geolocation) {
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setCoords({
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        });
      },
      () => {}
    );
  }, []);

  useEffect(() => {
    if (!coords) {
      return;
    }
    axios
      .get("http://localhost:8080/agents/active")
      .then((res) => setAgents(res.data || []))
      .catch(() => setAgents([]));
  }, [coords]);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      return;
    }
    axios
      .get("http://localhost:8080/api/profile", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setCustomerId(res.data?.id ?? null);
        setAppointmentForm((prev) => ({
          ...prev,
          customerName: res.data?.fullName || prev.customerName,
          customerEmail: res.data?.email || prev.customerEmail,
          customerPhone: res.data?.phone || prev.customerPhone,
        }));
      })
      .catch(() => {});
  }, []);

  const selectAgent = (agent) => {
    setSelectedAgentId(agent.id);
    setSelectedAgent(agent);
    setSelectionMessage(`Agent Selected: ${agent.fullName}`);
    setAppointmentForm((prev) => ({
      ...prev,
      agentId: agent.id,
      agentName: agent.fullName,
      specialization: agent.specialization,
    }));
  };

  useEffect(() => {
    if (!coords || mapRef.current) {
      return;
    }

    const initMap = () => {
      if (!window.L || mapRef.current || !mapContainerRef.current) {
        return;
      }
      const map = window.L.map(mapContainerRef.current).setView(
        [coords.lat, coords.lng],
        13
      );
      streetLayerRef.current = window.L.tileLayer(
        "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        {
          attribution: "&copy; OpenStreetMap contributors",
        }
      );
      satelliteLayerRef.current = window.L.tileLayer(
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
        {
          attribution: "Tiles &copy; Esri",
        }
      );
      streetLayerRef.current.addTo(map);

      mapRef.current = map;
      customerMarkerRef.current = window.L.marker([coords.lat, coords.lng])
        .addTo(map)
        .bindPopup("Your location");
    };

    initMap();

    if (!mapRef.current) {
      const timer = setInterval(() => {
        initMap();
        if (mapRef.current) {
          clearInterval(timer);
        }
      }, 200);
      return () => clearInterval(timer);
    }
  }, [coords]);

  useEffect(() => {
    if (!mapRef.current) {
      return;
    }

    if (mapView === "street") {
      if (satelliteLayerRef.current) {
        mapRef.current.removeLayer(satelliteLayerRef.current);
      }
      if (streetLayerRef.current) {
        streetLayerRef.current.addTo(mapRef.current);
      }
    } else {
      if (streetLayerRef.current) {
        mapRef.current.removeLayer(streetLayerRef.current);
      }
      if (satelliteLayerRef.current) {
        satelliteLayerRef.current.addTo(mapRef.current);
      }
    }
  }, [mapView]);

  useEffect(() => {
    if (!mapRef.current || !window.L) {
      return;
    }

    agentMarkersRef.current.forEach((marker) => marker.remove());
    agentMarkersRef.current = [];

    agents.forEach((agent) => {
      if (agent.latitude == null || agent.longitude == null) {
        return;
      }
      const marker = window.L.marker([agent.latitude, agent.longitude]).addTo(mapRef.current);
      marker.on("click", () => selectAgent(agent));
      marker.bindPopup(
        `<strong>${agent.fullName}</strong><br/>Phone: ${agent.phone}<br/>Specialization: ${agent.specialization}<br/><button id="select-agent-${agent.id}" type="button">Select Agent</button>`
      );
      marker.on("popupopen", () => {
        const button = document.getElementById(`select-agent-${agent.id}`);
        if (button) {
          button.onclick = () => selectAgent(agent);
        }
      });
      agentMarkersRef.current.push(marker);
    });
  }, [agents]);

  useEffect(() => {
    if (!mapRef.current || !window.L) {
      return;
    }

    if (selectedAgentMarkerRef.current) {
      selectedAgentMarkerRef.current.remove();
      selectedAgentMarkerRef.current = null;
    }

    const agent = agents.find((item) => item.id === selectedAgentId);
    if (!agent || agent.latitude == null || agent.longitude == null) {
      return;
    }

    selectedAgentMarkerRef.current = window.L.circleMarker(
      [agent.latitude, agent.longitude],
      {
        radius: 14,
        color: "#f59e0b",
        weight: 3,
        fillColor: "#f59e0b",
        fillOpacity: 0.2,
      }
    ).addTo(mapRef.current);
  }, [agents, selectedAgentId]);

  const handleAppointmentChange = (e) => {
    const { name, value } = e.target;
    setAppointmentForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleAppointmentSubmit = (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    if (!token) {
      alert("Please login to book an appointment.");
      return;
    }
    if (!coords) {
      alert("Unable to capture your location.");
      return;
    }
    if (!appointmentForm.customerName || !appointmentForm.customerEmail || !appointmentForm.customerPhone) {
      alert("Please fill customer name, email, and phone.");
      return;
    }
    if (!appointmentForm.appointmentDate) {
      alert("Please select an appointment date.");
      return;
    }
    const autoAssign = !appointmentForm.agentId;
    if (autoAssign) {
      setSelectionMessage("Nearest Agent Assigned Automatically");
    }
    const headers = { Authorization: `Bearer ${token}` };
    const payload = {
      customerId,
      customerName: appointmentForm.customerName,
      customerEmail: appointmentForm.customerEmail,
      customerPhone: appointmentForm.customerPhone,
      agentId: appointmentForm.agentId || null,
      appointmentDate: appointmentForm.appointmentDate,
      notes: appointmentForm.notes,
      status: "PENDING",
      customerLatitude: coords.lat,
      customerLongitude: coords.lng,
      policyId: null,
    };
    axios
      .post("http://localhost:8080/appointments", payload, { headers })
      .then((res) => {
        if (autoAssign && res.data?.agentId) {
          const assigned = agents.find((agent) => agent.id === res.data.agentId);
          if (assigned) {
            setSelectedAgentId(assigned.id);
            setSelectedAgent(assigned);
          }
        }
        alert("Appointment booked successfully.");
        setAppointmentForm((prev) => ({
          ...prev,
          appointmentDate: "",
          notes: "",
        }));
      })
      .catch((err) => {
        alert(err.response?.data?.error || "Failed to book appointment");
      });
  };

  return (
    <>
      <Navbar />

      {/* TOP BANNER + BREADCRUMB */}
      <section className="contact-hero">
        <div className="contact-hero-inner">
          <h1>Contact</h1>
          <div className="contact-breadcrumb">
            <span>You are here</span>
            <span className="sep">/</span>
            <span className="link">Home</span>
            <span className="sep">/</span>
            <span className="current">Contact</span>
          </div>
        </div>
      </section>

      {/* MAP SECTION */}
      <section className="contact-map-section">
        <div className="contact-map-wrapper">
          <div className="map-toggle">
            <span className="map-toggle-label">Map View Toggle</span>
            <div className="map-toggle-actions">
              <button
                type="button"
                className={mapView === "street" ? "active" : ""}
                onClick={() => setMapView("street")}
              >
                Street
              </button>
              <button
                type="button"
                className={mapView === "satellite" ? "active" : ""}
                onClick={() => setMapView("satellite")}
              >
                Satellite
              </button>
            </div>
          </div>
          <div className="contact-map" ref={mapContainerRef} />
        </div>
      </section>

      {/* MAIN CONTACT CONTENT */}
      <section className="contact-main">
        <div className="contact-column left">
          <h2>Contact Us</h2>
          <div className="underline"></div>
          <p className="contact-intro">
            Fill out all required fields to send us a message. Please don&apos;t
            spam, thank you!
          </p>

          <form className="contact-form">
            <input type="text" placeholder="Your name" />
            <input type="email" placeholder="Email address" />
            <input type="text" placeholder="Subject" />
            <textarea placeholder="What would you like to tell us" rows={5} />
            <button type="submit" className="btn-primary">
              Send Message
            </button>
          </form>

          <div className="appointment-card">
            <h3>Book Appointment</h3>
            {selectionMessage && (
              <p style={{ marginBottom: "12px", color: "#22c55e", fontWeight: 600 }}>
                {selectionMessage}
              </p>
            )}
            <form onSubmit={handleAppointmentSubmit} className="contact-form">
              <input
                type="text"
                name="customerName"
                placeholder="Customer Name"
                value={appointmentForm.customerName}
                onChange={handleAppointmentChange}
                required
              />
              <input
                type="email"
                name="customerEmail"
                placeholder="Customer Email"
                value={appointmentForm.customerEmail}
                onChange={handleAppointmentChange}
                required
              />
              <input
                type="text"
                name="customerPhone"
                placeholder="Customer Phone"
                value={appointmentForm.customerPhone}
                onChange={handleAppointmentChange}
                required
              />
              <input
                type="text"
                name="agentName"
                placeholder="Agent Name"
                value={appointmentForm.agentName}
                readOnly
              />
              <input
                type="text"
                name="agentId"
                placeholder="Agent ID"
                value={appointmentForm.agentId}
                readOnly
              />
              <input
                type="text"
                name="specialization"
                placeholder="Specialization"
                value={appointmentForm.specialization}
                readOnly
              />
              <input
                type="datetime-local"
                name="appointmentDate"
                value={appointmentForm.appointmentDate}
                onChange={handleAppointmentChange}
                required
              />
              <textarea
                name="notes"
                placeholder="Notes / reason"
                rows={4}
                value={appointmentForm.notes}
                onChange={handleAppointmentChange}
              />
              <button type="submit" className="btn-primary">
                Submit Appointment
              </button>
            </form>
          </div>
        </div>

        <div className="contact-column right">
          <h2>Contact Information</h2>
          <div className="underline"></div>

          <p className="contact-info-text">
            The proposed system is a web-based application which maintains a
            centralized repository of all necessary information about policies,
            customers, and claims.
          </p>

          <ul className="contact-details">
            <li>
              <span className="icon">📍</span> Hebbal, Bengaluru
            </li>
            <li>
              <span className="icon">📞</span> 1800-302-0000
            </li>
            <li>
              <span className="icon">✉️</span> info@onlineinsurance.com
            </li>
          </ul>

          <div className="social-media">
            <h3>Social Media</h3>
            <div className="social-icons">
              <button type="button">f</button>
              <button type="button">t</button>
              <button type="button">p</button>
              <button type="button">g+</button>
              <button type="button">in</button>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </>
  );
}

