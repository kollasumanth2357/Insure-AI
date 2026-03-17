import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";

import AdminDashboard from "./pages/AdminDashboard";
import AgentDashboard from "./pages/AgentDashboard";
import CustomerDashboard from "./pages/CustomerDashboard";
import Profile from "./pages/Profile";
import CustomerAppointmentPage from "./pages/CustomerAppointmentPage";
import AgentAppointmentsPage from "./pages/AgentAppointmentsPage";
import Features from "./pages/Features";
import Plans from "./pages/Plans";
import Appointments from "./pages/Appointments";
import Feedback from "./pages/Feedback";
import AdminAgents from "./pages/AdminAgents";
import AdminPlans from "./pages/AdminPlans";
import CustomerPlansPage from "./pages/CustomerPlansPage";
import ApplyPolicyFlowPage from "./pages/ApplyPolicyFlowPage";

import ProtectedRoute from "./components/ProtectedRoute";
import Chatbot from "./components/Chatbot";

export default function App() {
  return (
    <BrowserRouter>
      <>
        <Routes>
          {/* ================= PUBLIC ROUTES ================= */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/features" element={<Features />} />
          <Route path="/plans" element={<Plans />} />
          <Route
            path="/appointments"
            element={
              <ProtectedRoute allowedRoles={["CUSTOMER", "AGENT"]}>
                <Appointments />
              </ProtectedRoute>
            }
          />
          <Route
            path="/customer/appointments"
            element={
              <ProtectedRoute allowedRole="CUSTOMER">
                <CustomerAppointmentPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/agent/appointments"
            element={
              <ProtectedRoute allowedRole="AGENT">
                <AgentAppointmentsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/feedback"
            element={
              <ProtectedRoute allowedRole="AGENT">
                <Feedback />
              </ProtectedRoute>
            }
          />
          <Route path="/contact" element={<CustomerAppointmentPage />} />

          {/* ================= ROLE BASED DASHBOARDS ================= */}
          <Route
            path="/admin-dashboard"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/agents"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <AdminAgents />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/plans"
            element={
              <ProtectedRoute allowedRole="ADMIN">
                <AdminPlans />
              </ProtectedRoute>
            }
          />

          <Route
            path="/agent-dashboard"
            element={
              <ProtectedRoute allowedRole="AGENT">
                <AgentDashboard />
              </ProtectedRoute>
            }
          />

          <Route
            path="/customer-dashboard"
            element={
              <ProtectedRoute allowedRole="CUSTOMER">
                <CustomerDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/customer/plans"
            element={
              <ProtectedRoute allowedRole="CUSTOMER">
                <CustomerPlansPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/customer/apply/:policyId"
            element={
              <ProtectedRoute allowedRole="CUSTOMER">
                <ApplyPolicyFlowPage />
              </ProtectedRoute>
            }
          />

          {/* ================= PROFILE (ANY LOGGED USER) ================= */}
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
        </Routes>

        {/* Floating chatbot visible on every page */}
        <Chatbot />
      </>
    </BrowserRouter>
  );
}
