import {fireEvent, render , screen} from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import NotFound from "./NotFound";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));

function mockFetchOk(ok) {
    jest.spyOn(global, 'fetch').mockImplementation(() => Promise.resolve({ ok }));
}

beforeEach(() => {
    jest.spyOn(global, 'fetch').mockImplementation(() =>
        Promise.resolve({ ok: false })
    );
});

afterEach(() => {
    global.fetch.mockRestore();
}); 

test("Renders NotFound component", async () => {
    mockFetchOk(true); // Simulate logged in
    render(
        <BrowserRouter>
            <NotFound />
        </BrowserRouter>
    );
    expect(screen.getByRole("alert", { name: /not found page/i })).toBeInTheDocument();
    expect(screen.getByText(/404 - not found/i)).toBeInTheDocument();
    expect(screen.getByText(/the page you are looking for does not exist./i)).toBeInTheDocument();
    const button = await screen.findByRole("button", { name: /go to dashboard/i });
    expect(button).toBeInTheDocument();
});

test("Login button is shown when not logged in", async () => {
    mockFetchOk(false); // Simulate not logged in
    render(
        <BrowserRouter>
            <NotFound />
        </BrowserRouter>
    );
    const button = await screen.findByRole("button", { name: /login/i });
    expect(button).toBeInTheDocument();
});

test("Dashboard button redirects to /dashboard", async () => {
    mockFetchOk(true); // Simulate logged in
    render(
        <BrowserRouter>
            <NotFound />
        </BrowserRouter>
    );
    const button = await screen.findByRole("button", { name: /go to dashboard/i });
    fireEvent.click(button);
    expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
});

test("Login button redirects to /login", async () => {
    mockFetchOk(false); // Simulate not logged in
    render(
        <BrowserRouter>
            <NotFound />
        </BrowserRouter>
    );
    const button = await screen.findByRole("button", { name: /login/i });
    fireEvent.click(button);
    expect(mockNavigate).toHaveBeenCalledWith('/login');
});

test("Dashboard button is focusable", async () => {
    mockFetchOk(true); // Simulate logged in
    render(
        <BrowserRouter>
            <NotFound />
        </BrowserRouter>
    );
    const button = await screen.findByRole("button", { name: /go to dashboard/i });
    button.focus();
    expect(button).toHaveFocus();
});

test("Login button is focusable", async () => {
    mockFetchOk(false); // Simulate not logged in
    render(
        <BrowserRouter>
            <NotFound />
        </BrowserRouter>
    );
    const button = await screen.findByRole("button", { name: /login/i });
    button.focus();
    expect(button).toHaveFocus();
});