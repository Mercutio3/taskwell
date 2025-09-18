import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

function Logout() {
  const navigate = useNavigate()

  useEffect(() => {
    fetch('http://localhost:8080/logout', {
      method: 'POST',
      credentials: 'include',
    }).finally(() => {
      navigate('/login', { replace: true })
    })
  }, [navigate])

  return (
    <div className="logout-container" aria-label="Logging out">
      <h1>Logging out...</h1>
    </div>
  )
}

export default Logout