import { useState, useRef, useEffect } from "react";
import { X } from "lucide-react";
import api from "../api/api";
import "../styles/chatbot.css";

export default function Chatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState("");
  const [messages, setMessages] = useState([
    {
      from: "bot",
      text: "Hi! I’m your Insurance Assistant. Ask me anything about this application, dashboards or how to fill the forms.",
    },
  ]);
  const [loading, setLoading] = useState(false);
  const containerRef = useRef(null);

  useEffect(() => {
    if (isOpen && containerRef.current) {
      containerRef.current.scrollTop = containerRef.current.scrollHeight;
    }
  }, [isOpen, messages]);

  const sendMessage = async (e) => {
    e.preventDefault();
    const trimmed = input.trim();
    if (!trimmed) return;

    const userMsg = { from: "user", text: trimmed };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setLoading(true);

    try {
      // Call backend AI helper – you can later connect it to a real LLM
      const res = await api.post("/api/chat", { message: trimmed });
      const botReply =
        res?.data?.reply ||
        "I’m here to help with questions about this insurance app, your role dashboards and how to complete each form.";

      setMessages((prev) => [
        ...prev,
        userMsg,
        { from: "bot", text: botReply },
      ]);
    } catch (err) {
      setMessages((prev) => [
        ...prev,
        userMsg,
        {
          from: "bot",
          text:
            "Sorry, I couldn’t reach the help service right now. Please try again or contact support.",
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {/* Floating toggle button */}
      <button
        type="button"
        className="chatbot-toggle"
        onClick={() => setIsOpen((o) => !o)}
        aria-label="Open insurance assistant chat"
      >
        {isOpen ? <X size={22} /> : <span className="chatbot-robot">🤖</span>}
      </button>

      {/* Chat window */}
      {isOpen && (
        <div className="chatbot-window">
          <header className="chatbot-header">
            <div>
              <div className="chatbot-title">Insurance Assistant</div>
              <div className="chatbot-subtitle">
                Ask about plans, dashboards, or how to fill forms.
              </div>
            </div>
            <button
              type="button"
              className="chatbot-close"
              onClick={() => setIsOpen(false)}
            >
              <X size={18} />
            </button>
          </header>

          <div className="chatbot-messages" ref={containerRef}>
            {messages.map((m, idx) => (
              <div
                key={idx}
                className={`chatbot-message ${
                  m.from === "user" ? "from-user" : "from-bot"
                }`}
              >
                <span>{m.text}</span>
              </div>
            ))}
            {loading && (
              <div className="chatbot-message from-bot">
                <span>Thinking…</span>
              </div>
            )}
          </div>

          <form className="chatbot-input-row" onSubmit={sendMessage}>
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask about this app or any form…"
            />
            <button type="submit" disabled={loading}>
              Send
            </button>
          </form>
        </div>
      )}
    </>
  );
}

