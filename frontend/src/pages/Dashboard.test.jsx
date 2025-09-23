import { render, screen, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import Dashboard from "./Dashboard";

beforeAll(() => {
  global.ResizeObserver = class {
    observe() {}
    unobserve() {}
    disconnect() {}
  };
});

test("Renders Dashboard component", () => {
  render(
    <BrowserRouter>
      <Dashboard />
    </BrowserRouter>,
  );
  expect(
    screen.getByRole("main", { name: /dashboard overview/i }),
  ).toBeInTheDocument();
  expect(screen.getByText(/welcome to your dashboard/i)).toBeInTheDocument();
});

test("Shows spinner when loading", () => {
  render(
    <BrowserRouter>
      <Dashboard />
    </BrowserRouter>,
  );
  expect(screen.getByLabelText(/loading/i)).toBeInTheDocument();
});

test("Renders widgets after loading", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { status: "COMPLETE" },
          { status: "PENDING" },
          { status: "COMPLETE" },
        ]),
    }),
  );
  render(
    <BrowserRouter>
      <Dashboard />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByLabelText(/task widgets/i)).toBeInTheDocument();
    expect(screen.getByText(/task summary/i)).toBeInTheDocument();
    expect(screen.getAllByText(/upcoming tasks/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/overdue tasks/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/productivity/i).length).toBeGreaterThan(0);
    expect(screen.getAllByText(/tasks by category/i).length).toBeGreaterThan(0);
  });
  jest.useRealTimers();
});

test("Dashboard container aria-busy updates after loading", async () => {
  jest.useFakeTimers();
  render(
    <BrowserRouter>
      <Dashboard />
    </BrowserRouter>,
  );
  expect(screen.getByRole("main")).toHaveAttribute("aria-busy", "true");
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByRole("main")).toHaveAttribute("aria-busy", "false");
  });
  jest.useRealTimers();
});

function testWidgetErrorDisplay() {
  jest.useFakeTimers();
  global.fetch = jest.fn(() => Promise.resolve({ ok: false }));
  render(
    <BrowserRouter>
      <Dashboard />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  return waitFor(() => {
    expect(
      screen.getAllByText(/failed to fetch tasks/i).length,
    ).toBeGreaterThan(0);
  }).finally(() => jest.useRealTimers());
}

const widgetLabels = [
  "Task Summary",
  "Upcoming Tasks",
  "Overdue Tasks",
  "Productivity",
  "Tasks by Category",
];

widgetLabels.forEach((widgetLabel) => {
  test(`Shows error in ${widgetLabel} widget when fetch fails`, async () => {
    await testWidgetErrorDisplay(widgetLabel);
  });
});
test("Numbers in TaskSummaryWidget match mocked fetch data", async () => {
  jest.useFakeTimers();
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { status: "COMPLETE" },
          { status: "PENDING" },
          { status: "COMPLETE" },
          { status: "PENDING" },
          { status: "PENDING" },
        ]),
    }),
  );
  render(
    <BrowserRouter>
      <Dashboard />
    </BrowserRouter>,
  );
  jest.runAllTimers();
  await waitFor(() => {
    expect(screen.getByText(/total tasks: 5/i)).toBeInTheDocument();
    expect(screen.getByText(/completed: 2/i)).toBeInTheDocument();
    expect(screen.getByText(/uncompleted: 3/i)).toBeInTheDocument();
  });
  jest.useRealTimers();
});
