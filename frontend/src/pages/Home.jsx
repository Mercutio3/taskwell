import "./Home.css";
import { useNavigate } from "react-router-dom";

function Home() {
  const navigate = useNavigate();

  return (
    <>
      <div className="background">
        <div className="homepageInfo" aria-label="Home Page" role="main">
          <h1>Welcome to Taskwell!</h1>
          <p>Home page test.</p>
          <button onClick={() => navigate("/dashboard")}>Go to Dashboard</button>
          <button onClick={() => navigate("/register")}>Register</button>
          <button onClick={() => navigate("/login")}>Login</button>
        </div>
      </div> 
    </>
  )
}

export default Home
