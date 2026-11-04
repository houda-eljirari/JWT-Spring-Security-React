import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link to="/dashboard" className="brand">
        <span className="brand-icon">◆</span> SecureDesk
      </Link>

      <div className="nav-links">
        <Link to="/dashboard">Tableau de bord</Link>
        <Link to="/incidents">Incidents</Link>
        <span className="user-info">
          {user?.username} · {user?.role}
        </span>
        <button className="logout-button" onClick={handleLogout}>
          Déconnexion
        </button>
      </div>
    </nav>
  );
}
