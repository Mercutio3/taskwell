import { Link } from "react-router-dom"

function Unauthorized() {
    return (
        <div className="page-center" role="alert" aria-label="Unauthorized Page">
            <h1>401 - Unauthorized</h1>
            <p>You must be logged in to access this.</p>
            <Link to="/login" className="btn">Login</Link>
        </div>
    )
}

export default Unauthorized