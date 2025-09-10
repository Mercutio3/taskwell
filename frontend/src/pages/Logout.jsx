import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

function Logout() {
  const navigate = useNavigate()

  useEffect(() => {
    // Clear authentication tokens or user state here
    // e.g., localStorage.removeItem('token')
    // Redirect to login page after logout
    navigate('/login', { replace: true })
  }, [navigate])

  return (
    <div className="logout-container">
      <h1>Logging out...</h1>
    </div>
  )
}

export default Logout