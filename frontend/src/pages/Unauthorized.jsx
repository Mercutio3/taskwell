import { Link } from "react-router-dom"
import { useNavigate } from 'react-router-dom';

function Unauthorized() {
    const navigate = useNavigate();

    return (
        <div className="page-center" role="alert" aria-label="Unauthorized Page">
            <h1>401 - Unauthorized</h1>
            <p>You must be logged in to access this.</p>
            <button onClick={() => navigate('/login')} className="btn">Login</button>
        </div>
    )
}

export default Unauthorized