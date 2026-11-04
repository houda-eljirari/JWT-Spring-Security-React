import { createContext, useContext, useState } from "react";
import api from "../services/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const saveSession = (data) => {
    localStorage.setItem("token", data.token);
    const currentUser = {
      username: data.username,
      role: data.role,
    };
    localStorage.setItem("user", JSON.stringify(currentUser));
    setUser(currentUser);
  };

  const login = async (credentials) => {
    const response = await api.post("/auth/login", credentials);
    saveSession(response.data);
    return response.data;
  };

  const register = async (formData) => {
    const response = await api.post("/auth/register", formData);
    saveSession(response.data);
    return response.data;
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{ user, login, register, logout, isAuthenticated: !!user }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
