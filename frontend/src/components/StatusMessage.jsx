import Spinner from "./Spinner";

function StatusMessage({ loading, error, success }) {
  if (loading)
    return (
      <div className="status-message">
        <Spinner aria-label="Loading..." />
      </div>
    );
  if (error)
    return (
      <div className="status-message error" aria-live="assertive">
        {error}
      </div>
    );
  if (success)
    return (
      <div className="status-message success" aria-live="polite">
        {success}
      </div>
    );
  return null;
}

export default StatusMessage;
