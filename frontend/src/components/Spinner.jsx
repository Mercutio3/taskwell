import "./Spinner.css";

export default function Spinner() {
  return (
    <div className="spinner" aria-label="Loading" role="status">
      <div className="spinner-circle"></div>
    </div>
  );
}
