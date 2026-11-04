import { useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

const STATUSES = ["OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"];

const STATUS_LABELS = {
  OPEN: "Ouvert",
  IN_PROGRESS: "En cours",
  RESOLVED: "Résolu",
  CLOSED: "Clôturé",
};

export default function Incidents() {
  const { user } = useAuth();
  const [incidents, setIncidents] = useState([]);
  const [form, setForm] = useState({ title: "", description: "" });
  const [technicianNames, setTechnicianNames] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const isAdmin = user?.role === "ADMIN";
  const isTechnician = user?.role === "TECHNICIAN";
  const canManage = isAdmin || isTechnician;

  const loadIncidents = async () => {
    setLoading(true);
    setError("");

    try {
      const endpoint = canManage
        ? "/api/incidents/assigned"
        : "/api/incidents/my";

      const response = await api.get(endpoint);
      setIncidents(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      setError(err.response?.data?.message || "Impossible de charger les incidents.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadIncidents();
  }, []);

  const createIncident = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");

    try {
      await api.post("/api/incidents", form);
      setForm({ title: "", description: "" });
      setMessage("Incident créé avec succès.");
      await loadIncidents();
    } catch (err) {
      setError(err.response?.data?.message || "Erreur lors de la création de l'incident.");
    }
  };

  const updateStatus = async (id, status) => {
    setError("");
    setMessage("");

    try {
      await api.put(`/api/incidents/${id}/status`, { status });
      setMessage("Statut mis à jour.");
      await loadIncidents();
    } catch (err) {
      setError(err.response?.data?.message || "Impossible de modifier le statut.");
    }
  };

  const assignTechnician = async (id) => {
    const technicianUsername = technicianNames[id]?.trim();

    if (!technicianUsername) {
      setError("Saisissez le nom d'utilisateur du technicien.");
      return;
    }

    setError("");
    setMessage("");

    try {
      await api.put(`/api/incidents/${id}/assign`, { technicianUsername });
      setMessage("Technicien affecté avec succès.");
      setTechnicianNames((previous) => ({ ...previous, [id]: "" }));
      await loadIncidents();
    } catch (err) {
      setError(err.response?.data?.message || "Impossible d'affecter ce technicien.");
    }
  };

  return (
    <main className="page-container">
      <section className="page-heading">
        <div>
          <span className="eyebrow">SUIVI ET INTERVENTIONS</span>
          <h1>Gestion des incidents</h1>
          <p>
            {isAdmin
              ? "Consultez et gérez les incidents de la plateforme."
              : isTechnician
                ? "Consultez les incidents qui vous sont affectés."
                : "Déclarez et suivez vos incidents informatiques."}
          </p>
        </div>
        <button className="secondary-button" onClick={loadIncidents}>
          Actualiser
        </button>
      </section>

      {error && <div className="error-message">{error}</div>}
      {message && <div className="success-message">{message}</div>}

      {user?.role === "EMPLOYEE" && (
        <section className="content-card">
          <h2>Signaler un incident</h2>
          <form className="incident-form" onSubmit={createIncident}>
            <label>Titre</label>
            <input
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              placeholder="Ex. Problème de connexion"
              required
            />

            <label>Description</label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              placeholder="Décrivez le problème rencontré..."
              rows={4}
              required
            />

            <button type="submit">Créer l'incident</button>
          </form>
        </section>
      )}

      <section className="content-card">
        <div className="section-title">
          <h2>{canManage ? "Incidents à traiter" : "Mes incidents"}</h2>
          <span>{incidents.length} incident(s)</span>
        </div>

        {loading ? (
          <p>Chargement des incidents...</p>
        ) : incidents.length === 0 ? (
          <div className="empty-state">
            <span>▤</span>
            <h3>Aucun incident trouvé</h3>
            <p>Les incidents apparaîtront ici.</p>
          </div>
        ) : (
          <div className="incident-list">
            {incidents.map((incident) => (
              <article className="incident-item" key={incident.id}>
                <div className="incident-top">
                  <div>
                    <span className="incident-id">INCIDENT #{incident.id}</span>
                    <h3>{incident.title}</h3>
                  </div>
                  <span className={`status-badge status-${incident.status?.toLowerCase()}`}>
                    {STATUS_LABELS[incident.status] || incident.status}
                  </span>
                </div>

                <p className="incident-description">{incident.description}</p>

                <div className="incident-meta">
                  <span>Déclaré par : {incident.employeeUsername || "—"}</span>
                  <span>Technicien : {incident.technicianUsername || "Non affecté"}</span>
                </div>

                {canManage && (
                  <div className="incident-actions">
                    <label>Modifier le statut</label>
                    <select
                      value={incident.status}
                      onChange={(e) => updateStatus(incident.id, e.target.value)}
                    >
                      {STATUSES.map((status) => (
                        <option value={status} key={status}>
                          {STATUS_LABELS[status]}
                        </option>
                      ))}
                    </select>

                    {isAdmin && (
                      <div className="assign-row">
                        <input
                          value={technicianNames[incident.id] || ""}
                          onChange={(e) =>
                            setTechnicianNames((previous) => ({
                              ...previous,
                              [incident.id]: e.target.value,
                            }))
                          }
                          placeholder="Nom d'utilisateur du technicien"
                        />
                        <button onClick={() => assignTechnician(incident.id)}>
                          Affecter
                        </button>
                      </div>
                    )}
                  </div>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}
