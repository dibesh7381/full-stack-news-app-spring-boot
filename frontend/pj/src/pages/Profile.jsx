import { useContext, useEffect, useState } from "react";
import { AuthContext } from "../contexts/AuthContext";

const Profile = () => {
  const { user, setUser } = useContext(AuthContext);
  const [loading, setLoading] = useState(!user);

  useEffect(() => {
    if(!user){
      fetch("http://localhost:8080/api/auth/profile", { credentials: "include" })
        .then(res => res.json())
        .then(data => { if(data.success) setUser(data.data); })
        .finally(() => setLoading(false));
    } else setLoading(false);
  }, [user]);

  if (loading) return <p className="text-center mt-10">Loading...</p>;

  return (
    <div className="p-4 max-w-md mx-auto bg-white rounded shadow mt-10">
      <h2 className="text-2xl font-bold mb-4">Profile</h2>
      <p><strong>Username:</strong> {user.username}</p>
      <p><strong>Email:</strong> {user.email}</p>
      <p><strong>Role:</strong> {user.role}</p>
    </div>
  );
};

export default Profile;
