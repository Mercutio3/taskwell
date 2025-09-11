function StatusMessage({ loading, error, success }) {
  if (loading) return <div className="status-message">Loading...</div>
  if (error) return <div className="status-message error">{error}</div>
  if (success) return <div className="status-message success">{success}</div>
  return null
}

export default StatusMessage