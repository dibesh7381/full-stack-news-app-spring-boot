import { useContext } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../contexts/AuthContext";

const PrivateRoute = ({ children }) => {
  const { user, loading } = useContext(AuthContext);

  if (loading) {
    // ✅ optional: loading state while fetching profile
    return <div className="text-center mt-10">Loading...</div>;
  }

  // ✅ If not logged in, redirect to login
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // ✅ If logged in, show the requested component
  return children;
};

export default PrivateRoute;
