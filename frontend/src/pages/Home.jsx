import { Link } from "react-router-dom";
import "./Home.css";

function Home() {

  return (
    <>
      <div className="background">
        <div className="homepageInfo" aria-label="Home Page" role="main">
        <h1>Welcome to Taskwell!</h1>
        <p>Home page test.</p>
        <Link to="/dashboard">Go to Dashboard</Link>
        <Link to="/register">Register</Link>
        <Link to="/login">Login</Link>
      </div>
      </div>
      
    </>
  )
}

export default Home
