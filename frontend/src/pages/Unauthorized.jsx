import { useNavigate } from 'react-router-dom';
import StatusMessage from '../components/StatusMessage';

function Unauthorized() {
    const navigate = useNavigate();

    return (
        <div className="page-center" role="main" aria-label="Unauthorized Page">
            <h1>401 - Unauthorized</h1>
            <StatusMessage error="You must be logged in to access this." />
            <button onClick={() => navigate('/login')} className="btn">Login</button>
        </div>
    )
}

export default Unauthorized