import React, { useEffect, useState } from "react";
import Spinner from "../Spinner";

function OverdueTasksWidget() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/tasks", { credentials: "include" })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch tasks");
        return res.json();
      })
      .then((tasks) => {
        const now = new Date();
        const overdue = tasks
          .filter(
            (t) =>
              t.dueDate && new Date(t.dueDate) < now && t.status !== "COMPLETE",
          )
          .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate));
        setTasks(overdue);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Spinner aria-label="Loading overdue tasks..." />;
  if (error)
    return (
      <div style={{ color: "red" }} aria-live="assertive">
        {error}
      </div>
    );

  return (
    <div
      className="widget overdue-tasks-widget"
      aria-label="Overdue Tasks Widget"
      role="region"
    >
      <h3>Overdue Tasks</h3>
      {tasks.length === 0 ? (
        <p>No overdue tasks!</p>
      ) : (
        <ul role="list">
          {tasks.map((task) => (
            <li key={task.id} role="listitem">
              <strong>{task.title}</strong> - Overdue since{" "}
              {new Date(task.dueDate).toLocaleDateString()}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default OverdueTasksWidget;
