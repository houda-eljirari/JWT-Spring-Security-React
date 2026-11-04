import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Dashboard() {
  const { user } = useAuth();

  const roleLabels = {
    EMPLOYEE: "Employé",
    TECHNICIAN: "Technicien",
    ADMIN: "Administrateur",
  };

  return (
    <main className="page-container">
      <section className="welcome-section">
        <div>
          <span className="eyebrow">ESPACE DE TRAVAIL</span>
          <h1>Bonjour, {user?.username} !</h1>
          <p>Bienvenue sur votre plateforme de gestion des incidents informatiques.</p>
        </div>
        <span className="role-badge">{roleLabels[user?.role] || user?.role}</span>
      </section>

      <section className="dashboard-grid">
        <article className="dashboard-card">
          <div className="card-icon">▤</div>
          <h2>Gestion des incidents</h2>
          <p>Consultez les incidents et suivez leur évolution.</p>
          <Link to="/incidents" className="card-link">Accéder aux incidents →</Link>
        </article>

        {user?.role === "EMPLOYEE" && (
          <article className="dashboard-card">
            <div className="card-icon">＋</div>
            <h2>Signaler un problème</h2>
            <p>Déclarez un nouvel incident informatique.</p>
            <Link to="/incidents" className="card-link">Créer un incident →</Link>
          </article>
        )}

        {(user?.role === "TECHNICIAN" || user?.role === "ADMIN") && (
          <article className="dashboard-card">
            <div className="card-icon">✓</div>
            <h2>Suivi technique</h2>
            <p>Consultez les incidents qui nécessitent une intervention.</p>
            <Link to="/incidents" className="card-link">Voir les interventions →</Link>
          </article>
        )}
      </section>
    </main>
  );
}
