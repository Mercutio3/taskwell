import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";
import { useEffect, useState } from "react";
import { formatCategory } from "../../utils/formatting";
import Spinner from "../Spinner";

function CategoryPieChart() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [data, setData] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/tasks", { credentials: "include" })
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch tasks");
        return res.json();
      })
      .then((tasks) => {
        const chartData = Object.entries(
          tasks.reduce((acc, task) => {
            const category = formatCategory(task.category) || "Uncategorized";
            acc[category] = (acc[category] || 0) + 1;
            return acc;
          }, {}),
        ).map(([name, value]) => ({ name, value }));
        setData(chartData);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const COLORS = [
    "#0088FE",
    "#00C49F",
    "#FFBB28",
    "#FF8042",
    "#AA336A",
    "#8884D8",
  ];

  if (loading) return <Spinner aria-label="Loading category pie chart..." />;
  if (error)
    return (
      <div style={{ color: "red" }} aria-live="assertive">
        {error}
      </div>
    );
  if (!data.length)
    return (
      <div
        className="widget category-piechart-widget"
        aria-label="Pie Chart of Tasks by Category"
        role="region"
      >
        <h3>Tasks by Category</h3>
        <div style={{ color: "#888", textAlign: "center", marginTop: 40 }}>
          No tasks to display
        </div>
      </div>
    );

  return (
    <div
      className="widget category-piechart-widget"
      aria-label="Pie Chart of Tasks by Category"
      role="region"
    >
      <h3>Tasks by Category</h3>
      <PieChart width={300} height={250}>
        <Pie
          data={data}
          dataKey="value"
          nameKey="name"
          cx="50%"
          cy="50%"
          outerRadius={80}
          label
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend />
      </PieChart>
    </div>
  );
}

export default CategoryPieChart;
