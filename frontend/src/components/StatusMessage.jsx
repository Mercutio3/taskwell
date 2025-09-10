function StatusMessage({ loading, error }) {
  if (loading) return <div className="status-message">Loading...</div>
  if (error) return <div className="status-message error">{error}</div>
  return null
}

export default StatusMessage