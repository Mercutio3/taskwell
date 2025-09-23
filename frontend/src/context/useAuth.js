import { useContext } from "react";
import { AuthContext } from "./AuthContextContext";

export default function useAuth() {
  return useContext(AuthContext);
}
