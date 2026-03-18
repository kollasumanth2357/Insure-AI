import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import api from "../api/api";
import "../styles/features.css";

const fallbackData = {
  heroTitle: "Our Services",
  heroSubtitle:
    "Explore our wide range of insurance products designed to protect every aspect of your life.",
  topServices: [
    {
      id: 1,
      title: "Online Insurance",
      description:
        "Comprehensive coverage for your peace of mind with instant online processing.",
      icon: "shield",
    },
    {
      id: 2,
      title: "Health Protection",
      description:
        "Secure your family's health with our extensive network of hospitals.",
      icon: "heart",
    },
    {
      id: 3,
      title: "Vehicle Safety",
      description:
        "Protect your vehicle against accidents, theft, and third‑party liabilities.",
      icon: "car",
    },
  ],
  midServices: [
    {
      id: 4,
      title: "Home Security",
      description:
        "Safeguard your dream home from natural calamities and burglaries.",
      icon: "home",
    },
    {
      id: 5,
      title: "Life Insurance",
      description:
        "Ensure your family's financial stability even in your absence.",
      icon: "umbrella",
    },
    {
      id: 6,
      title: "Business Cover",
      description:
        "Protect your business assets and liabilities with customized plans.",
      icon: "briefcase",
    },
  ],
  footer: {
    aboutTitle: "Online Insurance",
    aboutText:
      "Providing reliable insurance solutions for your family and assets 24/7.",
    quickLinks: ["About Us", "Our Service", "Contact"],
    tags: ["Insurance", "Policy", "Safety", "Family"],
    contact: {
      address: "123 Insurance Ave, City, State",
      phone: "+1 234 567 8900",
      email: "support@onlineinsurance.com",
    },
  },
};

function iconFor(key) {
  switch (key) {
    case "shield":
      return "🛡️";
    case "heart":
      return "❤️";
    case "car":
      return "🚗";
    case "home":
      return "🏠";
    case "umbrella":
      return "☂️";
    case "briefcase":
      return "💼";
    default:
      return "⭐";
  }
}

export default function Features() {
  const [data, setData] = useState(fallbackData);

  useEffect(() => {
    let isMounted = true;

    api
      .get("/api/features")
      .then((res) => {
        if (isMounted && res?.data) {
          setData((prev) => ({ ...prev, ...res.data }));
        }
      })
      .catch(() => {
        // keep fallback
      });

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <>
      <Navbar />

      {/* HERO SECTION: TITLE + SUBTEXT */}
      <section className="features-hero">
        <div className="features-hero-inner">
          <h1>{data.heroTitle}</h1>
          <p>{data.heroSubtitle}</p>
        </div>
      </section>

      {/* FIRST ROW: ONLINE INSURANCE / HEALTH PROTECTION / VEHICLE SAFETY */}
      <section className="features-services-section">
        <div className="features-services-inner">
          <div className="services-row">
            {data.topServices.map((s) => (
              <div key={s.id} className="service-card">
                <div className="service-icon">
                  <span>{iconFor(s.icon)}</span>
                </div>
                <h3>{s.title}</h3>
                <p>{s.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* SECOND ROW: HOME SECURITY / LIFE INSURANCE / BUSINESS COVER */}
      <section className="features-mid-row">
        <div className="features-services-inner">
          <div className="services-row">
            {data.midServices.map((s) => (
              <div key={s.id} className="service-card">
                <div className="service-icon">
                  <span>{iconFor(s.icon)}</span>
                </div>
                <h3>{s.title}</h3>
                <p>{s.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* BOTTOM FOOTER‑STYLE STRIP */}
      <section className="features-footer-strip">
        <div className="features-footer-inner">
          <div className="footer-column">
            <h3>{data.footer.aboutTitle}</h3>
            <p>{data.footer.aboutText}</p>
            <p className="footer-24">24 Hours Service</p>
          </div>

          <div className="footer-column">
            <h3>Quick Links</h3>
            <ul>
              {data.footer.quickLinks.map((item) => (
                <li key={item}>{item}</li>
              ))}
            </ul>
          </div>

          <div className="footer-column">
            <h3>Tags</h3>
            <div className="tags-row">
              {data.footer.tags.map((tag) => (
                <span key={tag} className="tag-pill">
                  {tag}
                </span>
              ))}
            </div>
          </div>

          <div className="footer-column">
            <h3>Contact Info</h3>
            <ul className="contact-info-list">
              <li>📍 {data.footer.contact.address}</li>
              <li>📞 {data.footer.contact.phone}</li>
              <li>✉️ {data.footer.contact.email}</li>
            </ul>
          </div>
        </div>
      </section>

      <Footer />
    </>
  );
}