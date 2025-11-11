import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Profile from "./pages/Profile";
import BecomeReporter from "./pages/BecomeReporter";
import PrivateRoute from "./components/PrivateRoute";
import ReporterDashboard from "./pages/ReporterDashboard";
import AllNews from "./pages/AllNews";

function App() {
  return (
    <Router>
      <AuthProvider>
        <Navbar />
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/login" element={<Login />} />

          {/* Protected Routes (one-liner) */}
          <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
          <Route path="/become-reporter" element={<PrivateRoute><BecomeReporter /></PrivateRoute>} />
          <Route path="/reporter-dashboard" element={<PrivateRoute><ReporterDashboard /></PrivateRoute>} />
          <Route path="/all-news" element={<PrivateRoute><AllNews /></PrivateRoute>} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;





