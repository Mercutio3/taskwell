import "./Home.css";
import { useNavigate } from "react-router-dom";

function Home() {
  const navigate = useNavigate();

  return (
    <>
      <div className="background">
        <div className="homepageInfo" aria-label="Home Page" role="main">
          <h1>Welcome to Taskwell!</h1>
          <p>Please log in or register if you don't have an account.</p>
          <div className="home-buttons">
            <button onClick={() => navigate("/register")}>Register</button>
            <button onClick={() => navigate("/login")}>Login</button>
          </div>
        </div>
      </div>
    </>
  );
}

export default Home;
