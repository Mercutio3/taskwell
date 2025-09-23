import {fireEvent, render , screen} from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import Logout from "./Logout";
import { waitFor } from "@testing-library/react";
import { wait } from "@testing-library/user-event/dist/cjs/utils/index.js";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
}));

beforeEach(() => {
    global.fetch = jest.fn(() => Promise.resolve({}));
});

afterEach(() => {
    jest.resetAllMocks();
});

test("Renders Logout component", () => {
    render(
        <BrowserRouter>
            <Logout />
        </BrowserRouter>
    );
    expect(screen.getByRole("main", { name: /logging out/i })).toBeInTheDocument();
    expect(screen.getByText(/logging out.../i)).toBeInTheDocument();
});

test("User is redirected to login as effect runs", async () => {
    render(
        <BrowserRouter>
            <Logout />
        </BrowserRouter>
    );
    await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/login', { replace: true });
    });
});

test("Calls logout API", async () => {
    const fetchSpy = jest.spyOn(global, 'fetch').mockResolvedValue({});
    render(
        <BrowserRouter>
            <Logout />
        </BrowserRouter>
    );
    expect(fetchSpy).toHaveBeenCalledWith('http://localhost:8080/logout', {
        method: 'POST',
        credentials: 'include',
    });
});

test("Does not crash if logout API fails", async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error("Logout failed")));
    render(
        <BrowserRouter>
            <Logout />
        </BrowserRouter>
    );
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/logout', {
        method: 'POST',
        credentials: 'include',
    });
    await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/login', { replace: true });
    });
});