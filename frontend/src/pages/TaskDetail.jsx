import Navbar from "../components/Navbar";
import { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { completeTask, uncompleteTask } from "../services/api";
import { formatCategory } from "../utils/formatting";
import StatusMessage from "../components/StatusMessage";

function TaskDetail() {
  const { id } = useParams();
  const [task, setTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [toggleLoading, setToggleLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(true);
    fetch(`http://localhost:8080/api/tasks/${id}`, {
      credentials: "include",
    })
      .then((res) => {
        if (res.status == 403) {
          navigate("/forbidden");
          return null;
        }
        if (res.status == 404) {
          navigate("/404");
          return null;
        }
        if (!res.ok) throw new Error("Failed to fetch task");
        return res.json();
      })
      .then(setTask)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleToggleComplete = async () => {
    if (!task) return;
    setToggleLoading(true);
    setError("");
    try {
      if (task.status === "COMPLETE") {
        await uncompleteTask(task.id);
      } else {
        await completeTask(task.id);
      }
      // Refetch task to update status
      const res = await fetch(`http://localhost:8080/api/tasks/${id}`, {
        credentials: "include",
      });
      if (!res.ok) throw new Error("Failed to fetch task");
      const updatedTask = await res.json();
      setTask(updatedTask);
    } catch {
      setError("Failed to update task status");
    } finally {
      setToggleLoading(false);
    }
  };

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this task?")) {
      try {
        await fetch(`http://localhost:8080/api/tasks/${task.id}`, {
          method: "DELETE",
          credentials: "include",
        });
        navigate("/tasks");
      } catch {
        setError("Failed to delete task");
      }
    }
  };

  return (
    <>
      <Navbar />
      <div
        className="taskdetail-container"
        aria-busy={loading}
        aria-label="Task Details"
        role="main"
      >
        <h1>Task Details</h1>
        <StatusMessage loading={loading} error={error} />
        {task && (
          <div className="taskdetail-info">
            <h2>{task.title}</h2>
            <p>
              <strong>Description:</strong> {task.description}
            </p>
            <p>
              <strong>Status:</strong> {task.status}
            </p>
            <p>
              <strong>Priority:</strong> {task.priority}
            </p>
            <p>
              <strong>Due Date:</strong>{" "}
              {new Date(task.dueDate).toLocaleDateString()}
            </p>
            <p>
              <strong>Category:</strong> {formatCategory(task.category)}
            </p>
            <p>
              <strong>Created At:</strong>{" "}
              {new Date(task.createdAt).toLocaleString()}
            </p>
            <p>
              <strong>Updated At:</strong>{" "}
              {new Date(task.updatedAt).toLocaleString()}
            </p>
            <div>
              <button onClick={handleToggleComplete} disabled={toggleLoading}>
                {task.status === "COMPLETE"
                  ? "Mark as Incomplete"
                  : "Mark as Complete"}
              </button>
            </div>
            <div>
              <button onClick={() => navigate(`/tasks/edit/${task.id}`)}>
                Edit Task
              </button>
            </div>
            <div>
              <button onClick={handleDelete}>Delete Task</button>
            </div>
            <Link to="/tasks" aria-label="Back to Task List">
              Back to Task List
            </Link>
          </div>
        )}
      </div>
    </>
  );
}

export default TaskDetail;
