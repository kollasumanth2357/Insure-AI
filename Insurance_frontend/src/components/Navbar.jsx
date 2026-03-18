import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useState, useRef, useEffect, useMemo } from "react";
import { useTheme } from "../context/ThemeContext";
import "../styles/navbar.css";

export default function Navbar() {
  useLocation();

  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const { theme, toggleTheme } = useTheme();

  let username = "";
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      username = payload.sub;
    } catch {
      username = "User";
    }
  }

  const [open, setOpen] = useState(false);
  const dropdownRef = useRef();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    navigate("/");
  };

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const navItems = useMemo(() => {
    if (!token) {
      return [
        { label: "Home", to: "/" },
        { label: "Features", to: "/features" },
        { label: "Plans", to: "/plans" },
        { label: "Contact", to: "/contact" },
      ];
    }

    if (role === "ADMIN") {
      return [
        { label: "Home", to: "/" },
        { label: "Agents", to: "/admin/agents" },
        { label: "Plans", to: "/admin/plans" },
        { label: "Dashboard", to: "/admin-dashboard" },
      ];
    }

    if (role === "AGENT") {
      return [
        { label: "Home", to: "/" },
        { label: "Plans", to: "/plans" },
        { label: "Appointments", to: "/appointments" },
        { label: "Feedback", to: "/feedback" },
      ];
    }

    return [
      { label: "Home", to: "/" },
      { label: "Features", to: "/features" },
      { label: "Plans", to: "/plans" },
      { label: "Appointments", to: "/appointments" },
      { label: "Contact", to: "/contact" },
    ];
  }, [role, token]);

  return (
    <nav className="navbar">
      <h2>ONLINE INSURANCE</h2>

      <ul>
        {navItems.map((item) => (
          <li key={item.to}>
            <NavLink
              to={item.to}
              end={item.to === "/"}
              className={({ isActive }) => (isActive ? "active-link" : "")}
            >
              {item.label}
            </NavLink>
          </li>
        ))}

        {!token && (
          <>
            <li>
              <NavLink
                to="/register"
                className={({ isActive }) => (isActive ? "active-link" : "")}
              >
                Register
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/login"
                className={({ isActive }) => (isActive ? "active-link" : "")}
              >
                Login
              </NavLink>
            </li>
          </>
        )}

        <li>
          <button className="theme-toggle" onClick={toggleTheme}>
            {theme === "dark" ? "Light Mode" : "Dark Mode"}
          </button>
        </li>

        {token && (
          <li className="profile-menu" ref={dropdownRef}>
            <span className="profile-trigger" onClick={() => setOpen(!open)}>
              👤 {username}
            </span>

            <div className={`dropdown ${open ? "show" : ""}`}>
              <div onClick={() => navigate("/profile")}>Profile</div>
              <div className="dropdown-role">Role: {role}</div>
              <div onClick={handleLogout}>Logout</div>
            </div>
          </li>
        )}
      </ul>
    </nav>
  );
}
