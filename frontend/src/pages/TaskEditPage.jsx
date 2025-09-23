import TaskForm from "./TaskForm";
import { updateTask, getTask } from "../services/api";
import { useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import StatusMessage from "../components/StatusMessage";

function TaskEditPage({ skipDelay = false }) {
  const { id } = useParams();
  const [task, setTask] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    getTask(id)
      .then(setTask)
      .catch(() => setError("Failed to load task."));
  }, [id]);

  const handleUpdate = async (form) => {
    setLoading(true);
    setError("");
    setSuccess(false);
    try {
      const dueDateTime = form.dueDate
        ? new Date(form.dueDate).toISOString()
        : null;
      await updateTask(id, { ...form, dueDate: dueDateTime });
      setSuccess(true);
      if (skipDelay) {
        navigate("/tasks/" + id);
      } else {
        setTimeout(() => {
          navigate("/tasks/" + id);
        }, 1000);
      }
    } catch {
      setError("Failed to update task");
    } finally {
      setLoading(false);
    }
  };

  if (!task) return <StatusMessage loading={true} />;

  return (
    <div aria-busy={loading}>
      <StatusMessage
        loading={loading}
        error={error}
        success={success && "Task updated!"}
      />
      <TaskForm
        initialTask={task}
        onSubmit={handleUpdate}
        loading={loading}
        error={error}
        success={success && "Task updated!"}
      />
    </div>
  );
}

export default TaskEditPage;
