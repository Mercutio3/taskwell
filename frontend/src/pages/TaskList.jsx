import Navbar from '../components/Navbar'
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Spinner from '../components/Spinner'

function TaskList() {
    const [tasks, setTasks] = useState([])
    const [loading, setLoading] = useState(true)
    const [search, setSearch] = useState('')
    const [statusFilter, setStatusFilter] = useState('')
    const [priorityFilter, setPriorityFilter] = useState('')
    const [sortField, setSortField] = useState('title')
    const [sortDirection, setSortDirection] = useState('asc')

    useEffect(() => {
        async function fetchTasks() {
            try {
                const res = await fetch('http://localhost:8080/api/tasks', {
                    credentials: 'include',
                });
                if (!res.ok) throw new Error('Failed to fetch tasks');
                const data = await res.json();
                setTasks(data);
            } catch (err) {
                setTasks([]);
            } finally {
                setLoading(false);
            }
        }
        fetchTasks();
    }, []);

    // Filtered tasks (search + filters)
    const filteredTasks = tasks.filter(task => {
        const matchesSearch = task.title.toLowerCase().includes(search.toLowerCase())
        const matchesStatus = statusFilter ? task.status === statusFilter : true
        const matchesPriority = priorityFilter ? task.priority === priorityFilter : true
        return matchesSearch && matchesStatus && matchesPriority
    })

    const sortedTasks = [...filteredTasks].sort((a, b) => {
        let aVal = a[sortField] || '';
        let bVal = b[sortField] || '';
        if (typeof aVal === 'string' && typeof bVal === 'string') {
            aVal = aVal.toLowerCase();
            bVal = bVal.toLowerCase();
        }
        if (aVal < bVal) return sortDirection === 'asc' ? -1 : 1;
        if (aVal > bVal) return sortDirection === 'asc' ? 1 : -1;
        return 0;
    });

    return (
        <>
            <Navbar />
            <div className="tasklist-container" aria-label="Task List Page" role="main">
                <h1>Task List</h1>
                <p>Welcome to your task list! Here you can see all your tasks and manage them effectively.</p>
                <div className="tasklist" aria-label="Task List Controls and Items">
                    <input
                        type="text"
                        placeholder="Search tasks..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        className="tasklist-search"
                        aria-label="Search tasks"
                    />
                    <div className="tasklist-filters">
                        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)} aria-label="Status Filter">
                            <option value="">All Statuses</option>
                            <option value="PENDING">Pending</option>
                            <option value="COMPLETED">Completed</option>
                        </select>
                        <select value={priorityFilter} onChange={e => setPriorityFilter(e.target.value)} aria-label="Priority Filter">
                            <option value="">All Priorities</option>
                            <option value="LOW">Low</option>
                            <option value="MEDIUM">Medium</option>
                            <option value="HIGH">High</option>
                        </select>
                    </div>
                    <div className="tasklist-sort">
                        <label>Sort By:&nbsp;
                        <select value={sortField} onChange={e => setSortField(e.target.value)} aria-label="Sort Field">
                            <option value="title">Title</option>
                            <option value="status">Status</option>
                            <option value="priority">Priority</option>
                        </select>
                        </label>
                        <label>Order:&nbsp;
                        <select value={sortDirection} onChange={e => setSortDirection(e.target.value)} aria-label="Sort Direction">
                            <option value="asc">Ascending</option>
                            <option value="desc">Descending</option>
                        </select>
                        </label>
                    </div>
                    <div className="tasklist-items" aria-live="polite">
                        {loading ? (
                            <Spinner aria-label="Loading tasks..." />
                        ) : filteredTasks.length === 0 ? (
                            <div>You have no tasks, or none that match your given search criteria. <Link to="/tasks/new">Create task</Link></div>
                        ) : (
                            <ul role="list">
                                {sortedTasks.map(task => (
                                    <li key={task.id} role="listitem">
                                        <Link to={`/tasks/${task.id}`}>{task.title}</Link>
                                        {task.status && <span> [{task.status}]</span>}
                                        {task.priority && <span> ({task.priority})</span>}
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>
                </div>
            </div>
        </>
    )
}

export default TaskList