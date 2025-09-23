import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import TaskList from "./TaskList";

test("Renders TaskList component", () => {
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  expect(
    screen.getByRole("main", { name: /task list page/i }),
  ).toBeInTheDocument();
  expect(screen.getByText(/welcome to your task list/i)).toBeInTheDocument();
});

test("Shows spinner when loading", () => {
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  expect(screen.getByLabelText(/loading/i)).toBeInTheDocument();
});

test("Displays tasks after successful fetch", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { id: 1, title: "Task One", status: "PENDING", priority: "HIGH" },
          { id: 2, title: "Task Two", status: "COMPLETE", priority: "LOW" },
        ]),
    }),
  );
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByText(/task one/i)).toBeInTheDocument();
    expect(screen.getByText(/task two/i)).toBeInTheDocument();
  });
  jest.useRealTimers();
});

test("Hanldes empty task list", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve([]),
    }),
  );
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByText(/you have no tasks/i)).toBeInTheDocument();
  });
  jest.useRealTimers();
});

test("Handles fetch error", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: false,
    }),
  );
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByText(/you have no tasks/i)).toBeInTheDocument();
  });
  jest.useRealTimers();
});

test("Search controls work", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { id: 1, title: "Task One", status: "PENDING", priority: "HIGH" },
          { id: 2, title: "Task Two", status: "COMPLETE", priority: "LOW" },
        ]),
    }),
  );
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByText(/task one/i)).toBeInTheDocument();
    expect(screen.getByText(/task two/i)).toBeInTheDocument();
  });

  const searchInput = screen.getByPlaceholderText(/search tasks/i);
  fireEvent.change(searchInput, { target: { value: "One" } });
  expect(searchInput.value).toBe("One");
  expect(screen.getByText(/task one/i)).toBeInTheDocument();
  expect(screen.queryByText(/task two/i)).not.toBeInTheDocument();

  jest.useRealTimers();
});

test("Filter controls work", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { id: 1, title: "Task One", status: "PENDING", priority: "HIGH" },
          { id: 2, title: "Task Two", status: "COMPLETE", priority: "LOW" },
        ]),
    }),
  );
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByText(/task one/i)).toBeInTheDocument();
    expect(screen.getByText(/task two/i)).toBeInTheDocument();
  });

  const statusFilter = screen.getByLabelText(/status filter/i);
  fireEvent.change(statusFilter, { target: { value: "PENDING" } });
  expect(statusFilter.value).toBe("PENDING");
  expect(screen.getByText(/task one/i)).toBeInTheDocument();
  expect(screen.queryByText(/task two/i)).not.toBeInTheDocument();

  const priorityFilter = screen.getByLabelText(/priority filter/i);
  fireEvent.change(priorityFilter, { target: { value: "HIGH" } });
  expect(priorityFilter.value).toBe("HIGH");
  expect(screen.getByText(/task one/i)).toBeInTheDocument();
  expect(screen.queryByText(/task two/i)).not.toBeInTheDocument();

  jest.useRealTimers();
});

test("Sorting works", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { id: 1, title: "B Task", status: "PENDING", priority: "HIGH" },
          { id: 2, title: "A Task", status: "COMPLETE", priority: "LOW" },
        ]),
    }),
  );
  render(
    <BrowserRouter>
      <TaskList />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    // Get only the task links inside the task list
    const taskList = screen.getByRole("list");
    const items = Array.from(taskList.querySelectorAll("li"));
    expect(items[0].textContent).toMatch(/A Task/);
    expect(items[1].textContent).toMatch(/B Task/);
  });

  // Simulate changing sort order
  const sortFieldSelect = screen.getByLabelText(/sort field/i);
  fireEvent.change(sortFieldSelect, { target: { value: "title" } });
  const sortDirectionSelect = screen.getByLabelText(/sort direction/i);
  fireEvent.change(sortDirectionSelect, { target: { value: "asc" } });

  await waitFor(() => {
    const taskList = screen.getByRole("list");
    const items = Array.from(taskList.querySelectorAll("li"));
    expect(items[0].textContent).toMatch(/A Task/);
    expect(items[1].textContent).toMatch(/B Task/);
  });

  fireEvent.change(sortDirectionSelect, { target: { value: "desc" } });

  await waitFor(() => {
    const taskList = screen.getByRole("list");
    const items = Array.from(taskList.querySelectorAll("li"));
    expect(items[0].textContent).toMatch(/B Task/);
    expect(items[1].textContent).toMatch(/A Task/);
  });

  jest.useRealTimers();
});
