import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom'
import Home from './pages/Home';
import Register from './pages/Register';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import TaskList from './pages/TaskList';
import TaskDetail from './pages/TaskDetail';
import TaskFormPage from './pages/TaskFormPage';
import TaskEditPage from './pages/TaskEditPage';
import Profile from './pages/Profile';
import Forbidden from './pages/Forbidden';
import NotFound from './pages/NotFound';
import "./App.css"
import Logout from './pages/Logout';
import Unauthorized from './pages/Unauthorized';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/tasks" element={<TaskList />} />
        <Route path="/tasks/:id" element={<TaskDetail />} />
        <Route path="/tasks/new" element={<TaskFormPage />} />
        <Route path="/tasks/edit/:id" element={<TaskEditPage />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/forbidden" element={<Forbidden />} />
        <Route path="/404" element={<NotFound />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/unauthorized" element={<Unauthorized />} />
      </Routes>
    </Router>
  )
}

export default App