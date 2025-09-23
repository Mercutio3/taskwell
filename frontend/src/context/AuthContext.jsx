import { useEffect, useState } from "react";
import { AuthContext } from "./AuthContextContext";

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isVerified, setIsVerified] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    fetch("http://localhost:8080/api/users/me", {
      credentials: "include",
    })
      .then(async (res) => {
        setIsAuthenticated(res.ok);
        if (res.ok) {
          const data = await res.json();
          setIsVerified(!!data.verified);
        } else {
          setIsVerified(false);
        }
      })
      .catch(() => {
        setIsAuthenticated(false);
        setIsVerified(false);
      })
      .finally(() => setLoading(false));
  }, []);

  const login = () => setIsAuthenticated(true);

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        isVerified,
        loading,
        setIsAuthenticated,
        setIsVerified,
        login,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
