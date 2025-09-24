import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { BrowserRouter } from "react-router-dom";
import TaskFormPage from "./TaskFormPage";
import { AuthProvider } from "../context/AuthContext";

jest.mock("../services/api", () => ({
  createTask: jest.fn().mockResolvedValue({}),
}));

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate,
}));

beforeEach(() => {
  mockNavigate.mockClear();
});

beforeAll(() => {
  global.fetch = jest.fn((url) => {
    if (url.includes("/api/tasks/categories")) {
      return Promise.resolve({
        json: () => Promise.resolve(["WORK", "PERSONAL", "OTHER"]),
      });
    }
    // fallback for other fetch calls
    return Promise.resolve({
      json: () => Promise.resolve([]),
    });
  });
});

afterAll(() => {
  global.fetch.mockRestore && global.fetch.mockRestore();
});

test("Renders TaskFormPage component with correct headings", () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <TaskFormPage />
      </BrowserRouter>
    </AuthProvider>,
  );
  expect(screen.getByText(/welcome to your task form/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/task title/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/task description/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/due date/i)).toBeInTheDocument();
  expect(
    screen.getByRole("button", { name: /create task/i }),
  ).toBeInTheDocument();
});

test("Handles successful task creation", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <TaskFormPage skipDelay />
      </BrowserRouter>
    </AuthProvider>,
  );
  await userEvent.type(screen.getByPlaceholderText(/task title/i), "New Task");
  await userEvent.type(
    screen.getByPlaceholderText(/task description/i),
    "Task Description",
  );
  await userEvent.type(screen.getByPlaceholderText(/due date/i), "2026-12-31");
  await userEvent.selectOptions(
    screen.getByPlaceholderText(/category/i),
    "Work",
  );
  await userEvent.click(screen.getByRole("button", { name: /create task/i }));

  await waitFor(() => {
    expect(mockNavigate).toHaveBeenCalledWith("/tasks");
  });
});

test("Handles task creation error for unverified user", async () => {
  const { createTask } = require("../services/api");
  createTask.mockRejectedValue({ status: 403 });

  render(
    <AuthProvider value={{ isVerified: false }}>
      <BrowserRouter>
        <TaskFormPage skipDelay />
      </BrowserRouter>
    </AuthProvider>,
  );
  await userEvent.type(screen.getByPlaceholderText(/task title/i), "New Task");
  await userEvent.type(
    screen.getByPlaceholderText(/task description/i),
    "Task Description",
  );
  await userEvent.type(screen.getByPlaceholderText(/due date/i), "2026-12-31");
  await userEvent.selectOptions(
    screen.getByPlaceholderText(/category/i),
    "Work",
  );
  await userEvent.click(screen.getByRole("button", { name: /create task/i }));

  const errors = await screen.findAllByText(
    /please verify your account to create tasks/i,
  );
  expect(errors.length).toBeGreaterThan(0);
  errors.forEach((e) => expect(e).toBeVisible());
});

test("Handles due date in the past", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <TaskFormPage skipDelay />
      </BrowserRouter>
    </AuthProvider>,
  );
  await userEvent.type(screen.getByPlaceholderText(/task title/i), "New Task");
  await userEvent.type(
    screen.getByPlaceholderText(/task description/i),
    "Task Description",
  );
  const pastDate = new Date();
  pastDate.setDate(pastDate.getDate() - 1);
  const pastDateString = pastDate.toISOString().split("T")[0];
  await userEvent.type(
    screen.getByPlaceholderText(/due date/i),
    pastDateString,
  );
  await userEvent.selectOptions(
    screen.getByPlaceholderText(/category/i),
    "Work",
  );
  await userEvent.click(screen.getByRole("button", { name: /create task/i }));

  await waitFor(() => {
    expect(
      screen.getByText((content) =>
        content.includes("Due date cannot be in the past"),
      ),
    ).toBeInTheDocument();
  });
});

test("Handles empty title field", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <TaskFormPage skipDelay />
      </BrowserRouter>
    </AuthProvider>,
  );
  await userEvent.type(
    screen.getByPlaceholderText(/task description/i),
    "Task Description",
  );
  await userEvent.type(screen.getByPlaceholderText(/due date/i), "2026-12-31");
  await userEvent.click(screen.getByRole("button", { name: /create task/i }));

  await waitFor(() => {
    expect(
      screen.getByText((content) => content.includes("Title is required")),
    ).toBeInTheDocument();
  });
});

test("All form fields are focusable in order", async () => {
  render(
    <AuthProvider value={{ isVerified: true }}>
      <BrowserRouter>
        <TaskFormPage />
      </BrowserRouter>
    </AuthProvider>,
  );

  const titleInput = screen.getByPlaceholderText(/task title/i);
  const descriptionInput = screen.getByPlaceholderText(/task description/i);
  const dueDateInput = screen.getByPlaceholderText(/due date/i);
  const statusSelect = screen.getByPlaceholderText(/status/i);
  const prioritySelect = screen.getByPlaceholderText(/priority/i);
  const categorySelect = screen.getByPlaceholderText(/category/i);
  const submitButton = screen.getByRole("button", { name: /create task/i });

  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  await userEvent.tab();
  expect(titleInput).toHaveFocus();

  await userEvent.tab();
  expect(descriptionInput).toHaveFocus();

  await userEvent.tab();
  expect(dueDateInput).toHaveFocus();

  await userEvent.tab();
  expect(statusSelect).toHaveFocus();

  await userEvent.tab();
  expect(prioritySelect).toHaveFocus();

  await userEvent.tab();
  expect(categorySelect).toHaveFocus();

  await userEvent.tab();
  expect(submitButton).toHaveFocus();
});
