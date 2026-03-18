import { useEffect, useRef, useState } from "react";
import api from "../api/api";

export default function AgentMapSelector({
  pincode,
  selectedAgentId,
  onSelectAgent,
  onAgentsLoaded,
}) {
  const mapContainerRef = useRef(null);
  const mapRef = useRef(null);
  const agentMarkersRef = useRef([]);
  const [agents, setAgents] = useState([]);
  const pincodeCenters = {
    "521154": [16.506, 80.648],
    "521301": [16.506, 80.648],
  };

  const resolveCoordinates = (agent) => {
    if (agent.latitude != null && agent.longitude != null) {
      const lat = Number(agent.latitude);
      const lng = Number(agent.longitude);
      if (!Number.isNaN(lat) && !Number.isNaN(lng)) {
        return [lat, lng];
      }
    }
    const lookupPincode = agent.pincode || pincode;
    const baseCoords = lookupPincode ? pincodeCenters[lookupPincode] : null;
    if (baseCoords) {
      return baseCoords;
    }
    return null;
  };

  const resolveMarkerCoordinates = (agent, index) => {
    const coords = resolveCoordinates(agent);
    if (!coords) {
      return null;
    }
    const offset = index * 0.0001;
    return [coords[0] + offset, coords[1] + offset];
  };

  useEffect(() => {
    if (!pincode || pincode.length < 6) {
      setAgents([]);
      onAgentsLoaded?.([]);
      return;
    }

    api
      .get(`/api/agents/pincode/${pincode}`)
      .then((res) => {
        const nextAgents = Array.isArray(res.data) ? res.data : [];
        setAgents(nextAgents);
        onAgentsLoaded?.(nextAgents);
      })
      .catch(() => {
        setAgents([]);
        onAgentsLoaded?.([]);
      });
  }, [pincode, onAgentsLoaded]);

  useEffect(() => {
    if (mapRef.current || !mapContainerRef.current || !window.L) {
      return;
    }

    const defaultCenter = [20.5937, 78.9629];
    const map = window.L.map(mapContainerRef.current).setView(defaultCenter, 5);
    window.L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap contributors",
    }).addTo(map);
    mapRef.current = map;
  }, []);

  useEffect(() => {
    if (!mapRef.current || !window.L) {
      return;
    }

    agentMarkersRef.current.forEach((marker) => marker.remove());
    agentMarkersRef.current = [];

    if (agents.length === 0) {
      return;
    }

    agents.forEach((agent, index) => {
      const coords = resolveMarkerCoordinates(agent, index);
      if (!coords) {
        return;
      }
      const marker = window.L.marker(coords).addTo(mapRef.current);
      marker.on("click", () => onSelectAgent?.(agent));
      marker.bindPopup(
        `<strong>${agent.name}</strong><br/>Pincode: ${agent.pincode}<br/><button id="select-agent-${agent.id}" type="button">Select Agent</button>`
      );
      marker.on("popupopen", () => {
        const button = document.getElementById(`select-agent-${agent.id}`);
        if (button) {
          button.onclick = () => onSelectAgent?.(agent);
        }
      });
      agentMarkersRef.current.push(marker);
    });

    const firstIndex = agents.findIndex((agent) => resolveCoordinates(agent));
    if (firstIndex >= 0) {
      const coords = resolveMarkerCoordinates(agents[firstIndex], firstIndex);
      if (coords) {
        mapRef.current.setView(coords, 12);
      }
    }
  }, [agents, onSelectAgent]);

  useEffect(() => {
    if (!mapRef.current || !window.L || !selectedAgentId) {
      return;
    }

    const selectedIndex = agents.findIndex((agent) => agent.id === selectedAgentId);
    const selected = selectedIndex >= 0 ? agents[selectedIndex] : null;
    const coords = selected ? resolveMarkerCoordinates(selected, selectedIndex) : null;
    if (!coords) {
      return;
    }

    mapRef.current.setView(coords, 13);
  }, [agents, selectedAgentId]);

  return (
    <>
      <div className="agent-map" ref={mapContainerRef} />
      {agents.length === 0 ? (
        <p style={{ marginTop: "12px", color: "#64748b" }}>
          Enter a valid pincode to load nearby agents.
        </p>
      ) : (
        <div className="agent-list">
          {agents.map((agent) => (
            <button
              key={agent.id}
              type="button"
              className={selectedAgentId === agent.id ? "active" : ""}
              onClick={() => onSelectAgent?.(agent)}
            >
              <span>{agent.name}</span>
              <span>{agent.pincode}</span>
            </button>
          ))}
        </div>
      )}
    </>
  );
}
