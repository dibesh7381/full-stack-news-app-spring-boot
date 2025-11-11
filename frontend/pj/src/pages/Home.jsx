import React, { useEffect, useState } from "react";

const Home = () => {
  const [homeData, setHomeData] = useState({ title: "", description: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/api/auth/home", { credentials: "include" })
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          setHomeData(data.data);
        } else {
          setError(data.message || "Failed to fetch data");
        }
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="p-4 text-center">Loading...</div>;
  if (error) return <div className="p-4 text-center text-red-500">{error}</div>;

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100 p-4">
      <div className="bg-white shadow-lg rounded-lg p-6 w-full max-w-md text-center">
        <h1 className="text-2xl md:text-3xl font-bold mb-4">{homeData.title}</h1>
        <p className="text-gray-700 text-base md:text-lg">{homeData.description}</p>
      </div>
    </div>
  );
};

export default Home;



